package com.pretty.library.network

import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


abstract class ILiveDataCallAdapterFactory : Factory() {

    override fun get(
        returnType: Type,
        annotations:
        Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) return null
        //获取第一个泛型类型
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        return createCallAdapter(observableType)
    }

    abstract fun createCallAdapter(observableType: Type): CallAdapter<*, *>
}