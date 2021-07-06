package com.project.androidquiz.models

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.project.androidquiz.repositories.State

data class Listing<T>(
    val usersList: LiveData<PagedList<T>>,
    val State: LiveData<State>
)
