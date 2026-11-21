package br.com.moraisdrive

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.CallAudioState

class MoraisInCallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CurrentCall.call = call
        CurrentCall.inCallService = this
        call.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                CurrentCall.call = call
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

    fun toggleSpeaker() {
        val current = callAudioState
        val route = if (current.route == CallAudioState.ROUTE_SPEAKER) {
            CallAudioState.ROUTE_EARPIECE
        } else {
            CallAudioState.ROUTE_SPEAKER
        }
        setAudioRoute(route)
    }

    override fun onDestroy() {
        if (CurrentCall.inCallService === this) CurrentCall.inCallService = null
        super.onDestroy()
    }
}

object CurrentCall {
    @Volatile var call: Call? = null
    @Volatile var inCallService: MoraisInCallService? = null
}
