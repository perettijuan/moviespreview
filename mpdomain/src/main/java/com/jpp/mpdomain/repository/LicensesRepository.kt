package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Licenses

interface LicensesRepository {
    suspend fun loadLicences(): Licenses?
}
