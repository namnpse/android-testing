package com.namnp.testingandroid.feature.form_registration

object FormRegistrationUtil {

    /**
     * invalid when:
     * the username/password is empty
     * the username is already taken
     * confirmed password is not the same as the password
     * the password contains less than 2 digits
     */

    private val existingUsers = listOf("Nam", "Bryan")

    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        if(username.isEmpty() || password.isEmpty()) {
            return false
        }
        if(username in existingUsers) {
            return false
        }
        if(password != confirmPassword) {
            return false
        }
        if(password.count { it.isDigit() } < 2) {
            return false
        }
        return true
    }
}