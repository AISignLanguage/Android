package com.example.ai_language.ui.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ai_language.ui.account.change.ChangePw
import com.example.ai_language.domain.model.request.FindPwdDTO
import com.example.ai_language.domain.model.request.FindPwdOk
import com.example.ai_language.R
import com.example.ai_language.Util.RetrofitClient
import com.example.ai_language.data.remote.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordFindFragment : Fragment() {
    private lateinit var call: Call<FindPwdOk>
    private lateinit var service: Service
    private lateinit var findPwdOk: FindPwdOk
    private lateinit var gMailSender: GMailSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) // 키보드가 UI 가리지 않게
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Retrofit Instance 생성
        RetrofitClient.getInstance()
        service = RetrofitClient.getUserRetrofitInterface()

        val certBtn = view.findViewById<Button>(R.id.cert_btn) //전송, 확인 버튼
        certBtn.setOnClickListener {
            fetchDataFromServer()
            certBtn.setBackgroundResource(R.drawable.confrim_num)
        }

    }

    private fun toastMsg(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun fetchDataFromServer() {
        val nameEditText = view?.findViewById<EditText>(R.id.put_name2)
        val emailEditText = view?.findViewById<EditText>(R.id.put_email)
        val name = nameEditText!!.text.toString()
        val email = emailEditText!!.text.toString()

        val findPwdDTO = FindPwdDTO(name, email)
        call = service.findPwd(findPwdDTO)

        call.enqueue(object : Callback<FindPwdOk> {
            override fun onResponse(call: Call<FindPwdOk>, response: Response<FindPwdOk>) {
                if (response.isSuccessful) {
                    findPwdOk = response.body()!!
                    val isOk = findPwdOk.success_find_pwd
                    toastMsg("인증번호가 전송되었습니다.")

                    if (isOk) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // 이메일 보내기
                            gMailSender = GMailSender()
                            gMailSender.sendEmali(email)


                            // 이메일 코드를 가져오기 위해 잠시 대기
                            //delay(3000) // 이메일을 보내는 데에 걸리는 시간에 맞게 조절하십시오

                            // 인증번호 확인 버튼 클릭 시의 동작 설정
                            val find_pwd_btn = view?.findViewById<TextView>(R.id.find_pwd_btn)!!
                            find_pwd_btn.setOnClickListener {
                                Log.d("로그", gMailSender.getEmailCode())
                                val certification_number =
                                    view?.findViewById<TextView>(R.id.certification_number)!!

                                if (gMailSender.emailCheck(certification_number.text.toString())) {
                                    val intent = Intent(requireContext(), ChangePw::class.java)
                                    startActivity(intent)
                                    toastMsg("인증 되었습니다.")
                                    nameEditText.text = null
                                    emailEditText.text = null
                                } else {
                                    toastMsg("잘못된 번호입니다")
                                    certification_number.text = null
                                }
                            }
                        }
                    }
                }

            }

            override fun onFailure(call: Call<FindPwdOk>, t: Throwable) {
                Log.d("로그", "서버 연결 실패 (PasswordFindFragment)")
            }
        })

    }

}