/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.components.ceno

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import mozilla.components.browser.state.state.WebExtensionState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.webextension.Action
import mozilla.components.support.base.feature.LifecycleAwareFeature
import org.mozilla.reference.browser.ext.components

/**
 * This is a stripped-down version of the WebExtensionToolbarFeature android-component
 * that allows only the pageAction to be added to the toolbar instead of also adding browserAction button.
 * Additionally, it adds the pageAction as a BrowserToolbar.Button, instead of
 * as a WebExtensionToolbarAction, which failed to trigger the pageAction in reference-browser
 *
 * Web extension toolbar implementation that updates the toolbar whenever the state of web
 * extensions changes.
 */
class WebExtensionToolbarFeature(
        private val toolbar: BrowserToolbar,
        private var store: BrowserStore
) : LifecycleAwareFeature {
    // This maps web extension ids to [WebExtensionToolbarAction]s for efficient
    // updates of global and tab-specific browser/page actions within the same
    // lifecycle.
    @VisibleForTesting
    internal val webExtensionPageActions = HashMap<String, BrowserToolbar.Button>()

    /**
     * Starts observing for the state of web extensions changes
     */
    override fun start() {
        // The feature could start with an existing view and toolbar so
        // we have to check if any stale actions (from uninstalled or
        // disabled extensions) are being displayed and remove them.
        webExtensionPageActions
                .filterKeys { !store.state.extensions.containsKey(it) || store.state.extensions[it]?.enabled == false }
                .forEach { (extensionId, action) ->
                    toolbar.removePageAction(action)
                    toolbar.invalidateActions()
                    webExtensionPageActions.remove(extensionId)
                }
    }

    override fun stop() {
        /*
        nothing to stop
        */
    }

    // Don't need to block extension in certain tabs, but leaving code for reference
    /*
    private fun extensionNotAllowedInTab(
            extension: WebExtensionState?,
            tab: SessionState?
    ): Boolean = extension?.allowedInPrivateBrowsing == false && tab?.content?.private == true
     */

    suspend fun addPageActionButton(context: Context, id: String) {
        context.components.core.store.state.extensions[id]?.let { ext ->
            ext.pageAction?.let { pageAction ->
                /* loading icon is a suspend function and must be called in coroutine */
                val loadIcon = pageAction.loadIcon?.invoke(32)
                /* add the WebExtension to the toolbar as page action button */
                addOrUpdateAction(
                        extension = ext,
                        globalAction = pageAction,
                        imageDraw = BitmapDrawable(context.resources, loadIcon),
                        contentDesc = ext.name!! //"CENO Sources"
                )
            }
        }
    }

    private fun addOrUpdateAction(
            extension: WebExtensionState,
            globalAction: Action,
            imageDraw: Drawable,
            contentDesc : String
            //tabAction: Action?
    ) {
        // Add the global page/browser action if it doesn't exist
        webExtensionPageActions.getOrPut(extension.id) {
            val toolbarButton = BrowserToolbar.Button(
                    imageDrawable = imageDraw,
                    contentDescription = contentDesc,
                    visible = { true },
                    listener = globalAction.onClick
            )
            toolbar.addPageAction(toolbarButton)
            toolbar.invalidateActions()
            toolbarButton
        }

        // Apply tab-specific override of page/browser action
        // Don't need tabAction override, but leaving for reference
        /*
        tabAction?.let {
            toolbarAction.action = globalAction.copyWithOverride(it)
            toolbar.invalidateActions()
        }
        */
    }
}
