# Jetpack | Lifecycle 详解

## Lifecycle问题汇总

- 什么是 Lifecycle
- 如何使用 Lifecycle 观察宿主状态
- Lifecycle 是如何分发宿状态的
- Fragment 是如何实现 Lifecycle 的
- Activity 是如何实现 Lifecycle 的
- Application 是如何实现 Lifecycle 的
- Service 是如何实现 Lifecycle 的
- View 是如何实现观察宿主 Lifecycle 的
- Lifecycle 涉及的依赖库是如何划分的
-  Lifecycle 实现观察宿主状态有几种方式
-  注解+反射/生成代码的方式为什么又被废弃了
- Activity 的生命周期分发为何通过 ReportFragment 实现
- Lifecycle Event 和 State 的关系
- 在 onResume 方法中注册观察者，是否能观察到其它生命周期的回调
- 分发宿状态过程中是如何同步 Event 和 State 的



## Lifecycle是什么

生命周期感知型组件 (Lifecycle-Aware Components) 可执行操作来响应另一个组件（如 Activity 和 Fragment）的生命周期状态的变化。从而帮助开发者写出简介以维护高效的代码。

一种常见的场景是有些逻辑需要依赖在 Activity 和 Fragment 的生命周期方法中实现，通过 Lifecycle  组件就可以将这部分代码从生命周期方法中提取到单独的类中，从而使整个流程更清晰，更易维护。

## Lifecycle 有什么

androidx.lifecycle 组下的组件,了解 lifecycle  有什么才能更好的运用。

| lifecycle相关                                                | livedata 相关                                                | viewmodel相关                                                | 其它扩展组件相关                                             |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| lifecycle-common <br />lifecycle-common-java8 <br />lifecycle-compiler<br />lifecycle-runtime <br />lifecycle-runtime-ktx <br />lifecycle-runtime-ktx- lint <br />lifecycle-runtime-testing | lifecycle-livedata <br />lifecycle-livedata-core <br />lifecycle-livedata-core-ktx<br />lifecycle-livedata-core-ktx-lint <br />lifecycle-livedata-core-truth  <br />lifecycle-livedata-ktx<br />lifecycle-reactivestreams <br />lifecycle-reactivestreams-ktx | lifecycle-viewmodel <br />lifecycle-viewmodel-compose <br />lifecycle-viewmodel-ktx <br />lifecycle-viewmodel-savedstate | lifecycle-process <br />lifecycle-service <br />lifecycle-extensions |
| common-java8 已经废弃                                        | -                                                            | -                                                            | extensions 耦合重已经废弃                                    |

再来看一下其中的几个核心组件之间的依赖关系

![WechatIMG77](/Users/xuejiewang/Desktop/WechatIMG77.jpeg)

开发时按需添加 Lifecycle 的依赖项

```groovy
def lifecycle_version = "2.4.0"
// ViewModel
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
// ViewModel utilities for Compose
implementation "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
// LiveData
implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
// Lifecycles only (without ViewModel or LiveData)
implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
// Saved state module for ViewModel
implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"
// Annotation processor
kapt "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
// alternately - if using Java8, use the following instead of lifecycle-compiler
implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
// optional - helpers for implementing LifecycleOwner in a Service
implementation "androidx.lifecycle:lifecycle-service:$lifecycle_version"
// optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
implementation "androidx.lifecycle:lifecycle-process:$lifecycle_version"
// optional - ReactiveStreams support for LiveData
implementation "androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version"
```

## Lifecycle 怎么用

Lifecycle 的以下使用方式是以  Activity 或者 Fragment 为宿主举例。

- 方式一：运行时注解+反射
  - 自定义 LifecycleObserver 观察者，用 OnLifecycleEvent 注解配合 Lifecycle.Event 枚举标注生命周期方法；
  - 在宿主 Activity 或者 Fragment 中通过 getLifecycle().addObserver() 方法注册定义的观察者；
- 方式二：编译时注解+生成辅助类(XXX_LifecycleAdapter)
  - 添加注解处理器组件：lifecycle-compiler
  - 自定义 LifecycleObserver 观察者，用 OnLifecycleEvent 注解配合 Lifecycle.Event 枚举 标注生命周期方法；
  - 在宿主 Activity 或者 Fragment 中通过 getLifecycle().addObserver() 方法注册定义的观察者；
- 方式三：实现 FullLifecycleObserver
  - 自定义 FullLifecycleObserver 观察者，FullLifecycleObserver 是普通接口需要实现其中定义的所有生命周期方法；
  - 在宿主 Activity 或者 Fragment 中通过 getLifecycle().addObserver() 方法注册定义的观察者；
- 方式四：实现 LifecycleEventObserver
  - 自定义 LifecycleEventObserver 观察者，通过实现 onStateChanged(LifecycleOwner ,Lifecycle.Event) 方法自行判断生命周期方法的回调；
  - 在宿主 Activity 或者 Fragment 中通过 getLifecycle().addObserver() 方法注册定义的观察者；
- **方式五：实现 DefaultLifecycleObserver (推荐方式)**
  - 自定义 DefaultLifecycleObserver 观察者，DefaultLifecycleObserver 中通过java default 关键字都实现了方法体，所以只需实现需要的声明后期方法即可；
  - 在宿主 Activity 或者 Fragment 中通过 getLifecycle().addObserver() 方法注册定义的观察者；



### 观察 Activity Lifecycle

```kotlin
class MyLifecycleActivityObserver : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("MyLifecycleActivity", "onStart")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("MyLifecycleActivity", "onStop")
    }
}
```

```kotlin
class MyLifecycleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        addLifecycleObserver()
    }
    private fun addLifecycleObserver() {
        lifecycle.addObserver(MyLifecycleActivityObserver())
    }
}
```

### 观察 Fragment Lifecycle

```kotlin
class MyLifecycleFragmentObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectListener() {
        Log.i("MyLifecycleFragment", "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {
        Log.i("MyLifecycleFragment", "onPause")

    }
}
```

```kotlin
class MyLifecycleFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addLifecycleObserver()
    }

    private fun addLifecycleObserver() {
        lifecycle.addObserver(MyLifecycleFragmentObserver())
    }
  
}
```

### 观察 Service Lifecycle

```kotlin
public class MyLifecycleServiceObserver implements LifecycleEventObserver {
    
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_START) {
            Log.d("MyLifecycleService", "onStart()");
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            Log.d("MyLifecycleService", "onDestroy()");
        }
    }
}
```

```kotlin
public class MyLifecycleService extends LifecycleService {
    public MyLifecycleService() {
        getLifecycle().addObserver(new MyLifecycleServiceObserver());
    }
}
```

### 观察 Application Lifecycle

```kotlin
class MyLifecycleApplicationObserver(private val application: Application) :
    LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun appInResumeState() {
        Toast.makeText(application, "In Foreground", Toast.LENGTH_LONG).show()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun appInPauseState() {
        Toast.makeText(application, "In Background", Toast.LENGTH_LONG).show()
    }
}
```

```kotlin
public class MyLifecycleApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //饿汉式单例获取 ProcessLifecycleOwner
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new MyLifecycleApplicationObserver(this));
    }
}
```



### 各种方式的执行顺序

- DefaultLifecycleObserver 所有方法将在 [LifecycleOwner] 的生命周期回调方法被调用之前被调用，这里需要注意Fragment 生命周期的回调时机。

* LifecycleEventObserver onStateChanged 方法在当状态转换事件发生时调用。
*  如果一个类同时实现了DefaultLifecycleObserver 和LifecycleEventObserver ，则首先调用DefaultLifecycleObserver方法，然后调用LifecycleEventObserver.onStateChanged(LifecycleOwner, Lifecycle.Event) 方法。
*  如果一个类实现了这个接口并且同时使用了OnLifecycleEvent 注解，那么注解将被忽略。

```kotlin
D/Life_Owner: onCreate
D/Life_Observer: onCreate
D/Life_Observer: onStateChanged,event:ON_CREATE
D/Life_Owner: onStart
D/Life_Observer: onStart
D/Life_Observer: onStateChanged,event:ON_START
D/Life_Owner: onResume
D/Life_Observer: onResume
D/Life_Observer: onStateChanged,event:ON_RESUME
D/Life_Observer: onPause
D/Life_Observer: onStateChanged,event:ON_PAUSE
D/Life_Owner: onPause
D/Life_Observer: onStop
D/Life_Observer: onStateChanged,event:ON_STOP
D/Life_Owner: onStop
D/Life_Observer: onDestroy
D/Life_Observer: onStateChanged,event:ON_DESTROY
D/Life_Owner: onDestroy
```

在Activity 的onPause 方法注册观察者，当宿主执行onPause时 观察者也是会从 onCreate 开始直到对齐当前状态，Lifecycle 内部做了同步和对齐的处理。

```
D/Life_Owner: onCreate
D/Life_Owner: onStart
D/Life_Owner: onResume
D/Life_Observer: onCreate
D/Life_Observer: onStateChanged,event:ON_CREATE
D/Life_Observer: onStart
D/Life_Observer: onStateChanged,event:ON_START
D/Life_Owner: onPause
D/Life_Observer: onStop
D/Life_Observer: onStateChanged,event:ON_STOP
D/Life_Owner: onStop
D/Life_Observer: onDestroy
D/Life_Observer: onStateChanged,event:ON_DESTROY
D/Life_Owner: onDestroy
```



## Lifecycle 实现原理

### 观察者模式

Lifecycle 的设计原理是观察者模式



```java
//androidx.core.app.ComponentActivity，@hide标注，不对外使用，只做了 Lifecycle 和 KeyEvent 的封装
public class ComponentActivity extends Activity implements
        LifecycleOwner,
        KeyEventDispatcher.Component {...}
```

```java
//androidx.activity.ComponentActivity，以上特性 + 集成了Jitpack 的其它组件，例如：Lifecycle，ViewModel等
public class ComponentActivity extends androidx.core.app.ComponentActivity implements
        ContextAware,
        LifecycleOwner,
        ViewModelStoreOwner,
        HasDefaultViewModelProviderFactory,
        SavedStateRegistryOwner,
        OnBackPressedDispatcherOwner,
        ActivityResultRegistryOwner,
        ActivityResultCaller {...}
```

```java
//androidx.fragment.app.FragmentActivity，以上特性 + 简化Fragment 的使用，例如：FragmentManager
public class FragmentActivity extends ComponentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        ActivityCompat.RequestPermissionsRequestCodeValidator {...}
```

```java
//androidx.appcompat.app.AppCompatActivity 以上特性 + 简化 Material 设计，例如主题、暗黑、导航条等
public class AppCompatActivity extends FragmentActivity implements 
  			AppCompatCallback,
        TaskStackBuilder.SupportParentable, 
				ActionBarDrawerToggle.DelegateProvider {...}
```



activity 组件下的 **ComponentActivity** 可以说是 androidx 系开发中的顶层Activity基类了，

















![image-20211031113640319](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211031113648.png)









`Lifecycle` 是一个类，用于存储有关组件（如 Activity 或 Fragment）的生命周期状态的信息，并允许其他对象观察此状态。

`Lifecycle` 提供了两种枚举跟踪其关联组件的生命周期状态：

- 事件：从框架和 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle) 类分派的生命周期事件。这些事件映射到 Activity 和 Fragment 中的回调事件。

- 状态：由 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle) 对象跟踪的组件的当前状态。



![生命周期状态示意图](https://developer.android.com/images/topic/libraries/architecture/lifecycle-states.svg)

状态是图中的节点，将事件看作这些节点之间的边。

![image-20211027012432687](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211027012437.png)







![img](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20211027015118.jpeg)



## DefaultLifecycleObserver



> default 关键字
>
> default 关键字修饰的方法能够向接口添加新功能方法，必须提供方法体，并确保兼容实现这个接口的之前的类不用在接口的子类中进行逐个实现该方法。可以按需实现
>
> default是在需要给接口新增方法时，但是子类数量过多，或者子类没必要实现的场景下使用。 比如java8中的List接口，新增了sort()方法
>
> ```java
> //@since 1.8
> public interface List<E> extends Collection<E> {
>   ...
>   default void sort(Comparator<? super E> c) {
>       Object[] a = this.toArray();
>       Arrays.sort(a, (Comparator) c);
>       ListIterator<E> i = this.listIterator();
>       for (Object e : a) {
>           i.next();
>           i.set((E) e);
>       }
>   }
> }
> ```



用于监听LifecycleOwner状态变化的回调接口。
如果您使用 Java 8 语言，请始终更喜欢它而不是注释。
如果一个类同时实现了这个接口和 LifecycleEventObserver ，则首先调用DefaultLifecycleObserver方法，然后调用LifecycleEventObserver.onStateChanged(LifecycleOwner, Lifecycle.Event)
如果一个类实现了这个接口并且同时使用了OnLifecycleEvent ，那么注解将被忽略

```java
lifecycle-common-java8
public interface DefaultLifecycleObserver extends FullLifecycleObserver {}
```



## 

`LifecycleOwner` 是单一方法接口，表示类具有可观察的生命周期。它只有一个  `getLifecycle()` 方法。

```java
public interface LifecycleOwner {
    @NonNull
    Lifecycle getLifecycle();
}
```

支持库 26.1.0 及更高版本中的 Fragment 和 Activity 已实现 [`LifecycleOwner`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner) 接口。

## ProcessLifecycleOwner

管理整个应用进程的生命周期



## LifecycleObserver

将类标记为 LifecycleObserver。 它没有任何方法，而是依赖于OnLifecycleEvent注释方法

实现 `LifecycleObserver` 的组件可与实现 `LifecycleOwner` 的组件完美配合，因为所有者可以提供生命周期，而观察者可以注册以观察生命周期。









## 生命周期感知型组件的用例

生命周期感知型组件可使您在各种情况下更轻松地管理生命周期。下面列举几个例子：

- 在粗粒度和细粒度位置更新之间切换。使用生命周期感知型组件可在位置应用可见时启用细粒度位置更新，并在应用位于后台时切换到粗粒度更新。
- 停止和开始视频缓冲。使用生命周期感知型组件可尽快开始视频缓冲，但会推迟播放，直到应用完全启动。此外，应用销毁后，您还可以使用生命周期感知型组件终止缓冲。
- 开始和停止网络连接。借助生命周期感知型组件，可在应用位于前台时启用网络数据的实时更新（流式传输），并在应用进入后台时自动暂停。
- 暂停和恢复动画可绘制资源。借助生命周期感知型组件，可在应用位于后台时暂停动画可绘制资源，并在应用位于前台后恢复可绘制资源。
- Handler 的消息移除
- Presenter 的 attach&detach View 



1. `Lifecycle` 库通过在 `SupportActivity` 的 `onCreate` 中注入 `ReportFragment` 来感知发生命周期；
2. `Lifecycle` 抽象类，是 `Lifecycle` 库的核心类之一，它是对生命周期的抽象，定义了生命周期事件以及状态，通过它我们可以获取当前的生命周期状态，同时它也奠定了观察者模式的基调；（我是党员你看出来了吗:-D）
3. `LifecycleOwner` ，描述了一个拥有生命周期的组件，可以自己定义，不过通常我们不需要，直接使用 `AppCompatActivity` 等即可；
4. `LifecycleRegistry` 是 `Lifecycle` 的实现类，它负责接管生命周期事件，同时也负责 `Observer` 的注册以及通知；
5. `ObserverWithState` ，是 Observer 的一个封装类，是它最终 通过 `ReflectiveGenericLifecycleObserve` 调用了我们用注解修饰的方法；
6. `LifecycleObserver` ，Lifecycle 的观察者，利用它我们可以享受 Lifecycle 带来的能力；
7. `ReflectiveGenericLifecycleObserver`，它存储了我们在 Observer 里注解的方法，并在生命周期发生改变的时候最终通过反射的方式调用对应的方法。





## 链接

- [Github | Lifecycle](https://github.com/androidx/androidx/tree/androidx-main/lifecycle)

- [Android 开发者 | Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)

- [用户指南](https://developer.android.com/topic/libraries/architecture/lifecycle) 

- [代码示例](https://github.com/android/architecture-components-samples) 

- [Codelab](https://codelabs.developers.google.com/codelabs/android-lifecycles/index.html?index=#0)
- [Lifecycle 版本说明](https://developer.android.com/jetpack/androidx/releases/lifecycle#declaring_dependencies)
- [googlecodelabs | android-lifecycles](https://github.com/googlecodelabs/android-lifecycles)
- [What is lifecycle observer and how to use it correctly?](https://stackoverflow.com/questions/52369540/what-is-lifecycle-observer-and-how-to-use-it-correctly)

