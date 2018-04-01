package com.hack.kind.lvivbikemap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hack.kind.lvivbikemap.domain.model.CategoryType
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : Fragment() {

    private lateinit var listener: FiltersSelectedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        bikeRentalCb.setOnClickListener { listener.onFiltersSelected(CategoryType.rental) }
        publicBikeSharingCb.setOnClickListener { listener.onFiltersSelected(CategoryType.sharing) }
        bikeRepairCb.setOnClickListener { listener.onFiltersSelected(CategoryType.repair) }
        usefulStopsCb.setOnClickListener { listener.onFiltersSelected(CategoryType.stops) }
        placesOfInterestCb.setOnClickListener { listener.onFiltersSelected(CategoryType.interests) }
        bicyclePathCb.setOnClickListener { listener.onFiltersSelected(CategoryType.path) }
        bikeParkingCb.setOnClickListener { listener.onFiltersSelected(CategoryType.parking) }
    }

    interface FiltersSelectedListener {
        fun onFiltersSelected(typeId: String)
    }

    companion object {
        fun newInstance(listener: FiltersSelectedListener) = FilterFragment().apply {
            this.listener = listener
        }
    }
}