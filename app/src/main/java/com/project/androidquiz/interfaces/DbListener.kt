package com.project.androidquiz.interfaces

import com.project.androidquiz.models.Users

interface DbListener {
    fun insertUser(user: Users)

    fun deleteUser(user:Users)
}