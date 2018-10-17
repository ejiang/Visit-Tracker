package io.github.ejiang.visittracker.util

import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class Utils {
    // NumberFormat is not thread safe
    companion object {
        fun calcTimeFrom(d: Date) : Int {
            return ((System.currentTimeMillis() - d.time) / (24 * 60 * 60 * 1000f)).roundToInt()
        }

        fun currencyFormat(cents: Int) : String {
            val n : NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            return n.format(cents/100.0f)
        }

        fun reverseCurrency(s: String) : Int {
            val n : NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            return (n.parse(s).toFloat() * 100).toInt()
        }

        fun currencyFormatNoSymbol(cents: Int) : String {
            val n : NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            val s = n.format(cents/100.0f)
            return s.substring(1, s.length)
        }
    }
}