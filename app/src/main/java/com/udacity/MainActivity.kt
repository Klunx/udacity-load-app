package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.custom_button
import kotlinx.android.synthetic.main.content_main.selected_opt

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    /*
    private lateinit var action: NotificationCompat.Action
     */
    private lateinit var selectedRadioButton: RadioButton
    private lateinit var selectedRepo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val selectedId = selected_opt.checkedRadioButtonId
            if (selectedId != -1) {
                selectedRadioButton = findViewById(selectedId)
                selectedRepo = selectedRadioButton.text.toString()
                when (selectedId) {
                    R.id.radio_glide -> download(GLIDE_URL)
                    R.id.radio_starter -> download(UDACITY_URL)
                    R.id.radio_retrofit -> download(RETROFIT_URL)
                }
            } else {
                Toast.makeText(applicationContext, getString(R.string.empty_selection), Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    // https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != -1L) {
                notificationManager.sendNotification(applicationContext, getString(R.string.success), selectedRepo)
            } else {
                notificationManager.sendNotification(applicationContext, getString(R.string.fail), selectedRepo)
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        getDownloadStatus(downloadManager)

    }

    private fun getDownloadStatus(downloadManager: DownloadManager) {
        try {
            //https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca
            var haveNotFinishDownload = true
            var progress: Int

            while (haveNotFinishDownload) {
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_RUNNING -> {
                            val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                //progress = (((downloaded * 100L) / total).toInt())
                            }
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            //progress = 100
                            haveNotFinishDownload = false
                        }

                        DownloadManager.STATUS_FAILED -> {
                            haveNotFinishDownload = false
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }

    }
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Load repository"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
