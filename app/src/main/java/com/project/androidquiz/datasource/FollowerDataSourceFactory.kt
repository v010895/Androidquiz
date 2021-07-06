package com.project.androidquiz.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.room.Database
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.QueryType

class FollowerDataSourceFactory(
    private val api:GithubService,
    private val user:String,
    private val page:Int,
    private val queryType: QueryType
):DataSource.Factory<Int, Users>() {

    val sourceLiveData =  MutableLiveData<FollowerDataSource>()
    override fun create(): DataSource<Int, Users> {
        val source = FollowerDataSource(api,user,page,queryType)
        sourceLiveData.postValue(source)
        return source
    }
}
