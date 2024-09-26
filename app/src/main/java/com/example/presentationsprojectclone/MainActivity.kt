package com.example.presentationsprojectclone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        val handler = android.os.Handler()
        handler.postDelayed({
            val intent = Intent(this@MainActivity, login_with_email_page::class.java)
            startActivity(intent)
            finish()
        }, 4000)

        var videoVIew = findViewById<VideoView>(R.id.startup_video)
        var packageName = "android.resource://" + getPackageName() + "/" + R.raw.startup_video
        val uri = Uri.parse(packageName)
        videoVIew.setVideoURI(uri)
        videoVIew.start()
    }
}