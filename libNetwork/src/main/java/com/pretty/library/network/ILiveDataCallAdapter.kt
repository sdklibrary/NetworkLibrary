package com.pretty.library.network

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

abstract class ILiveDataCallAdapter<R, T> : CallAdapter<R, LiveData<T>> {

    private var responseType: Type? = null

    fun setResponseType(type: Type) = apply {
        this.responseType = type
    }

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<T> {
        return object : LiveData<T>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(onNetSuccess(call, response))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            postValue(onNetFail(call, throwable))
                        }
                    })
                }
            }
        }
    }

    abstract fun onNetSuccess(call: Call<R>, response: Response<R>): T

    abstract fun onNetFail(call: Call<R>, throwable: Throwable): T
}