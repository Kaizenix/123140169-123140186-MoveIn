package com.example.movein.core.di

import com.example.movein.core.util.DatabaseDriverFactory
import com.example.movein.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import android.content.Context

// Fungsi pabrik untuk mengakali bug pembacaan compiler KMP
fun createDatabaseDriverFactory(context: Context): DatabaseDriverFactory {
    return DatabaseDriverFactory(context)
}

fun createDataStoreFactory(context: Context): DataStoreFactory {
    return DataStoreFactory(context)
}

val androidModule = module {
    single { createDatabaseDriverFactory(androidContext()) }
    single { createDataStoreFactory(androidContext()) }
}