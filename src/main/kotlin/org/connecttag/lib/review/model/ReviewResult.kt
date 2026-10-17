package org.connecttag.lib.review.model

/**
 * Outcome of an in-app review request.
 */
sealed interface ReviewResult {
    /**
     * The Google Play review flow has completed.
     * Note: Per Google Play guidelines, apps cannot know whether the user actually left a review
     * or dismissed the dialog.
     */
    data object Completed : ReviewResult

    /**
     * The review request was skipped because the heuristic criteria were not met.
     */
    data class NotEligible(val reason: String) : ReviewResult

    /**
     * An error occurred while communicating with the Google Play Review API.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : ReviewResult
}
