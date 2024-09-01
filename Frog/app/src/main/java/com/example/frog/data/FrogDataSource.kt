package com.example.frog.data

import com.example.frog.model.QA

class FrogDataSource {

    private val _qa = QA()

    fun fetchQuestion(): Map<Int, String> {
        return _qa.questions.questionMap
    }

    fun fetchAnswer(): Map<Int, String> {
        return _qa.answers.answerMap
    }

    fun fetchNumberOfQuestions(): Int {
        return _qa.questions.questionMap.size
    }
}