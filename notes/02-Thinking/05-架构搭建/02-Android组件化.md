# Android 组件化

## 背景篇

架构优势、要解决的问题 以及详细解决方案



## 独立调试

## 页面跳转

## 组件通信

## Application 生命周期分发

组件生命周期如何实现自动注册管理

- 组件AppLife初始化代码要解耦
- 组件AppLife的初始化顺序可以按照优先级排序，或可以动态排序
- 组件AppLife的初始化支持多进程
- 组件AppLife的初始化支持异步加载
- 组件AppLife的初始化时间可统计
- 组件AppLife可自动注册

自动识别所有组件的BaseAppLike类,增加或删除组件时，不用修改任何代码。

- 定义一个注解来标识实现了BaseAppLike的类。
  - 注解定义组件：lifecycle-annotation
    - public @interface AppLifeCycle
    - LifeCycleConfig

- 通过APT技术，在组件编译时扫描和处理前面定义的注解，生成一个BaseAppLike的代理类，姑且称之为BaseAppLikeProxy，所有的代理类都在同一个包名下，这个包名下必须只包含代理类，且类名由固定的规则来生成，保证不同组件生成的代理类的类名不会重复。
  - APT技术，编译时动态生成java代码
  - 注解处理组件：lifecycle-apt
    - 

- 需要有一个组件生命周期管理类，初始化时能扫描到固定包名下有多少个类文件，以及类文件的名称，这个固定包名就是前面我们生成的BaseAppLikeProxy的包名，代理类都放在同一个包名下，是为了通过包名找到我们所有的目标类。
  - 生命周期管理组件：lifecycle-api
    - 定义IAppLike接口，以及一个生命周期管理类。
  - 扫描到某个包名下有多少个class，以及他们的名称
  - gradle插件技术，在应用打包编译时，动态插入字节码以提高扫描效率
  - 制作gradle插件
  - 打包时动态插入字节码

- 组件集成后在应用的Application.onCreate()方法里，调用组件生命周期管理类的初始化方法。

- 组件生命周期管理类的内部，扫描到所有的BaseAppLikeProxy类名之后，通过反射进行类实例化。





第一版方式存在的问题：

- priority 排序是基于整个应用范围的，新增业务模块需要增加一个初始化类时，不便于插入到具体的初始化位置（可能需要修改其他初始化类的 priority），跨团队合作时维护人员更是不敢随意修改 priority 的值
- 编译期无法查看整个应用的初始化顺序，如果开发同学经验不足、自测不够充分或者代码审查不够仔细，初始化顺序错误的 bug 很容易被带到线上
- 运行期反射、排序影响应用冷启动时间

## Arouter 路由框架

注解

注解处理器

APT (Annotation Processing Tool)
是一种处理注释的工具，它对源代码文件进行检测找出其中的Annotation,根据注解自动生成代码,如果想要自定义的注解处理器能够正常运行，必须要通过APTI具来进行处理。也可以这样理解，只有通过声明APT工具后，程序在编译期间自定义注解解释器才能执行。
通俗理解:根据规则，帮我们生成代码、生成类文件

PackageElement
表示一个包程序元素。提供对有关包及其成员的信息的访问
Executab | eE | ement
表示某个类或接口的方法、构造方法或初始化程序(静态或实例)
TypeE I ement
表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。
Var i ab leE I ement
表示一个字段、
enumn

常量、方法或构造方法参数、局部变量或异常参数

Java 代码生成:JavaPoet

Gradle plugin

ASM







## 参考

- [Android 组件化开发实践 ｜ 简书](https://www.jianshu.com/p/59368ce8b670)
- [AppInitialzer](https://github.com/Panjianan/AppInitialzer)：用于子模块在Application启动时执行初始化代码的库
- [AppInit](https://github.com/bingoogolapple/AppInit) 是一款 Android 应用初始化框架，基于组件化的设计思路，功能灵活，使用简单。
- [Atlas](https://github.com/alibaba/atlas/tree/master/atlas-docs) 手淘项目动态组件化(Dynamic Bundle)框架。它主要提供了解耦化、组件化、动态性的支持。覆盖了工程师的工程编码期、Apk运行期以及后续运维期的各种问题。
- [Initiator](https://github.com/ren93/initiator) Android应用初始化工具
- [Alpha](https://github.com/alibaba/alpha) 是一个基于PERT图构建的Android异步启动框架，它简单，高效，功能完善。
- [ARouter](https://github.com/alibaba/ARouter/blob/master/README_CN.md) 是一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦

