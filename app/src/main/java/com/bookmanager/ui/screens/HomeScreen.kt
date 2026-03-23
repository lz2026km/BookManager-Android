package com.bookmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookmanager.data.entity.*
import com.bookmanager.viewmodel.MainViewModel
import com.bookmanager.viewmodel.StatisticsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel = viewModel(),
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val statistics by viewModel.statisticsState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 书籍管理") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBookClick) {
                Icon(Icons.Default.Add, contentDescription = "添加书籍")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 统计面板
            StatisticsPanel(statistics)
            
            // 筛选栏
            FilterBar(
                onFilterStatus = { viewModel.filterByStatus(it) },
                onFilterCategory = { viewModel.filterByCategory(it) },
                onClearFilters = { viewModel.clearFilters() }
            )
            
            // 书籍列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "暂无书籍",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "点击右下角 + 添加书籍",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.books) { book ->
                        BookItem(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onStatusChange = { viewModel.updateBookStatus(book.id, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsPanel(statistics: StatisticsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("📚 总藏书", statistics.totalCount)
            StatItem("✅ 已读", statistics.readCount)
            StatItem("📖 在读", statistics.readingCount)
            StatItem("📕 未读", statistics.unreadCount)
            StatItem("📤 借出", statistics.borrowedCount)
        }
    }
}

@Composable
fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun FilterBar(
    onFilterStatus: (BookStatus?) -> Unit,
    onFilterCategory: (BookCategory?) -> Unit,
    onClearFilters: () -> Unit
) {
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 状态筛选
        Box {
            Button(onClick = { expandedStatus = true }) {
                Text("状态筛选")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = expandedStatus,
                onDismissRequest = { expandedStatus = false }
            ) {
                DropdownMenuItem(
                    text = { Text("全部") },
                    onClick = { onFilterStatus(null); expandedStatus = false }
                )
                BookStatus.values().forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.toDisplayName()) },
                        onClick = { onFilterStatus(status); expandedStatus = false }
                    )
                }
            }
        }
        
        // 分类筛选
        Box {
            Button(onClick = { expandedCategory = true }) {
                Text("分类筛选")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                DropdownMenuItem(
                    text = { Text("全部") },
                    onClick = { onFilterCategory(null); expandedCategory = false }
                )
                BookCategory.values().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.toDisplayName()) },
                        onClick = { onFilterCategory(category); expandedCategory = false }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        TextButton(onClick = onClearFilters) {
            Text("清除筛选")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookItem(
    book: Book,
    onClick: () -> Unit,
    onStatusChange: (BookStatus) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 书籍图标
            Surface(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 书籍信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (book.readingProgress > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = book.readingProgress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 状态标签
            Box {
                AssistChip(
                    onClick = { showStatusMenu = true },
                    label = { Text(book.status.toDisplayName(), style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = {
                        Icon(
                            when (book.status) {
                                BookStatus.UNREAD -> Icons.Default.BookmarkBorder
                                BookStatus.READING -> Icons.Default.AutoStories
                                BookStatus.READ -> Icons.Default.CheckCircle
                                BookStatus.BORROWED -> Icons.Default.Outbox
                                BookStatus.LOST -> Icons.Default.SearchOff
                                BookStatus.TREASURED -> Icons.Default.Star
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                
                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
                ) {
                    BookStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.toDisplayName()) },
                            onClick = {
                                onStatusChange(status)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// 扩展函数：状态显示名称
fun BookStatus.toDisplayName(): String = when (this) {
    BookStatus.UNREAD -> "未读"
    BookStatus.READING -> "在读"
    BookStatus.READ -> "已读"
    BookStatus.BORROWED -> "已借出"
    BookStatus.LOST -> "已丢失"
    BookStatus.TREASURED -> "珍藏"
}

// 扩展函数：分类显示名称
fun BookCategory.toDisplayName(): String = when (this) {
    BookCategory.LITERATURE -> "文学"
    BookCategory.TECHNOLOGY -> "科技"
    BookCategory.HISTORY -> "历史"
    BookCategory.PHILOSOPHY -> "哲学"
    BookCategory.NOVEL -> "小说"
    BookCategory.TEXTBOOK -> "教辅"
    BookCategory.OTHER -> "其他"
}