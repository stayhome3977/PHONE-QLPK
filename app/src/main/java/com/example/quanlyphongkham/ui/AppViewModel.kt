package com.example.quanlyphongkham.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.quanlyphongkham.QlpkApp
import com.example.quanlyphongkham.di.AppContainer

/** Creates a ViewModel scoped to the current navigation entry, wired from the app's [AppContainer]. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(key: String? = null, crossinline create: (AppContainer) -> VM): VM {
    val container = (LocalContext.current.applicationContext as QlpkApp).container
    return viewModel(key = key, factory = viewModelFactory { initializer { create(container) } })
}

@Composable
fun appContainer(): AppContainer = (LocalContext.current.applicationContext as QlpkApp).container
