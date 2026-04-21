package dev.goodwy.math.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.goodwy.commons.extensions.applyColorFilter
import com.goodwy.commons.extensions.setText
import com.goodwy.commons.extensions.setTextSize
import dev.goodwy.math.R
import dev.goodwy.math.extensions.config
import dev.goodwy.math.receivers.CopyReceiver

class MyWidgetProvider : AppWidgetProvider(), Calculator {
    companion object {
        private var calc: CalculatorImpl? = null
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val config = context.config
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach { appWidgetId ->

        val views = RemoteViews(context.packageName, R.layout.widget)

            setupIntent(context, views, DECIMAL, R.id.btn_decimal, appWidgetId)
            setupIntent(context, views, ZERO, R.id.btn_0, appWidgetId)
            setupIntent(context, views, ONE, R.id.btn_1, appWidgetId)
            setupIntent(context, views, TWO, R.id.btn_2, appWidgetId)
            setupIntent(context, views, THREE, R.id.btn_3, appWidgetId)
            setupIntent(context, views, FOUR, R.id.btn_4, appWidgetId)
            setupIntent(context, views, FIVE, R.id.btn_5, appWidgetId)
            setupIntent(context, views, SIX, R.id.btn_6, appWidgetId)
            setupIntent(context, views, SEVEN, R.id.btn_7, appWidgetId)
            setupIntent(context, views, EIGHT, R.id.btn_8, appWidgetId)
            setupIntent(context, views, NINE, R.id.btn_9, appWidgetId)

            setupIntent(context, views, EQUALS, R.id.btn_equals, appWidgetId)
            setupIntent(context, views, PLUS, R.id.btn_plus, appWidgetId)
            setupIntent(context, views, MINUS, R.id.btn_minus, appWidgetId)
            setupIntent(context, views, MULTIPLY, R.id.btn_multiply, appWidgetId)
            setupIntent(context, views, DIVIDE, R.id.btn_divide, appWidgetId)
            setupIntent(context, views, PERCENT, R.id.btn_percent, appWidgetId)
            setupIntent(context, views, POWER, R.id.btn_power, appWidgetId)
            //setupIntent(context, views, ROOT, R.id.btn_root)
            setupIntent(context, views, CLEAR, R.id.btn_clear, appWidgetId)
            setupIntent(context, views, RESET, R.id.btn_reset, appWidgetId)

//            setupAppOpenIntent(context, views, R.id.formula)
            setupCopyFormulaIntent(context, views, R.id.formula, appWidgetId)
//            setupAppOpenIntent(context, views, R.id.result)
            setupCopyResultIntent(context, views, R.id.result, appWidgetId)
            setupAppOpenIntent(context, views, R.id.btn_convert)

            views.setViewVisibility(R.id.btn_reset, View.VISIBLE)
            views.applyColorFilter(R.id.widget_background, config.widgetBgColor)

            updateTextColors(views, config.widgetTextColor, context)
            setupDecimalButton(views)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getComponentName(context: Context) =
        ComponentName(context, MyWidgetProvider::class.java)

    private fun setupIntent(context: Context, views: RemoteViews, action: String, id: Int, appWidgetId: Int) {
        Intent(context, MyWidgetProvider::class.java).apply {
            this.action = action
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    appWidgetId * 100 + id,
                    this,
                    PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(id, pendingIntent)
        }
    }

    private fun setupCopyResultIntent(context: Context, views: RemoteViews, id: Int, appWidgetId: Int) {
        val resultText = calc?.getCurrentResult() ?: "0"

        Intent(context, CopyReceiver::class.java).apply {
            action = CopyReceiver.ACTION_COPY_RESULT
            putExtra(CopyReceiver.EXTRA_RESULT_TEXT, resultText)
            // Добавляем ID виджета для уникальности
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }.also { intent ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId * 1000 + id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(id, pendingIntent)
        }
    }

    private fun setupCopyFormulaIntent(context: Context, views: RemoteViews, id: Int, appWidgetId: Int) {
        val currentFormula = calc?.getCurrentFormula() ?: ""

        Intent(context, CopyReceiver::class.java).apply {
            action = CopyReceiver.ACTION_COPY_RESULT
            putExtra(CopyReceiver.EXTRA_RESULT_TEXT, currentFormula)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }.also { intent ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId * 1000 + id + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(id, pendingIntent)
        }
    }

    private fun setupAppOpenIntent(context: Context, views: RemoteViews, id: Int) {
        val intent = Intent(context, dev.goodwy.math.activities.MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(id, pendingIntent)
    }

    private fun updateTextColors(views: RemoteViews, color: Int, context: Context) {
        val viewIds = intArrayOf(
            R.id.formula, R.id.btn_0, R.id.btn_1, R.id.btn_2,
            R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9, R.id.btn_power, //R.id.btn_root,
            R.id.btn_clear, R.id.btn_reset, R.id.btn_decimal, R.id.btn_percent
        )

        for (i in viewIds) {
            views.setTextColor(i, color)
            views.setTextSize(i, context.resources.getInteger(R.integer.widget_text_size).toFloat())
        }

        val viewIconIds = intArrayOf(
            R.id.btn_plus, R.id.btn_minus, R.id.btn_multiply,
            R.id.btn_divide, R.id.btn_equals
        )

        for (i in viewIconIds) {
            views.setTextColor(i, color)
            views.setTextSize(i, context.resources.getInteger(R.integer.widget_text_size_big).toFloat())
        }

        views.setTextColor(R.id.result, color)
//        views.setTextSize(R.id.result, context.resources.getInteger(R.integer.widget_text_size_big).toFloat())

        views.applyColorFilter(R.id.btn_convert, color)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (val action = intent.action) {
            DECIMAL, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
            EQUALS, CLEAR, RESET, PLUS, MINUS, MULTIPLY, DIVIDE, PERCENT, POWER, ROOT -> {
                myAction(action, context)
                updateCopyIntent(context, intent)
            }

            else -> super.onReceive(context, intent)
        }
    }

    private fun updateCopyIntent(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        if (appWidgetId != 0) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.widget)

            setupCopyResultIntent(context, views, R.id.result, appWidgetId)
            setupCopyFormulaIntent(context, views, R.id.formula, appWidgetId)

            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }

    private fun myAction(action: String, context: Context) {
        if (calc == null) {
            calc = CalculatorImpl(this, context)
        }

        when (action) {
            DECIMAL -> calc!!.numpadClicked(R.id.btn_decimal)
            ZERO -> calc!!.numpadClicked(R.id.btn_0)
            ONE -> calc!!.numpadClicked(R.id.btn_1)
            TWO -> calc!!.numpadClicked(R.id.btn_2)
            THREE -> calc!!.numpadClicked(R.id.btn_3)
            FOUR -> calc!!.numpadClicked(R.id.btn_4)
            FIVE -> calc!!.numpadClicked(R.id.btn_5)
            SIX -> calc!!.numpadClicked(R.id.btn_6)
            SEVEN -> calc!!.numpadClicked(R.id.btn_7)
            EIGHT -> calc!!.numpadClicked(R.id.btn_8)
            NINE -> calc!!.numpadClicked(R.id.btn_9)
            EQUALS -> calc!!.handleEquals()
            CLEAR -> calc!!.handleClear()
            RESET -> calc!!.handleReset()
            PLUS, MINUS, MULTIPLY, DIVIDE, PERCENT, POWER, ROOT -> calc!!.handleOperation(action)
        }
    }

    override fun showNewResult(value: String, context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setText(R.id.result, value)
            setupCopyResultIntent(context, views, R.id.result, appWidgetId)
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }

    override fun showNewFormula(value: String, context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setText(R.id.formula, value)
            setupCopyFormulaIntent(context, views, R.id.formula, appWidgetId)
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        calc = null
    }

    private fun setupDecimalButton(views: RemoteViews) {
        views.setTextViewText(R.id.btn_decimal, getDecimalSeparator())
    }
}
