package io.radio.presentation.stations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.radio.shared.common.Inject
import io.radio.shared.model.RadioStation
import io.radio.shared.presentation.State
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class StationViewModel @Inject constructor(
    radioStationRepository: RadioStationRepository
) : BaseViewModel() {

    private val stationMutableLiveData = MutableLiveData<State<List<RadioStation>>>()
    val stationLiveData: LiveData<State<List<RadioStation>>> get() = stationMutableLiveData

    init {
        scope.launch(CoroutineExceptionHandler { _, throwable ->  stationMutableLiveData.value =State.Fail(throwable)}) {
            stationMutableLiveData.value = State.Loading
            stationMutableLiveData.value = State.Success(radioStationRepository.getStations())
        }
    }


}