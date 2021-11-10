# Android Framework|Handler 消息机制详解

![image-20211007211040939](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20211007211040939.png)

## 1. Handler 消息机制相关类简介

Java Level： `Handler.java`、`Looper.java` 、`ThreadLocal.java`、`MessageQueue.java`、`Message.java` 

C++ Level： `NativeMessageQueue.cpp`、 `Looper.cpp`

### 1.1 Handler.java

> 官方文档对 Handler的解释
>
> [Android Developers Docs | Handler](https://developer.android.com/reference/android/os/Handler)
>
> A Handler allows you to send and process `Message` and Runnable objects associated with a thread's `MessageQueue`. Each Handler instance is associated with a single thread and that thread's message queue. When you create a new Handler it is bound to a `Looper`. It will deliver messages and runnables to that Looper's message queue and execute them on that Looper's thread.
>
> There are two main uses for a Handler: (1) to schedule messages and runnables to be executed at some point in the future; and (2) to enqueue an action to be performed on a different thread than your own.
>
> Scheduling messages is accomplished with the `post(Runnable)`, `postAtTime(java.lang.Runnable, long)`, `postDelayed(Runnable, Object, long)`, `sendEmptyMessage(int)`, `sendMessage(Message)`, `sendMessageAtTime(Message, long)`, and `sendMessageDelayed(Message, long)` methods. The *post* versions allow you to enqueue Runnable objects to be called by the message queue when they are received; the *sendMessage* versions allow you to enqueue a `Message` object containing a bundle of data that will be processed by the Handler's `handleMessage(Message)` method (requiring that you implement a subclass of Handler).
>
> When posting or sending to a Handler, you can either allow the item to be processed as soon as the message queue is ready to do so, or specify a delay before it gets processed or absolute time for it to be processed. The latter two allow you to implement timeouts, ticks, and other timing-based behavior.
>
> When a process is created for your application, its main thread is dedicated to running a message queue that takes care of managing the top-level application objects (activities, broadcast receivers, etc) and any windows they create. You can create your own threads, and communicate back with the main application thread through a Handler. This is done by calling the same *post* or *sendMessage* methods as before, but from your new thread. The given Runnable or Message will then be scheduled in the Handler's message queue and processed when appropriate.

大概意思就是：

- Handler 是可以在一个与线程关联的 MessageQueue 上**发送和处理** Message 和 Runnable 的工具类

- 当创建一个新的 Handler 时，它会绑定一个 Looper。Handler 会将 Message 和 Runnable  发送到 Looper 的 MessageQueue 中，并最终会在该 Looper 的线程上执行它们。

- Handler实现线程间通讯的根本原理是通过内存共享的方案，Looper 是在线程中创建并通过 ThreadLocal 与其它线程的 Looper 隔离，也就是说我只要获取到某个线程的 Looper 就能向这个线程发消息，MessageQueue 是在构建 Looper 时创建的，Handler 绑定 Looper 后会持有 mLooper 和 mLooper.mQueue ，当 Handler 在其他线程发消息时就能通过 mLooper.mQueue 将消息发送到 Looper 所在线程的消息队列中并得到处理，这就完成了线程间的一次通讯。

- Handler 有两个主要用途：

  - **延迟执行：**安排 Message 和 Runnable 在将来的某个时间点执行

  - **线程切换：**将其它线程上的执行的操作消息加入到当前线程上的 MessageQueue 中，当前线程接收并做相应处理

- Handler **post 系列**的方法将 Runnable 对象排入队列，以便在接收到消息时回调它们

- Handler **send 系列**的方法可以将一个 Message 发送到 Handler 的 `handleMessage(Message)`方法中

- 当 posting  或者 sending 到 Handler 时且 MessageQueue  准备好后，Message 和 Runnable 可以立即执行也可以延迟执行

- 主线程的 Looper 是在程序运行时系统默认就开启的，该 Looper 已经对应的 MessageQueue  主要负责管理顶级应用程序对象(activities, broadcast receivers, etc）及其创建的任何 windows 。在开发时我们可以创建自己的工作线程，并通过 Handler 与主线程进行通信。例如你通过子线程下载一张图片然后将图片数据通过绑定了主线程 Looper 的 Handler 发送给主线程再由主线程显示到 ImageView 上。

### 1.2 Looper.java

> 官方文档对 Looper 的解释
>
> [Android Developers Docs | Looper](https://developer.android.com/reference/android/os/Looper)
>
> Class used to run a message loop for a thread. Threads by default do not have a message loop associated with them; to create one, call `prepare()` in the thread that is to run the loop, and then `loop()` to have it process messages until the loop is stopped.
>
> Most interaction with a message loop is through the `Handler` class.

大概意思就是：

- Looper 是用于在一个线程中运行的消息循环器。默认情况下，线程中没有与之关联的消息循环器，需要手动创建一个，如果要在某个线程中运行一个消息循环器需要调用 `Looper.prepare()` 创建一个Looper 并缓存在 ThreadLocal<Looper> 中与其它线程的 Looper 隔离开，每个线程只能创建一个 Looper，然后调用 `Looper.loop()` 开启一个循环不断从 MessageQueue 取消息、处理消息，直到循环停止或者调用 `Looper.quitSafely()` 或者`Looper.quit()` 方法手动结束消息循环器并回收所有消息。

- 大多数情况下与消息循环器的交互都是是通过 `Handler`类进行的。

- 主线程 Looper 搞特殊：在 Android 中当一个程序运行时已经默认开启了主线程的消息循环器，开启的地方是在 ActivityThread 类的 main 方法中的 `Looper.prepareMainLooper()`  prepareMainLooper 方法除了调用 prepare 创建 Looper 实例还会缓存主线程的消息循环器给一个静态变量  `private static Looper sMainLooper` 目的就是在非主线程中就能通过 sMainLooper 获取主线程的 Looper 了。

### 1.3 ThreadLocal.java

> 官方文档对 MessageQueue 的解释
>
> [Android Developers Docs | ThreadLocal](https://developer.android.com/reference/java/lang/ThreadLocal)
>
> This class provides thread-local variables. These variables differ from their normal counterparts in that each thread that accesses one (via its `get` or `set` method) has its own, independently initialized copy of the variable. `ThreadLocal` instances are typically private static fields in classes that wish to associate state with a thread (e.g., a user ID or Transaction ID).
>
> For example, the class below generates unique identifiers local to each thread. A thread's id is assigned the first time it invokes `ThreadId.get()` and remains unchanged on subsequent calls.
>
> Each thread holds an implicit reference to its copy of a thread-local variable as long as the thread is alive and the `ThreadLocal` instance is accessible; after a thread goes away, all of its copies of thread-local instances are subject to garbage collection (unless other references to these copies exist).

大概意思就是：

- ThreadLocal 可以创建线程局部变量，使用 ThreadLocal 创建的这种局部变量只能被当前线程访问，其他线程则无法访问和修改。

- ThreadLocal 底层是通过 ThreadLocalMap 来实现的（ >= android 24），每个 Thread 对象中都存在⼀个 ThreadLocalMap 的引用，Map 的 key 为 ThreadLocal 对象，Map 的 value 为需要缓存的值,当从ThreadLocal 获取一个本地变量是执行的过程如下：`Thread.currentThread().threadLocals.getEntry(ThreadLocal.this).value` ，通过这个操作就只能获取当前线程当前ThreadLocal 中 threadLocalHashCode 所对应的 value 值。

- 只要线程处于活跃状态并且可以访问 ThreadLocal 实例，每个线程都持有对其线程局部变量副本的隐式引用；线程消失后，它的所有线程本地实例副本都将进行垃圾回收（除非存在对这些副本的其他引用）。

- Looper 类就是利用了 ThreadLocal 的特性，保证每个线程只存在一个 Looper 对象。

- 在 Android API Level 23 之前，Android 中的 ThreadLocal 类和 JDK 中的 ThreadLocal 类存储数据的容器是有差别的，Android 23 中采用 `Values + Object[] table`  存储局部变量 , 在 Android API Level 24 中的 ThreadLocal 类已经与 JDK 一致，局部链路的存储容器改为了 `ThreadLocalMap+Entry[] table` 的方式。

### 1.4 MessageQueue.java

> 官方文档对 MessageQueue 的解释
>
> [Android Developers Docs | MessageQueue](https://developer.android.com/reference/android/os/MessageQueue)
>
> Low-level class holding the list of messages to be dispatched by a `Looper`. Messages are not added directly to a MessageQueue, but rather through `Handler` objects associated with the Looper.
>
> You can retrieve the MessageQueue for the current thread with `Looper.myQueue()`.

大概意思就是：

- MessageQueue 是供 Looper 调度的消息列表。消息不是直接添加到 MessageQueue 的，而是通过与 Looper 关联的 Handler 对象添加的。

- 可以通过 Looper.myQueue() 返回与当前线程关联的 MessageQueue 对象。 这个方法必须从运行了 Looper 的线程中调用，否则会空指针异常。

- MessageQueue 从名字上看是一个队列，实则却是一个普通的类 `public final class MessageQueue` ，其内部是通过 `Message mMessages;` 作为头节点封装的一个链表结构实现的整个队列的操作。

### 1.5 Message.java

> 官方文档对 Message 的解释
>
> [Android Developers Docs | Message](https://developer.android.com/reference/android/os/Message)
>
> Defines a message containing a description and arbitrary data object that can be sent to a `Handler`. This object contains two extra int fields and an extra object field that allow you to not do allocations in many cases.
>
> While the constructor of Message is public, the best way to get one of these is to call `Message.obtain()` or one of the `Handler.obtainMessage()` methods, which will pull them from a pool of recycled objects.

大概意思就是：

- Message 类用来定义一个包含了描述和数据对象的消息，可以将这样消息体发送到 Handler。该对象包含两个额外的 int 字段(arg1、arg2)和一个额外的对象字段(obj)，在特定情况下可以组合使用这些额外字段向目标线程发送消息。

- 虽然 Message 的构造函数是公共的，但获得其中之一的最佳方法是调用 Message.obtain() 或 Handler.obtainMessage() 方法之一，这两种方式会从已有的消息池中复用无效的 Message 实例，从而可以避免消息处理过多时频繁创建/销毁 Message 实例有可能导致的内存问题。

### 1.6 阅读 C++ 代码前的一些前置知识

> #### Linux Epoll 机制浅析
>
> select，poll，epoll都是IO多路复用的机制。I/O多路复用就是通过一种机制，一个进程可以监视多个描述符，一旦某个描述符就绪（一般是读就绪或者写就绪），能够通知程序进行相应的读写操作。但select，poll，epoll本质上都是同步I/O，因为他们都需要在读写事件就绪后自己负责进行读写，也就是说这个读写过程是阻塞的
>
> - select是个系统调用，一个系统调用：select四个宏: FD_ZERO FD_SET FD_CLR FD_ISSET
> - poll 是 unix 沿用 select 自己重新实现了一遍，唯一解决的问题是 poll 没有最大文件描述符数量的限制
> -  epoll是个模块，由三个系统调用组成，内核中由用文件系统实现，三个系统调用：epoll_create epoll_ctl epoll_wait
> - epoll带来了两个优势，大幅度提升了性能
>   - 基于事件的就绪通知方式 ，select/poll方式，进程只有在调用一定的方法后，内核才会对所有监视的文件描述符进行扫描，而epoll事件通过epoll_ctl()注册一个文件描述符，一旦某个文件描述符就绪时，内核会采用类似call back的回调机制，迅速激活这个文件描述符，epoll_wait()便会得到通知
>   - 调用一次epoll_wait()获得就绪文件描述符时，返回的并不是实际的描述符，而是一个代表就绪描述符数量的值，拿到这些值去epoll指定的一个数组中依次取得相应数量的文件描述符即可，这里使用内存映射（mmap）技术， 避免了复制大量文件描述符带来的开销
>
> **`epoll`**是Linux内核的可扩展I/O事件通知机制。于Linux 2.5.44首度登场，它设计目的旨在取代既有POSIX `select`与`poll`系统函数，让需要大量操作文件描述符的程序得以发挥更优异的性能（举例来说：旧有的系统函数所花费的时间复杂度为O(n)，`epoll`的时间复杂度O(log n)）。epoll 实现的功能与 poll 类似，都是监听多个文件描述符上的事件。
>
> `epoll` 底层是由可配置的操作系统内核对象建构而成，并以文件描述符(file descriptor)的形式呈现于用户空间。`epoll` 通过使用红黑树(RB-tree)搜索被监控的文件描述符(file descriptor)。
>
> 在 epoll 实例上注册事件时，epoll 会将该事件添加到 epoll 实例的红黑树上并注册一个回调函数，当事件发生时会将事件添加到就绪链表中。
>
> `epoll_create(int size)`来创建一个epoll的句柄。这个函数会返回一个新的epoll句柄，之后的所有操作将通过这个句柄来进行操作。在用完之后，记得用close()来关闭这个创建出来的epoll句柄。
>
> 
>
> #### 文件描述符
>
> 文件描述符在形式上是一个非负整数。实际上，它是一个索引值，指向内核为每一个进程所维护的该进程打开文件的记录表。当程序打开一个现有文件或者创建一个新文件时，内核向进程返回一个文件描述符。



> #### NDK 浅析
>
> 在Android OS上开发应用程序，Google提供了两种开发包：SDK和NDK
>
> Android NDK(Native Develop Kit) 是一套允许您使用原生代码语言(例如C和C++) 实现部分应用的工具集。在开发某些类型应用时，这有助于您重复使用以这些语言编写的代码库。
>
> 一般情况，是用NDK工具把C/C++编译为.so文件，然后在Java中调用。
>
> 由于 NDK 增加了开发的复杂度，在一般情况下是不被推荐使用的，但是在特定需求下，NDK是有很大的价值的，如：
>
> 1、在平台之间移植其应用
>
> 2、重复使用现在库，或者提供其自己的库重复使用
>
> 3、在某些情况下提性能，特别是像游戏这种计算密集型应用
>
> 4、使用第三方库，现在许多第三方库都是由C/C++库编写的，比如Ffmpeg这样库。
>
> 5、不依赖于Dalvik Java虚拟机的设计
>
> 6、代码的保护，由于APK的Java层代码很容易被反编译，而C/C++库反编译难度大。
>
> JNI，全称为Java Native Interface，即Java本地接口，JNI是Java调用Native 语言的一种特性。通过JNI可以使得Java与C/C++机型交互。即可以在Java代码中调用C/C++等语言的代码或者在C/C++代码中调用Java代码。由于JNI是JVM规范的一部分，因此可以将我们写的JNI的程序在任何实现了JNI规范的Java虚拟机中运行。
>
> Java调用C/C++在Java语言里面本来就有的，并非Android自创的，一般的Java程序使用的JNI标准可能和android不一样，Android的JNI更简单。
>
> #### JNIEnv 浅析
>
> Java语言的执行环境是Java虚拟机(JVM)，JVM其实是主机环境中的一个进程，每个JVM虚拟机都在本地环境中有一个JavaVM结构体，该结构体在创建Java虚拟机时被返回
>
> JavaVM 是 Java 虚拟机在JNI层的代表，JNI全局仅仅有一个JavaVM结构中封装了一些函数指针（或叫函数表结构），JavaVM中封装的这些函数指针主要是对JVM操作接口。
>
> JNIEnv 是当前Java线程的执行环境，一个JVM对应一个JavaVM结构，而一个JVM中可能创建多个Java线程，每个线程对应一个JNIEnv结构，它们保存在线程本地存储TLS中。因此，不同的线程的JNIEnv是不同，也不能相互共享使用。JNIEnv结构也是一个函数表，在本地代码中通过JNIEnv的函数表来操作Java数据或者调用Java方法。也就是说，只要在本地代码中拿到了JNIEnv结构，就可以在本地代码中调用Java代码。
>
> ##### JNIEnv和JavaVM的区别：
>
> - JavaVM：JavaVM是Java虚拟机在JNI层的代表，JNI全局仅仅有一个
> - JNIEnv：JavaVM 在线程中的代码，每个线程都有一个，JNI可能有非常多个JNIEnv；
>
> JNIEnv的创建与释放在C和C++ 中的实现是不同的
>
> ##### 与JNIEnv相关的常用函数
>
> - jobject NewObject(JNIEnv *env, jclass clazz,jmethodID methodID, ...)：第一个参数jclass class 代表的你要创建哪个类的对象，第二个参数,jmethodID methodID代表你要使用那个构造方法ID来创建这个对象
> - jstring NewString(JNIEnv *env, const jchar *unicodeChars,jsize len)： env是JNI接口指针；unicodeChars是指向Unicode字符串的指针；len是Unicode字符串的长度。返回值是Java字符串对象，如果无法构造该字符串，则为null。
> - ArrayType New<PrimitiveType>Array(JNIEnv *env, jsize length);指定一个长度然后返回相应的Java基本类型的数组
>
> #### JNI的引用
>
> 从Java虚拟机创建的对象传到C/C++代码就会产生引用，根据Java的垃圾回收机制，只要有引用存在就不会触发该该引用所指向Java对象的垃圾回收。
>
> 在JNI规范中定义了三种引用：局部引用（Local Reference）、全局引用（Global Reference）、弱全局引用（Weak Global Reference）。区别如下：
>
> - 局部引用，也成本地引用，通常是在函数中创建并使用。会阻止GC回收所有引用对象。例如使用NewObject，就会返回创建出来的实例的局部引用，使用DeleteLocalRef函数
>
> - 全局引用，可以跨方法、跨线程使用，直到被开发者显式释放。类似局部引用，一个全局引用在被释放前保证引用对象不被GC回收。和局部应用不同的是，能创建全部引用的函数只有NewGlobalRef，而释放它需要使用ReleaseGlobalRef函数
>
> - 弱全局引用，与全局引用类似，创建跟删除都需要由编程人员来进行，这种引用与全局引用一样可以在多个本地带阿妈有效，不一样的是，弱引用将不会阻止垃圾回收期回收这个引用所指向的对象，所以在使用时需要多加小心，它所引用的对象可能是不存在的或者已经被回收。
>
>   
>
> [参考链接](https://www.jianshu.com/p/87ce6f565d37)



> C++类型转换
>
> C 语言中强制类型转换的一般格式为：**（类型说明符）表达式**  实现的功能就是把表达式的值强制转换为类型说明符表示的类型。C++是兼容C的，因此C语言中的强制类型转换在C++中同样适用，除了这种强制类型转换方法外，C++还提供了四种类型转换方法，分别为
>
> - **static_cast<类型说明符>(表达式）**
> - **dynamic_cast<类型说明符>(表达式）**
> - **const_cast<类型说明符>(表达式）**
> - **reinterpret_cast<类型说明符>(表达式）**
>
> **reinterpret_cast 运算符并不会改变括号中运算对象的值，而是对该对象从位模式上进行重新解释**



> C++中的双冒号 :: 的作用
>
> 第一种，类作用域，用来标明类的变量、函数
>
> ```c++
> 	Human::setName(char* name);
> ```
>
> 第二种，命名空间作用域，用来注明所使用的类、函数属于哪一个命名空间的
>
> ```cpp
> 	std::cout << "Hello World" << std::endl; 
> ```



> C++ extern 关键字解析
>
> extern 可以置于变量或者函数前，以标示变量或者函数的定义在别的文件中，提示编译器遇到此变量和函数时在其他模块中寻找其定义。此外extern也可用来进行链接指定。



> C++ 头文件与源文件解析
>
> 头文件(.h)：
>
> 写类的声明（包括类里面的成员和方法的声明）、函数原型、#define常数等，但一般来说不写出具体的实现。
>
> 在写头文件时需要注意，在开头和结尾处必须按照如下样式加上预编译语句（如下）：
> \#ifndef CIRCLE_H
> \#define CIRCLE_H
>
> //你的代码写在这里
>
> \#endif
>
> 源文件（.cpp）：
>
> 源文件主要写实现头文件中已经声明的那些函数的具体代码。需要注意的是，开头必须#include一下实现的头文件，以及要用到的头文件。



> Andoird Jni 中 RefBase 类解析
>
> 在Android的源代码中，经常会看到形如：sp<xxx>、wp<xxx>这样的类型定义，这其实是Android中的智能指针。智能指针是C++中的一个概念，通过基于引用计数的方法，解决对象的自动释放的问题。
>
> Android的智能指针相关的源代码在下面两个文件中：[RefBase.h](https://android.googlesource.com/platform/system/core/+/master/libutils/include/utils/RefBase.h) 和 [RefBase.cpp](https://android.googlesource.com/platform/frameworks/native/+/jb-dev/libs/utils/RefBase.cpp)
>
> Android中定义了两种智能指针类型，一种是强指针sp（strong pointer），一种是弱指针wp（weak pointer）。
>
> 强指针与一般意义的智能指针概念相同，通过引用计数来记录有多少使用者在使用一个对象，如果所有使 用者都放弃了对该对象的引用，则该对象将被自动销毁。
>
> 弱指针也指向一个对象，但是弱指针仅仅记录该对象的地址，不能通过弱指针来访问该对象，也就是说不能通过弱指针来调用对象的成员函数或访问对象的成员变量。要想访问弱指针所指向的对象，需首先将弱指针升级为强指针（通过wp类所提供的promote()方法）。弱指针所指向的对象是有可能在其它地方被销 毁的，如果对象已经被销毁，wp的promote()方法将返回空指针，这样就能避免出现地址访问错的情况。
>
> 如果要使用智能指针来引用这个类的对象，那么这个类需满足下列两个前提条件：
>
> - 这个类是基类RefBase的子类或间接子类；
> - 这个类必须定义虚构造函数，即它的构造函数需要这样定义：virtual ~MyClass();
>
> 普通指针是这样定义　MyClass* p_obj; 智能指针是这样定义：sp<MyClass> p_obj; 　　
>
> 弱指针定义：wp<MyClass> wp_obj = new MyClass(); 　升级为强指针：p_obj = wp_obj.promote(); 
>
> [参考链接](https://blog.csdn.net/dongdong230/article/details/16894807)



> C++ namespace 关键字解析
>
> 命名空间(namespace )：实际上就是一个由程序设计者命名的内存区域，程序设计者可以根据需要指定一些有名字的空间域，把一些全局实体分别放在各个命名空间中，从而与其他全局实体分隔开来。使用命名空间解决名字冲突。
> 如： namespace ns1 //指定命名中间nsl
>
> namespace 是CPP的关键字，用于声明代码块所属的命名空间，AOSP底层的本地代码把声明了命令空间“android”，把所有的“android”代码都看成一个工程了，这样做的好处是把自己的代码与第三方开源的代码区分开来，同时避免符号重命名的问题。



> C++ virtual 关键字解析
>
> virtual 在英文中表示“虚”、“虚拟”的含义。c++中的关键字“virtual”主要用在两个方面：虚函数与虚基类。下面将分别从这两个方面对virtual 进行介绍。
>
> **1.虚函数**
>
> 在基类中将被重写的成员函数设置为虚函数，其含义是：当通过基类的指针或者引用调用该成员函数时，将根据指针指向的对象类型确定调用的函数，而非指针的类型。例如 两个父类类型的指针一个指向的父类实例一个指向子类实例，当调用重载方法时，指向子类的指针会执行子类的方法，就是实现了 Java 中原生的多态特性。
>
> **2.虚基类**
>
> 在c++中，派生类可以继承多个基类。问题在于：如果这多个基类又是继承自同一个基类时，那么派生类是不是需要多次继承这“同一个基类”中的内容？虚基类可以解决这个问题。简而言之，虚基类可以使得从多个类（它们继承自一个类）中派生出的对象只继承一个对象。
>
> 当该类从不同的途径继承了两个或者更多的同名函数时，如果没有对类名限定为virtual，将导致二义性。当然，如果使用了虚基类，则不一定会导致二义性。编译器将选择继承路径上“最短”的父类成员函数加以调用。该规则与成员函数的访问控制权限并不矛盾。也就是说，不能因为具有更高调用优先级的成员函数的访问控制权限是"private"，而转而去调用public型的较低优先级的同名成员函数。
>
> C++ 纯虚函数
>
> 纯虚函数是一种特殊的虚函数，它的一般格式如下：`virtual <类型><函数名>(<参数表>)=0;`
>
> 在许多情况下，在基类中不能对虚函数给出有意义的实现，而把它声明为纯虚函数，它的实现留给该基类的派生类去做。这就是纯虚函数的作用。纯虚函数可以让类先具有一个操作名称，而没有操作内容，让派生类在继承时再去具体地给出定义。凡是含有纯虚函数的类叫做抽象类。这种类不能声明对象，只是作为基类为派生类服务。除非在派生类中完全实现基类中所有的的纯虚函数，否则，派生类也变成了抽象类，不能实例化对象。



Handler 机制 Native层实现的一些文章介绍

[Android消息机制2-Handler(Native层)](http://gityuan.com/2015/12/27/handler-message-native/)

[源码解读epoll内核机制](http://gityuan.com/2019/01/06/linux-epoll/)

[MessageQueue 实现详解（下）- C++ 世界对 Message 的支持](https://jekton.github.io/2018/04/27/handler-uncover-the-messagequeue-part2/)

[Android深入分析NativeMessageQueue和Looper.cpp(Native层消息机制)](https://blog.csdn.net/szqsdq/article/details/78866655)

[select/poll/epoll对比分析](select/poll/epoll对比分析)

[腾讯技术 | 十个问题理解 Linux epoll 工作原理](https://zhuanlan.zhihu.com/p/378892166)

[Linux IO模式及 select、poll、epoll详解](https://segmentfault.com/a/1190000003063859)



### 1.7 android_os_MessageQueue.cpp

> 对 Native 层的 MessageQueue 目前只能通过 C++ 源码了解
>
> [android_os_MessageQueue.cpp](https://android.googlesource.com/platform/frameworks/base/+/master/core/jni/android_os_MessageQueue.cpp)

**android_os_MessageQueue.h ** 头文件中 引入了 Looper.h 头文件用于定义 Looper 类型，定义了继承了 Android 智能类型基类的 MessageQueue ：  `class MessageQueue : public virtual RefBase`   , MessageQueue 类中声明了强指针类型 mLooper 变量： `protected:  sp<Looper> mLooper;` 除了MessageQueue 类还有个函数声明 android_os_MessageQueue_getMessageQueue, 这个方法在在 **[android_view_InputEventReceiver.cpp](https://android.googlesource.com/platform/frameworks/base/+/master/core/jni/android_view_InputEventReceiver.cpp)**  这个文件中有调用，在Handler 这边没找到用它的地方，目的是获取 Java 层的 MessageQueue 实例

`extern sp<MessageQueue> android_os_MessageQueue_getMessageQueue(JNIEnv* env, jobject messageQueueObj);`

**android_os_MessageQueue.cpp** 源文件中引入了 Looper.h 和 android_os_MessageQueue.h 头文件以及其它需要的头文件

MessageQueue 的实现类是：class NativeMessageQueue : public MessageQueue, public LooperCallback ，LooperCallback 是Looper.h 中定义的纯虚函数类似 Java 中的抽象类用来被继承实现多态的。

NativeMessageQueue 实例化时回初始化 Native 层的 Looper 对象，在处理消息时都是通过 Looper 来完成的，例如：pollOnce、wake

setFileDescriptorEvents 等，handleEvent 方法是 LooperCallback 的回调方法主要用来收集looperEvents 然后调用 Java 层 MessageQueue 的  dispatchEvents 用来获取文件描述符记录和任何可能改变的状态 最终将事件状态同步给 Native 层。



###  1.8 Looper.cpp

> 对 Native 层的 Looper 对应于的 NDK Docs 和 C++ 源码
>
> [Android Developers NDK Docs | Looper.cpp](https://developer.android.com/ndk/reference/group/looper)
>
> [Looper.cpp](https://android.googlesource.com/platform/frameworks/native/+/jb-dev/libs/utils/Looper.cpp)

**Looper.h** 头文件 引入了 threads.h、android/looper.h、sys/epoll.h 等头文件。

`struct ALooper {};` ：结构体是NDK Looper 的具体类型 即 Looper 继承自 ALooper 

`struct Message {};` ：Message 结构体封装了发送到 Looper 的消息体

`class LooperCallback : public virtual RefBase {}` 接口的实现类是 SimpleLooperCallback

`class MessageHandler : public virtual RefBase {};`：MessageHandler 接口 实现类是 WeakMessageHandler 它只是负责处理 `Message`，而不再负责把 Message 插入队列。Message 的入队由 Looper 直接处理

`class Looper : public ALooper, public RefBase {}` ：支持监视文件描述符事件的轮询循环，可选使用回调。该实现在内部使用 epoll()。主要声明的处理方法有：pollOnce、wake、addFd、sendMessage、prepare、setForThread、getForThread等

**Looper.cpp** 源文件 

WeakMessageHandler 的实现、SimpleLooperCallback 的实现、Looper类的实现

Native 层的 Looper 本质上是对 epoll 的封装，与 Java 层的 Looper 没有任何关系

Native 层的 Looper 同样是和线程绑定在一起的

Native层的 Looper 在创建的时候会调用epoll_create()函数来创建epoll，Java层在调用 poll_once 的时候 Native 层会调用 epoll_wait 等待消息的触发

Looper 也有 `sendMessage, sendMessageDelayed` 成员函数，但最终都是由 `sendMessageAtTime` 处理的。

和 Java 层对 Message 的处理类似，C++ 中也是按 `Message` 的触发时间排序。不同的是，C++ 世界的 Message 还被封装到了一个 `MessageEnvelope` 对象里。

Java 层执行 `Looper.quit()` 后，最终会执行 `Message.dispose()` 释放一下 native 层的资源：`nativeMessageQueue->decStrong(env)` 后，`nativeMessageQueue` 的强引用计数降为 0，对象被销毁。



## 2. Handler 消息机制相关类关键点详解

### 2.1 Handler 关键点详解

Handler 类是整个消息机制的出入口类，基于迪米特法则：“一个类的成员变量，入参或出参所涉及到的对象都是该类的朋友类，在方法体里面的类则不算其朋友类。因此我们需要尽可能保持不要与其他非朋友类对话” Handler 类中屏蔽外部了对 MessageQueue 、Looper 这两个角色的直接访问。我们只需要关心输入什么，输出什么就行。

handler 七个 post 系方法和八个 send 系方法是对外公开的消息发送方法，每一个方法都在 Android sdk 源码中调用过，这也是为什么说 Handler 是 Android Frameworks 中重要成员之一的所在。

```java
public class Handler {
  @UnsupportedAppUsage
  final Looper mLooper; //消息循环器
  final MessageQueue mQueue; //消息队列
  @UnsupportedAppUsage
  final Callback mCallback; //消息回到
  final boolean mAsynchronous; //异步控制变量
  //构造方法
  public Handler(@Nullable Callback callback, boolean async) {
    mLooper = Looper.myLooper();
    mQueue = mLooper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
  }
  
  //消息入口
  private boolean enqueueMessage(@NonNull MessageQueue queue, @NonNull Message msg, long uptimeMillis) {
    msg.target = this; //当前 Handler 的句柄传递到 Message
    msg.workSourceUid = ThreadLocalWorkSource.getUid(); // 返回当前在此线程上执行的代码的 UID
    if (mAsynchronous) {
        msg.setAsynchronous(true); //异步消息
    }
    return queue.enqueueMessage(msg, uptimeMillis); // 消息入队，详见 MessageQueue
  }
  
  //消息出口
  public void dispatchMessage(@NonNull Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
	}
}
```

#### 构造器系列方法

```kotlin
@RequiresApi(Build.VERSION_CODES.P)
fun createHandler(type: Int): Handler {
    return when (type) {
        0x01 -> Handler()//Deprecated from API level 30
        0x02 -> Handler(CallBack())//Deprecated from API level 30
        0x03 -> Handler(Looper.myLooper())
        0x04 -> Handler(Looper.myLooper(), CallBack())
        0x05 -> Handler(false)//UnsupportedAppUsage
        0x06 -> Handler(CallBack(), false) //todo 无法调用，会崩溃
        0x07 -> Handler(Looper.myLooper(), CallBack(), false)//UnsupportedAppUsage
        0x08 -> Handler.createAsync(Looper.myLooper())//Call requires API level 28
        0x09 -> HandlerCompat.createAsync(Looper.myLooper())
        0x0a -> Handler.createAsync(Looper.myLooper(), CallBack())//Call requires API level 28
        0x0b -> HandlerCompat.createAsync(Looper.myLooper(), CallBack())
        0x0c -> Handler.getMain() //懒汉式单例
        0x0d -> Handler.mainIfNull(Handler(Looper.myLooper())) //todo 无法调用，会崩溃
        else -> Handler.getMain()
    }
}
```

- 如果没有显式的为 Handler 指定 Looper ，Handler 在构造时会通过 mLooper = Looper.myLooper() 方式与当前线程的 Looper 相关联。
- 如果此线程没有创建对应的 Looper，Handler在构造时会引发异常：RuntimeException: Can't create handler inside thread Thread[Thread-3,5,main] that has not called Looper.prepare()

- 01和02 创建方式 弃用原因：
  - 在 Handler 构建过程中如果没有显示的指定一个 Looper 在一些情况下可能会导致 bug，例如以下情况：
  - 如果此线程没有创建对应的 Looper，Handler在构造时会导致 RuntimeException
  - 多线程情况下并发时创建 Handler 时可能出现不能确定是哪个线程中创建的，mLooper = Looper.myLooper() 这行代码不是线程安全的
- 替代隐式设置Looper 的替代方案(以创建一个主线程 Handler  为例)：
  - ContextCompat.getMainExecutor(context) //Use an Executor
  - view.getHandler() //使用系统中创建好的 Handler  
  -  Handler.getMain() / Handler(Looper.getMainLooper()) //显式指定 Looper。
- CallBack是一个回调接口，handleMessage 方法的回调对象，只有 send 系方式才会执行到这个回调，它的返回值也会影响 Handler 的 handleMessage 成员方法是否执行。
-  默认情况下，Handler 处理的 Message 都是是同步的，使用05、06、07、08、09、0a的构造函数可以创建一个能够处理异步 Message 的Handler。异步消息表示不需要像同步消息那样对中断或事件进行全局排序。 异步消息不受MessageQueue.enqueueSyncBarrier(long) 方法插入的的同步障碍的影响。
- HandlerCompat 类中的创建方式做了版本兼容
- UnsupportedAppUsage：此注解表示可以访问该成员，但是 Android 不鼓励且不支持此访问。此注解的字段和方法可能会受到限制、更改或在未来的 Android 版本中删除。

#### post 系列方法

```kotlin
@RequiresApi(Build.VERSION_CODES.P)
fun postMessage(type: Int): Boolean {
    val handler = Handler(Looper.myLooper())
    return when (type) {
        0x01 -> handler.post {}
        0x02 -> handler.postDelayed({}, 1024)
        0x03 -> handler.postDelayed({}, 0, 1024)
        0x04 -> handler.postDelayed({}, Any(), 1024)
        0x05 -> HandlerCompat.postDelayed(handler, {}, Any(), 1024)
        0x06 -> handler.postAtTime({}, 1024)
        0x07 -> handler.postAtTime({}, Any(), 1024)
        0x08 -> handler.postAtFrontOfQueue {}
        0x09 -> handler.postDelayed(200L) {}.equals("")
        0x0a -> handler.postAtTime(200L) {}.equals("")
        else -> handler.post {}
    }
}
//发送方法最终都会执行到这个私有方法
private boolean enqueueMessage(MessageQueue queue, @NonNull Message msg, long uptimeMillis)
```

- post 系列的发送方法时将 Runnable 包装成 Message 并添加到 handler-mLooper-mQueue 中。 runnable 将在会在 mLooper 对应的线程上执行。
- 如果 Runnable 成功放入 mQueue，则返回 true。 失败时返回 false，通常是因为处理 mQueue 的 mLooper 正在退出。结果为 true 并不意味着 Runnable 将被处理，有一种情况是如果 mLooper 在延迟时间之前退出了，则消息将被丢弃。
- 在 delayMillis 毫秒后运行，延迟的基准时间点是 SystemClock.uptimeMillis。 在深度睡眠中(不知道谁睡眠，应该是关机)花费的时间会增加执行的额外延迟。
- messageToken 是一个对象，将 Runnable 包装成 Message 时将一个对象设置给了 Message 的 obj。这个对象可以用来在队列中找到这条消息，从而可以通过这个令牌调用 handler.removeCallbacksAndMessages(messageToken) 取消 Runnable 的执行。
- SystemClock.uptimeMillis(): 自系统启动开始从0开始的到调用该方法时相差的毫秒数，不计算深度睡眠所花费的时间。
- System.currentTimeMillis(): 代表的是从 1970-01-01 00:00:00 到当前时间的毫秒数，这个值是一个强关联系统时间的值，我们可以通过修改系统时间达到修改该值的目的，所以该值是不可靠的值
- uptimeMillis 是指定的特定时间点运行；delayMillis 是多少毫秒后运行
- postAtFrontOfQueue 将 Runnable 包装成 Message 并排在 mQueue 的前面，当 mLooper 下一次迭代时进行处理
- HandlerCompat 类中的 postDelayed 做了版本兼容
- 09、0a 是 Handler.kt 类中为Handler 实现的两个扩展方法，目的是为了调整参数顺序可以使用 Lambda 表达式

#### send 系列方法

```kotlin
fun sendMessage(type: Int): Boolean {
    val handler = Handler(Looper.myLooper())
    return when (type) {
        0x01 -> handler.sendEmptyMessage(1)
        0x02 -> handler.sendEmptyMessageDelayed(2, 1024)
        0x03 -> handler.sendEmptyMessageAtTime(3, 1024)
        0x04 -> handler.sendMessage(Message.obtain())
        0x05 -> handler.sendMessageDelayed(Message.obtain(), 1024)
        0x06 -> handler.sendMessageAtTime(Message.obtain(), 1024)
        0x07 -> handler.sendMessageAtFrontOfQueue(Message.obtain())
        0x08 -> handler.executeOrSendMessage(Message.obtain())
        else -> handler.sendEmptyMessage(1)
    }
}
//发送方法最终都会执行到这个私有方法
private boolean enqueueMessage(MessageQueue queue, @NonNull Message msg, long uptimeMillis)
```

- 发送一个 Message 到 mQueue 中。handleMessage 方法将在会在 mLooper 对应的线程上执行
- executeOrSendMessage：构造 handler 时获取 mLooper 的与当前线程中缓存的 Looper 相比如果同一个线程 直接执行 handleMessage 否则调用 sendMessage 

#### dispatchMessage

```java

public void dispatchMessage(@NonNull Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
}
//从 Message 中取出 Runnable 对象调用它的 run 方法
private static void handleCallback(Message message) {
    message.callback.run();
}

/**
 * 可以在实例化 Handler 时使用这个回调接口，以避免必须实现一个 Handler 子类。
 */
public interface Callback {
    /**
     * 返回 true 表示不需要进一步处理，也就是不会执行 handleMessage 成员方法
     */
    boolean handleMessage(@NonNull Message msg);
}
/**
 * 子类必须实现它才能接收消息。
 */
public void handleMessage(@NonNull Message msg) {
}
```

- post runnable 与 send message 两种发送方式的消息分开处理，post 方式执行Runnable 的 run 方法，send 方式执行 handleMessage 方法，handleMessage区分是 Callback 的还是 Handler 的。

#### remove 系列方法

```kotlin
fun removeMessage(type: Int): Unit {
    val handler = Handler(Looper.myLooper())
    handler.sendEmptyMessage(5)
    val runnable = {}
    val token = Any()
    HandlerCompat.postDelayed(handler, runnable, token, 1024)
    when (type) {
        0x01 -> handler.removeMessages(5)
        0x02 -> handler.removeMessages(5, token)
        0x03 -> handler.removeEqualMessages(5, token)
        0x04 -> handler.removeCallbacksAndMessages(token)
        0x05 -> handler.removeCallbacksAndEqualMessages(token)
        0x06 -> handler.removeCallbacks(runnable, token)
        0x08 -> handler.removeCallbacks(runnable)
        else -> handler.removeCallbacks(runnable)
    }
}
```

- remove 系列的方法都是包装的 mQueue 的方法，具体实现在 MessageQueue 小节介绍

### 2.2 Message 关键点详解

```java
public final class Message implements Parcelable {
  public int what; 
  public int arg1;
  public int arg2;
  public Object obj;
  public Messenger replyTo;
  int flags //消息的标志
  static final int FLAG_ASYNCHRONOUS = 1 << 1; //异步消息标志
  static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE; //在 copyFrom 方法中清除的标志
  public long when;
  Bundle data;
  Handler target;
  Runnable callback;
  Message next;
  public static final Object sPoolSync = new Object();
  private static Message sPool;
  private static int sPoolSize = 0;
  private static final int MAX_POOL_SIZE = 50;
  private static boolean gCheckRecycle = true;

}
```

#### int what

Message 的唯一识别标识， 每个 Handler 都有自己的消息代码命名空间(例如两个H发送彼此的 what 对方并不会收到消息，这是通过 target 实现的隔离)，因此无需担心 Handler 发生冲突。

#### int arg1，arg1

如果发送的消息只需要携带几个整数值，arg1 和 arg2 就足够了，它们是使用 setData(Bundle data) 的简化替代方案。

#### Object obj

如果发送的消息需要携带任意对象。 例如跨进程发送消息时。

#### long when

此消息的执行时间。 基准时间点是 SystemClock.uptimeMillis 。

#### Bundle data

如果发送的消息只需要携带多种类型的数据，可以包装成一个 Bundle。

#### Handler target

发送和处理该消息的 Handler 。

#### Runnable callback

post runnable 时将 runnable 通过这个字段包装在 Message 里面

#### Message next

该条消息节点的下一条消息的引用，MessageQueue 通过链表结构来管理整个队列

#### 消息池 static Message sPool

消息池相关的字段

```java
public static final Object sPoolSync = new Object(); //类锁，用于确保消息池在操作 next、sPool、sPoolSize 时的安全问题
private static Message sPool; //消息池链表结构的头结点
private static int sPoolSize = 0; //当前消息池大小
Message next; //消息池链表结构的下一个节点引用
private static final int MAX_POOL_SIZE = 50; 消息池存储的Message对象最大数量
```

- 消息池是通过链表结构来实现，sPool 作为消息池的头结点。sPool是一个静态变量，它引用的对象的生命周期与类的生命周期一样长，这样处于单链表中的每一个Message对象均不会被gc回收，所以它们可作为内存缓存使用。

- 消息池（单链表）最大可以缓存50个Message对象，也就是说单链表的最大长度为50个元素，当缓存的 Message 对象总数已经为50个时，不能再被缓存的 Message 对象会被 gc 回收掉。
- 保留在消息池（单链表）中的Message对象，都被标记成了FLAG_IN_USE，表示Message对象是可用状态。
- 消息池中缓存 Message 对象、获取 Message 对象，均是在头结点处完成的，传说中的头插法，即在头部插入元素、也在头部取出元素，这是单链表最高效的插入与删除了，时间复杂度O（1）
- 回收对象时，将 Message 对象的实例变量均赋值为零值（初始值）

#### recycle/recycleUnchecked

享元模式：消息池，Activity 栈管理，Re

recycle：将 Message 实例重置并放入到全局消息池。调用此函数后，您不得触摸消息，因为它已被有效释放。 回收当前排队或正在传递给处理程序的消息是错误的

recycleUnchecked：回收可能正在使用的消息。 该方法非 public 是由 MessageQueue 和 Looper 在内部使用。

回收复用流程

- 将Message对象持有的属性全部赋值为零值，这样当前的 Message 对象就和通过 new 创建一个新的Message对象的状态是一样的。
- 将已经初始化为零值状态的当前 Message 对象添加到一个消息池中作为头结点缓存起来：判断当前已经缓存的 Message 对象数量是否小于最大的缓存对象总数 50，小于则获得指向第一个 Message 对象的 sPool 变量，sPool 变量是单链表的头结点引用，它总是指向单链表中的第一个Message对象，然后 sPool 先将自己目前持有的头结点 Message 对象赋值给当前Message对象的 next 变量防止后续节点断开，接着 sPool 指向当前 Message 对象，最后再将消息池的总数+1，就完成了当前Message对象的缓存工作。

#### obtain

用于获取一个 Message 对象的方法，它是从 Message 类持有的消息池 sPool 中取出一个 Message 对象(从链条的头部取出)供我们使用的，这是 google 推荐的创建一条消息的方法。复用对象可以节约内存空间，也可以节约创建对象的性能开销，还可以减少GC的工作。

```java
public static Message obtain() {
    synchronized (sPoolSync) {
        if (sPool != null) {
            Message m = sPool;
            sPool = m.next;
            m.next = null;
            m.flags = 0; // clear in-use flag
            sPoolSize--;
            return m;
        }
    }
    return new Message();
}
```

### 2.3 MessageQueue 关键点详解

![image-20211007223538867](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20211007223538867.png)

优先队列(priority queue)

普通的队列是一种先进先出的数据结构，元素在队列尾追加，而从队列头删除。在优先队列中，元素被赋予优先级。当访问元素时，具有最高优先级的元素最先删除。优先队列具有最高级先出 （first in, largest out）的行为特征。通常采用堆数据结构来实现。

MessageQueue 中的 mMessages 是链表结构的头节点，也就是消息队列的物理结构是链表结构，逻辑结构有人说是优先级队列。

when 确实可以作为优先级，同步消息都可以按照 when 字段 插入和去除，但是Handler 消息机制中还有同步屏障机制和空闲消息机制，不同类型的元素所依赖的优先级是不一样的，所以，MessageQueue 的逻辑结构应该是一种自定义的队列或者是 priority queue plus

```java
public final class MessageQueue {
  @UnsupportedAppUsage
  private final boolean mQuitAllowed; // true :可以退出消息队列,主线程不允许退出
  @UnsupportedAppUsage
  private long mPtr; //指针（pointer）实际上是 Native 层消息队列实例的对象指针即：NativeMessageQueue* ，类对象的指针可以调用类函数
  @UnsupportedAppUsage
  Message mMessages; //消息队列是通过链表结构实现，mMessages为链表头节点
  private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<IdleHandler>(); //空闲消息列表
  private SparseArray<FileDescriptorRecord> mFileDescriptorRecords; //文件描述符记录列表
  private IdleHandler[] mPendingIdleHandlers; //空闲消息数组
  private boolean mQuitting; //标记是否退出了消息的循环，退出后不再处理剩下的消息
  private boolean mBlocked; //指示 next() 是否在 pollOnce() 中以非零超时阻塞等待。
  @UnsupportedAppUsage
  private int mNextBarrierToken; //下一个同步障碍识别号。插入同步屏障时会将该值自增并存储在消息的 arg1 字段携带作为识别该屏障。
  //涉及的 Native 方法
  private native static long nativeInit(); //初始化 Native 层的 NativeMessageQueue 、Looper、epoll等
  private native static void nativeDestroy(long ptr); //销毁 NativeMessageQueue
  private native void nativePollOnce(long ptr, int timeoutMillis); //把 timeoutMillis 参数传到 epoll_wait() 系统调用中，epoll_wait() 获取可用事件，获取不到就阻塞当前线程，否则遍历可用事件数组,如果遍历到的事件的文件描述符是唤醒事件文件描述符 mWakeEventFd ，则调用 awoken()方法重置唤醒事件文件描述符
  private native static void nativeWake(long ptr); //唤醒消息轮询线程
  private native static boolean nativeIsPolling(long ptr); //标记 Native 层的 Looper 是否是空闲状态
  private native static void nativeSetFileDescriptorEvents(long ptr, int fd, int events); // fd 事件回调
  //构造器
   MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
   }
 
}
```



#### 存：enqueueMessage

`消息发送过程`一般是从 Handler 的 `sendMessage()` 方法开始的，当我们调用 Handler 的 sendMessage() 或 sendEmptyMessage() 等方法时，Handler 会调用 MessageQueue 的 `enqueueMessage()` 方法把消息加入到消息队列中。消息 `Message` 并不是真正的队列结构，而是`链表结构`。MessageQueue 的enqueueMessage() 方法首先会判断消息的延时时间是否晚于当前链表中最后一个结点的发送时间，是的话则把该消息作为链表的最后一个结点。然后 enqueueMessage() 方法会判断是否需要唤醒消息轮询线程，是的话则通过 `nativeWake()`  JNI 方法调用 NativeMessageQueue 的 wake() 方法。NativeMessageQueue 的 wake() 方法又会调用 Native 层 Looper 的 `wake()` 方法，在 Native 层 Looper 的 wake() 方法中，会通过 `write()` 系统调用写入一个 `W` 字符到`唤醒事件文件描述符`中，这时监听这个唤醒事件文件描述符的消息轮询线程就会`被唤醒`。

```java
boolean enqueueMessage(Message msg, long when) {
    synchronized (this) {
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }
        if (mQuitting) {
            msg.recycle();
            return false;
        }
        msg.markInUse(); //flags |= FLAG_IN_USE;
        msg.when = when; //新消息 时间
        Message p = mMessages; //当前消息队列头节点
        boolean needWake; //是否需要唤醒线程
      	// p == null 还没有消息， when == 0 没有设置延迟，when < p.when 延迟时间还小于当前消息
        if (p == null || when == 0 || when < p.when) { //
            // 将新消息拼接到头节点，如果阻塞则唤醒事件队列。
            msg.next = p;
            mMessages = msg;
            needWake = mBlocked; // 啥消息没 mBlocked 为true
        } else {
          	// 延迟消息了
            // 插入队列中间。通常我们不必唤醒事件队列，除非队列的头部有一个同步屏障barrier，并且消息是队列中最早的异步消息。
            //p.target == null 表示同步屏障消息，isAsynchronous 新消息为异步消息
          	needWake = mBlocked && p.target == null && msg.isAsynchronous();
            Message prev; //
          	// 这个分支执行 延迟消息
            for (;;) {
                prev = p; //缓存头节点
                p = p.next; // 取下一个
                if (p == null || when < p.when) { //找到了延迟点
                    break;
                }
                if (needWake && p.isAsynchronous()) { //异步延迟消息不着急唤醒线程
                    needWake = false;
                }
            }
           //找到了延迟时间点就在这两个节点之间插入新节点
            msg.next = p; // invariant: p == prev.next
            prev.next = msg;
        }

        // 如果需要唤醒就通过 Native 唤醒
        if (needWake) {
            nativeWake(mPtr); // 唤醒线程
        }
    }
    return true;
}
```



#### 醒：nativeWake

![image-20211007230522519](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20211007230522519.png)

write() 方法会向`唤醒事件文件描述符`写入一个 `W` 字符，这个操作`唤醒`被阻塞的消息循环线程 。

```c++
static void android_os_MessageQueue_nativeWake(JNIEnv* env, jclass clazz, jlong ptr) {
    NativeMessageQueue* nativeMessageQueue = reinterpret_cast<NativeMessageQueue*>(ptr);
    nativeMessageQueue->wake();
}

void NativeMessageQueue::wake() {
    mLooper->wake();
}

void Looper::wake() {
#if DEBUG_POLL_AND_WAKE
    ALOGD("%p ~ wake", this);
#endif
    uint64_t inc = 1;
  	//write 唤醒时间
    ssize_t nWrite = TEMP_FAILURE_RETRY(write(mWakeEventFd.get(), &inc, sizeof(uint64_t)));
}
```

#### 取：next

```java
public static void loop() {
  final Looper me = myLooper();
  final MessageQueue queue = me.mQueue;
  for (;;) {
    //Looper 中 loop 方法会有个死循环不断执行 queue.next()
      Message msg = queue.next(); // might block
  }
}

Message next() {
    // 如果消息循环已经退出并被回收，再次获取消息直接返回 null
    final long ptr = mPtr;
    if (ptr == 0) {
        return null;
    }
    int pendingIdleHandlerCount = -1; //仅在第一次迭代期间是 -1 表示待处理的 IdleHandler 
    int nextPollTimeoutMillis = 0; //下一个轮询超时 Millis
    for (;;) {
      //将当前线程中挂起的所有 Binder 命令刷新到内核驱动程序。 在执行可能会阻塞很长时间的操作之前调用这会很有用，以确保任何挂起的对象引用都已被释放，以防止进程持有对象的时间超过它所需的时间
        if (nextPollTimeoutMillis != 0) {
            Binder.flushPendingCommands();
        }
      
				// 阻塞到下一次轮询时间，
      	// 消息队列为空时也没有空闲消息：nextPollTimeoutMillis = -1
      	// 处理完空闲消息后：nextPollTimeoutMillis = 0
      	// 当前消息为延迟消息且还没到时间：nextPollTimeoutMillis= Math.min(msg.when - now, Integer.MAX_VALUE)
      	// 若是nextPollTimeoutMillis 为-1，此时消息队列处于等待状态,
        // 若是nextPollTimeoutMillis 为 0，立即执行
        // 若是nextPollTimeoutMillis 为 (when - now) 延迟结束才唤醒
        nativePollOnce(ptr, nextPollTimeoutMillis); //Native 层会调用 epoll_wait 等待消息的触发
      
        synchronized (this) {
            // 尝试检索下一条消息。找到就返回。
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages; //头节点
            if (msg != null && msg.target == null) { // 当前头节点是同步屏障消息
                // 被屏障挡住了。查找队列中的下一条异步消息。
                do {
                  	//不管有没有异步消息，先获取同步屏障消息的下一个，有可能是异步或同步
                    prevMsg = msg; 
                    msg = msg.next;
                } while (msg != null && !msg.isAsynchronous()); //如果是同步消息一直循环下去，直到找到一个异步消息为止
            }
          	
            if (msg != null) {
                if (now < msg.when) { //下一条消息的延迟时间还没到。设置超时以在准备好时唤醒。
                    nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE); //延迟间隔
                } else {
                 		//now >= msg.when 立即执行的消息
                    mBlocked = false; //取到消息，不能阻塞了
                    if (prevMsg != null) { //屏障节点
                        prevMsg.next = msg.next; //断开msg 节点
                    } else {
                        mMessages = msg.next; //断开 msg 节点
                    }
                    msg.next = null; //游离 msg 节点
                    if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                    msg.markInUse(); //待处理状态
                    return msg; //返回
                }
            } else {
                // 空的消息队列，等着吧
                nextPollTimeoutMillis = -1;
            }
          	//走到这里说明已经没有消息了或者只有一个平展消息了
          
            // 如果执行退出了 Looper，这里需要做一些释放操作，nativeDestroy 。
            if (mQuitting) {
                dispose(); //释放 Native 
                return null;
            }
            // 如果第一次空闲，则获取要运行的空闲程序的数量。空闲句柄仅在队列为空或队列中的第一条消息（可能是障碍）将在未来处理时运
            if (pendingIdleHandlerCount < 0 && (mMessages == null || now < mMessages.when)) {
                pendingIdleHandlerCount = mIdleHandlers.size(); // 
            }
            if (pendingIdleHandlerCount <= 0) {
                mBlocked = true; //取不到消息，阻塞了
                continue;
            }

            if (mPendingIdleHandlers == null) { //创建指定数量的 IdleHandler 数组，数组节省空间，不考虑扩容
                mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
            }
            mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers); // ArrayList 转 数组
        }

        // 运行空闲处理程序。我们只在第一次迭代中到达这个代码块。
        for (int i = 0; i < pendingIdleHandlerCount; i++) {
            final IdleHandler idler = mPendingIdleHandlers[i];
            mPendingIdleHandlers[i] = null; // 回收触发gc
            boolean keep = false; 
            try {
                keep = idler.queueIdle(); // 执行队列空闲任务, keep 是 true 不会从列表移除，下次还会执行
            } catch (Throwable t) {
                Log.wtf(TAG, "IdleHandler threw exception", t);
            }
            if (!keep) {
                synchronized (this) {
                    mIdleHandlers.remove(idler); // 根据 queueIdle 的返回值决定是否移除
                }
            }
        }
        pendingIdleHandlerCount = 0; //将空闲处理程序计数重置为 0，这样我们就不会再次运行它们。

        // 在调用空闲处理程序时，可能已经传递了一条新消息，因此无需等待,返回并再次查找待处理的消息。
        nextPollTimeoutMillis = 0;
    }
}
```

#### 等：nativePollOnce

```java
private native void nativePollOnce(long ptr, int timeoutMillis); /*non-static for callbacks*/
```

```c++
static void android_os_MessageQueue_nativePollOnce(JNIEnv* env, jobject obj,
        jlong ptr, jint timeoutMillis) {
    NativeMessageQueue* nativeMessageQueue = reinterpret_cast<NativeMessageQueue*>(ptr);
    nativeMessageQueue->pollOnce(env, obj, timeoutMillis);
}

void NativeMessageQueue::pollOnce(JNIEnv* env, jobject pollObj, int timeoutMillis) {
    mPollEnv = env;
    mPollObj = pollObj;
    mLooper->pollOnce(timeoutMillis);
    mPollObj = NULL;
    mPollEnv = NULL;
    if (mExceptionObj) {
        env->Throw(mExceptionObj);
        env->DeleteLocalRef(mExceptionObj);
        mExceptionObj = NULL;
    }
}

int Looper::pollOnce(int timeoutMillis, int* outFd, int* outEvents, void** outData) {
    int result = 0;
    for (;;) {
        while (mResponseIndex < mResponses.size()) {
            const Response& response = mResponses.itemAt(mResponseIndex++);
            int ident = response.request.ident;
            if (ident >= 0) {
                int fd = response.request.fd;
                int events = response.events;
                void* data = response.request.data;
#if DEBUG_POLL_AND_WAKE
                ALOGD("%p ~ pollOnce - returning signalled identifier %d: "
                        "fd=%d, events=0x%x, data=%p",
                        this, ident, fd, events, data);
#endif
                if (outFd != nullptr) *outFd = fd;
                if (outEvents != nullptr) *outEvents = events;
                if (outData != nullptr) *outData = data;
                return ident;
            }
        }
        if (result != 0) {
#if DEBUG_POLL_AND_WAKE
            ALOGD("%p ~ pollOnce - returning result %d", this, result);
#endif
            if (outFd != nullptr) *outFd = 0;
            if (outEvents != nullptr) *outEvents = 0;
            if (outData != nullptr) *outData = nullptr;
            return result;
        }
        result = pollInner(timeoutMillis);
    }
}

int Looper::pollInner(int timeoutMillis) {
#if DEBUG_POLL_AND_WAKE
    ALOGD("%p ~ pollOnce - waiting: timeoutMillis=%d", this, timeoutMillis);
#endif
    // Adjust the timeout based on when the next message is due.
    if (timeoutMillis != 0 && mNextMessageUptime != LLONG_MAX) {
        nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
        int messageTimeoutMillis = toMillisecondTimeoutDelay(now, mNextMessageUptime);
        if (messageTimeoutMillis >= 0
                && (timeoutMillis < 0 || messageTimeoutMillis < timeoutMillis)) {
            timeoutMillis = messageTimeoutMillis;
        }
#if DEBUG_POLL_AND_WAKE
        ALOGD("%p ~ pollOnce - next message in %" PRId64 "ns, adjusted timeout: timeoutMillis=%d",
                this, mNextMessageUptime - now, timeoutMillis);
#endif
    }
    // Poll.
    int result = POLL_WAKE;
    mResponses.clear();
    mResponseIndex = 0;
    // We are about to idle.
    mPolling = true;
    struct epoll_event eventItems[EPOLL_MAX_EVENTS];
    //todo epoll_wait
    int eventCount = epoll_wait(mEpollFd.get(), eventItems, EPOLL_MAX_EVENTS, timeoutMillis);
}
```

等待事件可用，以毫秒为单位可选超时。为发生事件的所有文件描述符调用回调，内部通过 epoll_wait 实现。

如果超时为零，则立即返回而不阻塞。

如果超时为负，则无限期等待直到事件出现。

如果在超时到期之前使用wake() 唤醒轮询并且没有调用回调并且没有其他文件描述符准备好，则返回POLL_WAKE。

如果调用了一个或多个回调，则返回 POLL_CALLBACK。

如果在给定的超时到期之前没有数据，则返回 POLL_TIMEOUT。

如果发生错误，则返回 POLL_ERROR。

如果它的文件描述符有数据并且没有回调函数（需要调用者处理它），则返回一个 >= 0 包含标识符的值。

在这个（并且只有这个）情况下，outFd、outEvents 和 outData 将包含与 fd 关联的轮询事件和数据，否则它们将被设置为 NULL。此方法在为所有已发出信号的文件描述符完成调用适当的回调之前不会返回。

**int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout);**

- 第1个参数 epfd是 epoll的描述符。

- 第2个参数 events则是分配好的 epoll_event结构体数组，epoll将会把发生的事件复制到 events数组中（events不可以是空指针，内核只负责把数据复制到这个 events数组中，不会去帮助我们在用户态中分配内存。内核这种做法效率很高）。

- 第3个参数 maxevents表示本次可以返回的最大事件数目，通常 maxevents参数与预分配的events数组的大小是相等的。

- **第4个参数 timeout表示在没有检测到事件发生时最多等待的时间（单位为毫秒），如果 timeout为0，则表示 epoll_wait在 rdllist链表中为空，立刻返回，不会等待，0 会立即返回，-1将不确定，也有说法说是永久阻塞，具体时间则延迟到点后返回**

#### 停：quit

```java
void quit(boolean safe) {
    if (!mQuitAllowed) {
        throw new IllegalStateException("Main thread not allowed to quit.");
    }
    synchronized (this) {
        if (mQuitting) { //已经执行了退出
            return;
        }
        mQuitting = true; //退户标记
        if (safe) {
            removeAllFutureMessagesLocked();
        } else {
            removeAllMessagesLocked();
        }
        // 移除所有消息了，唤醒线程执行收尾工作
        nativeWake(mPtr);
    }
}

private void removeAllFutureMessagesLocked() {
    final long now = SystemClock.uptimeMillis();
    Message p = mMessages;
    if (p != null) {
        if (p.when > now) {
            removeAllMessagesLocked(); //如果头节点是延迟消息直接遍历干掉以及后面的延迟消息
        } else {
          // 正在执行的消息，先执行完
            Message n;
            for (;;) {
                n = p.next;
                if (n == null) {//
                    return;
                }
                if (n.when > now) {
                    break;
                }
                p = n;
            }
            p.next = null;
            do {
                p = n;
                n = p.next;
                p.recycleUnchecked();
            } while (n != null);
        }
    }
}

private void removeAllMessagesLocked() {
    Message p = mMessages;
    while (p != null) {
        Message n = p.next;
        p.recycleUnchecked(); //回收每一条消息并添加到消息池中
        p = n;
    }
    mMessages = null; // gc root 置空，可以 GC 回收了
}
```

#### 删：remove

```java
void removeCallbacksAndEqualMessages(Handler h, Object object) {
    if (h == null) {
        return;
    }

    synchronized (this) {
        Message p = mMessages;

        // 删除头界定
        while (p != null && p.target == h
                && (object == null || object.equals(p.obj))) {
            Message n = p.next;
            mMessages = n;
            p.recycleUnchecked();
            p = n;
        }

        // 删除头节点之后的所有匹配的节点
        while (p != null) {
            Message n = p.next;
            if (n != null) {
                if (n.target == h && (object == null || object.equals(n.obj))) {
                    Message nn = n.next;
                    n.recycleUnchecked();
                    p.next = nn;
                    continue;
                }
            }
            p = n;
        }
    }
}
```

#### 同步屏障：postSyncBarrier

```java
ViewRootImpl#scheduleTraversals()
//插入同步屏障
mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
//发送异步消息
Message msg = mHandler.obtainMessage(MSG_DO_SCHEDULE_VSYNC);
msg.setAsynchronous(true);
mHandler.sendMessageAtFrontOfQueue(msg);
//发送异步消息
Message msg = mHandler.obtainMessage(MSG_DO_SCHEDULE_CALLBACK, action);
msg.arg1 = callbackType;
msg.setAsynchronous(true);
mHandler.sendMessageAtTime(msg, dueTime);
//移除同步屏障
mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
```

处理逻辑在 next 方法

#### 空闲消息：IdleHandler

```java
/**
 * 用于在线程阻塞等待时，添加更多消息的回调接口。
 */
public static interface IdleHandler {
    /**
     * 当消息队列用完消息并且现在将等待更多消息时调用。 返回 true 以保持空闲处理程序处于活动状态，返回 false 以将其删除。 
     *  如果队列中仍有待处理的消息，则可能会调用此方法，但它们都被安排在当前时间之后调度
     */
    boolean queueIdle();
}

//向这个消息队列添加一个新的MessageQueue.IdleHandler 。 
//这可以通过在调用时从IdleHandler.queueIdle()返回 false 来自动删除，或者使用removeIdleHandler显式删除它。
//从任何线程调用此方法都是安全的
public void addIdleHandler(@NonNull IdleHandler handler) {
    if (handler == null) {
        throw new NullPointerException("Can't add a null IdleHandler");
    }
    synchronized (this) {
        mIdleHandlers.add(handler);
    }
}
//从先前使用addIdleHandler添加的队列中删除MessageQueue.IdleHandler 。 如果给定的对象当前不在空闲列表中，则什么都不做。
//从任何线程调用此方法都是安全的
public void removeIdleHandler(@NonNull IdleHandler handler) {
    synchronized (this) {
        mIdleHandlers.remove(handler);
    }
}
```

IdleHandler 可以用来做一些在主线程空闲的时候才做的事情，通过 `Looper.myQueue().addIdleHandler()` 就能添加一个 IdleHandler 到 MessageQueue 中。

当 IdleHandler 的 queueIdle() 方法返回 false 时，那 MessageQueue 就会在执行完 queueIdle() 方法后把这个 IdleHandler 从数组中删除，下次不再执行，否则会多次执行。



### 2.4 Looper 关键点详解

```java
public final class Looper {
  static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
  private static Looper sMainLooper;  // guarded by Looper.class
  private static Observer sObserver;
  final MessageQueue mQueue;
  final Thread mThread;
  private boolean mInLoop;
  private static Observer sObserver;
  
  private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
  }
  
}
```

Looper API 实现说明：该类包含设置和管理基于 MessageQueue 的事件循环所需的代码。影响队列状态的 API 应该定义在 MessageQueue 或 Handler 而不是 Looper 本身。例如，空闲处理程序和同步屏障在队列上定义，而准备线程、循环和退出在循环器上定义。

#### 构建：prepare

```java
//创建 Looper 并缓存到 ThreadLocal 确保线程间单例
private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
//通过 ThreadLocal 获取 Looper
public static @Nullable Looper myLooper() {
    return sThreadLocal.get();
}
//创建主线吃 Looper ，
@Deprecated
public static void prepareMainLooper() {
    prepare(false);// 构建一个不允许退出的Looper
    synchronized (Looper.class) {
        if (sMainLooper != null) {
            throw new IllegalStateException("The main Looper has already been prepared.");
        }
        sMainLooper = myLooper(); //缓存到静态变量 sMainLooper
    }
}
```

#### 循环：loop

```java
public static void loop() {
    final Looper me = myLooper(); //通过 ThreadLocal 获取 Looper 保证线程唯一
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    if (me.mInLoop) { // 再次循环将使排队的消息在完成之前执行
        Slog.w(TAG, "Loop again would have the queued messages be executed"
                + " before this one completed.");
    }
    me.mInLoop = true; //标记循环中
    final MessageQueue queue = me.mQueue; //对应的消息队列
    // 确保该线程的身份是本地进程的身份，并跟踪该身份令牌实际是什么。
    Binder.clearCallingIdentity();
    final long ident = Binder.clearCallingIdentity();

    // Allow overriding a threshold with a system prop. e.g.
    // adb shell 'setprop log.looper.1000.main.slow 1 && stop && start'
    final int thresholdOverride =
            SystemProperties.getInt("log.looper."
                    + Process.myUid() + "."
                    + Thread.currentThread().getName()
                    + ".slow", 0);

    boolean slowDeliveryDetected = false;

    for (;;) {
        Message msg = queue.next(); // 这里可能会阻塞，例如：没有消息、延迟消息等
        if (msg == null) {
            // 没有消息表示消息队列正在退出。
            return;
        }

        // This must be in a local variable, in case a UI event sets the logger
        final Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " +
                    msg.callback + ": " + msg.what);
        }
        // Make sure the observer won't change while processing a transaction.
        final Observer observer = sObserver;

        final long traceTag = me.mTraceTag;
        long slowDispatchThresholdMs = me.mSlowDispatchThresholdMs;
        long slowDeliveryThresholdMs = me.mSlowDeliveryThresholdMs;
        if (thresholdOverride > 0) {
            slowDispatchThresholdMs = thresholdOverride;
            slowDeliveryThresholdMs = thresholdOverride;
        }
        final boolean logSlowDelivery = (slowDeliveryThresholdMs > 0) && (msg.when > 0);
        final boolean logSlowDispatch = (slowDispatchThresholdMs > 0);

        final boolean needStartTime = logSlowDelivery || logSlowDispatch;
        final boolean needEndTime = logSlowDispatch;

        if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
            Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
        }

        final long dispatchStart = needStartTime ? SystemClock.uptimeMillis() : 0;
        final long dispatchEnd;
        Object token = null;
        if (observer != null) {
            token = observer.messageDispatchStarting(); //观察分发开始事件
        }
      	//开始分发消息
        long origWorkSource = ThreadLocalWorkSource.setUid(msg.workSourceUid);
        try {
            msg.target.dispatchMessage(msg); //交给 Handler 处理
            if (observer != null) {
                observer.messageDispatched(token, msg); //观察分发完成事件
            }
            dispatchEnd = needEndTime ? SystemClock.uptimeMillis() : 0;
        } catch (Exception exception) {
            if (observer != null) {
                observer.dispatchingThrewException(token, msg, exception); //观察分发异常事件
            }
            throw exception;
        } finally {
            ThreadLocalWorkSource.restore(origWorkSource);
            if (traceTag != 0) {
                Trace.traceEnd(traceTag);
            }
        }
        if (logSlowDelivery) {
            if (slowDeliveryDetected) {
                if ((dispatchStart - msg.when) <= 10) {
                    Slog.w(TAG, "Drained");
                    slowDeliveryDetected = false;
                }
            } else {
                if (showSlowLog(slowDeliveryThresholdMs, msg.when, dispatchStart, "delivery",
                        msg)) {
                    // Once we write a slow delivery log, suppress until the queue drains.
                    slowDeliveryDetected = true;
                }
            }
        }
        if (logSlowDispatch) {
            showSlowLog(slowDispatchThresholdMs, dispatchStart, dispatchEnd, "dispatch", msg);
        }
        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }
        // Make sure that during the course of dispatching the
        // identity of the thread wasn't corrupted.
        final long newIdent = Binder.clearCallingIdentity();
        if (ident != newIdent) {
            Log.wtf(TAG, "Thread identity changed from 0x"
                    + Long.toHexString(ident) + " to 0x"
                    + Long.toHexString(newIdent) + " while dispatching to "
                    + msg.target.getClass().getName() + " "
                    + msg.callback + " what=" + msg.what);
        }
        msg.recycleUnchecked(); // 回收并归池
    }
}
```



### 2.5 ThreadLocal 关键点详解

ThreadLocal 适用于变量在线程间隔离而在方法或类间共享的场景

- 每个线程需要有自己单独的实例
- 实例需要在多个方法中共享，但不希望被多线程共享

三元素的关系

- Thread1----ThreadLocalMap<ThreadLocal , Value >

- hread2----ThreadLocalMap<ThreadLocal , Value >

- ThreadLocalMap其实是Thread线程的一个属性值，而ThreadLocal是维护ThreadLocalMap。这个属性指的一个工具类。*Thread线程可以拥有多个ThreadLocal维护的自己线程独享的共享变量*（这个共享变量只是针对自己线程里面共享）

ThreadLocal与Synchronized的区别

- ThreadLocal和Synchonized都用于解决多线程并发访问，但是ThreadLocal与synchronized有本质的区别：

- Synchronized用于线程间的数据共享，而ThreadLocal则用于线程间的数据隔离。

- Synchronized是利用锁的机制，使变量或代码块在某一时该只能被一个线程访问。而ThreadLocal为每一个线程都提供了变量的副本

，使得每个线程在某一时间访问到的并不是同一个对象，这样就隔离了多个线程对数据的数据共享。而Synchronized却正好相反，它用于在多个线程间通信时能够获得数据共享。

```java
public class ThreadLocal<T> {
  private final int threadLocalHashCode = nextHashCode();//这是一个自定义哈希代码（仅在 ThreadLocalMaps 中有用），它消除了在相同线程使用连续构造的 ThreadLocals 的常见情况下的冲突，同时在不太常见的情况下保持良好行为。
  private static AtomicInteger nextHashCode =new AtomicInteger(); //要给出的下一个哈希码。 原子更新。 从零开始
  private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
  }

}
```

#### 存：set

```java
public class Thread implements Runnable {
  ThreadLocal.ThreadLocalMap threadLocals = null; //与此线程有关的 ThreadLocal 值。该映射由 ThreadLocal 类维护。
}

```

```java
 public void set(T value) {
        //1、获取当前线程
        Thread t = Thread.currentThread(); //native 方法
        //2、获取线程中的属性 threadLocalMap ,如果threadLocalMap 不为空，
        //则直接更新要保存的变量值，否则创建threadLocalMap，并赋值
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            // 初始化thradLocalMap 并赋值
            createMap(t, value);
}

ThreadLocalMap getMap(Thread t) {
   return t.threadLocals;
}

void createMap(Thread t, T firstValue) {
    t.threadLocals = new ThreadLocalMap(this, firstValue); //初始化ThreadLocalMap并赋值在Thread 上
}
```

#### 取：get

```java
public T get() {
    //1、获取当前线程
    Thread t = Thread.currentThread();
    //2、获取当前线程的ThreadLocalMap
    ThreadLocalMap map = getMap(t);
    //3、如果map数据不为空，
    if (map != null) {
        //3.1、获取当前 ThreadLocal 对应的 threalLocalMap中 存储的节点
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value; //获取 value 
            return result;
        }
    }
    //如果是数据为null，则初始化，初始化的结果，TheralLocalMap中存放key值为threadLocal，值为null
    return setInitialValue();
}
```

####  删：remove

```java
public void remove() {
    ThreadLocalMap m = getMap(Thread.currentThread());
    if (m != null)
        m.remove(this);
}
```

remove方法，直接将 ThrealLocal 对应的值从当前T hread 中的 ThreadLocalMap 中删除。为什么要删除，这涉及到内存泄露的问题。实际上 ThreadLocalMap 中使用的 key 为 ThreadLocal 的弱引用，弱引用的特点是，如果这个对象只存在弱引用，那么在下一次垃圾回收的时候必然会被清理掉。

所以如果 ThreadLocal 没有被外部强引用的情况下，在垃圾回收的时候会被清理掉的，这样一来 ThreadLocalMap中使用这个 ThreadLocal 的 key 也会被清理掉。但是，value 是强引用，不会被清理，这样一来就会出现 key 为 null 的 value。

#### ThreadLocalMap

ThreadLocalMap 是一种定制的哈希映射，仅适用于维护线程本地变量。 该类是包私有的，以允许在类 Thread 中声明字段。 为了存储非常大和长期存在的对象，哈希表条目使用 WeakReferences 作为键。 但是，由于不使用引用队列，因此只有在哈希表耗尽空间时才能保证删除陈旧条目

和普通Hashmap类似存储在一个数组内，但与hashmap使用的拉链法解决散列冲突的是 ThreadLocalMap使用开放地址法

开放寻址发空间利用率低，在散列冲突时寻找下一个可存入的槽点，为了避免冲突负载因子会设置的相对较小

```java
static class ThreadLocalMap {
  //这个哈希映射中的条目扩展了 WeakReference，使用它的主要 ref 字段作为键（它总是一个 ThreadLocal 对象）。 请注意，空键（即 entry.get() == null）意味着不再引用该键，因此可以从表中删除该条目。 此类条目在以下代码中称为“陈旧条目”
  static class Entry extends WeakReference<ThreadLocal<?>> {
    /** The value associated with this ThreadLocal. */
    Object value;
    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
  }
  private static final int INITIAL_CAPACITY = 16; //初始容量 -- 必须是 2 的幂
  private Entry[] table; //槽位数组，根据需要调整大小。 table.length 必须始终是 2 的幂。
  private int size = 0;
  
}
```

##### 存：set

```java
private void set(ThreadLocal<?> key, Object value) {
    // 我们不像 get() 那样使用快速路径，因为使用 set() 创建新条目至少与替换现有条目一样常见，在这种情况下，快速路径通常会失败.
    Entry[] tab = table;
    int len = tab.length;
  //与运算  & (len-1) 这就是为什么 要求数组len 要求2的n次幂 因为len减一后最后一个bit是1 与运算计算出来的数值下标 能保证全覆盖 
  //否则数组有效位会减半
    int i = key.threadLocalHashCode & (len-1);
		//从下标位置开始向后循环搜索  不会死循环  有扩容因子 必定有空余槽点
    for (Entry e = tab[i];
         e != null;
         e = tab[i = nextIndex(i, len)]) {
        ThreadLocal<?> k = e.get();
      //是当前引用 返回
        if (k == key) {
            e.value = value;
            return;
        }
      //槽点被GC掉 重设状态
        if (k == null) {
            replaceStaleEntry(key, value, i);
            return;
        }
    }
  //槽点为空 设置value
    tab[i] = new Entry(key, value);
    int sz = ++size;
  //没有可清理的槽点 并且数量大于负载因子 rehash
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash();
}
```

##### 取：getEntry

```java
private Entry getEntry(ThreadLocal<?> key) {
    int i = key.threadLocalHashCode & (table.length - 1); //计算key
    Entry e = table[i]; //取槽位
    if (e != null && e.get() == key)
        return e;
    else
        return getEntryAfterMiss(key, i, e);
}
```

[参考链接](https://blog.csdn.net/u010445301/article/details/111322569)

## 2.6 Handler 消息机制运转流程

一张时序图理解基于主线程的消息机制运转流程（only java level）

![Handler原理时序图](/Users/xuejiewang/Desktop/Handler原理时序图.jpg)



## 3. Handler 消息机制相关问题汇总

- 背景相关
  - Handler被设计出来的原因？有什么用？
    - **延迟执行：**安排 Message 和 Runnable 在将来的某个时间点执行
    - **线程切换：**将其它线程上的执行的操作消息加入到当前线程上的 MessageQueue 中，当前线程接收并做相应处理
  - 延迟执行是如何实现的？
    - when 字段设置时间差，epoll 机制接收这个时间，在这个时间到来之前会休眠线程让出 cpu 的执行权
  - 线程切换是如何实现的
    - 内存共享+ThreadLocal ,Handler 可以在任何线程发，ThreadLocal 保证发的消息只能存到与H绑定的线程上
  - 子线程访问UI的崩溃原因和解决办法?
    - 崩溃发生在 ViewRootImpl 类的`checkThread`方法中：
    - 在构建 ViewRootImpl  类时会获取当前线程 mThread = Thread.currentThread();
    - 在视图刷新或者焦点改变时都会检查 mThread != Thread.currentThread() 如果不同就抛异常
  - UI为什么不设计成线程安全的
    - UI具有可变性，甚至是高频可变性，UI组件需要批量绘制来保证效率，如果多线程访问UI控件需要额外的同步处理，这必然会增加复杂性降低性能
    - Android设计出了 `单线程模型` 来处理UI操作，再搭配上Handler，是一个比较合适的解决方案。
    - 非UI线程一定不能更新UI吗
      - view.postInvalidate() 子线程间接更新UI 通过Handler 发送消息-处理消息-执行invalidate方法
      - SurfaceView 子线程绘制，GLSurfaceView，TextureView
- Handler 相关
  - Handler 隐式设置 Looper 的构造方法为什么不推荐使用了？
    - 在 Handler 构建过程中如果没有显示的指定一个 Looper 在一些情况下可能会导致 bug，例如以下情况：
    - 如果此线程没有创建对应的 Looper，Handler在构造时会导致 RuntimeException
    - 多线程情况下并发时创建 Handler 时可能出现不能确定是哪个线程中创建的，mLooper = Looper.myLooper() 这行代码不是线程安全的
  - Message 是怎么找到它所属的 Handler 然后进行分发的?
    - 通过 Message 的 target 字段
    - 发送时会设置当前 H ;分发时再取出 H
  - Handler 的 post 系列方法与 send 系列发送方法有什么区别？
    - 最终处理消息的出口有一些三中方式：
    - 1. msg.callback.run()
    - 2. Handler.Callback.handleMessage(msg)
    - 3. Handler.handleMessage(msg)
    - post 只能执行到1 send 可以是2或3，也可以2、3一起执行
  - Handler.Callback.handleMessage 和 Handler.handleMessage 有什么不一样
    - 这两个处理方法的区别在于`Handler.Callback.handleMessage`方法是否返回true：如果为`true`，则不再执行Handler.handleMessage；如果为`false`，则两个方法都要执行。
  - Handler、Looper、 MessageQueue、 Thread 是一一对应关系吗?
    - Looper、MessageQueue、线程是一一对应关系，而他们与Handler是可以一对多的
  - ActivityThread 中做了哪些关于Handler的工作? (为什么主线程不需要单独创建Looper)
    - 在main方法中，创建了主线程的`Looper`和`MessageQueue`，并且调用loop方法开启了主线程的消息循环。
    - 创建了一个Handler来进行四大组件的启动停止等事件处理
  - Handler 发送消息的 delay 可靠吗
    - 同步消息频发去重(remove), 互斥消息移除前置消息
    - 大于 Handler Looper 的周期时基本可靠
    - Looper 负载过高，任务越容易积压，进而导致卡顿
    - 不要要 Handler 的 delay 作为计时的依据
  - 实现一个简单的 Handler-Looper 框架
    - 利用现有数据结构 DelayQueue 
- MessageQueue 相关
  - 延迟消息是怎么实现的?
  - MessageQueue是干嘛呢?用的什么数据结构来存储数据?
  - MessageQueue的消息怎么被取出来的?
  - MessageQueue没有消息时候会怎样?阻塞之后怎么唤醒呢?说说pipe/epol机制?
  - 同步屏障和异步消息是怎么实现的?
  - 同步屏障和异步消息有具体的使用场景吗?
  - Message消息被分发之后会怎么处理?消息怎么复用的?
  - ldleHandler是啥?有什么使用场景?
    - `IdleHandler`就是当消息队列里面没有当前要处理的消息了，需要堵塞之前，可以做一些空闲任务的处理。
    - 当没有消息处理的时候，就会去处理这个`mIdleHandlers`集合里面的每个`IdleHandler`对象，并调用其`queueIdle`方法。最后根据`queueIdle`返回值判断是否用完删除当前的`IdleHandler`。
    - 常见的使用场景有：`启动优化`
- Looper 相关
  - Looper是干嘛呢?怎么获取当前线程的Looper?为什么不直接用Map存储线程和对象呢?
  - ThreadLocal运行机制?这种机制设计的好处?
  - 还有哪些地方运用到了ThreadLocal机制?
  - 可以多次创建Looper吗?
  - Looper中的quitAllowed字段是啥?有什么用?
  - Looper.loop方法是死循环，为什么不会卡死(ANR) ?
    - ANR产生的条件
      - Service Timeout：前台服务20s，后台服务200s
      - BroascastQueue Timeout：前台广播10s，后台广播60s
      - ContentProvider Timeout：10s
      - InputDispatching Timeout：5s
    - ANR与Looper的关系
      - Looper 是进程上的概念，ANR是进行某一环节后对占用主线程耗时的监控，ANR也是通过消息机制实现的
    - Looper 为什么不会导致CPU占用率高
      - IO多路复用
      - 没有消息的时候：e
- Native 层相关
  - epoll 机制
- 综合实践
  - HandlerThread是啥?有什么使用场景?
    - `HandlerThread`就是一个封装了Looper的Thread类，目的为了让我们在子线程里面更方便的使用 Handler。
    - 在调用 Thread 的 run 方法时会创建Looper 并  notifyAll(); 通知 getLooper 
    - `getLooper`方法获取Looper 时可能还没创建好，所以wait的意思就是等待Looper创建好，那边创建好之后再通知这边正确返回Looper。
  - IntentService是啥?有什么使用场景?
    - IntentService 继承自 Service 在 onCreate 时构建一个 HandlerThread 子线程
    - 内部类 ServiceHandler 继承自 Handler 也是在 onCreate 时构建 并绑定子线程的Looper
    - 在 IntentService  执行 start 方法时发送一条消息，在 ServiceHandler  中的 handleMessage 方法中执行具体的耗时任务
    - 耗时任务执行完就停止了服务
  - BlockCanary使用过吗?说说原理
    - Looper - Printer - mLogging，它在每个message处理的前后被调用，而如果主线程卡住了，不就是在dispatchMessage里卡住了吗？
    - 自定义 LooperPrinter ：Looper.getMainLooper().setMessageLogging(mainLooperPrinter);
    - 通过一个标志位 mPrintingStarted 区分开始时间和结束时间，得出一个时间差，如果大于指定阈值就说明卡顿了
  - 说说Hanlder内存泄露问题。
  - 利用Handler机制设计一个不崩溃的App?





## 4. Handler 消息机制实践

- Android Freamwork 中对 Handler 消息机制的应用
  - View.post 方法通过Handler 实现原理
  - 屏幕绘制
  - 。。。
- Handler 消息机制在动画效果上的应用
  - Banner 实现无限轮播
  - 。。。
- Handler 消息机制时间相关业务上的应用
  - 倒计时
- Handler 消息机制时间相关业务上的应用
  - 线程切换





## 参考

- Android Handler Native 层源码
  - [Looper.cpp](https://android.googlesource.com/platform/system/core/+/master/libutils/Looper.cpp)
  - [Looper.h](https://android.googlesource.com/platform/system/core/+/refs/heads/master/libutils/include/utils/Looper.h)
  - [android_os_MessageQueue.cpp](https://android.googlesource.com/platform/frameworks/base/+/master/core/jni/android_os_MessageQueue.cpp)
  - [android_os_MessageQueue.h](https://android.googlesource.com/platform/frameworks/base/+/master/core/jni/android_os_MessageQueue.h)
- Android Handler Java 层源码

  - [aospxref | Handler Android 11](http://aospxref.com/android-11.0.0_r21/xref/frameworks/base/core/java/android/os/Handler.java)
  - [aospxref | Handler Android 10](http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/core/java/android/os/Handler.java)

  - [GoogleSource | ThreadLocal.java Android 23](https://android.googlesource.com/platform/libcore/+/1f07ea2/luni/src/main/java/java/lang/ThreadLocal.java)

  - [GoogleSource | ThreadLocal.java Android 30](https://android.googlesource.com/platform/libcore/+/refs/heads/master/ojluni/src/main/java/java/lang/ThreadLocal.java)
- Android Handler Java 层 Api 文档
  - [Android Developers NDK Docs | Looper.cpp](https://developer.android.com/ndk/reference/group/looper)
  - [Android Developers Docs | Handler](https://developer.android.com/reference/android/os/Handler)
  - [Android Developers Docs | Looper](https://developer.android.com/reference/android/os/Looper)
  - [Android Developers Docs | MessageQueue](https://developer.android.com/reference/android/os/MessageQueue)
  - [Android Developers Docs | Message](https://developer.android.com/reference/android/os/Message)
  - [Android Developers Docs | ThreadLocal](https://developer.android.com/reference/java/lang/ThreadLocal)
- 其它
  - [掘金 | 探索 Android 消息机制](https://juejin.cn/post/6983598752837664781)
  - [简书 | Android异步通信Handler机制学习攻略](https://www.jianshu.com/p/9fe944ee02f7)
  - [Stack Overflow | When and how should I use a ThreadLocal variable?](https://stackoverflow.com/questions/817856/when-and-how-should-i-use-a-threadlocal-variable)
  - [Handler二十七问](https://mp.weixin.qq.com/s/F_-FVJ0Y8tst-iKSCLBF7w)

