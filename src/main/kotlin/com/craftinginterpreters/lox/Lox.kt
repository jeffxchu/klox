package com.craftinginterpreters.lox

import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

/**
 * Klox interpreter main entry point.
 */
@Throws(IOException::class)
fun main(args: Array<String>) {
    val interpreter = Lox()
    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        interpreter.runFile(args[0])
    } else {
        interpreter.runPrompt()
    }
}

class Lox  {
    /**
     * Reads from a file.
     */
    @Throws(IOException::class)
    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        runSource(bytes.toString(Charsets.UTF_8))

        if (hadError) exitProcess(65)
    }

    /**
     * REPL.
     */
    @Throws(IOException::class)
    fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = input.buffered()

        while (true) {
            print("> ")
            val line = reader.readLine() ?: break
            runSource(line)
            hadError = false
        }
    }

    /**
     * Runs the lox input.
     */
    private fun runSource(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }

    /**
     * Error handling.
     */
    companion object {

        var hadError: Boolean = false

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            println("[line $line Error $where: $message]")
            hadError = true
        }
    }
}
