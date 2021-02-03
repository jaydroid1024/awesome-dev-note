# awesome-dev-note


|      编程基础      |             编程思维             |     编程语言&nbsp;     |        业务方向        |      &nbsp;效率工具&nbsp;&nbsp;      | &nbsp;其它技能 |
| :-----------------: | :--------------------------: | :--------------------: | :------------------: | :--------------------------: | :--------------------: |
| [:computer:](#算法) | [:computer:](#computer-操作系统) | [:cloud:](#cloud-网络) | [:art:](#art-面向对象) | [:floppy_disk:](#floppy_disk-数据库) |  [:coffee:](#coffee-java)  |
|      组成原理       | 算法 | Java | 源码解析 | Git | 沟通 |
|      编译原理       | 数据结构 | Kotlin | Android | IDE | 效率 |
|      操作系统       | 面向对象 | C++ | Flutter | UML | 求知 |
|        计算机网络      | 编程规范 | Html | ... | Gradle | 自控 |
|       ...       | 重构 | Swift |                        | Mac | ... |
|  | 数学 | ... | | Chrome | |
|  | ... | | | ... | |

1. 编程基础：组成原理、编译原理、操作系统、计算机网络

2. 编程思维：算法、数据结构、重构、面向对象、编程规范、数学

3. 编程语言：Java、Kotlin、Python、Swift、C++、Html

4. 业务方向：Mobile、PC、Server

5. 工具：Git、IDE、UML、Mac、Gradle

6. 软技能：沟通、效率、求知、自控

   



## 编程语言

## Java



|       Java 泛型        |          Java 注解           |        Java 反射        |                       Java 并发编程                       | Java 序列化                  |       Java 虚拟机原理        |         Java 类加载          |           Java IO            |
| :--------------------: | :--------------------------: | :---------------------: | :-------------------------------------------------------: | ---------------------------- | :--------------------------: | :--------------------------: | :--------------------------: |
|       自定义注解       |      自定义注解与元注解      |   反射基本概念与Class   |                      线程共享和协作                       |                              |                              |                              |                              |
|      通配符与嵌套      |       注解参数与默认值       | 三种获取Class对象的方式 |           CPU核心数，线程数，时间片轮转机制解读           |                              |                              |                              |                              |
|      泛型上下边界      |    APT，编译时注解处理器     |  获取构造器与属性信息   | synchronized、Lock、volatile、ThreadLocal如何实现线程共享 |                              |                              |                              |                              |
| RxJava中泛型的使用分析 |     插桩，编译后处理筛选     |      包信息和方法       |                                                           |                              |                              |                              |                              |
|                        | 反射，运行时动态获取注解信息 |    Hook技术动态编程     |               反射，运行时动态获取注解信息                | 反射，运行时动态获取注解信息 | 反射，运行时动态获取注解信息 | 反射，运行时动态获取注解信息 | 反射，运行时动态获取注解信息 |
|                        |       Retrofit中的注解       |                         |                                                           |                              |                              |                              |                              |
|                        |                              |                         |                                                           |                              |                              |                              |                              |
|                        |                              |                         |                                                           |                              |                              |                              |                              |





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







