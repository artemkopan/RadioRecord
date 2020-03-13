package io.radio.presentation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import io.radio.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main_nav_host.findNavController().setGraph(R.navigation.main_graph, intent.extras)
    }

}