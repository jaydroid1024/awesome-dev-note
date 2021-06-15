## Gradle



JRouter



## 构建的基石 Gradle

- Gradle工程结构
  - 初步认识基于Gradle的工程结构,理解Gradle是什么
  - Gradle是一个基于Apache和Apache Maven概念的项目自动化构建工具。它使用一种基于Groovy的特定领域语言来声明项目设置,而不是传统的XML。
  - APK构建流程：编译资源和代码-压缩-签名-对齐

- Gradle的使用方法
  - Gradle的安装、执行、升级
  - 安装
    - 在系统全局安装Gradle
    - 利用生成 Gradle Wrapper
  - 执行
    - 命令格式: `./gradlew [task-name...] [-option-name]`
      - `./gradle clean`：将上次的构建产物清理掉
      - `./gradle projects`：树状形式打印所有module
      - `./gradle tasks`：查看所有任务
  - 升级
    - `./gradlew wrapper --gradle-version 6.4`  ：将gradle版本升/降级到6.4
    - `tree -L 3` :项目结构三级查看

- Gradle的开发语言-Groovy
  - 掌握 Groovy 的基本语法和进阶特性
  - 基本语法
    - 变量：int a=10; def b=20;
    - 字符串：String str="Hello World"
    - 集合：
      - 列表：def array =[1,2,3,4]; array.add(5); array.each{}
      - 映射表：def map=["key1":"value1", "key2":"value2"]; map["key1"]; 
    - 流程控制：
      - 循环：for (int i=0,i<10,i++) println("i is $i")
      - 方法：def fun(String name){println("name is $name")}
  - 进阶语法
    - DSL (Domain Specific Language )：领域专用语言；对比通用语言：Java,C等
    - 闭包
      - 开放匿名的代码块,可以接受参数,具有返回值，也可以被分配给变量
      - 定义规则: { [ 参数可选-> ] 表达式 }
      - 例如：def c={println("Hello Closure")}； def c2={it-> println("it is $it")} 调用 c(); c2("it value")
    - 

- Gradle构建技术
  - 学习Gradle构建的核心技术，包括生命周期、任务等
  - 生命周期
    - 初始化阶段 
    - 配置阶段
    - 执行阶段
  - 几个重要的角色
    - root project
    - project
    - task
      - 执行 ./gradle taskName
      - 创建 task taskName{ doLast{}}
      - 依赖 dependsOn [taskName]; task taskName{dependsOn :app:taskName  doLast{}}



## 页面路由开发实战Gradle插件



- Gradle插件介绍
  - 认识Gradle插件的概念使用场景
  - Gradle 插件
  - 分类
    - 二进制插件
      - Android 插件基于Java插件基础上添加了编译资源、打包apk等操作
      - 声明：classpath "com.android.tools.build:gradle:3.1.2"
      - 应用：plugins { id 'com.android.application'}
      - 参数配置：android { compileSdkVersion 30 }
    - 脚本插件
      - apply from: project.rootProject.file("other.gradle")
  - 开发
    - 建立插件工程-实现插件逻辑-发布
    - 页面路由插件
      - 对于一个URL ,根据映射关系表,来打开路由所指定的页面
      - 标记页面
      - 收集页面
      - 生成文档
      - 注册映射
      - 打开页面
- Gradle插件的使用
  - 学习Gradle插件的基本使用方法
- 实战开发一个Gradle插件
  - 手把手实战开发页面路由中的Gradle插件

### APT采集页面路由信息

APT是什么
APT的技术原理
APT实战应用：路由信息采集功能的实现



字节码插桩

