package com.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.calculadora.ui.CalculatorScreen
import com.calculadora.ui.theme.CalculadoraTheme

/**
 * CONCEPTO: Activity
 * ─────────────────────────────────────────────────────────────────────────────
 * Una Activity es el punto de entrada visible de una aplicación Android.
 * Representa una pantalla. En Jetpack Compose solo necesitamos UNA Activity.
 *
 * ComponentActivity: clase base de AndroidX que habilita el uso de Compose.
 *
 * CONCEPTO: enableEdgeToEdge()
 * Hace que el contenido de la app se dibuje detrás de las barras del sistema
 * (status bar y navigation bar), logrando el efecto de pantalla completa
 * característico de las apps modernas.
 *
 * CONCEPTO: setContent { }
 * Reemplaza el clásico setContentView(R.layout.activity_main).
 * Aquí se define el árbol de funciones @Composable que conforman la UI.
 * Compose usa un modelo declarativo: describes el estado deseado,
 * no los pasos para llegar a él.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dibuja contenido detrás de las barras del sistema
        enableEdgeToEdge()

        setContent {
            // CalculadoraTheme aplica el tema oscuro de Material 3
            CalculadoraTheme {
                // Pantalla principal de la calculadora
                CalculatorScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
