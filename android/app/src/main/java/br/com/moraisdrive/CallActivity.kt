package br.com.moraisdrive

import android.app.Activity
import android.os.Bundle
import android.telecom.Call
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class CallActivity : Activity() {
    private lateinit var callTitle: TextView
    private lateinit var answerButton: Button
    private lateinit var speakerButton: Button
    private var observedCall: Call? = null

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            runOnUiThread { refresh() }
        }

        override fun onDetailsChanged(call: Call, details: Call.Details) {
            runOnUiThread { refresh() }
        }
    }

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
        callTitle = TextView(this).apply {
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.WHITE)
        }
        root.addView(callTitle)
        answerButton = Button(this).apply {
            text = "ATENDER"
            setOnClickListener {
                CurrentCall.call?.takeIf { it.state == Call.STATE_RINGING }
                    ?.answer(android.telecom.VideoProfile.STATE_AUDIO_ONLY)
                refresh()
            }
        }
        root.addView(answerButton)
        speakerButton = Button(this).apply {
            text = "ALTO-FALANTE"
            setOnClickListener { CurrentCall.inCallService?.toggleSpeaker() }
        }
        root.addView(speakerButton)
        root.addView(Button(this).apply {
            text = "ENCERRAR / RECUSAR"
            setOnClickListener {
                CurrentCall.call?.disconnect()
                finish()
            }
        })
        setContentView(root)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        observeCurrentCall()
        refresh()
    }

    override fun onPause() {
        observedCall?.unregisterCallback(callCallback)
        observedCall = null
        super.onPause()
    }

    private fun observeCurrentCall() {
        val current = CurrentCall.call
        if (observedCall === current) return
        observedCall?.unregisterCallback(callCallback)
        observedCall = current
        observedCall?.registerCallback(callCallback)
    }

    private fun refresh() {
        val call = CurrentCall.call
        if (call == null) {
            callTitle.text = "NENHUMA CHAMADA ATIVA"
            answerButton.isEnabled = false
            speakerButton.isEnabled = false
            return
        }
        val number = call.details.handle?.schemeSpecificPart
        val status = when (call.state) {
            Call.STATE_RINGING -> "CHAMADA RECEBIDA"
            Call.STATE_ACTIVE -> "EM LIGAÇÃO"
            Call.STATE_DIALING, Call.STATE_CONNECTING -> "CHAMANDO"
            Call.STATE_DISCONNECTED -> "CHAMADA ENCERRADA"
            else -> "CHAMADA"
        }
        callTitle.text = if (!number.isNullOrBlank()) "$status\\n$number" else status
        answerButton.isEnabled = call.state == Call.STATE_RINGING
        speakerButton.isEnabled = call.state == Call.STATE_ACTIVE
    }
}
