# Flutter

## 第一章 Flutter 概述

### 1.1 特性

- **秒级的热重载：**Flutter的热重载可帮助您快速地进行测试、构建UI、添加功能并更快地修复错误。在iOS和Android模拟器或真机上可以在亚秒(亚秒级：没有达到秒的速度，这里应该是大于一秒)内重载，并且不会丢失状态。
- **富有表现力和灵活的UI**：使用Flutter内置美丽的Material Design和Cupertino（iOS风格）widget、丰富的motion API、平滑而自然的滑动效果和平台感知，为您的用户带来全新体验。
- **响应式框架：** Widget描述了他们的视图在给定其当前配置和状态时应该看起来像什么。当widget的状态发生变化时，widget会重新构建UI，Flutter会对比前后变化的不同， 以确定底层渲染树从一个状态转换到下一个状态所需的最小更改（译者语：类似于React/Vue中虚拟DOM的diff算法）。
- **原生级绘制性能：**许多Widget都可以在iOS和Android上达到原生应用一样的性能。
- **灵活的原生交互与混合开发：**使用Flutter作为视图(View)层，复用现有的Java、Swift或ObjC代码，访问iOS和Android上的原生系统功能和系统SDK。



### 1.2 学习策略

- 宜
  - 首先熟悉，上图6大类的widget的作用和应用场景，对widget有个印象;
  - 用widget练习画界面，先实现简单的页面，然后在再到复杂的布局;
  - 实现界面的时候如遇到一些复杂的操作，可以针对性的通过Google查询具体的解决方案;
  - 在实践的过程中多做总结，很多widget的功能都是可以用其他widget来替代的;

- 忌
  - 不宜将170+个widgets从头到尾一-个个记一 遍，除非你有特别好的记忆力和时间;
  - 只学习widget的用法却不动手实践，纸上得来终觉浅，绝知此事要躬行;
  - 不宜死记硬背，每个widget都有它特定的应用场景，以及复杂的页面都是由各种各样widget组合而来，这些需要在实践过程中慢慢积累经验;



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

## 第二章 Flutter 框架和原理详解

## 第三章 Flutter 混合架构

3.1 

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

