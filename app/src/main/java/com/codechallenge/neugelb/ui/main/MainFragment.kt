package com.codechallenge.neugelb.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechallenge.neugelb.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), LifecycleOwner {

    @Inject
    lateinit var mainAdapter: MainAdapter
    private val viewModel by viewModels<MainViewModel>()
    private val searchQueryField = "search_query"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(allMovies_rv) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(context)
        }
        mainAdapter.getNextPresentations {
            getNextMovies()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner,
            Observer<MainViewModelState> { state ->
                when (state) {
                    MainViewModelState.Loading -> showLoading(true)
                    is MainViewModelState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    is MainViewModelState.RequestResult -> {
                        showLoading(false)
                        showNextMovies(state.presentations)
                    }
                }
            })
        startLoadingMovies()
    }

    private fun startLoadingMovies() {
        val searchQuery = requireArguments().getString(searchQueryField, "")
        //probably, it's better to move this 'if' to viewModel, but looks like it's more clear here
        if (searchQuery.isNotEmpty()) {
            if (!searchQuery!!.contentEquals(viewModel.previousSearch)) mainAdapter.cleanPresentationsList()
            if (mainAdapter.itemCount == 0) viewModel.onAction(MainViewModelAction.StartSearching(searchQuery))
        } else {
            if (mainAdapter.itemCount == 0) viewModel.onAction(MainViewModelAction.StartLoading)
        }
    }

    private fun getNextMovies() {
        val searchQuery = requireArguments().getString(searchQueryField, "")
        //probably, it's better to move this 'if' to viewModel, but looks like it's more clear here
        if (searchQuery.isNotEmpty()) {
            viewModel.onAction(MainViewModelAction.StartSearching(searchQuery))
        } else {
            viewModel.onAction(MainViewModelAction.StartLoading)
        }
    }

    private fun showNextMovies(presentations: List<ShortPresentations>) {
        mainAdapter.addPresentations(presentations)
    }

    private fun showLoading(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}