[Github | ARouter](https://github.com/alibaba/ARouter) 

[Github | Forked From ARouter 内涵分析的注释和调试信息](https://github.com/jaydroid1024/ARouter)



# ARouter 组件化路由框架源码深入分析篇



## ARouter 组件化路由框架工程组件

**arouter-annotation -- 注解相关的声明, 其他工程都要依赖这个**

**arouter-api -- ARouter 框架的 API**

**arouter-compiler -- 编译期对注解分析的库**



### ARouter中引用的核心技术点扫盲

- APT
  - ARouter的使用非常方便，得益于APT。APT的作用是在编译阶段扫描并处理代码中的注解，然后根据注解输出Java文件。

- [**javapoet: **](https://github.com/square/javapoet) JavaPoet 是用于生成 .java 源文件的 Java API。他提供了调用对象方法的方式生成需要的代码，而不再需要人为的用StringBuilder去拼接代码，再使用IO写入文件。

- [**AutoService: **](https://github.com/google/auto/blob/master/service/README.md)他提供了简便的方式去注册APT，避免了原本繁琐的注册步骤。

- Gradle Plugin



## 从架构师角度看ARouter实现原理



### Arouter配置与基于用法

- Route注解定义页面路由

- AutoWired注解 参数自动注入
  - ButterKnife 的自动注入

- Interceptor注解定义拦截器
- IProvider跨模块Api调用

### Arouter编译时原理分析

- RouteProcessor收集路由节点信息
- AutowiredProcessor收集自动注入字段信息
- InterceptorProcessor收集拦截器

### Arouter运行时原理分析

- Arouter初始化

- 路由表获取

- 页面路由启动，服务创建
- 页面参数自动注入



![image-20210607021206990](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210607021206990.png)





# ARouter 组件化路由框架核心手写实现篇



# ARouter 组件化路由框架项目最佳实践篇





## 参考

[阿里云 | 开源最佳实践：Android平台页面路由框架ARouter](https://developer.aliyun.com/article/71687)

