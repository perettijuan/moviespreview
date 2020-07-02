package com.jpp.mpdata.cache.room

internal sealed class ImageTypes(val id: Int) {
    object PosterType : ImageTypes(11)
    object ProfileType : ImageTypes(22)
    object BackdropType : ImageTypes(33)
}
