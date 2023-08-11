package com.mutsuddi_s.mvvm.Api

import com.mutsuddi_s.mvvm.model.Quiz
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("quiz.json")
    suspend fun getAllQues():Response<Quiz>
}