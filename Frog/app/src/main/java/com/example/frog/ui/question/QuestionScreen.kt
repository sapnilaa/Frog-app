package com.example.frog.ui.question

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frog.ui.bar.NavigationBottomBar
import com.example.frog.ui.error.ErrorScreen
import com.example.frog.ui.loading.LoadingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    navController: NavController
) {

    val questionScreenViewModel: QuestionScreenViewModel = viewModel()
    val questionUIState by questionScreenViewModel.questionScreenUIState.collectAsState()

    questionScreenViewModel.initializeQuestion()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Questions",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(30, 53, 47),
                    titleContentColor = Color(193, 212, 183)
                )
            )
        },
        bottomBar = {
            NavigationBottomBar(navController = navController, screen = "QuestionScreen")
        },
        containerColor = Color(90, 126, 92)
    ) {
        when (val questionState = questionUIState) {
            is QuestionScreenUIState.Success -> {
                QuestionScreenComponent(
                    paddingValues = it,
                    questions = questionState.questions,
                    answers = questionState.answers,
                    randomizeQuestion = questionScreenViewModel::randomizeQuestion,
                    updateAnswered = questionScreenViewModel::updateAnswered,
                    updateCurrentQuestionId = questionScreenViewModel::updateCurrentQuestionId
                )
            }

            is QuestionScreenUIState.Loading -> {
                LoadingScreen()
            }

            is QuestionScreenUIState.Error -> {
                ErrorScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionScreenComponent(
    paddingValues: PaddingValues,
    questions: Map<Int, String>,
    answers: Map<Int, String>,
    randomizeQuestion: () -> Int,
    updateAnswered: (answeredQuestionId: Int) -> Unit,
    updateCurrentQuestionId: (newQuestionId: Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { questions.size })
    val coroutineScope = rememberCoroutineScope()
    val progress = if (pagerState.pageCount > 0) {
        (pagerState.currentPage.toFloat() / pagerState.pageCount.toFloat())
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                modifier = Modifier.size(width = 300.dp, height = 20.dp),
                progress = { progress },
                color = Color(30, 53, 47),
                trackColor = Color(193, 212, 183)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 20.dp)
        ) { page ->
            if (questions[page] != null && answers[page] != null) {
                QuestionCard(
                    question = questions[page]!!,
                    answer = answers[page]!!,
                    updateAnswered = updateAnswered,
                    updateCurrentQuestionId = updateCurrentQuestionId
                )
            }
        }

        Row {
            BackButton(
                enabled = pagerState.currentPage != 0,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )
            Spacer(modifier = Modifier.width(20.dp))

            NextButton(
                enabled = pagerState.currentPage <= pagerState.pageCount,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )
        }
        RandomButton(
            coroutineScope = coroutineScope,
            pagerState = pagerState,
            randomizeQuestion = randomizeQuestion
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionCard(
    question: String,
    answer: String,
    updateAnswered: (answeredQuestionId: Int) -> Unit,
    updateCurrentQuestionId: (newQuestionId: Int) -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(30, 53, 47),
            contentColor = Color(193, 212, 183)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = question,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic
            )
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = {
                    Text(
                        text = "Answer here",
                        color = Color(30, 53, 47)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(193, 212, 183),
                    unfocusedContainerColor = Color(193, 212, 183)
                ),
                singleLine = true
            )

            OutlinedButton(
                modifier = Modifier.padding(20.dp),
                onClick = {
                    result = if (userInput != "" && userInput.lowercase() == answer.lowercase()) {
                        "Correct"
                    } else if (userInput != ""){
                        "Wrong! \n\n Answer was: $answer"
                    } else {
                        "Please enter an answer"
                    }
                },
                colors = ButtonColors(
                    containerColor = Color(193, 212, 183),
                    contentColor = Color(30, 53, 47),
                    disabledContentColor = Color(30, 53, 47),
                    disabledContainerColor = Color(193, 212, 183)
                )
            ) {
                Text(
                    text = "Check",
                    fontSize = 20.sp
                )
            }

            Text(
                text = result,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NextButton(
    enabled: Boolean,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    val newQuestionPage = pagerState.currentPage + 1

    OutlinedIconButton(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .size(width = 80.dp, height = 50.dp),
        onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(newQuestionPage) }
        },
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackButton(
    enabled: Boolean,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    val newQuestionPage = pagerState.currentPage - 1

    OutlinedIconButton(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .size(width = 80.dp, height = 50.dp),
        onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(newQuestionPage) }
        },
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Arrow"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RandomButton(
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    randomizeQuestion: () -> Int,
) {
    val randomQuestionId = randomizeQuestion()

    OutlinedButton(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .size(width = 170.dp, height = 60.dp),
        onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(randomQuestionId) }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(193, 212, 183),
            contentColor = Color(30, 53, 47),
            disabledContentColor = Color(30, 53, 47),
            disabledContainerColor = Color(193, 212, 183)
        )
    ) {
        Text(
            text = "Randomize",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}