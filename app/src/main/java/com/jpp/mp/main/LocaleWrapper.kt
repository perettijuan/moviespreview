package com.jpp.mp.main

import java.util.Locale

/**
 * This class exists mostly because of testing purposes: avoid using
 * Locale.XXX directly in production code.
 */
class LocaleWrapper {
    fun getDefault(): Locale = Locale.getDefault()
    fun localeFrom(from: String) = Locale(from)
}
