package com.project.androidquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.project.androidquiz.adapters.GithubAdapter
import com.project.androidquiz.databinding.ActivityMainBinding
import com.project.androidquiz.interfaces.DbListener
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.State
import com.project.androidquiz.viewmodels.GithubViewModel
import com.project.androidquiz.viewmodels.GithubViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var githubViewModel: GithubViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = GithubViewModelFactory(ServiceLocator.instance(this))
        githubViewModel = ViewModelProvider(this,factory).get(GithubViewModel::class.java)
        githubViewModel.networkState.observe(this, Observer {
            when(it){
                State.SUCCESS->Snackbar.make(binding.root,it.status.toString(),Snackbar.LENGTH_SHORT).show()
                State.LOADING->Snackbar.make(binding.root,it.status.toString(),Snackbar.LENGTH_SHORT).show()
                else->Snackbar.make(binding.root,it.msg?:"Unknown error",Snackbar.LENGTH_SHORT).show()
            }

        })
        initAdapter()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.to_favorite->{
                val intent = Intent(this,FavoriteActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun initAdapter(){
        val adapter = GithubAdapter(this,object:DbListener{
            override fun insertUser(user: Users) {
                githubViewModel.insertUser(user)
            }

            override fun deleteUser(user: Users) {

            }

        }){
            val name = (it as Users).login
            val intent = Intent(this,DetailActivity::class.java).apply{
                putExtra("name",name)
            }
            startActivity(intent)
        }
        val mDividerItemDecoration = DividerItemDecoration(binding.usersList.context,DividerItemDecoration.VERTICAL)
        binding.usersList.addItemDecoration(mDividerItemDecoration)
        binding.usersList.adapter = adapter
        githubViewModel.listUsers.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}