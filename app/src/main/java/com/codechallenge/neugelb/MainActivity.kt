package com.codechallenge.neugelb

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.codechallenge.neugelb.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView =
            menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.isIconifiedByDefault = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) return true
                val navigationController = findNavController(R.id.container)
                val bundle = Bundle()
                bundle.putString("search_query", query)
                navigationController.setGraph(navigationController.graph, bundle)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //TODO if needs to update list on each entered symbol
                return true
            }
        })
        return true
    }
}