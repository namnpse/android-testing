package com.namnp.testingandroid.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: UserRepository)
    : ViewModel() {
    val personalDetailsData : MutableLiveData<Resource<PersonalDetailsResponse>> = MutableLiveData<Resource<PersonalDetailsResponse>>()
    fun getPersonalDetails() = viewModelScope.launch {
        repository.getPersonalDetailsApi().let {
            if (it.isSuccessful){
                personalDetailsData.postValue(Resource.success(it.body()))
            }else{
                personalDetailsData.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}