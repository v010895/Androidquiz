package com.project.androidquiz.dao

import androidx.paging.DataSource
import androidx.room.*
import com.project.androidquiz.models.Users


@Dao
interface GithubDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertUser(user: Users)

   @Query("SELECT * FROM users")
   fun getUsers():DataSource.Factory<Int,Users>

   @Delete
   suspend fun deleteUser(user:Users)
}