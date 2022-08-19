package one.credify.marketsample.bnplandroid

import android.app.Application
import one.credify.marketsample.bnplandroid.model.Constant
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.model.Environment
import one.credify.sdk.core.model.Language

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CredifySDK.Builder()
            .withApiKey(Constant.API_KEY)
            .withContext(this)
            .withEnvironment(Environment.SIT)
            .withMarketId(Constant.APP_ID)
            .build()

    }
}