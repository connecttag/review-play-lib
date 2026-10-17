package org.connecttag.lib.review.fake

import android.app.Activity
import org.connecttag.lib.review.api.AppReviewEngine
import org.connecttag.lib.review.model.ReviewPromptCriteria
import org.connecttag.lib.review.model.ReviewResult

/**
 * Mock implementation of [AppReviewEngine] for testing and previewing review flows.
 */
class FakeAppReviewEngine : AppReviewEngine {

    var nextResult: ReviewResult = ReviewResult.Completed
    var isEligibleOverride: Boolean? = null
    var appLaunchCount: Int = 0
    var keyActionCount: Int = 0
    var reviewRequestedCount: Int = 0
    var isReviewCompleted: Boolean = false

    override suspend fun requestReview(activity: Activity): ReviewResult {
        reviewRequestedCount++
        isReviewCompleted = true
        return nextResult
    }

    override suspend fun requestReviewIfEligible(
        activity: Activity,
        criteria: ReviewPromptCriteria
    ): ReviewResult {
        val override = isEligibleOverride
        if (override != null) {
            return if (override) requestReview(activity) else ReviewResult.NotEligible("Mock ineligible override")
        }

        if (isReviewCompleted) {
            return ReviewResult.NotEligible("Review already completed")
        }

        if (appLaunchCount < criteria.minAppLaunches) {
            return ReviewResult.NotEligible("App launches ($appLaunchCount) < required (${criteria.minAppLaunches})")
        }

        if (keyActionCount < criteria.minKeyActionsCount) {
            return ReviewResult.NotEligible("Key actions ($keyActionCount) < required (${criteria.minKeyActionsCount})")
        }

        return requestReview(activity)
    }

    override fun recordAppLaunch() {
        appLaunchCount++
    }

    override fun recordKeyAction() {
        keyActionCount++
    }

    override fun resetReviewState() {
        appLaunchCount = 0
        keyActionCount = 0
        reviewRequestedCount = 0
        isReviewCompleted = false
        isEligibleOverride = null
    }
}
