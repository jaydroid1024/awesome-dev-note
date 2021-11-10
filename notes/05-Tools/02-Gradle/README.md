## Gradle

[Hunter| Hunter是这么一个框架，帮你快速开发插件，在编译过程中修改字节码](https://github.com/Leaking/Hunter)

[ByteX | ByteX是一个基于gradle transform api和ASM的字节码插件平台](https://github.com/bytedance/ByteX) 



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







## AGP字节码操作基础基础技术-Transform

### 利用 AGP Transform 修改字节码的原理是什么？

在 transform方法中，我们将每个jar包和class文件复制到dest路径，这个dest路径就是下一个Transform的输入数据，而在复制时，我们就可以做一些狸猫换太子，偷天换日的事情了，先将jar包和class文件的字节码做一些修改，再进行复制即可，至于怎么修改字节码，就要借助我们后面介绍的ASM了。

### Transform的工作机制什么？

每个Transform其实都是一个gradle task，Android编译器中的TaskManager将每个Transform串连起来，第一个Transform接收来自javac编译的结果，以及已经拉取到在本地的第三方依赖（jar. aar），还有resource资源，注意，这里的resource并非android项目中的res资源，而是asset目录下的资源。这些编译的中间产物，在Transform组成的链条上流动，每个Transform节点可以对class进行处理再传递给下一个Transform。我们常见的混淆，Desugar等逻辑，它们的实现如今都是封装在一个个Transform中，而我们自定义的Transform，会插入到这个Transform链条的最前面。

### Transform的输入源有哪几种，分别是什么

Transform有两种输入，一种是消费型的，当前Transform需要将消费型型输出给下一个Transform，另一种是引用型的，当前Transform可以读取这些输入，而不需要输出给下一个Transform，比如Instant Run就是通过这种方式，检查两次编译之间的diff的。

消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();

### Transform数据流转处理机制源码解析？

//TODO

### Transform的输入数据的过滤机制?

可以通过Scope和ContentType两个维度进行过滤。ContentType，顾名思义，就是数据类型，在插件开发中，我们一般只能使用CLASSES和RESOURCES两种类型，CLASSES包含了class文件和jar文件。如果是要处理所有class和jar的字节码，ContentType我们一般使用TransformManager.CONTENT_CLASS。
Scope相比ContentType则是另一个维度的过滤规则，
如果是要处理所有class字节码，Scope我们一般使用TransformManager.SCOPE_FULL_PROJECT。
TransformManager有几个常用的Scope集合方便开发者使用。SCOPE_FULL_PROJECT，SCOPE_FULL_LIBRARY

### 自定义Transform时如何提供增量编译？

想要开启增量编译，我们需要重写Transform的这个接口，返回true。
public boolean isIncremental() {
    return true;
}
虽然开启了增量编译，但也并非每次编译过程都是支持增量的，毕竟一次clean build完全没有增量的基础，所以，我们需要检查当前编译是否是增量编译。

如果不是增量编译，则清空output目录，然后按照前面的方式，逐个class/jar处理
如果是增量编译，则要检查每个文件的Status，Status分四种，并且对这四种文件的操作也不尽相同。
NOTCHANGED: 当前文件不需处理，甚至复制操作都不用；
ADDED、CHANGED: 正常处理，输出给下一个任务；
REMOVED: 移除outputProvider获取路径对应的文件。
 boolean isIncremental = transformInvocation.isIncremental();
Status status = jarInput.getStatus();
ADDED、CHANGED: 正常处理，输出给下一个任务；
transformJar(jarInput.getFile(), dest, status);
移除outputProvider获取路径对应的文件。
  if (dest.exists()) {
        FileUtils.forceDelete(dest);
   }
DirectoryInput 也是同样的处理
这就能为我们的编译插件提供增量的特性。



### Transform 扫描过程如何实现并发处理加快编译？

编译流程一般都是在PC上，所以我们可以尽量敲诈机器的资源。所以transform方法遍历处理每个jar/class的流程，其实可以并发处理，
并发编译的实现并不复杂，只需要将上面处理单个jar/class的逻辑，并发处理，最后阻塞等待所有任务结束即可。

private WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();


//异步并发处理jar/class
waitableExecutor.execute(() -> {
    bytecodeWeaver.weaveJar(srcJar, destJar);
    return null;
});
waitableExecutor.execute(() -> {
    bytecodeWeaver.weaveSingleClassToFile(file, outputFile, inputDirPath);
    return null;
});  


//等待所有任务结束
waitableExecutor.waitForTasksWithQuickFail(true);

并发编译，基本比非并发编译速度提高了80%。增量的速度比全量的速度提升了3倍多，而且这个速度优化会随着工程的变大而更加显著。

### 写一个自定义的Transform实现一个什么样的功能？

//TODO





## AGP字节码操作基础基础技术-ASM













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

