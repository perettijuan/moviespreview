package com.jpp.mp.common.extensions

import android.content.Intent
import android.net.Uri
import android.os.Build

/**
 * Formats the [Intent] to expanded the provided [uriString] in a completely
 * new task.
 */
fun Intent.cleanView(uriString: String): Intent {
    action = Intent.ACTION_VIEW
    data = Uri.parse(uriString)
    var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
    } else {
        flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
    }
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
