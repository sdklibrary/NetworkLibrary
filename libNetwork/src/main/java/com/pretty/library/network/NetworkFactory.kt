package com.pretty.library.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object NetworkFactory {

    private lateinit var config: NetworkConfig

    private val retrofits = ConcurrentHashMap<String, Retrofit>()
    private val serviceMap = ConcurrentHashMap<String, Any>()

    private fun createRetrofit(host: String): Retrofit {
        return if (retrofits[host] != null)
            retrofits[host]!!
        else {
            val params = config.sSLParams()
            val builder = OkHttpClient.Builder()
                .readTimeout(config.readTimeout, TimeUnit.SECONDS)
                .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
                .addInterceptor(config.headerIntercept())
                .addInterceptor(config.httpIntercept())
                .sslSocketFactory(params.socketFactory, params.trustManager)
                .connectionPool(config.connectionPool())
                .retryOnConnectionFailure(config.retryOnConnectionFailure())

            if (config.hostVerifier() != null)
                builder.hostnameVerifier(config.hostVerifier()!!)

            if (config.cache() != null)
                builder.cache(config.cache()!!)

            if (config.cookieJar() != null)
                builder.cookieJar(config.cookieJar()!!)

            val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(config.callAdapterFactory())
                .build()
            retrofits[host] = retrofit
            retrofit
        }
    }

    fun initConfig(config: NetworkConfig) {
        NetworkFactory.config = config
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> createService(clazz: Class<T>, host: String = config.hostUrl): T {
        return if (serviceMap[clazz.name] != null) {
            serviceMap[clazz.name] as T
        } else {
            val service = createRetrofit(host).create(clazz)
            serviceMap[clazz.name] = service!!
            service
        }
    }

}