# Android 内存优化详解

引用相关的问题是一个综合性的题目，包括引用类型以及使用场景，底层对象生命周期、垃圾收集机制等。

## 1. Java 内存相关机制

### Java 对象生命周期

Java对象在JVM上运行有7个阶段，如下：

- **Created（创建）**
- **InUse（应用）**
- **Invisible（不可见）**
- **Unreachable**
- **Collected**
- **Finalized**
- **Deallocated**

### Java 内存分配模型

JVM 将整个内存划分为了几块，分别如下所示：

- 方法区：存储类信息、常量、静态变量等。=> 所有线程共享
- 堆：内存最大的区域，每一个对象实际分配内存都是在堆上进行分配的，，而在虚拟机栈中分配的只是引用，这些引用会指向堆中真正存储的对象。此外，堆也是垃圾回收器（GC）所主要作用的区域，并且，内存泄漏也都是发生在这个区域。=> 所有线程共享
- 虚拟机栈：与线程的声明周期同步，虚拟机栈描述的是 java 方法执行的内存模型，每个方法执行都会创建一个**栈帧**，栈帧包含局部变量表、操作数栈、动态连接、方法出口等
- 本地方法栈：不同与虚拟机栈为 Java 方法服务、它是为 Native 方法服务的。
- 程序计数器：存储当前线程执行目标方法执行到了第几行。

### 可达性分析算法

可达性分析算法（Rechability Analysis）用于判定对象是否存活，这个算法的基本思路就是通过一系列称为 `GC Roots` 的根对象作为起始节点集，从这些节点开始，根据引用关系向下搜索，搜索过程中所走过的路径称为引用链（Reference Chain），如果某个对象到 GC Roots 之间没有任何引用链相连，也就是不可达时，则证明该对象不可能再被使用。

### Java 内存回收算法

- 标记清除算法
  - 标记所有需要回收的对象
  - 回收所有标记的对象
  - 缺点：效率不高、会产生大量不连续碎片
- 复制算法
  - 将内存划分成大小相等的两块
  - 第一块内存用完之后，复制存活的对象到第二块
  - 清理第一块的所有内存
  - 缺点：浪费一半的内存，代价大
- 标记整理算法
  - 标记所有需要回收的对象
  - 剩余存活的对象往一端进行移动
  - 回收所有标记的对象
  - 优势：解决了标记清除算法碎片问题也解决了复制算法浪费空间的问题
- 分代收集算法
  - 结合了多种收集算法的优势
  - 新生对象存活率低、不需要太多控件，适合使用复制算法
  - 老年代对象存活率高，需要长期占用大量内存，适合使用标记整理算法

### 垃圾回收时机

- 要真正宣告一个对象死亡,至少要经历两次标记过程:
- 如果对象在进行可达性分析后发现没有与 GC Roots 相连接的引用链,那它将会被第一次标记并且进行一次筛选,筛选的条件是此对象是否有必要执行 finalize() 方法。当对象没有覆盖 finalize() 方法,或者 finalize() 方法已经被虚拟机调用过,虚拟机将这两种情况都视为“没有必要执行”，也就是意味着直接回收。
- 如果这个对象被判定为有必要执行 finalize() 方法,那么这个对象将会放置在一个叫做F-Queue的队列之中,并在稍后由一个由虚拟机自动建立的、低优先级的 Finalizer 线程去执行它。这里所谓的“执行”是指虚拟机会触发这个方法,但并不承诺会等待它运行结束,这样做的原因是,如果一个对象在 finalize() 方法中执行缓慢,或者发生了死循环(更极端的情况),将很可能会导致F-Queue队列中其他对象永久处于等待,甚至导致整个内存回收系统崩溃。finalize() 方法是对象逃脱死亡命运的最后一次机会,稍后GC将对F-Queue中的对象进行第二次小规模的标记,如果对象要在finalize()中成功拯救自己，只要重新与引用链上的任何一个对象建立关联即可,譬如把自己(this关键字)赋值给某个类变量或者对象的成员变量,那在第二次标记时它将被移除出“即将回收”的集合;如果对象这时候还没有逃脱,那基本上它就真的被回收了。
- 如果对象在进行可达性分析后发现与 GC Roots 相连接的引用链上存在软、弱、虚引用包装的引用对象，那么对于相连的引用还会结合引用的特性处理相应的回收逻辑。

### 可作为GC Roots 的对象

- 方法中的局部变量引用的对象也就是虚拟机栈(栈桢中的本地变量表)中的引用的对象
- 类中的静态属性，static 修饰的也就是方法区中的类静态属性引用的对象
- 类中的常量，final 修饰的也就是方法区中的常量引用的对象
- JNI 中 C/C++ 中指向的 Java 对象也就是本地方法栈中JNI的引用的对象 
- 所有被同步锁（synchronized关键字）持有的对象
- Java 虚拟机内部的引用，如基本数据类型对应的 Class 对象

### 四种引用类型

- 不同的引用类型，主要体现的是对象不同的可达性状态和对GC的影响。

- 强引用
  - 说明：强引用（ Strong  Reference），就是我们最常见的普通对象引用，我们平常典型编码 Object obj = new Object() 中的obj就是强引用。通过关键字new创建的对象所关联的引用就是强引用。 当JVM内存空间不足，JVM宁愿抛出OutOfMemoryError运行时错误（OOM），使程序异常终止，也不会靠随意回收具有强引用的“存活”对象来解决内存不足的问题。只要还有强引用指向一个对象，就能表明对象还“活着”，垃圾收集器不会碰这类对象。
  - 回收：对于一个普通的对象，如果没有其他的引用关系，只要超过了引用的作用域或者显式地将引用赋值为 null，就是可以被垃圾收集的了，当然具体回收时机还是要看垃圾收集策略。

- 软引用
  - 说明：软引用（SoftReference），是一种相对强引用弱化一些的引用，可以让对象豁免一些垃圾收集。实现方式是通过SoftReference<User> 类包裹。
  - 回收：当内存不足，会触发 JVM 的GC，如果GC后，内存还是不足，就会把软引用的包裹的对象给干掉。JVM 会确保在抛出 OutOfMemoryError 之前，清理软引用指向的对象。
  - 应用：软引用通常用来实现内存敏感的缓存，如果还有空闲内存，就可以暂时保留缓存，当内存不足时清理掉，这样就保证了使用缓存的同时，不会耗尽内存。

- 弱引用
  - 说明：弱引用（WeakReference）并不能使对象豁免垃圾收集，仅仅是提供一种访问在弱引用状态下对象的途径。实现方式是通过WeakReference<User> 类包裹。
  - 回收：只要触发了GC操作就会回收弱引用包裹的对象。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。由于垃圾回收器是一个优先级很低的线程，因此不一定会很快回收弱引用的对象。
  - 应用：可以用来构建一种没有特定约束的关系，比如，维护一种非强制性的映射关系，如果试图获取时对象还在，就使用它，否则重现实例化。它同样是很多缓存实现的选择。
- 虚引用
  - 说明：虚引用（PhantomReference），任何时候都不能通过get访问对象。虚引用仅仅是提供了一种确保对象被 finalize 以后，做某些事情的机制。实现方式是通过PhantomReference<User> 类包裹。
  - 回收：如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。虚引用必须和引用队列 （ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中。
  - 应用：监控对象的创建和销毁。可用来跟踪对象被垃圾回收器回收的活动，当一个虚引用关联的对象被垃圾收集器回收之前会收到一条系统通知



## 2. Android 内存相关机制

### Dalvik/ART 特性

- 基于寄存器
  - 基于栈的虚拟机支持顺序访问，基于寄存器的虚拟机支持随机访问；最直观的表现就是，在java字节码里，调用一个函数时实际的invokexxx指令只要求指定被调用方法，参数必须通过栈来传递；而基于寄存器的smali里，invoke-xxx指令要求你带上参数，以寄存器的方式传递。
  - 效率方面，在只考虑解释器的情况下，基于寄存器的虚拟机内存访问次数会较少；

- dx 工具

  - 在 Java SE 程序中，Java 类会被编译成一个或多个 .class 文件，然后打包成 jar 文件，JVM 会通过对应的 .class 文件和 jar 文件获取对应的字节码。

  -  `Dalvik 有自己的字节码`， Dalvik 会用 dx 工具将所有的 .class 文件转换为一个 .dex 文件，然后会从该 .dex 文件读取指令和数据

- 与 Zygote 共享内存区域

  - Dalvik 由 Zygote 孵化器创建的，Zygote 本身也是一个 Dalvik VM 进程，当系统需要创建一个进程时，Zygote 就会进行 fork，快速创建和初始化一个 DVM 实例。
  - 对于一些只读的系统库，所有的 Dalvik 实例都能和 Zygote 共享一块内存区域，这样能节省内存开销。

- 独立进程空间
  - 在 Androd 中，每一个应用都运行在一个 Dalvik VM 实例中，`每一个 Dalvik VM 都运行在一个独立的进程空间`，这种机制使得 Dalvik 能在有限的内存中同时运行多个进程。

- 类共享机制
  - Dalvik 拥有预加载—共享机制，不同应用之间在运行时可以共享相同的类，拥有更高的效率。
  - 而 JVM 不存在这种共享机制，不同的程序，打包后的程序都是彼此独立的，即使包中使用了同样的类，运行时也是单独加载和运行的，无法进行共享。

- 不兼容 JVM
  - Dalvik 不是 Java 虚拟机，它并不是按照 Java 虚拟机规范实现的，两者之间并不兼容。
  - Dalvik 是为 32 位 CPU 设计的，而 ART 支持 64 位并兼容 32 位 CPU，这也是 Dalvik 被淘汰的主要原因。

- 预编译(ART)
  - Dalvik 中的应用每次运行时，字节码都需要通过即时编译器 JIT 转换为机器码，这会使得应用的运行效率降低。
  - 在 ART 中，系统在安装应用时会进行一次预编译（AOT，Ahead-Of-Time），将字节码预先编译成机器码并存储在本地，这样应用就不用在每次运行时执行编译了，运行效率也大大提高。
- 垃圾回收算法
  -  在 Dalvik 采用的垃圾回收算法是标记-清除算法，启动垃圾回收机制会造成两次暂停（一次在遍历阶段，另一次在标记阶段）
  - 在 ART 下，GC 速度比 Dalvik 要快，**ART** 回收算法可在 **运行期按需选择**，并且，**ART** 具备 **内存整理** 能力，**减少内存空洞**。



### 查看 Dalvik 堆信息

- dvm最大可用内存：`adb shell getprop | grep dalvik.vm.heapsize`
  - 一旦应用申请的内存超过这个值，就会 OOM。
  - 测试结果：[dalvik.vm.heapsize]: [512m]
- 单个程序限制最大可用内存：`adb shell getprop|grep heapgrowthlimit`
  - 如果在清单文件中声明 largeHeap 为 true，则 App 使用的内存到 heapsize 才会 OOM，否则达到 heapgrowthlimit 就会 OOM。 实测小米10 没有开启 largeHeap  320M 也是可以正常运行的，再次分配几十兆才会OOM，但是OOM异常信息中限制的还是256M
  - 测试结果：[dalvik.vm.heapgrowthlimit]: [256m]
  - 未开 largeHeap ：java.lang.OutOfMemoryError: Failed to allocate a 400016 byte allocation with 180072 free bytes and 175KB until OOM, target footprint 268435456, growth limit 268435456
  - 开启 largeHeap ：java.lang.OutOfMemoryError: Failed to allocate a 400016 byte allocation with 256512 free bytes and 250KB until OOM, target footprint 536870912, growth limit 536870912
- 堆分配的初始值大小：`adb shell getprop | grep dalvik.vm.heapstartsize`
  - 测试结果：[dalvik.vm.heapstartsize]: [8m]
- top 命令是 Linux 下常用的性能分析工具，能够 实时显示系统中各个进程的资源占用状况，类似于 Windows 的任务管理器。top 命令提供了 实时的对系统处理器的状态监视。它将 显示系统中 CPU 最“敏感”的任务列表。该命令可以按 CPU使用、内存使用和执行时间 对任务进行排序。 `adb shell top -n 1`

- 进程的内存概要：`adb shell dumpsys meminfo`

- 查看单个 App 进程的内存信息

  - dumpsys meminfo <pid> // 输出指定pid的某一进程

  - dumpsys meminfo --package <packagename> // 输出指定包名的进程，可能包含多个进程

  - ```shell
    adb shell dumpsys meminfo com.component.app_android
    xuejiewang@xuejiedeMacBook-Pro awesome-dev-practice % adb shell dumpsys meminfo com.component.app_android
    Applications Memory Usage (in Kilobytes):
    Uptime: 42114689 Realtime: 46765570
    
    ** MEMINFO in pid 20475 [com.component.app_android] **
                       Pss  Private  Private  SwapPss      Rss     Heap     Heap     Heap
                     Total    Dirty    Clean    Dirty    Total     Size    Alloc     Free
                    ------   ------   ------   ------   ------   ------   ------   ------
      Native Heap     4828     4812        0     9041     5524    27740    23077     4662
      Dalvik Heap     4344     2208     2012       91     5332     6445     3223     3222
     Dalvik Other     1766     1592        4      592     2292                           
            Stack      360      360        0      308      368                           
           Ashmem        4        4        0        0       12                           
          Gfx dev      420      420        0        0      420                           
        Other dev       16        0       16        0      300                           
         .so mmap     2252      212     1300       97    28140                           
        .jar mmap     1125        0      556        0    30876                           
        .apk mmap      104        0       28        0     2340                           
        .ttf mmap      498        0       88        0     1992                           
        .dex mmap      525      400       28    14716     1256                           
        .oat mmap       18        0        0        0     2672                           
        .art mmap     4493     3044     1024     4795    12500                           
       Other mmap       13        4        0        0      600                           
       EGL mtrack     9660     9660        0        0     9660                           
        GL mtrack     5876     5876        0        0     5876                           
          Unknown      576      540        0      148      832                           
            TOTAL    66666    29132     5056    29788    66666    34185    26300     7884
     
     App Summary
                           Pss(KB)                        Rss(KB)
                            ------                         ------
               Java Heap:     6276                          17832
             Native Heap:     4812                           5524
                    Code:     2648                          67748
                   Stack:      360                            368
                Graphics:    15956                          15956
           Private Other:     4136
                  System:    32478
                 Unknown:                                    3564
     
               TOTAL PSS:    66666            TOTAL RSS:   110992       TOTAL SWAP PSS:    29788
     
     Objects
                   Views:       51         ViewRootImpl:        2
             AppContexts:        8           Activities:        2
                  Assets:       22        AssetManagers:        0
           Local Binders:       22        Proxy Binders:       38
           Parcel memory:        5         Parcel count:       22
        Death Recipients:        2      OpenSSL Sockets:        0
                WebViews:        0
     
     SQL
             MEMORY_USED:        0
      PAGECACHE_OVERFLOW:        0          MALLOC_SIZE:        0
    
    #Pss: 该进程独占的内存+与其他进程共享的内存（按比例分配，比如与其他3个进程共享9K内存，则这部分为3K）
    #Privete Dirty:该进程独享内存
    #Heap Size:分配的内存
    #Heap Alloc:已使用的内存
    #Heap Free:空闲内存
    ```

- ActivityManager.getMemoryInfo()

  - Android 提供了一个 ActivityManager.getMemoryInfo() 方法给我们查询内存信息，这个方法会返回一个 ActivityManager.MemoryInfo 对象，这个对象包含了系统当前内存状态，这些状态信息包括可用内存、总内存以及低杀内存阈值。

  - ```
    小米10 测试：Dalvik 2.1.0 单位（M）
         memoryInfo-totalMem: 7640
         memoryInfo-availMem: 2568
         memoryInfo-lowMemory: false
         memoryInfo-threshold: 1080
         memoryInfo-foregroundAppThreshold: 72
         memoryInfo-hiddenAppThreshold: 1417
         memoryInfo-visibleAppThreshold: 90
         memoryInfo-secondaryServerThreshold: 1080
    ```

### Android 内存管理机制

- 内存弹性分配，分配置与最大值受具体设备影响
- OOM产生场景：内存真正不足、可用内存不足并且系统也没有多余的内存可分配了

### 五种进程优先级

低杀的全称是 `LowMemoryKiller` ，LowMemoryKiller 跟垃圾回收器 GC 很像，GC 的作用是保证应用有足够的内存可以使用，而低杀的作用是保证系统有足够的内存可以使用。

在 Android 中不同的进程有着不同的优先级，当两个进程的优先级相同时，LowMemoryKiller 会优先考虑干掉消耗内存更多的进程，也就是如果我们应用占用的内存比其他应用少，并且处于后台时，我们的应用就能在后台存活更长时间，做更多时间。

Android 进程在优先级方面可以分为 5 种：`前台进程`、`可见进程`、`服务进程`、`后台进程`与`空进程`。

- 前台进程
  - 前台进程（Foreground Process）是正在于用户交互的进程，是优先级最高的进程，如果满足下面 5 种情况，则一个进程就是前台进程。
  - 进程持有一个与用户交互的 Activity（该 Activity 的 onResume 方法被调用）
  - 进程持有一个 Service，Service 与用户正在交互的 Activity 绑定
  - 进程持有一个 Service，Service 调用了 `startForeground()` 方法
  - 进程持有一个 Service，Service 正在执行 `onCreate()`、`onStart()`、`onDestroy()` 方法
  - 进程持有一个 BroadcastReceiver，并且这个 BroadcastReceiver 正在执行 `onReceive()` 方法

- 可见进程

  - 可见进程（Visible Process）不含有任何前台组件，但用户还能在屏幕上看见它，当满足一下 2 个条件时，进程被认定是可见进程。
  - 进程持有一个 Activity，这个 Activity 处于 pause 状态，比如前台 Activity 打开了一个对话框，这样后面的 Activity 就处于 pause 状态
  - 进程持有一个 Service， 这个 Service 和一个可见的 Activity 绑定。

- 服务进程

  - 服务进程（Service Process）一般用来播放音乐或在后台下载文件，除非系统内存不足，否则 Android 系统会尽量维持服务进程的运行。

  - 当一个进程中运行着一个 Service，并且这个 service 是通过 startService() 开启的，那这个进程就是一个服务进程

- 后台进程

  - 系统会把后台进程（Background Process）保存在*一*个 LruCache 列表中，因为终止后台进程对用户体验影响不大，所以系统会酌情清理部分后台进程。

  - 有需要的话可以在 Activity 的 onSaveInstanceState() 方法中保存一些数据，以免在应用在后台被系统清理掉后，用户已输入的信息被清空，导致要重新输入。

  - 当一个进程持有一个用户不可见的 Activity（Activity 的 onStop() 方法被调用），但是 onDestroy() 方法没有被调用，这个进程就会被系统认定为后台进程。

- 空进程

  - 当一个进程不包含任何活跃的应用组件，则被系统认定为是空进程。

  - 系统保留空进程的目的是为了加快下次启动进程的速度。

### ComponentCallback2

在 Android 4.0 后，Android 应用可以通过在 Activity 中重写 onTrimMemory 方法或实现 ComponentCallback2 接口获取系统内存的相关事件，这样就能在系统内存不足时提前知道这件事，提前做出释放内存的操作，避免我们自己的应用被系统干掉。

**onTrimMemory**(level: **Int**)：当应用处于后台或系统资源紧张时，我们可以在这里方法中释放资源，避免被系统将我们的应用进行回收

- 根据不同的应用生命周期和系统事件进行不同的操作
- ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN：应用界面处于后台，可以在这里释放 UI 对象
-  ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE：应用正常运行中，不会被杀掉，但是系统内存已经有点低了
- ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW：系统内存已经非常低了，这时候应该释放一些不必要的资源以提升系统性能
-  ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL：应用正常运行，但是系统内存非常紧张，统已经开始根据 LRU 缓存杀掉了大部分缓存的进程。这时候我们要释放所有不必要的资源，不然系统可能会继续杀掉所有缓存中的进程
- ComponentCallbacks2.TRIM_MEMORY_BACKGROUND：*这时我们的程序在 LRU 缓存列表的最近位置，不太可能被清理掉，*               但是也要去释放一些比较容易恢复的资源，让系统内存变得充足
- ComponentCallbacks2.TRIM_MEMORY_MODERATE：*系统内存很低，并且我们的应用处于 LRU 列表的中间位置*
-  ComponentCallbacks2.TRIM_MEMORY_COMPLETE：系统内存非常低，并且我们的应用处于 LRU 列表的最边缘位置，把所有能释放的资源都释放了

## 3. 内存问题

### 内存带来的具体问题

- 内存抖动：内存曲线锯齿状、GC频繁、导致卡顿
- 内存泄漏：可用内存逐渐减少、频繁GC、导致卡顿
- 内存溢出：程序异常，导致崩溃



### 内存抖动

- 定义：内存频繁分配和回收导致内存不稳定，例如短时间内频繁创建大量临时对象时，就会引起内存抖动
- 表现：频繁GC、Profiler 内存曲线呈锯齿状
- 危害：导致卡顿、OOM
- 导致OOM的原因
  - 频繁创建对象，导致内存不足以及产生很多不连续的碎片
  - 不连续的内存碎片无法被分配，导致OOM
- 解决方法
  - 使用 Memory Profiler 初步排查，直观查看内存曲线是否出现锯齿状
  - Memory Profiler 或 CPU Profiler 进行具体的代码确定
  - 查找技巧：找循环或者轮询频繁调用的地方
  - 解决方案：
    - 尽量避免在循环体中创建对象
    - 尽量不要在自定义 View 的 onDraw() 方法中创建对象，因为这个方法会被频繁调用
    - 对于能够复用的对象，可以考虑使用对象池把它们缓存起来，可以参考Message 的对象池



### 内存泄漏

#### 什么是内存泄漏？

内存泄漏是一种编程错误，它导致应用程序保留了对不再需要的对象的引用。比如Activity 退出后，内存中还存在若干对该Activity 的引用。导致该对象分配的内存无法回收，内存曲线一般表现为内存抖动，可用内存逐渐减少。

内存泄漏在 Android 应用程序中非常普遍，小内存泄漏的累积会导致应用程序内存不足、GC频繁、OOM 崩溃等问题。

内存泄漏主要分两种情况，一种是同一个对象泄漏，还有一种情况更加糟糕，就是每次都会泄漏新的对象，可能会出现几百上千个无用的对象。

#### 内存泄漏的常见原因?

常见的导致内存泄漏的 3 个原因分别是：`非静态内部类`、`静态变量`、`资源未释放`，`内存泄漏的本质就是`长生命周期对象持有了短生命周期对象的引用`，导致短生命周期对象无法被释放。

大多数内存泄漏是由与对象生命周期相关的错误引起的。以下是一些常见的 Android 错误：

- 非静态内部类（内部类、匿名内部类）持有外部类引用引起的泄漏
  - 原因：匿名内部类指的是一个没有人类可识别名称的类，但是在字节码中，匿名内部类也有构造函数，而这个构造函数的参数就包含外部类的实例。
  - 解决方案：我们可以把 Handler 或 AsyncTask 声明为静态内部类，采用构造方法传入Activity的引用并且使用 WeakReference 包住 Activity，这样 Handler 拿到的就是一个 Activity 的弱引用，GC 时就可以回收 Activity。
- 静态变量引起的内存泄漏
  - 原因：静态变量导致内存泄漏的原因是因为`长生命周期对象持有了短生命周期对象的引用`，导致短生命周期对象无法被释放。
  - 场景：比如一个单例持有了 Activity 的引用，而 Activity 的生命周期可能很短，用户一打开就关闭了，但是单例的生命周期往往是与应用的生命周期相同的，从而导致 Activity 无法被释放。
  - 解决方案：如果单例需要 Context， 可以考虑使用 ApplicationContext，这样单例持有的 Context 引用就是与应用的生命周期相同的了。
- 资源未释放引起的内存泄漏
  - 原因：对于使用了 BraodcastReceiver，ContentObserver，File，Bitmap，RxJava，协程等资源的使用完未正确释放。
  - 解决方案：在Activity销毁时及时关闭或者注销，否则这些资源将不会被回收，造成内存泄漏。

- 具体到业务场景的内存泄漏场景，具体就是以上三大类问题的业务实现（具体业务场景待补充）
  - Timer泄漏漏
  - 地图没有正确destroy
  - 某定位SDK内存泄漏漏
  - 某地图SDK路路线绘制泄漏漏
  - Bitmap泄漏漏
  - 某SDK数据缓存泄漏漏
  - 在不清除 Fragment.onDestroyView() 中的 Fragment 视图字段的情况下，将 Fragment 实例添加到 backstack。
  - 将 Activity 实例作为 Context 字段存储在由于配置更改而在 Activity  重新创建中幸存的对象中。
  - 注册一个侦听器、广播接收器或 RxJava 订阅，这些订阅引用具有生命周期的对象，并且在生命周期结束时忘记取消注册。
  - WebView内存泄漏
    - 系统会在attach和detach处进行注册和反注册component callback； 在onDetachedFromWindow() 方法的第一行中：**if** (isDestroyed()) **return**;，如果 isDestroyed() 返回 true 的话，那么后续的逻辑就不能正常走到，所以就不会执行unregister的操作；我们的activity退出的时候，都会主动调用 WebView.destroy() 方法，这会导致 isDestroyed() 返回 true；destroy()的执行时间又在onDetachedFromWindow之前，所以就会导致不能正常进行unregister()。
    - 解决方法就是：**让onDetachedFromWindow先走，在主动调用destroy()之前，把webview从它的parent上面移除掉。**
  - Handler 
  - AsyncTask
  - Thread



#### LeakCanary 的工作原理

**[ leakcanary](https://github.com/square/leakcanary)**

安装 LeakCanary 后，它会通过 4 个步骤自动检测并报告内存泄漏：

1. 检测可能泄漏的对象；
2. 堆快照，生成hprof文件；
3. 分析hprof文件；
4. 对泄漏进行分类。

自动检测的对象包含以下四类：

- 销毁的Activity实例
- 销毁的Fragment实例\
- 销毁的View实例
- 清除的ViewModel实例

另外，LeakCanary也会检测 **AppWatcher** 监听的对象



LeakCanary在判定有内存泄漏时，首先会生成一个内存快照文件(`.hprof`文件)，这个快照文件通常有10+M，然后根据 referenceKey 找出泄漏实例，再在快照堆中使用BFS找到实例所在的节点，并以此节点信息反向生成最小引用链。在生成引用链后，将其保存在 `AnalysisResult` 对象当中，然后将`AnalysisResult`对象写入`.hporf.result`文件，此时的`.hprof.result`文件只有几十KB大小。最后，在 `DisplayLeakActivity` 的 `onResume` 中读取所有的 `.hprof.result`文件并显示在界面上。



直接将 LeakCanary 应用于线上会有如下一些问题：

1. 每次内存泄漏以后，都会生成一个`.hprof`文件，然后解析，并将结果写入`.hprof.result`。增加手机负担，引起手机卡顿等问题。
2. 同样的泄漏问题，会重复生成 `.hprof` 文件，重复分析并写入磁盘。
3. `.hprof`文件较大，信息回捞成问题。



### KOOM

#### KOOM的工作原理

**[ KOOM](https://github.com/KwaiAppTeam/KOOM)**



.触发内存泄漏检测，常规是watcher activity/fragment的onDestroy，而KOOM是定期轮询查看当前内存是否到达阀值；

dump hprof，常规是对应进程dump，而KOOM是fork进程dump。





### 内存溢出

如何避免OOM的产生

OOM如何产生

- 已使用的内存+新申请的内存>可分配内存
- OOM 几乎覆盖所有的内存区域，通常指堆区
- Native Heap 在物理内存不足时也会抛出OOM





## 4. 内存优化

### 内存优化背景介绍

- 内存是大问题但往往缺乏关注
  - JVM 系列的虚拟机都提供 GC 自动回收内存，所以一般情况下不会关注内存的分配和回收情况
  - 内存溢出(OutOfMemory)复现困难
  - ⽆无法有效获得线上内存泄漏漏的可疑对象
- 压死骆驼的最后一根稻草

  - 在异常收集平台上可能看到内存溢出的错误，但是出这个错误的地方有可能不是根本原因，内存问题相对复杂，是一个逐渐挤压的过程，上报上来的堆栈信息可能就是压死骆驼的最后一根稻草，并不是引起 oom 的根本原因
  - 堆栈信息不不能看出内存泄漏漏的根本原因
  - 特别是第三⽅方SDK的内存问题更更为棘⼿手
- 内存问题与崩溃

  - 内存问题导致 Crash 的具体表现就是内存溢出异常 OOM、还包括内存分配失败异常，因为整体内存不足导致应用被杀死、设备重启等问题。
- 内存问题与卡顿

  - Java 内存不足会导致频繁 GC，这个问题在 Dalvik 虚拟机会更加明显。而 ART 虚拟机在内存管理跟回收策略上都做大量优化，内存分配和 GC 效率相比提升了 5～10 倍。
  - 在 GC 时，所有线程都要停止，包括主线程，当 GC 和绘制界面的操作同时触发时，绘制的执行就会被搁置，导致掉帧，也就是界面卡顿。系统负载过高也是造成卡顿的另外一个原因。

- 内存与程序存活时长
  - 物理内存不足时系统会触发 low memory killer 机制，结合进程优先级与回收收益策略清理进程，清理进程时优先会考虑清理后台进程，如果某个应用在后台运行并且占用的内存更多，就会被优先清理掉。
  - 用户在移动设备上使用应用的过程中被打断是很常见的，如果我们的应用不能活到用户回来的时候，要用户再次进行操作的体验就会很差。



### 工具选择

- Memory Profiler
  - 功能
  
    - 堆转储
    - 查看内存分配详情
  
  - 实时图表展示应用内存使用量，非常直观
  
  - 识别内存泄漏、抖动等
  
  - 提供捕获转储、强制GC以及跟踪内存分配的能力
  
  - 适合线下平时使用
  
  - `javaHeap`:这部分内存大小是有限制的，溢出则会OOM，这部分内存也是我们分析优化的重点
  
    `NativeHeap`:native层的 so 中调用malloc或new创建的内存,对于单个进程来说大小没有限制，所以可以利用在native层分配内存来缓解javaHeap的压力（比如2.3.3之前Android Bitmap的内存分配就是在native层，之后移到javaHeap, 8.0又回到native）
  
    `Graphics`：这部分一般游戏app中用的较多，OpenGL和SurfaceFlinger相关的内存，若没有直接调用到OpenGL,则一般不会涉及到这块内存
  
    `Stack`:栈，了解jvm内存模型的应该都知道
  
    `Code`: 代码，主要是dex以及so等占用的内存
  
  - 所以我们可以看到事实上我们可以优化的点有：JavaHeap、NativeHeap、Stack、Code所占用的内存
  
- Memory Analyzer
  - 强大的 Java Heap 分析工具，可用于查找内存泄漏以及内存占用
  
  - 生成整体报告、分析问题等
  
  - 适合线下深入使用
  
  - 转换hprof：platform-tools hprof-conv ../原始文件.hprof ../输出文件.hprof 
  
  - Dominator ⽀支配者 —— 如果从GC root到达对象Y的路路径上，必须经过对象X，那么X就是Y的⽀支配者。
  
  - 有一点比较不友好的是，MAT需要标准的.hprof文件,所以在AndroidStduio的Profiler中GC后dump出的内存快照还要自己手动利用android sdk platform-tools下的hprof-conv进行转换一下才能被MAT打开。 当然如果觉得麻烦的话也可以自己写个脚本执行几条命令来直接完成GC->dump java heap->转换.hprof文件   这个流程：
  
    - ```
      //adb and hprof-conv
      ADB=${ANDROID_HOME}/platform-tools/adb
      HPROF_CONV=${ANDROID_HOME}/platform-tools/hprof-conv
      //dump java heap
      ${ADB} shell "am dumpheap $(PACKAGE_NAME) $(OUT_PATH)"
      //conv hprof
      ${HPROF_CONV} -z ${FILE_NAME} droid-${FILE_NAME}
      ```
  
  - 下载链接：https://www.eclipse.org/mat/downloads.php
  
- LeakCanary
  - 自动内存泄漏检测
  - 适合线上集成
  - Github地址：https://github.com/square/leakcanary
  
- KOOM
  - 自动内存泄漏检测
  - 适合线上集成
  - Github地址：https://github.com/KwaiAppTeam/KOOM
  
- JHat

- OutOfMemory 分析组件 [Probe](https://static001.geekbang.org/con/19/pdf/593bc30c21689.pdf)





图片内存优化

- 大图检测：Epic Hook

内存问题线上监控

- 设定场景线上Dump:Debug.dumpHprofData() 将当前内存信息转化为本地文件上传
  - 具体操作：超过最大内存的80%-内存Dump-回传hprof文件-MAT 手动分析
  - 缺点：Dump 文件太大，上传失败率高，分析困难，配合一定策略只可以达到一定效果
- LeakCanary 定制带到线上
  - 预设泄漏怀疑点
  - 发现泄漏回传
  - 缺点：不适合所有情况，需要预设怀疑点，分析过程慢也容易OOM

内存优化总结

- 优化方向：设备分级、内存泄漏、内存抖动、BItmap
- LargeHeap 申请更多内存
- onTrimMemory 回调，根据内存等级做处理
- SparseArray 优化集合
- 慎用SP
- 业务架构设计合理，懒加载，分批加载等



设备分级

- 同一个应用在 4GB 内存的手机运行得非常流畅，但在 1GB 内存的手机就不一定可以做到，而且在系统空闲和繁忙的时候表现也不太一样。

- 内存优化首先需要根据设备环境来综合考虑并不是存占用越少越好，其实我们可以让高端设备使用更多的内存，做到针对设备性能的好坏使用不同的内存分配和回收策。
- 良好的架构设计支撑：使用类似 [device-year-class](https://github.com/facebookarchive/device-year-class) 的策略对设备分级，对于低端机用户可以关闭复杂的动画，或者是某些功能；使用 565 格式的图片，使用更小的缓存内存等。在现实环境下，不是每个用户的设备都跟我们的测试机一样高端，在开发过程我们要学会思考功能要不要对低端机开启、在系统资源吃紧的时候能不能做降级。
- 缓存管理。我们需要有一套统一的缓存管理机制，可以适当地使用内存；当“系统有难”时，也要义不容辞地归还。我们可以使用 OnTrimMemory 回调，根据不同的状态决定释放多少内存。对于大项目来说，可能存在几十上百个模块，统一缓存管理可以更好地监控每个模块的缓存大小。
- 进程模型。一个空的进程也会占用 10MB 的内存，而有些应用启动就有十几个进程，甚至有些应用已经从双进程保活升级到四进程保活，所以减少应用启动的进程数、减少常驻进程、有节操的保活，对低端机内存优化非常重要。
- 安装包大小。安装包中的代码、资源、图片以及 so 库的体积，跟它们占用的内存有很大的关系。一个 80MB 的应用很难在 512MB 内存的手机上流畅运行。这种情况我们需要考虑针对低端机用户推出 4MB 的轻量版本，例如 Facebook Lite、今日头条极速版都是这个思路。



内存监控

- Java 内存泄漏。建立类似 LeakCanary 自动化检测方案，至少做到 Activity 和 Fragment 的泄漏检测。在开发过程，我们希望出现泄漏时可以弹出对话框，让开发者更加容易去发现和解决问题。
- OOM 监控。美团的Probe ,快手的KOOM, 自定义LeakCanary
- **onTrimMemory**(level: **Int**)：当应用处于后台或系统资源紧张时，我们可以在这里方法中释放资源，避免被系统将我们的应用进行回收
  - 根据不同的应用生命周期和系统事件进行不同的操作
  - ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN：应用界面处于后台，可以在这里释放 UI 对象
  -  ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE：应用正常运行中，不会被杀掉，但是系统内存已经有点低了
  - ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW：系统内存已经非常低了，这时候应该释放一些不必要的资源以提升系统性能
  -  ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL：应用正常运行，但是系统内存非常紧张，统已经开始根据 LRU 缓存杀掉了大部分缓存的进程。这时候我们要释放所有不必要的资源，不然系统可能会继续杀掉所有缓存中的进程
  - ComponentCallbacks2.TRIM_MEMORY_BACKGROUND：*这时我们的程序在 LRU 缓存列表的最近位置，不太可能被清理掉，*               但是也要去释放一些比较容易恢复的资源，让系统内存变得充足
  - ComponentCallbacks2.TRIM_MEMORY_MODERATE：*系统内存很低，并且我们的应用处于 LRU 列表的中间位置*
  -  ComponentCallbacks2.TRIM_MEMORY_COMPLETE：系统内存非常低，并且我们的应用处于 LRU 列表的最边缘位置，把所有能释放的资源都释放了

- 采集方式：用户在前台的时候，可以每 5 分钟采集一次 PSS、Java 堆、图片总内存
- 内存异常率：可以反映内存占用的异常情况，如果出现新的内存使用不当或内存泄漏的场景，这个指标会有所上涨。其中 PSS 的值可以通过 Debug.MemoryInfo 拿到。`内存 UV 异常率 = PSS 超过 400MB 的 UV / 采集 UV`
- 触顶率：可以反映 Java 内存的使用情况，如果超过 85% 最大堆限制，GC 会变得更加频繁，容易造成 OOM 和卡顿。`内存 UV 触顶率 = Java 堆占用超过最大堆限制的 85% 的 UV / 采集 UV`，float proportion = (float) (runtime.totalMemory-runtime.freeMemory())/ javaMax;
- 一般客户端只上报数据，所有计算都在后台处理，这样可以做到灵活多变。后台还可以计算平均 PSS、平均 Java 内存、平均图片占用这些指标，它们可以反映内存的平均情况。通过平均内存和分区间内存占用这些指标，我们可以通过版本对比来监控有没有新增内存相关的问题。





Bitmap 优化

- 图片在 Android 中对应的是 Bitmap 和 Drawable 类，我们从网络上加载下来的图片最终会转化为 Bitmap。图片会消耗大量内存，如果使用图片不当，很容易就会造成 OOM。

- 获取 Bitmap 占用的内存大小

  - `Bitmap.getByteCount()`：Bitmap 提供了一个 getByteCount() 方法获取图片占用的内存大小，但是这个方法只能在程序运行时动态计算。
  - 图片占用内存公式：`宽 * 高 * 一个像素占用的内存`。假如我们现在有一张 2048 * 2048 的图片，并且编码格式为 ARGB_8888，那么这个图片的大小为 2048 * 2048 * 4 = 16, 777, 216 个字节，也就是 16M。

- 四种 Bitmap 解码选项

  - 下面四种解码选项中的的 ARGB 分别代表透明度和三原色 Alpha、Red、Green、Blue。
  - ARGB_8888：ARGB 四个通道的值都是 8 位，加起来 32 位，也就是每个像素占 4 个字节

  - ARGB_4444：ARGB 四个通道的值都是 4 位，加起来 16 位，也就是每个像素占 2 个字节

  - RGB_565：RGB 三个通道分别是 5 位、6 位、5 位，加起来 16 位，也就是每个像素占 2 个字节

  - ALPHA_8：只有 A 通道，占 8 位，也就是每个像素占 1 个字节

- Android Bitmap 内存分配的变化
  - 在 Android 3.0 之前，Bitmap 对象放在 Java 堆，而像素数据是放在 Native 内存中。如果不手动调用 recycle，Bitmap Native 内存的回收完全依赖 finalize 函数回调，但是，这个时机不太可控。
  - Android 3.0～Android 7.0 将 Bitmap 对象和像素数据统一放到 Java 堆中，这样就算我们不调用 recycle，Bitmap 内存也会随着对象一起被回收。不过 Bitmap 是内存消耗的大户，把它的内存放到 Java 堆中似乎不是那么美妙。即使是最新的华为 Mate 20，最大的 Java 堆限制也才到 512MB，可能我的物理内存还有 5GB，但是应用还是会因为 Java 堆内存不足导致 OOM。Bitmap 放到 Java 堆的另外一个问题会引起大量的 GC，对系统内存也没有完全利用起来。
  - 有没有一种实现，可以将 Bitmap 内存放到 Native 中，也可以做到和对象一起快速释放，同时 GC 的时候也能考虑这些内存防止被滥用？NativeAllocationRegistry 可以一次满足你这三个要求，Android 8.0 正是使用这个辅助回收 Native 内存的机制，来实现像素数据放到 Native 内存中。Android 8.0 还新增了硬件位图 Hardware Bitmap，它可以减少图片内存并提升绘制效率。
- 图片优化方案
  - ImageView 大小与图片大小尽量匹配
    - 如果服务器返回给我们的图片是 200 * 200，但是我们的 ImageView 大小是 100 * 100，如果直接把图片加载到 ImageView 中，那就是一种内存浪费。
    - Glide 会根据 ImageView 的大小把图片大小调整成 ImageView 的大小加载图片，并且 Glide 有三级缓存，在内存缓存中，Glide 会根据屏幕大小选择合适的大小作为图片内存缓存区的大小。
  - 统一图片库：图片内存优化的前提是收拢图片的调用，这样我们可以做整体的控制策略。例如低端机使用 565 格式、更加严格的缩放算法，可以使用 Glide、Fresco 或者采取自研都可以。而且需要进一步将所有 Bitmap.createBitmap、BitmapFactory 相关的接口也一并收拢。
  - 统一监控：在统一图片库后就非常容易监控 Bitmap 的使用情况了。
    - 大图片监控。我们需要注意某张图片内存占用是否过大，例如长宽远远大于 View 甚至是屏幕的长宽。在开发过程中，如果检测到不合规的图片使用，应该立即弹出对话框提示图片所在的 Activity 和堆栈，让开发同学更快发现并解决问题。在灰度和线上环境下可以将异常信息上报到后台，我们可以计算有多少比例的图片会超过屏幕的大小，也就是图片的“超宽率”。
    - 重复图片监控。重复图片指的是 Bitmap 的像素数据完全一致，但是有多个不同的对象存在。这个监控不需要太多的样本量，一般只在内部使用。
    - 图片总内存。通过收拢图片使用，我们还可以统计应用所有图片占用的内存，这样在线上就可以按不同的系统、屏幕分辨率等维度去分析图片内存的占用情况。在 OOM 崩溃的时候，也可以把图片占用的总内存、Top N 图片的内存都写到崩溃日志中，帮助我们排查问题。



设计合理业务架构加持内存优化

- 架构和监控需要两手抓，一个好的架构可以减少甚至避免我们犯错，而一个好的监控可以帮助我们及时发现问题



大佬总结的内存优化5R法则

- Reduce 缩减：降低图片分辨率、重采样、抽稀策略
- Reuse 复用：池化策略、避免频繁创建对象、减少GC压力
- Recycle 回收：主动销毁、结束、避免内存泄漏，实现声明周期闭环
- Refactor 重构：选择更适合的数据结构、更合理的程序架构
- Revalue 重审：谨慎使用 LargeHeap、多进程、第三方框架



内存优化技巧

- 谨慎使用 Service
  - 当你的应用中运行着一个 Service，除非系统内存不足，否则它不会被干掉，对于系统来说 Service 的运行成本很高，因为 Service 占用的内存其他的进程是不能使用的。
  - 如果我们是用 Service 监听一些系统广播，可以考虑使用 JobScheduler。
  - 如果你真的要用 Service，可以考虑使用 IntentService，IntentService 是 Service 的一个子类，在它的内部有一个工作线程来处理耗时任务，当任务执行完后，IntentService 就会自动停止。

- 使用合适的数据结构

  - HashMap/ArrayMap/SparseArray、SparseBooleanArray 以及SparseLongArray。
  - 数据规模大于1000且增删频繁考虑HashMap
  - 数据规模小且增删不频繁且Key是整数考虑SparseArray否则考虑ArrayMap
  - HashMap ：增、删、改、查都能解决O(1) 、2倍扩容、无缩容机制、增加额外的 Entry 对象
  - ArrayMap：增、删、改、查都能解决O(logN) 、1.5倍扩容、0.5倍索缩容、无额外的对象开销、支持小数组复用池
  - SparseArray 之所以更高效，是因为它的设计是只能使用整型作为 key，这样就避免了自动装箱的开销。
- 避免使用枚举
  - 单个枚举类会增加安装包的大小1.0-1.4KB
  - 替代方案：@IntDef 编译提示
- Apk 瘦身
  - Bitmap 大小、资源、动画以及第三方库会影响到 APK 的大小，减少APK的大小同样也能减少内存占用
- Bitmap 使用

  - 尽量根据实际需求选择合适的分辨率
  - 注意原始文件分辨率与内存缩放的结果，设备密度有关
  - 不使用帧动画，使用代码实现动效
  - 考虑对 Bitmap 重采样与复用配置
- 谨慎使用多进程
  - 新开一个子进程会消耗 5-10M
- 谨慎使用 Large Heap
  - 开启大堆：android:largeHeap="true"
  - 大堆的弊端会影响GC的效率
- 使用NDK
  - Native Heap 没有专门的使用限制，只要物理内存足够就可以
  - 内存大户的核心逻辑放在Native 层：Cocos2dx\Unit3D游戏、OpenGL等



StrictMode

- [Android Developers | StrictMode](https://developer.android.com/reference/android/os/StrictMode) 
- StrictMode 是一个开发者工具，常用于捕获在应用主线程中发生的磁盘I/O、网络访问违例等问题。
- 不应在正式版本启用 StrictMode



## 参考

[极客时间 | Java核心技术面试精讲](https://time.geekbang.org/column/article/6970?cid=100006701)

[极客时间 | Android 开发高手课](https://time.geekbang.org/column/article/71610)

[微信 Android 终端内存优化实践](https://mp.weixin.qq.com/s/KtGfi5th-4YHOZsEmTOsjg?)

[Android内存优化杂谈](https://mp.weixin.qq.com/s/Z7oMv0IgKWNkhLon_hFakg?)

[Android性能优化之内存优化](https://juejin.cn/post/6844904096541966350)

[深入探索 Android 内存优化（炼狱级别-上）](https://juejin.cn/post/6844904099998089230)

[深入探索 Android 内存优化（炼狱级别-下）](https://juejin.cn/post/6872919545728729095)

[实践App内存优化：如何有序地做内存分析与优化](https://juejin.cn/post/6844903618642968590#comment)

[探索 Android 内存优化方法](https://juejin.cn/post/6844903897958449166)

[我们常说的dalvik虚拟机是基于寄存器的，而jvm是基于栈，到底指的是什么？](https://www.wanandroid.com/wenda/show/13383)

[美团 Android OOM案例分析](https://blog.csdn.net/meituantech/article/details/80062521)







## 内存优化相关文章

内存优化那么主要就是去消除应用中的内存泄露、避免内存抖动；常用工具就是AS自带的内存检测，可以很好的发现内存抖动；leakcanary可以非常方便的帮助我们发现内存泄露；MAT可以做更多的内存分析。

- Android性能优化（三）之内存管理

http://www.jianshu.com/p/c4b283848970

- Android性能优化第（二）篇---Memory Monitor检测内存泄露

http://www.jianshu.com/p/ef9081050f5c

- 内存泄露实例分析 -- Android内存优化第四弹

http://www.jianshu.com/p/cbe2ee08ca02

- Android最佳性能实践(一)——合理管理内存

http://blog.csdn.net/guolin_blog/article/details/42238627

- Android最佳性能实践(二)——分析内存的使用情况

http://blog.csdn.net/guolin_blog/article/details/42238633

- [Android性能优化-内存泄漏的8个Case](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650822597&idx=1&sn=462b116f97623f239ecf667d3bdef446&chksm=80b7835bb7c00a4d4cbc9f7e19829d9a99f3cf58c1bc43dace16ffec58c98668927c9fa8dcda&scene=21#wechat_redirect)
- Android 内存优化总结&实践

https://mp.weixin.qq.com/s/2MsEAR9pQfMr1Sfs7cPdWQ

- Android内存优化之OOM

http://hukai.me/android-performance-oom/

- Android应用内存泄露分析、改善经验总结

https://zhuanlan.zhihu.com/p/20831913

- 内存泄露从入门到精通三部曲之基础知识篇

http://dev.qq.com/topic/59152c9029d8be2a14b64dae

- 内存泄露从入门到精通三部曲之排查方法篇

http://dev.qq.com/topic/591522d9142eee2b6b9735a2

- [手把手教你在Android Studio 3.0上分析内存泄漏](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650824544&idx=1&sn=2fc3cc16806bd1ddd9902ccef2cd12f5&chksm=80b78bfeb7c002e822314661aa0df8e8d7981fb2ff3bc362c129bbf2952a87ff59a6213b821c&scene=21#wechat_redirect)
