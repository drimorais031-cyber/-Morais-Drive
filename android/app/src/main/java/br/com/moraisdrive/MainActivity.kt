package br.com.moraisdrive

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.graphics.Color
import android.view.WindowManager

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var geoCallback: GeolocationPermissions.Callback? = null
    private var geoOrigin: String? = null
    private var pendingCallNumber: String? = null
    private var pendingFileChooser: android.webkit.ValueCallback<Array<Uri>>? = null
    private val appUrl = "https://drimorais031-cyber.github.io/-Morais-Drive/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 12, 18)
        window.navigationBarColor = Color.BLACK
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        webView = WebView(this).apply {
            setBackgroundColor(Color.rgb(8, 12, 18))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setGeolocationEnabled(true)
            settings.loadsImagesAutomatically = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.allowFileAccess = false
            settings.allowContentAccess = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            addJavascriptInterface(NativeBridge(), "MoraisNative")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    injectContactsButton()
                }
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    val uri = Uri.parse(url)
                    return when {
                        uri.scheme == "tel" -> {
                            try { startActivity(Intent(Intent.ACTION_DIAL, uri)) } catch (_: Exception) { toast("Não foi possível abrir o telefone.") }
                            true
                        }
                        uri.host == "drimorais031-cyber.github.io" -> false
                        uri.scheme == "https" || uri.scheme == "http" -> {
                            try { startActivity(Intent(Intent.ACTION_VIEW, uri)) } catch (_: Exception) {}
                            true
                        }
                        else -> true
                    }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    view: WebView,
                    filePathCallback: android.webkit.ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    pendingFileChooser?.onReceiveValue(null)
                    pendingFileChooser = filePathCallback
                    return try {
                        val intent = fileChooserParams.createIntent().apply {
                            type = "audio/*"
                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        }
                        startActivityForResult(intent, REQUEST_AUDIO_FILES)
                        true
                    } catch (_: Exception) {
                        pendingFileChooser = null
                        toast("Não foi possível abrir os arquivos de música.")
                        false
                    }
                }
                override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        callback.invoke(origin, true, false)
                    } else {
                        geoOrigin = origin
                        geoCallback = callback
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION)
                    }
                }
            }
        }
        setContentView(webView)
        webView.loadUrl(appUrl)
    }

    private fun injectContactsButton() {
        val script = """
            (function() {
              if (document.getElementById('moraisNativeContactsBtn')) return;
              var host = document.querySelector('.top-right') || document.querySelector('.topbar');
              if (!host) return;
              var b = document.createElement('button');
              b.id = 'moraisNativeContactsBtn';
              b.type = 'button';
              b.textContent = '👥';
              b.title = 'Contatos do celular';
              b.setAttribute('aria-label','Contatos do celular');
              b.style.cssText = 'width:44px;height:44px;margin-left:8px;border:1px solid rgba(255,255,255,.22);border-radius:13px;background:#ff303b;color:white;font-size:21px;font-weight:800;cursor:pointer;flex:0 0 auto;';
              b.onclick = function(){ if(window.MoraisNative) window.MoraisNative.openContacts(); };
              host.appendChild(b);
              window.addEventListener('morais-contact-selected', function(e) {
                var d=e.detail||{};
                var field=document.querySelector('input[type=tel],input[autocomplete=tel],input[name*=phone i],input[id*=phone i]');
                if(field){ field.value=d.phone||''; field.dispatchEvent(new Event('input',{bubbles:true})); field.dispatchEvent(new Event('change',{bubbles:true})); }
              });
            })();
        """.trimIndent()
        webView.evaluateJavascript(script, null)
    }

    private inner class NativeBridge {
        @JavascriptInterface fun openContacts() { runOnUiThread { requestContacts() } }
    }

    private fun requestContacts() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS)
        } else showContacts()
    }

    private fun showContacts() {
        val labels = ArrayList<String>()
        val names = ArrayList<String>()
        val numbers = ArrayList<String>()
        try {
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
            contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC")?.use { cursor ->
                val ni = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val pi = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(ni).orEmpty()
                    val phone = cursor.getString(pi).orEmpty()
                    if (phone.isNotBlank()) { labels.add("$name\n$phone"); names.add(name); numbers.add(phone) }
                }
            }
        } catch (_: Exception) { toast("Não foi possível acessar os contatos."); return }
        if (labels.isEmpty()) { toast("Nenhum contato com telefone foi encontrado."); return }
        AlertDialog.Builder(this).setTitle("CONTATOS DO CELULAR")
            .setItems(labels.toTypedArray()) { _, index ->
                val name = names[index]
                val phone = numbers[index]
                val payload = org.json.JSONObject().put("name", name).put("phone", phone)
                webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('morais-contact-selected',{detail:$payload}));", null)
                AlertDialog.Builder(this).setTitle(name.ifBlank { "Contato selecionado" }).setMessage(phone)
                    .setNegativeButton("FECHAR", null).setPositiveButton("LIGAR") { _, _ -> callNumber(phone) }.show()
            }.setNegativeButton("FECHAR", null).show()
    }

    private fun callNumber(number: String) {
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            pendingCallNumber = number
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
            return
        }
        try {
            val telecom = getSystemService(TELECOM_SERVICE) as TelecomManager
            telecom.placeCall(Uri.fromParts("tel", number, null), Bundle())
        } catch (_: Exception) {
            try { startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))) }
            catch (_: Exception) { toast("Não foi possível iniciar a ligação. Verifique o chip e a rede.") }
        }
    }

    @Deprecated("Deprecated in Android, retained for WebView file chooser compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUDIO_FILES) {
            val callback = pendingFileChooser
            pendingFileChooser = null
            if (callback != null) {
                val results = if (resultCode == RESULT_OK) {
                    val clip = data?.clipData
                    when {
                        clip != null -> Array(clip.itemCount) { i -> clip.getItemAt(i).uri }
                        data?.data != null -> arrayOf(data.data!!)
                        else -> null
                    }
                } else null
                callback.onReceiveValue(results)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            REQUEST_CONTACTS -> if (granted) showContacts() else toast("Permita o acesso aos contatos para usar sua agenda.")
            REQUEST_CALL -> {
                val number = pendingCallNumber
                pendingCallNumber = null
                if (granted && number != null) callNumber(number) else if (!granted) toast("Permissão de telefone não concedida.")
            }
            REQUEST_LOCATION -> {
                val callback = geoCallback
                val origin = geoOrigin
                geoCallback = null
                geoOrigin = null
                callback?.invoke(origin, granted, false)
                if (!granted) toast("Permita a localização para usar o GPS do Morais Drive.")
            }
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    companion object {
        private const val REQUEST_CONTACTS = 102
        private const val REQUEST_CALL = 101
        private const val REQUEST_LOCATION = 103
        private const val REQUEST_AUDIO_FILES = 104
    }
}
