package com.hack.kind.lvivbikemap.domain.model

import com.google.gson.annotations.SerializedName

class PropertiesModel(@SerializedName("name") val name: String,
                      @SerializedName("styleUrl") val styleUrl: String,
                      @SerializedName("styleHash") val styleHash: String,
                      @SerializedName("category") val category: Category,
                      @SerializedName("styleMapHash") val styleMapHash: HashMap<String, String>)

//"properties":{
//    "name":"Rental - ProfiRider",
//    "styleUrl":"#icon-503-DB4436-nodesc",
//    "styleHash":"220200e0",
//    "styleMapHash":{
//        "normal":"#icon-503-DB4436-nodesc-normal",
//        "highlight":"#icon-503-DB4436-nodesc-highlight"
//    }
//}