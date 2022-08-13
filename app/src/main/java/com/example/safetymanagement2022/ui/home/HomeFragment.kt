package com.example.safetymanagement2022.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.safetymanagement2022.common.*
import com.example.safetymanagement2022.data.remote.model.request.ConnectIotRequest
import com.example.safetymanagement2022.databinding.FragmentHomeBinding
import com.example.safetymanagement2022.ui.basic_dialog.BasicDialog
import com.example.safetymanagement2022.ui.common.MyViewModelFactory
import com.example.safetymanagement2022.ui.connect_building.SelectBuildingDialog
import com.example.safetymanagement2022.ui.connect_smart_glass.SelectSmartGlassDialog

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels { MyViewModelFactory(requireContext()) }
    private lateinit var homeAdapter: HomeAdapter

    private var glassId = -1
    private var glassName = ""
    private var buildingId = -1
    private var buildingName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setLayout("seongmin")
    }

    private fun setLayout(userId: String) {
        viewModel.loadHomeData(userId)
        viewModel.homeData.observe(viewLifecycleOwner) { homeData ->
            binding.home = homeData
            homeAdapter = HomeAdapter(homeData.issueList)
            binding.rvSafetyIssue.adapter = homeAdapter

            // 관리자 모드
            if (homeData.admin == KEY_MANAGER) setSpinner(homeData.buildingList ?: listOf())
            // 사용자 모드
            else setConnectGlassBtnListener(userId)
        }
    }

    private fun setSpinner(list: List<String>) {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (position == 0) homeAdapter.filter.filter("")
                else homeAdapter.filter.filter(list[position])
            }
        }
    }

    private fun setConnectGlassBtnListener(userId: String) {
        binding.btnConnectSamrtglass.setOnClickListener {
            if (viewModel.homeData.value?.isConnected == KEY_ENABLE) disConnectIot(userId)
            else setConnectIot(userId)
        }
    }

    private fun setConnectIot(userId: String) {
        // 스마트 글래스 선택 Dialog
        val glassDialog = SelectSmartGlassDialog(requireContext(), userId)
        // 건물 선택 Dialog
        val buildingDialog = SelectBuildingDialog(requireContext(), userId)

        parentFragmentManager.setFragmentResultListener(KEY_DIALOG_GLASS,
            viewLifecycleOwner) { key, bundle ->
            glassId = bundle.get(KEY_DIALOG_GLASS_ID).toString().toInt()
            glassName = bundle.get(KEY_DIALOG_GLASS_NAME).toString()
            buildingDialog.show(parentFragmentManager, "SelectBuildingDialog")
        }
        parentFragmentManager.setFragmentResultListener(KEY_DIALOG_BUILDING,
            viewLifecycleOwner) { key, bundle ->
            buildingId = bundle.get(KEY_DIALOG_BUILDING_ID).toString().toInt()
            buildingName = bundle.get(KEY_DIALOG_BUILDING_NAME).toString()

            // 확인 Dialog
            val confirmDialog = BasicDialog("연결 확인",
                """
                    스마트 글래스: ‘${glassName}’
                    건물명: ‘${buildingName}’
                    로 연결하시겠습니까?
                    """.trimIndent(),
                "", "연결하기")
            confirmDialog.show(parentFragmentManager, "BasicDialog")
        }
        parentFragmentManager.setFragmentResultListener(KEY_DIALOG_BASIC_BTN2_CLICK,
            viewLifecycleOwner) { key, bundle ->
            val request = ConnectIotRequest(userId, glassId, buildingId)
            postConnectIot(request)
        }

        glassDialog.show(parentFragmentManager, "SelectSmartGlassDialog")
    }

    private fun postConnectIot(body: ConnectIotRequest) {
        viewModel.postConnectIot(body)
        viewModel.connectIotResponse.observe(viewLifecycleOwner) { response ->
            Log.d("mmm home-connect", "${response.connectMessage}")
        }
    }

    private fun disConnectIot(userId: String) {
        // 연결 종료 확인 Dialog
        val confirmDialog = BasicDialog("연결 종료",
            """
               스마트 글래스: ‘${glassName}’
               건물명: ‘${buildingName}’
               연결을 종료하시겠습니까?
               """.trimIndent(),
            "", "연결 종료하기")
        confirmDialog.show(parentFragmentManager, "BasicDialog")
        parentFragmentManager.setFragmentResultListener(KEY_DIALOG_BASIC_BTN2_CLICK,
            viewLifecycleOwner) { key, bundle ->
            viewModel.fetchDisConnectIot(userId)
            viewModel.disConnectIotResponse.observe(viewLifecycleOwner) { response ->
                Log.d("mmm home-disconnect", "${response}")
            }
        }
    }
}