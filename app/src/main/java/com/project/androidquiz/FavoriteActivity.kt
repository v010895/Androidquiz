package com.project.androidquiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.project.androidquiz.adapters.GithubAdapter
import com.project.androidquiz.databinding.ActivityFavoriteBinding
import com.project.androidquiz.interfaces.DbListener
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.State
import com.project.androidquiz.viewmodels.GithubViewModel
import com.project.androidquiz.viewmodels.GithubViewModelFactory

class FavoriteActivity:AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var githubViewModel: GithubViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = GithubViewModelFactory(ServiceLocator.instance(this))
        githubViewModel = ViewModelProvider(this,factory).get(GithubViewModel::class.java)
        initAdapter()
        githubViewModel.ioState.observe(this, Observer {
            when(it)
            {
                State.SUCCESS-> Snackbar.make(binding.root,it.status.toString(), Snackbar.LENGTH_SHORT).show()
                else-> Snackbar.make(binding.root,it.msg!!, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun initAdapter()
    {
        val adapter = GithubAdapter(this,object: DbListener {
            override fun insertUser(user: Users) {
            }

            override fun deleteUser(user: Users) {
                githubViewModel.deleteUsers(user)
            }

        }){
            val name = (it as Users).login
            val intent = Intent(this,DetailActivity::class.java).apply{
                putExtra("name",name)
            }
            startActivity(intent)
        }
        val mDividerItemDecoration = DividerItemDecoration(binding.usersList.context,
            DividerItemDecoration.VERTICAL)
        binding.usersList.addItemDecoration(mDividerItemDecoration)
        binding.usersList.adapter = adapter
        githubViewModel.favoriteUsers.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}