package com.example.safetymanagement2022.ui.connect_building

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetymanagement2022.common.TAG
import com.example.safetymanagement2022.data.remote.model.response.ConnectBuildingResponse
import com.example.safetymanagement2022.data.remote.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectBuildingViewModel @Inject constructor(
    private val repository: HomeRepository
):  ViewModel() {
    private val _buildingList = MutableLiveData<ConnectBuildingResponse>()
    val buildingList: LiveData<ConnectBuildingResponse> = _buildingList

    fun getConnectBuilding(userId: String) = viewModelScope.launch {
        kotlin.runCatching {
            repository.getConnectBuilding(userId)
        }.onSuccess {
            _buildingList.value = it
        }.onFailure {
            Log.d(TAG, "get home connect building api fail ${it.message}")
        }
    }
}