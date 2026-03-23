package com.bookmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bookmanager.data.dao.*
import com.bookmanager.data.entity.*

/**
 * Room 数据库配置
 */
@Database(
    entities = [
        Book::class,
        BorrowRecord::class,
        ReadingRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    abstract fun borrowRecordDao(): BorrowRecordDao
    abstract fun readingRecordDao(): ReadingRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null
        
        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_manager_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * 类型转换器
 */
class Converters {
    @androidx.room.TypeConverter
    fun fromBookCategory(value: BookCategory): String = value.name
    
    @androidx.room.TypeConverter
    fun toBookCategory(value: String): BookCategory = 
        try { BookCategory.valueOf(value) } catch (e: Exception) { BookCategory.OTHER }
    
    @androidx.room.TypeConverter
    fun fromBookStatus(value: BookStatus): String = value.name
    
    @androidx.room.TypeConverter
    fun toBookStatus(value: String): BookStatus = 
        try { BookStatus.valueOf(value) } catch (e: Exception) { BookStatus.UNREAD }
}