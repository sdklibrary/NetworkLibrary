package com.pretty.library.network

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

class RequestParam(
    private val debug: Boolean = false,
    private val logKey: String = "http"
) {

    private val params = mutableMapOf<String, Any>()

    fun add(key: String, value: Any) = apply {
        params[key] = value
    }

    fun build() = params

    fun buildBody(): RequestBody {
        val jsonData = Gson().toJson(params)
        if (debug)
            Log.i(logKey, "requestBody: $jsonData")
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonData
        )
    }

    fun buildString(): String {
        return Gson().toJson(params)
    }
}