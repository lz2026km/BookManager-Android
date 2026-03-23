package com.bookmanager

import android.app.Application
import com.bookmanager.data.database.BookDatabase

class BookManagerApp : Application() {
    val database: BookDatabase by lazy { BookDatabase.getDatabase(this) }
}