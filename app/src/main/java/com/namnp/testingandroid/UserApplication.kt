package com.namnp.testingandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UserApplication: Application()


// NOTE:
// 1. Characteristics:
// + Scope
// + Speed
// + Fidelity (how close are your test cases in real-world scenarios, how many cases they covers, how it works well in the real world)
// 2. Void Flaky tests (sometime pass, sometime fail), e.g: random numbers, external factors... effect output of the test
