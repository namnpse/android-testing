package com.namnp.testingandroid.feature.form_registration

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FormRegistrationUtilTest {

    @Test
    fun `empty username returns false`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "",
            "123",
            "123"
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password returns true`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "Namnpse",
            "123",
            "123"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `username already exists returns false`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "Nam",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `incorrectly confirmed password returns false`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "Namnpse",
            "123456",
            "abcdefg"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "Namnpse",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 2 digit password returns false`() {
        val result = FormRegistrationUtil.validateRegistrationInput(
            "Namnpse",
            "abcdefg5",
            "abcdefg5"
        )
        assertThat(result).isFalse()
    }
}