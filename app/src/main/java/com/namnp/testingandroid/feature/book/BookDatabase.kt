package com.namnp.testingandroid.feature.book

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Book::class],
    version = 1
)
abstract class BookDatabase: RoomDatabase() {

    abstract val dao: BookDao

    companion object {
        const val BOOK_DATABASE_NAME = "book_db"
        const val BOOK_TABLE_NAME = "book_table"
    }
}