package br.com.moraisdrive

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.telecom.Call

class CallActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(android.graphics.Color.rgb(15, 15, 15))
        }
        val title = TextView(this).apply {
            text = "CHAMADA"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.WHITE)
        }
        root.addView(title)
        val answer = Button(this).apply {
            text = "ATENDER"
            setOnClickListener {
                val call = CurrentCall.call
                if (call?.state == Call.STATE_RINGING) call.answer(android.telecom.VideoProfile.STATE_AUDIO_ONLY)
                refresh()
            }
        }
        root.addView(answer)
        root.addView(Button(this).apply {
            text = "ENCERRAR / RECUSAR"
            setOnClickListener {
                CurrentCall.call?.disconnect()
                finish()
            }
        })
        root.addView(Button(this).apply {
            text = "ALTO-FALANTE"
            setOnClickListener {
                val call = CurrentCall.call
                if (call != null) {
                    val audio = call.details
                    Toast.makeText(this@CallActivity, "Controle de áudio será conectado na próxima etapa.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        setContentView(root)
        refresh()
    }

    private fun refresh() {
        val call = CurrentCall.call ?: return
        title = when (call.state) {
            Call.STATE_RINGING -> "CHAMADA RECEBIDA"
            Call.STATE_ACTIVE -> "EM LIGAÇÃO"
            Call.STATE_DIALING -> "CHAMANDO"
            else -> "CHAMADA"
        }
    }
}
