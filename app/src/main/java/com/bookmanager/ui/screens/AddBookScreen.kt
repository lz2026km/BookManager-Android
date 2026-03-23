package com.bookmanager.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bookmanager.data.entity.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    editBookId: Long? = null,
    onSave: (Book) -> Unit,
    onNavigateBack: () -> Unit
) {
    // 表单状态
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var authorCountry by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var publishYear by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BookCategory.OTHER) }
    var status by remember { mutableStateOf(BookStatus.UNREAD) }
    var tags by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var coverImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // 图片选择器
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        coverImageUri = uri
    }
    
    // 分类下拉菜单
    var categoryExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    
    // 表单验证
    val isValid = title.isNotBlank() && author.isNotBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editBookId == null) "添加书籍" else "编辑书籍") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (isValid) {
                                val book = Book(
                                    id = editBookId ?: 0,
                                    title = title,
                                    author = author,
                                    authorCountry = authorCountry,
                                    publisher = publisher,
                                    publishYear = publishYear.toIntOrNull(),
                                    isbn = isbn,
                                    pageCount = pageCount.toIntOrNull(),
                                    category = category,
                                    status = status,
                                    tags = tags,
                                    notes = notes
                                )
                                onSave(book)
                            }
                        },
                        enabled = isValid
                    ) {
                        Text("保存")
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
            // 封面图片
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { imagePicker.launch("image/*") }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    if (coverImageUri != null) {
                        // 显示选中的图片
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp))
                    } else {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("点击添加封面图片")
                        }
                    }
                }
            }
            
            // 基本信息
            Text("基本信息", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("书名 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("作者 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = authorCountry,
                onValueChange = { authorCountry = it },
                label = { Text("作者国籍") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = publisher,
                onValueChange = { publisher = it },
                label = { Text("出版社") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = publishYear,
                    onValueChange = { publishYear = it.filter { c -> c.isDigit() } },
                    label = { Text("出版年份") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = pageCount,
                    onValueChange = { pageCount = it.filter { c -> c.isDigit() } },
                    label = { Text("页数") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 分类选择
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category.toDisplayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("分类") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    BookCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.toDisplayName()) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            
            // 状态选择
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                OutlinedTextField(
                    value = status.toDisplayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("状态") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    BookStatus.values().forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st.toDisplayName()) },
                            onClick = {
                                status = st
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
            
            // 标签
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("标签（逗号分隔）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 备注
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}