package com.example.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.controlefinanceiro.ui.ControleDespesasScreen

// Activity é a porta de entrada de uma tela Android.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent define qual interface Compose será mostrada pela Activity.
        setContent {
            // MaterialTheme fornece cores, tipografia e componentes no padrão Material.
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Nossa tela principal fica em outro arquivo para manter responsabilidades separadas.
                    ControleDespesasScreen()
                }
            }
        }
    }
}
