package com.craftinginterpreters.lox

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScannerTest {

    @Test
    fun testScanTokensSingleCharParen() {
        // Given
        val testString = "(())("
        val testScanner = Scanner(testString)

        // When
        val tokens = testScanner.scanTokens()

        // Then
        assertEquals(testString.length + 1, tokens.size)
        assertEquals(TokenType.LEFT_PAREN, tokens.first().type)
        assertEquals(TokenType.RIGHT_PAREN, tokens[2].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun testScanTokensSingleCharBraces() {
        // Given
        val testString = "{}{{}}"
        val testScanner = Scanner(testString)

        // When
        val tokens = testScanner.scanTokens()

        // Then
        assertEquals(testString.length + 1, tokens.size)
        assertEquals(TokenType.LEFT_BRACE, tokens.first().type)
        assertEquals(TokenType.RIGHT_BRACE, tokens[testString.length-1].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun testScanTokensSingleChars() {
        // Given
        // LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
        // COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR
        val testString = "(){},.-+;/*"
        val testScanner = Scanner(testString)

        // When
        val tokens = testScanner.scanTokens()

        // Then
        assertEquals(testString.length + 1, tokens.size)
        assertEquals(TokenType.LEFT_PAREN, tokens[0].type)
        assertEquals(TokenType.RIGHT_PAREN, tokens[1].type)
        assertEquals(TokenType.LEFT_BRACE, tokens[2].type)
        assertEquals(TokenType.RIGHT_BRACE, tokens[3].type)
        assertEquals(TokenType.COMMA, tokens[4].type)
        assertEquals(TokenType.DOT, tokens[5].type)
        assertEquals(TokenType.MINUS, tokens[6].type)
        assertEquals(TokenType.PLUS, tokens[7].type)
        assertEquals(TokenType.SEMICOLON, tokens[8].type)
        assertEquals(TokenType.SLASH, tokens[9].type)
        assertEquals(TokenType.STAR, tokens[10].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun testScanTokensWhenInvalidCharShouldMarkErrors() {
        // Given
        val testString = "{}&.9" // & is invalid token
        val testScanner = Scanner(testString)

        // When
        testScanner.scanTokens()

        // Then
        assertTrue(Lox.hadError)
    }
}
