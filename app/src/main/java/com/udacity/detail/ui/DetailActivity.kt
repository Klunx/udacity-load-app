package com.udacity.detail.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity
import com.udacity.R
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.content_detail.file_name_value
import kotlinx.android.synthetic.main.content_detail.status_value
import kotlinx.android.synthetic.main.content_detail.ok

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        initializeScreen()

        ok.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun initializeScreen() {
        val extras = intent.extras
        extras?.let { bundle ->
            val status = bundle.getString("status")
            val repo = bundle.getString("repository")
            status?.let {
                validateStatus(it)
                status_value.text = it
            }
            repo?.let { file_name_value.text = it }
        }
    }

    private fun validateStatus(status: String) {
        status_value.apply {
            when (status) {
                getString(R.string.success) -> setTextColor(getColor(R.color.colorPrimaryDark))
                getString(R.string.fail) -> setTextColor(getColor(R.color.colorAccent))
            }
        }
    }
}
