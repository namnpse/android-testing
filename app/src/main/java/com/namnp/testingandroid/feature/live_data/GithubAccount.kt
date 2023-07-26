package com.namnp.testingandroid.feature.live_data

import com.google.gson.annotations.SerializedName

data class GithubAccount(
    @SerializedName("login") var login : String = "",
    @SerializedName("id") var id : Int = 0,
    @SerializedName("avatar_url") var avatarUrl : String = "",
    @SerializedName("created_at") var createdAt : String = "",
    @SerializedName("updated_at") var updatedAt : String = "") {

    override fun equals(other: Any?): Boolean {
        return login == (other as GithubAccount).login
    }
}
