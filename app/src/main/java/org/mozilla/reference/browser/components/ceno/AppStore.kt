/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.components.ceno

import mozilla.components.lib.state.Middleware
import mozilla.components.lib.state.Store
import org.mozilla.reference.browser.components.ceno.appstate.AppAction
import org.mozilla.reference.browser.components.ceno.appstate.AppState
import org.mozilla.reference.browser.components.ceno.appstate.AppStoreReducer

/** CENO: Copied from Fenix
 *
 * A [Store] that holds the [AppState] for the app and reduces [AppAction]s
 * dispatched to the store.
 *
 * This store is not persisted to disk and is scoped to the life-cycle of the application.
 */
class AppStore(
    initialState: AppState = AppState(),
    middlewares: List<Middleware<AppState, AppAction>> = emptyList()
) : Store<AppState, AppAction>(initialState, AppStoreReducer::reduce, middlewares)