package io.github.ejiang.roomtests2.util

import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class Utils {
    companion object {
        val n = NumberFormat.getCurrencyInstance(Locale.US)

        fun calcTimeFrom(d: Date) : Int {
            return ((System.currentTimeMillis() - d.time) / (24 * 60 * 60 * 1000f)).roundToInt()
        }

        fun currencyFormat(cents: Int) : String {
            return n.format(cents/100.0f)
        }

        fun reverseCurrency(s: String) : Int {
            return (n.parse(s).toFloat() * 100).toInt()
        }
    }
}