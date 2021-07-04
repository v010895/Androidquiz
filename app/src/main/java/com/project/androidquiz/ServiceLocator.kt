package com.project.androidquiz

import android.content.Context
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.repositories.GithubRepository
import java.util.concurrent.Executors

object ServiceLocator {

    var INSTANCE: GithubRepository? = null
    private val api by lazy{
        GithubService.create()
    }
    private val NETWORK_IO = Executors.newFixedThreadPool(5)
    fun instance(context: Context): GithubRepository {
        return INSTANCE ?: createRepository(context,api)
    }
    private fun createRepository(context:Context,api:GithubService):GithubRepository
    {
        return GithubRepository(api, NETWORK_IO)
    }

}