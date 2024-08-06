package com.namnp.testingandroid.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.namnp.testingandroid.feature.fire_store.User
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {

    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS) // wait 2 seconds for the live data to be emitted

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}

val testUser = User("user_id", "Nam Nguyen", "namnpse@test.com", "09031232323")