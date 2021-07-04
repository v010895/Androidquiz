package com.project.androidquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.androidquiz.repositories.GithubRepository

class GithubViewModelFactory(private val githubRepository: GithubRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GithubViewModel::class.java))
            return GithubViewModel(githubRepository) as T
        throw IllegalArgumentException("UnKnow viewModel class")
    }
}