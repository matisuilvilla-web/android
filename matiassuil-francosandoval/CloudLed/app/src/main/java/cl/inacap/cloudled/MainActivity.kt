package cl.inacap.cloudled

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cl.inacap.cloudled.ui.PantallaLogin
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import cl.inacap.cloudled.ui.*
import cl.inacap.cloudled.ui.MonitorViewModel
import cl.inacap.cloudled.ui.PantallaMonitor
import cl.inacap.cloudled.ui.theme.CloudLedTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloudLedTheme {

                var isLoggedIn by remember { mutableStateOf(false) }
                val vistaModelo = MonitorViewModel()

                val prefs = getEncryptedPrefs(this)
                val claveGuardada = prefs.getString("hash_admin", null)

                if (claveGuardada == null) {
                    // Contraseña REAL (solo para inicializar)
                    val hash = sha256("admin123")

                    prefs.edit().putString("hash_admin", hash).apply()
                }


                if (!isLoggedIn) {
                    // Obtener el hash seguro de las preferencias
                    val hashGuardado = prefs.getString("hash_admin", null) ?: ""

                    PantallaLogin(
                        hashGuardado = hashGuardado,
                        onLoginCorrecto = {
                            isLoggedIn = true
                        }
                    )
                } else {
                    var pantallaActual by remember { mutableStateOf("monitor")}
                    when (pantallaActual) {
                        "monitor" -> PantallaMonitor(
                            vm = vistaModelo,
                            onIrAActivador = { pantallaActual = "activador" }
                        )
                        "activador" -> PantallaActivador(
                            vm = vistaModelo,
                            onVolver = { pantallaActual = "monitor"}
                        )
                    }
                }
            }
        }
    }
}

