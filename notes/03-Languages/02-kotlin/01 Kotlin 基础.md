# Kotlin 基础-代理



属性代理

高阶函数

DSL

注解

泛型

反射

协程



```
// printSum 为高阶函数，定义了 lambda 形参
fun printSum(sum: (Int, Int) -> Int) {
    val result = sum(1, 2)
    println(result)
}

fun main() {

    //实际上kotlin中的lambda 就是一个匿名函数，java8的lambda表达式 却是一个sam的语法糖
    //参数类型包含函数类型，或者返回值类型是一个函数类型 的函数 都可以称之为是高阶函数。
    
    //形参：(Int, Int) -> Int
    //实参：{ x: Int, y: Int -> x + y }

    // 以下 lambda 为实参，传递给高阶函数 printSum
    val sum = { x: Int, y: Int -> x + y }
    fun sunF(x: Int, y: Int) = x + y
    printSum(sum)
    printSum(::sunF)
    //Kotlin 的 lambda 有个规约：如果 lambda 表达式是函数的最后一个实参，则可以放在括号外面，并且可以省略括号,这个规约是 Kotlin DSL 实现嵌套结构的本质原因
    printSum({ x: Int, y: Int -> x + y })//Lambda argument should be moved out of parentheses
    printSum() { x: Int, y: Int -> x + y }//Remove unnecessary parentheses from function call with lambda
    printSum { x: Int, y: Int -> x + y }

    fun pr(s: StringBuilder) {}
    val block = { x: StringBuilder ->
        /* 这个 lambda 的接收者类型为StringBuilder */
    }

    //带接收者的 lambda
    // 调用高阶函数
    kotlinDSL({})
    kotlinDSL(block)
    kotlinDSL(::pr)

}

typealias Block = StringBuilder.() -> Unit

// 声明接收者
fun kotlinDSL(block: Block) {
    block(StringBuilder("Kotlin"))
}
```