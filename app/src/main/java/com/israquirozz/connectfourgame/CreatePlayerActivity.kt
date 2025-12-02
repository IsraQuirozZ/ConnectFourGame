package com.israquirozz.connectfourgame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast

class CreatePlayerActivity : AppCompatActivity() {

    // Rutas XML / IDs usados:
    // input_username_layout : TextInputLayout (para mostrar errores)
    // input_username        : TextInputEditText
    // input_password_layout / input_password...
    // btn_create_player_form : MaterialButton

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var btnCreate: Button

    // Configuración de validación:
    private val USER_MIN_LEN = 3
    private val USER_MAX_LEN = 10

    // Expresión regular explicada:
    // ^                         : inicio de cadena
    // (?=.{3,10}$)              : lookahead para longitud total entre 3 y 10
    // (?=(?:.*\\d){0,2}$)      : lookahead que permite como máximo 2 dígitos en toda la cadena
    // [A-Za-z]                  : primer carácter debe ser letra
    // [A-Za-z0-9_]*             : resto puede ser letras, dígitos o guión bajo
    // $                         : fin de cadena
    //
    // Ejemplo válido: "a_b1", "user_12", "Abc"
    // Ejemplo inválido: "1abc" (no inicia por letra), "user123" (3 dígitos), "ab" (menos de 3)
    private val usernameRegex = Regex("^(?=.{$USER_MIN_LEN,$USER_MAX_LEN}$)(?=(?:.*\\d){0,2}$)[A-Za-z][A-Za-z0-9_]*$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_player)

        // Find views
        usernameLayout = findViewById(R.id.input_username_layout)
        etUsername = findViewById(R.id.input_username)
        btnCreate = findViewById(R.id.btn_create_player_form)

        // Validación en tiempo real: cuando el focus sale del campo (onFocusChange),
        // mostramos error si no es válido. Esto reproduce el comportamiento:
        // "si salimos de la cajita con errores que aparezca el mensaje debajo".
        etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateUsernameAndShow()
            } else {
                // al entrar a editar, quitar error visual (no borrar mensaje aún)
                usernameLayout.error = null
                usernameLayout.isErrorEnabled = false
            }
        }

        // También validamos al pulsar Crear (doble check antes de aceptar)
        btnCreate.setOnClickListener {
            val okUser = validateUsernameAndShow()
            // por ahora solo validamos username; más adelante validarás passwords
            if (okUser) {
                // OK: devolvemos el username y cerramos (simulando creación)
                val username = etUsername.text.toString().trim()
                val data = Intent().apply {
                    putExtra("EXTRA_USERNAME", username)
                }
                setResult(Activity.RESULT_OK, data)
                Toast.makeText(this, "Usuario creado: $username", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // si hay error, hacemos focus y mostramos toast opcional
                etUsername.requestFocus()
                Toast.makeText(this, "Corrige los errores antes de crear el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Valida el username según las reglas definidas.
     * Si hay error, muestra el mensaje en el TextInputLayout y devuelve false.
     * Si es válido, limpia el error y devuelve true.
     */
    private fun validateUsernameAndShow(): Boolean {
        val raw = etUsername.text?.toString() ?: ""
        val username = raw.trim()

        // Check vacío rápido
        if (username.isEmpty()) {
            showUsernameError("El nombre de usuario no puede estar vacío")
            return false
        }

        // Check longitud
        if (username.length < USER_MIN_LEN) {
            showUsernameError("El nombre debe tener al menos $USER_MIN_LEN caracteres")
            return false
        }
        if (username.length > USER_MAX_LEN) {
            showUsernameError("Máximo $USER_MAX_LEN caracteres")
            return false
        }

        // Check regex (inicia por letra, solo letras/dígitos/_ y max 2 dígitos)
        if (!usernameRegex.matches(username)) {
            // determinar cuál es el error más probable para dar mensaje claro
            // Si no empieza por letra:
            if (!username[0].isLetter()) {
                showUsernameError("El nombre debe comenzar por una letra")
                return false
            }

            // Si contiene caracteres inválidos:
            val invalidChar = username.firstOrNull { !(it.isLetterOrDigit() || it == '_') }
            if (invalidChar != null) {
                showUsernameError("Carácter no permitido: '$invalidChar' (solo letras, números y '_')")
                return false
            }

            // Si tiene más de 2 dígitos:
            val digitCount = username.count { it.isDigit() }
            if (digitCount > 2) {
                showUsernameError("Como máximo se permiten 2 números en el nombre")
                return false
            }

            // fallback genérico
            showUsernameError("Nombre inválido (debe empezar por letra y usar solo letras, números y '_')")
            return false
        }

        // Si todo OK, limpia errores
        usernameLayout.error = null
        usernameLayout.isErrorEnabled = false
        return true
    }

    private fun showUsernameError(message: String) {
        // Activa el modo error en TextInputLayout y pone el texto
        usernameLayout.error = message
        usernameLayout.isErrorEnabled = true

        // (Opcional) marcar visualmente el campo cambiando el stroke color a error:
        // Para un cambio visual más claro podrías usar usernameLayout.boxStrokeColor = ...
        // pero eso requiere API MaterialComponents recientes; nos quedamos con error por ahora.
        Log.d("CreatePlayer", "Username error: $message")
    }
}
