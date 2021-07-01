package com.project.androidquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.androidquiz.databinding.ActivityMainBinding
import com.project.androidquiz.viewmodels.GithubViewModel
import com.project.androidquiz.viewmodels.GithubViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var githubViewModel: GithubViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = GithubViewModelFactory()
        githubViewModel = ViewModelProvider(this,factory).get(GithubViewModel::class.java)

        githubViewModel.users.observe(this, Observer {
            binding.usersTxt.text = it
        })

    }
}