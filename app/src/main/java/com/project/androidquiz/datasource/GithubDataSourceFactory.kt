package com.project.androidquiz.datasource

import androidx.lifecycle.MutableLiveData
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import java.util.concurrent.Executor
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class GithubDataSourceFactory(
    private val api:GithubService,
    private val id:Int,
): DataSource.Factory<Int, Users>() {
    val sourceLiveData = MutableLiveData<GithubDataSource>()
    override fun create(): DataSource<Int, Users> {
        val source = GithubDataSource(api,id)
        sourceLiveData.postValue(source)
        return source
    }

}