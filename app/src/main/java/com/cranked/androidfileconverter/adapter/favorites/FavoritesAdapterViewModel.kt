package com.cranked.androidfileconverter.adapter

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cranked.androidcorelibrary.adapter.BaseViewBindingRecyclerViewAdapter
import com.cranked.androidcorelibrary.viewmodel.BaseViewModel
import com.cranked.androidfileconverter.data.database.entity.FavoriteFile
import com.cranked.androidfileconverter.databinding.RowFavoriteAdapterItemBinding
import com.cranked.androidfileconverter.ui.home.HomeFragmentViewModel
import javax.inject.Inject

class FavoritesAdapterViewModel @Inject constructor() :
    BaseViewModel() {
    fun setAdapter(
        context: Context,
        recyclerView: RecyclerView,
        favoritesAdapter: FavoritesAdapter,
        list: List<FavoriteFile>,
    ): FavoritesAdapter {
        favoritesAdapter.apply {
            setItems(list)
        }
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = favoritesAdapter
        }
        return favoritesAdapter
    }

}