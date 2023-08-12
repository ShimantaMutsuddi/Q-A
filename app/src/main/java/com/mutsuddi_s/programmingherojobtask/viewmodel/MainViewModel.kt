package com.mutsuddi_s.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutsuddi_s.mvvm.model.Question
import com.mutsuddi_s.mvvm.repository.QuizRepository
import com.mutsuddi_s.programmingherojobtask.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: QuizRepository): ViewModel() {
    private  val TAG = "MainViewModel"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllQuestions()
        }

    }
    val currentIndex: LiveData<Int> = repository.currentIndex
    val score: LiveData<Int> = repository.score
    val checkNavigation: LiveData<Boolean> = repository.checkNavigation
    val  highestScore: LiveData<Int> = repository.highestScore


    fun moveToNextQuestion() {
        repository.moveToNextQuestion()
    }
    fun totalScore(point:Int) {
        repository.totalScore(point)
    }

    fun setNavigationFalse() {
        repository.setNavigationFalse()

    }
    fun setCurrentIndexZero() {
        repository.setCurrentIndexZero()

    }




    val questions: LiveData<Response<List<Question>>>
        get()=repository.questions
}