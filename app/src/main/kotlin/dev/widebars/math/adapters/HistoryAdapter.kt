package dev.goodwy.math.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.goodwy.commons.extensions.copyToClipboard
import com.goodwy.commons.extensions.getContrastColor
import com.goodwy.commons.extensions.getProperBackgroundColor
import com.goodwy.commons.extensions.getProperPrimaryColor
import com.goodwy.commons.extensions.getProperTextColor
import com.goodwy.commons.extensions.getSurfaceColor
import com.goodwy.commons.extensions.isBlackTheme
import com.goodwy.commons.extensions.isDynamicTheme
import com.goodwy.commons.extensions.isRTLLayout
import com.goodwy.commons.extensions.isSystemInDarkMode
import com.goodwy.commons.extensions.slideLeft
import com.goodwy.commons.extensions.slideLeftReturn
import com.goodwy.commons.extensions.slideRight
import com.goodwy.commons.extensions.slideRightReturn
import com.goodwy.commons.helpers.ensureBackgroundThread
import dev.goodwy.math.R
import dev.goodwy.math.activities.SimpleActivity
import dev.goodwy.math.databinding.HistoryViewBinding
import dev.goodwy.math.extensions.calculatorDB
import dev.goodwy.math.helpers.CalculatorImpl
import dev.goodwy.math.helpers.SWIPE_ACTION_DELETE
import dev.goodwy.math.helpers.SWIPE_ACTION_EDIT
import dev.goodwy.math.models.History
import me.thanel.swipeactionview.SwipeActionView
import me.thanel.swipeactionview.SwipeGestureListener

class HistoryAdapter(
    val activity: SimpleActivity,
    var items: List<History>,
    val calc: CalculatorImpl,
    val itemClick: () -> Unit
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var textColor = activity.getProperTextColor()
    private var backgroundColor = activity.getProperBackgroundColor()
    private var surfaceColor = activity.getSurfaceColor()
    private var primaryColor = activity.getProperPrimaryColor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(HistoryViewBinding.inflate(activity.layoutInflater, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: HistoryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: History): View {
            itemView.apply {
                binding.itemFormula.text = item.formula
                binding.itemResult.text = item.result
                binding.itemFormula.setTextColor(textColor)
                binding.itemResult.setTextColor(textColor)

                setOnClickListener {
                    calc.addNumberToFormula(item.result)
                    itemClick()
                }

                setOnLongClickListener {
                    activity.baseContext.copyToClipboard(item.result)
                    true
                }

                if (activity.isBlackTheme()) {
                    binding.historyHolder.setBackgroundColor(surfaceColor)
                } else binding.historyHolder.setBackgroundColor(backgroundColor)

                val isRTL = activity.isRTLLayout
                val swipeLeftAction = if (isRTL) SWIPE_ACTION_EDIT else SWIPE_ACTION_DELETE
                binding.swipeLeftIcon.setImageResource(swipeActionImageResource(swipeLeftAction))
                binding.swipeLeftIcon.setColorFilter(primaryColor.getContrastColor())
                binding.swipeLeftIconHolder.setBackgroundColor(swipeActionColor(swipeLeftAction))

                val swipeRightAction = if (isRTL) SWIPE_ACTION_DELETE else SWIPE_ACTION_EDIT
                binding.swipeRightIcon.setImageResource(swipeActionImageResource(swipeRightAction))
                binding.swipeRightIcon.setColorFilter(primaryColor.getContrastColor())
                binding.swipeRightIconHolder.setBackgroundColor(swipeActionColor(swipeRightAction))

                binding.historySwipeWrapper.swipeGestureListener = object : SwipeGestureListener {
                    override fun onSwipedLeft(swipeActionView: SwipeActionView): Boolean {
                        swipeAction(swipeLeftAction, item)
                        binding.swipeLeftIcon.slideLeftReturn(binding.swipeLeftIconHolder)
                        return true
                    }

                    override fun onSwipedRight(swipeActionView: SwipeActionView): Boolean {
                        swipeAction(swipeRightAction, item)
                        binding.swipeRightIcon.slideRightReturn(binding.swipeRightIconHolder)
                        return true
                    }

                    override fun onSwipedActivated(swipedRight: Boolean) {
                        if (swipedRight) binding.swipeRightIcon.slideRight(binding.swipeRightIconHolder)
                        else binding.swipeLeftIcon.slideLeft()
                    }

                    override fun onSwipedDeactivated(swipedRight: Boolean) {
                        if (swipedRight) binding.swipeRightIcon.slideRightReturn(binding.swipeRightIconHolder)
                        else binding.swipeLeftIcon.slideLeftReturn(binding.swipeLeftIconHolder)
                    }
                }
            }

            return itemView
        }
    }

    fun updateData(newItems: List<History>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    private fun swipeActionImageResource(swipeAction: Int): Int {
        return when (swipeAction) {
            SWIPE_ACTION_DELETE -> com.goodwy.commons.R.drawable.ic_delete_outline
            else -> com.goodwy.commons.R.drawable.ic_copy_vector
        }
    }

    private fun swipeActionColor(swipeAction: Int): Int {
        return when (swipeAction) {
            SWIPE_ACTION_DELETE -> activity.resources.getColor(com.goodwy.commons.R.color.red_missed, activity.theme)
            else -> primaryColor
        }
    }

    private fun swipeAction(swipeAction: Int, item: History) {
        when (swipeAction) {
            SWIPE_ACTION_DELETE -> swipedDelete(item)
            else -> swipedCopy(item)
        }
    }

    private fun swipedDelete(item: History) {
        if (item.id != null) {
            val position = items.indexOf(item)
            if (position != -1) {
                val newItems = items.toMutableList()
                newItems.removeAt(position)
                items = newItems
                notifyItemRemoved(position)
            }
            ensureBackgroundThread {
                activity.applicationContext.calculatorDB.deleteHistoryItem(item.id!!)
            }
        }
    }

    private fun swipedCopy(item: History) {
        activity.baseContext.copyToClipboard(item.result)
    }
}
