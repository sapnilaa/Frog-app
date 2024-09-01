package com.example.frog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frog.ui.bar.NavigationBottomBar
import com.example.frog.ui.error.ErrorScreen
import com.example.frog.ui.loading.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val uiState by homeScreenViewModel.homeScreenUIState.collectAsState()

    homeScreenViewModel.initialize()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Frog Questions",
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
            NavigationBottomBar(navController = navController, screen = "HomeScreen")
        },
        containerColor = Color(90, 126, 92)
    ) {
        when (val state = uiState) {
            is HomeScreenUIState.Success -> {
                HomeScreenComponent(
                    navController = navController,
                    paddingValues = it,
                    numberOfQuestions = state.numberOfQuestions
                )
            }

            is HomeScreenUIState.Loading -> {
                LoadingScreen()
            }

            is HomeScreenUIState.Error -> {
                ErrorScreen()
            }
        }
    }
}

@Composable
fun HomeScreenComponent(
    navController: NavController,
    paddingValues: PaddingValues,
    numberOfQuestions: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(0.5f),
            verticalAlignment = Alignment.Bottom
        ) {
            HomeCard(navController = navController)
        }
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            InfoCard(numberOfQuestions = numberOfQuestions)
        }
    }
}

@Composable
fun HomeCard(
    navController: NavController
) {
    Card(
        onClick = { navController.navigate(route = "QuestionScreen") },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier
            .padding(20.dp)
            .size(width = 150.dp, height = 70.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(30, 53, 47),
            contentColor = Color(193, 212, 183)
        )
    ) {
        Text(
            text = "Start",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Composable
fun InfoCard(numberOfQuestions: Int) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier.padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(193, 212, 183),
            contentColor = Color(30, 53, 47)
        )
    ) {
        Text(
            text = "Number of questions to answer: \n $numberOfQuestions \n Have fun!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}