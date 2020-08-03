package com.codechallenge.neugelb.mockdi

import com.codechallenge.neugelb.network.Repository
import com.codechallenge.neugelb.network.RepositoryImpl
import com.codechallenge.neugelb.network.RepositoryService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class MainMockModule {
    @Provides
    @Singleton
    fun getUrl(): String = "http://localhost:8080/"

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, url: String) = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideRepositoryService(retrofit: Retrofit) =
        retrofit.create(RepositoryService::class.java)

    @Provides
    @Singleton
    fun provideRepository(repository: RepositoryImpl): Repository = repository
}