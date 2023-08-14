package com.namnp.testingandroid.feature.shopping.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.namnp.testingandroid.feature.shopping.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(AndroidJUnit4::class) don't need, cause we declare our own HiltTestRunner
@HiltAndroidTest // we can use Hilt here
@SmallTest
class ShoppingDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    // LiveData is asynchronous by default -> and use LiveData in runBlocking -> JUnit throws a error
    //-> add that rule to tell JUnit that we want to execute tests one after another, the same thread (blocking)

//    private lateinit var database: ShoppingItemDatabase
    @Inject
    @Named("test_db")
    lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            ShoppingItemDatabase::class.java
//        ).allowMainThreadQueries().build()
        hiltRule.inject() // Hilt injects all dependencies here
        dao = database.shoppingDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
//    fun insertShoppingItem() = runBlockingTest { runBlocking -> runBlockingTest(more optimized, skip delay func) are deprecated -> use runTest {}
    fun insertShoppingItem() = runTest {
        val shoppingItem = ShoppingItem("Apple", 1, 1f, "url", id = 1)
        dao.addShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems).contains(shoppingItem)
    }

    @Test
    fun deleteShoppingItem() = runTest {
        val shoppingItem = ShoppingItem("Apple", 10, 10f, "url", id = 1)
        dao.addShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPriceSum() = runTest {
        val shoppingItem1 = ShoppingItem("name1", 2, 1f, "url", id = 1)
        val shoppingItem2 = ShoppingItem("name2", 5, 3.5f, "url", id = 2)
        val shoppingItem3 = ShoppingItem("name3", 0, 10f, "url", id = 3)
        dao.addShoppingItem(shoppingItem1)
        dao.addShoppingItem(shoppingItem2)
        dao.addShoppingItem(shoppingItem3)

        val totalPriceSum = dao.observeTotalPrice().getOrAwaitValue()

        assertThat(totalPriceSum).isEqualTo(2 * 1f + 5 * 3.5f)
    }

}

// NOTE
// 1. runBlocking -> runBlockingTest(more optimized, skip delay func) are deprecated -> use runTest {}