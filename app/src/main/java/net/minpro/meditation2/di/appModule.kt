package net.minpro.meditation2.di

import android.app.Application
import net.minpro.meditation2.model.UserSettingsRepository
import net.minpro.meditation2.service.MusicServiceHelper
import net.minpro.meditation2.view.dialog.LevelSelectDialog
import net.minpro.meditation2.view.dialog.ThemeSelectDialog
import net.minpro.meditation2.view.dialog.TimeSelectDialog
import net.minpro.meditation2.view.main.MainFragment
import net.minpro.meditation2.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    factory { MainFragment() }
    factory { LevelSelectDialog() }
    factory { ThemeSelectDialog() }
    factory { TimeSelectDialog() }
    factory { MusicServiceHelper(get()) }
    factory { UserSettingsRepository() }

    viewModel { MainViewModel(androidContext() as Application) }
}