package org.connecttag.lib.review.api

import android.app.Activity
import org.connecttag.lib.review.model.ReviewPromptCriteria
import org.connecttag.lib.review.model.ReviewResult

/**
 * Universal interface for requesting Google Play in-app reviews.
 */
interface AppReviewEngine {
    /**
     * Unconditionally requests and launches the Google Play review flow in the host [activity].
     */
    suspend fun requestReview(activity: Activity): ReviewResult

    /**
     * Checks if the user meets the specified [criteria] before prompting.
     * If eligible, launches the review flow; otherwise returns [ReviewResult.NotEligible].
     */
    suspend fun requestReviewIfEligible(
        activity: Activity,
        criteria: ReviewPromptCriteria = ReviewPromptCriteria()
    ): ReviewResult

    /**
     * Increments the launch/session counter. Should typically be called on App or Activity startup.
     */
    fun recordAppLaunch()

    /**
     * Increments the count of positive/key actions completed by the user.
     */
    fun recordKeyAction()

    /**
     * Resets stored prompt counters and completion flags.
     */
    fun resetReviewState()
}
