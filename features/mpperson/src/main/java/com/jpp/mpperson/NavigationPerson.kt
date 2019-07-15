package com.jpp.mpperson

import android.os.Bundle
import com.jpp.mp.common.extensions.getStringOrFail

/**
 * Contains utilities to perform navigation to the person module.
 */
object NavigationPerson {

    fun navArgs(personId: String, personImageUrl: String, personName: String) = Bundle()
            .apply {
                putString("personId", personId)
                putString("personImageUrl", personImageUrl)
                putString("personName", personName)
            }

    fun personId(args: Bundle?) = args.getStringOrFail("personId")
    fun personImageUrl(args: Bundle?) = args.getStringOrFail("personImageUrl")
    fun personName(args: Bundle?) = args.getStringOrFail("personName")
}