package com.pretty.library.network.credential

import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

data class SSLParams(
    val socketFactory: SSLSocketFactory,
    val trustManager: X509TrustManager
)