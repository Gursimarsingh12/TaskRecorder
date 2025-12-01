package com.app.taskrecorder.di

import com.app.taskrecorder.core.database.AppDatabase
import com.app.taskrecorder.core.database.DatabaseFactory
import com.app.taskrecorder.core.network.HttpClientFactory
import com.app.taskrecorder.features.image_description.presentation.ImageDescriptionViewModel
import com.app.taskrecorder.features.noise_test.presentation.NoiseTestViewModel
import com.app.taskrecorder.features.photo_capture.presentation.PhotoCaptureViewModel
import com.app.taskrecorder.features.products.data.remote.ProductApi
import com.app.taskrecorder.features.products.data.repository.ProductRepositoryImpl
import com.app.taskrecorder.features.products.domain.repository.ProductRepository
import com.app.taskrecorder.features.products.domain.usecase.GetProductUseCase
import com.app.taskrecorder.features.products.domain.usecase.GetProductsUseCase
import com.app.taskrecorder.features.task_history.data.repository.TaskRepositoryImpl
import com.app.taskrecorder.features.task_history.domain.repository.TaskRepository
import com.app.taskrecorder.features.task_history.domain.usecase.GetAllTasksUseCase
import com.app.taskrecorder.features.task_history.domain.usecase.GetTaskStatsUseCase
import com.app.taskrecorder.features.task_history.domain.usecase.SaveTaskUseCase
import com.app.taskrecorder.features.task_history.presentation.TaskHistoryViewModel
import com.app.taskrecorder.features.text_reading.presentation.TextReadingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }
    
    single { get<DatabaseFactory>().create().build() }
    single { get<AppDatabase>().taskDao() }
    
    singleOf(::ProductApi)
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    
    singleOf(::TaskRepositoryImpl) bind TaskRepository::class
    
    singleOf(::GetProductsUseCase)
    singleOf(::GetProductUseCase)
    singleOf(::SaveTaskUseCase)
    singleOf(::GetAllTasksUseCase)
    singleOf(::GetTaskStatsUseCase)
    
    viewModelOf(::NoiseTestViewModel)
    viewModelOf(::TextReadingViewModel)
    viewModelOf(::ImageDescriptionViewModel)
    viewModelOf(::PhotoCaptureViewModel)
    viewModelOf(::TaskHistoryViewModel)
}
