package com.bookmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bookmanager.data.entity.Book
import com.bookmanager.ui.screens.*
import com.bookmanager.ui.theme.BookManagerTheme
import com.bookmanager.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookManagerApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddBook : Screen("add_book")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    object EditBook : Screen("edit_book/{bookId}") {
        fun createRoute(bookId: Long) = "edit_book/$bookId"
    }
}

@Composable
fun BookManagerApp(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 主页
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onAddBookClick = {
                    navController.navigate(Screen.AddBook.route)
                }
            )
        }
        
        // 添加书籍
        composable(Screen.AddBook.route) {
            AddBookScreen(
                onSave = { book ->
                    viewModel.addBook(book)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 书籍详情
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(androidx.navigation.NavArgument("bookId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
            BookDetailScreen(
                bookId = bookId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditBook = { id ->
                    navController.navigate(Screen.EditBook.createRoute(id))
                }
            )
        }
        
        // 编辑书籍
        composable(
            route = Screen.EditBook.route,
            arguments = listOf(androidx.navigation.NavArgument("bookId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
            AddBookScreen(
                editBookId = bookId,
                onSave = { book ->
                    viewModel.updateBook(book)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}