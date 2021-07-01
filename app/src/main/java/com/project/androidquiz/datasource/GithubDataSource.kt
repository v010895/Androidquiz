package com.project.androidquiz.datasource

import androidx.paging.PageKeyedDataSource
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class GithubDataSource(
    private val githubApi: GithubService,
    private val id: Int,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, Users>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Users>
    ) {
        val result = githubApi.listAllUsers(
            id,
            params.requestedLoadSize
        )
        try {
            val response = result.execute()
            val header = response.headers()["link"].toString()
            val next = parseNext(header)
            val users = response.body() ?: emptyList()
            callback.onResult(users,0,next)
        } catch (e: IOException) {

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Users>) {
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

                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {

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