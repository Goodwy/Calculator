package dev.goodwy.math.dialogs

import android.graphics.drawable.LayerDrawable
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.goodwy.commons.extensions.applyColorFilter
import com.goodwy.commons.extensions.getProperBackgroundColor
import com.goodwy.commons.extensions.getProperPrimaryColor
import com.goodwy.commons.extensions.getSurfaceColor
import com.goodwy.commons.extensions.isBlackTheme
import com.goodwy.commons.helpers.ensureBackgroundThread
import dev.goodwy.math.R
import dev.goodwy.math.activities.SimpleActivity
import dev.goodwy.math.adapters.HistoryAdapter
import dev.goodwy.math.databinding.DialogHistoryBinding
import dev.goodwy.math.extensions.calculatorDB
import dev.goodwy.math.helpers.CalculatorImpl
import dev.goodwy.math.models.History

class HistoryDialog(activity: SimpleActivity, items: List<History>, calculator: CalculatorImpl) {
    private var dialog: BottomSheetDialog? = null

    init {
        val binding = DialogHistoryBinding.inflate(activity.layoutInflater, null, false)

        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(binding.root)

        bottomSheetDialog.behavior.isFitToContents = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.behavior.isDraggable = true

        // Setting up the adapter DO showed the dialog
        val adapter = HistoryAdapter(activity, items, calculator) {
            bottomSheetDialog.dismiss()
        }
        binding.historyList.adapter = adapter

        // Ensure that LayoutManager is installed
        if (binding.historyList.layoutManager == null) {
            binding.historyList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        }

        binding.dialogHolder.background =
            ResourcesCompat.getDrawable(activity.resources, com.goodwy.commons.R.drawable.bottom_sheet_bg, activity.theme).apply {
                val backgroundColor = if (activity.isBlackTheme()) activity.getSurfaceColor() else activity.getProperBackgroundColor()
                (this as LayerDrawable).findDrawableByLayerId(com.goodwy.commons.R.id.bottom_sheet_background).applyColorFilter(backgroundColor)
            }

        binding.clearHistory.setTextColor(activity.resources.getColor(com.goodwy.commons.R.color.red_missed, activity.theme))
        binding.clearHistory.setOnClickListener {
            ensureBackgroundThread {
                activity.applicationContext.calculatorDB.deleteHistory()
                activity.runOnUiThread {
                    Toast.makeText(activity, R.string.history_cleared, Toast.LENGTH_SHORT).show()
                    // Clean the adapter
                    adapter.updateData(emptyList())
                    bottomSheetDialog.dismiss()
                }
            }
        }

        binding.closeButton.setTextColor(activity.getProperPrimaryColor())
        binding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
            dialog = null
        }

        bottomSheetDialog.show()
        dialog = bottomSheetDialog
    }
}
