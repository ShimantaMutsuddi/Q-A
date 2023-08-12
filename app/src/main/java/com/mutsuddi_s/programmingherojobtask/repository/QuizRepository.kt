package com.mutsuddi_s.mvvm.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mutsuddi_s.mvvm.Api.ApiInterface
import com.mutsuddi_s.mvvm.model.Question
import com.mutsuddi_s.programmingherojobtask.sharedpreference.SharedPreferencesManager
import com.mutsuddi_s.programmingherojobtask.utils.NetworkChecker
import com.mutsuddi_s.programmingherojobtask.utils.Response
import java.io.IOException
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val networkChecker: NetworkChecker,
    private val sharedPreferences: SharedPreferencesManager
) {

    private val TAG = "QuizRepository"

    //to hold quoteList
    private val _questions = MutableLiveData<Response<List<Question>>>()

    //accessable livedata
    val questions: LiveData<Response<List<Question>>>
        get() = _questions


    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _checkNavigation = MutableLiveData<Boolean>().apply { value = false }
    val checkNavigation: LiveData<Boolean> = _checkNavigation

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

   // private val _highestScore = MutableLiveData(0)
    private val _highestScore = MutableLiveData(sharedPreferences.getHighestScore())
    val highestScore: LiveData<Int> = _highestScore


    suspend fun getAllQuestions() {
        _questions.postValue(Response.Loading())
        var isNetwork: Boolean = networkChecker.isNetworkAvailable()
        if (isNetwork) {
            try {
                val result = apiInterface.getAllQues()
                Log.d(TAG, "getAllQuestions: $result}")
                if (result?.body() != null) {

                    _questions.postValue(Response.Success(result.body()!!.questions))


                } else {
                    _questions.postValue(Response.Error("Network Failure"))
                }

            } catch (e: Exception) {
                _questions.postValue(Response.Error(e.message.toString()))
               /* when(e) {
                    is IOException -> _questions.postValue(Response.Error("Network Failure"))
                    else -> _questions.postValue(Response.Error("Conversion Error"))
                }*/

            }
        }
        else {
            _questions.postValue(Response.Error("No internet connection"))
        }

    }


    fun moveToNextQuestion() {
        val current = _currentIndex.value ?: 0
        val questionsList = _questions.value?.data ?: emptyList()

        if (current < questionsList.size - 1) {
            _currentIndex.value = current + 1
        }
        else
        {
            _checkNavigation.value=true
            //_currentIndex.value=0

        }
    }

    fun totalScore(point: Int) {
        //var currentScore=
        _score.value= _score.value?.plus(point)
        if (_score.value!! > _highestScore.value ?: 0) {
            _highestScore.value = _score.value
            saveHighestScore(_score.value)
        }
    }

    private fun saveHighestScore(score: Int?) {
       // sharedPreferences.saveIntValue(score!!)
        sharedPreferences.saveHighestScore(score!!)
    }

    fun setNavigationFalse() {
        _checkNavigation.value=false

    }

    fun setCurrentIndexZero() {
        _currentIndex.value=0
        _score.value=0
    }


}