package khom.pavlo.footballlover

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import khom.pavlo.footballlover.ui.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeRule.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        composeRule.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
    }

    @Test
    fun bottomNavigation_showsAllTabs() {
        composeRule.onNodeWithText(string(R.string.nav_matches)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.nav_live)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.nav_favorites)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.nav_leagues)).assertIsDisplayed()
    }

    @Test
    fun switchingTabs_updatesTopBarTitle_andShowsPlaceholderScreens() {
        composeRule.onNodeWithText(string(R.string.nav_matches)).assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.nav_live)).performClick()
        composeRule.onNodeWithText(string(R.string.placeholder_live)).assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.nav_leagues)).performClick()
        composeRule.onNodeWithText(string(R.string.placeholder_leagues)).assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.nav_favorites)).performClick()
        composeRule.onNodeWithText(string(R.string.nav_favorites)).assertIsDisplayed()
    }

    @Test
    fun settingsButton_opensSettings_andBackReturnsHome() {
        composeRule.onNodeWithContentDescription(string(R.string.title_settings)).performClick()

        composeRule.onNodeWithText(string(R.string.title_settings)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.title_language)).assertIsDisplayed()

        pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(string(R.string.nav_matches)).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsLanguageOptions() {
        composeRule.onNodeWithContentDescription(string(R.string.title_settings)).performClick()

        composeRule.onNodeWithText(string(R.string.lang_english)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.lang_ukrainian)).assertIsDisplayed()
    }

    @Test
    fun settingsLanguage_canSwitchToUkrainian_andBackToEnglish() {
        composeRule.onNodeWithContentDescription(string(R.string.title_settings)).performClick()

        composeRule.onNodeWithText(string(R.string.lang_ukrainian)).performClick()
        composeRule.waitForIdle()

        // Re-read strings from current activity resources after locale change.
        composeRule.onNodeWithText(string(R.string.title_settings)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.lang_english)).assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.lang_english)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(string(R.string.title_settings)).assertIsDisplayed()
    }

    private fun string(resId: Int): String = composeRule.activity.getString(resId)
}
