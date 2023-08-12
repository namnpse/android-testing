package com.namnp.testingandroid.feature.shopping.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShoppingItem::class],
    version = 1,
)
abstract class ShoppingItemDatabase: RoomDatabase() {

    abstract val shoppingDao: ShoppingDao
}