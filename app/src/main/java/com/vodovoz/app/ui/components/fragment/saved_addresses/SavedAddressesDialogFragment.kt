package com.vodovoz.app.ui.components.fragment.saved_addresses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentAdressesSavedBinding
import com.vodovoz.app.ui.components.adapter.AddressesSavedAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.AddressDiffUtilCallback
import com.vodovoz.app.ui.components.diffUtils.SimpleAddressDiffUtilCallback
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class SavedAddressesDialogFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: DialogFragmentAdressesSavedBinding
    private lateinit var viewModel: SavedAddressesViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onEditAddressClickSubject: PublishSubject<AddressUI> = PublishSubject.create()
    private val onDeleteAddressClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val officeAddressesSavedAdapter = AddressesSavedAdapter(
        onEditAddressClickSubject = onEditAddressClickSubject,
        onDeleteAddressClickSubject = onDeleteAddressClickSubject
    )
    private val personalAddressesSavedAdapter = AddressesSavedAdapter(
        onEditAddressClickSubject = onEditAddressClickSubject,
        onDeleteAddressClickSubject = onDeleteAddressClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SavedAddressesViewModel::class.java]
        viewModel.updateData()
    }

    private fun subscribeSubjects() {
        onEditAddressClickSubject.subscribeBy { addressUI ->
            findNavController().navigate(SavedAddressesDialogFragmentDirections.actionToMapDialogFragment().apply {
                this.address = addressUI
            })
        }.addTo(compositeDisposable)

        onDeleteAddressClickSubject.subscribeBy { addressId ->
            showDeleteAddressDialog(addressId)
        }.addTo(compositeDisposable)
    }

    private fun showDeleteAddressDialog(addressId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Удалить адрес?")
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                viewModel.deleteAddress(addressId)
                dialog.cancel()
            }
            .show()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = DialogFragmentAdressesSavedBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() { viewModel.updateData() }

    override fun initView() {
        initActionBar()
        initAddAddressButton()
        initAddressesRecyclers()
        observeViewModel()
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initAddAddressButton() {
        binding.addAddress.setOnClickListener {
            findNavController().navigate(SavedAddressesDialogFragmentDirections.actionToMapDialogFragment())
        }
    }

    private fun initAddressesRecyclers() {
        binding.scrollContainer.setScrollElevation(binding.appbar)

        binding.officeAddressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.officeAddressesRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.officeAddressesRecycler.adapter = officeAddressesSavedAdapter

        binding.personalAddressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.personalAddressesRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.personalAddressesRecycler.adapter = personalAddressesSavedAdapter
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Success -> onStateSuccess()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
            }
        }

        viewModel.officeAddressUIListLD.observe(viewLifecycleOwner) { addressUIList ->
            if (addressUIList.isEmpty())  binding.officeAddressesTitle.visibility = View.GONE
            else  binding.officeAddressesTitle.visibility = View.VISIBLE

            val diffUtil = AddressDiffUtilCallback(
                oldList = officeAddressesSavedAdapter.addressUIList,
                newList = addressUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                officeAddressesSavedAdapter.addressUIList = addressUIList
                diffResult.dispatchUpdatesTo(officeAddressesSavedAdapter)
            }
        }

        viewModel.personalAddressUIListLD.observe(viewLifecycleOwner) { addressUIList ->
            if (addressUIList.isEmpty())  binding.personaAddressesTitle.visibility = View.GONE
            else  binding.personaAddressesTitle.visibility = View.VISIBLE

            val diffUtil = AddressDiffUtilCallback(
                oldList = personalAddressesSavedAdapter.addressUIList,
                newList = addressUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                personalAddressesSavedAdapter.addressUIList = addressUIList
                diffResult.dispatchUpdatesTo(personalAddressesSavedAdapter)
            }
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}