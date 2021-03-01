package com.havaylab.screensaver

import android.service.dreams.DreamService
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.res.ResourcesCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class MainActivity : DreamService() {

    lateinit var imageView: ImageView
    lateinit var videoView: VideoView
    lateinit var textView: TextView
    private lateinit var contentRunnable: Runnable
    private lateinit var timeRunnable: Runnable

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.activity_main)

        isInteractive = false

        isFullscreen = true

        imageView = findViewById(R.id.image_view)
        videoView = findViewById(R.id.video_view)
        textView = findViewById(R.id.text_view)

        timeRunnable = object : Runnable {
            val timeUpdateInterval = TimeUnit.SECONDS.toMillis(1)

            override fun run() {
                val currentTime = LocalDateTime.now()
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val formattedDateTime = currentTime.format(formatter)
                textView.text = formattedDateTime

                textView.postDelayed(this, timeUpdateInterval)

            }
        }

        contentRunnable = object : Runnable {
            var contentUpdateInterval = TimeUnit.MINUTES.toMillis(1)

            var index: Int = 0
            var stringArray = resources.getStringArray(R.array.contents).toMutableSet().shuffled()


            override fun run() {
                if (index == stringArray.size) {
                    stringArray = stringArray.toMutableSet().shuffled()
                    index = 0
                }
                val resourceName: String = stringArray[index]
                index++

                if (resourceName.endsWith(".jpg")) {
                    val resourceId: Int = resources.getIdentifier(
                        resourceName.substringBefore("."), "drawable",
                        applicationContext.packageName
                    )
                    imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            resourceId,
                            theme
                        )
                    )

                    videoView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE

                    imageView.postDelayed(this, contentUpdateInterval)
                } else if (resourceName.endsWith(".mp4")) {
                    videoView.setVideoPath(
                        "android.resource://$packageName/raw/" + resourceName.substring(
                            0,
                            resourceName.indexOf(".mp4")
                        )
                    )
                    videoView.requestFocus()
                    videoView.start()

                    imageView.visibility = View.GONE
                    videoView.visibility = View.VISIBLE


                    videoView.setOnCompletionListener {
                        videoView.postDelayed(this, 0)
                    }
                }
            }
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        contentRunnable.run()
        timeRunnable.run()
    }

}