package io.shared.presentation.podcast.details

import io.shared.core.CoroutineScopeProvider
import io.shared.core.extensions.JobRunner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackPositionScrollerHelper(
    private val coroutineScopeProvider: CoroutineScopeProvider,
    private val findVisiblePositionsBounds: () -> Pair<Int, Int>,
    private val onShowScrollButton: (Boolean) -> Unit,
    private val onScrollTo: (Int) -> Unit
) {

    var wasScrolledByUser: Boolean = false

    private val scrollButtonJob = JobRunner()
    private var currentTrack = -1

    fun onTrackChanged(currentTrack: Int) {
        this.currentTrack = currentTrack
        val (firstVisiblePosition, lastVisiblePosition) = findVisiblePositionsBounds()

        if (currentTrack !in firstVisiblePosition..lastVisiblePosition) {
            if (wasScrolledByUser) {
                onShowScrollButton(true)
                scrollButtonJob.runAndCancelPrevious {
                    coroutineScopeProvider.scope.launch {
                        delay(SCROLL_BUTTON_DELAY)
                        onShowScrollButton(false)
                    }
                }
            } else {
                onShowScrollButton(false)
                onScrollTo(currentTrack)
            }
        }
    }

    fun onScrollButtonClicked() {
        if (currentTrack >= 0) {
            onShowScrollButton(false)
            onScrollTo(currentTrack)
        }
    }


    private companion object {
        const val SCROLL_BUTTON_DELAY = 4_000L //4 sec
    }
}