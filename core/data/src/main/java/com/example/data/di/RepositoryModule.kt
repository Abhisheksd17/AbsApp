package com.example.data.di

import com.example.data.repositoryImpl.AuthRepositoryImpl
import com.example.data.repositoryImpl.CallRepositoryImpl
import com.example.data.repositoryImpl.ChatListRepositoryImpl
import com.example.data.repositoryImpl.ChatRepositoryImpl
import com.example.data.repositoryImpl.MessageRepositoryImpl
import com.example.data.repositoryImpl.ContactRepositoryImpl
import com.example.data.repositoryImpl.TokenRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.CallRepository
import com.example.domain.repository.ChatListRepository
import com.example.domain.repository.ChatRepository
import com.example.domain.repository.MessageRepository
import com.example.domain.repository.ContactRepository
import com.example.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
 abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatListRepository(
        impl: ChatListRepositoryImpl
    ): ChatListRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        impl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindCallRepository(
        impl: CallRepositoryImpl
    ): CallRepository


    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        impl: TokenRepositoryImpl
    ): TokenRepository




}

