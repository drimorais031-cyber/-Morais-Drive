package br.com.moraisdrive

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class MoraisInCallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CurrentCall.call = call
        call.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                CurrentCall.call = call
                if (state == Call.STATE_DISCONNECTED) CurrentCall.call = null
            }
        })
        val screen = Intent(this, CallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(screen)
    }

    override fun onCallRemoved(call: Call) {
        if (CurrentCall.call === call) CurrentCall.call = null
        super.onCallRemoved(call)
    }
}

object CurrentCall {
    @Volatile var call: Call? = null
}
