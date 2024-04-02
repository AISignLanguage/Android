package com.example.ai_language.ui.map.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.ai_language.R
import com.example.ai_language.data.source.Map.MapDataSource
import com.example.ai_language.databinding.CorporationDialogBinding
import com.example.ai_language.ui.map.data.MapDialogData
import com.example.ai_language.ui.map.listener.DetailImWriteDialogInterface
import com.example.ai_language.ui.map.listener.DialogDismissListener

class CorporationDialog(
    private val detailImWriteDialogInterface: DetailImWriteDialogInterface,
    private val mapDialogData: MapDialogData
    ) : DialogFragment() {
    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.setLayout(width, height) // 다이얼로그의 너비와 높이 설정
        window?.setGravity(Gravity.BOTTOM) // 필요한 경우 다이얼로그의 위치 조정
    }
    private var _binding: CorporationDialogBinding? = null
    private val binding get() = _binding!!
    //다이얼로그 바인딩
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CorporationDialogBinding.inflate(inflater, container, false)
        setupDialogWindow()
        return binding.root
    }
    private fun setupDialogWindow() {
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.apply {
                // 완전 투명한 배경으로 설정
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                // 배경 투명색
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                // 다이얼로그 테두리 없애기
                setBackgroundDrawableResource(android.R.color.transparent)
                //너비는 match_parent, 높이는 wrap_content
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                //아래 쪽에 부착
                setGravity(Gravity.BOTTOM)
                //서서히 올라오는 스타일 지정
                attributes?.windowAnimations = R.style.DialogAnimation
            }
        }
    }
    //클릭리스너 등록
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBind()
        setupClickListeners()
    }
    private fun setBind() {
        with(binding) {
            dialogData = mapDialogData
            Log.d("값3", "${dialogData?.title}")
            dialogData?.address = mapDialogData.address
            dialogData?.title = mapDialogData.title
            dialogData?.state = mapDialogData.state
        }
    }




    private fun setupClickListeners() {
        binding.btFindRoute.apply {
            text = "길찾기"
            setOnClickListener { onButtonClicked(1) }
        }
        binding.bdFindDetail.apply {
            text = "자세히 보기"
            setOnClickListener { onButtonClicked(2) }
        }
    }

    //버튼 클릭시 이벤트
    private fun onButtonClicked(buttonId: Int) {
        detailImWriteDialogInterface.onClickButton(buttonId)
        dismiss()
    }

    //화면 사라지면 바인딩 제거
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}