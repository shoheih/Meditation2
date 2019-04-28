package net.minpro.meditation2.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import net.minpro.meditation2.R
import net.minpro.meditation2.data.ThemeData
import net.minpro.meditation2.model.UserSettings
import net.minpro.meditation2.model.UserSettingsRepository
import net.minpro.meditation2.util.PlayStatus
import java.util.*
import kotlin.concurrent.schedule

class MainViewModel(val context: Application): AndroidViewModel(context) {

    var msgUpperSmall = MutableLiveData<String>()
    var msgLowerLarge = MutableLiveData<String>()
    var themePicFileResId = MutableLiveData<Int>()
    var txtTheme = MutableLiveData<String>()
    var txtLevel = MutableLiveData<String>()

    var remainedTimeSeconds = MutableLiveData<Int>()
    var displayTimeSeconds = MutableLiveData<String>()

    var playStatus = MutableLiveData<Int>()

    var volume = MutableLiveData<Int>()

    private val userSettingsRepository = UserSettingsRepository()
    private lateinit var userSettings: UserSettings

    private var timerMeditation: Timer? = null

    //呼吸間隔
    private val inhaleInterval = 4
    private var holdInterval = 0
    private var exhaleInterval = 0
    private var totalInterval = 0

    fun initParameters() {
        userSettings = userSettingsRepository.loadUserSettings()
        msgUpperSmall.value = ""
        msgLowerLarge.value = ""
        themePicFileResId.value = userSettings.themeResId
        txtTheme.value = userSettings.themeName
        txtLevel.value = userSettings.levelName
        remainedTimeSeconds.value = userSettings.time * 60
        displayTimeSeconds.value = changeTimeFormat(remainedTimeSeconds.value!!)
        playStatus.value = PlayStatus.BEFORE_START
    }

    private fun changeTimeFormat(timeSeconds: Int): String? {
        val minutes = timeSeconds / 60
        val minutesString = if (minutes < 10) "0" + minutes.toString() else minutes.toString()
        val seconds = timeSeconds - (minutes * 60)
        val secondsString = if (seconds < 10) "0" + seconds.toString() else seconds.toString()
        return minutesString + " : " + secondsString
    }

    fun setLevel(selectedItemId: Int) {
        txtLevel.value = userSettingsRepository.setLevel(selectedItemId)
    }

    fun setTime(selectedItemId: Int) {
        remainedTimeSeconds.value = userSettingsRepository.setTime(selectedItemId) * 60
        displayTimeSeconds.value = changeTimeFormat(remainedTimeSeconds.value!!)
    }

    fun setTheme(themeData: ThemeData) {
        userSettingsRepository.setTheme(themeData)
        txtTheme.value = userSettingsRepository.loadUserSettings().themeName
        themePicFileResId.value = userSettingsRepository.loadUserSettings().themeResId
    }

    fun changeStatus() {
        val status = playStatus.value
        when(status) {
            PlayStatus.BEFORE_START -> {
                playStatus.value = PlayStatus.ON_START
            }
            PlayStatus.RUNNING -> {
                playStatus.value = PlayStatus.PAUSE
            }
            PlayStatus.PAUSE -> {
                playStatus.value = PlayStatus.RUNNING
            }
        }
    }

    fun countDownBeforeStart() {

        msgUpperSmall.value = context.resources.getString(R.string.starts_in)
        var timeRemained = 3
        msgLowerLarge.value = timeRemained.toString()
        val timer = Timer()
        timer.schedule(1000, 1000) {
            if (timeRemained > 1) {
                timeRemained -= 1
                msgLowerLarge.postValue(timeRemained.toString())
            } else {
                playStatus.postValue(PlayStatus.RUNNING)
                timeRemained = 0
                timer.cancel()
            }
        }
    }

    fun startMeditation() {
        holdInterval = setHoldInterval()
        exhaleInterval = setExhaleInterval()
        totalInterval = inhaleInterval + holdInterval + exhaleInterval

        remainedTimeSeconds.value = adjustRemainedTime(remainedTimeSeconds.value, totalInterval)
        displayTimeSeconds.value = changeTimeFormat(remainedTimeSeconds.value!!)

        msgUpperSmall.value = context.getString(R.string.inhale)
        msgLowerLarge.value = inhaleInterval.toString()

        clockMeditation()
    }

    private fun clockMeditation() {
        var timeElapsed = 0

        timerMeditation = Timer()
        timerMeditation?.schedule(1000, 1000) {
            val tempTime = remainedTimeSeconds.value!! - 1
            remainedTimeSeconds.postValue(tempTime)
            displayTimeSeconds.postValue(changeTimeFormat(tempTime))
            if (remainedTimeSeconds.value!! <= 1) {
                msgUpperSmall.postValue("")
                msgLowerLarge.postValue(context.resources.getString(R.string.meiso_finish))
                playStatus.postValue(PlayStatus.END)
                cancelTimer()
                return@schedule
            }
            //TODO 経過時間に応じて出す文言を変える
            timeElapsed = if (timeElapsed >= totalInterval - 1) 0 else timeElapsed + 1
            setDisplayText(timeElapsed)
        }

    }

    private fun setDisplayText(timeElapsed: Int) {
        if (timeElapsed >= 0 && timeElapsed < inhaleInterval) {
            msgUpperSmall.postValue(context.resources.getString(R.string.inhale))
            msgLowerLarge.postValue((inhaleInterval - timeElapsed).toString())
        } else if (timeElapsed < inhaleInterval + holdInterval) {
            msgUpperSmall.postValue(context.resources.getString(R.string.hold))
            msgLowerLarge.postValue((inhaleInterval + holdInterval - timeElapsed).toString())
        } else {
            msgUpperSmall.postValue(context.resources.getString(R.string.exhale))
            msgLowerLarge.postValue((totalInterval - timeElapsed).toString())
        }
    }

    private fun cancelTimer() {
        timerMeditation?.cancel()
    }

    private fun adjustRemainedTime(remainedTime: Int?, totalInterval: Int): Int? {
        val remainder = remainedTime!! % totalInterval
        return if (remainder > (totalInterval / 2)) {
            remainedTime + (totalInterval - remainder)
        } else {
            remainedTime - remainder
        }

    }

    private fun setHoldInterval(): Int {
        val levelId = userSettingsRepository.loadUserSettings().levelId
        return when(levelId) {
            0 -> 4
            1 -> 4
            2 -> 8
            3 -> 16
            else -> 0
        }
    }

    private fun setExhaleInterval(): Int {
        val levelId = userSettingsRepository.loadUserSettings().levelId
        return when(levelId) {
            0 -> 4
            1 -> 8
            2 -> 8
            3 -> 8
            else -> 0
        }
    }

    fun pauseMeditation() {
        cancelTimer()

    }

    fun finishMeditation() {
        cancelTimer()
        playStatus.value = PlayStatus.BEFORE_START
        remainedTimeSeconds.value = userSettingsRepository.loadUserSettings().time * 60
        displayTimeSeconds.value = changeTimeFormat(remainedTimeSeconds.value!!)
        msgUpperSmall.value = ""
        msgLowerLarge.value = context.resources.getString(R.string.meiso_finish)
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }

    fun setVolume(progress: Int) {
        volume.value = progress
    }
}