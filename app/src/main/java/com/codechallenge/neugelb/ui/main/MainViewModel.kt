package com.codechallenge.neugelb.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codechallenge.neugelb.data.Response
import com.codechallenge.neugelb.network.Repository
import com.codechallenge.neugelb.utils.ResultConverter
import retrofit2.Call
import retrofit2.Callback

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val converter: ResultConverter
) : ViewModel() {

    val state = MutableLiveData<MainViewModelState>()
    val shortRepresentations: ArrayList<ShortPresentations> = ArrayList()
    var loadingPage: Int = 1
    var searchingPage: Int = 1
    var previousSearch = ""

    fun onAction(action: MainViewModelAction) {
        when (action) {
            MainViewModelAction.StartLoading -> startLoading()
            is MainViewModelAction.StartSearching -> startSearching(action.searchQuery)
        }
    }

    private fun startLoading() {
        searchingPage = 1
        state.postValue(MainViewModelState.Loading)
        repository.loadNextMovies(loadingPage++, dataCallback)
    }

    private fun startSearching(searchQuery: String) {
        loadingPage = 1
        state.postValue(MainViewModelState.Loading)
        repository.searchNextMovies(searchingPage++, searchQuery, dataCallback)
        previousSearch = searchQuery
    }

    val dataCallback = object : Callback<Response> {
        override fun onFailure(call: Call<Response>, t: Throwable) {
            if (!t.message.isNullOrEmpty()) {
                state.postValue(MainViewModelState.Error(t.message!!))
                return
            }
            state.postValue(MainViewModelState.Error("Communication issue"))
        }

        override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
            if (response.isSuccessful && response.body() != null) {
                shortRepresentations.addAll(
                    converter.transform(response.body()?.results))
                state.postValue(MainViewModelState.RequestResult(shortRepresentations))
            } else {
                state.postValue(MainViewModelState.Error("Server side error"))
            }
        }
    }

}

sealed class MainViewModelAction {
    object StartLoading : MainViewModelAction()
    class StartSearching(val searchQuery: String) : MainViewModelAction()
}

sealed class MainViewModelState {
    object Loading : MainViewModelState()
    class Error(val message: String) : MainViewModelState()
    class RequestResult(val presentations: List<ShortPresentations>) : MainViewModelState()
}
