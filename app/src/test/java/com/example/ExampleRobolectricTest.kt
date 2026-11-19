package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.ChallengeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("منصة التحدي والمرح 3D", appName)
    }

    @Test
    fun `verify challenge viewmodel scoring and trivia flow`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ChallengeViewModel(app)
        viewModel.restoreFactoryDefaults()

        val initialState = viewModel.uiState.value
        assertEquals(2, initialState.teams.size)
        assertEquals(4, initialState.categories.size)
        assertEquals(12, initialState.mysteryBoxes.size)

        // Open a 200-point trivia question, activate double points, and award to Team 1
        val firstCat = initialState.categories[0]
        val secondQ = firstCat.questions[1] // 200 pts
        viewModel.openTriviaQuestion(firstCat, secondQ)
        assertNotNull(viewModel.uiState.value.activeDialog)

        viewModel.toggleDoublePoints()
        assertTrue(viewModel.uiState.value.isDoublePointsActive)

        viewModel.awardActiveChallengeToTeam(teamId = 1)
        val updatedTeam1 = viewModel.uiState.value.teams.first { it.id == 1 }
        assertEquals(400, updatedTeam1.score)
    }
}
