package org.connecttag.lib.review.play

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import org.connecttag.lib.review.api.AppReviewEngine
import org.connecttag.lib.review.api.ReviewLogger
import org.connecttag.lib.review.model.ReviewPromptCriteria
import org.connecttag.lib.review.model.ReviewResult
import org.connecttag.lib.review.storage.DefaultReviewPreferences
import org.connecttag.lib.review.storage.ReviewPreferences
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * Production-ready implementation of [AppReviewEngine] using the Google Play In-App Review API.
 */
class PlayAppReviewEngine(
    context: Context,
    private val preferences: ReviewPreferences = DefaultReviewPreferences(context),
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context.applicationContext),
    private val logger: ReviewLogger = ReviewLogger.DEFAULT
) : AppReviewEngine {

    companion object {
        private const val TAG = "PlayAppReviewEngine"
    }

    override suspend fun requestReview(activity: Activity): ReviewResult {
        val reviewInfo = obtainReviewInfo() ?: run {
            logger.w(TAG, "Failed to obtain ReviewInfo from Google Play.")
            return ReviewResult.Error("Could not retrieve ReviewInfo from Google Play")
        }

        return suspendCancellableCoroutine { continuation ->
            reviewManager.launchReviewFlow(activity, reviewInfo)
                .addOnCompleteListener { task ->
                    preferences.lastPromptTimeMs = System.currentTimeMillis()
                    preferences.isReviewCompleted = true

                    if (task.isSuccessful) {
                        logger.d(TAG, "Review flow completed successfully.")
                        if (continuation.isActive) continuation.resume(ReviewResult.Completed)
                    } else {
                        val exception = task.exception
                        logger.w(TAG, "Review flow finished with exception", exception)
                        if (continuation.isActive) {
                            continuation.resume(
                                ReviewResult.Error(
                                    message = exception?.message ?: "Review flow failed",
                                    throwable = exception
                                )
                            )
                        }
                    }
                }
        }
    }

    private suspend fun obtainReviewInfo(): ReviewInfo? = suspendCancellableCoroutine { continuation ->
        reviewManager.requestReviewFlow()
            .addOnSuccessListener { reviewInfo ->
                if (continuation.isActive) continuation.resume(reviewInfo)
            }
            .addOnFailureListener { exception ->
                logger.w(TAG, "requestReviewFlow failed", exception)
                if (continuation.isActive) continuation.resume(null)
            }
    }

    override suspend fun requestReviewIfEligible(
        activity: Activity,
        criteria: ReviewPromptCriteria
    ): ReviewResult {
        if (preferences.isReviewCompleted) {
            return ReviewResult.NotEligible("Review flow has already been completed.")
        }

        val now = System.currentTimeMillis()
        val daysSinceInstall = TimeUnit.MILLISECONDS.toDays(now - preferences.firstInstallTimeMs)
        if (daysSinceInstall < criteria.minDaysSinceInstall) {
            return ReviewResult.NotEligible(
                "Days since install ($daysSinceInstall) < required (${criteria.minDaysSinceInstall})."
            )
        }

        if (preferences.appLaunchCount < criteria.minAppLaunches) {
            return ReviewResult.NotEligible(
                "App launches (${preferences.appLaunchCount}) < required (${criteria.minAppLaunches})."
            )
        }

        if (preferences.keyActionCount < criteria.minKeyActionsCount) {
            return ReviewResult.NotEligible(
                "Key actions (${preferences.keyActionCount}) < required (${criteria.minKeyActionsCount})."
            )
        }

        if (preferences.lastPromptTimeMs > 0L) {
            val daysSinceLastPrompt = TimeUnit.MILLISECONDS.toDays(now - preferences.lastPromptTimeMs)
            if (daysSinceLastPrompt < criteria.minDaysBetweenPrompts) {
                return ReviewResult.NotEligible(
                    "Cooldown active: days since last prompt ($daysSinceLastPrompt) < required (${criteria.minDaysBetweenPrompts})."
                )
            }
        }

        return requestReview(activity)
    }

    override fun recordAppLaunch() {
        preferences.appLaunchCount = preferences.appLaunchCount + 1
    }

    override fun recordKeyAction() {
        preferences.keyActionCount = preferences.keyActionCount + 1
    }

    override fun resetReviewState() {
        preferences.reset()
    }
}
