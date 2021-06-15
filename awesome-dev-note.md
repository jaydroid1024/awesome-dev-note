# 资深架构师成长路线



## 编程基础

组成原理

编译原理

操作系统

计算机网络

## 编程思维

算法

数据结构

重构

面向对象

编程规范

数学

## 编程语言

Java

Kotlin



## 业务方向

Mobile



## 工具

Git

IDE

UML

Mac

Gradle

## 软技能

沟通

效率

求知

自控





































## Java

### Java 泛型

- 泛型的作用与定义
- 通配符与嵌套
- 泛型上下边界
- RxJava中泛型的使用分析

### Java 注解

- 自定义注解

	- 自定义注解与元注解
	- 注解参数与默认值
- 注解的使用

	- APT，编译时注解处理器
	- 插桩，编译后处理筛选
	- 反射，运行时动态获取注解信息
- Retrofit中的注解

### Java 反射

- 反射基本概念与Class

  - 三种获取Class对象的方式
  - 获取构造器实例化对象与属性信息
  - 包信息和方法
  - Hook技术动态编程

### Java 并发编程

- 线程共享和协作

	- CPU核心数，线程数，时间片轮转机制解读
	- synchronized、Lock、volatile、ThreadLocal如何实现线程共享
	- Wait,Notify/NotifyAll，Join方法如何实现线程间协作

- 站在巨人肩上操作CAS

	- CAS的原理
	- CAS带来的ABA问题

		- 原子操作类的正确使用实战

- 仅会用线程池是不够的

	- Callbale、Future和FutureTask源码解读
	- 线程池底层实现分析
	- 线程池排队机制
	- 手写线程池实战
	- Executor框架解读实战

- Android AsyncTask原理解析

### Java 数据传输与序列化

- Serializable原理
- Parcelable接口原理解析
- Json

### Java 虚拟机原理

- 垃圾回收器机制

	- 对象存活及强、弱等各种引用辨析
	- 快速解读GC算法之标记-清除、复制及标记-整理算法
	- 正确姿势解读GC日志

- 内存分配策略

	- JVM栈桢及方法调用详解
	- JMM，Java Memory Model

- Dalvik虚拟机

### Java 类加载

- ClassLoader类加载器

	- 动态代理模式
	- Android Davilk与ART
	- PathClassLoader、DexClassLoader与BootClassLoader
	- 双亲委托机制

### Java IO

- Java IO 体系

	- 装饰者模式
	- InputStream与OutputStream
	- Reader与Writer

- File文件操作

	- FileChannel
	- 内存映射

- IO操作Dex加密

### Java 集合



### 实战项目：QZone超级布丁



## Kotlin





## Android框架体系架构

### 高级UI晋升

- 触摸事件分发机制
- View渲染机制

	- onLayout与onMeasure
	- onDraw映射机制

- 常用View

	- RecycleView

		- 源码解析
		- 布局管理器LayoutManager
		- 条目装饰ItemDecoration
		- ViewHolder与回收复用机制

	- CardView

		- 源码解析
		- 圆角阴影实现原理
		- 5.0以下阴影与边距的适配

	- ViewPager

		- 加载机制与优化
		- 与Fragment的结合

	- WebView

		- 使用与原理
		- js与Java交互
		- 多进程WebView使用实战
		- WebView和Native的通信框架手写实战

- 布局ViewGroup

	- ConstraintLayout
	- LinearLayout
	- RelativeLayout
	- FrameLayout
	- GridLayout

- 自定义View实战

	- Canvas与Paint高级使用
	- 自定义属性与动画
	- 自定义瀑布流实战
	- 自定义流式布局
	- 手机清屏动画
	- 组合自定义View实战
	- 继承自定义View实战
	- 完全自定义view实战

### Android组件内核

- Activity与调用栈

	- 四大启动模式与Intent Flag
	- APK启动流程与ActivityThread解析
	- Activity生命周期源码解析
	- 实战Splash广告载入与延时跳转

- Fragment的管理与内核

	- Fragment事务管理机制
	- Fragment转场动画
	- 嵌套处理，ChildFragmentManager

- Service 内核原理

	- start与bind区别与原理
	- 自带工作线程的IntentService
	- 前台服务与Notify

- 组件间通信方案

	- Activity和Fragment低耦通信设计
	- Android与Serivice通信
	- Intent数据传输与限制
	- ViewModel通信方案
	- 事件总线EventBus源码解析
	- 实战：自动感知生命周期事件总线LiveDataBus

### 大型项目必备IPC

- Binder机制原理

	- AIDL配置文件
	- C/S架构Binder原理
	- Messager
	- 实战告别繁琐的AIDL，进程通信框架原理与实现

- 其他IPC方式

	- Broadcast
	- ContentProvider
	- 文件
	- Socket
	- 共享内存与管道

### 数据持久化

- Android文件系统

	- sdcard与内部存储

- 轻量级kv持久化

	- Shared Preference原理
	- 微信MMKV原理与实现

		- MMAP内存映射
		- 文件数据结构
		- 增量更新与全量更新

- 嵌入式Sqlite数据库

	- SqliteOpenHelper
	- Sqlite升级与数据迁移方案
	- 实战注解ORM数据库框架

### Framework内核解析

- XMS内核管理

	- AMS 

		- Activity管理

			- apk启动原理
			- activity运行机制
			- activity内核管理方案详细讲解

		- 实战插件化核心启动未安装Activity

	- WMS 

		- Windows体系
		- 悬浮窗工具实现

	- PackageMS面试锦囊
	- 实战插件化框架原理与实现

- Handler消息机制

	- Looper
	- Message链表与对象池
	- MessageQueue消息队列与epoll机制

- 事件分发机制

	- View事件分发原理
	- viewGroup事件分发体系
	- 事件冲突解决方案实战

- 布局加载与资源系统

	- LayoutManager加载布局流程
	- Resource与AssetManager
	- 实战海量网易云焕肤系统，加载外部APK资源

### 实战项目：腾讯新闻客户端



## Android 性能调优

### 设计思想与代码质量优化

- 六大原则

	- 单一职责原则
	- 开闭原则
	- 里氏替换原则
	- 依赖倒置原则
	- 接口隔离原则
	- 迪米特法则

- 设计模式

	- 结构型模式

		- 桥接模式
		- 适配器模式
		- 装饰器模式
		- 代理模式
		- 组合模式

	- 创建型模式

		- 建造者模式
		- 单例模式
		- 抽象工厂模式
		- 工厂方法模式
		- 静态工厂模式

	- 行为型模式

		- 模板方法模式
		- 策略模式
		- 观察者模式
		- 责任链模式
		- 命令模式
		- 访问者模式 

	- 实战设计模式解耦项目网络层框架

- 数据结构

	- 线性表ArrayList
	- 链表LinkedList
	- 栈Stack
	- 队列

		- Queue
		- Deque
		- 阻塞队列

	- Tree

		- 平衡二叉树
		- 红黑树

	- 映射表

		- HashTable
		- HashMap
		- SparseArray
		- ArrayMap

- 算法

	- 排序算法

		- 冒泡排序
		- 选择排序
		- 插入排序
		- 快速排序
		- 堆排序
		- 基数排序

	- 查找算法

		- 折半查找
		- 二分查找
		- 树形查找
		- hash查找

### 程序性能优化

- OOM问题原理解析

	- adj内存管理机制
	- JVM内存回收机制与GC算法解析
	- 生命周期相关问题总结
	- Bitmap压缩 方案总结

- ANR问题解析

	- AMS系统时间调节原理
	- 程序等待原理分析
	- ANR问题解决方案

- 启动速度与执行效率优化

	- 冷暖热启动耗时检测与分析
	- 启动黑白屏解决
	- 卡顿分析
	- StickMode严苛模式
	- Systrace与TraceView工具

- 布局检测与优化

	- 布局层级优化
	- 过度渲染检测
	- Hierarchy Viewer与Layout Inspactor工具

- 内存优化

	- 内存抖动和内存泄漏
	- 内存大户，Bitmap内存优化
	- Profile内存监测工具
	- Mat大对象与泄漏检测

- 耗电优化

	- Doze&Standby
	- Battery Historian
	- JobScheduler、WorkManager

- 网络传输与数据存储优化

	- google序列化工具protobuf
	- 7z极限压缩
	- 使用webp图片

- APK大小优化

	- APK瘦身
	- 微信资源混淆原理

- 屏幕适配

	- 屏幕适配方案总结
	- hook技术实现屏幕完全适配

### 开发效率优化

- 分布式版本控制系统Git
- 自动化构建系统Gradle

	- Gradle与Android插件
	- Transform API
	- 自定义插件开发
	- 插件实战

		- 多渠道打包
		- 发版自动钉钉

### 实战项目：全方位评测与解析腾讯新闻客户端性能

### 设计思想解读开源框架

- 热修复设计

	- AOT/JIT、dexopt 与 dex2oat 
	- CLASS_ISPREVERIFIED问题与解决
	- 即时生效与重启生效热修复原理
	- Gradle自动补丁包生成
	- 面试问题指导

- 插件化框架解读

	- Class文件加载Dex原理
	- Android资源加载与管理
	- 四大组件的加载与管理
	- so库的加载原理
	- Android系统服务的运行原理
	- 插件化面试技巧

- 组件化框架设计

	- 组件化之集中式路由--阿里巴巴ARouter原理
	- APT技术自动生成代码与动态类加载
	- Java SPI机制实现组件服务调用 
	- 拦截器AOP编程(跳转前预处理--登录)，路由参数传递与IOC注入
	- 手写组件化式路由
	- 组件化面试总结

- 图片加载框架

	- 图片加载框架选型

		- Universal ImangeLoader、Glide、Picasso与Fresco
		- Glide
		- Picasso
		- Fresco

	- Glide原理分析

		- Fragment感知生命周期原理
		- 自动图片大小计算
		- 图片解码
		- 优先级请求队列
		- ModelLoader与Registry机制
		- 内存缓存原理

			- LRU内存缓存
			- 引用计数与弱引用活跃缓存
			- Bitmap复用池
			- 缓存大小配置

		- 磁盘文件缓存

			- 原始图像文件缓存
			- 解码图像文件缓存

	- 手写图片加载框架实战

- 网络访问框架设计

	- 网络通信必备基础

		- Restful URL
		- HTTP协议& TCP/IP协议
		- SSL握手与加密
		- DNS解析
		- Socket通信原则

			- SOCKS代理
			- HTTP普通代理与隧道代理

	- OkHttp源码解读

		- Socket连接池复用机制
		- HTTP协议重定向与缓存处理
		- 高并发请求队列：任务分发
		- 责任链模式拦截器设计

	- Retrofit源码解析

- RXJava响应式编程框架设计

	- 链式调用
	- 扩展的观察者模式
	- 事件变换设计
	- Scheduler线程控制
	- RxJava系统方案总结
	- Rxjava面试技巧

- IOC架构设计

	- 依赖注入与控制反转
	- ButterKnife原理
	- Dagger架构设计核心解密

- Android架构组件Jetpack

	- LiveData原理
	- Navigation如何解决tabLayout问题
	- ViewModel如何感知View生命周期及内核原理
	- Room架构方式方法
	- dataBinding为什么能够支持MVVM
	- WorkManager内核揭秘
	- Lifecycles生命周期

- 每周比别人多花6个小时，知道别人不知道的底层技术

## NDK模块开发

### NDK基础知识体系

- C与C++

	- 数据类型
	- 内存结构与管理
	- 预处理指令、Typedef别名
	- 结构体与共用体
	- 指针、智能指针、方法指针
	- 线程
	- 类

		- 函数、虚函数、纯虚函数与析构函数
		- 初始化列表

- JNI开发

	- 静态与动态注册
	- 方法签名、与Java通信
	- 本地引用与全局引用

- Native开发工具

	- 编译器、打包工具与分析器
	- 静态库与动态库
	- CPU架构与注意事项
	- 构建脚本与构建工具

		- Cmake
		- Makefile

	- 交叉编译移植

		- FFmpeg交叉编译
		- X264、FAAC交叉编译
		- 解决所有移植问题

	- AS构建NDK项目

- Linux编程

	- Linux环境搭建，系统管理，权限系统和工具使用（vim等）
	- Shell脚本编程

### 底层图片处理

- PNG/JPEG/WEBP图像处理与压缩
- 微信图片压缩
- GIF合成原理与实现

### 音视频开发

- 多媒体系统

	- Camera与手机屏幕采集
	- 图像原始数据格式YUV420(NV21与YV12等)
	- 音频采集与播放系统
	- 编解码器MediaCodec
	- MediaMuxer复用与MediaExtractor

- FFmpeg

	- ffmpeg模块介绍
	- 音视频解码，音视频同步
	- I帧，B帧，P帧解码原理
	- x264视频编码与faac音频编码
	- OpenGL绘制与NativeWindow绘制

- 流媒体协议

	- RTMP协议
	- 音视频通话P2P WebRtc

- 音视频效果处理

	- OpenGL ES美颜大眼
	- 抖音视频效果分析与实现
	- 音视频变速原理

- 项目实战一：直播app(用户端与主播端)
- 实战项目二：抖音视频app

### 机器学习

-  Opencv

	- 图像预处理

		- 灰度化、二值化
		- 腐蚀与膨胀

	- 人脸检测
	- 身份证识别
	- 车牌号识别

		- SVM分类器检测车牌
		- 人工神经网络识别车牌

### 智能家居串口编程实战

## 架构师炼成实战

### 架构设计

- MVP、MVP与MVVM
- 模块化与组件化架构

### 网上商城项目实战

### 新闻客户端项目实战

### 多格式播放器项目实战

### Gradle自动化项目实战

## 微信小程序

### 小程序介绍

- 背景与趋势
- 小程序技术方案
- 公众平台注册及配置
- 开发工具的使用
- MINA框架架构剖析
- 应用程序配置详解
- 逻辑与界面分离架构
- 单向数据流

### UI开发

- 复杂的页面布局
- 文字图片等内容的呈现
- 用户交互表单开发
- 对话框等交互元素开发
- 下拉刷新和上拉加载
- 图形与动画操作
- 页面之间的跳转过渡
- 用户界面事件处理

### API操作

- 背景与趋势
- 多媒体操作
- 网络通信
- 本地存储及文件操作
- 地理位置信息
- 设备信息获取
- 系统功能（扫码、拨打电话等）
- 界面交互操作

### 微信对接

- 微信登陆
- 用户信息获取
- 微信支付
- 微信客服消息
- 微信开放数据
- 小程序更新
- 第三方平台接入

### 小程序项目实战

- 任务清单项目

	- 基础项目
	- 演练小程序框架的基本使用
	- 基本技术：应用配置、界面数据绑定、界面布局、采集用户输入、用户操作事件处理、日志记录

- 电影榜单项目

	- HTTPS
	- 第三方接口调用
	- 列表数据绑定及显示
	- 页面跳转传值
	- 上拉加载更多
	- 下拉刷新
	- 分享到微信群或者朋友圈等等

- 本地生活项目

	- 网络访问
	- 生命周期
	- 地理围栏
	- Promise的运用
	- weui样式库
	- Node.js接口部署
	- HTTPS证书操作等等

- 购物商场项目

	- 首页推荐频道展示
	- 分类筛选
	- 搜索商品
	- 分页加载数据及长列表展示优化
	- 购物车
	- 下单
	- 支付
	- 用户个人中心
	- Postman接口测试工具

## Hybrid 开发与Flutter

### Html5项目实战

- HTML&CSS&JavaScript 实战
- WordPress搭建网站项目实战

### Flutter

- 你好，Flutter

	- 原生开发与跨平台技术
	- 初识Flutter
	- Flutter开发环境搭建

- Flutter 编码语言Dart详解系列

	- 一切皆对象,Dart面向对象的原理解析
	- Dart中变量，函数，操作符，异常等语法与java原理对比
	- 类的机制
	- 初始化列表规则
	- 命名构造方法
	- 常量构造方式
	- 工厂构造特征
	- Mixin

- Flutter框架原理与使用技巧

	- widget控件详解：text,image,button
	- 布局分析：Linear布局，弹性布局，流水布局
	- 如何自定义View
	- 动画/手势交互
	- 多线程开发原理
	- 网络请求原理
	- Flutter架构与原生代码的交互
	- 实战发布自己的Flutter库

- Flutter架构知识落地实现

	- 干货集中营 gank app项目实战
	- WanAndroid API构建客户端项目实战

*XMind: ZEN - Trial Version*