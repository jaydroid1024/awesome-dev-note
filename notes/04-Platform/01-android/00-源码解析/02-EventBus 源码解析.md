# EventBus 事件总线框架

## 概述

> 框架的作用有：隐藏实现细节，降低开发难度，做到代码复用，解耦业务与非业务代码，让程序员聚焦业务开发。

EventBus 翻译为“事件总线”，它提供了实现观察者模式的骨架代码。我们可以基于此框架，非常容易地在自己的业务场景中实现**观察者模式**，从而减少样板代码。其中，[Google Guava EventBus](https://github.com/google/guava/wiki/EventBusExplained)  就是一个比较著名的 EventBus 框架，它不仅仅支持异步非阻塞模式，同时也支持同步阻塞模式。

而我们今天要分析的 [**GreenRobot EventBus**](https://github.com/greenrobot/EventBus) 是适用于 Android 和 Java 平台的事件总线框架，它可简化Activities, Fragments, Threads, Services之间的通信且轻量，它的核心设计理念是对观察者模式（Observer Design Pattern）也被称为发布订阅模式（Publish-Subscribe Design Pattern）的封装。传统的事件传递方式包括：Handler、BroadcastReceiver、Interface回调等，相比之下EventBus的优点是代码简洁，使用简单，并将事件发布和订阅充分解耦。

> **观察者模式 **
>
> 定义：定义了对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。一般情况下，被依赖的对象叫作被观察者（Observable），依赖的对象叫作观察者（Observer）。
>
> 作用：解耦观察者和被观察者。
>
> 实现：
>
> - **同步阻塞**是最经典的实现方式，主要是为了代码解耦；观察者和被观察者代码在同一个线程内执行，被观察者发送更新通知后就一直阻塞着，直到所有的观察者代码都执行完成之后，才会执行后续的代码。
>
> - **异步非阻塞**除了能实现代码解耦之外，还能提高代码的执行效率；实现方式是被观察者发送更新通知后启动一个新的线程来执行观察者的回调函数。
>

**EventBus 观察者模式框架 VS 自己实现观察模式**

利用 EventBus 框架实现的观察者模式，跟从零开始编写的观察者模式相比，从大的流程上来说，实现思路大致一样，都需要定义观察者（Observer），并且通过 register() 函数注册Observer，也都需要通过调用某个函数（比如，EventBus 中的 post() 函数）来给 Observer 发送消息（在 EventBus 中消息被称作事件 event）。

但在实现细节方面，它们又有些区别。基于 EventBus，我们不需要定义 Observer 接口，任意类型的对象都可以注册到 EventBus 中，通过 @Subscribe 注解来标明类中哪个函数可以接收被观察者发送的消息。

跟经典的观察者模式的不同之处在于，当我们调用 post() 函数发送消息的时候，并非把消息发送给所有的观察者，而是发送给可匹配的观察者。所谓可匹配指的是，能接收的消息类型是发送消息（post 函数定义中的 event）类型或是其父类。



## 工作机制

![EventBus-Android-Publish-Subscribe](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211107112804.png)

**发布者(Publisher)**：发布者主动生成事件发布事件给指定订阅者。

```java
EventBus.getDefault().post(new MessageEvent());
```

**事件总线(EventBus)**：统筹所有事件的调度工作，如：收集、注册、切换、分发、解注册等操作。

```java
EventBus.getDefault().register(this);
EventBus.getDefault().unregister(this);
```

**订阅者(Subscriber)**：声明订阅方法并通过注释标记，可指定线程模式。

```java
@Subscribe(threadMode = ThreadMode.MAIN)  
public void onMessageEvent(MessageEvent event) {/* Do something */};
```

## EventBus 优势

- 简化组件之间的通信，同进程内随便发，组件化中需要考虑 Event Object 的存放位置。
- 解耦事件的发送者和接收者，仅通过 Event Object 进行链接发送者和接受者。
- 在 UI 组件和后台线程切换中表现良好的性能
- 避免复杂且容易出错的依赖关系和生命周期问题，提供解注册订阅者。
- 很快；专为高性能而优化，很小（~60k jar）
- [在实践中被证明通过应用与1,000,000,000+安装](http://www.appbrain.com/stats/libraries/details/eventbus/greenrobot-eventbus)
- 具有分发指定线程、设置订阅者优先级等功能。

##  EventBus 功能

- **简单而强大：** EventBus 是一个小型库，其 API 非常容易学习。然而，通过解耦组件，您的软件架构可能会受益匪浅：订阅者在使用事件时并不了解发送者。
- 实战**测试：** EventBus 是最常用的 Android 库之一：数以千计的应用程序使用 EventBus，包括非常流行的应用程序。超过 10 亿次应用安装不言而喻。
- **高性能：**尤其是在 Android 上，性能很重要。EventBus 进行了大量分析和优化；可能使其成为同类中最快的解决方案。
- **方便的基于注释的 API** （不牺牲性能）**：**只需将 @Subscribe 注释放在您的订阅者方法中。由于注释的构建时索引，EventBus 不需要在您的应用程序运行时进行注释反射，这在 Android 上非常慢。
- **Android 主线程传递：**在与 UI 交互时，EventBus 可以在主线程中传递事件，而不管事件是如何发布的。
- **后台线程传递：**如果您的订阅者执行长时间运行的任务，EventBus 还可以使用后台线程来避免 UI 阻塞。
- **事件和订阅者继承：**在 EventBus 中，面向对象的范式适用于事件和订阅者类。假设事件类 A 是 B 的超类。发布的 B 类事件也将发布给对 A 感兴趣的订阅者。类似地考虑订阅者类的继承。
- **零配置：** 您可以立即开始使用代码中任何位置可用的默认 EventBus 实例。
- **可配置：** 要根据您的要求调整 EventBus，您可以使用构建器模式调整其行为。

## EventBus 应用场景

- 如果使用 EventBus 的页面比较多，可以在 Acitivity/Fragment  基类里面绑定和解绑，并添加一个默认接收事件。
- 跨界面修改值
  - 你有一个主界面，里面有一些信息可能会修改，但触发源不在该界面，是在其他的界面触发了一些事件后，首页的内容需要做修改。
  - 如果没有EventBus，也有很多的方式可以实现，譬如定义全局静态变量、或者onResume时获取触发源的值修改界面值、或者定义个CallBack接口传出去等。
  - 譬如微信首页你有未读消息3个时，界面会有3个小红点点，当你点开一个未读消息后，进入了下个界面，那么此时未读消息就是2了，但你并不在首页了，你需要在你打开消息并阅读完毕后通知首页改成2.这就是一种跨界面修改值。
- Activity/Fragment 与 Fragment 之间通信
- 注册页面回退逻辑
  - 在注册页面填写了手机号、个人信息，传头像操作后，注册成功了，进入了主界面。此时我们需要在主界面关闭之前的注册的所有页面，此时就可以使用eventbus来通知前几个注册用的activity来关闭自己。这样的目的就是当注册失败时，用户按返回键还是能回到填写信息页。当注册成功后，按返回键就直接退出程序，不再保留注册填信息页了。
- 推送/消息功能
  - 收到推送后需要不同的页面来做处理的。例如：微信PC登录时，手机端的确认登录页面是可以随时随地弹出的，
- 组件化通讯
  - 组件之间的交互，例如：测试环境中环境切换组件，切换后需要重新登录并重置环境信息等。
- EventBus最好的使用方式就是替代某些 BroadcastReceiver 和 Interface；如fragment之间进行通信，用广播和接口都比较麻烦，而用EventBus则比较简单。
- 以下场景可以考虑不用
  - Event 会根据传递的参数给所有接收者都传递消息，这就导致如果你想给指定一个类里发布消息就得自己写一个接口类，要不然就会好多执行者都会执行该方法，所以一般能用Intent组件传值时还是用Intent。
  - EventBus相对于BroadcastReceiver，广播是相对消耗时间、空间最多的一种方式，但是大家都知道，广播是四大组件之一，许多系统级的事件都是通过广播来通知的，比如说网络的变化、电量的变化，短信发送和接收的状态，所以，如果与android系统进行相关的通知，还是要选择本地广播；在BroadcastReceiver的 onReceive方法中，可以获得Context 、intent参数，这两个参数可以调用许多的sdk中的方法，而eventbus获得这两个参数相对比较困难。
  - EventBus相对于handler，可以实现handler的方式，但是也会面对有许多接收者的问题，所以如果是线程回调的话，我觉得还是用handler比较好。



## EventBus 使用

### 订阅者索引

使用订阅者索引可以避免在运行时使用反射对订阅者方法进行昂贵的查找。EventBus 注释处理器在编译时查找它们。

#### 符合注解收集的要求

- @Subscribe 方法及其类**必须是 public**。
- 事件类**必须是 public**。
- @Subscribe可以**不**被使用**匿名类的内部**。
- 当 EventBus 不能使用索引时，例如不满足上述要求，它会在运行时降级为通过反射查找订阅者。这确保@Subscribe 方法接收事件，即使它们不是索引的一部分。

#### 配置注解处理器

```groovy
//java
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ eventBusIndex : 'com.example.myapp.MyEventBusIndex' ]
            }
        }
    }
}
 
dependencies {
    def eventbus_version = '3.2.0'
    implementation "org.greenrobot:eventbus:$eventbus_version"
    annotationProcessor "org.greenrobot:eventbus-annotation-processor:$eventbus_version"
}

//kotlin 
apply plugin: 'kotlin-kapt' // ensure kapt plugin is applied

dependencies {
    def eventbus_version = '3.2.0'
    implementation "org.greenrobot:eventbus:$eventbus_version"
    kapt "org.greenrobot:eventbus-annotation-processor:$eventbus_version"
}

kapt {
    arguments {
        arg('eventBusIndex', 'com.example.myapp.MyEventBusIndex')
    }
}
```

#### 使用订阅者索引类

在您的*Application*类中，使用*EventBus.builder().addIndex(indexInstance)*将索引类的实例传递给 EventBus。组件中的索引类也可以通过addIndex方法添加到 EventBus 实例中。

```kotlin
//创建一个新实例并配置索引类
val eventBus = EventBus.builder().addIndex(MyEventBusIndex()).build()
//使用单例模式并配置索引类
EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
// Now the default instance uses the given index. Use it like this:
val eventBus = EventBus.getDefault()
```

#### 防止混淆订阅者

```java
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
 
# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
```



### 配置EventBus

EventBusBuilder 类可以配置 EventBus 的各个方面。例如

使用 EventBus.getDefault() 是一种从应用程序中的任何位置获取共享 EventBus 实例的简单方法。EventBusBuilder 还允许使用installDefaultEventBus ( )方法配置此默认实例。可以在 Application 类中在使用 EventBus 之前配置默认 EventBus 实例。

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EventBus.builder()
            //将此与 BuildConfig.DEBUG 一起使用可让应用程序尽在在 DEBUG 模式下崩溃。默认为false
            // 这样就不会在开发过程中错过异常（Invoking subscriber failed）
            .throwSubscriberException(false)
            //如果发送了没有订阅者的event,是否需要打印提示哪一个 event bean 的log,默认为true
            //提示信息： No subscribers registered for event class org.greenrobot.eventbusperf.jay.bus.SubEvent
            .logNoSubscriberMessages(true)
            .installDefaultEventBus()
    }
}
```



### ThreadMode

在 EventBus 中，您可以使用四种 ThreadMode 来指定订阅者方法所在的线程。

- [1 ThreadMode: POSTING](https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/#ThreadMode_POSTING) ：发布者和订阅者在同一个线程。
  - 这是默认设置。事件传递是同步完成的，需要注意避免阻塞主线程。
  - 避免了线程切换意味着开销较小。
- [2 ThreadMode: MAIN](https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/#ThreadMode_MAIN) ：订阅者将在 Android 的主线程（UI 线程）中调用。
  - 事件传递是同步完成的，需要注意避免阻塞主线程。
- [3 ThreadMode: MAIN_ORDERED](https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/#ThreadMode_MAIN_ORDERED) ：订阅者将在 Android 的主线程中被调用，该事件总是通过Handler排队等待稍后传递给订阅者。
  - 为事件处理提供了更严格和更一致的顺序。
  - 如果前一个也是main_ordered 需要等前一个执行完成后才执行。
  - 事件传递是异步完成的。
- [4 ThreadMode: BACKGROUND](https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/#ThreadMode_BACKGROUND) ：如果发帖线程非主线程则订阅者的处理会在工作线程中执行否则和发布者同一个线程处理。
  - 事件传递是异步完成的。
- [5 ThreadMode: ASYNC](https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/#ThreadMode_ASYNC) ：无论事件在哪个线程发布，订阅者都会在新建的工作线程中执行。
  - EventBus 使用线程池来有效地重用线程。
  - 事件传递是异步完成的。
  - 如果事件处理程序方法的执行可能需要一些时间，则应使用此模式，例如用于网络访问

```kotlin
 //在主线程发消息
 发布者所在线程:Thread-2, 订阅者所在线程: Thread-2, 订阅者线程模式: BACKGROUND 
 发布者所在线程:Thread-2, 订阅者所在线程: pool-1-thread-1, 订阅者线程模式: ASYNC 
 发布者所在线程:Thread-2, 订阅者所在线程: Thread-2, 订阅者线程模式: POSTING 
 发布者所在线程:Thread-2, 订阅者所在线程: main, 订阅者线程模式: MAIN 
 发布者所在线程:Thread-2, 订阅者所在线程: main, 订阅者线程模式: MAIN_ORDERED 
 //在子线程发消息
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: MAIN 
 发布者所在线程:main, 订阅者所在线程: pool-1-thread-2, 订阅者线程模式: BACKGROUND 
 发布者所在线程:main, 订阅者所在线程: pool-1-thread-1, 订阅者线程模式: ASYNC 
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: POSTING 
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: MAIN_ORDERED 
```

### 订阅者优先级

订阅者优先级影响事件传递的顺序。 在同一个交付线程 ( ThreadMode ) 中，较高优先级的订阅者将在其他具有较低优先级的订阅者之前收到事件。 默认优先级为 0。注意：优先级不影响具有不同ThreadMode的订阅者之间的传递顺序！

```kotlin
@Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
fun onMessageEvent_POSTING1(event: MessageEvent) {
    showMsg(event, "POSTING1")
}

@Subscribe(threadMode = ThreadMode.POSTING, priority = 4)
fun onMessageEvent_POSTING2(event: MessageEvent) {
    showMsg(event, "POSTING2")
}

@Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
fun onMessageEvent_MAIN1(event: MessageEvent) {
    showMsg(event, "MAIN1")
}

@Subscribe(threadMode = ThreadMode.MAIN, priority = 3)
fun onMessageEvent_MAIN2(event: MessageEvent) {
    showMsg(event, "MAIN2")
}

 //打印结果
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: POSTING2 
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: MAIN2 
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: POSTING1 
 发布者所在线程:main, 订阅者所在线程: main, 订阅者线程模式: MAIN1 
```

**取消事件传递**

您可以通过从订阅者的事件处理方法调用cancelEventDelivery ( Object event ) 来取消事件传递过程。任何进一步的事件传递都将被取消，后续订阅者将不会收到该事件。

```java
// Prevent delivery to other subscribers*
EventBus.getDefault().cancelEventDelivery(event) ;
```

事件通常由更高优先级的订阅者取消。取消仅限于在发布线程 ( ThreadMode . PostThread ) 中运行的事件处理方法。

### 粘性事件 

普通事件都是需要先注册(register)，再post才能接受到事件；如果你使用 postSticky 发送事件，那么可以不需要先注册，也能接受到事件，也就是一个延迟注册的过程。 

普通的事件我们通过post发送给EventBus，发送过后之后当前已经订阅过的方法可以收到。但是如果有些事件需要所有订阅了该事件的方法都能执行呢？例如一个Activity，要求它管理的所有Fragment都能执行某一个事件，但是当前我只初始化了3个Fragment，如果这时候通过post发送了事件，那么当前的3个Fragment当然能收到。但是这个时候又初始化了2个Fragment，那么我必须重新发送事件，这两个Fragment才能执行到订阅方法。 

粘性事件就是为了解决这个问题，通过 postSticky 发送粘性事件，这个事件不会只被消费一次就消失，而是一直存在系统中，直到被 removeStickyEvent 删除掉。那么只要订阅了该粘性事件的所有方法，只要被register 的时候就会被检测到并且执行。订阅的方法需要添加 sticky = true 属性。

```kotlin
EventBus.getDefault().postSticky(MessageEvent(Thread.currentThread().name))

//消费粘性事件方式一：
val stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent::class.java)
// 最好检查之前是否实际发布过事件
if (stickyEvent != null) {
    // 消费掉粘性事件
    EventBus.getDefault().removeStickyEvent(stickyEvent)
}
//消费粘性事件方式二：
val stickyEvent2 = EventBus.getDefault().removeStickyEvent(MessageEvent::class.java)
// 最好检查之前是否实际发布过事件
if (stickyEvent2 != null) {
    //已经消费了
}

@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
fun onMessageEvent_sticky(event: MessageEvent) {
    showMsg(event, "MAIN")
    //消费粘性事件方式三：
    EventBus.getDefault().removeStickyEvent(event)
}
```

### 异步执行器

AsyncExecutor 就像一个线程池，但具有失败（异常）处理功能。失败会引发异常，AsyncExecutor 会将这些异常包装在一个事件中，该事件会自动发布。

 *AsyncExecutor 是一个非核心实用程序类。它可能会为您节省一些在后台线程中进行错误处理的代码，但它不是核心 EventBus 类。*

调用 AsyncExecutor.create() 来创建一个实例并将其保存在应用程序范围内。然后要执行某些操作，请实现 RunnableEx接口并将其传递给AsyncExecutor的execute方法。与Runnable不同，RunnableEx可能抛出异常。

```kotlin
//AsyncExecutor类似于线程池，但具有失败(异常)处理。失败是抛出异常，AsyncExecutor将把这些异常封装在一个事件中，该事件将自动发布。
AsyncExecutor.create().execute {
    EventBus.getDefault().postSticky(SubEvent<String>())
}

//线程池中发出的时间
@Subscribe(threadMode = ThreadMode.MAIN)
fun handleLoginEvent(event: SubEvent<String>) {
    // do something
}

//线程池中任务异常时发出的时间
@Subscribe(threadMode = ThreadMode.MAIN)
fun handleFailureEvent(event: ThrowableFailureEvent) {
    // do something
}
```

## EventBus 实现原理

### 通过 APT 生成索引类

```java
@SupportedAnnotationTypes("org.greenrobot.eventbus.Subscribe")
@SupportedOptions(value = {"eventBusIndex", "verbose"})
@IncrementalAnnotationProcessor(AGGREGATING)
public class EventBusAnnotationProcessor extends AbstractProcessor {
  ...
}
```

- 通过处理器参数获取配置的订阅者索引全类名，没有配置抛异常

```java
String index = processingEnv.getOptions().get(OPTION_EVENT_BUS_INDEX);
if (index == null) {
    messager.printMessage(Diagnostic.Kind.ERROR, "No option " + OPTION_EVENT_BUS_INDEX +
            " passed to annotation processor");
    return false;
}
```

- 收集注解

```java
private void collectSubscribers(Set<? extends TypeElement> annotations, RoundEnvironment env, Messager messager{
    for (TypeElement annotation : annotations) {
        //获取目标注解标注的所有元素，这里是所有的订阅者方法
        Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
            //ExecutableElement 可执行元素指的是方法类型
            if (element instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) element;
                //检查方法:正好只有一个参数的非静态的公开的方法
                if (checkHasNoErrors(method, messager)) {
                    //获取方法所在的类元素
                    TypeElement classElement = (TypeElement) method.getEnclosingElement();
                    //存入容器
                    methodsByClass.putElement(classElement, method);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@Subscribe is only valid for methods", element);
            }
        }
    }
}
```

- 校验某个类类对索引类包是否可访问

```java
private boolean isVisible(String myPackage, TypeElement typeElement) {
    //类的修饰符
    Set<Modifier> modifiers = typeElement.getModifiers();
    boolean visible;
    if (modifiers.contains(Modifier.PUBLIC)) {
        visible = true;
    } else if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.PROTECTED)) {
        visible = false;
    } else {
        //类所在的包
        String subscriberPackage = getPackageElement(typeElement).getQualifiedName().toString();
        //处理器参数没有指定索引类
        if (myPackage == null) {
            //todo 没有包名什么情况
            visible = subscriberPackage.length() == 0;
        } else {
            //索引类和观察者包名一样
            visible = myPackage.equals(subscriberPackage);
        }
    }
    return visible;
}
```

- 校验收集到的注解元素信息是否符合预期

```java
private void checkForSubscribersToSkip(Messager messager, String myPackage) {
    //遍历所有订阅者方法所在的类
    for (TypeElement skipCandidate : methodsByClass.keySet()) {
        //方法所在的类，
        TypeElement subscriberClass = skipCandidate; //循环获取父类
        while (subscriberClass != null) {//所有观察者
            //校验某个类类对索引类包是否可访问
            if (!isVisible(myPackage, subscriberClass)) {
                //索引类访问不到观察者类，跳过
                boolean added = classesToSkip.add(skipCandidate);
                if (added) {//存在不可访问观察者
                    String msg;
                    //由于类不是公开的，所以回退到反射
                    if (subscriberClass.equals(skipCandidate)) { //没有继承关系存在
                        msg = "Falling back to reflection because class is not public";
                    } else { //父类
                        msg = "Falling back to reflection because " + skipCandidate +
                                " has a non-public super class";
                    }
                    messager.printMessage(Diagnostic.Kind.NOTE, msg, subscriberClass);
                }
                break;
            }
            //观察者类中的所有观察方法
            List<ExecutableElement> methods = methodsByClass.get(subscriberClass);
            if (methods != null) {
                for (ExecutableElement method : methods) {
                    String skipReason = null;
                    //方法第一个参数
                    VariableElement param = method.getParameters().get(0);
                    //参数类型
                    TypeMirror typeMirror = getParamTypeMirror(param, messager);
                    //不是类类型报错
                    if (!(typeMirror instanceof DeclaredType) || !(((DeclaredType) typeMirror).asElement() instanceof TypeElement)) {
                        skipReason = "event type cannot be processed";
                    }
                    //是类类型但是对索引类不可见
                    if (skipReason == null) {
                        TypeElement eventTypeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                        //参数类对索引类不可见
                        if (!isVisible(myPackage, eventTypeElement)) {
                            skipReason = "event type is not public";
                        }
                    }
                    //存在观察者方法但是不可见先存下来，用于过滤
                    if (skipReason != null) {
                        boolean added = classesToSkip.add(skipCandidate);
                        if (added) {
                            String msg = "Falling back to reflection because " + skipReason;
                            if (!subscriberClass.equals(skipCandidate)) {
                                msg += " (found in super class for " + skipCandidate + ")";
                            }
                            messager.printMessage(Diagnostic.Kind.NOTE, msg, param);
                        }
                        break;
                    }
                }
            }
            //获取观察者类的父类，继续循环
            subscriberClass = getSuperclass(subscriberClass);
        }
    }
}
```

- 写入索引信息

```java
private void writeIndexLines(BufferedWriter writer, String myPackage) throws IOException {
    for (TypeElement subscriberTypeElement : methodsByClass.keySet()) {
        //只生成可访问的
        if (classesToSkip.contains(subscriberTypeElement)) {
            continue;
        }
        String subscriberClass = getClassString(subscriberTypeElement, myPackage);
        if (isVisible(myPackage, subscriberTypeElement)) {
            writeLine(writer, 2,
                    "putIndex(new SimpleSubscriberInfo(" + subscriberClass + ".class,",
                    "true,", "new SubscriberMethodInfo[] {");
            List<ExecutableElement> methods = methodsByClass.get(subscriberTypeElement);
            writeCreateSubscriberMethods(writer, methods, "new SubscriberMethodInfo", myPackage);
            writer.write("        }));\n\n");
        } else {
            writer.write("        // Subscriber not visible to index: " + subscriberClass + "\n");
        }
    }
}
```

- 生成的 MyEventBusIndex 文件

```java
//通过注释处理创建的生成索引类的接口。
public interface SubscriberInfo {

    Class<?> getSubscriberClass();

    SubscriberMethod[] getSubscriberMethods();

    SubscriberInfo getSuperSubscriberInfo();

    boolean shouldCheckSuperclass();
}

public class MyEventBusIndex implements SubscriberInfoIndex {
    private static final Map<Class<?>, SubscriberInfo> SUBSCRIBER_INDEX;

    static {
        SUBSCRIBER_INDEX = new HashMap<Class<?>, SubscriberInfo>();
        putIndex(new SimpleSubscriberInfo(org.greenrobot.eventbusperf.testsubject.PerfTestEventBus.SubscriberClassEventBusAsync.class,true, new SubscriberMethodInfo[] {
            new SubscriberMethodInfo("onEventAsync", TestEvent.class, ThreadMode.ASYNC),
        }));
      。。。
      }

private static void putIndex(SubscriberInfo info) {
    SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);
}
  
@Override
public SubscriberInfo getSubscriberInfo(Class<?> subscriberClass) {
    SubscriberInfo info = SUBSCRIBER_INDEX.get(subscriberClass);
    if (info != null) {
        return info;
    } else {
        return null;
    }
}
      
```

### 运行时部分

添加索引类

```kotlin
EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
```

**初始化 SubscriberMethodFinder**

```java
subscriberMethodFinder = new SubscriberMethodFinder(
        //添加由 EventBus 的注释预处理器生成的索引。默认空集合
        builder.subscriberInfoIndexes,
        //启用严格的方法验证（默认值：false）
        builder.strictMethodVerification, 
        //即使有生成的索引也强制使用反射（默认值：false）
        builder.ignoreGeneratedIndex);
```

#### 注册订阅者类，查找订阅者方法

```java
public override fun onStart() {
    super.onStart()
    EventBus.getDefault().register(this)
}
//注册给定的订阅者以接收事件。 订阅者一旦对接收事件不再感兴趣，就必须调用 unregister(Object) 。
//订阅者类中至少需要一个订阅者方法且必须由Subscribe注释标注。
//Subscribe注解具有ThreadMode、优先级、粘性事件等参数配置
public void register(Object subscriber) {
    Class<?> subscriberClass = subscriber.getClass();
    //通过订阅者类找出该类中所有的订阅者方法
    List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
    synchronized (this) {
        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            subscribe(subscriber, subscriberMethod);
        }
    }
}
```

通过APT或反射方式查找订阅者方法并内存缓存

```java
//ConcurrentHashMap 内存缓存保证线程安全
private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

//通过订阅者类查找订阅者方法
List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
    //先从内存缓存尝试取，节省查找开销
    List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
    if (subscriberMethods != null) {
        return subscriberMethods;
    }
    if (ignoreGeneratedIndex) {
        //运行时反射查找
        subscriberMethods = findUsingReflection(subscriberClass);
    } else {
        //从APT中收集的备选中查找
        subscriberMethods = findUsingInfo(subscriberClass);
    }
    if (subscriberMethods.isEmpty()) {
        //订阅者类中至少有一个订阅者方法，否则运行时报错
        throw new EventBusException("Subscriber " + subscriberClass
                + " and its super classes have no public methods with the @Subscribe annotation");
    } else {
        //找到后缓存到内存
        METHOD_CACHE.put(subscriberClass, subscriberMethods);
        return subscriberMethods;
    }
}
```

##### 运行时反射查找



### 注销订阅者

```java
public override fun onStop() {
    super.onStop()
    EventBus.getDefault().unregister(this)
}
```

## 参考

**[Github | EventBus](https://github.com/greenrobot/EventBus)**

[EventBus Documentation](https://greenrobot.org/eventbus/documentation/)

[极客时间| 设计模式之美](https://time.geekbang.org/column/intro/250?code=Grxvvkczx9tydhzn0RhJfNfwaF2RgJA9qeUWd8orIYo%3D)

[EventBus 如何使用及一些常见场景](https://cloud.tencent.com/developer/article/1383971)

[EventBus使用总结和使用场景](https://blog.csdn.net/f552126367/article/details/86571012)

