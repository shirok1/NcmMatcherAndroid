package tech.shirok1.ncm.matcher.java.libncmapi

import org.apache.commons.text.translate.CodePointTranslator
import java.io.IOException
import java.io.Writer

object NonAsciiEscaper : CodePointTranslator() {

    fun String.nonAsciiEscape(): String = this@NonAsciiEscaper.translate(this)

    private const val space = ' '.code
    private const val wave = '~'.code

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f')

    @Throws(IOException::class)
    override fun translate(codepoint: Int, out: Writer): Boolean =
        if (codepoint in space..wave) {
            false
        } else {
            if (codepoint > 0xffff) {
                out.write("\\u" + Integer.toHexString(codepoint))
            } else {
                out.write("\\u")
                out.write(charArrayOf(
                    HEX_DIGITS[codepoint shr 12 and 15],
                    HEX_DIGITS[codepoint shr 8 and 15],
                    HEX_DIGITS[codepoint shr 4 and 15],
                    HEX_DIGITS[codepoint and 15],
                ))
            }
            true
        }
}