package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Licenses

interface LicensesRepository {
    fun loadLicences(): Licenses?
}
