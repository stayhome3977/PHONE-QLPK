package com.example.quanlyphongkham.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.ui.appContainer
import com.example.quanlyphongkham.ui.components.AppLogo
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.theme.Brand

/**
 * Mobile counterpart of the web's AuthGuard. A guest sees a sign-in prompt in place of the screen;
 * signing in pops back to this same destination, which then shows its content, so the patient lands
 * where they were heading (the web's `redirectTo`).
 */
@Composable
fun RequireAuth(
    nav: NavHostController,
    title: String,
    reason: String,
    showBack: Boolean = true,
    content: @Composable () -> Unit,
) {
    val session by appContainer().sessionStore.session.collectAsStateWithLifecycle()
    if (session != null) {
        content()
        return
    }
    Scaffold(
        topBar = { AppTopBar(title, onBack = if (showBack) ({ nav.popBackStack() }) else null) },
        containerColor = Brand.Surface,
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            AppLogo(96.dp)
            Spacer(Modifier.height(20.dp))
            Text("Vui lòng đăng nhập để tiếp tục", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(reason, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Đăng nhập", { nav.navigate(Routes.LOGIN) }, icon = Icons.AutoMirrored.Filled.Login)
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Đăng ký tài khoản", { nav.navigate(Routes.REGISTER) })
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = { nav.navigateToTab(Routes.HOME) }) { Text("Về trang chủ") }
        }
    }
}
