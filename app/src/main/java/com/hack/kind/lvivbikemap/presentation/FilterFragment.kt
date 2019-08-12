package com.hack.kind.lvivbikemap.presentation

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import com.hack.kind.lvivbikemap.R.layout
import com.hack.kind.lvivbikemap.data.categoryChecked
import com.hack.kind.lvivbikemap.data.putCategoryChecked
import com.hack.kind.lvivbikemap.domain.model.CategoryType
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : Fragment() {

    private lateinit var listener: FiltersSelectedListener
    private val checkBoxToOptionParam: ArrayList<Pair<CheckBox, String>> by lazy {
        arrayListOf<Pair<CheckBox, String>>(bikeRentalCb to CategoryType.rental,
                publicBikeSharingCb to CategoryType.sharing, bikeRepairCb to CategoryType.repair,
                usefulStopsCb to CategoryType.stops, placesOfInterestCb to CategoryType.interests,
                bicyclePathCb to CategoryType.path, bikeParkingCb to CategoryType.parking)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FiltersSelectedListener) {
            listener = context
        } else {
            throw IllegalStateException("Context must be instance of ${FiltersSelectedListener::class.java.simpleName}")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewsState()
        initListeners()
    }

    private fun initViewsState() {
        checkBoxToOptionParam.forEach { it.first.isChecked = categoryChecked(activity!!, it.second) }
    }

    private fun initListeners() {
        checkBoxToOptionParam.forEach {
            it.first.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
                listener.onFiltersSelected(it.second, checked)
                putCategoryChecked(activity!!, it.second, checked)
            }
        }
        btnSubmit.setOnClickListener { activity?.onBackPressed() }
    }

    interface FiltersSelectedListener {
        fun onFiltersSelected(typeId: String, checked: Boolean)
    }

    companion object {
        fun newInstance() = FilterFragment()
    }
}