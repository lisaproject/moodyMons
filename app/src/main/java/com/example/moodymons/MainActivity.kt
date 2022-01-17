package com.example.moodymons

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.moodymons.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
//        //download firebase model
//        val conditions = CustomModelDownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        FirebaseModelDownloader.getInstance()
//            .getModel("Speech-Emotion-Classifier", DownloadType.LOCAL_MODEL, conditions)
//            .addOnCompleteListener {
//                // Download complete. Depending on your app, you could enable the ML
//                // feature, or switch from the local model to the remote model, etc.
//            }
        var PERMISSION_CODE = 101
        var PERMISSION_INTERNET = Manifest.permission.INTERNET
        var PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        var PERMISSION_READ_EXTERNAL_STORAGE= Manifest.permission.READ_EXTERNAL_STORAGE
        var PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if ((ActivityCompat.checkSelfPermission(
                this,
                PERMISSION_RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(
            this,
            PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(
        this,
        PERMISSION_RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED))
        { //Permission Granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(PERMISSION_INTERNET,PERMISSION_RECORD_AUDIO,PERMISSION_WRITE_EXTERNAL_STORAGE,PERMISSION_READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        }
        setContentView(binding.root)

        supportActionBar?.hide()
    }
}