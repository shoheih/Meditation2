package net.minpro.meditation2.view.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.minpro.meditation2.R
import net.minpro.meditation2.service.MusicServiceHelper
import net.minpro.meditation2.util.FragmentTag
import net.minpro.meditation2.util.PlayStatus
import net.minpro.meditation2.view.dialog.LevelSelectDialog
import net.minpro.meditation2.view.dialog.ThemeSelectDialog
import net.minpro.meditation2.view.dialog.TimeSelectDialog
import net.minpro.meditation2.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var musicServiceHelper: MusicServiceHelper? = null

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

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        observeViewModel()

        btmNavi.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_select_level -> {
                    LevelSelectDialog().show(supportFragmentManager, FragmentTag.LEVEL_SELECT.name)
                    true
                }
                R.id.item_select_theme -> {
                    ThemeSelectDialog().show(supportFragmentManager, FragmentTag.THEME_SELECT.name)
                    true
                }
                R.id.item_select_time -> {
                    TimeSelectDialog().show(supportFragmentManager, FragmentTag.TIME_SELECT.name)
                    true
                }
                else -> {
                    false
                }
            }
        }

        musicServiceHelper = MusicServiceHelper(this)
        musicServiceHelper?.bindService()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        musicServiceHelper?.stopBgm()
        finish()
    }

    private fun observeViewModel() {
        viewModel.playStatus.observe(this, Observer { status ->
            when(status) {
                PlayStatus.BEFORE_START -> {
                    btmNavi.visibility = View.VISIBLE
                }
                PlayStatus.ON_START -> {
                    btmNavi.visibility = View.INVISIBLE
                }
                PlayStatus.RUNNING -> {
                    btmNavi.visibility = View.INVISIBLE
                    musicServiceHelper?.startBgm()
                }
                PlayStatus.PAUSE -> {
                    btmNavi.visibility = View.INVISIBLE
                    musicServiceHelper?.stopBgm()
                }
                PlayStatus.END -> {
                    btmNavi.visibility = View.VISIBLE
                    musicServiceHelper?.stopBgm()
                    musicServiceHelper?.ringFinalGong()
                }
            }
        })

        viewModel.volume.observe(this, Observer { volume ->
            musicServiceHelper?.setVolume(volume!!)
        })
    }
}
