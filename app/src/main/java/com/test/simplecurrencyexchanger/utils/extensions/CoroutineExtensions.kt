package com.test.simplecurrencyexchanger.utils.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.launchPeriodic(
    enable: Boolean,
    repeatMillis: Long,
    action: suspend () -> Unit
): Job {
    return launch {
        while (enable) {
            action()
            delay(repeatMillis)
        }
    }
}