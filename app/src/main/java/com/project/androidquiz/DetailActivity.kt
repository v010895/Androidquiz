package com.project.androidquiz

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.project.androidquiz.databinding.ActivityDetailBinding
import com.project.androidquiz.datasource.GithubDataSourceFactory
import com.project.androidquiz.dialogs.FollowerDialog
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.QueryType
import com.project.androidquiz.repositories.State
import com.project.androidquiz.tools.DebugLog
import com.project.androidquiz.viewmodels.GithubViewModel
import com.project.androidquiz.viewmodels.GithubViewModelFactory

class DetailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var githubViewModel: GithubViewModel
    private var user:Users? = null
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
        githubViewModel.ioState.observe(this, Observer {
            when(it)
            {
                State.SUCCESS-> Snackbar.make(binding.root,"SAVE " + it.status.toString(), Snackbar.LENGTH_SHORT).show()
                else->Snackbar.make(binding.root,it.msg!!,Snackbar.LENGTH_SHORT).show()
            }

        })
        setupComponent()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.heart->{
                if(user!=null)
                    githubViewModel.insertUser(user!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupComponent(){
        binding.queryFollower.setOnClickListener{
            if(user!=null) {
                DebugLog.d("myDebug","Query user ${user!!.login}")
                FollowerDialog(this, getString(R.string.follower), Pair<String,QueryType>(user!!.login,QueryType.FOLLOWER),githubViewModel)
            }
        }
        binding.queryFollowing.setOnClickListener{
            if(user!=null) {
                DebugLog.d("myDebug","Query user ${user!!.login}")
                FollowerDialog(this, getString(R.string.following), Pair<String,QueryType>(user!!.login,QueryType.FOLLOWING),githubViewModel)
            }
        }
    }
    private fun setupDetail(userDetail: Users)
    {
        user = Users(userDetail.id,userDetail.login,userDetail.avatarUrl,userDetail.siteAdmin,isInDb = true)
        Glide.with(this)
            .load(userDetail.avatarUrl)
            .into(binding.avatarImage)
        binding.name.text = userDetail.name
        binding.loginTxt.text = userDetail.login
        binding.blogTxt.text = userDetail.blog
        binding.locationTxt.text = userDetail.location
        binding.bioTxt.text = userDetail.bio
        binding.sideAdmin.text = "Side_admin: " + userDetail.siteAdmin.toString()
    }
}