package com.jpp.moviespreview.datalayer

import android.util.Log
import com.jpp.moviespreview.common.CommonLogger

class DataLogger {

    fun log(value: String) {
       Log.d("DataLogger", "Value in data logger -> $value")
    }
}