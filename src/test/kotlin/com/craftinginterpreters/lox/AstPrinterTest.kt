package com.craftinginterpreters.lox

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AstPrinterTest {

    @Test
    fun testAstPrinterPrints() {
        // Given
        val expression = Expr.Binary(
            left = Expr.Unary(
                operator = Token(TokenType.MINUS, "-", null, 1),
                right = Expr.Literal(123)
            ),
            operator = Token(TokenType.STAR, "*", null, 1),
            right = Expr.Grouping(
                Expr.Literal(45.67)
            )
        )
        val astPrinter = AstPrinter()

        // When
        val printed = astPrinter.print(expression)

        // Then
        assertEquals("(* (- 123) (group 45.67))", printed)
    }
}