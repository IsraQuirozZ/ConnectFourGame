package com.israquirozz.connectfourgame

import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val btnExit = findViewById<MaterialButton>(R.id.btn_exit_game)

        btnExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Limpia la pila de pantallas (muy importante)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )

            // Indicamos al MainActivity que debe resetear todo
            intent.putExtra("RESET_PLAYER", true)

            startActivity(intent)
            finish()
        }
    }
}
