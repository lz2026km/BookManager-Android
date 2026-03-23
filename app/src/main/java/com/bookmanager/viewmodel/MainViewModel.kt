package com.bookmanager.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookmanager.BookManagerApp
import com.bookmanager.data.entity.*
import com.bookmanager.data.repository.BookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 书籍列表状态
 */
data class BookListState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val sortBy: SortBy = SortBy.CREATED_TIME,
    val filterStatus: BookStatus? = null,
    val filterCategory: BookCategory? = null
)

enum class SortBy {
    NAME, CREATED_TIME, PROGRESS, RATING, YEAR
}

/**
 * 统计状态
 */
data class StatisticsState(
    val totalCount: Int = 0,
    val readCount: Int = 0,
    val readingCount: Int = 0,
    val unreadCount: Int = 0,
    val borrowedCount: Int = 0
)

/**
 * 主ViewModel
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: BookRepository
    
    // UI 状态
    private val _uiState = MutableStateFlow(BookListState())
    val uiState: StateFlow<BookListState> = _uiState
    
    // 统计状态
    private val _statisticsState = MutableStateFlow(StatisticsState())
    val statisticsState: StateFlow<StatisticsState> = _statisticsState
    
    // 选中的书籍（用于详情页）
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook
    
    init {
        val app = application as BookManagerApp
        repository = BookRepository(app.database, application)
        
        // 加载书籍列表
        loadBooks()
        
        // 加载统计数据
        loadStatistics()
    }
    
    // ========== 书籍加载 ==========
    
    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllBooks()
                .catch { e -> _uiState.update { it.copy(isLoading = false) } }
                .collect { books ->
                    _uiState.update { 
                        it.copy(books = books, isLoading = false)
                    }
                }
        }
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            repository.getTotalBookCount().collect { total ->
                _statisticsState.update { it.copy(totalCount = total) }
            }
        }
        viewModelScope.launch {
            repository.getBookCountByStatus(BookStatus.READ).collect { count ->
                _statisticsState.update { it.copy(readCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getBookCountByStatus(BookStatus.READING).collect { count ->
                _statisticsState.update { it.copy(readingCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getBookCountByStatus(BookStatus.UNREAD).collect { count ->
                _statisticsState.update { it.copy(unreadCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getBookCountByStatus(BookStatus.BORROWED).collect { count ->
                _statisticsState.update { it.copy(borrowedCount = count) }
            }
        }
    }
    
    // ========== 书籍操作 ==========
    
    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }
    
    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }
    
    fun deleteBooks(bookIds: List<Long>) {
        viewModelScope.launch {
            repository.deleteBooksByIds(bookIds)
        }
    }
    
    fun selectBook(book: Book?) {
        _selectedBook.value = book
    }
    
    fun updateBookStatus(bookId: Long, status: BookStatus) {
        viewModelScope.launch {
            repository.updateBookStatus(bookId, status)
        }
    }
    
    fun updateReadingProgress(bookId: Long, progress: Int) {
        viewModelScope.launch {
            repository.updateReadingProgress(bookId, progress)
        }
    }
    
    fun updateRating(bookId: Long, rating: Float) {
        viewModelScope.launch {
            repository.updateRating(bookId, rating)
        }
    }
    
    // ========== 搜索与筛选 ==========
    
    fun searchBooks(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            repository.searchBooks(query).collect { books ->
                _uiState.update { it.copy(books = books) }
            }
        }
    }
    
    fun filterByStatus(status: BookStatus?) {
        _uiState.update { it.copy(filterStatus = status) }
        viewModelScope.launch {
            if (status != null) {
                repository.getBooksByStatus(status).collect { books ->
                    _uiState.update { it.copy(books = books) }
                }
            } else {
                loadBooks()
            }
        }
    }
    
    fun filterByCategory(category: BookCategory?) {
        _uiState.update { it.copy(filterCategory = category) }
        viewModelScope.launch {
            if (category != null) {
                repository.getBooksByCategory(category).collect { books ->
                    _uiState.update { it.copy(books = books) }
                }
            } else {
                loadBooks()
            }
        }
    }
    
    fun clearFilters() {
        _uiState.update { it.copy(filterStatus = null, filterCategory = null, searchQuery = "") }
        loadBooks()
    }
    
    // ========== 图片操作 ==========
    
    fun saveBookImage(uri: Uri, bookId: Long, imageType: String): String {
        return repository.saveImage(uri, bookId, imageType)
    }
    
    // ========== 借阅记录 ==========
    
    fun addBorrowRecord(record: BorrowRecord) {
        viewModelScope.launch {
            repository.insertBorrowRecord(record)
        }
    }
    
    fun markBorrowReturned(recordId: Long) {
        viewModelScope.launch {
            repository.markBorrowAsReturned(recordId)
        }
    }
    
    // ========== 阅读打卡 ==========
    
    fun addReadingRecord(record: ReadingRecord) {
        viewModelScope.launch {
            repository.insertReadingRecord(record)
        }
    }
    
    // ========== 去重检测 ==========
    
    suspend fun checkDuplicate(title: String, author: String, isbn: String): Pair<Boolean, Book?> {
        return repository.checkDuplicate(title, author, isbn)
    }
}