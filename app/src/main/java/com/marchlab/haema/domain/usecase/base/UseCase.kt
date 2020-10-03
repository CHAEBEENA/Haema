package com.marchlab.haema.domain.usecase.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.marchlab.haema.util.result.Result
import timber.log.Timber
import timber.log.debug

abstract class UseCase<in Param, Type> {

    suspend operator fun invoke(
        parameters: Param,
        result: MutableLiveData<Result<Type>>
    ) {
        try {
            result.postValue(Result.Loading)

            try {
                result.postValue(Result.Success(withContext(Dispatchers.IO) { execute(parameters) }))

                Timber.debug { "${this::class.java.simpleName} succeeded" }
            } catch (exception: Exception) {
                result.postValue(Result.Error(exception))

                Timber.debug(exception) { "${this::class.java.simpleName} throw exception" }
            }
        } catch (exception: Exception) {
            result.postValue(Result.Error(exception))

            Timber.debug(exception) { "${this::class.java.simpleName} throw exception" }
        }
    }

    suspend operator fun invoke(parameters: Param): LiveData<Result<Type>> {
        val liveCallback: MutableLiveData<Result<Type>> = MutableLiveData()
        this(parameters, liveCallback)
        return liveCallback
    }

    /**
     *  Override this to set the code to be executed
     */
    protected abstract suspend fun execute(parameters: Param): Type
}

suspend operator fun <Type> UseCase<Unit, Type>.invoke(): LiveData<Result<Type>> = this(Unit)

suspend operator fun <Type> UseCase<Unit, Type>.invoke(result: MutableLiveData<Result<Type>>) = this(Unit, result)