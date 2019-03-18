package com.jpp.mpdomain.usecase

import com.jpp.mp.common.extensions.transformToInt

abstract class ConfigureImagePathUseCase {

    protected fun createUrlForPath(original: String?, baseUrl: String, sizes: List<String>, targetSize: Int): String? {
        return original?.let {
            StringBuilder()
                    .append(baseUrl)
                    .append(sizes.find { size -> size.transformToInt() ?: 0 >= targetSize }
                            ?: sizes.last())
                    .append(it)
                    .toString()
        }
    }
}