package com.beautyshop.movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.beautyshop.movil.ui.navigation.AppRoot
import com.beautyshop.movil.ui.theme.BeautyShopTheme

/** Única actividad: aloja la interfaz Compose de toda la aplicación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeautyShopTheme {
                AppRoot()
            }
        }
    }
}
