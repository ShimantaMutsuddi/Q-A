package com.mutsuddi_s.mvvm.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mutsuddi_s.mvvm.Api.ApiInterface
import com.mutsuddi_s.mvvm.model.Question
import com.mutsuddi_s.programmingherojobtask.utils.NetworkChecker
import com.mutsuddi_s.programmingherojobtask.utils.Response
import javax.inject.Inject

class QuizRepository @Inject constructor(private val apiInterface: ApiInterface,private val networkChecker: NetworkChecker) {


    //to hold quoteList
    private val _questions= MutableLiveData<Response<List<Question>>>()
    //accessable livedata
    val questions: LiveData<Response<List<Question>>>
        get()=_questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex



    suspend fun getAllQuestions() {
        var isNetwork:Boolean=networkChecker.isNetworkAvailable()
        if(isNetwork) {
            try {
                val result=apiInterface.getAllQues()
                if (result?.body() != null) {
                    _questions.postValue(Response.Success(result.body()!!.questions))

                }
                else
                {
                    _questions.postValue(Response.Error("API ERROR"))
                }

            }
            catch (e:Exception)
            {
                _questions.postValue(Response.Error(e.message.toString()))

            }

        }

    }

   /* fun moveToNextQuestion() {
        val current = _currentIndex.value ?: 0
        if (current < _questions.   .value?.size ?: 0 - 1) {
            _currentIndex.value = current + 1
        }
    }*/
   fun moveToNextQuestion() {
       val current = _currentIndex.value ?: 0
       val questionsList = _questions.value?.data ?: emptyList()

       if (current < questionsList.size - 1) {
           _currentIndex.value = current + 1
       }
   }
}