package com.codechallenge.neugelb.ui.main

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
class MainFragment : Fragment(R.layout.main_fragment), LifecycleOwner {

    @Inject
    lateinit var mainAdapter: MainAdapter
    private val viewModel by viewModels<MainViewModel>()
    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        setHasOptionsMenu(true)
        with(allMovies_rv) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true) //TODO ?
        }
        disposables.add(mainAdapter.clickSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                findNavController().navigate(it)
            })
        startLoadingMovies()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        searchView.isIconifiedByDefault = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val action = MainFragmentDirections.actionMainFragmentToSearchFragment(
                        searchQuery = query
                    )
                    findNavController().navigate(action)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingFlow?.collectLatest { pagingData ->
                mainAdapter.submitData(pagingData)
            }
        }
    }

}