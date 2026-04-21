package dev.goodwy.math.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService
import com.goodwy.commons.helpers.isUpsideDownCakePlus
import dev.goodwy.math.activities.MainActivity

class MyTileService : TileService() {

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        openApp()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun openApp() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivityAndCollapse(intent)
        } catch (_: Exception) {
            if (isUpsideDownCakePlus()) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    startActivityAndCollapse(pendingIntent)
                } catch (_: Exception) {
                    appUnlockAndRun()
                }
            } else {
                appUnlockAndRun()
            }
        }
    }

    private fun appUnlockAndRun() {
        unlockAndRun {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
