package com.example.frog.ui.home

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frog.data.FrogRepository
import com.example.frog.model.Answers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

sealed interface HomeScreenUIState {

    data class Success(
        val numberOfQuestions: Int = 0,
        val questionsAnswered: Map<Int, String> = mapOf()
    ) : HomeScreenUIState

    data object Loading : HomeScreenUIState

    data object Error : HomeScreenUIState
}

class HomeScreenViewModel() : ViewModel() {
    private val _frogRepository = FrogRepository()
    private val _homeScreenUIState: MutableStateFlow<HomeScreenUIState> = MutableStateFlow(HomeScreenUIState.Loading)
    val homeScreenUIState = _homeScreenUIState.asStateFlow()

    @MainThread
    fun initialize() {
        viewModelScope.launch(Dispatchers.Main) {
            _homeScreenUIState.value = try {

                HomeScreenUIState.Success(
                    numberOfQuestions = _frogRepository.getNumberOfQuestions()
                )

            } catch (exception: Exception) {
                HomeScreenUIState.Error
            }
        }
    }

    @MainThread
    fun updateQuestionAnswered() {
        viewModelScope.launch(Dispatchers.Main) {
            _homeScreenUIState.value = try {

                HomeScreenUIState.Success(

                )

            } catch (exception: Exception) {
                HomeScreenUIState.Error
            }
        }
    }
}