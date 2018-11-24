package com.jpp.moviespreview.domainlayer

interface ConnectivityVerifier {
    fun isConnectedToNetwork(): Boolean
}