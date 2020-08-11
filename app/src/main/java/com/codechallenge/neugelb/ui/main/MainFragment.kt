package com.codechallenge.neugelb.ui.main

import android.os.Bundle
import android.view.View
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
    private val searchQueryField = "search_query"
    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startLoadingMovies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun startLoadingMovies() {
        val searchQuery = requireArguments().getString(searchQueryField, "")
        if (searchQuery.isNotEmpty()) {
            searchQuery?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchingFlow(it)?.collectLatest { pagingData ->
                        mainAdapter.submitData(pagingData)
                    }
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadingFlow?.collectLatest { pagingData ->
                    mainAdapter.submitData(pagingData)
                }
            }
        }
    }

}