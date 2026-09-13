package com.example.quanlyphongkham.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import com.example.quanlyphongkham.MainActivity
import com.example.quanlyphongkham.R

/**
 * Android never lets an app drop its icon on the home screen by itself; it can only ask the launcher,
 * which shows its own "Add to home screen" dialog.
 */
object HomeScreenShortcut {
    private const val ID = "qlpk_home"

    fun isSupported(context: Context): Boolean = ShortcutManagerCompat.isRequestPinShortcutSupported(context)

    /** Returns false when the launcher cannot pin shortcuts. */
    fun request(context: Context): Boolean {
        if (!isSupported(context)) return false
        val intent = Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val label = context.getString(R.string.app_name)
        val shortcut = ShortcutInfoCompat.Builder(context, ID)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(IconCompat.createWithAdaptiveBitmap(adaptiveIconBitmap(context)))
            .setIntent(intent)
            .build()
        return ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
    }

    /** Renders the launcher icon's background and foreground layers into one 108dp adaptive bitmap. */
    private fun adaptiveIconBitmap(context: Context): Bitmap {
        val size = (108 * context.resources.displayMetrics.density).toInt()
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)
        listOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground).forEach { res ->
            ContextCompat.getDrawable(context, res)?.apply {
                setBounds(0, 0, size, size)
                draw(canvas)
            }
        }
        return bitmap
    }
}
