package com.example.ai_language

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//전화번호 데이터 클래스
data class Phone(val id:String?, val name:String?, val phone:String?)

class CallListPage : AppCompatActivity() {

    lateinit var call : Call<PhoneListDTO>
    lateinit var service: Service
    lateinit var phoneNumberDTO: PhoneNumberDTO
    lateinit var appPhoneNumbers: List<String> //서버에서 가져온 번호만 저장하는 리스트
    val contactListMap = mutableListOf<Pair<String, String>>() // 이름, 번호 map

    private val callViewModel: CallListViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()

    private lateinit var callRecyclerView: RecyclerView
    private lateinit var callListAdapter : CallListAdapter
    private lateinit var inviteRecyclerView : RecyclerView
    private lateinit var inviteListAdapter : InviteListAdapter
    var density = 0.0f
    var standardSize_X = 0
    var standardSize_Y = 0

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }

    fun getScreenSize(activity: Activity): Point {
        val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            val bounds = windowMetrics.bounds
            Point(bounds.width() - insets.left - insets.right, bounds.height() - insets.top - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        return metrics
    }
    fun getStandardSize() {
        val screenSize = getScreenSize(this)
        standardSize_X = screenSize.x  // 픽셀 단위로 화면 너비를 직접 사용
        standardSize_Y = screenSize.y  // 픽셀 단위로 화면 높이를 직접 사용
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_list)

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
        }

        callListRecyclerView()
        inviteRecyclerView()
        getContacts()
        fetchDataFromServer() //서버에서 데이터 갱신
    }

    private fun callListRecyclerView() {
        //val call = service.getCallData(uri, installCheck)

        callRecyclerView = findViewById<RecyclerView>(R.id.rv_call)
        callRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        callListAdapter = CallListAdapter(callViewModel)
        callRecyclerView.adapter = callListAdapter

        callListAdapter.setOnItemClickListener (object : CallListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "전화하기", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, CallActivity::class.java)
                startActivity(intent)
            }
        })

        //뷰 모델 observe
        callViewModel.callDataList.observe(this, Observer { callDataList ->
            callListAdapter.notifyDataSetChanged()
        })
    }

    private fun inviteRecyclerView() {
        //RecyclerView 초기화 및 어댑터 설정 - 앱 비 사용자
        inviteRecyclerView = findViewById<RecyclerView>(R.id.rv_invite)
        inviteListAdapter = InviteListAdapter(inviteViewModel)
        inviteRecyclerView.adapter = inviteListAdapter
        inviteRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //초대버튼 클릭
        inviteListAdapter.setOnItemClickListener(object : InviteListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "초대하기", Toast.LENGTH_SHORT).show()
            }
        })

        //뷰 모델 observe
        inviteViewModel.inviteDataList.observe(this, Observer { inviteDataList ->
            inviteListAdapter.notifyDataSetChanged()
        })
    }


    private fun ChangePhoneNumber(number: String?): Boolean {
        // 유효한 전화번호 패턴 확인 (010-1234-5678)
        val regex = Regex("^010-\\d{3,4}-\\d{4}$")
        return !number.isNullOrEmpty() && regex.matches(number)
    }

    private fun getContacts() {
        // 연락처 전체 정보에 대한 쿼리 수행
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null)

        val phoneNumbers = mutableListOf<String>()

        while (cursor?.moveToNext() == true) {
            val idColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID) //동명이인 때문에 ID 필요
            val id: String? = if (idColumnIndex != -1) cursor.getString(idColumnIndex) else null

            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val name: String? = if (nameColumnIndex != -1) cursor.getString(nameColumnIndex) else null

            val phoneCursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, // id 기반으로 전화번호
                null, null)

            if (phoneCursor?.moveToFirst() == true) {   // 동명이인 때문에 고유 id로 검색
                val numberColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numberColumnIndex != -1) {
                    val number: String? = if (numberColumnIndex != -1) phoneCursor.getString(numberColumnIndex) else null
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
            //Log.d("로그", "phoneNumbers : ${phoneNumbers}")

        } //while 종료
        cursor?.close()
    }

    // 서버에서 데이터를 가져오는 함수
    private fun fetchDataFromServer() {
        // Retrofit 인스턴스 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()
        call = service.sendCallData(phoneNumberDTO)

        // 서버로부터 데이터를 가져오는 요청 보내기
        call.enqueue(object : Callback<PhoneListDTO> {
            override fun onResponse(call: Call<PhoneListDTO>, response: Response<PhoneListDTO>) {
                if (response.isSuccessful) {
                    val PhoneListDTO = response.body() // 서버에서 받은 데이터
                    PhoneListDTO?.phones?.let { phones ->
                        for (phoneDTOList in phones) {
                            for (phoneDTO in phoneDTOList) {
                                Log.d("로그", "${phoneDTO.name}, ${phoneDTO.phoneNumbers}, Url: ${phoneDTO.profileImageUrl}")
                                val callListItem = CallListItem(phoneDTO.name, phoneDTO.phoneNumbers, phoneDTO.profileImageUrl)
                                callViewModel.addListItem(callListItem) // 뷰 모델에 서버에서 가져온 데이터 추가
                            }
                            // 중첩된 리스트에 대해 이중 반복문을 사용하여 phoneNumber 추출
                            appPhoneNumbers = phones
                                .flatMap { it } // 중첩된 리스트를 하나의 리스트로 평탄화
                                .mapNotNull { it.phoneNumbers } // 각 PhoneDTO에서 phoneNumber 추출하고 null 필터링
                            Log.d("로그", "appPhoneNumbers : ${appPhoneNumbers}")
                        }
                    }

                    // 서버에서 받은 번호와 일치하지 않는 번호만 남겨두기 위한 필터링
                    val filteredContactListMap = contactListMap.filter { contact ->
                        !appPhoneNumbers.contains(contact.second) // appPhoneNumbers에 해당 번호가 없는 경우만 필터링
                    }
                    contactListMap.clear() // 기존의 데이터를 모두 지움
                    contactListMap.addAll(filteredContactListMap) // 필터링된 데이터만 추가

                    //filteredContactListMap의 모든 항목 반복해서 아이템 만들고 뷰 모델에 추가
                    for (contact in filteredContactListMap) {
                        val inviteListItem = InviteListItem(contact.first, contact.second)
                        inviteViewModel.addListItem(inviteListItem)
                    }

                } else {
                    // 요청 실패 처리
                    Log.d("로그", "데이터 요청 실패. 응답 코드: ${response.code()}, "
                            + "오류 메시지: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PhoneListDTO>, t: Throwable) {
                // 통신 실패 처리
                Log.d("로그", "통신 실패: ${t.message}")
            }
        })
    }
}