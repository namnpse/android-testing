package com.namnp.testingandroid.live_data

import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.namnp.testingandroid.feature.live_data.GithubAccount
import com.namnp.testingandroid.feature.live_data.GithubApi
import com.namnp.testingandroid.feature.live_data.GithubViewModel
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import io.reactivex.Observable
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class GithubViewModelTest {

    // A JUnit Test Rule that swaps the background executor used by
    // the Architecture Components with a different one which executes each task synchronously.
    // You can use this rule for your host side tests that use Architecture Components.
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    // Test rule for making the RxJava to run synchronously in unit test
    @Rule
    @JvmField
    var schedulerRule = RxImmediateSchedulerRule()

//    companion object {
//        @ClassRule
//        @JvmField
//        val schedulers = RxImmediateSchedulerRule()
//    }

    @Mock
    lateinit var githubApi: GithubApi

    @Mock
    lateinit var observer: Observer<GithubAccount>

    lateinit var githubViewModel: GithubViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this) // Optional
        // initialize the ViewModel with a mocked github api
        githubViewModel = GithubViewModel(githubApi)
    }

    @Test
    fun `githubAccountLoginName successfully return correct data, update Live Data value`() {
        // mock data
        val githubAccountName = "namnpse"
        val githubAccount = GithubAccount(githubAccountName)

        // mock github api
        Mockito.`when`(githubApi.getGithubAccountObservable(githubAccountName))
            .thenReturn(Observable.just(Response.success(githubAccount)))

        // observe on the MutableLiveData with an observer
        githubViewModel.githubAccount.observeForever(observer)
        githubViewModel.fetchGithubAccountInfo(githubAccountName)

        // assert that the name matches
        assert(githubViewModel.githubAccount.value!!.login == githubAccountName)
    }

    @Test
    fun `githubAccountLoginName failure return error, then not change LiveData value`() {
        // mock data
        val githubAccountName = "namnpse"

        // mock github api
        Mockito.`when`(githubApi.getGithubAccountObservable(githubAccountName))
            .thenReturn(Observable.error(Throwable()))

        // observe on the MutableLiveData with an observer
        githubViewModel.githubAccount.observeForever(observer)
        githubViewModel.fetchGithubAccountInfo(githubAccountName)

        // assert that the name matches
        verify(observer, times(0)).onChanged(any())
    }

    @Before
    fun tearDown() {
    }
}