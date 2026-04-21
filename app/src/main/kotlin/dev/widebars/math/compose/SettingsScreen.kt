package dev.goodwy.math.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.goodwy.commons.compose.extensions.BooleanPreviewParameterProvider
import com.goodwy.commons.compose.extensions.MyDevices
import com.goodwy.commons.compose.lists.SimpleColumnScaffold
import com.goodwy.commons.compose.menus.ActionItem
import com.goodwy.commons.compose.menus.ActionMenu
import com.goodwy.commons.compose.menus.OverflowMode
import com.goodwy.commons.compose.settings.SettingsGroup
import com.goodwy.commons.compose.settings.SettingsHorizontalDivider
import com.goodwy.commons.compose.settings.SettingsPreferenceComponent
import com.goodwy.commons.compose.settings.SettingsPurchaseComponent
import com.goodwy.commons.compose.settings.SettingsSwitchComponent
import com.goodwy.commons.compose.theme.AppThemeSurface
import com.goodwy.commons.helpers.isTiramisuPlus
import dev.goodwy.math.BuildConfig
import com.goodwy.commons.R as CommonR
import dev.goodwy.math.R
import kotlinx.collections.immutable.toImmutableList
import com.goodwy.strings.R as StringsR

@Composable
internal fun SettingsScreen(
    goBack: () -> Unit,
    customizeColors: () -> Unit,
    customizeWidgetColors: () -> Unit,
    preventPhoneFromSleeping: Boolean,
    onPreventPhoneFromSleeping: (Boolean) -> Unit,
    vibrateOnButtonPressFlow: Boolean,
    onVibrateOnButtonPressFlow: (Boolean) -> Unit,
    isPro: Boolean,
    onPurchaseClick: () -> Unit,
    onAboutClick: () -> Unit,
    onWhatsNewClick: () -> Unit,
    isUseEnglishEnabled: Boolean,
    isUseEnglishChecked: Boolean,
    onUseEnglishPress: (Boolean) -> Unit,
    onSetupLanguagePress: () -> Unit,
    showCheckmarksOnSwitches: Boolean,
    displayLanguage: String,
    isTopAppBarColorIcon: Boolean,
    isTopAppBarColorTitle: Boolean,
) {
    SimpleColumnScaffold(
        title = stringResource(id = CommonR.string.settings),
        isTopAppBarColorIcon = isTopAppBarColorIcon,
        isTopAppBarColorTitle = isTopAppBarColorTitle,
        goBack = goBack,
        actions = {
            val actionMenus = remember {
                listOf(
                    ActionItem(
                        CommonR.string.whats_new,
                        icon = Icons.Rounded.History,
                        doAction = onWhatsNewClick,
                        overflowMode = OverflowMode.NEVER_OVERFLOW),
                ).toImmutableList()
            }

            var isMenuVisible by remember { mutableStateOf(false) }
            val iconColor =
                if (isTopAppBarColorIcon) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface

            ActionMenu(
                items = actionMenus,
                isMenuVisible = isMenuVisible,
                onMenuToggle = { isMenuVisible = it },
                iconsColor = iconColor
            )
        }
    ) {
        // Support project
        var shouldShake by remember { mutableStateOf(false) }
        if (!isPro) {
            SettingsPurchaseComponent(
                onPurchaseClick = onPurchaseClick,
                enabledShake = shouldShake,
                onShakeFinished = {
                    shouldShake = false
                }
            )
        }

        // Appearance
        SettingsGroup(title = {
            Text(text = stringResource(id = StringsR.string.pref_category_appearance).uppercase())
        }) {
            SettingsPreferenceComponent(
                label = stringResource(id = CommonR.string.customize_colors),
                doOnPreferenceClick = {
                    customizeColors()
                },
                preferenceLabelColor = MaterialTheme.colorScheme.onSurface,
                showChevron = true,
            )
            SettingsHorizontalDivider(thickness = 2.dp)
            SettingsPreferenceComponent(
                label = stringResource(id = CommonR.string.customize_widget_colors),
                doOnPreferenceClick = customizeWidgetColors,
                showChevron = true,
            )
        }

        // General
        SettingsGroup(title = {
            Text(text = stringResource(id = CommonR.string.general_settings).uppercase())
        }) {
            SettingsSwitchComponent(
                label = stringResource(id = R.string.vibrate_on_button_press),
                initialValue = vibrateOnButtonPressFlow,
                onChange = onVibrateOnButtonPressFlow,
                showCheckmark = showCheckmarksOnSwitches
            )
            SettingsHorizontalDivider(thickness = 2.dp)
            SettingsSwitchComponent(
                label = stringResource(id = CommonR.string.prevent_phone_from_sleeping),
                initialValue = preventPhoneFromSleeping,
                onChange = onPreventPhoneFromSleeping,
                showCheckmark = showCheckmarksOnSwitches
            )
            if (isUseEnglishEnabled) {
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsSwitchComponent(
                    label = stringResource(id = CommonR.string.use_english_language),
                    initialValue = isUseEnglishChecked,
                    onChange = onUseEnglishPress,
                    showCheckmark = showCheckmarksOnSwitches
                )
            }
            if (isTiramisuPlus()) {
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsPreferenceComponent(
                    label = stringResource(id = CommonR.string.language),
                    value = displayLanguage,
                    doOnPreferenceClick = onSetupLanguagePress,
                )
            }
        }

        // Other
        SettingsGroup(title = {
            Text(text = stringResource(id = CommonR.string.other).uppercase())
        }) {
            if (isPro) {
                SettingsPreferenceComponent(
                    label = stringResource(id = StringsR.string.tip_jar),
                    doOnPreferenceClick = onPurchaseClick,
                    showChevron = true,
                )
                SettingsHorizontalDivider(thickness = 2.dp)
            }
            val flavorName = BuildConfig.FLAVOR
            val storeDisplayName = when (flavorName) {
                "gplay" -> "Google Play"
                "foss" -> "FOSS"
                "rustore" -> "RuStore"
                else -> "Huawei"
            }
            val versionName = BuildConfig.VERSION_NAME
            val fullVersionText = "$versionName ($storeDisplayName)"

            SettingsPreferenceComponent(
                label = stringResource(id = CommonR.string.about),
                value = fullVersionText,
                doOnPreferenceClick = onAboutClick,
                showChevron = true,
            )
        }
    }
}

@MyDevices
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(BooleanPreviewParameterProvider::class) showCheckmarksOnSwitches: Boolean
) {
    AppThemeSurface {
        SettingsScreen(
            goBack = {},
            customizeColors = {},
            customizeWidgetColors = {},
            preventPhoneFromSleeping = false,
            onPreventPhoneFromSleeping = {},
            vibrateOnButtonPressFlow = false,
            onVibrateOnButtonPressFlow = {},
            isPro = false,
            onPurchaseClick = {},
            onAboutClick = {},
            onWhatsNewClick = {},
            isUseEnglishEnabled = false,
            isUseEnglishChecked = false,
            onUseEnglishPress = {},
            onSetupLanguagePress = {},
            displayLanguage = "English",
            showCheckmarksOnSwitches = showCheckmarksOnSwitches,
            isTopAppBarColorIcon = false,
            isTopAppBarColorTitle = false,
        )
    }
}
