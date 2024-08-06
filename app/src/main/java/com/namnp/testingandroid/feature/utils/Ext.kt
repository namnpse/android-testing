package com.namnp.testingandroid.feature.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Task Extension
suspend fun <T> Task<T>.await(): Result<T> {
    if (isComplete) {
        val e = exception
        if (e != null) return Result.Error(e)

        return if (isCanceled) {
            Result.Canceled(CancellationException("Task $this was cancelled normally."))
        } else {
            Result.Success(result as T)
        }
    }

    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener {
            val e = exception
            if (e != null) continuation.resumeWithException(e)

            if (isCanceled) continuation.cancel()
            else continuation.resume(Result.Success(result as T))
        }
    }
}