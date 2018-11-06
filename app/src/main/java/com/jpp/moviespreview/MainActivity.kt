package com.jpp.moviespreview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jpp.moviespreview.domainlayer.DomainLogger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DomainLogger().log("onCreate")
    }
}