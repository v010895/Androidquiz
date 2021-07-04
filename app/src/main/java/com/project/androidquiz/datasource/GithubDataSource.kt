package com.project.androidquiz.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.NetworkState
import com.project.androidquiz.tools.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class GithubDataSource(
    private val githubApi: GithubService,
    private val id: Int,
) : PageKeyedDataSource<Int, Users>() {

    val networkState = MutableLiveData<NetworkState>()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Users>
    ) {
        val result = githubApi.listAllUsers(
            id,
            params.requestedLoadSize
        )
        networkState.postValue(NetworkState.LOADING)
        try {
            val response = result.execute()
            val header = response.headers()["link"].toString()
            val next = parseNext(header)
            val users = response.body() ?: emptyList()
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(users,0,next)
        } catch (e: IOException) {
            networkState.postValue(NetworkState.error(e.message))
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Users>) {
        DebugLog.d("myDebug","since=${params.key},requestedLoadSize=${params.requestedLoadSize}")
        githubApi.listAllUsers(params.key,params.requestedLoadSize).enqueue(object:Callback<List<Users>>{
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if(response.isSuccessful)
                {
                    val header = response.headers()["link"].toString()
                    val next = parseNext(header)
                    val items = response.body()?: emptyList()
                    callback.onResult(items,next)
                }
                else{
                    networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message))
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Users>) {

    }

    private fun parseNext(header: String): Int {
        val regx = Regex(".*since=([0-9]+)&.*")
        val result = regx.find(header)

        return if (result != null)
            result!!.groups[1]!!.value.toInt()
        else
            0
    }
}