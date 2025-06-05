package io.devchw.coderabbit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoderabbitDemoApplication

fun main(args: Array<String>) {
    runApplication<CoderabbitDemoApplication>(*args)
}
