package com.project.androidquiz.models

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.project.androidquiz.repositories.NetworkState

data class Listing<T>(
    val usersList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>
)
