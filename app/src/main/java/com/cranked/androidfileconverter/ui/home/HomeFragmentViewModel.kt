package com.cranked.androidfileconverter.ui.home

import android.content.Context
import com.cranked.androidcorelibrary.utility.FileUtils
import com.cranked.androidcorelibrary.viewmodel.BaseViewModel
import com.cranked.androidfileconverter.data.database.dao.FavoritesDao
import com.cranked.androidfileconverter.data.database.entity.FavoriteFile
import com.cranked.androidfileconverter.utils.file.FileUtility
import java.io.File
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor(
    private val favoritesDao: FavoritesDao,
    mContext: Context
) :
    BaseViewModel() {
    val sdCardState = FileUtils.isSdCardMounted(mContext)
    val storageModel = FileUtility.getFileSize()
    val favoritesList = favoritesDao.getAll().toList()
    val favoritesState = favoritesList.size > 0

}