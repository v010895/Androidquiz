package com.project.androidquiz

import android.content.Context
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.database.GithubDatabase
import com.project.androidquiz.repositories.GithubRepository
import java.util.concurrent.Executors

object ServiceLocator {

    var INSTANCE: GithubRepository? = null
    val LOCK = Any()
    private val api by lazy{
        GithubService.create()
    }

    private val NETWORK_IO = Executors.newFixedThreadPool(5)
    fun instance(context: Context): GithubRepository {
        synchronized(LOCK)
        {
            return INSTANCE ?: createRepository(context, api)
        }
    }
    private fun createRepository(context:Context,api:GithubService):GithubRepository
    {
        val db = GithubDatabase.getInstance(context)
        val githubRepository = GithubRepository(api,db, NETWORK_IO)
        INSTANCE = githubRepository
        return githubRepository
    }

}