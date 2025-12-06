package cl.inacap.cloudled.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaMonitor(vm: MonitorViewModel, onIrAActivador: () -> Unit) {
    val estado by vm.estadoUi.collectAsState()
    val estaEncendido = estado.led == "ENCENDIDO"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (estaEncendido) "LED: ENCENDIDO" else "LED: APAGADO",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(onClick = { onIrAActivador() }) {
                Text("ir a Activador")
            }
        }
    }
}
