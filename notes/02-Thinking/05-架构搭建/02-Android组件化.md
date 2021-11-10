# Android 组件化











- Android开发目前的工程模式
  - **单工程开发模式：**一个App对应一个代码工程，所有的代码都集中在一个工程的一个module里。不利于多人协作和长期迭代开发。
  - **组件化开发模式：**将一个App根据业务功能划分成多个独立的代码模块，各个模块也能独立调试运行。有利于项目的高效迭代开发。
    - 组件化开发模式又分为**单工程**和**多工程**两种具体的开发模式：。
    - **单工程：**所有代码位于一个工程中，组件以module形式存在，由app壳负责独立调试或集成打包。
    - **多工程：**组件代码在单独的仓库，包含调试壳和相应的组件module;。主项目由一个主模块工程和多个子模块工程组成，用于集成子模块，进行集成调试和打包。主项目对各组件库支持**源码和AAR**两种依赖方式
- 组件VS模块
  - 模块和组件的本质思想是一样的，都是为了业务解耦和代码重用，组件相对模块粒度更细。
  - 在划分的时候，模块是业务导向，划分一个个独立的业务模块，组件是功能导向，划分一个个独立的功能组件。
- 单组件独立调试与编译
  - 通过无切换方式实现一个组件对应一个壳
    - 多个壳：新增组件时需要添加对应的壳，组件切换不需要编译
  - 控制条件变量实现同一个壳对应任一组件
    - 一个壳：每次切换都会编译
    - 通过变量控制实现切换 library 与 application 
- 组件分层问题
  - 从上到下共分为四层：app->biz->base->lib
  - 模块化架构设计的思路，我们总结为**纵向和横向两个维度**。纵向上根据与业务的紧密程度进行分层，横向上根据业务或者功能的边界拆分模块。
  - **层级多的问题在于，下层代码仓的修改会影响更多的上层代码仓，并且层级越多，并行开发、并行编译的程度越低。**
  - **模块依赖规则：**
    - 只有上层代码仓才能依赖下层代码仓，不能反向依赖，否则可能会出现循环依赖的问题；
    - 同一层的代码仓不能相互依赖，保证模块间彻底解耦。
  - 资源命名与Android AndroidManifest.xml 以及资源文件合并原理
  - Gradle dependencies 依赖穿透与组件编译隔离问题
  - 通过 component_export 组件隔离组件，保证组件单项依赖性
  - 代码中心化在Android组件化中的问题
    - 如何防止代码很“自然的”被下沉到基础工程中
    - 产生背景：Event 事件实体类、模块A想使用模块B的数据结构类、模块边界破坏、基础工程中心化，都是代码持续劣化的帮凶
    - 解决方案：组件暴露SDK的方式(sdk化)、组件接口暴露的形式(api化)、[SPI 方式实现](https://www.jianshu.com/p/46b42f7f593c)
    - 使用方式和思路都很简单。对于java文件，将工程里想要暴露出去的接口类后缀名从"java"改成".api",就可以了。而且并不只是java文件，其他文件如果也想暴露，在文件名后增加".api",也一样可以。
    - 我们使用某种技术将user组件中需要共享数据的部分抽象成接口，利用AS对文件类型的配置将（kotlin）后拽修改为.api ，然后再创建一个同包名的module-api 组件用来让其他组件依赖以及自身组件均依赖该组件，这样就能完美的将需要共享的数据单独出去使用了，保证了组件的单项依赖性。
- 组件路由跳转与通信
  - ARouter 
- 统一依赖配置文件与依赖管理问题
  -  **ext** 属性是ExtensionAware类型的一个特殊的属性，本质是一个Map类型的变量，而 ExtentionAware接口的实现类为Project, Settings, Task, SourceSet等， ExtentionAware 可以在运行时扩充属性，而这里的 ext 就是里面的一个特殊的属性。
- 组件生命周期管理
  - JDispatcher
- 编译优化与组件包aar化
  - Maven
  - 如何实现进可 aar 退可源代码
  - 如何解决 aar 化下层改动上层无感知







## 组件分层与依赖管理问题

<img src="https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20210806155745.png" style="zoom:50%;" />





#### 1.宿主壳：\app

- 壳工程依赖了需要集成的业务组件，它可能只有一些配置文件，没有任何代码逻辑。

- 根据你的需要选择集成你的业务组件，不同的业务组件就组成了不同的APP。

#### 2.业务组件：\biz

- 业务组件就是我们真正的体现在页面上的业务组件了，常规业务组件按需依赖基础业务组件和基础功能组件

- 我们通常按照功能模块来划分业务组件，例如：登录、首页、Feed列表、IM消息、详情页等组件，

- 这里的每个业务组件都是一个小的APP，借助组件壳都可以单独编译，单独打包成APK在手机上运行。

#### 3.基础业务组件：\base

 * 基础业务组件按需依赖功能层的组件，该层组件是对一些系统通用的业务能力进行封装的组件。
 * 例如公共业务组件，Application、BaseActivity、BaseFragment、mvp、mvvm 基类等；
 * 例如分享能力组件，其他业务只要集成该组件就能进行相关分享；
 * 例如共享公共数据，可以将用户登录信息缓存在这里等；
 * 例如共享公共资源，value、drawable、style 等；
 * 例如组件间数据通信的接口，ARouter 服务所需的IProvider 以及路由 path 等。

#### 4.功能组件：\lib

* 这层的组件都是最基础的功能，通常它不包含任何业务逻辑，
* 也可以说这些组件是一些通用的工具类和一些第三方库。
* 或者说只有足够优秀足够基础的代码才能下沉到基础层。
* 例如日志记录组件，它只是提供了日志记录的能力，你要记录什么样的日志，它并不关心；
* 例如图片加载组件，它是一个全局通用图片加载框架；
* 例如网络服务组件，它封装了网络的请求能力；



## 组件独立调试与编译

- 组件分层实现业务隔离
- Gradle 依赖
- Gradle 统一配置管理
  - 组件化重构后，module会很多，此时就需要对所有module的build.gradle脚本进行统一配置管理和依赖库版本管理，避免混乱。
- 套壳调试
  - 普通的组件化方案是使用一个变量在application与library之间切换，这很容易导致各种混乱问题：比如Manifest冲突，R文件冲突等。
  - 另一种可选择的方案是多app壳分组加载的方式
  - 每个业务组件，都分别有一个各自的app壳组件并且壳组件不会参与到集成编译中，主app壳依赖所有的业务组件
  - 调试时，直接运行各自的app壳模块进行单组件或部分组件调试，运行主app壳进行全量打包测试
  - 业务组件不再在library与application之间进行切换，单组件和集成调试时开发环境统一，不易出现环境切换冲突
- 打包上传
- 编译优化

## 组件路由跳转与通信

- 路由跳转
- 组件通信
- 自研路由框架：JRouter

## 组件生命周期与分发

- Android Application 声明周期分发
- 自研分发框架 JDispatcher



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



## JDispatcher

Android 组件生命周期与分发框架

### 框架结构

- jdispatcher-annotation
  - 模块类型：apply plugin: 'java'
  - 模块描述：声明编译时所需的注解类以及公共类等
  
- jdispatcher-compiler
  - 模块类型：apply plugin: 'java'
  - 模块描述：编译期(.java--.class阶段) 收集和处理整个工程中的Dispatch注解信息并通过 javapoet 生成辅助类文件 JDispatcher$$Group_hash.java 
  
- jdispatcher-plugin

  - 模块类型：apply plugin: 'groovy'

  - 模块描述：编译期(.class--.dex阶段) 自定义Transform拦截AGP的构建过程，找到所有Dispatch并排序

  - 模块主要工作：
    - 第一个：IDispatch 的分发流程
      - 扫描到所有 APT 生成的 JDispatcher$$Group_hash.java 文件
      - 反射获取收集到的 Map<String, DispatchItem> atlas)
      - 通过 atlas 集合收集到的 DispatchItem 实现对 IDispatch 对象的反射实例化
      - 按照 DispatchItem 的排序规则完成排序操作
      - 将排好序的 IDispatch 集合通过字节码插桩到 JDispatcher 中，运行时执行对所有 IDispatch 的分发操作
    - 第二个：Application 生命周期方法的自动注册流程
      - 通过调用方在gradle中配置的 Application 全类名，在自定义Transform中扫描到该类
      - JDispatcher 调用字节码注入到 onTerminate()
      - JDispatcher 调用字节码注入到 onConfigurationChanged(newConfig: Configuration)
      - JDispatcher 调用字节码注入到 onLowMemory()
      - JDispatcher 调用字节码注入到 onTrimMemory(level: Int)
- jdispatcher-api
  - 模块类型：apply plugin: 'com.android.library'
  - 模块描述：运行时用于整个框架的初始化，运行时分发等操作



### 框架特色

- 实现 Application 各个生命周期方法在所有需要的组件中分发
- 分发顺序支持多种规则
  - 优先级：优先级值越大越先被调用
  - 依赖项：组件依赖的分发类先初始化
  - 支持自动校正在优先级和依赖项两种规则交叉使用情况下的分发顺序
- 分发维度支持多种规则
  - 在指定进程(所有进程，主进程，非主进程)中分发
  - 在指定线程(主线程，空闲线程，工作线程)中分发，实现异步加载
  - 支持通过非阻塞式异步通知机制实现异步加载与同步加载交叉使用的情况（todo）
  - 手动延迟调用分发，实现延迟加载
  - 通过 ContentProvider 实现在 Application 之前超前预加载（todo）
  - 只在debug模式下分发，实现 DevTools、DoKit 等开发工具的初始化
- 维度值采用对整型 or/and 的位操作完成多维度值的收集与识别，灵活且高效
- 支持初始化时批量传参，可用于多项目多环境的三方 sdk 的初始化，使环境配置更统一
- 通过注解打点，APT 收集分发类，降低耦合，可用于模块化，组件化场景
- 通过拦截 AGP 构建流程实现在编译期间对分发类的扫描和排序，提高运行时性能
- 通过 ASM 字节码插桩实现分发表和 Application  生命周期回调方法的自动注入，集成更高效
- 支持统计所有分发类的初始化时间，可用于启动优化的统计与排查





























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



Arouter 路由流程

### 编译时

1. 声明一个注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    /**
     * Path of route
     */
    String path();

    /**
     * Used to merger routes, the group name MUST BE USE THE COMMON WORDS !!!
     */
    String group() default "";

    /**
     * Name of route, used to generate javadoc.
     */
    String name() default "";

    /**
     * Extra data, can be set by user.
     * Ps. U should use the integer num sign the switch, by bits. 10001010101010
     */
    int extras() default Integer.MIN_VALUE;

    /**
     * The priority of route.
     */
    int priority() default -1;
}
```

2. 页面标记注解

```java
@Route(path = "/test/activity1", name = "测试用 Activity")
public class Test1Activity extends BaseActivity {}
```

3. Gradle 文件中 配置 ModuleKey 用于分组和懒加载

```groovy
javaCompileOptions {
     annotationProcessorOptions {
           arguments = [AROUTER_MODULE_NAME: project.getName(), AROUTER_GENERATE_DOC: "enable"]
     }
}
```

4. RouteProcessor 路由注解处理器

4.1.  获取用户配置的选项参数

```java
// moduleName, generateDoc 用户配置的两个参数, 通过复写getSupportedOptions 方法注册参数的key
moduleName = options.get(KEY_MODULE_NAME);
generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
```

4.2. 初始化路由表 json 文件

```java
private void initGeneratedRouteDocJsonFile() {
        if (generateDoc) {
            try {
                docWriter = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, //文件的位置。
                        PACKAGE_OF_GENERATE_DOCS, //文件的路径。
                        "arouter-map-of-" + moduleName + ".json"  //文件的名字。
                ).openWriter();
            } catch (IOException e) {
                logger.error("Create doc writer failed, because " + e.getMessage());
            }
        }
}
```

4.3 获取标注了 Route 注解的页面元素集合

```java
Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
```

4.4 类型相关的准备工作

```java
// 类型相关的准备工作
// TypeMirror :表示 Java 编程语言中的一种类型。类型包括基本类型、声明类型（类和接口类型）、数组类型、类型变量和空类型。
// 还表示通配符类型参数、可执行文件的签名和返回类型，以及对应于包和关键字 {@code void} 的伪类型。
// interface of System
//android.app.Activity
TypeMirror type_Activity = elementUtils.getTypeElement(ACTIVITY).asType();
//android.app.Service
TypeMirror type_Service = elementUtils.getTypeElement(SERVICE).asType();
//android.app.Fragment
TypeMirror fragmentTm = elementUtils.getTypeElement(FRAGMENT).asType();
//android.support.v4.app.Fragment
TypeMirror fragmentTmV4 = elementUtils.getTypeElement(Consts.FRAGMENT_V4).asType();
//TypeElement: 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。请注意，枚举类型是一种类，注释类型是一种接口。
// Interface of ARouter
//com.alibaba.android.arouter.facade.template.IRouteGroup
TypeElement type_IRouteGroup = elementUtils.getTypeElement(IROUTE_GROUP);
//com.alibaba.android.arouter.facade.template.IInterceptor
TypeElement type_IProviderGroup = elementUtils.getTypeElement(IPROVIDER_GROUP);
//ClassName: 顶级类和成员类的完全限定类名。
//com.alibaba.android.arouter.facade.model.RouteMeta  它包含基本的路由信息。
ClassName routeMetaCn = ClassName.get(RouteMeta.class);
//com.alibaba.android.arouter.facade.enums.RouteType  路由类型
ClassName routeTypeCn = ClassName.get(RouteType.class);
```

4.5 遍历 4.3 中 获取到的 routeElements 获取注解参数封装 RouteMeta

```java
// RouteMeta 路由描述实体类
public class RouteMeta {
    private RouteType type;         // Type of route 四大组件类型 IProvider等
    private Element rawType;        // Raw type of route //Router 注解类的元素
    private Class<?> destination;   // Destination 跳转的目标class
    private String path;            // Path of route 路由
    private String group;           // Group of route 路由组
    private int priority = -1;      // The smaller the number, the higher the priority
    private int extra;              // Extra data 附加数据，表示状态
    private Map<String, Integer> paramsType;  // Param type intent 携带的参数
    private String name; //路由描述
    private Map<String, Autowired> injectConfig;  // Cache inject config. 参数注入的缓存
}

for (Element element : routeElements) {
  //标注了这个注解的类的类型，xxxActivity
	TypeMirror tm = element.asType();
  //获取这个元素上标注的注解类
  Route route = element.getAnnotation(Route.class);
  // Activity or Fragment
  if (types.isSubtype(tm, type_Activity) || types.isSubtype(tm, fragmentTm) 
      || types.isSubtype(tm, fragmentTmV4)) {
     //页面中的参数：["name",TypeKind.STRING.ordinal()]
      Map<String, Integer> paramsType = new HashMap<>();
      //页面中的参数：["name",Autowired(desc=, required=true, name=boy)]
      Map<String, Autowired> injectConfig = new HashMap<>();
      //递归注入页面中标注了Autowired 注解的字段。缓存在map中
      injectParamCollector(element, paramsType, injectConfig); 
      if (types.isSubtype(tm, type_Activity)) {  //tm is  Activity
        //route: Route(path=/yourservicegroupname/hello  )
        //element: com.alibaba.android.arouter.demo.module1.TestModuleActivity
        //paramsType: is {ser=9, pac=10, ch=5}
         routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY, paramsType);
      } else {//tm is  Fragment
         logger.info(">>> Found fragment route: " + tm.toString() + " <<<");
         routeMeta = new RouteMeta(route, element, RouteType.parse(FRAGMENT), paramsType);
      }
      //将Fragment 和 Activity 中的参数的注解 Map<String, Autowired>  封装到路由bean 中
       routeMeta.setInjectConfig(injectConfig);
    }
  //按组对路由数据进行排序
  categories(routeMeta);
}

//填充部分数据
public RouteMeta(Route route, Element rawType, RouteType type, Map<String, Integer> paramsType) {
this(type, rawType, null, route.name(), route.path(), route.group(), paramsType, route.priority(), route.extras());
    }
```

4.6  按组对路由数据进行排序并缓存到 全局的 groupMap 中

```java
// ModuleName and routeMeta.
private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();

private void categories(RouteMeta routeMete) {
    // 校验路由 path 设置group缺省值为 path 的 第一个/ 之前的值
    if (routeVerify(routeMete)) {
       //先从缓存中取
        Set<RouteMeta> routeMetas = groupMap.get(routeMete.getGroup()); 
      	// 缓存中还没有这个组就新加一个KV
        if (CollectionUtils.isEmpty(routeMetas)) { 
            //TreeSet: 保证唯一也有序
            Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                @Override
                public int compare(RouteMeta r1, RouteMeta r2) {
                    try {
                        //按照字符串的自然顺序排序 a-z
                        return r1.getPath().compareTo(r2.getPath());
                    } catch (NullPointerException npe) {
                        return 0;
                    }
                }
            });
            routeMetaSet.add(routeMete);
            groupMap.put(routeMete.getGroup(), routeMetaSet);
        } else {
            routeMetas.add(routeMete);
        }
    } 
}
```

4.7 到目前为止收集到的数据 groupMap 并且已经按照 group 分组

```java
//{test=[RouteMeta{
type=ACTIVITY, 
rawType=com.alibaba.android.arouter.demo.module1.testactivity.Test1Activity, 
destination=null, 
path='/test/activity1', 
group='test', 
priority=-1, 
extra=-2147483648, 
paramsType={ser=9, ch=5, fl=6, dou=7, boy=0, url=8, pac=10, obj=11, name=8, objList=11, map=11, age=3, height=3}, 
name='测试用 Activity'}, ...],...}
```

4.8 遍历 groupMap 以及每个组对应的列表

```java
for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
    List<RouteDoc> routeDocList = new ArrayList<>();
    String groupName = entry.getKey(); // K
    Set<RouteMeta> groupData = entry.getValue(); //V
    for (RouteMeta routeMeta : groupData) {
        //生成单个路由的文档信息
        RouteDoc routeDoc = extractDocInfo(routeMeta);
        //element 强转为 TypeElement 并通过 javapoet 的 ClassName 获取这个类的全类名
        ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());

        // todo 追加收集到的 iProvider 路由信息
  
        // todo 追加收集到的 Autowired 注解的参数信息

        // Make map body for paramsType
        StringBuilder mapBodyBuilder = new StringBuilder();
        // intent 携带的参数类型键值对
        Map<String, Integer> paramsType = routeMeta.getParamsType();
        // intent 携带的参注解信息键值对
        Map<String, Autowired> injectConfigs = routeMeta.getInjectConfig();

        if (MapUtils.isNotEmpty(paramsType)) {
            //[Param{key='ser', type='serializable', description='null', required=false},
            List<RouteDoc.Param> paramList = new ArrayList<>();
            for (Map.Entry<String, Integer> types : paramsType.entrySet()) {
                mapBodyBuilder.append("put(\"").append(types.getKey()).append("\", ").append(types.getValue()).append("); ");
                RouteDoc.Param param = new RouteDoc.Param();
                Autowired injectConfig = injectConfigs.get(types.getKey());
                param.setKey(types.getKey());
                param.setType(TypeKind.values()[types.getValue()].name().toLowerCase());
                param.setDescription(injectConfig.desc());
                param.setRequired(injectConfig.required());
                paramList.add(param);
            }
            routeDoc.setParams(paramList);
        }


        //收集到的 参数和类型
        String mapBody = mapBodyBuilder.toString();

        //注: ARouter::Compiler >>> Autowired: mapBody is :
        // put("ser", 9); put("ch", 5); put("fl", 6); put("dou", 7); put("boy", 0); put("url", 8); put("pac", 10); put("obj", 11); put("name", 8); put("objList", 11); put("map", 11); put("age", 3); put("height", 3);  <<<
        logger.info(">>> Autowired: mapBody is : " + mapBody + " <<<");


        //   追加语句： atlas.put(
        //   "/kotlin/java",
        //   RouteMeta.build(
        //   RouteType.ACTIVITY,
        //   TestNormalActivity.class,
        //   "/kotlin/java",
        //   "kotlin",
        //   null,
        //   -1,
        //   -2147483648));

        loadIntoMethodOfGroupBuilder.addStatement(
                "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + (StringUtils.isEmpty(mapBody) ? null : ("new java.util.HashMap<String, Integer>(){{" + mapBodyBuilder.toString() + "}}")) + ", " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                routeMeta.getPath(),
                routeMetaCn,
                routeTypeCn,
                className,
                routeMeta.getPath().toLowerCase(),
                routeMeta.getGroup().toLowerCase());

        routeDoc.setClassName(className.toString());

        routeDocList.add(routeDoc);
    }


    // Generate groups ARouter$$Group$$arouter
    String groupFileName = NAME_OF_GROUP + groupName;

    //生成Java 类
    // package com.alibaba.android.arouter.routes;
    // public class ARouter$$Group$$test implements IRouteGroup {
    JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
            TypeSpec.classBuilder(groupFileName)
                    .addJavadoc(WARNING_TIPS)
                    .addSuperinterface(ClassName.get(type_IRouteGroup)) 
                    .addModifiers(PUBLIC)
                    .addMethod(loadIntoMethodOfGroupBuilder.build())
                    .build()
    ).build().writeTo(mFiler);


    rootMap.put(groupName, groupFileName);  //["arouter","ARouter$$Group$$arouter"]
    docSource.put(groupName, routeDocList);
} 
```



### 运行时











## 参考

- 组件路由与交互问题
  - [ARouter](https://github.com/alibaba/ARouter/blob/master/README_CN.md) 是一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦
- 组件初始化问题
  - [Android 组件化开发实践 ｜ 简书](https://www.jianshu.com/p/59368ce8b670)
  - [AppInitialzer](https://github.com/Panjianan/AppInitialzer)：用于子模块在Application启动时执行初始化代码的库
  - [AppInit](https://github.com/bingoogolapple/AppInit) 是一款 Android 应用初始化框架，基于组件化的设计思路，功能灵活，使用简单。
  - [Atlas](https://github.com/alibaba/atlas/tree/master/atlas-docs) 手淘项目动态组件化(Dynamic Bundle)框架。它主要提供了解耦化、组件化、动态性的支持。覆盖了工程师的工程编码期、Apk运行期以及后续运维期的各种问题。
  - [Initiator](https://github.com/ren93/initiator) Android应用初始化工具
  - [Alpha](https://github.com/alibaba/alpha) 是一个基于PERT图构建的Android异步启动框架，它简单，高效，功能完善。[解析文章](https://juejin.cn/post/6844904201143713806)
  - [android-startup](https://github.com/idisfkj/android-startup)
  - [AppStartFaster](https://github.com/NoEndToLF/AppStartFaster)
- 组件依赖管理问题
  - [组件化场景下module依赖实践方案](https://mp.weixin.qq.com/s/i2xeAIei9N8oHTqV88IeNQ)
  -  [腾讯音乐技术团队 | Android全量编译加速——（透明依赖）](https://mp.weixin.qq.com/s/tK9z6QjMvMeakpTHdTBBBg)
- Flutter 与 组件化
  - [Flutter 混合开发和组件化实践 | 开发者说·DTalk](https://mp.weixin.qq.com/s/82MVIeqMF8esN7j28TXbXQ)
- 大厂组件化实战
  - [知乎组件化实战](https://zhuanlan.zhihu.com/p/45374964)
  - [有赞微商城-Android组件化方案](https://tech.youzan.com/you-zan-yi-dong-androidzu-jian-hua-fang-an/)
  - [Android组件化方案及组件消息总线modular-event实战](https://tech.meituan.com/2018/12/20/modular-event.html)
  - [汇总分享下十几家大厂的组件化方案](https://mp.weixin.qq.com/s/6I8FIHmYqgCXgD8wF06E_A)

