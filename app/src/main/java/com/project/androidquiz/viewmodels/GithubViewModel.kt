package com.project.androidquiz.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.GithubRepository
import com.project.androidquiz.repositories.NetworkState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class GithubViewModel(private val githubRepository: GithubRepository):ViewModel() {

    private val _listUsers = githubRepository.usersOfGithub(0,30)
    private val _userDetail = MutableLiveData<Users>()
    private val _favoriteUsers = githubRepository.getUsers()
    val listUsers: LiveData<PagedList<Users>>
        get() = _listUsers.usersList
    val userDetail:LiveData<Users>
        get() = _userDetail
    val networkState:LiveData<NetworkState>
        get() = _listUsers.networkState
    val favoriteUsers:LiveData<PagedList<Users>>
        get() = _favoriteUsers


    fun findUserDetail(login:String){
        if(login==null)
            return
        githubRepository.api.userDetail(login).enqueue(object:Callback<Users>{
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                val users = response.body()
                _userDetail.postValue(users)
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {

            }


        })
    }
    fun insertUser(user:Users)
    {
        viewModelScope.launch{
            try {
                githubRepository.insertUser(user)
            }catch (e:IOException)
            {

            }
        }
    }
    fun deleteUsers(user:Users)
    {
        viewModelScope.launch{
            try {
                githubRepository.deleteUser(user)
            }
            catch (e:IOException)
            {

            }
        }
    }
}