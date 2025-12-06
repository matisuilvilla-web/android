package cl.inacap.cloudled.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaActivador(vm: MonitorViewModel, onVolver: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Control del LED",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { vm.alternarLed() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Alternar LED")
            }

            Button(onClick = { onVolver() }) {
                Text("Volver a Monitor")
            }
        }
    }
}
