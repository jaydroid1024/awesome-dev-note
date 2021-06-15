## Android 增效开发工具-注解处理器从入门到实战

注解处理器是一个在javac编译期处理注解的工具，你可以创建注解处理器并注册，在编译期你创建的处理器以Java代码作为输入，生成文件.java文件作为输出。
注意：注解处理器不能修改已经存在的Java类（即不能向已有的类中添加方法）。只能生成新的Java类。









## 一些著名的引入注解处理功能的开源库

| Annotation Processor                                         | 增量编译支持的版本                                           | Details                                                      |
| :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| [Auto Value](https://github.com/google/auto)                 | [1.6.3](https://github.com/google/auto/releases/tag/auto-value-1.6.3) | N/A                                                          |
| [Auto Service](https://github.com/google/auto)               | [1.6.3](https://github.com/google/auto/releases/tag/auto-value-1.6.3) | N/A                                                          |
| [Auto Value extensions](https://github.com/google/auto)      | Partly supported.                                            | [Details in issue](https://github.com/google/auto/issues/673) |
| [Butterknife](https://github.com/JakeWharton/butterknife)    | [10.2.0](https://github.com/JakeWharton/butterknife/commit/2acac62c7354fee46a5201d50a4712732f6dd1ed) | N/A                                                          |
| [Lombok](https://github.com/rzwitserloot/lombok)             | [1.16.22](https://github.com/rzwitserloot/lombok/releases/tag/v1.16.22) | N/A                                                          |
| DataBinding                                                  | [AGP 3.5.0-alpha5](https://issuetracker.google.com/issues/110061530#comment28) | Hidden behind a feature toggle                               |
| Dagger                                                       | [2.18](https://github.com/google/dagger/issues/1120)         | 2.18 Feature toggle support, 2.24 Enabled by default         |
| kapt                                                         | [1.3.30](https://youtrack.jetbrains.com/issue/KT-23880)      | Hidden behind a feature toggle                               |
| Toothpick                                                    | [2.0](https://github.com/stephanenicolas/toothpick/pull/320) | N/A                                                          |
| Glide                                                        | [4.9.0](https://github.com/bumptech/glide/releases/tag/v4.9.0) | N/A                                                          |
| Android-State                                                | [1.3.0](https://github.com/evernote/android-state/releases/tag/v1.3.0) | N/A                                                          |
| Parceler                                                     | [1.1.11](https://github.com/johncarl81/parceler/releases/tag/parceler-project-1.1.11) | N/A                                                          |
| Dart and Henson                                              | [3.1.0](https://github.com/f2prateek/dart/releases/tag/3.1.0) | N/A                                                          |
| [MapStruct](https://github.com/mapstruct/mapstruct)          | [1.4.0.Beta1](https://github.com/mapstruct/mapstruct/releases/tag/1.4.0.Beta1) | N/A                                                          |
| [Assisted Inject](https://github.com/square/AssistedInject)  | [0.5.0](https://github.com/square/AssistedInject/blob/master/CHANGELOG.md#version-050-2019-08-08) | N/A                                                          |
| [Realm](https://github.com/realm/realm-java)                 | [5.11.0](https://github.com/realm/realm-java/blob/v5.11.0/CHANGELOG.md) | N/A                                                          |
| Requery                                                      | [Open issue](https://github.com/requery/requery/issues/773)  | N/A                                                          |
| [EventBus](https://github.com/greenrobot/EventBus)           | [3.2.0](https://github.com/greenrobot/EventBus/releases/tag/V3.2.0) | N/A                                                          |
| EclipseLink                                                  | [Open issue](https://bugs.eclipse.org/bugs/show_bug.cgi?id=535985) | N/A                                                          |
| [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher) | [4.2.0](https://github.com/permissions-dispatcher/PermissionsDispatcher/releases/tag/4.2.0) | N/A                                                          |
| Immutables                                                   | [Open issue](https://github.com/immutables/immutables/issues/804) | N/A                                                          |
| [Room](https://developer.android.com/topic/libraries/architecture/room) | [2.2.0](https://developer.android.com/jetpack/androidx/releases/room#version_220_3) | 2.2.0 Feature toggle support, 2.3.0-alpha02 Enabled by default |
| [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) | [2.2.0-alpha02](https://issuetracker.google.com/issues/129115778) | N/A                                                          |
| [AndroidAnnotations](https://github.com/androidannotations/androidannotations) | [4.7.0](https://github.com/androidannotations/androidannotations/wiki/ReleaseNotes#4.7.0) | N/A                                                          |
| DBFlow                                                       | [Open issue](https://github.com/agrosner/DBFlow/issues/1648) | N/A                                                          |
| AndServer                                                    | [Open issue](https://github.com/yanzhenjie/AndServer/issues/152) | N/A                                                          |
| [Litho](https://github.com/facebook/litho)                   | [0.25.0](https://github.com/facebook/litho/blob/master/CHANGELOG.md#version-0250) | N/A                                                          |
| [Moxy](https://github.com/moxy-community/Moxy/)              | [2.0](https://github.com/moxy-community/Moxy/releases/tag/2.0.0) | N/A                                                          |
| [Epoxy](https://github.com/airbnb/epoxy)                     | [4.0.0-beta1](https://github.com/airbnb/epoxy/releases/tag/4.0.0-beta1) | N/A                                                          |
| [JPA Static Metamodel Generator](https://docs.jboss.org/hibernate/orm/5.4/topical/html_single/metamodelgen/MetamodelGenerator.html) | [5.4.11](https://github.com/hibernate/hibernate-orm/releases/tag/5.4.11) | N/A                                                          |
| [DeepLinkDispatch](https://github.com/airbnb/DeepLinkDispatch) | [5.0.0-beta01](https://github.com/airbnb/DeepLinkDispatch/releases/tag/5.0.0-beta01) | Hidden behind a feature toggle                               |
| [Shortbread](https://github.com/MatthiasRobbers/shortbread)  | [1.1.0](https://github.com/MatthiasRobbers/shortbread/releases/tag/v1.1.0) | N/A                                                          |



### 参考：

- [Oracle | AbstractProcessor 官方文档](https://docs.oracle.com/javase/7/docs/api/javax/annotation/processing/AbstractProcessor.html)
- [Oracle | Element API 中文版](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/Element.html)
- [Annotation Processing 101](http://hannesdorfmann.com/annotation-processing/annotationprocessing101/)
- [Annotation-Processing-Tool详解](https://www.open-open.com/lib/view/open1470735314518.html)
- [Gradle | Incremental annotation processing ](https://docs.gradle.org/current/userguide/java_plugin.html#sec:incremental_annotation_processing)
- [Gradle | State of support in popular annotation processors](https://docs.gradle.org/current/userguide/java_plugin.html#state_of_support_in_popular_annotation_processors)
- [StackOverflow | How to make my own annotation processor incremental?](https://stackoverflow.com/questions/58966094/how-to-make-my-own-annotation-processor-incremental)

- [Kotlin | Using kapt](https://kotlinlang.org/docs/kapt.html)

- [Medium | Making incremental KAPT work (Speed Up your Kotlin projects!)](https://medium.com/@daniel_novak/making-incremental-kapt-work-speed-up-your-kotlin-projects-539db1a771cf)

- [Android Developer | 优化构建速度](https://developer.android.com/studio/build/optimize-your-build)

- apt 实战项目系列
  - https://www.baeldung.com/java-annotation-processing-builder
  - [Better Analytics in Android with Annotation Processing and KotlinPoet](https://medium.com/wantedly-engineering/better-analytics-in-android-with-annotation-processing-and-kotlinpoet-bffca3f24c37)
  - [Google Git | Lifecycle](https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-arch-core-release/lifecycle?autodive=0)
  - [Google GIt | Room](https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-arch-core-release/room?autodive=0%2F%2F%2F%2F%2F)
  - [Github | Router](https://github.com/alibaba/ARouter)

- apt 辅助工具系列
  - [Github |gradle-incap-helper：用于构建增量注释处理器的辅助库](https://github.com/tbroyer/gradle-incap-helper)
  - [Github | auto-service：用于自动生成  ServiceLoader 所需的 META-INF 元数据注册文件 ](https://github.com/google/auto/tree/master/service)
  - [Github | javapoet：用于生成 .java 源文件的 Java API](https://github.com/square/javapoet)
  - [Github | kotlinpoet：用于生成 .kt 源文件的 Kotlin 和 Java API](https://github.com/square/kotlinpoet)
  - [Github | apt-utils：用于快速开发注解处理程序的辅助工具类](https://github.com/jaydroid1024/base-dev-apt)

