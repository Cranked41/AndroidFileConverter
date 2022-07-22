package com.cranked.androidfileconverter.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cranked.androidcorelibrary.utility.FileUtils
import com.cranked.androidcorelibrary.viewmodel.BaseViewModel
import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.data.database.dao.FavoritesDao
import com.cranked.androidfileconverter.data.database.dao.ProcessedFilesDao
import com.cranked.androidfileconverter.utils.Constants
import com.cranked.androidfileconverter.utils.file.FileUtility
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor(
    private val favoritesDao: FavoritesDao,
    processedFilesDao: ProcessedFilesDao,
    private val mContext: Context,
    private val homeFragment: HomeFragment
) :
    BaseViewModel() {
    val sdCardState = FileUtils.isSdCardMounted(mContext)
    val storageModel = FileUtility.getMenuFolderSizes(mContext, processedFilesDao)
    val favoritesList = favoritesDao.getAll().toList()
    val favoritesState = favoritesList.size > 0
    fun goToTransitionFragmentWithIntent(view: View, path: String) {
        val bundle = Bundle()
        bundle.putString(Constants.DESTINATION_PATH_ACTION, path)
        view.findNavController()
            .navigate(R.id.action_home_dest_to_transition_fragment, bundle)
    }

    fun internalStoragePath(view: View) =
        goToTransitionFragmentWithIntent(view, FileUtility.getInternalStoragePath())

    fun sdCardPathFolder(view: View) =
        goToTransitionFragmentWithIntent(view, FileUtility.getSdCarPath(view.context))

    fun downloadsPathFolder(view: View) =
        goToTransitionFragmentWithIntent(view, FileUtility.getDownloadsPath())

    fun fileTransformerPathFolder(view: View) =
        goToTransitionFragmentWithIntent(view, FileUtility.getFileTransformerPath())

    fun processedFolderPath(view: View) =
        goToTransitionFragmentWithIntent(view, FileUtility.getProcessedPath())

    fun notifyFavoriteAdapterItems() {
        homeFragment.favoritesAdapter.setItems(favoritesDao.getAll())
    }
}
