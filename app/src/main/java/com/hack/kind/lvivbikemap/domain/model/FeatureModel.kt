package com.hack.kind.lvivbikemap.domain.model

import com.google.gson.annotations.SerializedName

data class FeatureModel(@SerializedName("type") val type: String,
                        @SerializedName("geometry") val geometry : GeometryModel,
                        @SerializedName("properties") val properties: PropertiesModel)

//"type":"Feature",
//"geometry":{
//    "type":"Point",
//    "coordinates":[
//    24.0427959,
//    49.8314465,
//    0
//    ]
//},
//"properties":{
//    "name":"Rental - ProfiRider",
//    "styleUrl":"#icon-503-DB4436-nodesc",
//    "styleHash":"220200e0",
//    "styleMapHash":{
//        "normal":"#icon-503-DB4436-nodesc-normal",
//        "highlight":"#icon-503-DB4436-nodesc-highlight"
//    }
//}
//},