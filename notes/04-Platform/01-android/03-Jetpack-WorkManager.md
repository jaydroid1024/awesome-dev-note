# Jetpack | WorkManager

后台任务类别

- 即时任务：任务是否需要在用户与应用进行互动时完成
- 精确任务：需要在精确的时间点运行任务
- 延期任务：无需在精确时间点运行的任务应归类为延期任务

![](https://developer.android.google.cn/images/guide/background/task-category-tree.png)

提供了任务分类标准，

执行任务所应使用的 API

一般来说，运行时间超过几毫秒的所有任务都应委派给台线程。长时间运行的常见任务包括解码位图、访问存储空间、处理机器学习 (ML) 模型、执行网络请求等。



### 用途

使用 WorkManager 实现可靠的工作，WorkManager 适用于需要**可靠运行**的工作，即使用户导航离开屏幕、退出应用或重启设备也不影响工作的执行。例如：

- 向后端服务发送日志或分析数据
- 定期将应用数据与服务器同步

WorkManager 不适用于那些可在应用进程结束时安全终止的进程内后台工作，也不适用于需要立即执行的工作。请查看[后台处理指南](https://developer.android.google.cn/guide/background)，了解哪种解决方案符合您的需求。

### 底层

在后台，WorkManager 根据以下条件使用底层作业来调度服务：

![](https://developer.android.google.cn/images/topic/libraries/architecture/workmanager/overview-criteria.png)



### 功能

除了具备更为简单且一致的 API 之外，WorkManager 还具备许多其他关键优势，其中包括：

- **约束条件**：仅在设备采用 Wi-Fi 网络连接时、当设备处于空闲状态或者有足够的存储空间时运行。
- **灵活调度**：
  - 支持运行[一次性](https://developer.android.google.cn/reference/androidx/work/OneTimeWorkRequest)或[重复](https://developer.android.google.cn/reference/androidx/work/PeriodicWorkRequest)工作。
  - 对工作进行标记或命名，以便调度唯一的、可替换的工作以及监控或取消工作组。
  - 已调度的工作存储在内部托管的 SQLite 数据库中，由 WorkManager 负责确保该工作持续进行，并在设备重新启动后重新调度。
  - WorkManager 遵循[低电耗模式](https://developer.android.google.cn/training/monitoring-device-state/doze-standby)等省电功能和最佳做法。
- **重试机制**：有时工作会失败。WorkManager 提供了[灵活的重试政策](https://developer.android.google.cn/topic/libraries/architecture/workmanager/how-to/define-work#retries_backoff)，包括可配置的[指数退避政策](https://developer.android.google.cn/reference/androidx/work/BackoffPolicy)。
- **串行并行**：对于复杂的相关工作，您可以使用流畅自然的接口[将各个工作任务串联起来](https://developer.android.google.cn/topic/libraries/architecture/workmanager/how-to/chain-work)，这样您便可以控制哪些部分依序运行，哪些部分并行运行。WorkManager 会自动将输出数据从一个工作任务传递给下一个工作任务。
- **线程处理**：WorkManager [无缝集成](https://developer.android.google.cn/topic/libraries/architecture/workmanager/advanced/threading) [RxJava](https://developer.android.google.cn/topic/libraries/architecture/workmanager/advanced/rxworker) 和[协程](https://developer.android.google.cn/topic/libraries/architecture/workmanager/advanced/coroutineworker)，并可灵活地[插入您自己的异步 API](https://developer.android.google.cn/topic/libraries/architecture/workmanager/advanced/listenableworker)。





## 链接

- [**后台处理指南**](https://developer.android.google.cn/guide/background)

- [**后台执行限制**](https://developer.android.google.cn/about/versions/oreo/background)

- [**使用 WorkManager 处理需要立刻执行的后台任务**](https://juejin.cn/post/6946373301715337224)

- **[开发者指南 | 在 WorkManager 中进行线程处理](https://link.zhihu.com/?target=https%3A//developer.android.google.cn/topic/libraries/architecture/workmanager/)**
- **[参考指南 | androidx.work](https://link.zhihu.com/?target=https%3A//developer.android.google.cn/reference/androidx/work/package-summary)**
- **[发行日志 | WorkManager](https://link.zhihu.com/?target=https%3A//developer.android.google.cn/jetpack/androidx/releases/work)**
- **[Codelab | 使用 WorkManager 处理后台任务](https://link.zhihu.com/?target=https%3A//codelabs.developers.google.com/codelabs/android-workmanager)**
- **[WorkManager 的源码 (AOSP的一部分)](https://link.zhihu.com/?target=https%3A//android.googlesource.com/platform/frameworks/support/%2B/master/work)**
- **[演讲 | 使用 WorkManager (2018 Android 开发者峰会)](https://link.zhihu.com/?target=https%3A//www.youtube.com/watch%3Fv%3D83a4rYXsDs0)**
- **[Issue Tracker](https://link.zhihu.com/?target=https%3A//issuetracker.google.com/issues%3Fq%3Dcomponentid%3A409906)**
- **[Stack Overflow 的 android-workmanager标签](https://link.zhihu.com/?target=https%3A//stackoverflow.com/questions/tagged/android-workmanager)**
- **[Android 开发者博客上关于 Power 的文章系列](https://link.zhihu.com/?target=https%3A//android-developers.googleblog.com/search/label/Power%20series)**
- [Cactus](https://github.com/gyf-dev/Cactus) ：Android Keep Alive(安卓保活)，Cactus 集成双进程前台服务，JobScheduler，onePix(一像素)，WorkManager，无声音乐

