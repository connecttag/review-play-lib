package org.connecttag.lib.review.api

/**
 * Pluggable logger interface for the review library.
 */
interface ReviewLogger {
    fun d(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)

    companion object {
        val DEFAULT: ReviewLogger = object : ReviewLogger {
            override fun d(tag: String, message: String) {
                android.util.Log.d(tag, message)
            }

            override fun w(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.w(tag, message, throwable)
            }

            override fun e(tag: String, message: String, throwable: Throwable?) {
                android.util.Log.e(tag, message, throwable)
            }
        }
    }
}
