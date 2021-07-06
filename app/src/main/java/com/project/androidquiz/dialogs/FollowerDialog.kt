package com.project.androidquiz.dialogs

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.project.androidquiz.DetailActivity
import com.project.androidquiz.adapters.FollowerDialogAdapter
import com.project.androidquiz.databinding.DialogFollowersBinding
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.QueryType
import com.project.androidquiz.tools.DebugLog
import com.project.androidquiz.viewmodels.GithubViewModel

class FollowerDialog(activity:AppCompatActivity,title:String,queryArg:Pair<String,QueryType>,githubViewModel: GithubViewModel) {
    private val view = DialogFollowersBinding.inflate(activity.layoutInflater)
    private val dialog:AlertDialog
    init{
        view.apply{
            val adapter = FollowerDialogAdapter(activity){}
            listFollowers.adapter = adapter
            githubViewModel.queryFollow(queryArg)
            githubViewModel.followerUsers.observe(activity as DetailActivity, Observer {
                DebugLog.d("myDebug","follower size ${it.size}")
                adapter.submitList(it)
            })
            dialog = AlertDialog.Builder(activity)
                .setNegativeButton("Cancel",null).create()
            dialog.also{
                it.setTitle(title)
                it.setView(this.root)
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setOnClickListener {
                cancelDialog()
            }
            dialog.show()
        }
    }

    private fun cancelDialog(){
        dialog?.cancel()
    }
}