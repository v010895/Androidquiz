package com.project.androidquiz.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.QueryType
import com.project.androidquiz.repositories.State
import com.project.androidquiz.tools.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FollowerDataSource(
    private val githubApi: GithubService,
    private val user: String,
    private val page: Int,
    private val queryType: QueryType
) : PageKeyedDataSource<Int, Users>() {

    val networkState = MutableLiveData<State>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Users>
    ) {
        val result = when (queryType) {
            QueryType.FOLLOWER -> githubApi.getFollowers(user, params.requestedLoadSize, page)
            QueryType.FOLLOWING -> githubApi.getFollowing(user, params.requestedLoadSize, page)
        }
        networkState.postValue(State.LOADING)

        try {
            val response = result.execute()
            val header = response.headers()["link"].toString()
            val next = parseNext(header)
            val users = response.body() ?: emptyList()
            networkState.postValue(State.SUCCESS)
            callback.onResult(users, 0, next)
        } catch (e: IOException) {
            networkState.postValue(State.error(e.message))
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Users>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Users>) {
        when(queryType) {
            QueryType.FOLLOWER->githubApi.getFollowers(user, params.requestedLoadSize, params.key).enqueue(
                object : Callback<List<Users>> {
                override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                    if (response.isSuccessful) {
                        val header = response.headers()["link"].toString()
                        val next = parseNext(header)
                        val items = response.body() ?: emptyList()
                        callback.onResult(items, next)
                    } else {
                        networkState.postValue(State.error("error code: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                    networkState.postValue(State.error(t.message))
                }

            })
            QueryType.FOLLOWING->githubApi.getFollowing(user,params.requestedLoadSize,params.key).enqueue(
                object:Callback<List<Users>>{
                    override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                        if (response.isSuccessful) {
                            val header = response.headers()["link"].toString()
                            val next = parseNext(header)
                            val items = response.body() ?: emptyList()
                            callback.onResult(items, next)
                        } else {
                            networkState.postValue(State.error("error code: ${response.code()}"))
                        }
                    }

                    override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                        networkState.postValue(State.error(t.message))
                    }

                }
            )
        }
    }

    private fun parseNext(header: String): Int {
        val regx = Regex(".*page=([0-9]+).*")
        val result = regx.find(header)

        return if (result != null)
            result!!.groups[1]!!.value.toInt()
        else
            0
    }

}