# Android 组件化

## 背景篇

架构优势、要解决的问题 以及详细解决方案



## 独立调试

## 页面跳转

## 组件通信

## Application 生命周期分发

组件生命周期如何实现自动注册管理

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





## Arouter 路由框架





## 参考

- [Android 组件化开发实践 ｜ 简书](https://www.jianshu.com/p/59368ce8b670)
- [AppInitialzer](https://github.com/Panjianan/AppInitialzer)：用于子模块在Application启动时执行初始化代码的库
- [AppInit](https://github.com/bingoogolapple/AppInit) 是一款 Android 应用初始化框架，基于组件化的设计思路，功能灵活，使用简单。
- [Atlas](https://github.com/alibaba/atlas/tree/master/atlas-docs) 手淘项目动态组件化(Dynamic Bundle)框架。它主要提供了解耦化、组件化、动态性的支持。覆盖了工程师的工程编码期、Apk运行期以及后续运维期的各种问题。
- [Initiator](https://github.com/ren93/initiator) Android应用初始化工具
- [Alpha](https://github.com/alibaba/alpha) 是一个基于PERT图构建的Android异步启动框架，它简单，高效，功能完善。

