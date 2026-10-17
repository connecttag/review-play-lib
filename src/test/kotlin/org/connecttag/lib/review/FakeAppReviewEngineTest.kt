package org.connecttag.lib.review

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.connecttag.lib.review.fake.FakeAppReviewEngine
import org.connecttag.lib.review.model.ReviewPromptCriteria
import org.connecttag.lib.review.model.ReviewResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAppReviewEngineTest {

    private lateinit var reviewEngine: FakeAppReviewEngine

    @Before
    fun setUp() {
        reviewEngine = FakeAppReviewEngine()
    }

    @Test
    fun requestReview_executesAndCompletes() = runTest {
        val result = reviewEngine.requestReview(mockActivity())

        assertEquals(ReviewResult.Completed, result)
        assertEquals(1, reviewEngine.reviewRequestedCount)
        assertTrue(reviewEngine.isReviewCompleted)
    }

    @Test
    fun requestReviewIfEligible_whenCriteriaNotMet_returnsNotEligible() = runTest {
        val criteria = ReviewPromptCriteria(
            minDaysSinceInstall = 0,
            minAppLaunches = 3,
            minKeyActionsCount = 2
        )

        // Only 1 launch, 0 actions
        reviewEngine.recordAppLaunch()

        val result = reviewEngine.requestReviewIfEligible(mockActivity(), criteria)
        assertTrue(result is ReviewResult.NotEligible)
        assertEquals(0, reviewEngine.reviewRequestedCount)
    }

    @Test
    fun requestReviewIfEligible_whenCriteriaMet_launchesReview() = runTest {
        val criteria = ReviewPromptCriteria(
            minDaysSinceInstall = 0,
            minAppLaunches = 2,
            minKeyActionsCount = 1
        )

        reviewEngine.recordAppLaunch()
        reviewEngine.recordAppLaunch()
        reviewEngine.recordKeyAction()

        val result = reviewEngine.requestReviewIfEligible(mockActivity(), criteria)
        assertEquals(ReviewResult.Completed, result)
        assertEquals(1, reviewEngine.reviewRequestedCount)
    }

    @Test
    fun resetReviewState_clearsAllCounters() {
        reviewEngine.recordAppLaunch()
        reviewEngine.recordKeyAction()

        reviewEngine.resetReviewState()

        assertEquals(0, reviewEngine.appLaunchCount)
        assertEquals(0, reviewEngine.keyActionCount)
        assertEquals(0, reviewEngine.reviewRequestedCount)
    }

    private fun mockActivity(): android.app.Activity {
        val field = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }
        val unsafe = field.get(null) as sun.misc.Unsafe
        return unsafe.allocateInstance(android.app.Activity::class.java) as android.app.Activity
    }
}
