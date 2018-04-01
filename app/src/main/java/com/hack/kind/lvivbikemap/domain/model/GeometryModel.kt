package com.hack.kind.lvivbikemap.domain.model

import com.google.gson.annotations.SerializedName

data class GeometryModel(@SerializedName("type") val type: String,
                        @SerializedName("coordinates") val coordinates : List<List<Double>>)

//"geometry":{
//    "type":"Point",
//    "coordinates":[
//    24.0427959,
//    49.8314465,
//    0
//    ]
//},