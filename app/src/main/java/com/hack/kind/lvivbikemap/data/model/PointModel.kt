package com.hack.kind.lvivbikemap.data.model

import com.google.gson.annotations.SerializedName

data class PointModel(@SerializedName("id") val id: Int,
                      @SerializedName("name") val name: String)