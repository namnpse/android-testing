package com.namnp.testingandroid.feature.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.namnp.testingandroid.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        fireApiAndGetData()
    }

    @SuppressLint("SetTextI18n")
    fun fireApiAndGetData(){
        viewModel.getPersonalDetails()
        viewModel.personalDetailsData.observe(this, Observer { user ->
            when(user.status){
                Resource.Status.SUCCESS -> {
                    user.data.let {
                        findViewById<TextView>(R.id.tv_show_data).text =
                            "Name: " + it?.name + "\n" +
                                    "Current City: " + it?.currentCity + "\n" +
                                    "Email: " + it?.email + "\n" +
                                    "Trained At: " + it?.trainedAt + "\n" +
                                    "Skills: " + it?.skills + "\n"
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(applicationContext,user.message, LENGTH_LONG).show()
                }
            }
        })
    }
}