package org.connecttag.lib.review.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Standard SharedPreferences-backed implementation of [ReviewPreferences].
 */
class DefaultReviewPreferences(
    context: Context,
    prefsName: String = "connecttag_review_prefs"
) : ReviewPreferences {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FIRST_INSTALL_TIME = "first_install_time_ms"
        private const val KEY_APP_LAUNCH_COUNT = "app_launch_count"
        private const val KEY_KEY_ACTION_COUNT = "key_action_count"
        private const val KEY_LAST_PROMPT_TIME = "last_prompt_time_ms"
        private const val KEY_REVIEW_COMPLETED = "review_completed"
    }

    override var firstInstallTimeMs: Long
        get() {
            val value = prefs.getLong(KEY_FIRST_INSTALL_TIME, 0L)
            if (value == 0L) {
                val now = System.currentTimeMillis()
                prefs.edit().putLong(KEY_FIRST_INSTALL_TIME, now).apply()
                return now
            }
            return value
        }
        set(value) {
            prefs.edit().putLong(KEY_FIRST_INSTALL_TIME, value).apply()
        }

    override var appLaunchCount: Int
        get() = prefs.getInt(KEY_APP_LAUNCH_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_APP_LAUNCH_COUNT, value).apply()

    override var keyActionCount: Int
        get() = prefs.getInt(KEY_KEY_ACTION_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_KEY_ACTION_COUNT, value).apply()

    override var lastPromptTimeMs: Long
        get() = prefs.getLong(KEY_LAST_PROMPT_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_PROMPT_TIME, value).apply()

    override var isReviewCompleted: Boolean
        get() = prefs.getBoolean(KEY_REVIEW_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_REVIEW_COMPLETED, value).apply()

    override fun reset() {
        prefs.edit().clear().apply()
    }
}
