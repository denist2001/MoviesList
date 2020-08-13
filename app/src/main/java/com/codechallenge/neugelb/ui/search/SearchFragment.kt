package com.codechallenge.neugelb.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechallenge.neugelb.R
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.main_fragment), LifecycleOwner {

    @Inject
    lateinit var searchAdapter: SearchAdapter
    private val viewModel by viewModels<SearchViewModel>()
    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchQuery = requireArguments().getString("search_query", "")
        view.isFocusableInTouchMode = true
        view.requestFocus()
        with(allMovies_rv) {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true) //TODO ?
        }
        disposables.add(searchAdapter.clickSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                findNavController().navigate(it)
            })
        if (searchQuery.isNotEmpty()) {
            startSearchingMovies(searchQuery)
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        searchView.isIconifiedByDefault = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchQuery = query
                    startSearchingMovies(query)
                } else {
                    viewModel.searchQuery = ""
                    startLoadingMovies()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //TODO if needs to update list on each entered symbol
                return true
            }
        })
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun startSearchingMovies(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchingFlow(query)?.collectLatest { pagingData ->
                searchAdapter.submitData(pagingData)
            }
        }
    }

}