package br.com.moraisdrive

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var number: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(15, 15, 15)
        window.navigationBarColor = android.graphics.Color.BLACK

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(android.graphics.Color.rgb(15, 15, 15))
        }
        root.addView(TextView(this).apply {
            text = "MORAIS DRIVE"
            textSize = 28f
            setTextColor(android.graphics.Color.WHITE)
            gravity = Gravity.CENTER
        }, match())
        root.addView(TextView(this).apply {
            text = "TELEFONE"
            textSize = 14f
            setTextColor(android.graphics.Color.rgb(230, 45, 55))
            gravity = Gravity.CENTER
        }, match())

        number = EditText(this).apply {
            hint = "Digite o número"
            textSize = 22f
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setTextColor(android.graphics.Color.WHITE)
            setHintTextColor(android.graphics.Color.GRAY)
        }
        root.addView(number, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64).apply { topMargin = 28 })

        root.addView(Button(this).apply {
            text = "LIGAR"
            setOnClickListener { makeCall() }
        }, match())
        root.addView(Button(this).apply {
            text = "DEFINIR COMO TELEFONE PADRÃO"
            setOnClickListener { requestDialerRole() }
        }, match())
        root.addView(TextView(this).apply {
            text = "A versão Android está em preparação. A versão GPS/web continua separada e funcionando."
            textSize = 13f
            setTextColor(android.graphics.Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }, match())
        setContentView(root)

        intent?.data?.schemeSpecificPart?.let { number.setText(it) }
    }

    private fun requestDialerRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager?.isRoleAvailable(RoleManager.ROLE_DIALER) == true &&
            !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
            startActivityForResult(roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER), 100)
        } else {
            Toast.makeText(this, "O Morais Drive já é o telefone padrão ou esta função não está disponível.", Toast.LENGTH_LONG).show()
        }
    }

    private fun makeCall() {
        val value = number.text.toString().trim()
        if (value.isBlank()) {
            Toast.makeText(this, "Digite um número.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val telecom = getSystemService(TELECOM_SERVICE) as TelecomManager
            telecom.placeCall(Uri.fromParts("tel", value, null), Bundle())
        } catch (e: SecurityException) {
            Toast.makeText(this, "Defina o Morais Drive como telefone padrão e conceda as permissões necessárias.", Toast.LENGTH_LONG).show()
        }
    }

    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 12 }
}
