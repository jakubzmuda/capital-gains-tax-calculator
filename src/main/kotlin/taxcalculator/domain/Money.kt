package taxcalculator.domain

import java.lang.IllegalArgumentException

data class Money(
    val amount: Float,
    val currency: Currency
) {

    operator fun plus(other: Money): Money {
        validateThatCurrenciesMatch(other)

        return Money(amount + other.amount, currency)
    }

    operator fun minus(other: Money): Money {
        validateThatCurrenciesMatch(other)

        return Money(amount - other.amount, currency)
    }

    private fun validateThatCurrenciesMatch(other: Money) {
        if (other.currency != currency) {
            throw IllegalArgumentException("Currency has to match")
        }
    }

}
