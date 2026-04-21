package dev.goodwy.math.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goodwy.commons.compose.extensions.enableEdgeToEdgeSimple
import com.goodwy.commons.compose.extensions.onEventValue
import com.goodwy.commons.compose.theme.AppThemeSurface
import com.goodwy.commons.dialogs.WhatsNewDialog
import com.goodwy.commons.extensions.isPro
import com.goodwy.commons.helpers.IS_CUSTOMIZING_COLORS
import com.goodwy.commons.helpers.isTiramisuPlus
import dev.goodwy.math.BuildConfig
import dev.goodwy.math.compose.SettingsScreen
import dev.goodwy.math.extensions.config
import dev.goodwy.math.extensions.launchAbout
import dev.goodwy.math.extensions.launchPurchase
import dev.goodwy.math.helpers.whatsNewList
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : SimpleActivity() {

    private val preferences by lazy { config }

    private val productIdX1 = BuildConfig.PRODUCT_ID_X1
    private val productIdX2 = BuildConfig.PRODUCT_ID_X2
    private val productIdX3 = BuildConfig.PRODUCT_ID_X3
    private val subscriptionIdX1 = BuildConfig.SUBSCRIPTION_ID_X1
    private val subscriptionIdX2 = BuildConfig.SUBSCRIPTION_ID_X2
    private val subscriptionIdX3 = BuildConfig.SUBSCRIPTION_ID_X3
    private val subscriptionYearIdX1 = BuildConfig.SUBSCRIPTION_YEAR_ID_X1
    private val subscriptionYearIdX2 = BuildConfig.SUBSCRIPTION_YEAR_ID_X2
    private val subscriptionYearIdX3 = BuildConfig.SUBSCRIPTION_YEAR_ID_X3

    private val productIdList = arrayListOf(productIdX1, productIdX2, productIdX3)
    private val productIdListRu = arrayListOf(productIdX1, productIdX2, productIdX3)
    private val subscriptionIdList = arrayListOf(subscriptionIdX1, subscriptionIdX2, subscriptionIdX3)
    private val subscriptionIdListRu = arrayListOf(subscriptionIdX1, subscriptionIdX2, subscriptionIdX3)
    private val subscriptionYearIdList = arrayListOf(subscriptionYearIdX1, subscriptionYearIdX2, subscriptionYearIdX3)
    private val subscriptionYearIdListRu = arrayListOf(subscriptionYearIdX1, subscriptionYearIdX2, subscriptionYearIdX3)

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            AppThemeSurface {
                val context = LocalContext.current
                val preventPhoneFromSleeping by preferences.preventPhoneFromSleepingFlow
                    .collectAsStateWithLifecycle(preferences.preventPhoneFromSleeping)
                val vibrateOnButtonPressFlow by preferences.vibrateOnButtonPressFlow
                    .collectAsStateWithLifecycle(preferences.vibrateOnButtonPress)
                val wasUseEnglishToggledFlow by preferences.wasUseEnglishToggledFlow
                    .collectAsStateWithLifecycle(preferences.wasUseEnglishToggled)
                val useEnglishFlow by preferences.useEnglishFlow
                    .collectAsStateWithLifecycle(preferences.useEnglish)
                val showCheckmarksOnSwitches by config.showCheckmarksOnSwitchesFlow
                    .collectAsStateWithLifecycle(initialValue = config.showCheckmarksOnSwitches)
                val isUseEnglishEnabled by remember(wasUseEnglishToggledFlow) {
                    derivedStateOf {
                        (wasUseEnglishToggledFlow || Locale.getDefault().language != "en") && !isTiramisuPlus()
                    }
                }
                val isPro = onEventValue {
                    context.isPro()
                }
                val displayLanguage = remember { Locale.getDefault().displayLanguage }
                val isTopAppBarColorIcon by config.isTopAppBarColorIcon
                    .collectAsStateWithLifecycle(initialValue = config.topAppBarColorIcon)
                val isTopAppBarColorTitle by config.isTopAppBarColorTitle
                    .collectAsStateWithLifecycle(initialValue = config.topAppBarColorTitle)

                SettingsScreen(
                    displayLanguage = displayLanguage,
                    goBack = ::finish,
                    customizeColors = ::startCustomizationActivity,
                    customizeWidgetColors = ::setupCustomizeWidgetColors,
                    preventPhoneFromSleeping = preventPhoneFromSleeping,
                    onPreventPhoneFromSleeping = preferences::preventPhoneFromSleeping::set,
                    vibrateOnButtonPressFlow = vibrateOnButtonPressFlow,
                    onVibrateOnButtonPressFlow = preferences::vibrateOnButtonPress::set,
                    isPro = isPro,
                    onPurchaseClick = ::launchPurchase,
                    onAboutClick = ::launchAbout,
                    onWhatsNewClick = ::setupWhatsNewDialog,
                    isUseEnglishEnabled = isUseEnglishEnabled,
                    isUseEnglishChecked = useEnglishFlow,
                    onUseEnglishPress = { isChecked ->
                        preferences.useEnglish = isChecked
                        exitProcess(0)
                    },
                    onSetupLanguagePress = ::launchChangeAppLanguageIntent,
                    showCheckmarksOnSwitches = showCheckmarksOnSwitches,
                    isTopAppBarColorIcon = isTopAppBarColorIcon,
                    isTopAppBarColorTitle = isTopAppBarColorTitle,
                )
            }
        }
    }

    private fun startCustomizationActivity() {
        startCustomizationActivity(
            showAccentColor = false,
            isCollection = false,
            productIdList = productIdList,
            productIdListRu = productIdListRu,
            subscriptionIdList = subscriptionIdList,
            subscriptionIdListRu = subscriptionIdListRu,
            subscriptionYearIdList = subscriptionYearIdList,
            subscriptionYearIdListRu = subscriptionYearIdListRu,
            showAppIconColor = true
        )
    }

    private fun setupCustomizeWidgetColors() {
        Intent(this, WidgetConfigureActivity::class.java).apply {
            putExtra(IS_CUSTOMIZING_COLORS, true)
            startActivity(this)
        }
    }

    private fun setupWhatsNewDialog() {
        WhatsNewDialog(this@SettingsActivity, whatsNewList())
    }
}
