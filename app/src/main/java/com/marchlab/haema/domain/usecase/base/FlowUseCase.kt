package com.marchlab.haema.domain.usecase.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.flow.*
import timber.log.Timber
import timber.log.debug

@ExperimentalCoroutinesApi
abstract class FlowUseCase<in Param, Type> {

    suspend operator fun invoke(
        parameters: Param,
        result: MutableLiveData<Result<Type>>
    ) = execute(parameters)
        .onStart { result.postValue(Result.Loading) }
        .catch {
            result.postValue(Result.Error(Exception(it)))

            Timber.debug(it) { "${this::class.java.simpleName} throw exception" }
        }
        .flowOn(Dispatchers.IO)
        .collect {
            result.postValue(Result.Success(it))

            Timber.debug { "${this::class.java.simpleName} succeeded" }
        }

    suspend operator fun invoke(parameters: Param): LiveData<Result<Type>> {
        val liveCallback: MutableLiveData<Result<Type>> = MutableLiveData()
        this(parameters, liveCallback)
        return liveCallback
    }

    abstract suspend fun execute(parameters: Param): Flow<Type>
}

@ExperimentalCoroutinesApi
suspend operator fun <Type> FlowUseCase<Unit, Type>.invoke(): LiveData<Result<Type>> = this(Unit)

@ExperimentalCoroutinesApi
suspend operator fun <Type> FlowUseCase<Unit, Type>.invoke(result: MutableLiveData<Result<Type>>) = this(Unit, result)