package cl.inacap.cloudled

import android.app.Application
import com.google.firebase.FirebaseApp

class CloudLedApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)   // ← inicializa Firebase
    }
}
