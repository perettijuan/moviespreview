package com.jpp.mp.common.extensions

import android.content.Intent
import android.net.Uri

/**
 * Formats the [Intent] to expanded the provided [uriString] in a completely
 * new task.
 */
fun Intent.cleanView(uriString: String): Intent {
    action = Intent.ACTION_VIEW
    data = Uri.parse(uriString)
    val flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
    addFlags(flags)
    return this
}

/**
 * Formats the [Intent] to expanded the native sharing options.
 */
fun Intent.send(sharingText: String): Intent {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, sharingText)
    type = "text/plain"
    return this
}

/**
 * Formats the [Intent] to expanded the web browser at the provided [url].
 */
fun Intent.web(url: String): Intent {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
    return this
}
