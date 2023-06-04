package com.soli.newframeapp.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.soli.libcommon.base.BaseFragment
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.net.DataType
import com.soli.libcommon.net.download.FileProgressListener
import com.soli.libcommon.view.loading.LoadingType
import com.soli.newframeapp.databinding.ActivityVoiceInputBinding
import org.jetbrains.anko.AnkoLogger
import org.json.JSONObject
import java.io.IOException

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

/**
 *
 * @author liuwanli
 * @Time 2023/5/25 14:03
 */
class AudioRecordFragment : BaseFragment<ActivityVoiceInputBinding>(), AnkoLogger {

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var mStartPlaying = true
    private var mStartRecording = true

    private val serverUrl = "http://180.184.103.46:53045"

    private val mediaSession: MediaSessionCompat by lazy { createMediaSession() }
    private val mediaSessionConnector: MediaSessionConnector by lazy {
        createMediaSessionConnector()
    }
    private val playerState by lazy { PlayerState() }
    private lateinit var playerHolder: PlayerHolder


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) activity?.finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
//        fileName =
//            "${ctx!!.externalCacheDir!!.absolutePath}/audio_teset" + ".3gp"
        fileName =
            "${ctx!!.externalCacheDir!!.absolutePath}/audio_" + System.currentTimeMillis() + ".3gp"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
            uploadFile(fileName)
        }
        recorder = null
    }

    override fun initView() {
        binding.videoView.hideController()
        requireActivity().volumeControlStream = AudioManager.STREAM_MUSIC
        createMediaSession()
        createPlayer()
    }

    override fun initListener() {
        binding.startPlaying.visibility = View.GONE
        binding.startPlaying.text = "Start playing"
        binding.startPlaying.setOnClickListener {
            onPlay(mStartPlaying)
            binding.startPlaying.text = when (mStartPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        binding.startRecord.text = "Start recording"
        binding.startRecord.setOnClickListener {
            onRecord(mStartRecording)
            binding.startRecord.text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }
    }

    override fun initData() {
    }

    override fun onPause() {
        super.onPause()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
        stopPlayer()
        binding.videoView.onPause()
    }


    private fun getInitData() {
        showProgress()

        ApiHelper.build {
            baseUrl = serverUrl
            bodyType = DataType.STRING
            url = "api/init"
        }.get<String> {
            dismissProgress()
            val result = it.fullData
            if (!TextUtils.isEmpty(result)) {
                val jsonObject = JSONObject(result)
                playerHolder.prologue = jsonObject.optString("prologue")
                playerHolder.scene = jsonObject.optString("scene")
                firstPlay()
            }
        }
    }

    private fun uploadFile(file: String) {
        showProgress(type = LoadingType.TypeDialog)
        ApiHelper.build {
            baseUrl = serverUrl
            bodyType = DataType.STRING
            fileUrl = file
            url = "api/task"
        }.uploadFile({
            dismissProgress()
            val result = it.fullData
            Log.d("uploadFile", result)
            if (!TextUtils.isEmpty(result)) {
                val jsonObject = JSONObject(result)
                val question = jsonObject.optString("question")
                val answer = jsonObject.optString("answer")
                val task = jsonObject.optString("task")
                binding.answerText.text = answer
                binding.questionText.text = question
                startPlayer(task)
            }
        }, object : FileProgressListener {
            override fun progress(
                progress: Int,
                bytes: Long,
                updateBytes: Long,
                fileSize: Long,
                isDone: Boolean
            ) {
                Log.d("uploadFile", "progress $progress  isDone $isDone")
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getInitData()
            binding.videoView.onResume()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    // MediaSession related functions.
    private fun createMediaSession(): MediaSessionCompat =
        MediaSessionCompat(requireActivity(), requireActivity().packageName)

    private fun createMediaSessionConnector(): MediaSessionConnector =
        MediaSessionConnector(mediaSession).apply {
            // If QueueNavigator isn't set, then mediaSessionConnector will not handle following
            // MediaSession actions (and they won't show up in the minimized PIP activity):
            // [ACTION_SKIP_PREVIOUS], [ACTION_SKIP_NEXT], [ACTION_SKIP_TO_QUEUE_ITEM]
            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player, windowIndex: Int
                ): MediaDescriptionCompat {
                    return mediaCatalog[windowIndex]
                }
            })
        }


    // MediaSession related functions.
    private fun activateMediaSession() {
        // Note: do not pass a null to the 3rd param below, it will cause a NullPointerException.
        // To pass Kotlin arguments to Java varargs, use the Kotlin spread operator `*`.
        mediaSessionConnector.setPlayer(playerHolder.audioFocusPlayer)
        mediaSession.isActive = true
    }

    private fun deactivateMediaSession() {
        mediaSessionConnector.setPlayer(null)
        mediaSession.isActive = false
    }

    private fun releaseMediaSession() {
        mediaSession.release()
    }

    // ExoPlayer related functions.
    private fun createPlayer() {
        playerHolder = PlayerHolder(requireActivity(), playerState, binding.videoView)
    }

    private fun firstPlay() {
        playerHolder.fristPlay()
        activateMediaSession()
    }

    private fun startPlayer(url: String?) {
        playerHolder.fristPlay(url!!)
        activateMediaSession()
    }

    private fun stopPlayer() {
        playerHolder.stop()
        deactivateMediaSession()
    }

    private fun releasePlayer() {
        playerHolder.release()
        releaseMediaSession()
    }
}
