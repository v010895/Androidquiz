package com.project.androidquiz.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.database.GithubDatabase
import com.project.androidquiz.datasource.FollowerDataSourceFactory
import com.project.androidquiz.datasource.GithubDataSourceFactory
import com.project.androidquiz.models.Listing
import com.project.androidquiz.models.Users
import java.util.concurrent.Executor

class GithubRepository(val api:GithubService,val db:GithubDatabase,private val networkExecutor: Executor) {

    fun followersOfGithub(user:String,page:Int,pageSize:Int,queryType:QueryType): Listing<Users>
    {
        val sourceFactory = FollowerDataSourceFactory(api,user,page,queryType)
        val livePagedList = sourceFactory.toLiveData(
            pageSize=pageSize,
            fetchExecutor = networkExecutor
        )
        return Listing(
            livePagedList,
            sourceFactory.sourceLiveData.switchMap {
                it.networkState
            }
        )
    }

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

    suspend fun insertUser(user:Users){
        db.githubDao().insertUser(user)
    }
    fun getUsers(): LiveData<PagedList<Users>>{
        return db.githubDao().getUsers().toLiveData(30)
    }
    suspend fun deleteUser(user:Users){
        db.githubDao().deleteUser(user)
    }


}