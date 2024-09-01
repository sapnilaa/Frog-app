package com.example.frog.ui.question

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frog.data.FrogRepository
import com.example.frog.model.Questions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface QuestionScreenUIState {

    data class Success(
        val questions: Map<Int, String> = mapOf(),
        val answers: Map<Int, String> = mapOf(),
        val correctAnswers: Int = 0,
        val wrongAnswers: Int = 0,
        var answered: MutableList<Int> = mutableListOf(),
        val currentQuestionId: Int = 0
    ) : QuestionScreenUIState

    data object Loading : QuestionScreenUIState

    data object Error : QuestionScreenUIState
}

class QuestionScreenViewModel : ViewModel() {
    private val _frogRepository: FrogRepository = FrogRepository()
    private val _questionScreenUIState: MutableStateFlow<QuestionScreenUIState> =
        MutableStateFlow(QuestionScreenUIState.Loading)
    val questionScreenUIState = _questionScreenUIState.asStateFlow()

    @MainThread
    fun initializeQuestion() {
        viewModelScope.launch(Dispatchers.Main) {
            _questionScreenUIState.value = try {

                val questions = _frogRepository.getQuestion()
                val answers = _frogRepository.getAnswer()

                QuestionScreenUIState.Success(
                    questions = questions,
                    answers = answers
                )

            } catch (exception: Exception) {
                QuestionScreenUIState.Error
            }
        }
    }

    @MainThread
    fun randomizeQuestion(): Int {
        val numberOfQuestions = _frogRepository.getNumberOfQuestions()
        return Random.nextInt(from = 0, until = numberOfQuestions - 1)
    }

    @MainThread
    fun updateAnswered(answeredQuestionId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            _questionScreenUIState.value = try {
                val currentUIState = _questionScreenUIState.value

                if (currentUIState is QuestionScreenUIState.Success) {
                    val updatedAnswered = currentUIState.answered.toMutableList()
                    updatedAnswered.add(answeredQuestionId)

                    QuestionScreenUIState.Success(
                        answered = mutableListOf()
                    )

                } else {

                    QuestionScreenUIState.Success(
                        answered = mutableListOf(answeredQuestionId)
                    )

                }
            } catch (exception: Exception) {
                QuestionScreenUIState.Error
            }
        }
    }

    @MainThread
    fun updateCurrentQuestionId(newQuestionId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            _questionScreenUIState.value = try {

                QuestionScreenUIState.Success(
                    currentQuestionId = newQuestionId
                )

            } catch (exception: Exception) {
                QuestionScreenUIState.Error
            }
        }
    }
}