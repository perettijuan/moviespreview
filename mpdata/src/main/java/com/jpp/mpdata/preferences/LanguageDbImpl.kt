package com.jpp.mpdata.preferences

import android.content.Context
import com.jpp.mpdata.datasources.language.LanguageDb

/**
 * [LanguageDb] implementation.
 * This DB is backed by SharedPreferences since it needs to store only key-value data.
 */
class LanguageDbImpl(private val context: Context) : LanguageDb {

    private val preferences by lazy { context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE) }

    override fun getStoredLanguageString(): String? {
        return preferences.getString(KEY_LANGUAGE_STORED, null)
    }

    override fun updateLanguageString(languageString: String) {
        with(preferences.edit()) {
            putString(KEY_LANGUAGE_STORED, languageString)
            apply()
        }
    }

    private companion object {
        const val PREFERENCES_FILE_NAME = "com.jpp.moviespreview.preferences.language"
        const val KEY_LANGUAGE_STORED = "lang_stored"
    }
}
