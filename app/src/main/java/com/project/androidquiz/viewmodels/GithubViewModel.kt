package com.project.androidquiz.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.project.androidquiz.api.GithubService
import com.project.androidquiz.models.Users
import com.project.androidquiz.repositories.GithubRepository
import com.project.androidquiz.repositories.QueryType
import com.project.androidquiz.repositories.State
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class GithubViewModel(private val githubRepository: GithubRepository):ViewModel() {

    private val _listUsers = githubRepository.usersOfGithub(0,30)
    private val _userDetail = MutableLiveData<Users>()
    private val _favoriteUsers = githubRepository.getUsers()
    private val _ioState = MutableLiveData<State>()
    private val queryArg = MutableLiveData<Pair<String,QueryType>>()
    private val _followerUsers = queryArg.map{
        githubRepository.followersOfGithub(it.first,0,30,it.second)
    }
    val listUsers: LiveData<PagedList<Users>>
        get() = _listUsers.usersList
    val userDetail:LiveData<Users>
        get() = _userDetail
    val networkState:LiveData<State>
        get() = _listUsers.State
    val favoriteUsers:LiveData<PagedList<Users>>
        get() = _favoriteUsers
    val ioState:LiveData<State>
        get() = _ioState
    val followerUsers:LiveData<PagedList<Users>>
        get() = _followerUsers.switchMap { it.usersList }


    fun findUserDetail(login:String){
        if(login==null)
            return
        githubRepository.api.userDetail(login).enqueue(object:Callback<Users>{
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                val users = response.body()
                _userDetail.postValue(users)
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                _ioState.postValue(State.error(t.message))
            }


        })
    }
    fun queryFollow(arg:Pair<String,QueryType>)
    {
        queryArg.postValue(arg)
    }
    fun insertUser(user:Users)
    {
        viewModelScope.launch{
            try {
                githubRepository.insertUser(user)
                _ioState.postValue(State.SUCCESS)
            }catch (e:IOException)
            {
                _ioState.postValue(State.error(e.message))
            }
        }
    }
    fun deleteUsers(user:Users)
    {
        viewModelScope.launch{
            try {
                githubRepository.deleteUser(user)
                _ioState.postValue(State.SUCCESS)
            }
            catch (e:IOException)
            {
                _ioState.postValue(State.error(e.message))
            }
        }
    }
}