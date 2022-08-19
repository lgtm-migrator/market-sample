package one.credify.marketsample.bnplandroid.model

import one.credify.sdk.core.model.UserProfile

internal class AppState {
    var userProfile: UserProfile? = null

    var user: User? = null

    companion object {
        private val mInstance = AppState()
        val instance: AppState
            get() {
                return mInstance
            }
    }
}