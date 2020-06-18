package com.jpp.mpsearch

import androidx.navigation.NavController

interface SearchNavigator {
    fun bind(newNavController: NavController)
    fun unBind()
}