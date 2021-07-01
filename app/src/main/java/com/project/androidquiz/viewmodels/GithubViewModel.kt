package com.project.androidquiz.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GithubViewModel:ViewModel() {
    private val api: GithubService by lazy {
        GithubService.create()
    }

    init {
        getlistUsers()
    }

    private val _users = MutableLiveData<String>()
    val users: LiveData<String>
        get() = _users

    private fun getlistUsers() {
        api.listAllUsers(0, 30).enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                _users.value = response.headers()["link"].toString()
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}