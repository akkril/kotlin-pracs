package com.example.prac5.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.prac5.model.TodoModel
import com.example.prac5.model.TodosModel

interface TodosRetrofitApi{
    @GET("/todos/{id}")
    suspend fun getTodoById(@Path("id") id: String): Response<TodoModel>

    @GET("/todos?limit=0")
    suspend fun getAllTodos(): Response<TodosModel>
}