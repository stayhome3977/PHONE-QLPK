package com.example.quanlyphongkham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.quanlyphongkham.ui.HomeScreenShortcut
import com.example.quanlyphongkham.ui.navigation.RootScreen
import com.example.quanlyphongkham.ui.theme.QuanLyPhongKhamTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        val sessionStore = (application as QlpkApp).container.sessionStore
        // Hold the splash until the stored session is read, so the first frame is already the right flow.
        splash.setKeepOnScreenCondition { !sessionStore.loaded.value }

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            QuanLyPhongKhamTheme {
                RootScreen()
            }
        }

        // First launch only: offer to put the app's icon on the home screen.
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                if (!sessionStore.pinPromptShown() && HomeScreenShortcut.isSupported(this@MainActivity)) {
                    sessionStore.markPinPromptShown()
                    HomeScreenShortcut.request(this@MainActivity)
                }
            }
        }
    }
}
