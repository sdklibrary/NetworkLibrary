package com.pretty.library.network

import android.text.TextUtils
import com.pretty.library.network.credential.SSLParams
import com.pretty.library.network.credential.Tls12SocketFactory
import com.pretty.library.network.credential.UnSafeTrustManager
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.CookieJar
import okhttp3.Interceptor
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.*

abstract class NetworkConfig {

    lateinit var hostUrl: String
    open val readTimeout: Long = 2000
    open val writeTimeout: Long = 2000
    open val connectTimeout: Long = 2000

    open fun headerIntercept(): Interceptor {
        return Interceptor { chain -> chain.proceed(chain.request()) }
    }

    open fun httpIntercept(): Interceptor {
        return Interceptor { chain -> chain.proceed(chain.request()) }
    }

    open fun connectionPool(): ConnectionPool {
        return ConnectionPool()
    }

    open fun retryOnConnectionFailure(): Boolean {
        return true
    }

    open fun sSLParams(): SSLParams {
        return try {
            val x509TrustManager: X509TrustManager = UnSafeTrustManager()
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), null)
            SSLParams(Tls12SocketFactory(sslContext.socketFactory), x509TrustManager)
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        }
    }

    open fun hostVerifier(): HostnameVerifier? {
        return HostnameVerifier { hostname: String, session: SSLSession ->
            val host = try {
                URL(hostUrl).host
            } catch (e: Exception) {
                ""
            }
            return@HostnameVerifier if (TextUtils.equals(host, hostname))
                true
            else {
                val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                hv.verify(hostname, session)
            }
        }
    }

    open fun cache(): Cache? {
        return null
    }

    open fun cookieJar(): CookieJar? {
        return null
    }

    abstract fun callAdapterFactory(): ILiveDataCallAdapterFactory
}