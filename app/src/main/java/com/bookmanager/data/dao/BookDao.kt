package com.bookmanager.data.dao

import androidx.room.*
import com.bookmanager.data.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * 书籍数据访问对象
 */
@Dao
interface BookDao {
    
    // ========== 插入操作 ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)
    
    // ========== 更新操作 ==========
    @Update
    suspend fun updateBook(book: Book)
    
    @Query("UPDATE books SET status = :status, modifiedTime = :modifiedTime WHERE id = :bookId")
    suspend fun updateStatus(bookId: Long, status: BookStatus, modifiedTime: Long = System.currentTimeMillis())
    
    @Query("UPDATE books SET readingProgress = :progress, modifiedTime = :modifiedTime WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: Long, progress: Int, modifiedTime: Long = System.currentTimeMillis())
    
    @Query("UPDATE books SET rating = :rating, modifiedTime = :modifiedTime WHERE id = :bookId")
    suspend fun updateRating(bookId: Long, rating: Float, modifiedTime: Long = System.currentTimeMillis())
    
    // ========== 删除操作 ==========
    @Delete
    suspend fun deleteBook(book: Book)
    
    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
    
    @Query("DELETE FROM books WHERE id IN (:bookIds)")
    suspend fun deleteBooksByIds(bookIds: List<Long>)
    
    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
    
    // ========== 查询操作 ==========
    @Query("SELECT * FROM books ORDER BY createdTime DESC")
    fun getAllBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Long): Flow<Book?>
    
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookByIdSync(bookId: Long): Book?
    
    // ========== 搜索操作 ==========
    @Query("""
        SELECT * FROM books 
        WHERE title LIKE '%' || :query || '%'
        OR author LIKE '%' || :query || '%'
        OR isbn LIKE '%' || :query || '%'
        OR tags LIKE '%' || :query || '%'
        OR publisher LIKE '%' || :query || '%'
        ORDER BY modifiedTime DESC
    """)
    fun searchBooks(query: String): Flow<List<Book>>
    
    // ========== 筛选操作 ==========
    @Query("SELECT * FROM books WHERE status = :status ORDER BY modifiedTime DESC")
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE category = :category ORDER BY modifiedTime DESC")
    fun getBooksByCategory(category: BookCategory): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE authorCountry = :country ORDER BY modifiedTime DESC")
    fun getBooksByCountry(country: String): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE publishYear = :year ORDER BY modifiedTime DESC")
    fun getBooksByYear(year: Int): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE readingProgress > 0 AND readingProgress < 100 ORDER BY modifiedTime DESC")
    fun getReadingBooks(): Flow<List<Book>>
    
    // ========== 排序操作 ==========
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getBooksSortedByName(): Flow<List<Book>>
    
    @Query("SELECT * FROM books ORDER BY createdTime DESC")
    fun getBooksSortedByCreatedTime(): Flow<List<Book>>
    
    @Query("SELECT * FROM books ORDER BY readingProgress DESC")
    fun getBooksSortedByProgress(): Flow<List<Book>>
    
    @Query("SELECT * FROM books ORDER BY rating DESC")
    fun getBooksSortedByRating(): Flow<List<Book>>
    
    @Query("SELECT * FROM books ORDER BY publishYear DESC")
    fun getBooksSortedByYear(): Flow<List<Book>>
    
    // ========== 统计操作 ==========
    @Query("SELECT COUNT(*) FROM books")
    fun getTotalBookCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM books WHERE status = :status")
    fun getBookCountByStatus(status: BookStatus): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM books WHERE category = :category")
    fun getBookCountByCategory(category: BookCategory): Flow<Int>
    
    // ========== 去重检测 ==========
    @Query("SELECT COUNT(*) FROM books WHERE title = :title AND author = :author")
    suspend fun checkDuplicateByTitleAndAuthor(title: String, author: String): Int
    
    @Query("SELECT COUNT(*) FROM books WHERE isbn = :isbn AND isbn != ''")
    suspend fun checkDuplicateByIsbn(isbn: String): Int
    
    @Query("SELECT * FROM books WHERE isbn = :isbn AND isbn != '' LIMIT 1")
    suspend fun findBookByIsbn(isbn: String): Book?
}

/**
 * 借阅记录数据访问对象
 */
@Dao
interface BorrowRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BorrowRecord): Long
    
    @Update
    suspend fun updateRecord(record: BorrowRecord)
    
    @Delete
    suspend fun deleteRecord(record: BorrowRecord)
    
    @Query("DELETE FROM borrow_records WHERE id = :recordId")
    suspend fun deleteRecordById(recordId: Long)
    
    @Query("SELECT * FROM borrow_records WHERE bookId = :bookId ORDER BY borrowDate DESC")
    fun getRecordsByBookId(bookId: Long): Flow<List<BorrowRecord>>
    
    @Query("SELECT * FROM borrow_records WHERE isReturned = 0 ORDER BY borrowDate DESC")
    fun getActiveBorrows(): Flow<List<BorrowRecord>>
    
    @Query("SELECT * FROM borrow_records ORDER BY borrowDate DESC")
    fun getAllRecords(): Flow<List<BorrowRecord>>
    
    @Query("UPDATE borrow_records SET isReturned = 1, actualReturnDate = :returnDate WHERE id = :recordId")
    suspend fun markAsReturned(recordId: Long, returnDate: Long = System.currentTimeMillis())
}

/**
 * 阅读打卡数据访问对象
 */
@Dao
interface ReadingRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ReadingRecord): Long
    
    @Query("SELECT * FROM reading_records WHERE bookId = :bookId ORDER BY date DESC")
    fun getRecordsByBookId(bookId: Long): Flow<List<ReadingRecord>>
    
    @Query("SELECT * FROM reading_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getRecordsBetweenDates(startDate: Long, endDate: Long): Flow<List<ReadingRecord>>
    
    @Query("DELETE FROM reading_records WHERE id = :recordId")
    suspend fun deleteRecordById(recordId: Long)
}