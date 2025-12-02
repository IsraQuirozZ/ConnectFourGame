package com.israquirozz.connectfourgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var tvUser: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnCreate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvUser = findViewById(R.id.tv_username)
        btnPlay = findViewById(R.id.btn_play)
        btnCreate = findViewById(R.id.btn_create_player)

        // Estado inicial: jugar deshabilitado, usuario oculto
        tvUser.visibility = TextView.INVISIBLE
        btnPlay.isEnabled = false

        // NAVEGACIÓN AL FORMULARIO (ACTIVITY_CREATE_PLAYER)
        btnCreate.setOnClickListener {
            val intent = Intent(this, CreatePlayerActivity::class.java)
            startActivity(intent)
        }

        // TODO: y luego cuando volvamos activaremos "Jugar"
    }
}