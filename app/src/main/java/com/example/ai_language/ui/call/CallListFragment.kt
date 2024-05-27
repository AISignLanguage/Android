package com.example.ai_language.ui.call

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil.getBaseContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.ActivityCallListBinding
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import com.example.ai_language.ui.call.adapter.CallListAdapter
import com.example.ai_language.ui.call.adapter.InviteListAdapter
import com.example.ai_language.ui.call.viewmodel.CallListViewModel
import com.example.ai_language.ui.call.viewmodel.InviteListItem
import com.example.ai_language.ui.call.viewmodel.InviteViewModel
import com.example.ai_language.ui.camera.StreamingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CallListFragment : BaseFragment<ActivityCallListBinding>(R.layout.activity_call_list) {

    private lateinit var phoneNumberDTO: PhoneNumberDTO
    private val contactListMap = mutableListOf<Pair<String, String>>() // 이름, 번호 map

    private val callViewModel by viewModels<CallListViewModel>()
    private val callListAdapter = CallListAdapter()

    private val inviteViewModel: InviteViewModel by viewModels()

    private lateinit var callRecyclerView: RecyclerView
    private lateinit var inviteRecyclerView: RecyclerView
    private lateinit var inviteListAdapter: InviteListAdapter

    companion object {
        val SMS_PERMISSION_CODE = 2 // 권한 요청 코드를 정의해야 함
    }

    private var standardSize_X = 0
    private var standardSize_Y = 0


    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
            .toInt()
    }

    fun getScreenSize(activity: Activity): Point {
        val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            val bounds = windowMetrics.bounds
            Point(
                bounds.width() - insets.left - insets.right,
                bounds.height() - insets.top - insets.bottom
            )
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        return metrics
    }

    fun getStandardSize() {
        val screenSize = getScreenSize(requireActivity())
        standardSize_X = screenSize.x  // 픽셀 단위로 화면 너비를 직접 사용
        standardSize_Y = screenSize.y  // 픽셀 단위로 화면 높이를 직접 사용
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }


    override fun setLayout() {
//        onClickedByNavi()
        getContacts()
        callListRecyclerView()
        inviteRecyclerView()
    }
//
//    private fun onClickedByNavi() {
//        binding.logoIcon.setNavigationOnClickListener {
//            val navController = findNavController()
//            navController.navigate(R.id.action_callListFragment_to_homeFragment)
//        }
//    }

    // callListRecyclerView 초기화 및 생명주기로 서버에서 뷰모델 값 받아오고 그 이후에는 뷰모델로 업데이트 하는 함수
    private fun callListRecyclerView() {
        callRecyclerView = binding.rvCall
        callRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        callRecyclerView.adapter = callListAdapter

        if (callViewModel.callDataList.value.phones.isEmpty()) {
            callViewModel.sendPhoneNumbers(phoneNumberDTO)
            Log.d("로그", "callViewModel 생성")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                callViewModel.callDataList.collectLatest {
                    if (it.phones?.isEmpty() == true) {
                        Log.d("로그", "callDataList is null")
                    } else {
                        callListAdapter.update(it)
                        binding.rvCall.adapter = callListAdapter
                        Log.d("로그", "callViewModel 업데이트 ")
                    }
                }
            }
        }

        callListAdapter.setOnItemClickListener(object : CallListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val phoneListDTO = callViewModel.getItem(position)
                val pn = phoneListDTO?.let { editPhoneNumber(it.phoneNumbers.toString()) }
                Log.d("번호","${pn.toString()}, ${phoneListDTO.name.toString()}")
                val intent = Intent(requireActivity(),StreamingActivity::class.java).apply {
                    putExtra("name",phoneListDTO.name.toString())
                    putExtra("phoneNum",pn.toString())
                }
                startActivity(intent)
            }
        })

    }

    private fun permissionMessage(num: String, name: String) {
//        val name = 이름
        val pushNum = num.replaceFirst("010", "+8210")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendSMS2(
                pushNum,
                "안녕하세요! 손짓의 순간 입니다.\n 링크 : https://shadow-sky-aab.notion.site/0b5e0f1f00494f2f8bca44ecab6233da?pvs=74"
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_CODE // 권한 요청 코드를 정의해야 함
            )
        }
    }

    private fun sendSMS2(phoneNumber: String, message: String) {
        Log.e("번호", phoneNumber)
        val smsUri = Uri.parse("smsto:" + phoneNumber) //phonNumber에는 01012345678과 같은 구성.
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setData(smsUri)
        intent.putExtra("sms_body", message) //해당 값에 전달하고자 하는 문자메시지 전달
        startActivity(intent)
    }

    private fun editPhoneNumber(num: String) = num.replace("-", "")

    private fun inviteRecyclerView() {
        //RecyclerView 초기화 및 어댑터 설정 - 앱 비 사용자
        inviteRecyclerView = binding.rvInvite
        inviteListAdapter = InviteListAdapter(inviteViewModel)
        inviteRecyclerView.adapter = inviteListAdapter
        inviteRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //초대버튼 클릭
        inviteListAdapter.setOnItemClickListener(object : InviteListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val inviteListItem = inviteViewModel.getListItem(position)
                Log.e("번호", "$inviteListItem")
                val pn = inviteListItem?.let { editPhoneNumber(it.callNumber) }
                if (inviteListItem != null) {
                    permissionMessage(pn.toString(), "")
                }
            }
        })

        // CallListViewModel에서 전화번호만 추출 (서버에서 가져와서 CallListViewModel에 저장됨)
        val phoneNumbers = mutableListOf<String>()
        callViewModel.callDataList.value.phones.forEach { phoneList ->
            phoneList.forEach { phoneDTO ->
                // phoneDTO에서 전화번호를 추출하여 리스트에 추가
                phoneDTO.phoneNumbers?.let { phoneNumber ->
                    phoneNumbers.add(phoneNumber)
                    Log.d("로그", "$phoneNumber")
                }
            }
        }

        // CallListViewModel에서 받아온 번호와 일치하지 않는 번호 필터링 (일치하지 않는 이름, 번호만 저장)
        val filteredContactListMap = contactListMap.filter { contact ->
            !phoneNumbers.contains(contact.second) // appPhoneNumbers에 해당 번호가 없는 경우만 필터링
        }

        // filteredContactListMap 모든 항목 반복해서 아이템 만들고 뷰 모델에 추가 (앱 비 사용자)
        for (contact in filteredContactListMap) {
            val inviteListItem = InviteListItem(contact.first, contact.second)
            inviteViewModel.addListItem(inviteListItem)
        }

        //뷰 모델 observe
        inviteViewModel.inviteDataList.observe(this, Observer { inviteDataList ->
            inviteListAdapter.notifyDataSetChanged()
        })
    }

    // 주소록 앱에서 전화번호 추출
    // 1. 이름과 전화번호 Pair 묶음 생성 -> 앱 비 사용자 분리하기 위함 (invieteRecylcerView)
    // 2. 전화번호 리스트 생성해서 서버에 보낼 DTO 생성 -> 서버에 보내기 위함
    private fun getContacts() {
        // 연락처 전체 정보에 대한 쿼리 수행
        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        val phoneNumbers = mutableListOf<String>()

        while (cursor?.moveToNext() == true) {
            val idColumnIndex =
                cursor.getColumnIndex(ContactsContract.Contacts._ID) //동명이인 때문에 ID 필요
            val id: String? = if (idColumnIndex != -1) cursor.getString(idColumnIndex) else null

            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val name: String? =
                if (nameColumnIndex != -1) cursor.getString(nameColumnIndex) else null

            val phoneCursor: Cursor? = requireActivity().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, // id 기반으로 전화번호
                null, null
            )

            if (phoneCursor?.moveToFirst() == true) {   // 동명이인 때문에 고유 id로 검색
                val numberColumnIndex =
                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numberColumnIndex != -1) {
                    val number: String? =
                        if (numberColumnIndex != -1) phoneCursor.getString(numberColumnIndex) else null
                    var cleanedNumber: String? = null

                    // "+"와 "82"로 시작하는 경우에는 국가 코드를 제거하고 0을 추가하여 번호 정리
                    if (number != null && (number.startsWith("+82") || number.startsWith("82"))) {
                        cleanedNumber = "0${number.substring(3).replace("-", "")}"
                    } else {
                        // "-"를 제거하고 번호 정리
                        cleanedNumber = number?.replace("-", "")?.replace(Regex("[^0-9]"), "")
                    }
                    // 번호가 11자리인 경우만 처리
                    if (cleanedNumber?.length == 11) {
                        // 번호가 010으로 시작하는 경우와 그렇지 않은 경우에 대해 다르게 처리
                        val formattedNumber = if (cleanedNumber.startsWith("010")) {
                            cleanedNumber.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
                        } else {
                            null // 010으로 시작하지 않는 경우에는 null 반환하여 처리하지 않음
                        }
                        // 번호가 010으로 시작하는 경우에만 처리 결과를 저장
                        formattedNumber?.let {
                            phoneNumbers.add(it) // 전화번호가 null이 아닌 경우 리스트에 추가
                            if (name != null) {
                                contactListMap.add(Pair(name, it)) // 이름과 번호를 함께 Pair로 묶어 리스트에 추가
                            }
                        }
                    }
                }
            }
            phoneCursor?.close()

            // 해당 연락처의 전화번호들을 DTO로 변환하여 리스트에 추가
            phoneNumberDTO = PhoneNumberDTO(phoneNumbers)

        } //while 종료
        cursor?.close()
    }

}