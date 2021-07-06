package com.project.androidquiz.api

import com.project.androidquiz.models.Users
import com.project.androidquiz.tools.DebugLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {

    @GET("users")
    fun listAllUsers(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int
    ): Call<List<Users>>
    @GET("users/{user}")
    fun userDetail(
        @Path("user") user:String,
    ):Call<Users>

    @GET("users/{user}/followers")
    fun getFollowers(
        @Path("user") user:String,
        @Query("per_page") perPage:Int,
        @Query("page") page:Int
    ):Call<List<Users>>
    @GET("users/{user}/following")
    fun getFollowing(
        @Path("user") user:String,
        @Query("per_page") perPage:Int,
        @Query("page") page:Int
    ):Call<List<Users>>
    companion object {
        private const val BASE_URL = "https://api.github.com"
        fun create(): GithubService {
            val logger = HttpLoggingInterceptor {
                DebugLog.d("GithubService", it)

            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
        }
    }

}
