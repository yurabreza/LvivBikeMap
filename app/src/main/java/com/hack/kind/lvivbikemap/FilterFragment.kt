package com.hack.kind.lvivbikemap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FilterFragment :Fragment(){

    private lateinit var listener: FiltersSelectedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter,container,false)
    }

    interface FiltersSelectedListener{
        fun onFiltersSelected()
    }

     companion object {
         fun newInstance(listener: FiltersSelectedListener) = FilterFragment().apply {
             this.listener =listener
         }
     }
}