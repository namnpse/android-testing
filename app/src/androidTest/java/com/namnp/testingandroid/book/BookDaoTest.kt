package com.namnp.testingandroid.book

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.namnp.testingandroid.feature.book.Book
import com.namnp.testingandroid.feature.book.BookDao
import com.namnp.testingandroid.feature.book.BookDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class BookDaoTest {

    private lateinit var bookDao: BookDao
    private lateinit var bookDatabase: BookDatabase

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        bookDatabase = Room.inMemoryDatabaseBuilder(
            context,
            BookDatabase::class.java
        ).build()
        bookDao = bookDatabase.dao
    }

    @Test
    fun addItem_shouldReturn_theItem_inFlow() = runTest {
        val item1 = Book(id = 1, bookName = "Book 1")
        val item2 = Book(id = 2, bookName = "Book 2")
        bookDao.addBook(item1)
        bookDao.addBook(item2)
        bookDao.getAllBooks().test {
            val list = awaitItem()
            assert(list.contains(item1))
            assert(list.contains(item2))
            cancel()
        }
    }

    @Test
    fun deletedItem_shouldNot_be_present_inFlow() = runTest {
        val item1 = Book(id = 1, bookName = "Book 1")
        val item2 = Book(id = 2, bookName = "Book 2")
        bookDao.addBook(item1)
        bookDao.addBook(item2)

        bookDao.deleteBook(item2)

        bookDao.getAllBooks().test {
            val list = awaitItem()
            assert(list.size == 1)
            assert(list.contains(item1))
            cancel()
        }
    }

    @Test
    fun updateItem_shouldReturn_theItem_inFlow() = runTest {
        val item1 = Book(id = 1, bookName = "Book 1")
        val item2 = Book(id = 2, bookName = "Book 2")
        val item3 = Book(id = 3, bookName = "Book 3")
        bookDao.addBook(item1)
        bookDao.addBook(item2)
        bookDao.addBook(item3)
        bookDao.getAllBooks().test {
            val list = awaitItem()
            assert(list.size == 2)
            assert(list.contains(item3))
            cancel()
        }
    }

    @After
    fun tearDown() {
        bookDatabase.close()
    }
}