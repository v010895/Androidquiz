package com.project.androidquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GithubViewModelFactory:ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GithubViewModel::class.java))
            return GithubViewModel() as T
        throw IllegalArgumentException("UnKnow viewModel class")
    }
}