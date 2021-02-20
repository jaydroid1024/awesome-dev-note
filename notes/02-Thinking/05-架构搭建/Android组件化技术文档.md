# Android组件化技术文档

- [WanDroid组件化测试项目](https://github.com/Jay-Droid/WanDroid)



## 目录

[1. 组件划分](#1-组件划分)

- [1.宿主壳和调试壳(app)](#1宿主壳和调试壳app)

- [2.常规业务组件(biz)](#2常规业务组件biz)
- [3.基础业务组件(base)](#3基础业务组件base)
- [4.基础功能组件(lib)](#4基础功能组件lib)
- [5.各层组件依赖关系图](#5各层组件依赖关系图)
- [6 骑士组件化工程架构图 V1.0](#6-骑士组件化工程架构图-v10)
- [7 骑士组件化工程架构图 V2.0](#7-骑士组件化工程架构图-v20)
- [8 ](#8-骑士组件化工程页面导航思维导图)[骑士组件化工程页面导航思维导图](https://www.processon.com/view/link/5ee9b4005653bb29259f9197)

[2. 组件创建](#2-组件创建)

- - [1. 从原有代码中拆分出的新组件](#1-从原有代码中拆分出的新组件)
  - [2. 为新功能创建新组件](#2-为新功能创建新组件)
  - [3. 组件创建流程](3. 组件创建流程)
    - [1. 添加调试壳的步骤](#1-添加调试壳的步骤)
    - [2. 添加常规业务组件的步骤](#2-添加常规业务组件的步骤)
    - [3. 添加基础业务组件的步骤](#3-添加基础业务组件的步骤)
    - [4. 添加基础功能组件的步骤](#4-添加基础功能组件的步骤)

[3. 组件开发](#3-组件开发)

- - [1.单组件调试](#1单组件调试)
  - [2.多组件调试](#2多组件调试)
  - [3.组件内的 Application 生命周期注入](#3组件内的-application-生命周期注入)
  - [4.组件间交互](#4组件间交互)
  - 5.依赖管理
    - [1. Gradle 依赖方法介绍](#1-gradle-依赖方法介绍)
    - [2. 依赖方法使用规范](#2-依赖方法使用规范)
  - 6.组件资源命名规范
    - [1. 组件命名规范](#1-组件命名规范)
    - [2. 组件资源命名规范](#2-组件资源命名规范)
  - 7.AndroidManifest 文件合并冲突问题
    - [1. 合并优先级（按优先级由高到低的顺序）](#1-合并优先级按优先级由高到低的顺序)
    - [2. 合并规则](#2-合并规则)
    - [3. 合并冲突解决(属性标记)](#3-合并冲突解决属性标记)
  - [8. ButterKnife 在library中使用的问题](#8-butterknife-在library中使用的问题)
  - \9. 视图绑定替代方案对比
    - [1. View Binding 与 findViewById() 的比较](#1-view-binding-与-findviewbyid-的比较)
    - [2. View Binding 与 Data Binding 库的比较](#2-view-binding-与-data-binding-库的比较)
    - [3. View Binding 与 Kotlin Synthtic 的比较](#3-view-binding-与-kotlin-synthtic-的比较)
    - [4. View Binding 与 Butter Knife 的比较](#4-view-binding-与-butter-knife-的比较)

[4.开发规范](#4开发规范)

- - [1.组件开发规范](#1组件开发规范)
  - [2.`ARouter` 路由使用规范](#2arouter-路由使用规范)
  - [3.`lib_net` 网络库使用规范](#3lib_net-网络库使用规范)
  - [4.`config.gradle` 文件更新规范](#4configgradle-文件更新规范)

[5. 组件发布](#5-组件发布)

- 1. 公共组件如何实现多项目共享

  - [方案一：将moudle放在本地指定目录实现多项目复用](#方案一将moudle放在本地指定目录实现多项目复用)
  - [方案二：复制粘贴组件](#方案二复制粘贴组件)
  - [方案三：每个人搭建本地Maven仓库，用git实现本地仓库的同步](#方案三每个人搭建本地maven仓库用git实现本地仓库的同步)
  - [方案四：搭建公司私有Maven仓库，依靠公司服务保持组件同步](#方案四搭建公司私有maven仓库依靠公司服务保持组件同步)
  - [方案五：使用Git中submodule实现](#方案五使用git中submodule实现)

- [2. 组件发布Maven 私服](#2-组件发布maven-私服)

- [3. 组件上传Maven规范](#3-组件上传maven规范)

[6. 后期计划](#6-后期计划)

[7. 其它问题](#7-其它问题)

- [1，其它流程上点问题](#1其它流程上点问题)





# 1. 组件划分

### 1.宿主壳和调试壳(app)
- 宿主壳依赖了需要集成的业务组件(biz)，它可能只有一些配置文件，没有任何代码逻辑。
- 根据你的需要选择集成你的业务层的组件，不同的业务组件就组成了不同的APP。

### 2.常规业务组件(biz)
- 常规业务组件按需依赖基础业务层组件和基础功能层组件，也可以不依赖下层组件的，
- 该层的组件就是我们真正的业务组件了。我们通常按照业务功能模块来划分业务组件，例如注册登录、首页、消息等。
- 这里的每个业务组件都是一个小的 app (编译组件 app 需对应一个宿主壳才可以运行)，它必须可以单独编译，单独打包成APK在手机上运行。
- 如果添加业务组件时需要依赖基础业务组件，此时为了有效隔离基础资源和数据需要考虑依赖单项目基础业务组件还是多项目共享基础业务组件。

### 3.基础业务组件(base)
基础业务组件按需依赖基础功能层的组件，该层组件是对一些系统通用的业务能力进行封装的组件。
- 例如公共业务基类：Application、BaseActivity、BaseFragment、mvp、mvvm 基类等；
- 例如分享能力，常规业务层组件只要集成该组件就能具备分享能力；
- 例如共享公共资源：layout、value、drawable、style、bean、constants、utils等；
- 例如组件路由相关：ARPath、ARHepler等；

根据是否是多项目使用以及有效隔离基础资源和数据，将该层次组件分为单项目基础业务组件和多项目共享基础业务组件：
- 单项目基础业务组件需要依赖多项目共享基础业务组件，每个项目会有自己的单项目基础组件
- 公共基础业务组件只能存放一些全公司所有项目中都需要的数据

### 4.基础功能组件(lib)
基础功能组件都是最基础的功能，通常它不包含任何业务逻辑，也可以说这些组件是一些通用的工具类和一些第三方库。
- 例如日志记录组件，它只是提供了日志记录的能力，你要记录什么样的日志，它并不关心；
- 例如基础UI组件，它是一个全局通用的UI资源库；
- 例如图片加载组件，它是一个全局通用图片加载框架；
- 例如网络服务组件，它封装了网络的请求能力；

### 5.各层组件依赖关系图
<img src="image/component_arch_summary.png"/>

### 6 骑士组件化工程架构图 V1.0

<img src="image/component_arch_boss.png"/>


### 7 骑士组件化工程架构图 V2.0

<img src="image/component_arch_boss_v2.png"/>

### 8 [骑士组件化工程页面导航思维导图](https://www.processon.com/view/link/5ee9b4005653bb29259f9197)



# 2. 组件创建

创建一个组件，有两个来源：
- 从原项目拆分出来创建的组件；
- 为新功能创建一个新组件；



### 1. 从原有代码中拆分出的新组件

把以前所有写在app/的代码，全部拉出来按照功能模块新建组件，然后再以四层组件模型逐层归类处理。
- 1.宿主壳和调试壳代码在`app/`路径下新加组件
- 2.常规业务组件代码在`biz/`路径下新加组件
- 3.基础业务组件代码在`base/`路径下新加组件
- 4.基础功能组件代码在`lib/`路径下新加组件

  
### 2. 为新功能创建新组件
- 新组件功能不能与已有的组件功能100%相同，否则这不是新功能。
- 新组件功能可以与已有组件功能交互，但不能有重合。
- 新组件功能的划分要保持粒度一致。一个组件一个 Activity是允许的，只要划分的粒度和其他组件的粒度保持一致。
- 组件间独立，架构不一样是允许的。



### 3. 组件创建流程



#### 1. 添加调试壳的步骤

1. 复制一份\app\app_template 重命名为新宿主壳的名字 如: app_login
1. 修改app_login\AndroidManifest.xml 文件指定启动 Activity，配置权限等
1. 修改app_login\build.gradle文件指定要调试的组件`implementation project(path: ':biz:login')` 以及其它需要的配置信息，引入公共的gradle配置文件 `apply from: "../../app_components.gradle" `
1. 在settings.gradle文件中添加引用路径： `include ':app:app_login' `同步代码，运行单个组件调试

>  调试壳组件主要包含`AndroidManifest.xml` 和 `build.gradle`两个文件，目前只能通过代码复制来创建其它调试壳，后期考虑通过创建模版实现



#### 2. 添加常规业务组件的步骤

1. 点击 biz_components 目录右键\New\Module 选择Android Library 新建一个组件 如：login
1. 修改 biz_components\login\build.gradle 文件 添加需要依赖的下层依赖库或者其它第三方依赖,引入公共等gradle配置文件 `apply from: "../../biz_components.gradle" `
1. 在settings.gradle文件中修改引用路径：`include ':biz:login'`
1. 同步工程，添加组件业务功能代码，需要考虑是否多个项目是否会依赖该组件，ButterKnife在组件中的使用问题等
1. 组件开发完成，可以考虑发布aar，本地依赖或远程依赖，具体查看发布流程



#### 3. 添加基础业务组件的步骤

1. 点击 base_components 目录右键\New\Module 选择Android Library 新建一个组件 如：base_component
1. 修改 base_components\base_component\build.gradle 文件 添加需要依赖的下层依赖库或者其它第三方依赖
1. 在settings.gradle文件中修改引用路径：`include ':base:base_component'`
1. 同步工程，添加组件基础业务功能，需要考虑是否多个项目会依赖该组件，ButterKnife在组件中的使用问题
1. 组件开发完成，可以考虑发布aar，本地依赖或远程依赖，具体查看发布流程

> - base_component 为所有项目提供基础业务功能
> - base_component_boss 只为 骑士 这个项目提供基础业务功能
> - base_component_owner 只为 当家 这个项目提供基础业务功能
> - base_component_owner 和 base_component_boss 都需要依赖 base_component，目的是在多项目开发中隔离各自项目的基础资源
> - 常规业务组件如果不需要基础业务数据也可以不用依赖base_component系列的基础业务组件，



#### 4. 添加基础功能组件的步骤

1. 点击 base_libs 目录右键\New\Module 选择Android Library 新建一个组件 如：base_lib
1. 修改 base_libs\base_lib\build.gradle 文件 添加需要依赖其它第三方依赖，引入公共等gradle配置文件 `apply from: "../../lib_common.gradle" `
1. 在settings.gradle文件中修改引用路径：`include ':lib:base_lib'`
1. 同步工程，添加组件功能相关代码
1. 组件开发完成，可以考虑发布aar，本地依赖或远程依赖，具体查看发布流程



# 3. 组件开发


### 1.单组件调试

- 组件只允许为库模式 即`apply plugin: 'com.android.library'`
- 在库模式的基础上套一个空壳，以间接实现组件的独立模式。
- 壳只由两个个文件构成。build.gradle 和 AndroidManifest.xml，事实上，壳可以写个模板文件夹，需要的时候复制粘贴即可。可以在build.gradle里声明AndroidManifest.xml放在同一级目录下，不用创建多余的文件夹。
    - 修改 build.gradle 声明塞进哪些组件，
    - 修改 AndroidManifest.xml 声明入口 Activity



### 2.多组件调试

可以一个组件一个壳，两个组件一个壳，想调试多少组件就把它们都塞进一个壳。切换壳的过程不需要 Gradle Sync，直接指定哪个壳直接 Build 就行。



### 3.组件内的 Application 生命周期注入

目前是反射实现，实现方式查看项目中的 `ApplicationDelegate.kt`以后找到更合适的方法可以替换

`ApplicationDelegate` 应用生命周期分发代理类使用规范
- 组件中声明一个实现IAppLife接口的组件App类如：`class BossApp : IAppLife`
- 在组件的 AndroidManifest.xml 文件中  <application> 节点下添加以下注册代码，用于筛选哪些组件注册了APP生命周期以及通过完整类名反射调用该类
    ```
   <application>
    ....
      <!--appLife ,name需要是全类名,value为固定值用于筛选 -->
        <meta-data
            android:name="com.quhuo.boss.application.BossApp"
            android:value="IModuleConfig" />
   </application>       
   ```
- 设置该appLife的优先级，必须设置，否则不会执行分发，下层组件使用高优先级
    ```
        /**
         * 低优先级
         */
        const val LOW = "LOW"
        /**
         * 中优先级
         */
        const val MEDIUM = "MEDIUM"
        /**
         * 高优先级
         */
        const val HIGH = "HIGH"
    ```



目前项目中的Application分发类有：

com.qlife.lib_base.base_lib.app.BaseLibApp

com.qlife.base_component.app.BaseComponentApp

com.qlife.base_web.app.BaseWebApp

com.quhuo.boss.application.BossApp

com.quhuo.boss.application.OwnerApp



### 4.组件间交互

组件间数据传递：EventBus/ARouter-Service
组件间函数调用：ARouter-Service
组件间界面跳转：ARouter
组件间 UI 混合：ARouter-Service

参考类
ARouterService

ARHelper



### 5.依赖管理

#### 1. Gradle 依赖方法介绍

升级 gradle 到 3.0+，使用 `implementation`, `api`, `runtimeOnly`, `compileOnly` 进行依赖控制。

- implementation:短依赖。我的依赖的依赖不是我的依赖。
- api: 长依赖。我的依赖，以及我的依赖的依赖，都是我的依赖。
- runtimeOnly:不合群依赖。写代码和编译时不会参与，只在生成 APK 时被打包进去。
- compileOnly:假装依赖。只在写代码和编译时有效，不会参与打包。

> 假设 A 依赖 B，B 依赖 C。
> - 如果 B 对 C 使用 implementation 依赖，则 A 无法调用 C 的代码
> - 如果 B 对 C 使用 api 依赖，则 A 可以调用 C 的代码
> - 如果 B 对 C 使用 compileOnly 依赖，则 A 无法调用 C 的代码，且 C 的代码不会被打包到 APK 中
> - 如果 B 对 C 使用 runtimeOnly 依赖，则 A、B 无法调用 C 的代码，但 C 的代码会被打包到 APK 中


####  2. 依赖方法使用规范
- implementation: 用于组件范围内的依赖，不与其他组件共享。（作用域是组件内）
- api: 用于基础层的依赖，要穿透基础层，暴露给上层组件层。（作用域是所有）
- runtimeOnly: 用于 app 宿主壳的依赖，组件间是绝对隔离的，编译时不可见，但参与打包。（无作用域）
- compileOnly: 用于高频依赖，防止 already present 错误。一般是开源库用来依赖 support 库防止与客户的 support 库版本冲突的。

> `最少依赖原则`。基础层的依赖必须尽量少，不得随意下沉依赖。一个依赖能下沉，当且仅当基础层的某个库需要这个依赖。
如果这个依赖只是给业务组件用的，不是基础库需要的，就算有几十几百个组件都要这个依赖，也要分别在组件的 build.gradle 里加，一定不能下沉到基础层。



### 6.组件资源命名规范

#### 1. 组件命名规范

- 1.宿主壳调试壳：`:app:app-xxx`
- 2.常规业务组件：`:biz:biz-xxx`
- 3.基础业务组件：`:base:base-xxx`
- 4.基础功能组件：`:lib:lib-xxx`
- 组件内的资源命名需要以组件名字(如：biz_xxx_ic_back.png)为前缀命名

#### 2. 组件资源命名规范
组件的build.gradle文件中添加如下代码，强制提示采用 `组件名称_资源名称` 来命名资源文件

```
android {
    //给 Module 内的资源名增加前缀, 避免资源名冲突
    resourcePrefix "${project.name.toLowerCase().replaceAll("-", "_")}_"
 }
```

开启该规则之后以下资源文件会有错误提示但是不会影响编译

如果资源名字没有使用组件名字为前缀会有以下错误错提示

`Resource named 'xxxxxxxx' does not start with the project's resource prefix 'biz_user_'; rename to 'biz_user_ xxxxxxxxx' `

- drwable/xxx.xml
- layout/xxx.xml
- values/attrs.xml
- values/color.xml
- values/dimens.xml
- values/strings.xml
- values/style.xml



如果某个资源文件不想采用这个规范可以添加`tools:ignore="ResourceName"`忽略这个错误提示

```
<?xml version="1.0" encoding="utf-8"?>
<resources 
xmlns:tools="http://schemas.android.com/tools" 
tools:ignore="ResourceName">
// 颜色、字符串等
</resources>
```

对于组件内的图片资源没有相应的提示，需要人为控制



### 7.AndroidManifest 文件合并冲突问题

对于多Module集成合并Manifest文件规则可以参考 [合并多个清单文件用户指南](https://developer.android.google.cn/studio/build/manifest-merge)

自定义 Application 需要声明在 AndroidManifest.xml 中，每个 Module 都有该清单文件，但是最终的 APK 文件只能包含一个AndroidManifest.xml 文件
因此，在构建应用时，Gradle 构建会将所有清单文件合并到一个封装到 APK 的清单文件中。



#### 1. 合并优先级（按优先级由高到低的顺序）

1. **build variants 中的的清单文件**
2. **app中的清单文件**
3. **library中的清单文件**

合并顺序为先将库清单合并到应用清单中，然后再将主应用清单合并到构建变体清单中。



#### 2. 合并规则

如果优先级较低的清单中的某个元素与优先级较高的清单中的所有元素都不匹配，则会将该元素添加到合并后的清单中。

如果有匹配的元素，则合并工具会尝试将每个元素的所有属性合并到同一元素中。

如果该工具发现两个清单包含相同的属性，但值不同，则会发生合并冲突。例如下表最后一种情况： App Module(值 A) > Library Module(值 B) 值 A 合并值 B，会产生冲突错误：
具体合并规则如下表所示：

| 高优先级属性（app） | 低优先级属性(library) |    属性的合并结果    |
| :-----------------: | :-------------------: | :------------------: |
|       没有值        |        没有值         | 没有值（使用默认值） |
|       没有值        |         值 B          |         值 B         |
|        值 A         |        没有值         |         值 A         |
|        值 A         |         值 A          |         值 A         |
|        值 A         |         值 B          |   **合并冲突错误**   |



#### 3. 合并冲突解决(属性标记)

`tools:replace="attr, ..."` 将优先级较低的清单中的指定属性替换为此清单中的属性。换句话说，始终保留优先级较高的清单中的值。例如：高优先级的 App Module 中使用 `tools:replace="android:name,android:label` "直接用值 A 替换值 B

其它解决合并冲突的方式可参考 [官方教程](https://developer.android.google.cn/studio/build/manifest-merge)



### 8. ButterKnife 在library中使用的问题

因为Android ADT14开始，library的 R 资源不再是final类型的了，@BindView的属性必须是一个常数，所以在library中你不能使用R.id.xx，需要使用findViewById()来代替;

 在使用switch的时候就会出问题。`Resource IDs cannot be used in a switch statement in Android library modules......` 也不能使用switch(R.id.xx) 需要使用if…else来代替。

解决方案：

在lib中使用ButterKnife需要引入`butterknife-gradle-plugin` 这个插件

```
buildscript {
  repositories {
    mavenCentral()
    google()
   }
  dependencies {
    classpath 'com.jakewharton:butterknife-gradle-plugin:10.2.1'
  }
}
```

然后在需要的Library Module中引用这个插件

```
apply plugin: 'com.android.library'
apply plugin: 'com.jakewharton.butterknife'
```

最后在所有使用butterknife注释中使用R2而不是R。


```
class ExampleActivity extends Activity {
  @BindView(R2.id.user) EditText username;
  @BindView(R2.id.pass) EditText password;
...
}
```

点击事件
```
   @OnClick(
        R2.id.iv_back,
        R2.id.tv_receive_site,
    )
    fun onClick(view: View) {
        if (view.id == R.id.iv_back) {
            this.finish()
        } else if (view.id == R.id.tv_receive_site) {
            mBottomSheetDialog.show()
        }
    }
```

但是在转换R2的过程中可能会出现很多问题，所以需要考虑替代 Butterknife 的方案，并且 [Butterknife](https://github.com/JakeWharton/butterknife/) 作者已经在github上说明后期将不再更新该库，建议使用 [view binding](https://developer.android.com/topic/libraries/view-binding).
> Butter Knife
>
> Attention: This tool is now deprecated. Please switch to view binding. Existing versions will continue to work, obviously, but only critical bug fixes for integration with AGP will be considered. Feature development and general bug fixes have stopped.




### 9. 视图绑定替代方案对比



#### 1. View Binding 与 findViewById() 的比较

与使用 findViewById 相比，视图绑定具有一些很显著的优点：

- Null 安全：由于视图绑定会创建对视图的直接引用，因此不存在因视图 ID 无效而引发 Null 指针异常的风险。此外，如果视图仅出现在布局的某些配置(例如横竖屏layout-land\平台版本layout-v21)中，则绑定类中包含其引用的字段会使用 @Nullable 标记。
- 类型安全：每个绑定类中的字段均具有与它们在 XML 文件中引用的视图相匹配的类型。这意味着不存在发生类转换异常的风险。
这些差异意味着布局和代码之间的不兼容性可能会导致编译版本在编译时（而非运行时）失败，从而可以提醒开发者在使用的时候注意异常的处理。



#### 2. View Binding 与 Data Binding 库的比较

视图绑定和数据绑定库均会生成可用于直接引用视图的绑定类。不过，这两者之间存在明显差异：

- 数据绑定库仅处理使用 <layout> 代码创建的数据绑定布局。
- 数据绑定因为使用了注解处理器的原因构建速度收到影响
- 视图绑定不支持布局变量或布局表达式，因此它不能用于在 XML 中将布局与数据绑定。
- 在许多情况下，视图绑定可以提供与数据绑定相同的好处，实现更简单，性能更好。如果您主要使用数据绑定来替换findViewById()调用，那么可以考虑使用视图绑定。

#### 3. View Binding 与 Kotlin Synthtic 的比较
- Kotlin synthetics 运行良好，但是没有编译时的安全性，例如你引用了一个其它布局文件的相同id,你只会在运行时发现产生的崩溃

#### 4. View Binding 与 Butter Knife 的比较
- Butter Knife 同样没有 Null 安全和类型安全的功能，
- Butter Knife 同样因为使用了注解处理器的原因构建速度收到影响
- Butter Knife 已经停止更新，作者推荐转移到View Binding



所以以后将采用来完成视图绑定的任务，具体使用参考 [View Binding 官方文档](https://developer.android.com/topic/libraries/view-binding)



```groovy
android {

    //模块中启用ViewBinding
    //谷歌在 Android Studio 3.6 Canary 11 及更高版本中加入了新的视图绑定工具 ViewBinding。
    viewBinding {
        enabled = true
    }
}
```

```kotlin
private lateinit var binding: BizAgreementActivityAgreementBinding

    private fun onCreate() {
        binding = BizAgreementActivityAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.wvArgeement.loadUrl("")
		}
```





# 4.开发规范

### 1.组件开发规范

- 只能在一个项目中本地调试好公共组件，然后直接复制到另一个项目，或者后期会直接发布到Maven仓库 然后所有项目中直接远程依赖或更新版本号
- 当家和骑士各自的非公共组件按需依赖base_component_owner/base_component_boss 并将各自独有的基础数据统一放置在这里
- 只有上层的组件才能依赖下层组件，不能反向依赖，否则可能会出现循环依赖的情况；
- 同一层之间的组件不能相互依赖，这也是为了组件之间的彻底解耦
- 组件间的通信必须考虑没有回应的情况。也就是说，单独调试组件时，即使访问了另一个不存在的组件，也不能崩。只要处理好没有回应的情况，就把握住了组件的边界，轻松解耦，目前ARouter如果找不到组件默认也会有错误提示
- 每个模块名称前面有个 ":"，表示的是相对于当前主工程的根目录， 如果将某几个组件放在一个二级目录下需要 `:dir:module`



### 2.`ARouter` 路由使用规范
- 路由 path (页面/服务）必须以'/'开头，并且包含多于2 '/'的内容!
- ARouter中默认会用你声明的 path 第一个/后面的字符用作group，不同组件之间的group不能相同
    ```
      /**
         * group name
         */
        private const val USER = "/user"

        /**
         * 登录页面
         */
        const val LOGIN_ACTIVITY_PATH = "$USER/login/activity"
    ```



### 3.`lib_net` 网络库使用规范

- 根据不同的需求分别继承 BaseNetwork/AbstractNetwork/AuthAbstractNetwork 三个层次(每一层负责的功能不同)的网络抽象基类
- 目前现有的业务网络类：Network(base_component), BossNetwork(base_component_boss)，UMSNetwork(base_component)
- 可通过以下类图了解不同层次的功能
 <img src="image/network_arch.png"/>



### 4.`config.gradle` 文件更新规范

- 三方依赖库多个依赖路径引入同一个版本号时需要提取版本号以便与后期版本升级维护
- 引入三方依赖库时考虑该库到稳定性，可维护性，需要标明github地址并归类放置三方依赖
```
   //三方依赖库多个依赖路径引入同一个版本号时需要提取版本号
    versionArray = [
            retrofitVersion = '2.8.1',
            okhttpVersion = '4.5.0',
    ]
    //okhttp https://github.com/square/okhttp
    okhttp = "com.squareup.okhttp3:okhttp:$okhttpVersion",
    //okhttp logging-interceptor https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md
    okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okhttpVersion",
    
```

- 签名信息

- 环境信息

  def currentPropertyType = dev //切换环境

- 

# 5. 组件发布



## 1. 公共组件如何实现多项目共享

### 方案一：将moudle放在本地指定目录实现多项目复用

[参考文章](https://code.luasoftware.com/tutorials/android/create-android-library-to-be-shared-with-multiple-projects/)

这种方案存在的问题：
- 1，多项目引用同一个moudle导致项目结构混乱不易维护
- 2，交叉编译过程中会出现另一个项目的文件，导致编译不通过，需要清理.idea文件夹重新打开(需要验证)

### 方案二：复制粘贴组件

这种方案存在的问题：
- 1，通过人工方式将一个组件等源码导入到另一个项目，不够高效
- 2，需要维护多份相同组件等源码

### 方案三：每个人搭建本地Maven仓库，用git实现本地仓库的同步
[参考文章](https://www.jianshu.com/p/8d7d0cc8fcc3)

这种方案存在的问题：
- 1，需要另一个git仓库来管理这个本地Maven仓库来保持多人同步
- 2，每次更新需要版本管理依赖
- 3，项目中的仓库对应的本地module也需要保留方便调试

### 方案四：搭建公司私有Maven仓库，依靠公司服务保持组件同步
[参考文章](https://blog.csdn.net/qq_33224330/article/details/80135606)

这种方案存在的问题：
- 1，每次更新需要版本管理依赖
- 2，项目中的仓库对应的本地module也需要保留方便调试

### 方案五：使用Git中submodule实现

 [参考文章](https://zhuanlan.zhihu.com/p/87053283)

Git 工具的 submodule 功能就是建立了当前项目与子模块之间的依赖关系
- 对于子模块而言，并不需要知道引用自己的主项目的存在。对于自身来讲，子模块就是一个完整的 Git 仓库，按照正常的 Git 代码管理规范操作即可。
- 对于主项目而言，子模块的内容发生的变动对主项目虽然是透明的，但其无法对子模块的改动做出操作



项目目前停留在方案二的实现方式，后续需要继续调研



## 2. 组件发布Maven 私服

一个组件相对稳定后，把该组件打包成 aar，发布到 maven，在本地 setting.gradle 注释掉该组件的声明，用 arr 替换原来的，也可大幅减少编译时间和运行内存占用。

在组件的build.gradle加入下面这段，sync后执行gradle uploadArchives实现自动打包出 arr 到指定Maven 仓库

```
/**
 * 每个发布的组件需要单独配置的信息
 */
// 唯一标识
group "com.qlife.${project.name}"
// 版本号
version "1.0.0"
apply from: "../../buildgradle/maven_publish.gradle"
```



## 3. 组件上传Maven规范

- 本地代码要保留在项目中以备下次更新
- 需要提供 `CHANGELOG.md` 记录更新记录
- 可以将每个组件的 `CHANGELOG.md` 统一管理在一个git仓库便于多人协作是同步组件版本



# 6. 后期计划

- 组件共享方式需要继续调研（要考虑开发效率，易维护，稳定性等多种因素） 
- 清理冗余代码
- 骑士主组件(boss)按照已有组件化开发规范细分，待拆分组件后续整理
- 当家主组件(owner)按照已有组件化开发规范细分，待拆分组件后续整理
- 集成MVVM框架，添加多架构支持



# 7. 其它问题



## 1，其它流程上点问题

- Tinker的在组件化改造中的影响（待测试）
- 由于组件在组件化改造阶段频繁更新，所以，将组件发布到Maven仓库暂时不考虑，待组件稳定时考虑搭建公司Maven私服以及本地modul和aar依赖切换逻辑
- 组件共享方式需要继续调研（要考虑开发效率，易维护，稳定等多因素）但是目前的方式并不影响后期采用的任何共享方式，因为组件一旦抽取出来采用至于用哪种依赖方式是很容易替换的




