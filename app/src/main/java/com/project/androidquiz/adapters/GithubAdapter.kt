package com.project.androidquiz.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.androidquiz.R
import com.project.androidquiz.interfaces.DbListener
import com.project.androidquiz.models.Users
import org.w3c.dom.Text

class GithubAdapter(activity:AppCompatActivity,recyclerView: RecyclerView,private val dbListener: DbListener,
                    itemClick:(Any)->Unit):MyRecyclerViewAdapter(activity,recyclerView,itemClick) {


    override fun getActionMenuId(): Int {
        return R.menu.cab_main
    }

    override fun prepareActionMode(menu: Menu) {
            menu.apply{
                findItem(R.id.add_favorite).isVisible = !this@GithubAdapter.getItem(getSelectPos())!!.isInDb
                findItem(R.id.delete_favorite).isVisible = this@GithubAdapter.getItem(getSelectPos())!!.isInDb
            }
    }

    override fun actionItemPressed(id: Int) {
        when(id)
        {
            R.id.add_favorite->{
                val user = getItem(getSelectPos())
                if(user !=null) {
                    val insertUser = Users(user.id,
                        user.login,
                        user.avatarUrl,
                        user.siteAdmin,
                        isInDb = true)
                    dbListener.insertUser(insertUser)
                }
            }
            R.id.delete_favorite->{
                val user = getItem(getSelectPos())
                if(user !=null) {
                    val insertUser = Users(user.id,
                        user.login,
                        user.avatarUrl,
                        user.siteAdmin,
                        isInDb = true)
                    resetSelectedKeys()
                    dbListener.deleteUser(insertUser)
                }
            }

        }
    }

    override fun getSelectableItemCount(): Int {
        return 0
    }

    override fun getIsItemSelectable(position: Int): Boolean {
        return true
    }


    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolder(R.layout.list_user_item,parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position)!!, allowSingleClick = true, allowLongClick = true){ itemView->
            setupView(itemView,getItem(position)!!)
        }
        bindViewHolder(holder)
    }

    private fun setupView(itemView: View, users: Users)
    {
        val frameLayout = itemView.findViewById<FrameLayout>(R.id.item_frame)
        val loginTxt = itemView.findViewById<TextView>(R.id.loginTxt)
        val avatarImage = itemView.findViewById<ImageView>(R.id.avatarImage)
        val sideAdmin = itemView.findViewById<TextView>(R.id.siteAdminTxt)
        val avatarUrl = users.avatarUrl
        loginTxt.text = users.login
        sideAdmin.text = users.siteAdmin.toString()
        Glide.with(activity)
            .load(avatarUrl)
            .into(avatarImage)
        when(users.isSelect){
            true -> frameLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.md_grey_300))
            false -> frameLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.white))
        }
    }
}