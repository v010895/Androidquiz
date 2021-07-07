package com.project.androidquiz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.androidquiz.dao.GithubDao
import com.project.androidquiz.models.Users

@Database(entities = [Users::class],version = 1, exportSchema = false)
abstract class GithubDatabase: RoomDatabase() {
    abstract  fun githubDao(): GithubDao

    companion object{
        @Volatile
        private  var instance:GithubDatabase? = null

        fun getInstance(context: Context):GithubDatabase{
            synchronized(this)
            {
                var database = instance
                if(database == null)
                {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        GithubDatabase::class.java,
                        "github_database").build()
                    instance = database
                }
                return database


            }
        }
    }

}