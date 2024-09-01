package com.example.frog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frog.ui.home.HomeScreen
import com.example.frog.ui.question.QuestionScreen

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "HomeScreen"
    ) {
        composable(
            route = "HomeScreen"
        ) {
            HomeScreen(
                navController = navController
            )
        }

        composable(
            route = "QuestionScreen"
        ) {
            QuestionScreen(
                navController = navController
            )
        }
    }
}