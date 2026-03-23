package com.bookmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书籍分类
 */
enum class BookCategory {
    LITERATURE,    // 文学
    TECHNOLOGY,    // 科技
    HISTORY,       // 历史
    PHILOSOPHY,    // 哲学
    NOVEL,         // 小说
    TEXTBOOK,      // 教辅
    OTHER          // 其他
}

/**
 * 书籍状态
 */
enum class BookStatus {
    UNREAD,        // 未读
    READING,       // 在读
    READ,          // 已读
    BORROWED,      // 已借出
    LOST,          // 已丢失
    TREASURED      // 珍藏
}

/**
 * 书籍实体类
 */
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 基本信息
    val title: String,                    // 书名
    val author: String,                   // 作者
    val authorCountry: String = "",       // 作者国籍
    val publisher: String = "",           // 出版社
    val publishYear: Int? = null,         // 出版年份
    val isbn: String = "",                // ISBN
    val pageCount: Int? = null,           // 页数
    
    // 分类与状态
    val category: BookCategory = BookCategory.OTHER,
    val status: BookStatus = BookStatus.UNREAD,
    
    // 个人信息
    val tags: String = "",                // 自定义标签（逗号分隔）
    val readingProgress: Int = 0,         // 阅读进度 0-100
    val rating: Float = 0f,               // 个人评分 0-5
    val purchaseDate: Long? = null,       // 购买日期（时间戳）
    val purchasePrice: Float? = null,     // 购买价格
    val notes: String = "",               // 备注
    
    // 图片路径（本地私有目录）
    val coverImagePath: String = "",      // 封面图片
    val additionalImages: String = "",    // 附加图片（逗号分隔）
    
    // 时间戳
    val createdTime: Long = System.currentTimeMillis(),
    val modifiedTime: Long = System.currentTimeMillis(),
    
    // 阅读记录
    val startReadingDate: Long? = null,   // 开始阅读日期
    val finishReadingDate: Long? = null   // 完成阅读日期
)

/**
 * 借阅记录实体类
 */
@Entity(tableName = "borrow_records")
data class BorrowRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val bookId: Long,                     // 书籍ID
    val borrowerName: String,             // 借阅人姓名
    val borrowerContact: String = "",     // 联系方式
    val borrowDate: Long,                 // 借出日期
    val expectedReturnDate: Long? = null, // 预计归还日期
    val actualReturnDate: Long? = null,   // 实际归还日期
    val notes: String = "",               // 备注
    val isReturned: Boolean = false       // 是否已归还
)

/**
 * 阅读打卡记录
 */
@Entity(tableName = "reading_records")
data class ReadingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val bookId: Long,                     // 书籍ID
    val date: Long,                       // 打卡日期
    val pagesRead: Int = 0,               // 阅读页数
    val notes: String = ""                // 备注
)