package com.craftinginterpreters.tool

import java.io.FileWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }
    val generateAst = GenerateAst()
    val outputDir = args[0]
    generateAst.defineAst(
        outputDir = outputDir,
        baseName = "Expr",
        types = listOf(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Any value",
            "Unary    : Token operator, Expr right"
        )
    )
}

class GenerateAst {

    fun defineAst(outputDir: String, baseName: String, types: List<String>) {
        val path = "$outputDir/$baseName.kt"
        val writer = FileWriter(path)

        writer.write("package com.craftinginterpreters.lox\n\n")
        writer.write("abstract class $baseName {\n\n")

        for (type in types) {
            val className = type.split(":")[0].trim()
            val fields = type.split(":")[1].trim()
            defineType(
                writer = writer,
                baseName = baseName,
                className = className,
                fields = fields
            )
        }
        writer.write("}")
        writer.close()
    }

    private fun defineType(writer: FileWriter, baseName: String, className: String, fields: String) {
        writer.write("    class $className(\n")
        val fieldList = fields.split(", ")
        for (field in fieldList) {
            val type = field.split(" ")[0]
            val name = field.split(" ")[1]
            writer.write("        val $name: $type,\n")
        }
        writer.write("    ): $baseName()\n\n")
    }
}