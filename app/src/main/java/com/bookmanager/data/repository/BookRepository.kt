package com.bookmanager.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import com.bookmanager.data.dao.*
import com.bookmanager.data.database.BookDatabase
import com.bookmanager.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 书籍仓库
 */
class BookRepository(private val database: BookDatabase, private val context: Context) {
    
    private val bookDao: BookDao = database.bookDao()
    private val borrowRecordDao: BorrowRecordDao = database.borrowRecordDao()
    private val readingRecordDao: ReadingRecordDao = database.readingRecordDao()
    
    // ========== 书籍操作 ==========
    
    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)
    
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    
    suspend fun deleteBookById(bookId: Long) = bookDao.deleteBookById(bookId)
    
    suspend fun deleteBooksByIds(bookIds: List<Long>) = bookDao.deleteBooksByIds(bookIds)
    
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
    
    fun getBookById(bookId: Long): Flow<Book?> = bookDao.getBookById(bookId)
    
    suspend fun getBookByIdSync(bookId: Long): Book? = bookDao.getBookByIdSync(bookId)
    
    fun searchBooks(query: String): Flow<List<Book>> = bookDao.searchBooks(query)
    
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>> = bookDao.getBooksByStatus(status)
    
    fun getBooksByCategory(category: BookCategory): Flow<List<Book>> = 
        bookDao.getBooksByCategory(category)
    
    fun getReadingBooks(): Flow<List<Book>> = bookDao.getReadingBooks()
    
    suspend fun updateBookStatus(bookId: Long, status: BookStatus) = 
        bookDao.updateStatus(bookId, status)
    
    suspend fun updateReadingProgress(bookId: Long, progress: Int) = 
        bookDao.updateReadingProgress(bookId, progress)
    
    suspend fun updateRating(bookId: Long, rating: Float) = 
        bookDao.updateRating(bookId, rating)
    
    // ========== 统计操作 ==========
    
    fun getTotalBookCount(): Flow<Int> = bookDao.getTotalBookCount()
    
    fun getBookCountByStatus(status: BookStatus): Flow<Int> = 
        bookDao.getBookCountByStatus(status)
    
    // ========== 去重检测 ==========
    
    suspend fun checkDuplicate(title: String, author: String, isbn: String): Pair<Boolean, Book?> {
        if (isbn.isNotBlank()) {
            val count = bookDao.checkDuplicateByIsbn(isbn)
            if (count > 0) {
                val existingBook = bookDao.findBookByIsbn(isbn)
                return Pair(true, existingBook)
            }
        }
        val count = bookDao.checkDuplicateByTitleAndAuthor(title, author)
        return Pair(count > 0, null)
    }
    
    // ========== 借阅记录操作 ==========
    
    suspend fun insertBorrowRecord(record: BorrowRecord): Long = 
        borrowRecordDao.insertRecord(record)
    
    suspend fun updateBorrowRecord(record: BorrowRecord) = 
        borrowRecordDao.updateRecord(record)
    
    suspend fun deleteBorrowRecord(recordId: Long) = 
        borrowRecordDao.deleteRecordById(recordId)
    
    fun getBorrowRecordsByBookId(bookId: Long): Flow<List<BorrowRecord>> = 
        borrowRecordDao.getRecordsByBookId(bookId)
    
    fun getActiveBorrows(): Flow<List<BorrowRecord>> = borrowRecordDao.getActiveBorrows()
    
    suspend fun markBorrowAsReturned(recordId: Long) = 
        borrowRecordDao.markAsReturned(recordId)
    
    // ========== 阅读打卡操作 ==========
    
    suspend fun insertReadingRecord(record: ReadingRecord): Long = 
        readingRecordDao.insertRecord(record)
    
    fun getReadingRecordsByBookId(bookId: Long): Flow<List<ReadingRecord>> = 
        readingRecordDao.getRecordsByBookId(bookId)
    
    // ========== 图片操作 ==========
    
    /**
     * 保存图片到APP私有目录
     */
    fun saveImage(uri: Uri, bookId: Long, imageType: String): String {
        val imageDir = File(context.filesDir, "book_images/$bookId")
        if (!imageDir.exists()) imageDir.mkdirs()
        
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${imageType}_$timestamp.jpg"
        val imageFile = File(imageDir, fileName)
        
        // 读取并压缩图片
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // 压缩到最大 1MB
        var quality = 90
        var outputStream = FileOutputStream(imageFile)
        while (quality >= 10) {
            outputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            if (imageFile.length() <= 1024 * 1024) break
            quality -= 10
        }
        outputStream.close()
        
        return imageFile.absolutePath
    }
    
    /**
     * 删除图片
     */
    fun deleteImage(imagePath: String) {
        if (imagePath.isNotBlank()) {
            val file = File(imagePath)
            if (file.exists()) file.delete()
        }
    }
    
    /**
     * 获取图片Uri
     */
    fun getImageUri(imagePath: String): Uri? {
        return if (imagePath.isNotBlank() && File(imagePath).exists()) {
            imagePath.toUri()
        } else null
    }
    
    // ========== 数据备份恢复 ==========
    
    /**
     * 导出数据到JSON文件
     */
    suspend fun exportData(): File {
        val books = mutableListOf<Book>()
        bookDao.getAllBooks().collect { books.addAll(it) }
        
        val exportDir = File(context.cacheDir, "export")
        if (!exportDir.exists()) exportDir.mkdirs()
        
        val exportFile = File(exportDir, "bookmanager_backup_${System.currentTimeMillis()}.json")
        // 实际项目中使用Gson或Moshi序列化
        exportFile.writeText(books.toString())
        
        return exportFile
    }
    
    /**
     * 清空所有数据
     */
    suspend fun clearAllData() {
        bookDao.deleteAllBooks()
    }
}