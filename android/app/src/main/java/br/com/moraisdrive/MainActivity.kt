package br.com.moraisdrive

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
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
    private val contactNames = ArrayList<String>()
    private val contactNumbers = ArrayList<String>()

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
            text = "CONTATOS DO CELULAR"
            setOnClickListener { openContacts() }
        }, match())
        root.addView(Button(this).apply {
            text = "LIGAR"
            setOnClickListener { makeCall() }
        }, match())
        root.addView(Button(this).apply {
            text = "DEFINIR COMO TELEFONE PADRÃO"
            setOnClickListener { requestDialerRole() }
        }, match())
        root.addView(TextView(this).apply {
            text = "Os contatos só serão acessados após sua autorização. Chamadas dependem de chip ativo e permissões."
            textSize = 13f
            setTextColor(android.graphics.Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }, match())
        setContentView(root)

        if (intent?.action == Intent.ACTION_DIAL) {
            intent.data?.schemeSpecificPart?.let { number.setText(it) }
        }
    }

    private fun openContacts() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 102)
            return
        }
        showContacts()
    }

    private fun showContacts() {
        contactNames.clear()
        contactNumbers.clear()
        try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC"
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex).orEmpty()
                    val phone = cursor.getString(numberIndex).orEmpty()
                    if (phone.isNotBlank()) {
                        contactNames.add("$name\n$phone")
                        contactNumbers.add(phone)
                    }
                }
            }
        } catch (e: SecurityException) {
            toast("O acesso aos contatos não foi autorizado.")
            return
        } catch (e: Exception) {
            toast("Não foi possível carregar os contatos deste aparelho.")
            return
        }

        if (contactNames.isEmpty()) {
            toast("Nenhum contato com número de telefone foi encontrado.")
            return
        }

        AlertDialog.Builder(this)
            .setTitle("CONTATOS DO CELULAR")
            .setItems(contactNames.toTypedArray()) { _, which ->
                number.setText(contactNumbers[which])
                number.setSelection(number.text.length)
            }
            .setNegativeButton("FECHAR", null)
            .show()
    }

    @Suppress("DEPRECATION")
    private fun requestDialerRole() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = getSystemService(RoleManager::class.java)
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                    if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                        startActivityForResult(roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER), 100)
                    } else {
                        toast("O Morais Drive já é o telefone padrão.")
                    }
                } else {
                    toast("Este aparelho não disponibilizou a função de telefone padrão.")
                }
            } else {
                val changeDefault = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                    putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                }
                startActivity(changeDefault)
            }
        } catch (e: Exception) {
            toast("Não foi possível abrir a configuração de telefone padrão neste aparelho.")
        }
    }

    private fun makeCall() {
        val value = number.text.toString().trim()
        if (value.isBlank()) {
            toast("Digite um número ou escolha um contato.")
            return
        }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 101)
            return
        }
        placeCall(value)
    }

    private fun placeCall(value: String) {
        try {
            val telecom = getSystemService(TELECOM_SERVICE) as TelecomManager
            telecom.placeCall(Uri.fromParts("tel", value, null), Bundle())
        } catch (e: SecurityException) {
            toast("Defina o Morais Drive como telefone padrão e verifique as permissões.")
        } catch (e: Exception) {
            toast("Não foi possível iniciar a chamada. Verifique o chip e a rede.")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            101 -> if (granted) placeCall(number.text.toString().trim())
                else toast("A permissão de telefone é necessária para realizar chamadas.")
            102 -> if (granted) showContacts()
                else toast("O acesso aos contatos é necessário para exibir sua lista.")
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun match() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply { topMargin = 12 }
}
