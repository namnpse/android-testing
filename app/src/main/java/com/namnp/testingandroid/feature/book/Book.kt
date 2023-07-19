package com.namnp.testingandroid.feature.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namnp.testingandroid.feature.book.BookConstant.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "book_name") val bookName: String,
)