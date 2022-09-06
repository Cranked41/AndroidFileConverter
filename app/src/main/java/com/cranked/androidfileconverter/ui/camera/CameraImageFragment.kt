package com.cranked.androidfileconverter.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cranked.androidcorelibrary.ui.base.BaseDaggerFragment
import com.cranked.androidcorelibrary.utility.FileUtils
import com.cranked.androidfileconverter.FileConvertApp
import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.adapter.photo.PhotoFile
import com.cranked.androidfileconverter.databinding.FragmentCameraImageBinding
import com.cranked.androidfileconverter.utils.Constants
import com.cranked.androidfileconverter.utils.junk.ToolbarState

class CameraImageFragment :
    BaseDaggerFragment<CameraImageFragmentViewModel, FragmentCameraImageBinding>(CameraImageFragmentViewModel::class.java) {
    private val TAG = this::class.java.toString().substringAfterLast(".")
    private val app by lazy {
        requireActivity().application as FileConvertApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = getViewDataBinding(inflater, container)
        initViewModel(viewModel)
        arguments?.let {
            onBundle(it)
        }
        app.rxBus.send(ToolbarState(false))
        return binding.root
    }

    override fun getViewDataBinding(layoutInflater: LayoutInflater, parent: ViewGroup?): FragmentCameraImageBinding {
        return DataBindingUtil.inflate(layoutInflater, R.layout.fragment_camera_image, parent, false)
    }

    override fun initViewModel(viewModel: CameraImageFragmentViewModel) {
        binding.viewModel = viewModel
    }

    override fun onBundle(bundle: Bundle) {
        val photoFile = bundle.get(Constants.IMAGE_CONTENT) as PhotoFile
        viewModel.sendImagePath(photoFile)
    }

    override fun createListeners() {
        viewModel.getImagePathMutableLiveData().observe(viewLifecycleOwner) {
            val imageList =
                FileUtils.getFolderFiles(it.path, 100000, 1).filter { it.isFile and Constants.VALID_TYPES.contains(it.extension) }
            binding.cameraToolbarMenu.camImgFileName.text = it.fileName
            println(imageList.toString())
        }
        binding.cameraToolbarMenu.camImgBackButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_cameraImageFragment_to_camera_dest)
        }
    }
}