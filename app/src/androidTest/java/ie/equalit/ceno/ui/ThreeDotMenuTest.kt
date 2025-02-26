/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package ie.equalit.ceno.ui

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import ie.equalit.ceno.helpers.AndroidAssetDispatcher
import ie.equalit.ceno.helpers.BrowserActivityTestRule
import ie.equalit.ceno.helpers.RetryTestRule
import ie.equalit.ceno.helpers.TestAssetHelper
import ie.equalit.ceno.ui.robots.navigationToolbar
import ie.equalit.ceno.ui.robots.onboarding

/**
 *  Tests for verifying the main three dot menu options
 *
 *  Including:
 * - Verify all menu items present
 * - Forward button navigates forward to a page
 * - Refresh button refreshes page content
 * - Share button opens app overlay menu
 * - Request desktop site toggle forwards to desktop view of web page (TBD)
 * - Find in page button can locate web page text
 * - Report issue button forwards to gitubh issues (TBD)
 * - Open settings button opens Settings sub-menu
 *
 * Not included:
 * - TODO: Request desktop site (user mockWebServer to parse request headers)
 * - Stop button stops page loading (covered by smoke tests)
 */

class ThreeDotMenuTest {

    private val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    val activityTestRule = BrowserActivityTestRule()

    @Rule
    @JvmField
    val retryTestRule = RetryTestRule(3)

    @Before
    fun setUp() {
        mockWebServer = MockWebServer().apply {
            dispatcher = AndroidAssetDispatcher()
            start()
        }
        onboarding {
        }.skipOnboardingIfNeeded()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /* ktlint-disable no-blank-line-before-rbrace */ // This imposes unreadable grouping.
    @Test
    fun homeScreenMenuTest() {
        mDevice.waitForIdle()
        navigationToolbar {
        }.openThreeDotMenu {
            verifyThreeDotMenuExists()
            // These items should not exist in the home screen menu
            verifyForwardButtonDoesntExist()
            verifyReloadButtonDoesntExist()
            verifyStopButtonDoesntExist()
            verifyShareButtonDoesntExist()
            verifyRequestDesktopSiteToggleDoesntExist()
            verifyAddToHomescreenButtonDoesntExist()
            verifyAddToShortcutsButtonDoesntExist()
            verifyFindInPageButtonDoesntExist()
            // Only these items should exist in the home screen menu
            verifyClearCenoButtonExists()
            //TODO: Https-by-default currently disabled on homepage, add back when needed
            //verifyHttpsByDefaultButtonExists()
            //TODO: uBlock Origin takes some time to install, needs special test case
            //verifyUblockOriginButtonExists()
            verifyAddOnsButtonExists()
            verifyOpenSettingsExists()
        }
    }

    @Test
    fun threeDotMenuItemsTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)
        navigationToolbar {
            // pull up URL to ensure this is not a first-user 3 dot menu
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
            mDevice.waitForIdle()
        }
        navigationToolbar {
        }.openThreeDotMenu {
            verifyThreeDotMenuExists()
            verifyBackButtonExists()
            verifyForwardButtonExists()
            verifyReloadButtonExists()
            //TODO: stop button only appears during load, needs special test case
            //verifyStopButtonExists()
            //verifyShareButtonExists()
            verifyRequestDesktopSiteToggleExists()
            verifyClearCenoButtonExists()
            verifyAddToHomescreenButtonExists()
            verifyAddToShortcutsButtonExists()
            verifyFindInPageButtonExists()
            verifyHttpsByDefaultButtonExists()
            //TODO: uBlock Origin takes some time to install, needs special test case
            //verifyUblockOriginButtonExists()
            verifyAddOnsButtonExists()
            //verifySyncedTabsButtonExists()
            //verifyReportIssueExists()
            verifyOpenSettingsExists()
        }
    }

    @Test
    fun normalBrowsingTabNavigationTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)
        val nextWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 2)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
            verifyPageContent("Page content: 1")
        }
        navigationToolbar {
        }.enterUrlAndEnterToBrowser(nextWebPage.url) {
            verifyPageContent("Page content: 2")
        }.goBack {
            verifyPageContent("Page content: 1")
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.goForward {
            verifyPageContent("Page content: 2")
        }
    }

    @Test
    fun privateBrowsingTabNavigationTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)
        val nextWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 2)

        navigationToolbar {
        }.openTabTrayMenu {
            openPrivateBrowsing()
        }.openNewTab {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
            verifyUrl(defaultWebPage.url.toString())
        }
        navigationToolbar {
        }.enterUrlAndEnterToBrowser(nextWebPage.url) {
            verifyUrl(nextWebPage.url.toString())
        }.goBack {
            verifyUrl(defaultWebPage.url.toString())
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.goForward {
            verifyUrl(nextWebPage.url.toString())
        }
    }

    // need to add clear cache setup to ensure correct starting page
    // also, investigate why this periodically causes mockWebServer to crash
    @Test
    @Ignore("https://github.com/mozilla-mobile/reference-browser/issues/1314")
    fun refreshPageTest() {
        val refreshWebPage = TestAssetHelper.getRefreshAsset(mockWebServer)

        navigationToolbar {
            // load the default page, to be refreshed
            // (test assumes no cookies cached at test start)

        }.enterUrlAndEnterToBrowser(refreshWebPage.url) {
            verifyPageContent("DEFAULT")
        }
        navigationToolbar {
        }.openThreeDotMenu {
            // refresh page and verify
        }.refreshPage {
            verifyPageContent("REFRESHED")
        }
    }

    /* TODO: Implement "share" button in Ceno
    @Test
    fun doShareTest() {
        val genericURL = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(genericURL.url) {
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.openShare {
            verifyShareContentPanel()
        }
    }
    */

    @Test
    fun findInPageTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
            verifyPageContent("Page content: 1")
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.openFindInPage {
            verifyFindInPageBar()
            enterFindInPageQuery("e")
            verifyFindInPageResult("1/2")
            clickFindInPageNextButton()
            verifyFindInPageResult("2/2")
            clickFindInPagePreviousButton()
            verifyFindInPageResult("1/2")
            clickFindInPageCloseButton()
            verifyFindInPageBarIsDismissed()
        }
    }

    // so less flaky, we only test redirect to github login
    // (redirect happens with / without WIFI enabled)
    /* TODO: implement report issue in Ceno
    @Test
    fun reportIssueTest() {
        val loremIpsumWebPage = TestAssetHelper.getLoremIpsumAsset(mockWebServer)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(loremIpsumWebPage.url) {
            mDevice.waitForIdle()
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.reportIssue {
            mDevice.waitForIdle()
            verifyGithubUrl()
        }
    }
    */

    @Test
    fun openSettingsTest() {
        navigationToolbar {
        }.openThreeDotMenu {
        }.openSettings {
            verifySettingsViewExists()
        }
    }

    // Verifies the Synced tabs menu opens from a tab's 3 dot menu and displays the correct view if the user isn't signed in
    /*
    @Test
    fun openSyncedTabsTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.openSyncedTabs {
            verifyNotSignedInSyncTabsView()
        }
    }
    */

    // CENO: requestDesktopSiteTest seems to work for us
    //@Ignore("Failing with frequent ANR: https://bugzilla.mozilla.org/show_bug.cgi?id=1764605")
    @Test
    fun requestDesktopSiteTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.switchRequestDesktopSiteToggle {
        }.openThreeDotMenu {
            verifyRequestDesktopSiteIsTurnedOn()
        }.goBack {
        }.openThreeDotMenu {
        }.switchRequestDesktopSiteToggle {
        }.openThreeDotMenu {
            verifyRequestDesktopSiteIsTurnedOff()
        }
    }

    @Test
    fun addToHomeScreenTest() {
        val defaultWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterUrlAndEnterToBrowser(defaultWebPage.url) {
        }
        navigationToolbar {
        }.openThreeDotMenu {
        }.openAddToHomeScreen {
            clickCancelAddToHomeScreenButton()
        }

        navigationToolbar {
        }.openThreeDotMenu {
        }.openAddToHomeScreen {
            clickAddAutomaticallyToHomeScreenButton()
        }.openHomeScreenShortcut(defaultWebPage.title) {
            verifyUrl(defaultWebPage.url.toString())
        }
    }
}
