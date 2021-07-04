package com.project.androidquiz.repositories

import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.datasource.GithubDataSourceFactory
import com.project.androidquiz.models.Listing
import com.project.androidquiz.models.Users
import java.util.concurrent.Executor

class GithubRepository(val api:GithubService,private val networkExecutor: Executor) {

    fun usersOfGithub(id:Int,pageSize:Int): Listing<Users>
    {
        val sourceFactory = GithubDataSourceFactory(api,id)
        val livePagedList = sourceFactory.toLiveData(
            pageSize=pageSize,
            fetchExecutor = networkExecutor
        )
        return Listing<Users>(
            livePagedList,
            sourceFactory.sourceLiveData.switchMap {
                it.networkState
            }
        )
    }


}