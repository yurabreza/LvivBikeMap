package com.hack.kind.lvivbikemap.domain.model

import com.google.gson.annotations.SerializedName

data class Category(@SerializedName("name") val name: String,
        @SerializedName("id") val id: String)


