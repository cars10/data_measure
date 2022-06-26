package com.cars10.datareport

import java.lang.Long.signum
import java.lang.String.format
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*

class ByteFormatter() {
    fun humanReadableByteCountBin(bytes: Long): String {
        val absB = Math.abs(bytes)
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
            "%.2f %cB",
            value / 1024.0,
            ci.current()
        )
    }
}