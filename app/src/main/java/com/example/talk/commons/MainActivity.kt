package com.example.talk.commons

import android.content.Intent
import android.os.Bundle
import com.example.talk.R
import com.example.talk.databinding.ActivityMainBinding
import com.example.talk.loginactivity.view.LoginActivity

class MainActivity : TalkBaseActivity<ActivityMainBinding>() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView(R.layout.activity_main)
        with(binding) {
            lifecycleOwner = this@MainActivity
        }

        startActivity(Intent(this,LoginActivity::class.java))
    }
}
