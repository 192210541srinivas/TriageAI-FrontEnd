package com.simats.triageai.utils

object ValidationUtils {
    
    /**
     * Validates a password based on:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character (@#$%^&+=!?,.-)
     */
    /**
     * Validates a full name (only alphabets and spaces).
     */
    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.all { it.isLetter() || it.isWhitespace() }
    }

    /**
     * Validates an email format.
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validates a phone number (must be exactly 10 digits).
     */
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length == 10
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.length < 8) {
            return ValidationResult.Invalid("Password must be at least 8 characters long")
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult.Invalid("Password must contain at least one uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult.Invalid("Password must contain at least one lowercase letter")
        }
        if (!password.any { it.isDigit() }) {
            return ValidationResult.Invalid("Password must contain at least one number")
        }
        val specialChars = "@#$!%*?&^()-_=+[]{};:'\",.<>/?"
        if (!password.any { it in specialChars }) {
            return ValidationResult.Invalid("Password must contain at least one special character")
        }
        return ValidationResult.Valid
    }

    /**
     * Validates vital signs based on provided limits:
     * - HR: 0-140
     * - Diastolic: 50-120
     * - Systolic: 80-180
     * - RR: 8-30
     * - Temp: 32-45
     * - SpO2: 80-110
     */
    fun validateVitalRange(type: String, value: Float): ValidationResult {
        return when (type.uppercase()) {
            "HR" -> if (value in 0f..140f) ValidationResult.Valid else ValidationResult.Invalid("Heart Rate must be between 0 and 140")
            "DIASTOLIC" -> if (value in 50f..120f) ValidationResult.Valid else ValidationResult.Invalid("Diastolic BP must be between 50 and 120")
            "SYSTOLIC" -> if (value in 80f..180f) ValidationResult.Valid else ValidationResult.Invalid("Systolic BP must be between 80 and 180")
            "RR" -> if (value in 8f..30f) ValidationResult.Valid else ValidationResult.Invalid("Respiratory Rate must be between 8 and 30")
            "TEMP" -> if (value in 32f..45f) ValidationResult.Valid else ValidationResult.Invalid("Temperature must be between 32 and 45")
            "SPO2" -> if (value in 80f..110f) ValidationResult.Valid else ValidationResult.Invalid("SpO2 must be between 80 and 110")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}
