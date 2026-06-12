package com.example.movein.core.di

import com.example.movein.core.network.HttpClientFactory
import com.example.movein.core.util.DatabaseDriverFactory
import com.example.movein.data.local.datastore.DataStoreFactory
import com.example.movein.data.local.datastore.UserPreferences
import com.example.movein.data.local.datastore.create
import com.example.movein.data.remote.api.GeminiService
import com.example.movein.data.repository.AIRepositoryImpl
import com.example.movein.data.repository.NoteRepositoryImpl
import com.example.movein.domain.repository.AIRepository
import com.example.movein.domain.repository.NoteRepository
import com.example.movein.domain.usecase.DeleteNoteUseCase
import com.example.movein.domain.usecase.GenerateIdeasUseCase
import com.example.movein.domain.usecase.GetAllNotesUseCase
import com.example.movein.domain.usecase.ImproveWritingUseCase
import com.example.movein.domain.usecase.SaveNoteUseCase
import com.example.movein.domain.usecase.SearchNotesUseCase
import com.example.movein.domain.usecase.SummarizeNoteUseCase
import com.example.movein.presentation.auth.AuthViewModel
import com.example.movein.presentation.screens.addnote.AddNoteViewModel
import com.example.movein.presentation.screens.ai.AIAssistantViewModel
import com.example.movein.presentation.screens.detail.NoteDetailViewModel
import com.example.movein.presentation.screens.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    single {
        GeminiService(
            client = get(),
            apiKey = get()
        )
    }
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        val clazz = Class.forName("com.example.movein.data.local.NoteDatabase")
        val constructor = clazz.getConstructor(app.cash.sqldelight.db.SqlDriver::class.java)
        constructor.newInstance(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    single<AIRepository> { AIRepositoryImpl(get()) }
}

val useCaseModule = module {
    single { GetAllNotesUseCase(get()) }
    single { SearchNotesUseCase(get()) }
    single { SaveNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { SummarizeNoteUseCase(get()) }
    single { ImproveWritingUseCase(get()) }
    single { GenerateIdeasUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::AuthViewModel)
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}