package com.cars10.datameasure

import java.lang.Long.signum
import java.lang.String.format
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*
import kotlin.math.abs

class ByteFormatter {
    fun humanReadableByteCountBin(bytes: Long): String {
        val absB = abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= signum(bytes).toLong()
        return format(
            Locale.getDefault(),
            "%.1f %cB",
            value / 1024.0,
            ci.current()
        )
    }
}