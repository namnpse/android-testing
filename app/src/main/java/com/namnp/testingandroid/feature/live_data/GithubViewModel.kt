package com.namnp.testingandroid.feature.live_data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class GithubViewModel(private val githubApi: GithubApi) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    var githubAccount = MutableLiveData<GithubAccount>()

    internal fun fetchGithubAccountInfo(username: String) {
        val disposable = githubApi.getGithubAccountObservable(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<GithubAccount>>() {
                override fun onNext(response: Response<GithubAccount>) {
                    githubAccount.value = response.body()
                }

                override fun onComplete() {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        compositeDisposable.add(disposable)
    }

    // This is called by the Android Activity when the activity is destroyed
    public override fun onCleared() {
        Log.d("GithubViewModel", "onCleared()")
        compositeDisposable.dispose()
        super.onCleared()
    }

}