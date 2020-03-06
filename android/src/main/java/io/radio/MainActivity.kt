package io.radio

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import io.radio.shared.platformName

class MainActivity : FragmentActivity(R.layout.main_activity){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, platformName(), Toast.LENGTH_LONG).show()
    }

}