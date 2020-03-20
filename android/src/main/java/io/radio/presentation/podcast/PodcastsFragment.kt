package io.radio.presentation.podcast

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.core.view.isVisible
import androidx.palette.graphics.Palette
import io.radio.R
import io.radio.di
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routeDetails
import io.radio.shared.common.*
import io.radio.shared.presentation.fragment.BaseFragment
import io.radio.shared.presentation.imageloader.ImageLoaderParams
import io.radio.shared.presentation.imageloader.loadImageDrawable
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.presentation.view.SizeScrollOffsetListener
import io.radio.shared.presentation.view.addScrollOffsetListener
import io.radio.shared.presentation.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts) {

    private val viewModel: PodcastsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@PodcastsFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val podcastsAdapter = PodcastsAdapter {
            viewModel.onPodcastSelected(it)
        }
        radioPodcastRecycleView.adapter = podcastsAdapter
        radioPodcastRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        viewModel.openPodcastFlow
            .onEach {
                when (it) {
                    is State.Success -> {
                        it.result.performContentIfNotHandled { params ->
                            viewScope.launch {
                                val drawable = withContext(IoDispatcher) {

                                    requireContext().loadImageDrawable(
                                        params.cover,
                                        ImageLoaderParams(),
                                        Size(100, 100)
                                    )

                                } as BitmapDrawable


                                val generate = Palette.from(drawable.bitmap).generate()

                                val dark = generate.isDark()
                                routeDetails(
                                    params.copy(
                                        headerColor = getDarkerColor(generate.getDominantColor(Color.BLACK)),
                                        toolbarColor = if (dark == IS_DARK) requireContext().getAttrColor(
                                            R.attr.colorPrimary
                                        ) else requireContext().getAttrColor(R.attr.colorAccent)
                                    )
                                )

                            }


                        }
                    }
                    is State.Fail -> {

                    }
                    State.Loading -> {

                    }
                }
            }.launchIn(viewScope)

        viewModel.podcastsFlow.onEach {
            Logger.d("Podcasts new state: $it")
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
        }.launchIn(viewScope)
    }

    override fun onResume() {
        super.onResume()
        radioPodcastRecycleView.updateScrollOffsetListener()
    }

}