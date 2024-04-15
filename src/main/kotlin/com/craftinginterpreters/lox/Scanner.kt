package com.craftinginterpreters.lox

/**
 * Scanner scans the source string.
 */
class Scanner (
    private val source: String
) {
    private val tokens = ArrayList<Token>()
    private val keywords = HashMap<String, TokenType>()

    init {
        keywords["and"] = TokenType.AND
        keywords["class"] = TokenType.CLASS
        keywords["else"] = TokenType.ELSE
        keywords["false"] = TokenType.FALSE
        keywords["for"] = TokenType.FOR
        keywords["fun"] = TokenType.FUN
        keywords["if"] = TokenType.IF
        keywords["nil"] = TokenType.NIL
        keywords["or"] = TokenType.OR
        keywords["print"] = TokenType.PRINT
        keywords["return"] = TokenType.RETURN
        keywords["super"] = TokenType.SUPER
        keywords["this"] = TokenType.THIS
        keywords["true"] = TokenType.TRUE
        keywords["var"] = TokenType.VAR
        keywords["while"] = TokenType.WHILE
    }

    private var start = 0
    private var current = 0
    private var line = 1

    /**
     * Scans tokens from the source input.
     */
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current
            scanToken()
        }

        tokens.add(
            Token(
                type = TokenType.EOF,
                lexeme = "",
                literal = null,
                line = line
            )
        )
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else if (peek() == '*') {
                    val cur = current
                    var ignore = false
                    while (!isAtEnd()) {
                        if (peek() == '*' && peekNext() == '/') {
                            ignore = true
                            break
                        }
                        advance()
                    }
                    if (ignore) {
                        current += 2
                    } else {
                        current = cur
                        addToken(TokenType.SLASH)
                    }
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            '\n' -> line++
            ' ', '\t', '\r' -> Unit // Ignore whitespaces

            '"' -> string()
            else ->
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    Lox.error(line = line, message = "Unexpected character '$c'")
                }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()

            while (isDigit((peek()))) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line = line, message = "Unterminated string.")
            return
        }

        advance() // Closing "

        val value = source.substring(start + 1, current -1)
        addToken(TokenType.STRING, value)
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(
            Token(
                type = type,
                lexeme = text,
                literal = literal,
                line = line
            )
        )
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        return if (source.elementAt(current) != expected) {
            false
        } else {
            current++
            true
        }
    }

    private fun peek(): Char? {
        if (isAtEnd()) return null // EOF
        return source.elementAt(current)
    }

    private fun peekNext(): Char? {
        val next = current + 1
        if (next >= source.length) return null // EOF
        return source.elementAt(next)
    }

    private fun isDigit(c: Char?): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char?): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '-'
    }

    private fun isAlphaNumeric(c: Char?): Boolean {
        return isDigit(c) || isAlpha(c)
    }

    private fun advance(): Char {
        return source.elementAt(current++)
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }
}
