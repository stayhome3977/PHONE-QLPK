package com.example.quanlyphongkham.ui.captcha

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.quanlyphongkham.BuildConfig
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.theme.Brand
import java.net.URI

const val NOT_SOLVED_MESSAGE = "Vui lòng hoàn tất ô kiểm tra bảo mật rồi thử lại."
private const val UNAVAILABLE_MESSAGE =
    "Không tải được ô kiểm tra bảo mật. Vui lòng kiểm tra kết nối Internet rồi thử lại."

/**
 * One anti-bot check, mirroring QLPK_FE's useCaptcha: the widget yields a one-time provider token,
 * which the backend exchanges for the verification token sent in X-Captcha-Token. Both are single
 * use, so every submit consumes the token and draws a fresh widget.
 */
class CaptchaState {
    var providerToken by mutableStateOf<String?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    /** Changing it re-creates the widget. */
    var resetKey by mutableIntStateOf(0)
        private set
    private var retries = 0

    val solved: Boolean get() = providerToken != null

    fun onVerify(token: String) {
        providerToken = token
        error = null
    }

    /** An expired token deserves a fresh widget: the next attempt can still succeed. */
    fun onExpire() {
        providerToken = null
        resetKey++
    }

    /** Retry a flaky failure once, then stop and say why; redrawing forever never fixes configuration. */
    fun onError(code: String?) {
        providerToken = null
        if (retries < MAX_AUTO_RETRIES) {
            retries++
            resetKey++
        } else {
            error = describeTurnstileError(code)
        }
    }

    fun onUnavailable() {
        providerToken = null
        error = UNAVAILABLE_MESSAGE
    }

    fun reset() {
        retries = 0
        providerToken = null
        error = null
        resetKey++
    }

    /** Hands out the token for one request and prepares a new widget for the next attempt. */
    fun consume(): String? = providerToken.also { reset() }

    private companion object {
        const val MAX_AUTO_RETRIES = 1
    }
}

/** Cloudflare Turnstile error codes, translated into what to do about them (from QLPK_FE useCaptcha.ts). */
fun describeTurnstileError(code: String?): String {
    val suffix = if (!code.isNullOrBlank()) " (mã Cloudflare $code)" else ""
    return when {
        code?.startsWith("1102") == true -> {
            val host = runCatching { URI(BuildConfig.TURNSTILE_HOST).host }.getOrNull() ?: BuildConfig.TURNSTILE_HOST
            "Tên miền $host chưa được cấp phép cho ô kiểm tra bảo mật. " +
                "Quản trị viên cần thêm tên miền này vào widget Turnstile trên Cloudflare$suffix."
        }
        code?.startsWith("1100") == true ->
            "Site key của ô kiểm tra bảo mật không hợp lệ. Vui lòng báo quản trị viên$suffix."
        code?.startsWith("300") == true || code?.startsWith("600") == true ->
            "Không kết nối được tới dịch vụ kiểm tra bảo mật. Vui lòng kiểm tra mạng rồi thử lại$suffix."
        else -> "Ô kiểm tra bảo mật gặp lỗi. Vui lòng thử lại$suffix."
    }
}

/** The page hosting the widget; loaded under the web portal's origin so the widget's hostname check passes. */
fun turnstileHtml(siteKey: String): String = """
<!doctype html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>html,body{margin:0;padding:0;background:transparent;overflow:hidden}#w{display:flex;justify-content:center}</style>
</head>
<body>
<div id="w"></div>
<script>
  window.onTurnstileLoad = function () {
    turnstile.render('#w', {
      sitekey: '$siteKey',
      theme: 'light',
      size: 'flexible',
      language: 'vi',
      callback: function (token) { QlpkCaptcha.onVerify(token); },
      'error-callback': function (code) { QlpkCaptcha.onError(String(code || '')); return true; },
      'expired-callback': function () { QlpkCaptcha.onExpire(); }
    });
  };
  var s = document.createElement('script');
  s.src = 'https://challenges.cloudflare.com/turnstile/v0/api.js?render=explicit&onload=onTurnstileLoad';
  s.async = true;
  s.onerror = function () { QlpkCaptcha.onUnavailable(); };
  document.head.appendChild(s);
</script>
</body>
</html>
""".trimIndent()

/** Bridges the page's callbacks (called on a WebView thread) back to Compose state on the main thread. */
private class CaptchaBridge(private val state: CaptchaState) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onVerify(token: String) = main.post { state.onVerify(token) }.let { }

    @JavascriptInterface
    fun onError(code: String) = main.post { state.onError(code) }.let { }

    @JavascriptInterface
    fun onExpire() = main.post { state.onExpire() }.let { }

    @JavascriptInterface
    fun onUnavailable() = main.post { state.onUnavailable() }.let { }
}

/** Cloudflare Turnstile widget. Put it above the submit button of every captcha-protected form. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TurnstileCaptcha(state: CaptchaState, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        if (state.error == null) {
            key(state.resetKey) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    factory = { context ->
                        WebView(context).apply {
                            setBackgroundColor(AndroidColor.TRANSPARENT)
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            isVerticalScrollBarEnabled = false
                            addJavascriptInterface(CaptchaBridge(state), "QlpkCaptcha")
                            loadDataWithBaseURL(BuildConfig.TURNSTILE_HOST, turnstileHtml(BuildConfig.TURNSTILE_SITE_KEY), "text/html", "utf-8", null)
                        }
                    },
                    onRelease = { it.destroy() },
                )
            }
        } else {
            ErrorBanner(state.error)
            TextButton(onClick = state::reset) { Text("Thử lại ô kiểm tra") }
        }
        if (state.solved) {
            Text("Đã xác minh bạn không phải robot.", style = MaterialTheme.typography.bodySmall, color = Brand.Success)
        }
        Spacer(Modifier.height(4.dp))
    }
}
