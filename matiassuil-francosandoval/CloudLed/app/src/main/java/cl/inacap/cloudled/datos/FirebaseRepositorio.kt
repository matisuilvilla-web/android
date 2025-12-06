package cl.inacap.cloudled.datos

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepositorio(
    baseDatos: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    companion object {
        private const val RUTA_ESTADO = "prueba_iot/estado"
        private const val ENCENDIDO = "ENCENDIDO"
        private const val APAGADO = "APAGADO"
    }

    private val referenciaEstado: DatabaseReference = baseDatos.getReference(RUTA_ESTADO)

    /** Observa el valor del LED en tiempo real. */
    fun observarEstado(): Flow<String> = callbackFlow {
        val oyente = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val valorCrudo = snapshot.getValue(String::class.java)
                trySend(normalizar(valorCrudo))
            }
            override fun onCancelled(error: DatabaseError) {
                // Podrías reportar error a un logger si quieres
            }
        }
        referenciaEstado.addValueEventListener(oyente)
        awaitClose { referenciaEstado.removeEventListener(oyente) }
    }

    /** Cambia el estado del LED a ENCENDIDO/APAGADO según el valor actual. */
    suspend fun alternarEstado() {
        val actualCrudo = referenciaEstado.get().await().getValue(String::class.java)
        val actual = normalizar(actualCrudo)
        val siguiente = if (actual == ENCENDIDO) APAGADO else ENCENDIDO
        establecerEstado(siguiente)
    }

    /** Establece un estado explícito. */
    suspend fun establecerEstado(nuevoEstado: String) {
        referenciaEstado.setValue(normalizar(nuevoEstado)).await()
    }

    /** Convierte cualquier representación a "encendido"/"apagado". */
    private fun normalizar(valor: String?): String {
        val s = (valor ?: APAGADO).trim().uppercase()
        return if (s in setOf("1", "true", "on", ENCENDIDO)) ENCENDIDO else APAGADO
    }
}
