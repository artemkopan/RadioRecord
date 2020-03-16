package io.radio.presentation.podcast

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import io.radio.R
import io.radio.di
import io.radio.shared.presentation.State
import io.radio.shared.presentation.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_podcasts.*

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts) {

    private val viewModel: PodcastsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@PodcastsFragment) }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val podcastsAdapter = PodcastsAdapter()
        radioPodcastRecycleView.adapter = podcastsAdapter


        viewModel.podcastsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is State.Success -> {
                    progressBar.isVisible = false
                    radioPodcastRecycleView.isVisible = true
                    podcastsAdapter.submitList(it.result)
                }
                is State.Fail -> {
                    progressBar.isVisible = false
                    radioPodcastRecycleView.isVisible = false
                    TODO()
                }
                State.Loading -> {
                    progressBar.isVisible = true
                    radioPodcastRecycleView.isVisible = true
                }
            }
        })
    }

}