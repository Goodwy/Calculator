package dev.goodwy.math.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.goodwy.commons.extensions.copyToClipboard
import com.goodwy.commons.R

class CopyReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_COPY_RESULT = "ACTION_COPY_RESULT"
        const val EXTRA_RESULT_TEXT = "extra_result_text"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_COPY_RESULT -> {
                val textToCopy = intent.getStringExtra(EXTRA_RESULT_TEXT)
                if (!textToCopy.isNullOrEmpty()) {
                    context.copyToClipboard(textToCopy)
                    Toast.makeText(
                        context,
                        context.getString(R.string.copy_to_clipboard),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
