# Kotlin 



## 修饰符

```kotlin
//如果一个声明有多个修饰符，请始终按照以下顺序安放：
public / protected / private / internal
expect / actual
final / open / abstract / sealed / const
external
override
lateinit
tailrec
vararg
suspend
inner
enum / annotation / fun // 在 `fun interface` 中是修饰符
companion
inline
infix
operator
data
```



## Kotlin VS Java

- Kotlin 旨在让已经了解 Java 的人易于学习。 在[官方比较页](https://www.kotlincn.net/docs/reference/comparison-to-java.html)上给出了二者差异的快速概述。

- 优势：简洁、更安全、与Java互操作、协程结构化并发

## 编码规范

- [编码规范](https://www.kotlincn.net/docs/reference/coding-conventions.html)
- [代码风格迁移指南](https://www.kotlincn.net/docs/reference/code-style-migration-guide.html)
- 在项目根目录的 **gradle.properties** 文件中添加 **kotlin.code.style**=**official** 属性，并将其提交到版本控制系统。



## 基本类型

[官方文档](https://www.kotlincn.net/docs/reference/basic-types.html)

Kotlin 中使用的基本类型：数字、字符、布尔值、数组与字符串。

### 数字类型

| 类型  | 大小（比特数） | 最小值                            | 最大值                              |
| :---- | :------------- | :-------------------------------- | :---------------------------------- |
| Byte  | 8              | -128                              | 127                                 |
| Short | 16             | -32768                            | 32767                               |
| Int   | 32             | -2,147,483,648 (-231)             | 2,147,483,647 (231 - 1)             |
| Long  | 64             | -9,223,372,036,854,775,808 (-263) | 9,223,372,036,854,775,807 (263 - 1) |

- 所有以未超出 `Int` 最大值的整型值初始化的变量都会推断为 `Int` 类型。如果初始值超过了其最大值，那么推断为 `Long` 类型。 如需显式指定 `Long` 型值，请在该值后追加 `L` 后缀。

| 类型   | 大小（比特数） | 有效数字比特数 | 指数比特数 | 十进制位数 |
| :----- | :------------- | :------------- | :--------- | :--------- |
| Float  | 32             | 24             | 8          | 6-7        |
| Double | 64             | 53             | 11         | 15-16      |

- 对于以小数初始化的变量，编译器会推断为 `Double` 类型。 如需将一个值显式指定为 `Float` 类型，请添加 `f` 或 `F` 后缀。
- 不支持隐式拓宽转换：具有 `Double` 参数的函数只能对 `Double` 值调用，而不能对 `Float`、 `Int` 或者其他数字值调用。

### 字面常量

- 十进制: `123`、`123L` 、`123.5`、`123.5f`
- 十六进制: `0x0F`
- 二进制: `0b00001011`

- 注意: KT 不支持八进制
- 下划线： `1_000_000`、`1234_5678_9012_3456L`、 `0xFF_EC_DE_5E`、 `0b11010010_01101001_10010100_10010010`

### 装箱问题

- Integer只缓存了【-128，127】个数字实例，其它范围都会新建实例，这里需要注意实例的同一性已经数值的相等性问题
- 其它类型的装箱类型缓存的区间：**Byte，Short，Integer，Long为 -128 到 127**，**Character范围为 0 到 127。**
- 除了 Integer 可以通过jvm参数改变范围外，其它的都不行。
- 较小的类型**不能**隐式转换为较大的类型。 这意味着在不进行显式转换的情况下我们不能把 `Byte` 型值赋给一个 `Int` 变量。如果需要转换需要通过以下方法：`toByte(): Byte`、`toShort(): Short`、`toInt(): Int`、`toLong(): Long`、`toFloat(): Float`、`toDouble(): Double`、`toChar(): Char`

### 位运算

位运算列表（只用于 `Int` 与 `Long`）：

- `shl(bits)` – 有符号左移
- `shr(bits)` – 有符号右移
- `ushr(bits)` – 无符号右移
- `and(bits)` – 位**与**
- `or(bits)` – 位**或**
- `xor(bits)` – 位**异或**
- `inv()` – 位非

对应的函数为：

```kotlin
/** Shifts this value left by the [bitCount] number of bits. */
public infix fun shl(bitCount: Int): Int

/** Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with copies of the sign bit. */
public infix fun shr(bitCount: Int): Int

/** Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with zeros. */
public infix fun ushr(bitCount: Int): Int

/** Performs a bitwise AND operation between the two values. */
public infix fun and(other: Int): Int

/** Performs a bitwise OR operation between the two values. */
public infix fun or(other: Int): Int

/** Performs a bitwise XOR operation between the two values. */
public infix fun xor(other: Int): Int

/** Inverts the bits in this value. */
public fun inv(): Int
```

### 字符

字符字面值用单引号括起来: `'1'`。 特殊字符可以用反斜杠转义。 支持这几个转义序列：`\t`、 `\b`、`\n`、`\r`、`\'`、`\"`、`\\` 与 `\$`。 编码其他字符要用 Unicode 转义序列语法：`'\uFF00'`。可以显式把字符转换为 `Int` 数字：c.toInt()。

### 布尔

布尔用 `Boolean` 类型表示，它有两个值：*true* 与 *false*。若需要可空引用布尔会被装箱。

内置的布尔运算有：

- `||` – 短路逻辑或
- `&&` – 短路逻辑与
- `!` - 逻辑非

### 数组

数组在 Kotlin 中使用 `Array` 类来表示，

- `arrayOf()` 来创建一个数组并传递元素值给它， `arrayOf(1, 2, 3)`

- `arrayOfNulls()` 可以用于创建一个指定大小的、所有元素都为空的数组,

- 构造器函数`inline constructor(size: Int, init: (Int) -> T)` 可以创建一个具有指定 [size] 的新数组，其中每个元素都通过调用指定的 [init] 函数入参一个索引返回该索引对应的值

- Kotlin 中数组是*不型变的（invariant）*。这意味着 Kotlin 不让我们把 `Array<String>` 赋值给 `Array<Any>`，以防止可能的运行时失败（但是你可以使用 `Array<out Any>`

- `[]` 运算符代表调用成员函数 `get()` 与 `set()`，例如：`val s1 = asc[0]`

- 原生类型数组：Kotlin 也有无装箱开销的专门的类来表示原生类型数组: `ByteArray`、 `ShortArray`、`IntArray` 等等。这些类与 `Array` 并没有继承关系，但是它们有同样的方法属性集。它们也都有相应的工厂方法:

  - ```kotlin
    // 大小为 5、值为 [0, 0, 0, 0, 0] 的整型数组
    val arr1 = IntArray(5)
    // 例如：用常量初始化数组中的值
    // 大小为 5、值为 [42, 42, 42, 42, 42] 的整型数组
    val arr2 = IntArray(5) { 42 }
    // 例如：使用 lambda 表达式初始化数组中的值
    // 大小为 5、值为 [0, 1, 2, 3, 4] 的整型数组（值初始化为其索引值）
    var arr3 = IntArray(5) { it * 1 }
    ```

无符号整型数组：与原生类型相同，每个无符号类型都有相应的为该类型特化的表示数组的类型：

- `kotlin.UByteArray`: 无符号字节数组
- `kotlin.UShortArray`: 无符号短整型数组
- `kotlin.UIntArray`: 无符号整型数组
- `kotlin.ULongArray`: 无符号长整型数组

与有符号整型数组一样，它们提供了类似于 `Array` 类的 API 而没有装箱开销。

### 无符号整型

- 无符号类型自 Kotlin 1.3 起才可用，并且目前处于 [Beta](https://www.kotlincn.net/docs/reference/evolution/components-stability.html) 版
- Kotlin 为无符号整数引入了以下类型：
  - `kotlin.UByte`: 无符号 8 比特整数，范围是 0 到 255
  - `kotlin.UShort`: 无符号 16 比特整数，范围是 0 到 65535
  - `kotlin.UInt`: 无符号 32 比特整数，范围是 0 到 2^32 - 1
  - `kotlin.ULong`: 无符号 64 比特整数，范围是 0 到 2^64 - 1

- 无符号类型支持其对应有符号类型的大多数操作。

### 字符串

- 字符串用 `String` 类型表示。字符串是不可变的。 字符串的元素可以使用索引运算符访问: `s[i]`。 可以用 *for* 循环迭代字符串
- 可以用 `+` 操作符连接字符串。这也适用于连接字符串与其他类型的值， 只要表达式中的第一个元素是字符串：
- 转义采用传统的反斜杠方式
- *原始字符串* 使用三个引号（`"""`）分界符括起来，内部没有转义并且可以包含换行以及任何其他字符:
- 你可以通过 [`trimMargin()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/trim-margin.html) 函数去除前导空格：
- 字符串字面值可以包含*模板表达式* ，即一些小段代码，会求值并把结果合并到字符串中。 模板表达式以美元符（`$`）开头，由一个简单的名字构成，或者用花括号括起来的任意表达式
- 原始字符串与转义字符串内部都支持模板。 如果你需要在原始字符串中表示字面值 `$` 字符（它不支持反斜杠转义）





## 内置类型

- 变量
  - 类型推倒
  - 易混淆的Long类型标记：val d=123333L ，Java里面大小写都可以
  - 数值类型转换：显示转换，int .toLong
  - 无符号类型：UInt、ULong
  - 字符串：
    - ==：比较内容等价于Java的equals, 
    - ===：比较的是引用，
    - 字符串模板：$name
    - Raw String  ""sssss""  trimIndent  格式化
  
  ![](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20210806111352.png)
  
- 数组
  - Java：int[]，Integer[]，char[]，Character[]，String[]
  - Kotlin：IntArray，Array<Int>，CharArray，Array <Char>，Array < String>
  - 声明，
  - 创建、
  - 读写，
  - 遍历: for(i in 0 until array.size) , for(i in array.indices) indices就是区间
  - 关键字：in、forEach
  - 包含关系：in，!in
- 区间
  - 闭区间
    - intRange: 1..10==[1,10]
    - charRange: 'a'..'z'==[a,z]
    - 倒序区间：10 downTo 1==[10,1]、’z' downTo 'a'
  - 开区间
    - 1 until 10==[1,10)、‘a' until 'z'==[a,z)、
  - 区间的步长 1..10 step 2
  - 打印：joinToString
  - 迭代：和数组一样
- 集合
  - 增加了不可变的集合框架接口
  - 复用Java API的所有实现类型
  - 提供了丰富的易用的方法：forEach/map/flatmap
  - 运算符级别的支持简化集合访问
  - 类型对比
    - Java：可变：List<T>、Map<K, V>、Set<T>
    - Kotlin：可变：MutableList <T>、MutableMap<K, V>、MutableSet <T >
    - Kotlin：不可变：List<T>、Map<K, V>、Set<T> 不可变不能添加或删除元素
  - 创建
    - listOf(1,2,3)，mutableListOf(1,2,3)
    - mapOf("name" to "Jay"), mutableMapOf("name" to "Jay") ， to pair类型
    - typealias/类型别名，Kotlin的类指向Java的类
  - 读写
    - list[3]="Hello" 、map["name"]
    - -=\+= 添加和移除
  - Pair: pair.first, pair.second ,val (x,y)=pair(解构)
  - Triple: triple.first,triple.second,triple.third ，（x,y,z）=triple (解构)
- 函数
  - 函数有自己的类型，可以赋值，传递，调用
  - fun main(args:Array<String>):Unit{}
  - 函数&方法
    - 方法可以认为是函数的一种特殊类型
    - 从形式上看，有receiver的函数即为方法，通过类调用的是方法
  - 函数的类型
    - ()->Unit, (int)->String, Foo.(String,Long)->Any ==(Foo,String,Long)->Any==Function3<Foo,String,Long,Any>
  - 函数的引用
    - 类似C中的函数指针，可用于函数传递
    - 定义：fun foo(){}  
    - 引用：::foo  Foo::bar
    - 接收：val f:()->Unit=::foo
    - 调用：f()==f.invoke()
  - 变长参数
    - vararg 关键字
  - 多返回值
    - val(a,b,c)=mutilReturn(){return Trople(1,2,3)}
  - 默认参数
    - 放在最后一个参数
    - defaultPerameter(5,"Hello"), fun defaultPerameter(x:int,y:String,**z:Long=0**)
  - 具名参数
    - 具名是形参的名字
    - defaultPerameter(y="Hello"),  fun defaultPerameter(x:int=1,y:String,**z:Long=0**)
  - 函数的基本用法
  - 函数的引用
  - 匿名函数
    - Lambda
    - SAM转换
  - 高阶函数
    - 常见高阶函数
    - 函数式编程



## 类型

- 类
  - class SimpleClass {} 默认public open
  - 成员参数必须初始化
  - constrcutor 构造器
  - 柱构造器 class SimpleClass constructor (var x:Int) {}
  - 继承：冒号 SimpleSupperClass()
- 接口
  - intterface SimpleInterface{}
  - 实现 ：冒号
- 抽象类
  - 普通方法默认不可复写相当于java中的final，open后可复写
- 属性
  - property=backing field + getter+setter
    - var age:Int =18 
      - get(){return field}
      - set(value){field=value}  这里的的 field:backing field
  - 属性引用：val ageRef=Person::age  , person::age.set(33)
- 扩展方法
  - fun String.times(count:Int) :String{ return "str"}
  - 引用：String::times
  - 类型：(String, int) ->String 
  - 绑定了receiver的引用/类型：“*”::times ,(int)->String
  - 扩展方法和接口中都不能存在状态（field ），只能定义行为
- 空类型安全
  - var notNull:String ="Hello", notNull不能赋值为null
  - notNull.length
  - var nullAble:String?="Hello" nullAble可以
  - nullAble?.length：安全访问   / nullAble!!.length :强转为不可空
  - 空类型的继承关系
    - String? 可以看做是String的父类
  - 运算符：！、？、？：
  - 平台类型（Java/JS/Native）
    - Kotlin 编译器不知道 Java 返回的是空还是非空
    - 可以通过 在Java平台添加 @NotNull注解人为告诉Kotlin
- 智能类型转换
  - 作用范围
    - if(Sub is Supper){  }
    - if(value!=null)(  )
  - 不支持的情况
    - 方法中引用了全局的变量，作用范围会失效
  - 安全的类型转换：as? 
  - 编码建议
    - 尽可能使用val 来声明不可变引用，让程序的含义更加清晰确定
    - 尽可能减少函数对外部引用的访问，增加函数的在多线程环境的不可控因素
    - 必要时创建局部变量指向外部变量，避免因为改变这个变量引起程序错误



## 表达式

- 变量与常量
  - var，变量
    - int a =2
    - var a :Int=2
  - val，只读变量
    - final int a=2
    - val a:Int =2 ， 只读变量  作为属性可以定义  get()(return random()) 可能会变
    - val person = Person("Jay",18) ，常量引用，在堆上创建对象，对象可变引用不变
    - 运行时常量：运行时确定值，调用处通过引用获取值
  - const，常量
    - static final int b=3
    - const val b=3
    - 编译期常量：编译期确定，用常量值替换调用处
- 分支表达式
  - if() {} else {}
    - c=a==3?4:5;
    - c=if(a==3) 4 else 5
  - when
    - switch(a) {case:0 c=5:break ; default c=0; }
    - when(a){ 0-> c=5 else -> c=0 }
    -  when{ x is String -> x.length},条件转移到分支 
    - c=when{ x is String -> x.length else -> -1}  表达式的返回值
    - c=when(val input =readLine()){null->0 else -> input.length}  条件可以是表达式
  - try/catch 
    - try{c=a/b } cache(Exception e) {e.printStackTrace();  c=0}
    - c= try{a/b} cache(e: Exception){e.printStackTrace() 0 }
    - Kotlin 中没有受检查的异常，不会强制提示try..cache 因为受检异常会让函数调用强制产生副作用
- 运算符与中缀表达式
  - 运算符重载 仅限官方指定的符号
    - ==  与 equals
      - "hello" == "world"
      - "hello".equals("world")
    - plus 与 +
      - 2+3 
      - 2.plus(3)
    - in 与 containes
      - 2 in list
      - list.contains(2)
    - get/set 与 []
      - map.get("name")
      - map["name"]
      - map.set("name","Jay)
      - map["name"]="Jay"
    - conpareTo 与 >
      - 2>3
      - 2.compareTo(3)>0
    - invoke 与 ()
      - func()
      - func.invoke()
    - 重载自己的运算符
      - operator
      - operator fun Complex.get(index: Int) :Double= when (index) {0-> this.real 1->this.image}; c1[0] c2[2]
  - 中缀表达式
    - infix
      - 2.to(3)===> 2 to 3 （代码像写文章一样）
      - infix fun <A,B> A.to(that:B) : Pair<A,B>= Pair(this,that)
      - class Book;  class Desk;  infix fun Book.on(desk:Desk )   val book= Book() ; val desk=Desk() ;  book on desk
- Lambda 表达式与匿名函数
  - 匿名函数
    - fun func(){println("Jay")}
    - val func= fun(){ println("Jay") } ；  func 是变量名；调用时用变量调用 func()
    - 匿名函数的类型：val func:**() -> Unit**= fun(){ println("Jay")} 
  - Lambda 表达式
    - Runnable lambda=()->{ System.out.println("Jay"); }
    - val lambda={ println("Jay") }
    - 类型和匿名函数一样：val lambda：**() - > Unit** ={ println("Jay") } 
    - Java Lambda 表达式是接口类型的语法糖：interface Function1{ void invoke(int p)}  Function f1= **(p)->**{System.out.println("Jay"); }
      - 接口类型：SAM : Single abstract method 
    - Kotlin Lambda 表达式匿名函数语法糖：
      -  val f1:**(int)-> Unit** ={p:Int -> println(p) } 
      - val f1:**Function1<int,Unit>** = {p:Int-> println(p)}
    - 表达式参数类型从表达式类型推断而来
      - val f1:Function1<**int**,Unit> = {**p** -> println(p)}
    - 表达式类型从参数声明推断而来
      - val f1={**p:Int** -> println(p)}
    - 返回值是表达式最后一行
      - val f1={p:Int -> println(p) **"Jay"** } //接收整形返回字符串
    - 参数省略形式：只有一个参数，参数名默认为 **it**
      - val f1:Function1<int,Unit> = { println( **it** )}
    - 最后一个是lambda 表达式，调用时可以将{} 移到（）外面
    - Java 与 Kotlin Lambda 表达式的不同



## 高阶函数

- 参数类型包含函数

  - val block:() -> Unit={println("Jay")} ;  book()  // 匿名函数/ Lambda 表达式
  - fun f1(block:() -> Unit) {block()}

- 返回值类型为函数

  - val time :()->Long= {return System.currentTimeMillis()  }
  - fun f2: ()->Long { return {System.currentTimeMillis()}}
  - fun f2: ()->Long { return time() }

- 常见的高阶函数

  - inline fun IntArray.forEach(action:(Int)->unit):Unit{ for(element in this) action(element) }
    - 扩展方法、接收action 函数类型作为参数、调用函数action
    - action函数的作用将循环的每个元素回调出去
    - 例如：val array={1,2,3,4};  array.forEach{it-> println(it )} ; array.forEach{::println} ； ::println的类型也是 (Int)->unit
  - inline fun <R> IntArray.map(transform:(Int)->R):List<R>{ return mapTo(ArrayList<R>(size), transform)}

- 高阶函数的调用

  - inline fun IntArray.forEach(action:(Int)->unit):Unit{ for(element in this) action(element) }
  - intArray.forEach **({e:Int-> println("Hello: $e")})**
  - intArray.forEach **()** {e:Int-> println("Hello: $e")} // 最后一个参数是函数类型可以将{} 移动到() 的外面
  - intArray.forEach {e:Int-> println("Hello: $e")} // 只有一个Lambda 表达式作为参数可以省略 ()
  - intArray.forEach {it-> println("Hello: $it")} //只有一个参数的Lambda 表达式，参数名默认为 **it**
  - intArray.forEach { println("Hello: $it")}

- 几个有用的高阶函数

  | 函数名 | 函数签名 | 描述                                            |
  | ------ | -------- | ----------------------------------------------- |
  | let    |          | 返回表达式结果：val r=X.let{x->R}               |
  | run    |          | 返回表达式结果：val r=X.run{this:X->R}          |
  | also   |          | 返回 Receiver 本身：val x=X.also{x->Unit}       |
  | apply  |          | 返回 Receiver 本身：val x=X.apply{this.X->unit} |
  | use    |          | 自动关闭资源：val r=Closeable.use{c->R}         |

  



### 内联函数

- inline
  - **inline** fun IntArray.forEach(action:(Int)->unit):Unit{ for(element in this) action(element) }
-  将内联函数体内的代码替换到调用处
  - val intArr =intArrayOf(1,2,3,4,5)
  - intArr .forEach { println("Hello: $it")}
  - 等价于：val action:(Int)->unit={ it-> println("Hello: $it")} ;   for(element in intArr){ action(element) }
- 限制
  - public/ protected的内联方法只能访问对应类的public成员
  - 内联函数的内联函数参数不能被存储(赋值给变量)
  - 内联函数的内联函数参数只能传递给其他内联函数参数



### 集合操作的高阶函数

- 集合的遍历操作
  - for i 
    - for (int i=0; i<=10;i++) { System.out.println(i); }
    - for(i in 0..10) { println(i)}
  - for each
    - for(int e:list) {System.out.println(e); }
    - for(e in list) {println(e)}
  - forEach 函数
    - list.forEach((e)->{System.out.println(e); })
    - list.forEach{println(it)}
    - forEach 函数不能continue 或者 break
- 变换操作
  - filterNot
  - filter
    - 保留满足条件的元素
    - [1,2,3,4]-->e%2=0-->[2,4]
    - Java: list.stream().filter(e-> e%2==0) 
      - 懒加载
    - Kotlin: list.filter{e->e%2==0}
    - Kotlin: 序列：list.asSequence().filter{e%2==0}
      - 懒序列，在调用终止操作是才开始遍历
  - map
    - 集合中的所有元素映射到其他元素构成新集合
    - [1,2,3,4]-->y=x*2+1-->[3,5,7,9]
    - Java: list.stream().map(e-> e*2+1) 
    - kotlin: list .map {e->e*2+1}
    - kotlin: list.asSequence().map{e*2+1}
  - flatMap
    - 集合中的所有元素打平后映射到新集合并合并这些集合得到新集合
    - [1,2,3]-->[0,0,1,0,1,2]
    - Java: list.stream.flatMap(e->{arrayList.add(e);  return arrayList.stream })
    - Kotlin: list.flatMap{0 until it } // range 也是Iterator
    - list.assequense().fflatMap{ (0 until it).asSequense() }.joinToString.let(::println)
  - zip
    - todo
  - groupBy
    - todo
  - rxJava
    - map
    - flatMap
- 集合的聚合操作
  - sum
    - 所有元素求和
  - reduce
    - 将元素依次按规则聚合,结果与元素类型 
  - fold:折叠操作
    - 给定初始化值，将元素按规则聚合,结果与初始化值类型一致
    - list.fold(StringBuild()){acc, i -> acc.append(i)}
    - 返回值类型为StringBuilder 作为下一个元素的acc
- SAM 转换
  - SAM: Single Abstract Method
    - 调用时可用Lambda 表达式做转换后作为参数
    - 转换限制条件
      - Java: 只有一个方法的接口(接口可以是Java也可以是Kotlin)
      - Kotlin: 必须是只有一个方法的 **Java接口** 的 **Java 方法** ， Kotlin 方法在1.3后可添加编译器参数支持
  - Java 匿名内部类 和 Lambda 表达式
    - new Runnable() { @Override public void run(){Systom.out.println("Jay");}}
    - ()->Systom.out.println("Jay")
  - Kotlin 匿名内部类和  Lambda 表达式
    - object: Runnable{override fun run(){println("Jay")}}
      - 匿名内部类的简写方式：Runnable{ println("Jay" }
      - 原理：fun Runnable(black:()>Unit) :Runnable{ return object:Runnable{override fun run(){ black() } }}
      - 该方法由编译器自动生成
    - {println("Jay")}
    - ()-Unit 类型的Lambda转换为了Runnable 
    - Kotlin中的Lambda 表达式本身有它自己的类型 即：()-Unit 
    - 原理：内联、调用Lambda ：object: Runnable{override fun run(){  **{println("Jay")}()**  }}
    - 本质上是创建了一个Runnable 包装了一下Lambda,并非直接转换
- 高阶函数实践
  - Html DEL 
  - Gradle DSL



## 类型进阶

- 构造器
  - 主构造器	
    - init块 与 Java 代码块
  - 副构造器
  - 推荐主构造器＋默认参数初始化类，保证类的初始化过程只有一条
  - 工厂函数构造类对象
- 可见性
  - 在 Kotlin 中有这四个可见性修饰符：`private`、 `protected`、 `internal` 和 `public`。 如果没有显式指定修饰符的话，默认可见性是 `public`。
  - public 与Java相同 默认
  - internal: 模块内可见
    - IntelliJ IDEA模块
    - Maven工程
    - Gradle SourceSet
    - Ant任务中一次调用<kotlinc> 的文件
    - 可以认为是一个jar包或aar文件
    - 一般由SDK或公共组件开发者用于隐藏模块内部细节实现
    - default可通过外部创建相同包名来访问,访问控制非常弱
    - default会导致不同抽象层次的类聚集到相同包之下
    - internal可方便处理内外隔离, 提升模块代码内聚减少接口暴露
    - internal修饰的Kotlin类或成员在Java当中可直接访问
  - protected: 类内以及子类可见
    - 只能修饰类内成员
  - private: 类或文件内可见
- 属性和延迟初始化
  - 初始化为null
    - var tv:TextView?=null  
    - 访问： tv?.text  tv!!.text
    - 增加代码复杂度;初始化与声明分离;调用处需要做判空处理
  - lateinit 不推荐
    - lateinit var tv:TextView
      - tv.text
      - if(::tv.isInitialized){tv.text} //判断lateinit的属性是否初始化；
    - 初始化与声明分离;调用处虽无需判空处理,但潜在的初始化问题可能被掩盖
    - lateinit会让编译器忽略变量的初始化,不支持Int等基本类型
    - 开发者必须能够在完全确定变量值的生命周期下使用lateinit
    - 不要在复杂的逻辑中使用lateinit ,它只会让你的代码更加脆弱
    - Kotlin 1.2加入的判断lateinit属性是否初始化的API最好不要用
  - lazy
    - val tv by lazy{ findviewbyId() }
    - 只有在属性第一次被访问的时候才会执行 Lambda 中的代码
    - 初始化与声明内聚;无需声明可空类型
- 代理
  - **我**代替**你**处理**它**
  - 接口代理
    - 对象X代替当前类A实现接口B的方法
    - class ApiWrapper(val api:Api):Api by apiDeleget{ 只需要重写需要的方法}
    - 对象 apiDeleget 代替 ApiWrapper实现接口Api 
  - 属性代理
    - 对象X代替属性a实现getter/setter方法
    - lazy
      - val name by lazy{"Jay"} //代理了name 属性的geter方法
      - 接口 Lazy 的实例代理了对象 Person 实例的属性 name 的 gettter 
    - observable
      - var state:Int by Delegates.observable(0){property,oldValue,newValue-> }
      - ObservableProperty 的实例代理了属性tate 的getter 和 settter
    - vetoable
      - todo
  - 代理实战案例读写Properties 文件
- 单例 Object
  - public class Singleton{public static final Singleton INSTANCE= new Singleton() ;}
  - Object Singleton{}
    - 饿汉式单例，类加载时实例化对象
    - Singleton 既是类名也是对象名
    - 模拟静态成员 @JvmStatic @JvmField
    - 普通类的静态成员：半生对象：与普通类同名的Object
    - 不能定义构造器，可以定义init代码块
    - 可以继承类实现接口
- 内部类
  - 静态内部类、非静态内部类
    - public class Outer{calss Inner{}  static calss StaticInner{}}
    - class Outer{inner class Inner{}  class StaticInner{}}
      - val inner=Outer().Inner()
      - val staticInner=Outer.staticInner()
  - 内部 Object
    - object OuterObject{object StaticInnerObject{}}
    - 不存在非静态的情况
  - 匿名内部类
    - new Runnable (){ @Override public void run(){....}}
    - object:Runnable{override fun run(){....}}
      - object省略了名字即匿名
      - 可以继承和实现多个接口，Java 不支持
    - 本地类和本地函数
      - 方法内部定义类或方法
      - Java 只有本地类没有本地方法

- 数据类

  - **data** class Person(val name :String)
  - 定义在主构造器中的属性又称为 component 
    - val name =person.component1()
    - 编译器基于component 自动生成了 equals/hashcode/toString/copy
  - 数据类的解构
    - Pair
  - 数据类不可被继承
    - 如果可以被继承会违反对称性，和传递性
  - component 不可以自定义 Getter 和 Setter 
  - 主构造器的属性最好是 val 不可变
  - 破解 final 主构造器 的限制
    - NoArg 插件可以生成一个午餐构造
    - AllOpen 插件 可以去除 final 的限制

- 枚举类

  - enum Num{ONE, TWO}
    - Num.ONE.name()
    - Num.ONE.ordinal()
  - enum class  Num {ONE, TWO}
    - Num.ONE.name
    - Num.ONE.ordinal
  - 枚举构造器
  - 枚举类实现接口
  - 枚举区间，判断在某几个状态之内
  - 枚举比较，按照 ordinal 依次递增
  - when 分支表达式中不需要 else->{} 分支，它是完备的

- 密封类

  - seald class PlayerState
  - 特殊的抽象类
  - 子类必须定义在和自身文件相同的文件,构造器私有
  - 子类个数是有限的
  - 枚举类 VS 密封类

- 内联类

  - inline class  BoxInt (val value:Int) 包装 int 类型

  - 对另外一个类型的包装

  - 类似于Java 中对基本类型的装箱后的类型

  - 1.3 引入 ，内侧阶段

  - 可以实现接口，不能继承和被继承

  - 属性必须 val 没有 backing field

  - 使用场景

    - 无符号类型 UInt
    - 模拟枚举，编辑器会优化为基本类型

  - 限制

    - 主构造器必须有且仅有一个只读属性
    - 不能定义有backing-field的其他属性
    - 被包装类型必须不能是泛型类型
    - 不能继承父类也不能被继承
    - 内联类不能定义为其他类的内部类

    

## 泛型

- 概念
  - 泛型是一-种类型层面的抽象
  - 泛型通过泛型参数实现构造更加通用的类型的能力
  - 泛型可以让符合继承关系的类型批量实现某些能力
  
- 函数泛型
  
  - fun <T> maxOf(a:T, b:T):T
  
- 类泛型
  
  - class List<T>
  
- 泛型参数
  
  - 形参
  - 实参
  - where 
  
- 泛型的约束

  - public static <T **extends** Comparable<T>> T maxOf( T a, T b){if(acompareTo(b)>0) return a; else return b; }
  - fun <T:Comparable<T>> maxOf(a:T,b:T):T(){return if(a>b) a else b}
  - T 需要实现Comparable 接口，比如：String，Int
  - 多个约束
    - fun <T> callMax(a:T,b:T) **where** T:Comparable<T>, T:()->Unit{ if(a>b) a() else b() }
    - 比较完大小后再调用 T
  - 多个泛型参数
    - public static<T extends Comparable<T> & Supplier<R> , R extends Number> R callMax(T a , Tb){ if(acompareTo(b)>0) return a.get(); else return b.get(); }
    - fun <T,R> callMax(a:Tmb:T) :R where T:Comparable<T>, T:()->R,R:Number    { return if(a>b) a() else b() }
      - 比较完大小后调用T,T的返回值是R ,R是一个Number 类型
    - class Map<K,V> where K:Serializable, V:Comparable<V> 
      - K可序列化，V可比较

  

- 泛型的型变
  - 泛型实参的继承关系对泛型类型的继承关系的影响
  - 协变
    - 继承关系一致
    - out 生产者
    - 如果：Int:Number  那么 List<Int> : List<Number>，能提供子类的可以提供父类，反之不可以
    - class BookStore<out T:Book> { fun getBook():T {return T }}
      - EduBook : Book
      - val eduBookStore: BookStore<EduBook>
      - val bookStore : BookStore<Book>
      - val book:Book=bookStore.getBook()
      - val book:Book=eduBookStore.getBook() // eduBookStore 可以拿到书籍
      - val eduBook:EduBook=eduBookStore.getBook()
      - val eduBookLEduBook=bookStore.getBook() // bookStore只能获取书籍，不能拿到教辅书籍
    - 协变点
      - 定义：函数的返回值类型为泛型参数
      - operator fun <T> List<T>.get(index : Int) :**T** = when{}
      - 意义：泛型类型作为生产者的时候
    - 存在协变点的类的泛型参数必须声明为协变或不变
    - 当泛型类作为泛型参数类实例的生产者时用协变
  - 逆变
    - 继承关系相反
    - in 消费者
    - Int:Number  那么 List<Number> : List<Int>   能存放父类的的一定可以存放子类, 反之不可以
    - class DustBin<in :T:Waste>  { fun put(t:T){//todo }}
      - DryWaste: Waste
      - 
    - 逆变点
      - 定义：函数参数类型为泛型参数
      - 意义：泛型参数作为消费者的时候
    - 存在逆变点的类的泛型参数必须声明为逆变或不变
    - 当泛型类作为泛型参数类实例的消费者时用逆变
  - 不变
    - 没有继承关系
  - 破坏型变的规则
    - @UnsafeVariance 注解
- 星投影 <*>
  - 可用在变量类型声明的位置
  - 可用以描述一 个未知的类型
  - 所替换的类型在:
    - 协变点返回泛型参数上限类型
      - class QueryMap <out K:CharSequence, out V:Any>{ fun getKey():K, fun getValue():V}
      - val queryMap :QueryMap< *, *> = QueryMap<String,Int>
      - queryMap.getKey() // K 的上限  CharSequence
      - queryMap.getValue() //V 的上限 Any
    - 逆变点接收泛型参数下限类型
      - class Function<in P1, in P2>{ fun invoke(p1:P1, p2:P2 )= Unit }
      - val f: Function<* ,*> Function < Number ,Any>()
      - f.invoke() //需要传入的实参是 P1 P2的下限，也就是Nothing  所以这里无法调用
  - 不能直接或间接应用在属性或函数上
    - QueryMap<String, * >() //间接作用到属性上
    - maxOf<*>(1, 3) // 直接作用到函数上
  - 适用于作为类型描述的场景
    - val queryMap: QueryMap<*, *>
    - if(f is Function<*, *>)[ ... ]
    - HashMap<String, List<*>>()
- 泛型的实现原理
  - 伪泛型
    - 编译时擦除类型，运行时无实际类型生成
    - 语言：Java, Kotlin，TypeScript
  - 真泛型
    - 编译时生成真实类型，运行时也存在改类型
    - 语言：C#, C++模板
  - 泛型擦除
    - 编译前
      - fun < T:Comparable< T > > maxOf(a:T,b:T) :T{ return if (a>b) a else b}
    - 编译后
      - fun maxOf(a:Comparable,b:Comparable) :Comparable { return if (a>b) a else b}
    - 擦除T 替换为T的上限类型
    - 编译前：List< String > 字节码： List  运行时：List 
    - 需要类型强转
    - 擦除的泛型类型在 字节码的 **signature** 中 这个信息是可以被混淆，可以通过 -keepattributes Signature 防止混淆 
  - 泛型类型无法当做真实类型
    - fun < T > testFun(a:T){
    - val t=T() // 不知道有无无参构造
    - val tArray=Array< T > (3){} //  数组的泛型不擦除，
    - val tClass=T::calss.java // 擦除
    - val tList=ArrayList< T > () // 这个可以 ，
    - }
  - 内联特化 reified  
    - 破除泛型的限制
    - fun < **reified**  T > testFun(a:T){
    - val t=T() // 知道具体类型也不知道有无无参构造
    - val tArray=Array< T > (3){} //  内联后可以确定类型
    - val tClass=T::calss.java // 内联后可以确定类型
    - val tList=ArrayList< T > () // 这个可以 ，
    - }
    - 实际应用
    - Gson
      - fun < T > Gson.fromJson(json : String) :T = fromJson(json**, T::calss.java**) //  不可以传入 **T::calss.java**
      - inline fun <  reified T > Gson.fromJson(json : String) :T = fromJson(json**, T::calss.java**) //  内联优化后可以传入 
      - Gson().fromJson< Person> (json) 
  - 案例
    - 模拟 Selt Tyoe 通过泛型参数在父类中使用子类类型
    - 泛型+属性代理实现对象注入

## 反射

- 反射的概念
  - 反射是允许程序在运行时访问程序结构的一类特性
  - 程序结构包括:类、接口、方法、属性等语法特性
- Kotlin 反射依赖库
  - Kotlin 反射API 是在 Jdk 中反射的基础上包装了一层
  - implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
  - implementation "org.jetbrains.kotlin:kotlin-**reflect**"

- 反射的常见用途
  - 列出类型的所有属性、方法、内部类等等
  - 调用给定名称及签名的方法或访问指定名称的属性
  - 通过签名信息获取泛型实参的具体类型
  - 访问运行时注解及其信息完成注入或者配置操作
- 反射常用的数据结构
  - KType--Type
    - 描述未擦除的类型或泛型参数等，例如Map<String, Int> ;可通过typeOf或者以下类型获取对应的父类、属性、函数参数等
  - KClass--Class
    - 描述对象的实际类型,不包含泛型参数,例如Map ;可通过对象、类型名直接获得
  - KProperty-- Field 
    - 描述属性 ,可通过属性引用、属性所在类的KClass获取
  - KFunction--Method
    - 描述函数,可通过函数引用、函数所在类的KClass获取
- Java反射 VS  Kotlin反射
  - Java反射
    - 优点:无需引入额外依赖,首次使用速度相对较快
    - 缺点:无法访问Kotlin语法特性,需对Kotlin生成的字节码足够了解
  - Kotlin反射
    - 优点:支持访问Kotlin几乎所有特性, API设计更友好
    - 缺点:引入Kotlin反射库(2.5MB ,编译后400KB)，首次调用慢
    - 为什么首次调用比 Java 慢一两个数量级
      - Kotlin 的反射信息写到了MeteData 注解中，通过protocolBuffer的结构序列化为二进制写入，读取的时候需要反序列化的过程
- 反射API
  - Class<String> jClass =String.class
  - val kClass :KClass<String> = String::class
  - 转换  kClass.java, jClass.kotlin
  - todo
- 反射案例
  - 反射获取泛型实参的方法
    - 方法的返回值泛型实参
    - 父类中获取子类传入的泛型实参
  - 为数据类实现DeepCopy
  - Model映射
  - 可释放对象引用的不可空类型
  - 插件化



## 注解

- 概念
  - 注解是对程序的附加信息说明
  - 注解可以对类、函数、函数参数、属性等做标注
  - 注解的信息可用于源码期(文档中，@Deprecate)、编译期(编译器需要的，注解处理器，noArg)、运行期(反射时，@MetaData)
- 定义
  - public **@interface** Api 
  - **annotation class** Api //注解类
- 限定标注的对象
  - @Target(AnnotationTarget.CLASS)
  - CLASS：限定为类，接口或对象，注释类也包括在内
  - FUNCTION：限定为函数
  - LOCAL_VARIABLE：限定为函数或构造函数的参数
  - FIELD：限定为属性
  - ...
- 指定作用的时机
  - @Retation(AnnotationRetention.RUNTIME)
  - SOURCE：作用于源码期
  - BINARY：作用于编译期
  - RUNTIME：作用于运行时
- 注解参数
  - 参数类型支持以下类型以及它们组成的数组
    - 因为注解需要再编译期使用，所以一些自定义的类型无法被使用
    - 基本类型、KClass、枚举、其它注解
- 常见的内置注解
  - 用于标注注解的注解
    - 包位置：kotlin.annotation.*
    - public annotation class **Retention**(val value: AnnotationRetention = AnnotationRetention.RUNTIME)
    - public annotation class **Target**(vararg val allowedTargets: AnnotationTarget)
  - 标准库中一些通用用途的注解
    - 包位置：kotlin.*
    - @Metadata
      - Kotlin的反射的信息通过该注解附带在该元素上
    - @UnsafeVariance
      - 泛型用来破除型变限制
    - @Suppress
      - 用来去除编译器警告，警告类型作为参数传入
  - 用于与 Java 虚拟机交互的注解
    - 包位置：kotlin.jvm.*
    - @JvmField：生成Java Field
    - @JvmStatic：生成静态成员
    - @Synchtonized 标记函数为同步函数
    - @Throws：标记函数抛出的异常类型
    - @Volatile：生成volatile 的Field
- 实践
  - 仿Retrofit 请求网络
  - 注解+反射实现Model 映射
  - 注解+注解处理器+反射实现Model 映射
    - 注解处理器原理
      - Java编译过程：*.java -->AST(构建抽象语法树)-->Symbol(符号表填充)--> *.class
      - APT执行过程：AST/Symbol --> **APT** -->*.java-->AST(构建抽象语法树)-->Symbol(符号表填充)--> *.class
    - JavaPoet
    - KotlinPoet
    - [aptUtils](https://github.com/enbandari/Apt-Utils)



## Kotlin 的编译器插件

- AllOpen 插件
  - 破除final限制
  - 不是 final 的跳过
  - 判断是否被对应的注解标注
  - 如果是就变成open
  - 如果是源码显式声明为final的，不处理
- NoArg 插件
  - 生成无参构造器
  - ASM 操作字节码
- android-extensions
  - 合成View 属性，生成对应字节码
- kotlin-serialization 
  - 为徐泪花类生成 serializer , 支持夸平台

