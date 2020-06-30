package com.jpp.mpdata.repository.licenses

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.repository.LicensesRepository
import java.io.IOException

class LicensesRepositoryImpl(private val context: Context) : LicensesRepository {

    override suspend fun loadLicences(): Licenses? = try {
        val input = context.assets.open(LICENSES_FILE_LOCATION)
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        gsonLoader.fromJson(String(buffer))
    } catch (e: IOException) {
        null
    }

    /**
     * Helper class to load an object from GSON
     */
    private inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    private companion object {
        const val LICENSES_FILE_LOCATION = "licenses/licenses.json"
        val gsonLoader by lazy { Gson() }
    }
}
