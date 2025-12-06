/*
  Prueba simple de conexión:
  ESP32 + Botón + LED en protoboard
*/

#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <time.h>

const char* ssid = "Felinoi";
const char* password = "maincra777";

const char* firebase_host = "mobilesiot-default-rtdb.firebaseio.com";
const char* firebase_path = "/prueba_iot/estado.json";

WiFiClientSecure firebaseClient;

const int PIN_LED   = 2;
const int PIN_BOTON = 4;

int estadoLed = LOW;

int ultimoEstadoBoton = HIGH;
unsigned long ultimoCambioMs = 0;
const unsigned long DEBOUNCE_MS = 50;

// 🔹 Variables del bloque automático
unsigned long ultimoCheckFirebase = 0;
const unsigned long INTERVALO_FIREBASE = 1000;

void setup() {
  Serial.begin(115200);
  Serial.println("\nConectando a WiFi...");
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(300);
    Serial.print(".");
  }
  Serial.print("\nWiFi OK. IP: ");
  Serial.println(WiFi.localIP());

  firebaseClient.setInsecure();
  delay(500);

  Serial.println("\nIniciando prueba Boton + LED en ESP32...");

  pinMode(PIN_LED, OUTPUT);
  pinMode(PIN_BOTON, INPUT_PULLUP);

  digitalWrite(PIN_LED, estadoLed);
}

void loop() {

  int lectura = digitalRead(PIN_BOTON);
  unsigned long ahora = millis();

  if (lectura != ultimoEstadoBoton) {
    ultimoCambioMs = ahora;
    ultimoEstadoBoton = lectura;
  }

  if ((ahora - ultimoCambioMs) > DEBOUNCE_MS) {

    if (lectura == LOW) {

      while (digitalRead(PIN_BOTON) == LOW) {
        delay(10);
      }

      estadoLed = (estadoLed == LOW) ? HIGH : LOW;
      digitalWrite(PIN_LED, estadoLed);

      Serial.print("Boton presionado. Nuevo estado LED: ");
      Serial.println(estadoLed == HIGH ? "ENCENDIDO" : "APAGADO");

      String json = estadoLed == HIGH ? "\"ENCENDIDO\"" : "\"APAGADO\"";

      Serial.println("\nConectando a Firebase (443)...");
      if (!firebaseClient.connect(firebase_host, 443)) {
        Serial.println("No se pudo conectar a Firebase");
        return;
      }

      String request;
      request = String("PUT ") + firebase_path + " HTTP/1.1\r\n";
      request += String("Host: ") + firebase_host + "\r\n";
      request += "Connection: close\r\n";
      request += "Content-Type: application/json; charset=utf-8\r\n";
      request += "Content-Length: " + String(json.length()) + "\r\n\r\n";
      request += json;

      firebaseClient.print(request);

      while (firebaseClient.connected()) {
        String line = firebaseClient.readStringUntil('\n');
        if (line == "\r") break;
      }

      String payload;
      while (firebaseClient.available()) {
        payload += firebaseClient.readStringUntil('\n');
      }
      firebaseClient.stop();

      Serial.println("↪ Respuesta Firebase:");
      Serial.println(payload);

      payload.trim();

      if (payload.indexOf("ENCENDIDO") >= 0) {
        digitalWrite(PIN_LED, HIGH);
      } else if (payload.indexOf("APAGADO") >= 0) {
        digitalWrite(PIN_LED, LOW);
      }

      Serial.println("\nConsultando estado en Firebase...");
      if (firebaseClient.connect(firebase_host, 443)) {

        firebaseClient.print(
          String("GET ") + firebase_path + " HTTP/1.1\r\n" +
          "Host: " + firebase_host + "\r\n" +
          "Connection: close\r\n\r\n"
        );

        while (firebaseClient.connected()) {
          String line = firebaseClient.readStringUntil('\n');
          if (line == "\r") break;
        }

        String payload2 = firebaseClient.readString();
        firebaseClient.stop();

        payload2.trim();

        if (payload2.indexOf("ENCENDIDO") >= 0) {
          digitalWrite(PIN_LED, HIGH);
          estadoLed = HIGH;
        } else if (payload2.indexOf("APAGADO") >= 0) {
          digitalWrite(PIN_LED, LOW);
          estadoLed = LOW;
        }

      } else {
        Serial.println("No se pudo conectar a Firebase para leer estado");
      }

      delay(50);
    }
  }

  // ──────────────────────────────────────────────
  // 🔵 BLOQUE AUTOMÁTICO (mantenido igual, solo movido)
  // ──────────────────────────────────────────────
  if (millis() - ultimoCheckFirebase > INTERVALO_FIREBASE) {

    ultimoCheckFirebase = millis();

    Serial.println("\n[Auto] Consultando estado en Firebase...");
    if (firebaseClient.connect(firebase_host, 443)) {

      firebaseClient.print(
        String("GET ") + firebase_path + " HTTP/1.1\r\n" +
        "Host: " + firebase_host + "\r\n" +
        "Connection: close\r\n\r\n"
      );

      while (firebaseClient.connected()) {
        String line = firebaseClient.readStringUntil('\n');
        if (line == "\r") break;
      }

      String payload = firebaseClient.readString();
      firebaseClient.stop();

      payload.trim();

      if (payload.indexOf("ENCENDIDO") >= 0) {
        digitalWrite(PIN_LED, HIGH);
        estadoLed = HIGH;
      } else if (payload.indexOf("APAGADO") >= 0) {
        digitalWrite(PIN_LED, LOW);
        estadoLed = LOW;
      }

    } else {
      Serial.println("[Auto] No se pudo conectar a Firebase");
    }
  }
}
