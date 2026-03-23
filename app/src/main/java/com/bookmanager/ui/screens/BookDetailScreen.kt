package com.bookmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookmanager.data.entity.*
import com.bookmanager.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    viewModel: MainViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onEditBook: (Long) -> Unit
) {
    val book by viewModel.selectedBook.collectAsState()
    
    LaunchedEffect(bookId) {
        val b = viewModel.getBookByIdSync(bookId)
        viewModel.selectBook(b)
    }
    
    book?.let { currentBook ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentBook.title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditBook(bookId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = { /* 分享 */ }) {
                            Icon(Icons.Default.Share, contentDescription = "分享")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 基本信息
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("基本信息", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow("书名", currentBook.title)
                        DetailRow("作者", currentBook.author)
                        if (currentBook.authorCountry.isNotBlank()) {
                            DetailRow("国籍", currentBook.authorCountry)
                        }
                        if (currentBook.publisher.isNotBlank()) {
                            DetailRow("出版社", currentBook.publisher)
                        }
                        currentBook.publishYear?.let { DetailRow("出版年份", it.toString()) }
                        if (currentBook.isbn.isNotBlank()) {
                            DetailRow("ISBN", currentBook.isbn)
                        }
                        currentBook.pageCount?.let { DetailRow("页数", "$it 页") }
                        DetailRow("分类", currentBook.category.toDisplayName())
                        DetailRow("状态", currentBook.status.toDisplayName())
                    }
                }
                
                // 个人信息
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("个人信息", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 阅读进度
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("阅读进度")
                            Text("${currentBook.readingProgress}%")
                        }
                        LinearProgressIndicator(
                            progress = currentBook.readingProgress / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 评分
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("评分")
                            RatingBar(
                                rating = currentBook.rating,
                                onRatingChange = { viewModel.updateRating(bookId, it) }
                            )
                        }
                        
                        if (currentBook.tags.isNotBlank()) {
                            DetailRow("标签", currentBook.tags)
                        }
                        if (currentBook.notes.isNotBlank()) {
                            DetailRow("备注", currentBook.notes)
                        }
                    }
                }
                
                // 借阅记录
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("借阅记录", style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { /* 添加借阅记录 */ }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("添加")
                            }
                        }
                        // 借阅记录列表...
                        Text("暂无借阅记录", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    Row {
        repeat(5) { index ->
            IconButton(
                onClick = { onRatingChange((index + 1).toFloat()) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < rating.toInt()) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}