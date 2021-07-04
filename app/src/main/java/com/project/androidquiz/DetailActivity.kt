package com.project.androidquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.androidquiz.databinding.ActivityDetailBinding
import com.project.androidquiz.datasource.GithubDataSourceFactory
import com.project.androidquiz.models.Users
import com.project.androidquiz.viewmodels.GithubViewModel
import com.project.androidquiz.viewmodels.GithubViewModelFactory

class DetailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var githubViewModel: GithubViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = GithubViewModelFactory(ServiceLocator.instance(this))
        val name = intent.getStringExtra("name")?:""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        githubViewModel = ViewModelProvider(this,factory).get(GithubViewModel::class.java)
        githubViewModel.findUserDetail(name)
        githubViewModel.userDetail.observe(this, Observer { userDetail->
            setupDetail(userDetail)
        })

    }
    private fun setupDetail(userDetail: Users)
    {
        Glide.with(this)
            .load(userDetail.avatarUrl)
            .into(binding.avatarImage)
        binding.name.text = userDetail.name
        binding.loginTxt.text = userDetail.login
        binding.blogTxt.text = userDetail.blog
        binding.locationTxt.text = userDetail.location
        binding.bioTxt.text = userDetail.bio
    }
}