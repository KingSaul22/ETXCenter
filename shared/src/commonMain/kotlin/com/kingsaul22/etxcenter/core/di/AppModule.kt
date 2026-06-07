package com.kingsaul22.etxcenter.core.di

import com.kingsaul22.etxcenter.data.repository.AuthRepositoryImpl
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import com.kingsaul22.etxcenter.feature.auth.AuthViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    // Global Firebase instances
    single { Firebase.auth }
    single { Firebase.database }

    // Bind the Interface to the Implementation
    single<IAuthRepository> { AuthRepositoryImpl(get()) }

    // Register the ViewModel (using modern Koin DSL)
    // NOTE: Depending on your exact Koin version, you might use `factory { AuthViewModel(get()) }`
    // but `viewModelOf` or `factory` works fine in KMP. Let's use factory to be safe across versions.
    factory { AuthViewModel(get()) }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}