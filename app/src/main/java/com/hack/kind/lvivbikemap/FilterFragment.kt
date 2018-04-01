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

    override fun onStart() {
        super.onStart()
        initButtons()
    }

    private fun initButtons() {
        bikeRentalCb.isChecked = rentalChecked
        publicBikeSharingCb.isChecked = bikeSharingChecked
        bikeRepairCb.isChecked = bikeRepairChecked
        usefulStopsCb.isChecked = usefulChecked
        placesOfInterestCb.isChecked = interestChecked
        bicyclePathCb.isChecked = pathChecked
        bikeParkingCb.isChecked = parkingChecked
    }

    private fun initListeners() {
        bikeRentalCb.setOnClickListener { listener.onFiltersSelected(CategoryType.rental, bikeRentalCb.isChecked) }
        publicBikeSharingCb.setOnClickListener { listener.onFiltersSelected(CategoryType.sharing, publicBikeSharingCb.isChecked) }
        bikeRepairCb.setOnClickListener { listener.onFiltersSelected(CategoryType.repair, bikeRepairCb.isChecked) }
        usefulStopsCb.setOnClickListener { listener.onFiltersSelected(CategoryType.stops, usefulStopsCb.isChecked) }
        placesOfInterestCb.setOnClickListener { listener.onFiltersSelected(CategoryType.interests, placesOfInterestCb.isChecked) }
        bicyclePathCb.setOnClickListener { listener.onFiltersSelected(CategoryType.path, bicyclePathCb.isChecked) }
        bikeParkingCb.setOnClickListener {
            listener.onFiltersSelected(CategoryType.parking, bikeParkingCb.isChecked)
            parkingChecked = bikeParkingCb.isChecked
        }
        btnSubmit.setOnClickListener({ activity?.onBackPressed() })
    }

    interface FiltersSelectedListener {
        fun onFiltersSelected(typeId: String, checked: Boolean)
    }

    companion object {
        var rentalChecked = true
        var bikeSharingChecked = true
        var bikeRepairChecked = true
        var usefulChecked = true
        var interestChecked = true
        var pathChecked = true
        var parkingChecked = true

        fun newInstance(listener: FiltersSelectedListener) = FilterFragment().apply {
            this.listener = listener
        }
    }
}