package com.shopapp

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.drawee.backends.pipeline.Fresco
import com.shopapp.data.dao.Dao
import com.shopapp.data.dao.impl.DaoImpl
import com.shopapp.di.component.AppComponent
import com.shopapp.di.component.DaggerAppComponent
import com.shopapp.di.module.RepositoryModule
import com.shopapp.shopify.api.ShopifyApi
import com.shopapp.gateway.Api
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins

open class ShopApplication : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()


        val api = ShopifyApi(this, "porganicworld.myshopify.com",
                "62761bc137e3dac4c66d1d1a93d8dab3",
                "1e5f1c6facb67ef0bf9e0c4af642d192",
                "5c3061bb2e053642fc1422409ccbb89e") ; //null //Initialize your api here.
        val dao = DaoImpl(this)

        appComponent = buildAppComponent(api, dao)

        setupFresco()
        setupFabric()

        RxJavaPlugins.setErrorHandler { e ->
            e.printStackTrace()
            Crashlytics.logException(e)
        }
    }

    protected open fun buildAppComponent(api: Api?, dao: Dao?): AppComponent {
        val builder = DaggerAppComponent.builder()
        if (api != null && dao != null) {
            builder.repositoryModule(RepositoryModule(api, dao))
        }
        return builder.build()
    }

    private fun setupFabric() {
        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)
    }

    private fun setupFresco() {
        Fresco.initialize(this)
    }
}