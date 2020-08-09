package com.codechallenge.neugelb.ui.main

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.ActionBarContextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechallenge.neugelb.MainActivity
import com.codechallenge.neugelb.R
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), LifecycleOwner {

    @Inject
    lateinit var mainAdapter: MainAdapter
    private val viewModel by viewModels<MainViewModel>()
    private val searchQueryField = "search_query"
    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        with(allMovies_rv) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(context)
        }
        mainAdapter.getNextPresentations {
            getNextMovies()
        }
        disposables.add(mainAdapter.clickSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                findNavController().navigate(it)
            })
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        searchView.isIconifiedByDefault = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    mainAdapter.cleanPresentationsList()
                    MainViewModelAction.StartSearching(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //TODO if needs to update list on each entered symbol
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun startLoadingMovies() {
        val searchQuery = requireArguments().getString(searchQueryField, "")
        //probably, it's better to move this 'if' to viewModel, but looks like it's more clear here
        if (searchQuery.isNotEmpty()) {
            searchQuery?.let {
                if (!it.contentEquals(viewModel.previousSearch)) mainAdapter.cleanPresentationsList()
                if (mainAdapter.itemCount == 0) viewModel.onAction(
                    MainViewModelAction.StartSearching(it)
                )
            }
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
        mainAdapter.submitList(presentations)
    }

    private fun showLoading(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}