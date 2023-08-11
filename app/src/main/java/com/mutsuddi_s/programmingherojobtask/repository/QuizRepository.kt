package com.mutsuddi_s.mvvm.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mutsuddi_s.mvvm.Api.ApiInterface
import com.mutsuddi_s.mvvm.model.Question
import com.mutsuddi_s.programmingherojobtask.utils.NetworkChecker
import com.mutsuddi_s.programmingherojobtask.utils.Response
import javax.inject.Inject

class QuizRepository @Inject constructor(private val apiInterface: ApiInterface,private val networkChecker: NetworkChecker) {

    private  val TAG = "QuizRepository"
    //to hold quoteList
    private val _questions= MutableLiveData<Response<List<Question>>>()
    //accessable livedata
    val questions: LiveData<Response<List<Question>>>
        get()=_questions



    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score



    suspend fun getAllQuestions() {
        _questions.postValue(Response.Loading())
        var isNetwork:Boolean=networkChecker.isNetworkAvailable()
        if(isNetwork) {
            try {
                val result=apiInterface.getAllQues()
                Log.d(TAG, "getAllQuestions: $result}")
                if (result?.body() != null) {

                    _questions.postValue(Response.Success(result.body()!!.questions))
                  /*  val quiz = result.body() ?: return

                    val randomizedQuestions = quiz.questions.map { question ->
                        val answerOptions = mutableListOf(
                            question.answers.A,
                            question.answers.B,
                            question.answers.C,
                            question.answers.D
                        ).shuffled()

                        question.copy(randomizedAnswers = answerOptions)
                    }
                    _questions.value = Response.Success(Quiz(randomizedQuestions))*/

                  //  val shuffledQuestions = result.body()!!.questions?.map { it.shuffled() }
                   // Log.d(TAG, "getAllQuestions: $shuffledQuestions}")
                    /*val shuffledQuestions = result.body()!!.questions?.map { question ->
                        question.shuffled()
                    }
                    Log.d(TAG, "getAllQuestions: $shuffledQuestions}")
                   _questions.postValue(Response.Success(shuffledQuestions))*/
                  /*  val shuffledQuestions = result.body()!!.questions?.map { question ->
                        question.copy(answers = question.answers.shuffle())
                    }*/
                    /*val shuffledQuestions = result.body()!!.questions?.map { question ->
                        question.copy(answers = question.answers.shuffled()) // Use shuffled() instead of shuffle()
                    }
                    _questions.postValue(Response.Success(shuffledQuestions))*/
                  /*  val shuffledQuestions = result.body()?.questions?.map { question ->
                        question.copy(answers = question.answers.shuffle())
                    }
                    _questions.postValue(Response.Success(shuffledQuestions))*/
                   /* val shuffledQuestions = result.body()?.questions?.map { it.shuffled() }
                    _questions.postValue(Response.Success(shuffledQuestions))*/
                   /* val originalQuestions = result.body()?.questions ?: emptyList()
                    val shuffledQuestions = originalQuestions.map { question ->
                        val shuffledAnswers = question.shuffled()
                        question.copy(answers = shuffledAnswers)
                    }
                    _questions.postValue(Response.Success(shuffledQuestions))*/

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


   fun moveToNextQuestion() {
       val current = _currentIndex.value ?: 0
       val questionsList = _questions.value?.data ?: emptyList()

       if (current < questionsList.size - 1) {
           _currentIndex.value = current + 1
       }
   }

    fun totalScore(point: Int) {
        _score.value = _score.value?.plus(point)
    }
}