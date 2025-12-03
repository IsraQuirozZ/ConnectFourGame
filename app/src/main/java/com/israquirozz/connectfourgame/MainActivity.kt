package com.israquirozz.connectfourgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvUser: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnCreate: Button

    // Guardamos username actual (si existe)
    private var currentUser: String? = null

    // Launcher moderno para recibir resultados
    private val createPlayerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val username = result.data?.getStringExtra("EXTRA_USERNAME")

            if (!username.isNullOrEmpty()) {
                setUserLogged(username)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvUser = findViewById(R.id.tv_username)
        btnPlay = findViewById(R.id.btn_play)
        btnCreate = findViewById(R.id.btn_create_player)

        // Estado inicial:
        setUserLogged(null)

        // Click en "Crear jugador" o "Cambiar jugador"
        btnCreate.setOnClickListener {
            val intent = Intent(this, CreatePlayerActivity::class.java)
            createPlayerLauncher.launch(intent)
        }

        btnPlay.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

    //------------------------------------------------------------
    // ACTUALIZA LA UI SEGÚN SI HAY USUARIO O NO
    //------------------------------------------------------------
    private fun setUserLogged(username: String?) {

        currentUser = username

        if (username == null) {
            // Estado sin usuario
            tvUser.text = ""
            tvUser.visibility = TextView.INVISIBLE

            btnPlay.isEnabled = false
            btnCreate.text = "Crear jugador"

        } else {
            // Estado CON usuario
            tvUser.text = "Jugando: $username"
            tvUser.visibility = TextView.VISIBLE

            btnPlay.isEnabled = true
            btnCreate.text = "Cambiar jugador"
        }
    }
}
