package net.minpro.meditation2.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import net.minpro.meditation2.MyApplication
import net.minpro.meditation2.R
import net.minpro.meditation2.model.UserSettingsRepository
import net.minpro.meditation2.util.NO_BGM

class MusicService : Service() {

    private val binder: IBinder = MusicBinder()

    inner class MusicBinder: Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    //ExoPlayerを使うための道具
    private var bellsPlayer: SimpleExoPlayer? = null
    private var bgmPlayer: SimpleExoPlayer? = null

    //BGMを読み込むための道具
    private var rawResourceDataSourceBell: RawResourceDataSource? = null
    private var rawResourceDataSourceBgm: RawResourceDataSource? = null

    private var soundPool: SoundPool? = null
    private var soundIdGong: Int = 0

    private lateinit var audioManager: AudioManager
    private var maxVolume: Int = 0

    override fun onBind(intent: Intent): IBinder {
        initExoPlayer()
        initSoundPool()
        initAudiomanager()
        return binder
    }

    private fun initExoPlayer() {
        bellsPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        bgmPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        rawResourceDataSourceBell = RawResourceDataSource(this)
        rawResourceDataSourceBgm = RawResourceDataSource(this)
    }

    private fun initSoundPool() {
        soundPool = SoundPool.Builder().setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
            .setMaxStreams(1)
            .build()
        soundIdGong = soundPool?.load(this, R.raw.gong_meiso_end, 1)!!
    }

    private fun initAudiomanager() {
        audioManager = MyApplication.appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        soundPool?.release()
        releaseExoPlayer()
        return super.onUnbind(intent)
    }

    fun startBgm() {
        val userSettingsRepository = UserSettingsRepository()
        val bellsSoundId = when(userSettingsRepository.loadUserSettings().levelId){
            0 -> R.raw.bells_easy
            1 -> R.raw.bells_normal
            2 -> R.raw.bells_mid
            3 -> R.raw.bells_advanced
            else -> 0
        }
        val themeSoundId = userSettingsRepository.loadUserSettings().themeSoundId
        bellsPlayer?.let {
            startSound(bellsPlayer!!, bellsSoundId, rawResourceDataSourceBell!!)
        }
        if (themeSoundId != NO_BGM) bgmPlayer?.let {
            startSound(bgmPlayer!!, themeSoundId, rawResourceDataSourceBgm!!)
        }
    }

    private fun startSound(exoPlayer: SimpleExoPlayer, soundId: Int, rawResourceDataSource: RawResourceDataSource){
        val dataSourceUri = RawResourceDataSource.buildRawResourceUri(soundId)
        val dataSpec = DataSpec(dataSourceUri)
        rawResourceDataSource.open(dataSpec)
        val dataSourceFactory = DataSource.Factory { return@Factory  rawResourceDataSource }
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(Mp3Extractor.FACTORY)
            .createMediaSource(dataSourceUri)
        exoPlayer.apply {
            prepare(mediaSource)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    fun stopBgm() {
        bellsPlayer?.stop()
        bgmPlayer?.stop()
    }

    fun setVolume(volume: Int) {
        val adjustVolume = (maxVolume.toDouble() * (volume.toDouble() / 100.toDouble())).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, adjustVolume, 0)
    }

    fun ringFinalGong() {
        soundPool?.play(soundIdGong, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private fun releaseExoPlayer(){
        bellsPlayer?.release()
        bgmPlayer?.release()
        rawResourceDataSourceBgm?.close()
        rawResourceDataSourceBell?.close()
    }

}
