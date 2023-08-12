package com.namnp.testingandroid.feature.android_test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import com.namnp.testingandroid.R

class ResourceComparatorTest {
    // don't use global/singleton here -> easy to become flaky test
    // create different instances for each of test case
//    private val resourceComparator = ResourceComparator()
    private lateinit var resourceComparator: ResourceComparator

    @Before
    fun setup() {
        // init here to avoid init resourceComparator instance multiple times -> avoid duplicate code
        resourceComparator = ResourceComparator()
    }

    @Test
    fun stringResourceSameAsGivenString_returnsTrue() {
//        resourceComparator = ResourceComparator()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparator.isEqual(context, R.string.app_name, "TestingAndroid")
        assertThat(result).isTrue()
    }

    @Test
    fun stringResourceDifferentAsGivenString_returnsFalse() {
//        resourceComparator = ResourceComparator()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparator.isEqual(context, R.string.app_name, "Hello")
        assertThat(result).isFalse()
    }
}