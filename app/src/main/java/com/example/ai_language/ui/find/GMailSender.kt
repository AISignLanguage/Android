package com.example.ai_language.ui.find

import android.util.Log
import com.example.ai_language.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GMailSender : Authenticator() {
    private var host = ""
    private var password = ""
    private val passwordLength = 8 // 기본 길이는 8

    private lateinit var user: String
    private lateinit var emailCode: String
    private lateinit var session: Session

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(host, password)
    }

    private fun createRandom_CertificationNumber(): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') // 알파벳과 숫자 조합
        return (1..passwordLength)
            .map { chars.random() }         //문자 하나씩 생성
            .joinToString("")    //문자열로 합침
    }

    private fun setHost() {
        host = BuildConfig.gmail_sender_id
        password = BuildConfig.gmail_sender_pwd
    }

    fun getEmailCode(): String {
        return emailCode
    }

    fun emailCheck(code: String): Boolean {
        val bool = code == emailCode
        Log.d("로그", "emialCode: ${emailCode} " + bool)
        return code == emailCode
    }

    fun sendEmail(toUser: String) {
        setHost()

        CoroutineScope(Dispatchers.IO).launch {
            user = toUser
            emailCode = createRandom_CertificationNumber()
            val props = Properties()
            props.setProperty("mail.transport.protocol", "smtp")
            props.setProperty("mail.host", "smtp.gmail.com")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.socketFactory.fallback", "false")
            props.setProperty("mail.smtp.quitwait", "false")

            // 구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달
            session = Session.getInstance(props, this@GMailSender)

            val message = MimeMessage(session)
            message.sender = InternetAddress(host)
            message.addRecipient(Message.RecipientType.TO, InternetAddress(toUser))
            message.subject = "이메일 제목"
            message.setText("인증번호는 ${emailCode}입니다.")
            Transport.send(message)
        }
    }

}
