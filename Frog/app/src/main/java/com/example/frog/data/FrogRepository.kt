package com.example.frog.data

class FrogRepository {
    private val _frogDataSource: FrogDataSource = FrogDataSource()

    fun getQuestion(): Map<Int, String> {
        return _frogDataSource.fetchQuestion()
    }

    fun getAnswer(): Map<Int, String> {
        return _frogDataSource.fetchAnswer()
    }

    fun getNumberOfQuestions(): Int {
        return _frogDataSource.fetchNumberOfQuestions()
    }
}