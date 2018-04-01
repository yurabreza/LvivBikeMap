package com.hack.kind.lvivbikemap.domain.model

import com.google.gson.annotations.SerializedName

data class PointModel(@SerializedName("id") val id: Int,
                      @SerializedName("feature") val feature: FeatureModel)