# Kotlin 协程详解

`kotlinx.coroutines` 是由 JetBrains 开发的功能丰富的协程库



### 线程 & 协程

![图片](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211010235947.gif)





有的人会将协程比喻成: 线程的封装框架，从宏观角度是可以这么理解，但 Kotlin 官方最喜欢说: 协程有点像轻量级的线程

**从包含关系上看，协程跟线程的关系，有点像 "线程与进程的关系"，毕竟，协程不可能脱离线程运行**

协程虽然不能脱离线程而运行，但可以在不同的线程之间切换

高效和轻量，都不是 Kotlin 协程的核心竞争力。

Kotlin 协程的核心竞争力在于: 它能简化异步并发任务

### 协程的作用

- 协程是可以由程序自行控制挂起、恢复的程序
- 协程可以用来实现多任务的协作执行
- 协程可以用来解决异步任务控制流的灵活转移，也就是异步代码同步化，降低异步程序的设计复杂度



### 挂起函数

- 挂起函数:以suspend修饰的函数
- 挂起函数只能在其他挂起函数或协程中调用
- 挂起函数调用时包含了协程“挂起”的语义
- 挂起函数返回时则包含了协程“恢复”的语义
- 通过 `suspend` 修饰的挂起函数编译器会在该函数中自动添加 Continuation<> 类型的参数
- Continuation 泛型参数类型就是挂起函数的返回值类型
- 挂起函数的函数签名：foo(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
- 挂起函数的类型：suspend （）-> Unit
- 挂起函数的返回值会作为Continuation<> 的泛型参数，编译后的挂起函数还有个 Any 的返回值，这个Any 会指代不同情况
- 如果正常挂起了就返回一个挂起标志对象：COROUTINE_SUSPENDED
- suspendCoroutine 可以获取挂起函数的 continuation 然后可以自定义挂起中的操作
- 真正的挂起必须异步调用了 resumeWith ，没有切线程调用 resumeWith 不是真正的挂起



Continuation 挂起函数的最后一个参数，编译器自动追加，实际上就是一个回调接口

```kotlin
public interface Continuation<in T> {
    //与此 Continuation 相对应的协程的上下文。
    public val context: CoroutineContext
		//继续执行相应的协程，将成功或失败的结果作为最后一个暂停点的返回值。
    public fun resumeWith(result: Result<T>)
}
//成功的回调方法，或者直接调用 resumeWith
public inline fun <T> Continuation<T>.resume(value: T): Unit =resumeWith(Result.success(value))
//失败的回调方法
public inline fun <T> Continuation<T>.resumeWithException(exception: Throwable): Unit =resumeWith(Result.failure(exception))
```

挂起函数函数签名

```kotlin
//以下声明具有相同的 JVM 签名: foo(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
suspend fun foo() {}
fun foo(continuation: Continuation<Unit>): Any {} //Continuation 泛型参数类型就是挂起函数的返回值类型

suspend fun bar(a: Int): String = "Hello"
fun bar(a: Int, continuation: Continuation<String>): Any = "Hello"
```

suspendCoroutine  获取挂起函数的 continuation

```kotlin
suspend fun getUserSuspend(name: String) = suspendCoroutine<User> { continuation ->
    githubApi.getUserCallback(name).enqueue(object : Callback<User> {
        override fun onFailure(call: Call<User>, t: Throwable) =
            continuation.resumeWithException(t)
        override fun onResponse(call: Call<User>, response: Response<User>) =
            response.takeIf { it.isSuccessful }?.body()?.let(continuation::resume)
                ?: continuation.resumeWithException(HttpException(response))
    })
}

val user = getUserSuspend("jaydroid1024")
showUser(user)
```

### 协程的开启与创建

SuspendLambda：是协程函数体

SafeContinuation：仅在挂起点是出现

Intercepted: 拦截器在每次执行协程体时调用

开启

```kotlin
public fun <T> (suspend () -> T).startCoroutine(
    completion: Continuation<T>
) {
    createCoroutineUnintercepted(completion).intercepted().resume(Unit)
}

public fun <R, T> (suspend R.() -> T).startCoroutine(
    receiver: R,
    completion: Continuation<T>
) {
    createCoroutineUnintercepted(receiver, completion).intercepted().resume(Unit)
}
```

创建

```kotlin
public fun <T> (suspend () -> T).createCoroutine(
    completion: Continuation<T>
): Continuation<Unit> =
    SafeContinuation(createCoroutineUnintercepted(completion).intercepted(), COROUTINE_SUSPENDED)

public fun <R, T> (suspend R.() -> T).createCoroutine(
    receiver: R,
    completion: Continuation<T>
): Continuation<Unit> =
    SafeContinuation(createCoroutineUnintercepted(receiver, completion).intercepted(), COROUTINE_SUSPENDED)
```



Kotlin协程的启动模式
DEFAULT：立即开始调度协程体个调度前若取消则直接取消
ATOMIC：立即开始调度，且在第一个挂起点前不能被取消
L AZY：只有在需要(start/join/await)时开始调度
UNDISPATCHED：立即在当前线程执行协程体,直到遇到第一个挂起点(后面取决于调度器)



### 协程上下文

- 协程执行过程中需要携带数据，CoroutineContext就是携带数据的载体
- 索引：CoroutineContext.Key
- 元素： CoroutineContext. Element
- 拦截器：ContinuationInterceptor,拦截 Continuation 



### 协程协作流程

- 协程体内的代码都是通过Continuation.resumeWith调用
- 每调用一次label加1 ,每- -个挂起点对应于一个case分支
- 挂起函数在返回COROUTINE SUSPENDED时才会挂起











## 协程实践

在本动手教程中，我们将熟悉协程的概念。协程为我们提供了异步和非阻塞行为的所有好处，但又不缺乏可读性。我们将看到如何使用协程在不阻塞底层线程和不使用回调的情况下执行网络请求。

```
Github Token：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
```

- 为什么以及如何使用挂起函数来执行网络请求。
- 如何使用协程并发发送请求。
- 如何使用通道在不同协程之间共享信息。
- 协程与其他现有解决方案的对比。





## 参考

- [新版Kotlin从入门到精通](https://coding.imooc.com/class/398.html)
- [开发者说·DTalk | Kotlin Jetpack 实战: 图解协程原理](https://mp.weixin.qq.com/s/qXjUFWTiUoLs9TON-SJD7w)

- [Kotlin 语言中文站 | coroutines-guide](https://www.kotlincn.net/docs/reference/coroutines/coroutines-guide.html)

- [Kotlin 语言官网 | coroutines-guide](https://kotlinlang.org/docs/coroutines-guide.html)

- [ Github | kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
-  [Introduction to Coroutines and Channels Hands-On Lab](https://play.kotlinlang.org/hands-on/Introduction to Coroutines and Channels/01_Introduction) 

