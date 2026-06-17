package com.lksnext.ParkingAVillegas.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

class JsonStorage(
    context: Context
) {

    private val gson: Gson =
        GsonBuilder()
            .setPrettyPrinting()
            .create()

    private val filesDir =
        context.filesDir

    fun <T> read(
        fileName: String,
        type: java.lang.reflect.Type
    ): T? {

        val file = File(filesDir, fileName)

        if (!file.exists()) return null

        return try {
            gson.fromJson(file.readText(), type)
        } catch (e: Exception) {
            null
        }
    }

    fun write(
        fileName: String,
        data: Any
    ) {

        val file = File(filesDir, fileName)

        file.writeText(
            gson.toJson(data)
        )
    }
}