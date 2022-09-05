package com.example.safetymanagement2022.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class BuildingCreate2Request(
    @SerializedName("building_id") val buildingName: Int,
    @SerializedName("drawing_list") val list: List<String>
)
