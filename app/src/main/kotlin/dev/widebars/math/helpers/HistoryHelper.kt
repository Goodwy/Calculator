package dev.goodwy.math.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.goodwy.commons.helpers.ensureBackgroundThread
import dev.goodwy.math.extensions.calculatorDB
import dev.goodwy.math.models.History

class HistoryHelper(val context: Context) {
    fun getHistory(callback: (calculations: ArrayList<History>) -> Unit) {
        ensureBackgroundThread {
            val notes = context.calculatorDB.getHistory() as ArrayList<History>

            Handler(Looper.getMainLooper()).post {
                callback(notes)
            }
        }
    }

    fun insertOrUpdateHistoryEntry(entry: History) {
        ensureBackgroundThread {
            context.calculatorDB.insertOrUpdate(entry)
        }
    }
}
