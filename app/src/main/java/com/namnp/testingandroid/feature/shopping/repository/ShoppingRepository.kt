package com.namnp.testingandroid.feature.shopping.repository

import androidx.lifecycle.LiveData
import com.namnp.testingandroid.feature.shopping.data.local.ShoppingItem
import com.namnp.testingandroid.feature.shopping.data.remote.ImageResponse
import com.namnp.testingandroid.feature.shopping.utils.Resource

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}