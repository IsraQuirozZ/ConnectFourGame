package com.israquirozz.connectfourgame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
import android.view.LayoutInflater

class CreatePlayerActivity : AppCompatActivity() {

    // Username
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var etUsername: TextInputEditText

    // Password
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var etPassword: TextInputEditText

    // Repeat Password
    private lateinit var repeatPasswordLayout: TextInputLayout
    private lateinit var etRepeatPassword: TextInputEditText

    // Button
    private lateinit var btnCreate: Button

    // Username rules
    private val USER_MIN_LEN = 3
    private val USER_MAX_LEN = 10

    // Regex corregido (triple quotes → sí interpolan correctamente!)
    private val usernameRegex = Regex(
        """^(?=.{${USER_MIN_LEN},${USER_MAX_LEN}}$)(?=(?:.*\d){0,2}$)[A-Za-z][A-Za-z0-9_]*$"""
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_player)

        // FIND VIEWS
        usernameLayout = findViewById(R.id.input_username_layout)
        etUsername = findViewById(R.id.input_username)

        passwordLayout = findViewById(R.id.input_password_layout)
        etPassword = findViewById(R.id.input_password)

        repeatPasswordLayout = findViewById(R.id.input_repeat_password_layout)
        etRepeatPassword = findViewById(R.id.input_repeat_password)

        btnCreate = findViewById(R.id.btn_create_player_form)

        //---------------------------------------------------------
        // VALIDACIÓN AL PERDER FOCO
        //---------------------------------------------------------

        etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateUsernameAndShow()
            else clearUsernameError()
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePasswordAndShow()
            else clearPasswordError()
        }

        etRepeatPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateRepeatPasswordAndShow()
            else clearRepeatPasswordError()
        }

        //---------------------------------------------------------
        // VALIDAR
        //---------------------------------------------------------

        btnCreate.setOnClickListener {
            val okUser = validateUsernameAndShow()
            val okPass = validatePasswordAndShow()
            val okRepeat = validateRepeatPasswordAndShow()

            if (okUser && okPass && okRepeat) {
                val username = etUsername.text.toString().trim()

                val data = Intent().apply {
                    putExtra("EXTRA_USERNAME", username)
                }

                setResult(Activity.RESULT_OK, data)

                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val textView = layout.findViewById<TextView>(R.id.toast_text)
                textView.text = "Usuario creado: $username"

                val toast = Toast(this)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()

                finish()
            } else {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val textView = layout.findViewById<TextView>(R.id.toast_text)
                textView.text = "Corrige los errores antes de continuar"

                val toast = Toast(this)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()
            }
        }

    }

    //---------------------------------------------------------
    // USERNAME VALIDATION
    //---------------------------------------------------------

    private fun validateUsernameAndShow(): Boolean {
        val username = etUsername.text?.toString()?.trim() ?: ""

        if (username.isEmpty()) {
            showUsernameError("El nombre de usuario no puede estar vacío")
            return false
        }

        // Longitud
        if (username.length < USER_MIN_LEN) {
            showUsernameError("Mínimo $USER_MIN_LEN caracteres")
            return false
        }
        if (username.length > USER_MAX_LEN) {
            showUsernameError("Máximo $USER_MAX_LEN caracteres")
            return false
        }

        // Primer carácter debe ser letra
        if (!username[0].isLetter()) {
            showUsernameError("Debe comenzar con una letra")
            return false
        }

        // Solo letras, números o "_"
        if (!username.all { it.isLetterOrDigit() || it == '_' }) {
            showUsernameError("Solo se permiten letras, números y '_'")
            return false
        }

        // Máximo 2 dígitos
        val digits = username.count { it.isDigit() }
        if (digits > 2) {
            showUsernameError("Máximo 2 números permitidos")
            return false
        }

        // Pasa todo
        clearUsernameError()
        return true
    }


    private fun showUsernameError(msg: String) {
        usernameLayout.error = msg
        usernameLayout.isErrorEnabled = true
    }

    private fun clearUsernameError() {
        usernameLayout.error = null
        usernameLayout.isErrorEnabled = false
    }

    //---------------------------------------------------------
    // PASSWORD VALIDATION
    //---------------------------------------------------------

    private fun validatePasswordAndShow(): Boolean {
        val password = etPassword.text?.toString() ?: ""

        if (password.isBlank()) {
            showPasswordError("La contraseña no puede estar vacía")
            return false
        }

        if (password.length < 6) {
            showPasswordError("Debe tener al menos 6 caracteres")
            return false
        }

        if (!password.any { it.isLetter() }) {
            showPasswordError("Debe incluir al menos una letra")
            return false
        }

        if (!password.any { it.isDigit() }) {
            showPasswordError("Debe incluir al menos un número")
            return false
        }

        // Al menos un caracter especial
        val specialRegex = Regex("[^A-Za-z0-9 ]") // cualquier símbolo que NO sea letra, número o espacio
        if (!specialRegex.containsMatchIn(password)) {
            showPasswordError("Debe incluir al menos un carácter especial")
            return false
        }

        if (password.contains(" ")) {
            showPasswordError("No puede contener espacios")
            return false
        }

        clearPasswordError()
        return true
    }

    private fun showPasswordError(msg: String) {
        passwordLayout.error = msg
        passwordLayout.isErrorEnabled = true
    }

    private fun clearPasswordError() {
        passwordLayout.error = null
        passwordLayout.isErrorEnabled = false
    }

    //---------------------------------------------------------
    // REPEAT PASSWORD VALIDATION
    //---------------------------------------------------------

    private fun validateRepeatPasswordAndShow(): Boolean {
        val pass = etPassword.text?.toString() ?: ""
        val repeat = etRepeatPassword.text?.toString() ?: ""

        if (repeat.isBlank()) {
            showRepeatPasswordError("Repite la contraseña")
            return false
        }

        if (repeat != pass) {
            showRepeatPasswordError("Las contraseñas no coinciden")
            return false
        }

        clearRepeatPasswordError()
        return true
    }

    private fun showRepeatPasswordError(msg: String) {
        repeatPasswordLayout.error = msg
        repeatPasswordLayout.isErrorEnabled = true
    }

    private fun clearRepeatPasswordError() {
        repeatPasswordLayout.error = null
        repeatPasswordLayout.isErrorEnabled = false
    }
}