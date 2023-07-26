package com.namnp.testingandroid.feature.live_data

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.namnp.testingandroid.R

class GithubActivity : AppCompatActivity() {

    lateinit var viewModel : GithubViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github)

        val factory = GithubActivityViewModelFactory(Network.getGitHubApi(this))
        viewModel = ViewModelProviders.of(this, factory).get(GithubViewModel::class.java)
        viewModel.fetchGithubAccountInfo("google")

        initObserver()
    }

    private fun initObserver() {
        val gitHubAccountObserver = Observer<GithubAccount> { githubAccount ->
                findViewById<TextView>(R.id.tv_account_info).text = githubAccount!!.login + "\n" + githubAccount.createdAt
            }

        viewModel.githubAccount.observe(this, gitHubAccountObserver)
    }

    // this is for passing the constructor parameter into the ViewModel
    class GithubActivityViewModelFactory(private val githubApi: GithubApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GithubViewModel::class.java)) {
                return GithubViewModel(githubApi) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}