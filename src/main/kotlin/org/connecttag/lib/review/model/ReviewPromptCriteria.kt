package org.connecttag.lib.review.model

/**
 * Heuristic criteria used to determine whether the user is in an optimal, non-intrusive
 * state to be prompted for a Google Play store review.
 *
 * @param minDaysSinceInstall Minimum number of days elapsed since the user first opened the app.
 * @param minAppLaunches Minimum number of distinct app launch/foreground sessions.
 * @param minKeyActionsCount Minimum number of significant successful actions performed (e.g. created rule, saved contact, completed scan).
 * @param minDaysBetweenPrompts Minimum cooldown in days between subsequent review prompt attempts.
 */
data class ReviewPromptCriteria(
    val minDaysSinceInstall: Int = 3,
    val minAppLaunches: Int = 5,
    val minKeyActionsCount: Int = 2,
    val minDaysBetweenPrompts: Int = 30
)
