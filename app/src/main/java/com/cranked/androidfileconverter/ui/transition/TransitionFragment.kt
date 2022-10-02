package com.cranked.androidfileconverter.ui.transition

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cranked.androidcorelibrary.extension.showToast
import com.cranked.androidcorelibrary.ui.base.BaseDaggerFragment
import com.cranked.androidfileconverter.FileConvertApp
import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.adapter.transition.TransitionGridAdapter
import com.cranked.androidfileconverter.adapter.transition.TransitionListAdapter
import com.cranked.androidfileconverter.databinding.FragmentTransitionBinding
import com.cranked.androidfileconverter.utils.Constants
import com.cranked.androidfileconverter.utils.enums.LayoutState
import com.cranked.androidfileconverter.utils.image.BitmapUtils
import com.cranked.androidfileconverter.utils.junk.ToolbarState
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransitionFragment @Inject constructor() :
    BaseDaggerFragment<TransitionFragmentViewModel, FragmentTransitionBinding>(
        TransitionFragmentViewModel::class.java
    ) {
    private val TAG = TransitionFragment::class.toString()
    private val app by lazy {
        requireActivity().application as FileConvertApp
    }
    private val dialog by lazy {
        Dialog(requireContext(), R.style.fullscreenalert)
    }
    private val spinnerList by lazy {
        listOf(
            requireContext().getString(R.string.sorting_a_to_z),
            requireContext().getString(R.string.sorting_z_to_a),
            requireContext().getString(R.string.sorting_newest_items),
            requireContext().getString(R.string.sorting_oldest_items),
        )
    }

    lateinit var transitionListAdapter: TransitionListAdapter
    lateinit var transitionGridAdapter: TransitionGridAdapter

    private lateinit var path: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getViewDataBinding(inflater, container)
        initViewModel(viewModel)
        transitionListAdapter = TransitionListAdapter(viewModel)
        transitionGridAdapter = TransitionGridAdapter(viewModel)
        arguments?.let {
            onBundle(it)
        }
        app.rxBus.send(ToolbarState(false))
        viewModel.init(binding, this, activity!!, app, path, spinnerList)
        viewModel.setCreatefolderAnimationsWithRecyclerView(binding.createFolderButton, binding.transitionRecylerView)
        activity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.backStack(binding.transitionToolbarMenu.backImageView)
                viewModel.sendLongListenerActivated(false)
                viewModel.sendItemsChangedSate(true)
                viewModel.getSelectedRowList().clear()
                setLayoutSate(app.getLayoutState(),
                    viewModel.getFilesFromPath(path, app.getFilterState()))
                binding.createFolderButton.visibility = View.VISIBLE
            }
        })
        return binding.root
    }

    override fun onBundle(bundle: Bundle) {
        path = bundle.getString(Constants.DESTINATION_PATH_ACTION).toString()
        viewModel.sendPath(path)
    }

    override fun getViewDataBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentTransitionBinding {
        return DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_transition,
            parent,
            false
        )
    }


    override fun createLiveData(viewLifecycleOwner: LifecycleOwner) {
        viewModel.getFolderPathMutableLiveData().observe(viewLifecycleOwner)
        {
            val list = viewModel.getFilesFromPath(it!!, app.getFilterState())
            viewModel.sendNoDataState(list.size > 0)
            setLayoutSate(app.getLayoutState(), list)
        }
        viewModel.getNoDataStateMutableLiveData().observe(viewLifecycleOwner) {
            binding.emptyFolder.visibility = if (it) View.GONE else View.VISIBLE
            binding.noDataImageView.visibility = if (it) View.GONE else View.VISIBLE
            binding.emptyFolderDescription.visibility = if (it) View.GONE else View.VISIBLE
        }
        viewModel.getFilterStateMutableLiveData().observe(viewLifecycleOwner) {
            app.setFilterState(it)
            val list = viewModel.getFilesFromPath(path, app.getFilterState())
            transitionGridAdapter.setItems(list)
            transitionListAdapter.setItems(list)
        }
        viewModel.getItemsChangedStateMutableLiveData().observe(viewLifecycleOwner) {
            val list = viewModel.getFilesFromPath(path, app.getFilterState())
            transitionGridAdapter.setItems(list)
            transitionListAdapter.setItems(list)
        }
        viewModel.getLongListenerActivatedMutableLiveData().observe(viewLifecycleOwner) {
            BitmapUtils.setViewVisibility(binding.multipleSelectionMenu.root, it)
            BitmapUtils.setViewVisibility(binding.transitionToolbarMenu.root, !it)
            val list = viewModel.getFilesFromPath(path, app.getFilterState())
            transitionGridAdapter.setItems(list)
            transitionListAdapter.setItems(list)
            binding.createFolderButton.visibility = if (!it) View.VISIBLE else View.GONE
        }
        viewModel.getSelectedRowSizeMutableLiveData().observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.createFolderButton.visibility = View.VISIBLE
                viewModel.sendLongListenerActivated(false)
            } else
                binding.createFolderButton.visibility = View.INVISIBLE
            binding.multipleSelectionMenu.selectedItemsMultiple.text = it.toString()
        }
        viewModel.getErrorMessageMutableLiveData().observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    fun setLayoutSate(state: Int, list: MutableList<TransitionModel>) {
        try {
            when (state) {
                LayoutState.LIST_LAYOUT.value -> {
                    binding.transitionToolbarMenu.layoutImageView.setImageDrawable(context!!.getDrawable(
                        R.drawable.icon_grid))
                    transitionListAdapter =
                        viewModel.setAdapter(context!!, activity!!, activity!!.layoutInflater, binding.transitionRecylerView, dialog,
                            transitionListAdapter, list)
                }
                LayoutState.GRID_LAYOUT.value -> {
                    binding.transitionToolbarMenu.layoutImageView.setImageDrawable(context!!.getDrawable(
                        R.drawable.icon_list))
                    transitionGridAdapter = viewModel.setAdapter(context!!, requireActivity(), requireActivity().layoutInflater,
                        binding.transitionRecylerView, transitionGridAdapter, list)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override fun initViewModel(viewModel: TransitionFragmentViewModel) {
        binding.viewModel = viewModel
    }

    override fun createListeners() {
        binding.transitionToolbarMenu.layoutImageView.setOnClickListener {
            when (app.getLayoutState()) {
                LayoutState.LIST_LAYOUT.value -> {
                    app.setLayoutState(LayoutState.GRID_LAYOUT.value)
                    binding.transitionToolbarMenu.layoutImageView.setImageDrawable(requireContext().getDrawable(
                        R.drawable.icon_list))
                    viewModel.setAdapter(requireContext(),
                        requireActivity(),
                        requireActivity().layoutInflater,
                        binding.transitionRecylerView,
                        transitionGridAdapter,
                        viewModel.getFilesFromPath(path, app.getFilterState()))
                }
                LayoutState.GRID_LAYOUT.value -> {
                    app.setLayoutState(LayoutState.LIST_LAYOUT.value)
                    binding.transitionToolbarMenu.layoutImageView.setImageDrawable(requireContext().getDrawable(
                        R.drawable.icon_grid))
                    viewModel.setAdapter(requireContext(), requireActivity(), requireActivity().layoutInflater,
                        binding.transitionRecylerView,
                        dialog, transitionListAdapter,
                        viewModel.getFilesFromPath(path, app.getFilterState()))
                }
            }
        }
    }
}