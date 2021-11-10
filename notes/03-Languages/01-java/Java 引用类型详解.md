# Android 引用类型

Java 引用相关的源码关系图，Android 中的 Java 库减少了一些类，[详见这里](https://developer.android.google.cn/reference/java/lang/ref/package-summary?hl=en)。Java 提供引用对象类，这些类支持与垃圾收集器的有限程度的交互。一个程序可以使用一个引用对象来维护对某个其他对象的引用，这样被包装的对象仍然可以被收集器回收。程序还可以在对象的可达性已更改后的某个时间收到通知。

![Package ref](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211009120238.png)



### 引用包装类

引用对象封装了对其他对象的引用，以便可以像任何其他对象一样检查和操作引用本身。Java 提供了三种类型的引用对象，每一种都比上一种弱：*soft*, *weak*, 和 *phantom*。每种类型对应于不同级别的可达性。每个引用对象类型都是继承自抽象类 [Reference](https://developer.android.google.cn/reference/java/lang/ref/Reference) 实现。每个子类实例都封装了对特定对象的单个引用，称为所指对象。每个引用对象都提供了获取和清除引用的方法。除了清除操作之外，引用对象在其他方面是不可变的，因此不提供 set 操作。程序可以进一步对这些子类进行子类化，添加其目的所需的任何字段和方法，或者它可以直接使用这些子类。

不同的引用类型，主要体现的是对象不同的可达性状态和对GC的影响。

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

### 引用包装类源码分析(Android 版)

#### ReferenceQueue.java

引用队列，在检测到适当的可达性更改后，垃圾收集器会将注册的引用对象附加到该队列中。

```java
private static final ReferenceQueue<User> RQ = new ReferenceQueue<>();

public class ReferenceQueue<T> {
  //哨兵引用对象
  private static final Reference sQueueNextUnenqueued = new PhantomReference(null, null);
  //ReferenceQueue 的这种实现是 FIFO（类似队列）
  private Reference<? extends T> head = null;
  private Reference<? extends T> tail = null;

  //入队
  boolean enqueue(Reference<? extends T> reference) {
    synchronized (lock) {
        if (enqueueLocked(reference)) {
            lock.notifyAll();
            return true;
        }
        return false;
    }
 }
  //将给定的引用加入此队列。 调用者负责确保锁定在此队列上，并在引用入队后调用此队列上的 notifyAll。 如果引用已成功入队，则返回 true，如果引用已入队，则返回 false
  private boolean enqueueLocked(Reference<? extends T> r) {
    // 已经入队
    if (r.queueNext != null) {
        return false;
    }

    if (r instanceof Cleaner) {
        //如果这个引用是一个 Cleaner，那么只需调用 clean 方法而不是将它排入队列。清洁器与从不轮询且对象从不排队的虚拟队列相关联。
        Cleaner cl = (sun.misc.Cleaner) r;
        cl.clean();
        //更新 queueNext 以指示引用已入队，但现在已从队列中删除。
        r.queueNext = sQueueNextUnenqueued;
        return true;
    }
    if (tail == null) {// 队列为空，头节点和尾结点都指向r
        head = r; 
    } else {
        tail.queueNext = r; //追加到尾结点之后
    }
    tail = r; //尾结点后移
    tail.queueNext = r; //queueNext 指向自己，是为了标识已经入队了
    return true;
}
  
  
  //轮询此队列以查看参考对象是否可用。如果一个可用而没有进一步的延迟，则将其从队列中删除并返回。否则此方法立即返回null。
  public Reference<? extends T> poll() {
      synchronized (lock) {
          if (head == null)
              return null;
          return reallyPollLocked();
      }
  }
  //取出头节点的引用对象并返回
  private Reference<? extends T> reallyPollLocked() {
    if (head != null) {
        Reference<? extends T> r = head;
        if (head == tail) {
            tail = null;
            head = null;
        } else {
            head = head.queueNext;
        }
        // queueNext 字段不为空表示引用已入队，但现在已从队列中删除。
        r.queueNext = sQueueNextUnenqueued;
        return r;
    }
    return null;
 }
  
  //移除此队列中的下一个引用对象，阻塞直到其中一个变为可用或给定的超时期限到期。
  //此方法不提供实时保证：它通过调用Object.wait(long)方法来安排超时
  public Reference<? extends T> remove(long timeout)
    throws IllegalArgumentException, InterruptedException{
    if (timeout < 0) {
        throw new IllegalArgumentException("Negative timeout value");
    }
    synchronized (lock) {
        Reference<? extends T> r = reallyPollLocked(); //取出头节点，可能为空
        if (r != null) return r; //不为空，直接返回
        long start = (timeout == 0) ? 0 : System.nanoTime();
        for (;;) {
            lock.wait(timeout); //阻塞等待倒计时,引用入队时会 notifyAll
            r = reallyPollLocked(); //再次获取头节点的引用
            if (r != null) return r;//不为空，直接返回
            if (timeout != 0) {
                long end = System.nanoTime();
                timeout -= (end - start) / 1000_000;
                if (timeout <= 0) return null;
                start = end;
            }
        }
    }
}
}
```

#### Reference.java

[Reference Android Api Doc](https://developer.android.google.cn/reference/java/lang/ref/Reference?hl=en)

引用对象的抽象基类。此类定义了所有引用对象的通用操作。由于引用对象是与垃圾收集器密切配合实现的，因此该类可能无法直接实例化。

```java
public abstract class Reference<T> {
  //GC 特殊处理。 ART 的 ClassLinker::LinkFields() 知道这是按字母顺序排列的最后一个非静态字段。
  volatile T referent; //引用的具体对象
  //此字段形成已入队的参考对象的单向链接列表。当且仅当此引用已入队时，queueNext 字段为非空。
  Reference queueNext; //链表节点，链接队列
  //构造时支持将 referent 和 queue 一起传入 例如：new SoftReference<User>(new User("soft-" + i), RQ);
	Reference(T referent, ReferenceQueue<? super T> queue) {
    this.referent = referent;
    this.queue = queue;
	}
  // 将引用添加到引用队列，一般由 VM 调用
  public boolean enqueue() {
   return queue != null && queue.enqueue(this);
	}
  //获取 具体对象
  @FastNative
	private final native T getReferent();
  
  //确保给定引用所引用的对象保持 强可达，无论程序之前的任何操作可能导致对象变得不可达；因此，至少在调用此方法之后，垃圾收集器无法回收引用的对象。调用此方法本身不会启动垃圾收集或终结。
  public static void reachabilityFence(Object ref) {
    // 变为强可达
    SinkHolder.sink = ref;
    // 置空
    if (SinkHolder.finalize_count == 0) {
        SinkHolder.sink = null;
    }
}

private static class SinkHolder {
    static volatile Object sink;
    // Ensure that sink looks live to even a reasonably clever compiler.
    private static volatile int finalize_count = 0;

    private static Object sinkUser = new Object() {
        protected void finalize() {
            if (sink == null && finalize_count > 0) {
                throw new AssertionError("Can't get here");
            }
            finalize_count++;
        }
    };
}
 
}
```



#### SoftReference.java

软引用对象，由垃圾收集器根据内存需求自行决定清除。假设垃圾收集器在某个时间点确定一个对象是软可达。那时，它可以选择以原子方式清除对该对象的所有软引用以及对任何其他软可达对象的所有软引用，通过强引用链可以从中访问该对象。同时或稍后它会将那些注册到引用队列的新清除的软引用加入队列。 在虚拟机抛出 OutOfMemoryError之前，保证所有对软可达对象的软引用都已被清除。

否则，对清除软引用的时间或清除对不同对象的一组此类引用的顺序没有限制。然而，鼓励虚拟机实现偏向于清除最近创建或最近使用的软引用。 

避免缓存的软引用

实际上，软引用对于缓存是低效的。运行时没有关于要清除和保留哪些引用的足够信息。最致命的是，当在清除软引用和增加堆之间做出选择时，它不知道该怎么做。 缺乏有关每个引用对您的应用程序的价值的信息限制了软引用的有用性。过早清除的引用会导致不必要的工作；那些清除太晚的浪费内存。 大多数应用程序应该使用 LruCache 而不是软引用。 LruCache 有一个有效的驱逐策略，让用户调整分配的内存量。

```java
public class SoftReference<T> extends Reference<T> {
	static private long clock; //时间戳时钟，由垃圾收集器更新
  //每次调用 get 方法都会更新时间戳。 VM 可以在选择要清除的软引用时使用此字段，但这不是必需的
  private long timestamp;
  //构造方法，引用对象和引用队列
  public SoftReference(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
    this.timestamp = clock;
  }
  //返回此引用对象的引用对象。 如果此引用对象已被程序或垃圾收集器清除，则此方法返回null 
  public T get() {
    T o = super.get();
    if (o != null && this.timestamp != clock)
        this.timestamp = clock;
    return o;
	}  
  //父类中的get 最终会调到这个 JNI 方法
 @FastNative
 private final native T getReferent();
}
```

#### WeakReference.java

弱引用对象，不会阻止它们的引用对象被终结、终结和回收。弱引用最常用于实现规范化映射。

假设垃圾收集器在某个时间点确定某个对象是弱可达的。那时它将原子地清除对该对象的所有弱引用以及对任何其他弱可达对象的所有弱引用，通过强引用和软引用链可以从中访问该对象。同时它将声明所有以前弱可达的对象是可终结的。同时或稍后它会将那些新清除的已注册到引用队列的弱引用加入队列。

```java
public class WeakReference<T> extends Reference<T> {
 //只有两个构造方法，其它实现都在父类
  public WeakReference(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
  }
}
```

#### PhantomReference.java

Phantom 引用对象，在收集器确定它们的引用对象可能会被回收后排队。 幻像引用最常用于以比 Java 终结机制更灵活的方式调度预检清理操作。
如果垃圾收集器在某个时间点确定幻像引用的所指对象是幻像可达的，那么在那个时候或稍后的某个时间它将将该引用加入队列。
为了确保可回收对象保持如此，可能无法检索幻像引用的所指对象：幻像引用的get方法始终返回null 。
与软引用和弱引用不同，幻像引用在入队时不会被垃圾收集器自动清除。 可通过幻像引用访问的对象将保持不变，直到所有此类引用都被清除或自身变得无法访问为止。

```java
public class PhantomReference<T> extends Reference<T> {
  //get 方法永远返回 null
  public T get() {
    return null;
	}
  //只有一个两参数的构造方法，引用队列必须传
  public PhantomReference(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
  }
}
```

Java 源码中还有一类引用：FinalReference

[特殊的引用-FinalReference](https://zhuanlan.zhihu.com/p/108020737)

