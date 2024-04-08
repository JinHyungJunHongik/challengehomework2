package com.example.challengehomework1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var name: EditText
    private lateinit var id: EditText
    private lateinit var password: EditText
    private lateinit var btnRegister: Button
    private lateinit var errorNameText: TextView
    private lateinit var errorIdText: TextView
    private lateinit var errorPwdText: TextView
    private lateinit var registerData: MutableLiveData<member>
    private lateinit var errorId: MutableLiveData<String>
    private lateinit var errorPwd: MutableLiveData<String>
    private lateinit var checkName: MutableLiveData<Boolean>
    private lateinit var checkId: MutableLiveData<Boolean>
    private lateinit var checkPwd: MutableLiveData<Boolean>
    private lateinit var editTextList: MutableList<EditText>
    private lateinit var buttonActivated: MutableLiveData<Boolean>
    private lateinit var errorPwdFirm : MutableLiveData<String>
    private lateinit var pwdFirm: EditText
    private lateinit var errorPwdFirmText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //각종 초기화 코드 담음
        init()
        editTextSetting(editTextList)
        setFocusSetting(editTextList)
        viewModel.ButtonActivate()
        setButtonActivate()
        //임시로 단일 데이터에 해당 가입 정보 저장 및 registerActivityForResult 사용
        btnRegister.setOnClickListener {
            val Member = member(
                name.text.toString(),
                id.text.toString(),
                password.text.toString()
            )
            viewModel.addData(Member)
            registerMessage()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("id", id.text.toString())
            intent.putExtra("password", password.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
        idCheck()
        passwordCheck()
        passwordSame()
    }


    //뷰모델의 데이터를 뽑아오는 함수
    private fun registerMessage() {
        registerData.observe(this) {
            val name = it.name
            val id = it.id
            val password = it.password
            Toast.makeText(
                this@RegisterActivity,
                "이름 : $name, 아이디 : $id, 비밀번호 : $password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun idCheck() {
        errorId.observe(this) {
            errorIdText.text = it
        }
    }

    private fun passwordCheck() {
        errorPwd.observe(this) {
            errorPwdText.text = it
        }
    }
    private fun setButtonActivate(){
        buttonActivated.observe(this){
            btnRegister.isEnabled = it
        }
    }
    private fun passwordSame(){
        errorPwdFirm.observe(this){
            errorPwdFirmText.text = it
        }
    }
    private fun init() {
        name = findViewById(R.id.edit_name)
        id = findViewById(R.id.edit_ID)
        password = findViewById(R.id.edit_password)
        pwdFirm = findViewById(R.id.edit_passwordFirm)
        btnRegister = findViewById(R.id.btn_register)
        errorNameText = findViewById(R.id.errorText_name)
        errorIdText = findViewById(R.id.errorText_id)
        errorPwdText = findViewById(R.id.errorText_password)
        errorPwdFirmText = findViewById(R.id.errorText_passwordFirm)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        errorIdText.setTextColor(Color.rgb(255, 0, 0))
        errorPwdText.setTextColor(Color.rgb(80,80,80))
        errorNameText.setTextColor(Color.rgb(255, 0, 0))
        errorIdText.text = ""
        errorPwdText.text = "8자리 이상 대문자 포함"
        errorNameText.text = ""
        errorPwdFirmText.text = ""
        btnRegister.isEnabled = false
        registerData = viewModel.registerData
        errorId = viewModel.errorIDText
        errorPwd = viewModel.errorPWDText
        errorPwdFirm = viewModel.errorPwdFirmText
        checkName = viewModel.checkName
        checkId = viewModel.checkId
        checkPwd = viewModel.checkPwd
        editTextList = mutableListOf<EditText>(
            name, id, password, pwdFirm
        )
        buttonActivated = viewModel.isActivateButton
    }

    //EditText 관련
    private fun editTextSetting(list: MutableList<EditText>) {
        for (i in 0..list.size - 1) {
            list[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (list[i] == name) {
                        viewModel.isNameAvailable(list[i].text.toString())
                    } else if (list[i] == id) {
                        viewModel.setErrorIDText(list[i].text.toString())
                    } else if (list[i] == password) {
                            errorPwdText.setTextColor(Color.rgb(255,0,0))
                            viewModel.setErrorPwdText(list[i].text.toString())
                    }
                    else if (list[i] == pwdFirm){
                        viewModel.setPasswordFirmText(password.text.toString(), list[i].text.toString())
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    private fun setFocusSetting(list: MutableList<EditText>) {
        for (i in 0..list.size - 1) {
            list[i].setOnFocusChangeListener(object : OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus) {
                        list[i].setBackgroundResource(R.drawable.edit_blue)
                    } else {
                        Log.d("확인", "${i+1}번째 포커싱 아웃")
                        list[i].setBackgroundResource(R.drawable.edit_black)
                    }
                }
            })
        }
    }
}
