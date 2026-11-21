package br.com.moraisdrive

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarText
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class MoraisCarScreen(
    carContext: CarContext,
    private val launchIntent: Intent
) : Screen(carContext) {

    private val destination: String?
        get() {
            val data = launchIntent.data ?: return null
            return data.getQueryParameter("q")
                ?: data.schemeSpecificPart?.takeIf { it.isNotBlank() }
        }

    override fun onGetTemplate(): Template {
        val place = destination
        val message = if (place.isNullOrBlank()) {
            "GPS do Morais Drive. Configure o destino no celular antes de iniciar a viagem."
        } else {
            "Destino recebido: $place. Confira o trajeto e inicie a navegação com o veículo parado."
        }

        val openMap = Action.Builder()
            .setTitle("ABRIR MAPA")
            .setOnClickListener {
                val query = place?.takeIf { it.isNotBlank() } ?: "Apucarana, Paraná"
                val mapIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + Uri.encode(query))
                )
                carContext.startCarApp(mapIntent)
            }
            .build()

        return MessageTemplate.Builder(CarText.create(message))
            .setTitle("MORAIS DRIVE")
            .addAction(openMap)
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
