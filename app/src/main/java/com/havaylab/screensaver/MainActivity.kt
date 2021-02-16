package com.havaylab.screensaver

import android.service.dreams.DreamService
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : DreamService() {

    lateinit var imageView: ImageView
    lateinit var textView: TextView
    private lateinit var imageRunnable: Runnable
    private lateinit var timeRunnable: Runnable

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.activity_main)

        isInteractive = false

        isFullscreen = true

        imageView = findViewById(R.id.image_view)
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

        imageRunnable = object : Runnable {
            var imageUpdateInterval = TimeUnit.MINUTES.toMillis(5)

            override fun run() {
                val stringArray = resources.getStringArray(R.array.images);
                val randomNumber: Int = Random().nextInt(stringArray.size)
                val resourceId: Int = resources.getIdentifier(
                    stringArray[randomNumber].substringBefore("."), "drawable",
                    applicationContext.packageName
                )
                imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        resourceId,
                        theme
                    )
                )

                imageView.postDelayed(this, imageUpdateInterval)

            }
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        imageRunnable.run()
        timeRunnable.run()
    }

}