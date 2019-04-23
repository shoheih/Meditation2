package net.minpro.meditation2.model

import android.content.Context
import net.minpro.meditation2.MyApplication
import net.minpro.meditation2.R
import net.minpro.meditation2.util.LevelId


class UserSettingsRepository {

    private val context = MyApplication.appContext
    private val pref = context.getSharedPreferences(
        UserSettings.PREF_USERSETTINGS_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun loadUserSettings(): UserSettings {
        return UserSettings(
            levelId = pref.getInt(UserSettingsPrefKey.LEVEL_ID.name, LevelId.EASY),
            levelName = context.getString(
                pref.getInt(
                    UserSettingsPrefKey.LEVEL_NAME_STR_ID.name,
                    R.string.level_easy_header
                )
            ),
            themeId = pref.getInt(UserSettingsPrefKey.THEME_ID.name, 0),
            themeName = context.getString(
                pref.getInt(
                    UserSettingsPrefKey.THEME_NAME_STR_ID.name,
                    R.string.theme_silent
                )
            ),
            themeResId = pref.getInt(
                UserSettingsPrefKey.THEME_RES_ID.name,
                R.drawable.pic_nobgm
            ),
            themeSoundId = pref.getInt(UserSettingsPrefKey.THEME_SOUND_ID.name, 0),
            time = pref.getInt(UserSettingsPrefKey.TIME.name, 30)
        )
    }

    fun setLevel(selectedItemId: Int): String {
        editor.putInt(UserSettingsPrefKey.LEVEL_ID.name, selectedItemId).commit()
        val levelNameStrId = when (selectedItemId) {
            0 -> R.string.level_easy_header
            1 -> R.string.level_normal_header
            2 -> R.string.level_mid_header
            3 -> R.string.level_high_header
            else -> {0}
        }
        editor.putInt(UserSettingsPrefKey.LEVEL_NAME_STR_ID.name, levelNameStrId).commit()
        return loadUserSettings().levelName
    }
}