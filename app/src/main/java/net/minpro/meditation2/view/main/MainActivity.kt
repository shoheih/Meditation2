package net.minpro.meditation2.view.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.minpro.meditation2.R
import net.minpro.meditation2.util.FragmentTag
import net.minpro.meditation2.view.dialog.LevelSelectDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.screen_container,
                        MainFragment()
                    )
                    .commit()
        }

        btmNavi.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_select_level -> {
                    LevelSelectDialog().show(supportFragmentManager, FragmentTag.LEVEL_SELECT.name)
                    true
                }
                R.id.item_select_theme -> {
                    true
                }
                R.id.item_select_time -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}