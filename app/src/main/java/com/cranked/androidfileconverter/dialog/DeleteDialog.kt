package com.cranked.androidfileconverter.dialog

import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.data.database.dao.FavoritesDao
import com.cranked.androidfileconverter.databinding.DialogDeleteFilesBinding
import com.cranked.androidfileconverter.ui.transition.TransitionFragmentViewModel
import com.cranked.androidfileconverter.ui.transition.TransitionModel
import com.cranked.androidfileconverter.utils.LogManager
import com.cranked.androidfileconverter.utils.file.FileUtility

class DeleteDialog(
    private val viewModel: TransitionFragmentViewModel,
    private val list: ArrayList<TransitionModel>,
    private val favoritesDao: FavoritesDao,
) :
    BaseDialogFragment<DialogDeleteFilesBinding>(R.layout.dialog_delete_files) {
    private val TAG = DeleteDialog::class.java.name.toString()
    override fun onBindingCreate(binding: DialogDeleteFilesBinding) {
        try {
            binding.deleteContentDescription.text = context!!.getString(R.string.wantToDeleteFile)
            binding.deleteDialogLayout.cancelButton.setOnClickListener {
                dismiss()
            }
            binding.deleteDialogLayout.okButton.setOnClickListener {
                list.forEach {
                    if (FileUtility.deleteFile(it.filePath)) {
                        val favoriteFile =
                            favoritesDao.getFavorite(it.filePath, it.fileName, it.fileType)
                        if (favoriteFile != null) {
                            favoritesDao.delete(favoriteFile)
                        }
                    }
                }
                viewModel.getItemsChangedStateMutableLiveData().postValue(true)
                viewModel.sendLongListenerActivated(false)
                dismiss()
            }
        } catch (e: Exception) {
            LogManager.log(TAG, e.toString())
        }
    }
}