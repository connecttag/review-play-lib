package org.connecttag.lib.review.storage

/**
 * Storage contract for persisting review prompt heuristics.
 */
interface ReviewPreferences {
    var firstInstallTimeMs: Long
    var appLaunchCount: Int
    var keyActionCount: Int
    var lastPromptTimeMs: Long
    var isReviewCompleted: Boolean

    fun reset()
}
