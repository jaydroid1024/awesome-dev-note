# Flutter

##  Flutter 概述

###  特性

- **秒级的热重载：**Flutter的热重载可帮助您快速地进行测试、构建UI、添加功能并更快地修复错误。在iOS和Android模拟器或真机上可以在亚秒(亚秒级：没有达到秒的速度，这里应该是大于一秒)内重载，并且不会丢失状态。
- **富有表现力和灵活的UI**：使用Flutter内置美丽的Material Design和Cupertino（iOS风格）widget、丰富的motion API、平滑而自然的滑动效果和平台感知，为您的用户带来全新体验。
- **响应式框架：** Widget描述了他们的视图在给定其当前配置和状态时应该看起来像什么。当widget的状态发生变化时，widget会重新构建UI，Flutter会对比前后变化的不同， 以确定底层渲染树从一个状态转换到下一个状态所需的最小更改（译者语：类似于React/Vue中虚拟DOM的diff算法）。
- **原生级绘制性能：**许多Widget都可以在iOS和Android上达到原生应用一样的性能。
- **灵活的原生交互与混合开发：**使用Flutter作为视图(View)层，复用现有的Java、Swift或ObjC代码，访问iOS和Android上的原生系统功能和系统SDK。

### 学习策略

- 宜
  - 首先熟悉，上图6大类的widget的作用和应用场景，对widget有个印象;
  - 用widget练习画界面，先实现简单的页面，然后在再到复杂的布局;
  - 实现界面的时候如遇到一些复杂的操作，可以针对性的通过Google查询具体的解决方案;
  - 在实践的过程中多做总结，很多widget的功能都是可以用其他widget来替代的;

- 忌
  - 不宜将170+个widgets从头到尾一-个个记一 遍，除非你有特别好的记忆力和时间;
  - 只学习widget的用法却不动手实践，纸上得来终觉浅，绝知此事要躬行;
  - 不宜死记硬背，每个widget都有它特定的应用场景，以及复杂的页面都是由各种各样widget组合而来，这些需要在实践过程中慢慢积累经验;

### 系统架构|Flutter System Architecture

- 框架层(Dart)
- 引擎层(C++)
- 平台层

### 开发语言|Dart 

- dart
- JIT&AOT
  - JIT()：即时编译,开发期间,更快编译,更快的重载
  - AOT()：事前编译, release期间，更快更流畅







### Android开发者如何快速上手Flutter布局

https://www.jianshu.com/p/447a48519227

- Linearl ayout在Flutter中等价于什么(Android)?
- RelativeL ayout在Flutter中等价于什么(Android)?
- 如何使用widget定义布局属性?
- 如何分层布局?
- 如何设置布局样式?
- ScrollView在Flutter中 等价于什么?
- 谁是Flutter的列表组件?
- 如何知道点击了列表中哪个item?
- 如何动态更新ListView?

## Flutter 框架和原理详解



## Flutter 混合架构



[官方文档](https://flutter.dev/docs/development/add-to-app/android/project-setup)

Flutter 可以作为源代码 Gradle 子项目或 AAR 嵌入到您现有的 Android 应用程序中。

- 创建Flutter module 的方式有两种方式

  - AS+Flutter plugin

  - 命令行

    - ```shell
      cd some/path/
      flutter create -t module --org com.jay flutter_hybrid_module
      ```

    - 这将创建一个`some/path/flutter_hybrid_module/`带有一些 Dart 代码的Flutter 模块项目和一个`.android/` 隐藏的子文件夹。

  - 将自定义 Android 代码添加到您自己现有应用程序的项目或插件中，而不是添加到`.android/`. 在您的模块`.android/` 目录中所做的更改不会出现在您使用该模块的现有 Android 项目中。

  - 不要对`.android/`目录进行源代码控制，因为它是自动生成的。在新机器上构建模块之前，先`flutter pub get` 在`my_flutter`目录中运行重新生成`.android/`目录，然后再使用Flutter模块构建Android项目。

  - 为避免 Dex 合并问题，`flutter.androidPackage`不应与您的宿主应用程序的包名称相同

  - Flutter Android 引擎使用 Java 8 特性。

- 添加 Flutter 模块作为依赖

  - AAR 机制创建通用的 Android AAR 作为中介来打包您的 Flutter 模块。

    - 将 Flutter 库打包为由 AAR 和 POM 工件组成的通用本地 Maven 存储库。

    - ```shell
      flutter build aar
      ```

    - AARs can only be built for plugin or module projects.

    - ```groovy
      xuejiewang@xuejiedeMacBook-Pro flutter_hybrid_module % flutter build aar
      
      Building without sound null safety
      For more information see https://dart.dev/null-safety/unsound-null-safety
      
      Running Gradle task 'assembleAarDebug'...                               
      Running Gradle task 'assembleAarDebug'... Done                     40.0s
      ✓ Built build/host/outputs/repo.
      Running Gradle task 'assembleAarProfile'...                             
      Running Gradle task 'assembleAarProfile'... Done                   63.4s
      ✓ Built build/host/outputs/repo.
      Running Gradle task 'assembleAarRelease'...                             
      Running Gradle task 'assembleAarRelease'... Done                   70.0s
      ✓ Built build/host/outputs/repo.
      
      Consuming the Module
        1. Open <host>/app/build.gradle
        2. Ensure you have the repositories configured, otherwise add them:
      
            String storageUrl = System.env.FLUTTER_STORAGE_BASE_URL ?: "https://storage.googleapis.com"
            repositories {
              maven {
                  url '/Users/xuejiewang/FlutterProjects/Jay/flutter-hybird/flutter_hybrid_module/build/host/outputs/repo'
              }
              maven {
                  url "$storageUrl/download.flutter.io"
              }
            }
      
        3. Make the host app depend on the Flutter module:
      
          dependencies {
            debugImplementation 'com.jay.flutter_hybrid_module:flutter_debug:1.0'
            profileImplementation 'com.jay.flutter_hybrid_module:flutter_profile:1.0'
            releaseImplementation 'com.jay.flutter_hybrid_module:flutter_release:1.0'
          }
      
      
        4. Add the `profile` build type:
      
          android {
            buildTypes {
              profile {
                initWith debug
              }
            }
          }
      
      ```

    - 更具体地说，此命令创建（默认情况下所有调试/配置文件/发布模式）一个 Maven 本地存储库，其中包含以下文件：

    - ![image-20210827145028293](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210827145028293.png)

    - 

  - 源码子项目机制是方便的一键构建过程，但是需要Flutter SDK。这是 Android Studio IDE 插件使用的机制。





## Flutter 面试题

### Flutter 框架和原理

跨平台开发框架、高保真、

框架层、引擎层、平台层

绘制流程

![image-20210829165445575](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210829165445575.png)

渲染流程

![image-20210829165357002](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210829165357002.png)

组件的生命周期

![image-20210829165325548](file:///Users/xuejiewang/Library/Application%20Support/typora-user-images/image-20210829165325548.png?lastModify=1630227229)





### 控件的状态：StatelessWidget & StatefulWidget

有状态的Widget

无状态的Widget



### Flutter 渲染机制三棵树

Widget tree-- createElement-  Element Tree ----Render Tree

![image-20210829170002916](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210829170002916.png)



三棵树的作用

为了性能，能用缓存的尽量用缓存、缓存 Render tree 



### Android 与 Flutter 混合开发的场景

- Android 列表页面-Flutter 详情页面
- Android 页面部分嵌套Flutter 视图
- Flutter 页面部分嵌套 Android 视图



### Android 与 Flutter 通信

![image-20210830163329230](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210830163329230.png)



![image-20210830162852067](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210830162852067.png)



Flutter定义了三种不同类型的Channel: 

- BasicMessageChannel: 用于传递字符串和半结构化的信息，持续通信，收到消息后可以回复此次消息，如: Native将遍历到的文件信息陆续传递到Dart,在比如: Flutter将 从服务端陆陆续获取到信息交给Native加工，Native处理完返回等;
- MethodChannel: 用于传递方法调用(method invocation) 一 次性通信:如Flutter调用Native拍照;
- EventChannel: 用于数据流(event streams)的通信，持续通信，通常用于Native向Dart的通信，如:手机电量变化，网络连接变化，陀螺仪，传感器等;
- 这三种类型的类型的Channel都是全双工通信，即A <=> B，Dart可以主 动发送消息给platform端，并且platform接收到消息后可以做出回应，同样，platform端 可以主动发送消息给Dart端，dart端 接收数后返回给platform端。



通信原理

![image-20210830163521796](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210830163521796.png)















### 其它

条件断点

Dart Devtools

Flutter Inspector 浏览 Widget tree 诊断

​     





## 参考

- [Flutter中文网](https://flutterchina.club/)
- [Dart中文网](http://www.dartdoc.cn/) 
- [Github | flutter](https://github.com/flutter/flutter)
- [Flutter插件库](https://pub.dev/): Flutter无法实现的一些功能，我们可以借助一些插件来实现
- [B站 | Flutter 布局（Layout）原理](https://space.bilibili.com/589533168?spm_id_from=333.788.b_765f7570696e666f.2) 
- Flutter 架构相关
  - [Flutter System Architecture](https://docs.google.com/presentation/d/1cw7A4HbvM_Abv320rVgPVGiUP2msVs7tfGbkgdrTy0I/edit#slide=id.g70d668005_2_22)
- 网络相关
  - **[Github | http：用于在 Dart 中发出 HTTP 请求的可组合 API](https://github.com/dart-lang/http)**
  - **[Github | dio：Dart 的强大 Http 客户端，支持拦截器、FormData、请求取消、文件下载、超时等](https://github.com/flutterchina/dio)**
- 布局相关
  - **[flutter_staggered_grid_view](https://github.com/letsar/flutter_staggered_grid_view)**
- Widget 整理开源项目
  - **[flutter-go](https://github.com/alibaba/flutter-go)**
  - **[FlutterUnit](https://github.com/toly1994328/FlutterUnit)**
- Flutter Android 混编相关
  - [**Github | Add-to-App Samples**](https://github.com/flutter/samples/tree/master/add_to_app) ：包含导入和使用 Flutter module 的 Android 和 iOS 项目。它们旨在展示将 Flutter 添加到现有 Android 和 iOS 应用程序的推荐方法。

