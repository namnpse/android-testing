package com.namnp.testingandroid.user.util

import java.io.InputStreamReader

class MockResponseFileReader(path: String) {
    val content: String
    init {
        val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }
}
//[class MockResponseFileReader(path: String) { val content: String init { val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path)) content = reader.readText() reader.close() } }] x