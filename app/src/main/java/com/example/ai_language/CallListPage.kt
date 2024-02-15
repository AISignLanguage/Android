package com.example.ai_language

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//전화번호 데이터 클래스
data class Phone(val id:String?, val name:String?, val phone:String?)

class CallListPage : AppCompatActivity() {

    lateinit var call : Call<PhoneDTO>
    lateinit var service: Service
    lateinit var phoneDTO: PhoneDTO

    private val callViewModel: CallListViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()

    private lateinit var rv_call : RecyclerView
    private lateinit var callListAdapter : CallListAdapter
    private lateinit var rv_invite : RecyclerView
    private lateinit var inviteListAdapter : InviteListAdapter

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

        rv_call = findViewById<RecyclerView>(R.id.rv_call)
        rv_call.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        callListAdapter = CallListAdapter(callViewModel)
        rv_call.adapter = callListAdapter

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
        rv_invite = findViewById<RecyclerView>(R.id.rv_invite)
        inviteListAdapter = InviteListAdapter(inviteViewModel)
        rv_invite.adapter = inviteListAdapter
        rv_invite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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
                    number?.let { phoneNumbers.add(it) } // 전화번호가 null이 아닌 경우 리스트에 추가
                }
            }
            phoneCursor?.close()

            // 해당 연락처의 전화번호들을 DTO로 변환하여 리스트에 추가
            phoneDTO = PhoneDTO(phoneNumbers)

        } //while 종료
        cursor?.close()
    }

    // 서버에서 데이터를 가져오는 함수
    private fun fetchDataFromServer() {
        // Retrofit 인스턴스 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()
        call = service.sendCallData(phoneDTO)

        // 서버로부터 데이터를 가져오는 요청 보내기
        call.enqueue(object : Callback<PhoneDTO> {
            override fun onResponse(call: Call<PhoneDTO>, response: Response<PhoneDTO>) {
                if (response.isSuccessful) {
                    val callListDto = response.body() // 서버에서 받은 데이터
                    Log.d("로그", "onCreate 응답 성공")
                    Log.d("로그", "phoneDTO.phoneNumbers: ${phoneDTO.phoneNumbers}")
                    // 받아온 데이터를 처리
                    // 예: 뷰 모델에 연동하여 UI 업데이트 등 수행
                } else {
                    // 요청 실패 처리
                    Log.d("로그", "데이터 요청 실패. 응답 코드: ${response.code()}, "
                            + "오류 메시지: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PhoneDTO>, t: Throwable) {
                // 통신 실패 처리
                Log.d("로그", "통신 실패: ${t.message}")
            }
        })
    }
}