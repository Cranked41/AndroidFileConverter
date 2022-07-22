package com.cranked.androidfileconverter.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cranked.androidcorelibrary.ui.base.BaseDaggerFragment
import com.cranked.androidfileconverter.FileConvertApp
import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.adapter.FavoritesAdapter
import com.cranked.androidfileconverter.adapter.FavoritesAdapterViewModel
import com.cranked.androidfileconverter.adapter.recentfile.RecentFileAdapter
import com.cranked.androidfileconverter.adapter.recentfile.RecentFileAdapterViewModel
import com.cranked.androidfileconverter.data.database.dao.FavoritesDao
import com.cranked.androidfileconverter.databinding.FragmentHomeBinding
import com.cranked.androidfileconverter.ui.main.MainViewModel
import com.cranked.androidfileconverter.utils.junk.ToolbarState
import javax.inject.Inject

@SuppressWarnings("unchecked")
class HomeFragment @Inject constructor() :
    BaseDaggerFragment<HomeFragmentViewModel, FragmentHomeBinding>(HomeFragmentViewModel::class.java) {
    @Inject
    lateinit var favoritesAdapterViewModel: FavoritesAdapterViewModel

    @Inject
    lateinit var recentFileAdapterViewModel: RecentFileAdapterViewModel

    @Inject
    lateinit var favoritesDao: FavoritesDao

    val app by lazy {
        activity!!.application as FileConvertApp
    }
    var favoritesAdapter: FavoritesAdapter = FavoritesAdapter(R.layout.row_favorite_adapter_item)
    var recentFileAdapter = RecentFileAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getViewDataBinding(inflater, container)
        initViewModel(viewModel)
        app.rxBus.send(ToolbarState(true))
        app.appComponent.bindHomeFragment(this)
        return binding.root
    }

    override fun getViewDataBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentHomeBinding {
        return DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, parent, false)
    }

    override fun initViewModel(viewModel: HomeFragmentViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoritesList = favoritesDao.getAll()
        favoritesAdapter = favoritesAdapterViewModel.setAdapter(
            this.context!!,
            binding.favoritesRecylerView,
            favoritesAdapter,
            favoritesList
        )
        recentFileAdapter = recentFileAdapterViewModel.setAdapter(
            this.context!!, binding.recentFileRecylerView,
            recentFileAdapter, recentFileAdapterViewModel.recentFileList
        )
    }

}