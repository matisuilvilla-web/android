// PantallaLogin.kt
package cl.inacap.cloudled.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

fun sha256(input: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}

@Composable
fun PantallaLogin(
    hashGuardado: String,
    onLoginCorrecto: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Login", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )

            if (error) {
                Text("Credenciales incorrectas", color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = {
                val hashIngresado = sha256(password)
                if (usuario == "admin" && hashIngresado == hashGuardado) {
                    onLoginCorrecto()
                } else {
                    error = true
                }
            }) {
                Text("Ingresar")
            }
        }
    }
}
