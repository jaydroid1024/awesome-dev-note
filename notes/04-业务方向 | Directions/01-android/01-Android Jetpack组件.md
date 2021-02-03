

# Android Jetpack 组件



## 数据绑定库（DataBinding）

- [Android开发者 ｜ 数据绑定库概览](https://developer.android.com/topic/libraries/data-binding?hl=zh-cn)

- [示例 | Android 数据绑定库示例](https://github.com/android/databinding-samples)

- [Codelab | Android 数据绑定 Codelab](https://codelabs.developers.google.com/codelabs/android-databinding?hl=zh-cn)

- [博文 | 数据绑定 - 经验教训](https://medium.com/androiddevelopers/data-binding-lessons-learnt-4fd16576b719)



### 开发环境

**Android 4.0（API 级别 14）：**数据绑定库不但灵活，而且兼容性广，它是一个支持库，因此您可以在运行 Android 4.0（API 级别 14）或更高级别的设备上使用它。

 **Android Plugin for Gradle 1.5.0+ ：**建议您在项目中使用最新的 Android Plugin for Gradle。不过，1.5.0 版及更高版本支持数据绑定。

### 编译环境

要开始使用数据绑定，请从 Android SDK 管理器中的**支持代码库**下载该库。

要将应用配置为使用数据绑定，请在应用模块的 `build.gradle` 文件中添加 `dataBinding` 元素，

```groovy
android {
        ...
        dataBinding {
            enabled = true
        }
}
```

**注意**：即使应用模块不直接使用数据绑定，也必须为依赖于使用数据绑定的库的应用模块配置数据绑定。

### Android Studio 对数据绑定的支持

Android Studio 支持许多用于修改数据绑定代码的功能。例如，它支持用于数据绑定表达式的以下功能：

- 语法突出显示
- 标记表达式语言语法错误
- XML 代码完成
- 包括[导航](https://www.jetbrains.com/help/idea/2017.1/navigation-in-source-code.html)（如导航到声明）和[快速文档](https://www.jetbrains.com/help/idea/2017.1/viewing-inline-documentation.html)在内的引用

**注意**：数组和[通用类型](https://docs.oracle.com/javase/tutorial/java/generics/types.html)（如 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 类）可能会不正确地显示错误。

**Layout Editor** 中的 **Preview** 窗格显示数据绑定表达式的默认值（如果提供）。例如，**Preview** 窗格会在以下示例声明的 `TextView` 微件上显示 `my_default` 值：如果您只需要在项目的设计阶段显示默认值，则可以使用 `tools` 属性，而不是默认表达式值。

```xml
<TextView 
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@{user.firstName, default=my_default}"/>
```



### 布局和绑定表达式

数据绑定库会自动生成将布局中的视图与您的数据对象绑定所需的类。

数据绑定布局文件略有不同，以根标记 `layout` 开头，后跟 `data` 元素和 `view` 根元素(view/viewgroup)

```xml
<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android">
       <data>
           <variable name="user" type="com.example.User"/>
       </data>
       <LinearLayout
           android:orientation="vertical"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.firstName}"/>
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.lastName}"/>
       </LinearLayout>
    </layout>
    
```

`data` 中的 `user` 变量描述了可在此布局中使用的属性。

布局中的表达式使用“`@{}`”语法将数据写入控件的特性属性中。在这里，`TextView` 文本被设置为 `user` 变量的 `firstName` 属性：

**注意**：布局表达式应保持精简，因为它们无法进行单元测试，并且拥有的 IDE 支持也有限。为了简化布局表达式，可以使用自定义[绑定适配器](https://developer.android.com/topic/libraries/data-binding/binding-adapters?hl=zh-cn)。

### 数据对象

现在我们假设您有一个 plain-old 对象来描述 `User` 实体：

> POJO或“Plain Old Java Object”是用于描述“普通”Java对象的名称

```kotlin
    data class User(val firstName: String, val lastName: String)
```

### 绑定数据

系统会为每个布局文件生成一个绑定类。默认情况下，类名称基于布局文件的名称，它会转换为 Pascal 大小写形式并在末尾添加 Binding 后缀。以上布局文件名为 `activity_main.xml`，因此生成的对应类为 `ActivityMainBinding`。此类包含从布局属性（例如，`user` 变量）到布局视图的所有绑定，并且知道如何为绑定表达式指定值。建议的绑定创建方法是在扩充布局时创建，如以下示例所示：

> **Pascal 大小写形式（帕斯卡命名法）**指当变量名和函式名称是由二个或二个以上单词连结在一起，每个单词)首字母大写。而构成的唯一识别字时，用以增加变量和函式的可读性。

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_main)

        binding.user = User("Test", "User")
    }

    
```

或者，您可以使用 `LayoutInflater` 获取视图，如以下示例所示：

```kotlin
    val binding: ActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater())
```

如果您要在 `Fragment`、`ListView` 或 `RecyclerView` 适配器中使用数据绑定项，您可能更愿意使用绑定类或 [`DataBindingUtil`](https://developer.android.com/reference/androidx/databinding/DataBindingUtil?hl=zh-cn) 类的 [`inflate()`](https://developer.android.com/reference/androidx/databinding/DataBindingUtil?hl=zh-cn#inflate(android.view.LayoutInflater, int, android.view.ViewGroup, boolean, android.databinding.DataBindingComponent)) 方法，如以下代码示例所示：

```kotlin
    val listItemBinding = ListItemBinding.inflate(layoutInflater, viewGroup, false)
    // or
    val listItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false)
```



演示代码

```kotlin
BizJetpackActivityDataBindingBinding.java
位置：build/generated/data_binding_base_class_source_out/debug/out/包名/databinding/BizJetpackActivityDataBindingBinding.java
作用：持有含有id的View的引用 / 持有绑定的类的引用

BizJetpackActivityDataBindingBindingImpl.java
位置：build/generated/source/kapt/debug/包名/databinding/BizJetpackActivityDataBindingBindingImpl.java
作用：持有没有id的View的引用 / 具体实现了绑定

BR.java
位置：/build/generated/source/kapt/debug/包名/BR.java
作用：BR文件储存了引入的数据类的id， 功能与R文件类似

DataBinderMapperImpl.java
位置： /build/generated/source/kapt/debug/包名/DataBinderMapperImpl.java
作用： 这个类主要是提供了从布局文件layoutId到ViewDataBinding类对象的映射，主要是用于在加载Layout返回对应的ViewDataBinding对象。

流程分析
DataBindingUtil.setContentView（)
activity.setContentView(layoutId);
bindToAddedViews()//判断布局是否有多个子布局，如果有则遍历存入View数组，最后调用不同参数的bind方法
DataBinderMapperImpl-getDataBinder() // 获得对应Layout的VIewDataBinding类实例
...
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="user"
            type="com.jay.biz_jetpack.databinding.data.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        android:padding="20dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="bindViewWithDataBinding"
            android:textSize="23sp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.firstName}"
            android:textSize="33sp"
            tools:text="firstName" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.lastName}"
            android:textSize="33sp"
            tools:text="lastName" />
    </LinearLayout>
</layout>


```

```kotlin
@Route(path = ARPath.PathJetPack.DATA_BINDING_ACTIVITY_PATH)
class DataBindingMainActivity : AppCompatActivity() {

    private val user = User("Jay-002", "Droid")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.biz_jetpack_activity_data_binding)

        bindViewWithDataBinding()
    }

    private fun bindViewWithDataBinding() {
        //BizJetpackActivityDataBindingBinding 类的生成路径
        ///build/generated/data_binding_base_class_source_out/debug/out/com/jay/biz_jetpack/databinding/BizJetpackActivityDataBindingBinding.java
        val binding: BizJetpackActivityDataBindingBinding =
            DataBindingUtil.setContentView(this, R.layout.biz_jetpack_activity_data_binding)
        binding.user = user

    }
}
```

BizJetpackActivityDataBindingBinding 编译时自动生成的类

```java
// Generated by data binding compiler. Do not edit!
package com.jay.biz_jetpack.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.jay.biz_jetpack.R;
import com.jay.biz_jetpack.databinding.data.User;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class BizJetpackActivityDataBindingBinding extends ViewDataBinding {
  @Bindable
  protected User mUser;

  protected BizJetpackActivityDataBindingBinding(Object _bindingComponent, View _root,
      int _localFieldCount) {
    super(_bindingComponent, _root, _localFieldCount);
  }

  public abstract void setUser(@Nullable User user);

  @Nullable
  public User getUser() {
    return mUser;
  }

  @NonNull
  public static BizJetpackActivityDataBindingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.biz_jetpack_activity_data_binding, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static BizJetpackActivityDataBindingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<BizJetpackActivityDataBindingBinding>inflateInternal(inflater, R.layout.biz_jetpack_activity_data_binding, root, attachToRoot, component);
  }

  @NonNull
  public static BizJetpackActivityDataBindingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.biz_jetpack_activity_data_binding, null, false, component)
   */
  @NonNull
  @Deprecated
  public static BizJetpackActivityDataBindingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<BizJetpackActivityDataBindingBinding>inflateInternal(inflater, R.layout.biz_jetpack_activity_data_binding, null, false, component);
  }

  public static BizJetpackActivityDataBindingBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static BizJetpackActivityDataBindingBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (BizJetpackActivityDataBindingBinding)bind(component, view, R.layout.biz_jetpack_activity_data_binding);
  }
}

```

BizJetpackActivityDataBindingBindingImpl 编译时自动生成

```java
package com.jay.biz_jetpack.databinding;
import com.jay.biz_jetpack.R;
import com.jay.biz_jetpack.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class BizJetpackActivityDataBindingBindingImpl extends BizJetpackActivityDataBindingBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = null;
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    @NonNull
    private final android.widget.TextView mboundView1;
    @NonNull
    private final android.widget.TextView mboundView2;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public BizJetpackActivityDataBindingBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 3, sIncludes, sViewsWithIds));
    }
    private BizJetpackActivityDataBindingBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.TextView) bindings[1];
        this.mboundView1.setTag(null);
        this.mboundView2 = (android.widget.TextView) bindings[2];
        this.mboundView2.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.user == variableId) {
            setUser((com.jay.biz_jetpack.databinding.data.User) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setUser(@Nullable com.jay.biz_jetpack.databinding.data.User User) {
        this.mUser = User;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.user);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        java.lang.String userFirstName = null;
        com.jay.biz_jetpack.databinding.data.User user = mUser;
        java.lang.String userLastName = null;

        if ((dirtyFlags & 0x3L) != 0) {



                if (user != null) {
                    // read user.firstName
                    userFirstName = user.getFirstName();
                    // read user.lastName
                    userLastName = user.getLastName();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, userFirstName);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView2, userLastName);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): user
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}
```

### 表达式语言

#### 导入、变量和包含

数据绑定库提供了诸如导入、变量和包含等功能。

通过导入功能，您可以轻松地在布局文件中引用类。

通过变量功能，您可以描述可在绑定表达式中使用的属性。

通过包含功能，您可以在整个应用中重复使用复杂的布局。

##### 导入

通过导入功能，您可以轻松地在布局文件中引用类，就像在托管代码中一样。您可以在 `data` 元素使用多个 `import` 元素，也可以不使用。以下代码示例将 `View` 类导入到布局文件中：

```xml
<data>
        <import type="android.view.View"/>
</data>
```

导入 `View` 类可让您通过绑定表达式引用该类。以下示例展示了如何引用 `View` 类的 `VISIBLE` 和 `GONE` 常量：

```xml
   android:visibility="@{user.isAdult ? View.VISIBLE : View.GONE}"
```

使用别名重命名

当类名有冲突时，其中一个类可使用别名重命名。以下示例将 `com.example.real.estate` 软件包中的 `View` 类重命名为 `Vista`：

您可以在布局文件中使用 `Vista` 引用 `com.example.real.estate.View`，使用 `View` 引用 `android.view.View`。

```xml
<import type="android.view.View"/>
<import type="com.example.real.estate.View" alias="Vista"/>
```



导入的类型可用作变量和表达式中的类型引用。以下示例显示了用作变量类型的 `User` 和 `List`：

```xml
<data>
        <import type="com.example.User"/>
        <import type="java.util.List"/>
        <variable name="user" type="User"/>
        <variable name="userList" type="List&lt;User>"/>
</data>
```

**注意**：Android Studio 尚不处理导入，因此导入变量的自动填充功能可能无法在您的 IDE 中使用。您的应用仍可以编译，并且您可以通过在变量定义中使用完全限定名称来解决这个 IDE 问题。

您还可以使用导入的类型来对表达式的一部分进行类型转换。以下示例将 `connection` 属性强制转换为类型 `User`：

```xml
       android:text="@{((User)(user.connection)).lastName}"
```

在表达式中引用静态字段和方法时，也可以使用导入的类型。以下代码会导入 `MyStringUtils` 类，并引用其 `capitalize` 方法：

就像在托管代码中一样，系统会自动导入 `java.lang.*`。

```xml
		<data>
        <import type="com.example.MyStringUtils"/>
        <variable name="user" type="com.example.User"/>
    </data>
    …
    <TextView
       android:text="@{MyStringUtils.capitalize(user.lastName)}"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>
```

##### 变量

您可以在 `data` 元素中使用多个 `variable` 元素。每个 `variable` 元素都描述了一个可以在布局上设置、并将在布局文件中的绑定表达式中使用的属性。以下示例声明了 `user`、`image` 和 `note` 变量：

```xml
<data>
        <import type="android.graphics.drawable.Drawable"/>
        <variable name="user" type="com.example.User"/>
        <variable name="image" type="Drawable"/>
        <variable name="note" type="String"/>
</data>
```

变量类型在编译时进行检查，因此，如果变量实现 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 或者是[可观察集合](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_collections)，则应反映在类型中。如果该变量是不实现 `Observable` 接口的基类或接口，则变量是“不可观察的”。

如果不同配置（例如横向或纵向）有不同的布局文件，则变量会合并在一起。这些布局文件之间不得存在有冲突的变量定义。

在生成的绑定类中，每个描述的变量都有一个对应的 setter 和 getter。在调用 setter 之前，这些变量一直采用默认的托管代码值，

系统会根据需要生成名为 `context` 的特殊变量，用于绑定表达式。`context` 的值是根视图的 `getContext()` 方法中的 `Context` 对象。`context` 变量会被具有该名称的显式变量声明替换。

##### 包含

通过使用应用命名空间和特性中的变量名称，变量可以从包含的布局传递到被包含布局的绑定。以下示例展示了来自 `name.xml` 和 `contact.xml` 布局文件的被包含 `user` 变量：

```xml
<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:bind="http://schemas.android.com/apk/res-auto">
       <data>
           <variable name="user" type="com.example.User"/>
       </data>
       <LinearLayout
           android:orientation="vertical"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
           <include layout="@layout/name" bind:user="@{user}"/>
           <include layout="@layout/contact" bind:user="@{user}"/>
       </LinearLayout>
    </layout>
    
```

数据绑定不支持 include 作为 merge 元素的直接子元素。例如，以下布局不受支持：

```xml
<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:bind="http://schemas.android.com/apk/res-auto">
       <data>
           <variable name="user" type="com.example.User"/>
       </data>
      
      <!-- Doesn't work -->
       <merge>
           <include layout="@layout/name" bind:user="@{user}"/>
           <include layout="@layout/contact"  bind:user="@{user}"/>
       </merge>
    </layout>
    
```



#### 常见表达式

表达式语言与托管代码中的表达式非常相似。您可以在表达式语言中使用以下运算符和关键字：

- 算术运算符 `+ - / * %`
- 字符串连接运算符 `+`
- 逻辑运算符 `&& ||`
- 二元运算符 `& | ^`
- 一元运算符 `+ - ! ~`
- 移位运算符 `>> >>> <<`
- 比较运算符 `== > < >= <=`（请注意，`<` 需要转义为 `<`）
- `instanceof`
- 分组运算符 `()`
- 字面量运算符 - 字符、字符串、数字、`null`
- 类型转换
- 方法调用
- 字段访问
- 数组访问 `[]`
- 三元运算符 `?:`
- Null 合并运算符 如果左边运算数不是 `null`，则 Null 合并运算符 (`??`) 选择左边运算数，如果左边运算数为 `null`，则选择右边运算数。

例如：

```xml
		android:text="@{String.valueOf(index + 1)}"
    android:visibility="@{age > 13 ? View.GONE : View.VISIBLE}"
    android:transitionName='@{"image_" + id}'
		//Null 合并运算符 ??
		android:text="@{user.displayName ?? user.lastName}"
		//三元运算符 ?:
		android:text="@{user.displayName != null ? user.displayName : user.lastName}"
    //表达式可以使用以下格式在类中引用属性，这对于字段、getter 和 ObservableField 对象都一样：
		android:text="@{user.lastName}"

    
```

####  不支持的表达式

您可以在托管代码中使用的表达式语法中缺少以下运算：

- `this`
- `super`
- `new`
- 显式泛型调用

#### 避免出现 Null 指针异常

生成的数据绑定代码会自动检查有没有 `null` 值并避免出现 Null 指针异常。例如，在表达式 `@{user.name}` 中，如果 `user` 为 Null，则为 `user.name` 分配默认值 `null`。如果您引用 `user.age`，其中 age 的类型为 `int`，则数据绑定使用默认值 `0`。

引用类型返回 `null`，`int` 返回 `0`，`boolean` 返回 `false`，等等。

#### 视图引用

表达式可以通过以下语法按 ID 引用布局中的其他控件：

**注意**：绑定类将 ID 转换为驼峰式大小写。

在以下示例中，`TextView` 视图引用同一布局中的 `EditText` 视图：

```xml
<EditText
        android:id="@+id/example_text"
        android:layout_height="wrap_content"
        android:layout_width="match_parent"/>
<TextView
        android:id="@+id/example_output"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@{exampleText.text}"/>
```

#### 使用集合

为方便起见，可使用 `[]` 运算符访问常见集合，例如数组、列表、稀疏列表和映射。您还可以使用 `object.key` 表示法在映射中引用值。例如，以上示例中的 `@{map[key]}` 可替换为 `@{map.key}`。

```xml
<data>
        <import type="android.util.SparseArray"/>
        <import type="java.util.Map"/>
        <import type="java.util.List"/>
        <variable name="list" type="List&lt;String>"/>
        <variable name="sparse" type="SparseArray&lt;String>"/>
        <variable name="map" type="Map&lt;String, String>"/>
        <variable name="index" type="int"/>
        <variable name="key" type="String"/>
</data>
    …
    android:text="@{list[index]}"
    …
    android:text="@{sparse[index]}"
    …
    android:text="@{map[key]}"
    
```

**注意**：要使 XML 不含语法错误，您必须转义 `<` 字符。例如：不要写成 `List<String>` 形式，而是必须写成 `List<String>`。

#### 字符串字面量

您可以使用单引号括住特性值，这样就可以在表达式中使用双引号，如以下示例所示：

```xml
android:text='@{map["firstName"]}'
```

也可以使用双引号括住特性值。如果这样做，则还应使用反单引号 ``` 将字符串字面量括起来：

```xml
android:text="@{map[`firstName`]}"
```

#### 引用资源

表达式可以使用以下语法引用应用资源：

```xml
android:padding="@{large? @dimen/largePadding : @dimen/smallPadding}"
```

您可以通过提供参数来评估格式字符串和复数形式：

```xml
android:text="@{@string/nameFormat(firstName, lastName)}"
android:text="@{@plurals/banana(bananaCount)}"
```

您可以将[属性引用](https://developer.android.com/topic/libraries/data-binding/expressions?hl=zh-cn#property_reference)和[视图引用](https://developer.android.com/topic/libraries/data-binding/expressions?hl=zh-cn#view_references)作为资源参数进行传递：

```xml
android:text="@{@string/example_resource(user.lastName, exampleText.text)}"
```

当一个复数带有多个参数时，您必须传递所有参数：

```xml
      Have an orange
      Have %d oranges
    android:text="@{@plurals/orange(orangeCount, orangeCount)}"
```

某些资源需要显式类型求值，如下表所示：

| 类型              | 常规引用  | 表达式引用         |
| :---------------- | :-------- | :----------------- |
| String[]          | @array    | @stringArray       |
| int[]             | @array    | @intArray          |
| TypedArray        | @array    | @typedArray        |
| Animator          | @animator | @animator          |
| StateListAnimator | @animator | @stateListAnimator |
| color int         | @color    | @color             |
| ColorStateList    | @color    | @colorStateList    |

#### 事件处理

事件特性名称由监听器方法的名称确定，但有一些例外情况。例如，`View.OnClickListener` 有一个 `onClick()` 方法，所以该事件的特性为 `android:onClick`。

有一些专门针对点击事件的事件处理脚本，这些处理脚本需要使用除 `android:onClick` 以外的特性来避免冲突。您可以使用以下属性来避免这些类型的冲突：

| 类             | 监听器 setter                                     | 属性                    |
| :------------- | :------------------------------------------------ | :---------------------- |
| `SearchView`   | `setOnSearchClickListener(View.OnClickListener)`  | `android:onSearchClick` |
| `ZoomControls` | `setOnZoomInClickListener(View.OnClickListener)`  | `android:onZoomIn`      |
| `ZoomControls` | `setOnZoomOutClickListener(View.OnClickListener)` | `android:onZoomOut`     |

您可以使用以下机制处理事件：

- [方法引用](https://developer.android.com/topic/libraries/data-binding/expressions?hl=zh-cn#method_references)：在表达式中，您可以引用符合监听器方法签名的方法。当表达式求值结果为方法引用时，数据绑定会将方法引用和所有者对象封装到监听器中，并在目标视图上设置该监听器。如果表达式的求值结果为 `null`，则数据绑定不会创建监听器，而是设置 `null` 监听器。
- [监听器绑定](https://developer.android.com/topic/libraries/data-binding/expressions?hl=zh-cn#listener_bindings)：这些是在事件发生时进行求值的 lambda 表达式。数据绑定始终会创建一个要在视图上设置的监听器。事件被分派后，监听器会对 lambda 表达式进行求值。

##### 方法引用

事件可以直接绑定到自定义的事件点击方法上，类似于为 Activity 中的方法指定 `android:onClick` 的方式。与 `View` `onClick` 特性相比，一个主要优点是表达式在编译时进行处理，因此，如果该方法不存在或其签名不正确，则会收到编译时错误。

方法引用和监听器绑定之间的主要区别在于实际监听器实现是在绑定数据时创建的，而不是在事件触发时创建的。如果您希望在事件发生时对表达式求值，则应使用监听器绑定。

```kotlin
  class MyHandlers {
        fun onClickFriend(view: View) { ... }
  }
```

**"@{handlers::onClickFriend}"** 绑定表达式可将视图的点击监听器分配给 `onClickFriend()` 方法，如下所示：

**注意**：表达式中的方法签名必须与监听器对象中的方法签名完全一致。

```xml
<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android">
       <data>
           <variable name="handlers" type="com.example.MyHandlers"/>
           <variable name="user" type="com.example.User"/>
       </data>
       <LinearLayout
           android:orientation="vertical"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.firstName}"
               android:onClick="@{handlers::onClickFriend}"/>
       </LinearLayout>
    </layout>
    
```

##### 监听器绑定

监听器绑定是在事件发生时运行的绑定表达式。它们类似于方法引用，但允许您运行任意数据绑定表达式。此功能适用于 Gradle 2.0 版及更高版本的 Android Gradle 插件。

在方法引用中，方法的参数必须与事件监听器的参数匹配。在监听器绑定中，只有您的返回值必须与监听器的预期返回值相匹配（预期返回值无效除外）。例如，请参考以下具有 `onSaveClick()` 方法的 presenter 类：

```
    class Presenter {
        fun onSaveClick(task: Task){}
    }
```

然后，您可以将点击事件绑定到 `onSaveClick()` 方法，如下所示： **"@{() -> presenter.onSaveClick(task)}"**

```xml
<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android">
        <data>
            <variable name="task" type="com.android.example.Task" />
            <variable name="presenter" type="com.android.example.Presenter" />
        </data>
        <LinearLayout android:layout_width="match_parent" android:layout_height="match_parent">
            <Button android:layout_width="wrap_content" android:layout_height="wrap_content"
            android:onClick="@{() -> presenter.onSaveClick(task)}" />
        </LinearLayout>
    </layout>
    
```

或者，如果您想在表达式中使用参数，则采用如下形式：**"@{(theView) -> presenter.onSaveClick(theView, task)}"**

```kotlin
  class Presenter {
        fun onSaveClick(view: View, task: Task){}
    }
```

```xml
android:onClick="@{(theView) -> presenter.onSaveClick(theView, task)}"
```

也可以在 lambda 表达式中使用多个参数：**"@{(cb, isChecked) -> presenter.completeChanged(task, isChecked)}"** 

```kotlin
  class Presenter {
        fun onCompletedChanged(task: Task, completed: Boolean){}
  }
```

```xml
	<CheckBox 
          android:layout_width="wrap_content" 
          android:layout_height="wrap_content"     
          android:onCheckedChanged="@{(cb, isChecked) -> presenter.completeChanged(task, isChecked)}" />  
```

如果您监听的事件返回类型不是 `void` 的值，则您的表达式也必须返回相同类型的值。例如，如果要监听长按事件，表达式应返回一个布尔值。

```kotlin
 class Presenter {
        fun onLongClick(view: View, task: Task): Boolean { }
 }
```

```xml
android:onLongClick="@{(theView) -> presenter.onLongClick(theView, task)}"
```

如果您需要将表达式与谓词（例如，三元运算符）结合使用，则可以使用 `void` 作为符号。

```xml
android:onClick="@{(v) -> v.isVisible() ? doSomething() : void}"
```

##### 避免使用复杂的监听器

监听器表达式功能非常强大，可以使您的代码非常易于阅读。另一方面，包含复杂表达式的监听器会使您的布局难以阅读和维护。这些表达式应该像将可用数据从界面传递到回调方法一样简单。您应该在从监听器表达式调用的回调方法中实现任何业务逻辑。

### 使用可观察的数据对象

可观察性是指一个对象将其数据变化告知其他对象的能力。通过数据绑定库，您可以让对象、字段或集合变为可观察。

任何 plain-old (POJO或“Plain Old Java Object”是用于描述“普通”Java对象的名称 ) 对象都可用于数据绑定，但修改对象不会自动使界面更新。

通过数据绑定，数据对象可在其数据发生更改时通知其他对象，当其中一个可观察数据对象绑定到界面并且该数据对象的属性发生更改时，界面会自动更新。可观察类有三种不同类型：[对象](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_objects)、[字段](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_fields)和[集合](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_collections)。

#### 可观察字段

在创建实现 [`Observable`](https://developer.android.com/reference/android/databinding/Observable?hl=zh-cn) 接口的类时要完成一些操作，但如果您的类只有少数几个属性，这样操作的意义不大。在这种情况下，您可以使用通用 [`Observable`](https://developer.android.com/reference/android/databinding/Observable?hl=zh-cn) 类和以下特定于基元的类，将字段设为可观察字段：

- [`ObservableBoolean`](https://developer.android.com/reference/android/databinding/ObservableBoolean?hl=zh-cn)
- [`ObservableByte`](https://developer.android.com/reference/android/databinding/ObservableByte?hl=zh-cn)
- [`ObservableChar`](https://developer.android.com/reference/android/databinding/ObservableChar?hl=zh-cn)
- [`ObservableShort`](https://developer.android.com/reference/android/databinding/ObservableShort?hl=zh-cn)
- [`ObservableInt`](https://developer.android.com/reference/android/databinding/ObservableInt?hl=zh-cn)
- [`ObservableLong`](https://developer.android.com/reference/android/databinding/ObservableLong?hl=zh-cn)
- [`ObservableFloat`](https://developer.android.com/reference/android/databinding/ObservableFloat?hl=zh-cn)
- [`ObservableDouble`](https://developer.android.com/reference/android/databinding/ObservableDouble?hl=zh-cn)
- [`ObservableParcelable`](https://developer.android.com/reference/android/databinding/ObservableParcelable?hl=zh-cn)

可观察字段是具有单个字段的自包含可观察对象。原语版本避免在访问操作期间封箱和开箱。如需使用此机制，请采用 Java 编程语言创建 `public final` 属性，或在 Kotlin 中创建只读属性，如以下示例所示：

```Java kotlin
    //kotlin
		class User {
        val firstName = ObservableField<String>()
        val lastName = ObservableField<String>()
        val age = ObservableInt()
    }
    //Java
    private static class User {
        public final ObservableField<String> firstName = new ObservableField<>();
        public final ObservableField<String> lastName = new ObservableField<>();
        public final ObservableInt age = new ObservableInt();
    }

```

如需访问字段值，请使用 [`set()`](https://developer.android.com/reference/android/databinding/ObservableField?hl=zh-cn#set) 和 [`get()`](https://developer.android.com/reference/android/databinding/ObservableField?hl=zh-cn#get) 访问器方法，或使用 [Kotlin 属性语法](https://kotlinlang.org/docs/reference/properties.html#declaring-properties)：

```kotlin
  	user.firstName = "Google"
    val age = user.age

    user.firstName.set("Google");
    int age = user.age.get();
```

**注意**：Android Studio 3.1 及更高版本允许用 LiveData 对象替换可观察字段，从而为您的应用提供额外的好处。如需了解详情，请参阅[使用 LiveData 将数据变化通知给界面](https://developer.android.com/topic/libraries/data-binding/architecture?hl=zh-cn#livedata)。

#### 可观察集合

某些应用使用动态结构来保存数据。可观察集合允许使用键访问这些结构。当键为引用类型（如 `String`）时，[`ObservableArrayMap`](https://developer.android.com/reference/android/databinding/ObservableArrayMap?hl=zh-cn) 类非常有用，如以下示例所示：

```kotlin
    ObservableArrayMap<String, Any>().apply {
        put("firstName", "Google")
        put("lastName", "Inc.")
        put("age", 17)
    }
    
    ObservableArrayMap<String, Object> user = new ObservableArrayMap<>();
    user.put("firstName", "Google");
    user.put("lastName", "Inc.");
    user.put("age", 17);
    
```

在布局中，可使用字符串键找到地图，如下所示：

```xml
		<data>
        <import type="android.databinding.ObservableMap"/>
        <variable name="user" type="ObservableMap<String, Object>"/>
    </data>
    …
    <TextView
        android:text="@{user.lastName}"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    <TextView
        android:text="@{String.valueOf(1 + (Integer)user.age)}"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    
```

当键为整数时，[`ObservableArrayList`](https://developer.android.com/reference/android/databinding/ObservableArrayList?hl=zh-cn) 类非常有用，如下所示：

```kotlin
    ObservableArrayList<Any>().apply {
        add("Google")
        add("Inc.")
        add(17)
    }
    
    ObservableArrayList<Object> user = new ObservableArrayList<>();
    user.add("Google");
    user.add("Inc.");
    user.add(17);
```

在布局中，可通过索引访问列表，如以下示例所示：

```xml
		<data>
        <import type="android.databinding.ObservableList"/>
        <import type="com.example.my.app.Fields"/>
        <variable name="user" type="ObservableList<Object>"/>
    </data>
    …
    <TextView
        android:text='@{user[Fields.LAST_NAME]}'
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    <TextView
        android:text='@{String.valueOf(1 + (Integer)user[Fields.AGE])}'
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    
```

#### 可观察对象

实现 [`Observable`](https://developer.android.com/reference/android/databinding/Observable?hl=zh-cn) 接口的类允许注册监听器，以便它们接收有关可观察对象的属性更改的通知。

[`Observable`](https://developer.android.com/reference/android/databinding/Observable?hl=zh-cn) 接口具有添加和移除监听器的机制，但何时发送通知必须由您决定。为便于开发，数据绑定库提供了用于实现监听器注册机制的 [`BaseObservable`](https://developer.android.com/reference/android/databinding/BaseObservable?hl=zh-cn) 类。实现 `BaseObservable` 的数据类负责在属性更改时发出通知。具体操作过程是向 getter 分配 [`Bindable`](https://developer.android.com/reference/android/databinding/Bindable?hl=zh-cn) 注释，然后在 setter 中调用 [`notifyPropertyChanged()`](https://developer.android.com/reference/android/databinding/BaseObservable?hl=zh-cn#notifypropertychanged) 方法，如以下示例所示：

```kotlin
    class User : BaseObservable() {

        @get:Bindable
        var firstName: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.firstName)
            }

        @get:Bindable
        var lastName: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.lastName)
            }
    }

    private static class User extends BaseObservable {
        private String firstName;
        private String lastName;

        @Bindable
        public String getFirstName() {
            return this.firstName;
        }

        @Bindable
        public String getLastName() {
            return this.lastName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
            notifyPropertyChanged(BR.firstName);
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
            notifyPropertyChanged(BR.lastName);
        }
    }
```

数据绑定在模块包中生成一个名为 `BR` 的类，该类包含用于数据绑定的资源的 ID。在编译期间，[`Bindable`](https://developer.android.com/reference/android/databinding/Bindable?hl=zh-cn) 注释会在 `BR` 类文件中生成一个条目。如果数据类的基类无法更改，[`Observable`](https://developer.android.com/reference/android/databinding/Observable?hl=zh-cn) 接口可以使用 [`PropertyChangeRegistry`](https://developer.android.com/reference/android/databinding/PropertyChangeRegistry?hl=zh-cn) 对象实现，以便有效地注册和通知监听器。

### 生成的绑定类

数据绑定库可以生成用于访问布局的变量和视图的绑定类。生成的绑定类将布局变量与布局中的视图关联起来。绑定类的名称和包可以[自定义](https://developer.android.com/topic/libraries/data-binding/generated-binding?hl=zh-cn#custom_binding_class_names)。所有生成的绑定类都是从 [`ViewDataBinding`](https://developer.android.com/reference/androidx/databinding/ViewDataBinding?hl=zh-cn) 类继承而来的。

系统会为每个布局文件生成一个绑定类。默认情况下，类名称基于布局文件的名称，它会转换为 Pascal 大小写形式并在末尾添加 Binding 后缀。以上布局文件名为 `activity_main.xml`，因此生成的对应类为 `ActivityMainBinding`。此类包含从布局属性（例如，`user` 变量）到布局视图的所有绑定，并且知道如何为绑定表达式指定值。

#### 创建绑定对象

在对布局进行扩充后立即创建绑定对象，以确保视图层次结构在通过表达式与布局内的视图绑定之前不会被修改。将对象绑定到布局的最常用方法是在绑定类上使用静态方法。您可以使用绑定类的 `inflate()` 方法来扩充视图层次结构并将对象绑定到该层次结构，如以下示例所示：

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MyLayoutBinding = MyLayoutBinding.inflate(layoutInflater)
      
        val binding: MyLayoutBinding = MyLayoutBinding.inflate(getLayoutInflater(), viewGroup, false)

        setContentView(binding.root)
    }

    
```

如果布局是使用其他机制扩充的，可单独绑定，如下所示：

```kotlin
    val binding: MyLayoutBinding = MyLayoutBinding.bind(viewRoot)
```

有时，系统无法预先知道绑定类型。在这种情况下，可以使用 [`DataBindingUtil`](https://developer.android.com/reference/androidx/databinding/DataBindingUtil?hl=zh-cn) 类创建绑定，如以下代码段所示：

```kotlin
    val viewRoot = LayoutInflater.from(this).inflate(layoutId, parent, attachToParent)
    val binding: ViewDataBinding? = DataBindingUtil.bind(viewRoot)
```

如果您要在 `Fragment`、`ListView` 或 `RecyclerView` 适配器中使用数据绑定项，您可能更愿意使用绑定类或 [`DataBindingUtil`](https://developer.android.com/reference/androidx/databinding/DataBindingUtil?hl=zh-cn) 类的 [`inflate()`](https://developer.android.com/reference/androidx/databinding/DataBindingUtil?hl=zh-cn#inflate(android.view.LayoutInflater, int, android.view.ViewGroup, boolean, android.databinding.DataBindingComponent)) 方法，如以下代码示例所示：

```kotlin
    val listItemBinding = ListItemBinding.inflate(layoutInflater, viewGroup, false)
    // or
    val listItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false)
```

数据绑定库会针对布局中具有 ID 的每个视图在绑定类中创建不可变字段。例如，数据绑定库会根据以下布局创建 `TextView` 类型的 `firstName` 和 `lastName` 字段：

```xml
		<layout xmlns:android="http://schemas.android.com/apk/res/android">
       <data>
           <variable name="user" type="com.example.User"/>
       </data>
       <LinearLayout
           android:orientation="vertical"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.firstName}"
       android:id="@+id/firstName"/>
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.lastName}"
      android:id="@+id/lastName"/>
       </LinearLayout>
    </layout>
    
```

#### 生成变量访问器

数据绑定库为布局中声明的每个变量生成访问器方法。例如，以下布局在绑定类中针对 `user`、`image` 和 `note` 变量生成了 setter 和 getter 方法：

```xml
		<data>
       <import type="android.graphics.drawable.Drawable"/>
       <variable name="user" type="com.example.User"/>
       <variable name="image" type="Drawable"/>
       <variable name="note" type="String"/>
    </data>
    
```

#### ViewStubs

与普通视图不同，`ViewStub` 对象初始是一个不可见视图。当它们显示出来或者获得明确指示进行扩充时，它们会通过扩充另一个布局在布局中完成自我取代。

由于 `ViewStub` 实际上会从视图层次结构中消失，因此绑定对象中的视图也必须消失，才能通过垃圾回收进行回收。由于视图是最终结果，因此 [`ViewStubProxy`](https://developer.android.com/reference/androidx/databinding/ViewStubProxy?hl=zh-cn) 对象将取代生成的绑定类中的 `ViewStub`，让您能够访问 `ViewStub`（如果存在），同时还能访问 `ViewStub` 进行扩充后的扩充版视图层次结构。

在扩充其他布局时，必须为新布局建立绑定。因此，`ViewStubProxy` 必须监听 `ViewStub` `OnInflateListener` 并在必要时建立绑定。由于在给定时间只能有一个监听器，因此 `ViewStubProxy` 允许您设置 `OnInflateListener`，它将在建立绑定后调用这个监听器。

#### 即时绑定

当可变或可观察对象发生更改时，绑定会按照计划在下一帧之前发生更改。但有时必须立即执行绑定。要强制执行，请使用 [`executePendingBindings()`](https://developer.android.com/reference/androidx/databinding/ViewDataBinding?hl=zh-cn#executePendingBindings()) 方法。

#### 动态变量

有时，系统并不知道特定的绑定类。例如，针对任意布局运行的 `RecyclerView.Adapter` 不知道特定绑定类。在调用 `onBindViewHolder()` 方法时，仍必须指定绑定值。

在以下示例中，`RecyclerView` 绑定到的所有布局都有 `item` 变量。`BindingHolder` 对象具有一个 `getBinding()` 方法，这个方法返回 [`ViewDataBinding`](https://developer.android.com/reference/androidx/databinding/ViewDataBinding?hl=zh-cn) 基类。

**注意**：数据绑定库在模块包中生成一个名为 `BR` 的类，其中包含用于数据绑定的资源的 ID。在上例中，该库自动生成 `BR.item` 变量。

```kotlin
    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        item: T = items.get(position)
        holder.binding.setVariable(BR.item, item);
        holder.binding.executePendingBindings();
    }
```

#### 后台线程

您可以在后台线程中更改数据模型，但前提是这个模型不是集合。数据绑定会在求值过程中对每个变量/字段进行本地化，以避免出现并发问题。

#### 自定义绑定类名称

默认情况下，绑定类是根据布局文件的名称生成的，以大写字母开头，移除下划线 ( _ )，将后一个字母大写，最后添加后缀 **Binding**。该类位于模块包下的 `databinding` 包中。例如，布局文件 `contact_item.xml` 会生成 `ContactItemBinding` 类。如果模块包是 `com.example.my.app`，则绑定类放在 `com.example.my.app.databinding` 包中。

通过调整 `data` 元素的 `class` 特性，绑定类可重命名或放置在不同的包中。例如，以下布局在当前模块的 `databinding` 包中生成 `ContactItem` 绑定类：

```xml
<data class="ContactItem">
        …
</data>
```

您可以在类名前添加句点和前缀，从而在其他文件包中生成绑定类。以下示例在模块包中生成绑定类：

```xml
<data class=".ContactItem">
        …
</data>
    
```

您还可以使用完整软件包名称来生成绑定类。以下示例在 `com.example` 包中创建 `ContactItem` 绑定类：

```xml
<data class="com.example.ContactItem">
        …
</data>
    
```

### 绑定适配器

数据绑定库允许您通过使用适配器指定为设置值而调用的方法、提供您自己的绑定逻辑，以及指定返回对象的类型。

#### 设置特性值

只要绑定值发生更改，生成的绑定类就必须使用绑定表达式在视图上调用 setter 方法。您可以允许数据绑定库自动确定方法、显式声明方法或提供选择方法的自定义逻辑。

##### 自动选择方法

对于名为 `example` 的特性，库自动尝试查找接受兼容类型作为参数的方法 `setExample(arg)`。系统不会考虑特性的命名空间，搜索方法时仅使用特性名称和类型。

以 `android:text="@{user.name}"` 表达式为例，库会查找接受 `user.getName()` 所返回类型的 `setText(arg)` 方法。如果 `user.getName()` 的返回类型为 `String`，则库会查找接受 `String` 参数的 `setText()` 方法。如果表达式返回的是 `int`，则库会搜索接受 `int` 参数的 `setText()` 方法。表达式必须返回正确的类型，您可以根据需要强制转换返回值的类型。

即使不存在具有给定名称的特性，数据绑定也会起作用。然后，您可以使用数据绑定为任何 setter 创建特性。例如，支持类 `DrawerLayout` 没有任何特性，但有很多 setter。以下布局会自动将 `setScrimColor(int)` 和 `setDrawerListener(DrawerListener)` 方法分别用作 `app:scrimColor` 和 `app:drawerListener` 特性的 setter：

```xml
<android.support.v4.widget.DrawerLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:scrimColor="@{@color/scrim}"
        app:drawerListener="@{fragment.drawerListener}">
    
```

##### 指定自定义方法名称

一些属性具有名称不符的 setter 方法。在这些情况下，某个特性可能会使用 [`BindingMethods`](https://developer.android.com/reference/androidx/databinding/BindingMethods?hl=zh-cn) 注释与 setter 相关联。注释与类一起使用，可以包含多个 [`BindingMethod`](https://developer.android.com/reference/androidx/databinding/BindingMethod?hl=zh-cn) 注释，每个注释对应一个重命名的方法。绑定方法是可添加到应用中任何类的注释。在以下示例中，`android:tint` 属性与 `setImageTintList(ColorStateList)` 方法相关联，而不与 `setTint()` 方法相关联：

```kotlin
    @BindingMethods(value = [
        BindingMethod(
            type = android.widget.ImageView::class,
            attribute = "android:tint",
            method = "setImageTintList")])

    
```

大多数情况下，您无需在 Android 框架类中重命名 setter。特性已使用命名惯例实现，可自动查找匹配的方法。

##### 提供自定义逻辑

一些属性需要自定义绑定逻辑。例如，`android:paddingLeft` 特性没有关联的 setter，而是提供了 `setPadding(left, top, right, bottom)` 方法。使用 [`BindingAdapter`](https://developer.android.com/reference/androidx/databinding/BindingAdapter?hl=zh-cn) 注释的静态绑定适配器方法支持自定义特性 setter 的调用方式。

Android 框架类的特性已经创建了 `BindingAdapter` 注释。例如，以下示例展示了 `paddingLeft` 属性的绑定适配器：

```kotlin
    @BindingAdapter("android:paddingLeft")
    fun setPaddingLeft(view: View, padding: Int) {
        view.setPadding(padding,
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    view.getPaddingBottom())
    }
    
```

参数类型非常重要。第一个参数用于确定与特性关联的视图类型，第二个参数用于确定在给定特性的绑定表达式中接受的类型。

绑定适配器对其他类型的自定义很有用。例如，可以通过工作器线程调用自定义加载程序来加载图片。

出现冲突时，您定义的绑定适配器会替换由 Android 框架提供的默认适配器。

您还可以使用接收多个属性的适配器，如以下示例所示：

```kotlin
  @BindingAdapter("imageUrl", "error")
    fun loadImage(view: ImageView, url: String, error: Drawable) {
        Picasso.get().load(url).error(error).into(view)
   }
```

您可以在布局中使用适配器，如以下示例所示。请注意，`@drawable/venueError` 引用应用中的资源。使用 `@{}` 将资源括起来可使其成为有效的绑定表达式。

**注意**：数据绑定库在匹配时会忽略自定义命名空间。

```xml
<ImageView app:imageUrl="@{venue.imageUrl}" app:error="@{@drawable/venueError}" />
```

如果 `ImageView` 对象同时使用了 `imageUrl` 和 `error`，并且 `imageUrl` 是字符串，`error` 是 `Drawable`，就会调用适配器。如果您希望在设置了任意属性时调用适配器，则可以将适配器的可选 [`requireAll`](https://developer.android.com/reference/androidx/databinding/BindingAdapter?hl=zh-cn#requireAll()) 标志设置为 `false`，如以下示例所示：

**注意**：出现冲突时，绑定适配器会替换默认的数据绑定适配器。

```kotlin
    @BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
    fun setImageUrl(imageView: ImageView, url: String?, placeHolder: Drawable?) {
        if (url == null) {
            imageView.setImageDrawable(placeholder);
        } else {
            MyImageLoader.loadInto(imageView, url, placeholder);
        }
    }
```



绑定适配器方法可以选择性在处理程序中使用旧值。同时获取旧值和新值的方法应该先为属性声明所有旧值，然后再声明新值，如以下示例所示：

```kotlin
    @BindingAdapter("android:paddingLeft")
    fun setPaddingLeft(view: View, oldPadding: Int, newPadding: Int) {
        if (oldPadding != newPadding) {
            view.setPadding(padding,
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        view.getPaddingBottom())
        }
    }

    
```

事件处理脚本只能与具有一种抽象方法的接口或抽象类一起使用，如以下示例所示：

```kotlin
    @BindingAdapter("android:onLayoutChange")
    fun setOnLayoutChangeListener(
            view: View,
            oldValue: View.OnLayoutChangeListener?,
            newValue: View.OnLayoutChangeListener?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (oldValue != null) {
                view.removeOnLayoutChangeListener(oldValue)
            }
            if (newValue != null) {
                view.addOnLayoutChangeListener(newValue)
            }
        }
    }
    
```

按如下方式在布局中使用此事件处理脚本：

```xml
<View android:onLayoutChange="@{() -> handler.layoutChanged()}"/>
```

当监听器有多个方法时，必须将它拆分为多个监听器。例如，`View.OnAttachStateChangeListener` 有两个方法：`onViewAttachedToWindow(View)` 和 `onViewDetachedFromWindow(View)`。该库提供了两个接口，用于区分它们的属性和处理脚本：

```kotlin
    // Translation from provided interfaces in Java:
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    interface OnViewDetachedFromWindow {
        fun onViewDetachedFromWindow(v: View)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    interface OnViewAttachedToWindow {
        fun onViewAttachedToWindow(v: View)
    }
    
```

因为更改一个监听器也会影响另一个监听器，所以需要适用于其中一个属性或同时适用于这两个属性的适配器。您可以在注释中将 [`requireAll`](https://developer.android.com/reference/androidx/databinding/BindingAdapter?hl=zh-cn#requireAll()) 设置为 `false`，以指定并非必须为每个属性都分配绑定表达式，如以下示例所示：

```kotlin
    @BindingAdapter(
            "android:onViewDetachedFromWindow",
            "android:onViewAttachedToWindow",
            requireAll = false
    )
    fun setListener(view: View, detach: OnViewDetachedFromWindow?, attach: OnViewAttachedToWindow?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            val newListener: View.OnAttachStateChangeListener?
            newListener = if (detach == null && attach == null) {
                null
            } else {
                object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                        attach.onViewAttachedToWindow(v)
                    }

                    override fun onViewDetachedFromWindow(v: View) {
                        detach.onViewDetachedFromWindow(v)
                    }
                }
            }

            val oldListener: View.OnAttachStateChangeListener? =
                    ListenerUtil.trackListener(view, newListener, R.id.onAttachStateChangeListener)
            if (oldListener != null) {
                view.removeOnAttachStateChangeListener(oldListener)
            }
            if (newListener != null) {
                view.addOnAttachStateChangeListener(newListener)
            }
        }
    }
    
```

以上示例比一般情况稍微复杂一些，因为 `View` 类使用 `addOnAttachStateChangeListener()` 和 `removeOnAttachStateChangeListener()` 方法，而非 `OnAttachStateChangeListener` 的 setter 方法。`android.databinding.adapters.ListenerUtil` 类有助于跟踪以前的监听器，以便在绑定适配器中将它们移除。

通过用 `@TargetApi(VERSION_CODES.HONEYCOMB_MR1)` 注释接口 `OnViewDetachedFromWindow` 和 `OnViewAttachedToWindow`，数据绑定代码生成器知道只应在运行 Android 3.1（API 级别 12）及更高级别（`addOnAttachStateChangeListener()` 方法支持的相同版本）时生成监听器。

#### 对象转换

##### 自动转换对象

当绑定表达式返回 `Object` 时，库会选择用于设置属性值的方法。`Object` 会转换为所选方法的参数类型。对于使用 [`ObservableMap`](https://developer.android.com/reference/androidx/databinding/ObservableMap?hl=zh-cn) 类存储数据的应用，这种行为非常便捷，如以下示例所示：

```xml
<TextView
       android:text='@{userMap["lastName"]}'
       android:layout_width="wrap_content"
       android:layout_height="wrap_content" />
    
```

**注意**：您还可以使用 `object.key` 表示法引用映射中的值。例如，以上示例中的 `@{userMap["lastName"]}` 可替换为 `@{userMap.lastName}`。

表达式中的 `userMap` 对象会返回一个值，该值会自动转换为用于设置 `android:text` 特性值的 `setText(CharSequence)` 方法中的参数类型。如果参数类型不明确，则必须在表达式中强制转换返回类型。

##### 自定义转换

在某些情况下，需要在特定类型之间进行自定义转换。例如，视图的 `android:background` 特性需要 `Drawable`，但指定的 `color` 值是整数。以下示例展示了某个属性需要 `Drawable`，但结果提供了一个整数：

```xml
<View
       android:background="@{isError ? @color/red : @color/white}"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>
    
```

每当需要 `Drawable` 且返回整数时，`int` 都应转换为 `ColorDrawable`。您可以使用带有 [`BindingConversion`](https://developer.android.com/reference/androidx/databinding/BindingConversion?hl=zh-cn) 注释的静态方法完成这个转换，如下所示：

```kotlin
    @BindingConversion
    fun convertColorToDrawable(color: Int) = ColorDrawable(color)
    
```

但是，绑定表达式中提供的值类型必须保持一致。您不能在同一个表达式中使用不同的类型，如以下示例所示：

```xml
<View
       android:background="@{isError ? @drawable/error : @color/white}"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>
    
```



### 将布局视图绑定到架构组件

AndroidX 库包含[架构组件](https://developer.android.com/topic/libraries/architecture?hl=zh-cn) (Architecture Components)，可用于设计可靠、可测试且可维护的应用。数据绑定库 (Data Binding Library) 可与架构组件无缝协作，进一步简化界面的开发。

#### 使用 LiveData 将数据变化通知给界面

您可以使用 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象作为数据绑定来源，自动将数据变化通知给界面。

与实现 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 的对象（例如[可观察字段](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_fields)）不同，`LiveData` 对象了解订阅数据更改的观察器的生命周期。了解这一点有许多好处，具体说明请参阅[使用 LiveData 的优势](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#the_advantages_of_using_livedata)。在 Android Studio 版本 3.1 及更高版本中，您可以在数据绑定代码中将[可观察字段](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn#observable_fields)替换为 `LiveData` 对象。

要将 `LiveData` 对象与绑定类一起使用，您需要指定生命周期所有者来定义 `LiveData` 对象的范围。以下示例在绑定类实例化后将 Activity 指定为生命周期所有者：

```kotlin
    class ViewModelActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            // Inflate view and obtain an instance of the binding class.
            val binding: UserBinding = DataBindingUtil.setContentView(this, R.layout.user)

            // Specify the current activity as the lifecycle owner.
            binding.setLifecycleOwner(this)
        }
    }
    
```

您可以根据[使用 ViewModel 管理界面相关数据](https://developer.android.com/topic/libraries/data-binding/architecture?hl=zh-cn#viewmodel)中所述，使用 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 组件来将数据绑定到布局。在 `ViewModel` 组件中，您可以使用 `LiveData` 对象转换数据或合并多个数据源。以下示例展示了如何在 `ViewModel` 中转换数据：

```kotlin
    class ScheduleViewModel : ViewModel() {
        val userName: LiveData

        init {
            val result = Repository.userName
            userName = Transformations.map(result) { result -> result.value }
        }
    }
```

#### 使用 ViewModel 管理界面相关数据

数据绑定库可与 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 组件无缝协作，这类组件会公开布局观察到并对其变化做出响应的数据。通过将 `ViewModel` 组件与数据绑定库结合使用，您可以将界面逻辑从布局移出，并移入到这些组件中，以便于测试。数据绑定库确保在需要时将视图与数据源绑定或解绑。大部分的其余工作是为了确保您公开的是正确的数据。有关此架构组件的更多信息，请参阅 [ViewModel 概览](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=zh-cn)。

要将 `ViewModel` 组件与数据绑定库一起使用，必须实例化从 `ViewModel` 类继承而来的组件，获取绑定类的实例，并将您的 `ViewModel` 组件分配给绑定类中的属性。以下示例展示了如何将组件与库结合使用：

```kotlin
    class ViewModelActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            // Obtain the ViewModel component.
            val userModel: UserModel by viewModels()

            // Inflate view and obtain an instance of the binding class.
            val binding: UserBinding = DataBindingUtil.setContentView(this, R.layout.user)

            // Assign the component to a property in the binding class.
            binding.viewmodel = userModel
        }
    }
    
```

在您的布局中，使用绑定表达式将 `ViewModel` 组件的属性和方法分配给对应的视图，如以下示例所示：

```xml
<CheckBox
        android:id="@+id/rememberMeCheckBox"
        android:checked="@{viewmodel.rememberMe}"
        android:onCheckedChanged="@{() -> viewmodel.rememberMeChanged()}" />
    
```

#### 使用 Observable ViewModel 更好地控制绑定适配器

您可以使用实现 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 的 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 组件，向其他应用组件发出数据变化通知，这与使用 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象的方式类似。

在某些情况下，您可能更愿意使用实现 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 接口的 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 组件，而不是使用 `LiveData` 对象，即使这样会失去对 `LiveData` 的生命周期管理功能也不影响。使用实现 `Observable` 的 `ViewModel` 组件可让您更好地控制应用中的绑定适配器。例如，这种模式可让您更好地控制数据更改时发出的通知，您还可以指定自定义方法来设置双向数据绑定中的属性值。

如需实现可观察的 `ViewModel` 组件，您必须创建一个从 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 类继承而来并实现 [`Observable`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn) 接口的类。您可以使用 [`addOnPropertyChangedCallback()`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn#addOnPropertyChangedCallback(android.databinding.Observable.OnPropertyChangedCallback)) 和 [`removeOnPropertyChangedCallback()`](https://developer.android.com/reference/androidx/databinding/Observable?hl=zh-cn#removeOnPropertyChangedCallback(android.databinding.Observable.OnPropertyChangedCallback)) 方法提供观察器订阅或取消订阅通知时的自定义逻辑。您还可以在 [`notifyPropertyChanged()`](https://developer.android.com/reference/androidx/databinding/BaseObservable?hl=zh-cn#notifyPropertyChanged(int)) 方法中提供属性更改时运行的自定义逻辑。以下代码示例展示了如何实现一个可观察的 `ViewModel`：

```kotlin
    /**
     * 也是可观察的ViewModel,与数据绑定库一起使用。
     */
    open class ObservableViewModel : ViewModel(), Observable {
        private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

        override fun addOnPropertyChangedCallback(
                callback: Observable.OnPropertyChangedCallback) {
            callbacks.add(callback)
        }

        override fun removeOnPropertyChangedCallback(
                callback: Observable.OnPropertyChangedCallback) {
            callbacks.remove(callback)
        }

        /**
         * 通知观察者该实例的所有属性已更改。
         */
        fun notifyChange() {
            callbacks.notifyCallbacks(this, 0, null)
        }

        /**
         * 通知观察者特定属性已更改。 更改的属性的吸气剂应使用@Bindable批注标记，以在BR类中生成一个用作fieldId参数的字段。
         * @param fieldId为可绑定字段生成的BR ID。
         */
        fun notifyPropertyChanged(fieldId: Int) {
            callbacks.notifyCallbacks(this, fieldId, null)
        }
    }
    
```



### 双向数据绑定

使用单向数据绑定时，您可以为特性设置值，并设置对该特性的变化作出反应的监听器：

```xml
    <CheckBox
        android:id="@+id/rememberMeCheckBox"
        android:checked="@{viewmodel.rememberMe}"
        android:onCheckedChanged="@{viewmodel.rememberMeChanged}"
    />
    
```

双向数据绑定为此过程提供了一种快捷方式：`@={}` 

```xml
    <CheckBox
        android:id="@+id/rememberMeCheckBox"
        android:checked="@={viewmodel.rememberMe}"
    />
    
```

`@={}` 表示法（其中重要的是包含“=”符号）可接收属性的数据更改并同时监听用户更新。

为了对后台数据的变化作出反应，您可以将您的布局变量设置为 `Observable`（通常为 [`BaseObservable`](https://developer.android.com/reference/androidx/databinding/BaseObservable?hl=zh-cn)）的实现，并使用 [`@Bindable`](https://developer.android.com/reference/androidx/databinding/Bindable?hl=zh-cn) 注释，如以下代码段所示：

```kotlin
    class LoginViewModel : BaseObservable {
        // val data = ...

        @Bindable
        fun getRememberMe(): Boolean {
            return data.rememberMe
        }

        fun setRememberMe(value: Boolean) {
            // Avoids infinite loops.
            if (data.rememberMe != value) {
                data.rememberMe = value

                // React to the change.
                saveData()

                // Notify observers of a new value.
                notifyPropertyChanged(BR.remember_me)
            }
        }
    }
    
```

由于可绑定属性的 getter 方法称为 `getRememberMe()`，因此属性的相应 setter 方法会自动使用名称 `setRememberMe()`。

有关 `BaseObservable` 和 `@Bindable` 用法的详细信息，请参阅[使用可观察的数据对象](https://developer.android.com/topic/libraries/data-binding/observability?hl=zh-cn)。



#### 使用自定义特性的双向数据绑定

该平台为[最常见的双向特性](https://developer.android.com/topic/libraries/data-binding/two-way?hl=zh-cn#two-way-attributes)和更改监听器提供了双向数据绑定实现，您可以将其用作应用的一部分。如果您希望结合使用双向数据绑定和自定义特性，则需要使用 [`@InverseBindingAdapter`](https://developer.android.com/reference/androidx/databinding/InverseBindingAdapter?hl=zh-cn) 和 [`@InverseBindingMethod`](https://developer.android.com/reference/androidx/databinding/InverseBindingMethod?hl=zh-cn) 注释。

例如，如果要在名为 `MyView` 的自定义视图中对 `"time"` 特性启用双向数据绑定，请完成以下步骤：

1. 使用 `@BindingAdapter`，对用来设置初始值并在值更改时进行更新的方法进行注释：

   ```kotlin
       @BindingAdapter("time")
       @JvmStatic fun setTime(view: MyView, newValue: Time) {
           // Important to break potential infinite loops.
           if (view.time != newValue) {
               view.time = newValue
           }
       }
   ```

2. 使用 `@InverseBindingAdapter` 对从视图中读取值的方法进行注释：

   ```kotlin
       @InverseBindingAdapter("time")
       @JvmStatic fun getTime(view: MyView) : Time {
           return view.getTime()
       }
   ```

此时，数据绑定知道在数据发生更改时要执行的操作（调用使用 [`@BindingAdapter`](https://developer.android.com/reference/androidx/databinding/BindingAdapter?hl=zh-cn) 注释的方法）以及当 view 视特性发生更改时要调用的内容（调用 [`InverseBindingListener`](https://developer.android.com/reference/androidx/databinding/InverseBindingListener?hl=zh-cn)）。但是，它不知道特性何时或如何更改。

为此，您需要在视图上设置监听器。这可以是与您的自定义视图相关联的自定义监听器，也可以是通用事件，例如失去焦点或文本更改。将 `@BindingAdapter` 注释添加到设置监听器（用来监听属性更改）的方法中：

```kotlin
    @BindingAdapter("app:timeAttrChanged")
    @JvmStatic fun setListeners(
            view: MyView,
            attrChange: InverseBindingListener
    ) {
        // Set a listener for click, focus, touch, etc.
    }
```

该监听器包含一个 `InverseBindingListener` 参数。您可以使用 `InverseBindingListener` 告知数据绑定系统，特性已更改。然后，该系统可以开始调用使用 `@InverseBindingAdapter` 注释的方法，依此类推。

**注意**：每个双向绑定都会生成“合成事件特性”。该特性与基本特性具有相同的名称，但包含后缀 `"AttrChanged"`。合成事件特性允许库创建使用 `@BindingAdapter` 注释的方法，以将事件监听器与相应的 `View` 实例相关联。

实际上，此监听器包含一些复杂逻辑，包括用于单向数据绑定的监听器。用于文本属性更改的适配器 [`TextViewBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/TextViewBindingAdapter.java#352) 就是一个例子。



#### 转换器

如果绑定到 [`View`](https://developer.android.com/reference/android/view/View?hl=zh-cn) 对象的变量需要设置格式、转换或更改后才能显示，则可以使用 `Converter` 对象。

以显示日期的 `EditText` 对象为例：

```xml
<EditText
        android:id="@+id/birth_date"
        android:text="@={Converter.dateToString(viewmodel.birthDate)}"
    />
    
```

`viewmodel.birthDate` 属性包含 `Long` 类型的值，因此需要使用转换器设置格式。

由于使用了双向表达式，因此还需要使用反向转换器，以告知库如何将用户提供的字符串转换回后备数据类型（在本例中为 `Long`）。此过程是通过向其中一个转换器添加 [`@InverseMethod`](https://developer.android.com/reference/androidx/databinding/InverseMethod?hl=zh-cn) 注释并让此注释引用反向转换器来完成的。以下代码段显示了此配置的一个示例：



```kotlin
    object Converter {
        @InverseMethod("stringToDate")
        @JvmStatic fun dateToString(
            view: EditText, oldValue: Long,
            value: Long
        ): String {
            // Converts long to String.
        }

        @JvmStatic fun stringToDate(
            view: EditText, oldValue: String,
            value: String
        ): Long {
            // Converts String to long.
        }
    }
```

#### 使用双向数据绑定的无限循环

使用双向数据绑定时，请注意不要引入无限循环。当用户更改特性时，系统会调用使用 `@InverseBindingAdapter` 注释的方法，并且该值将分配给后备属性。继而调用使用 `@BindingAdapter` 注释的方法，从而触发对使用 `@InverseBindingAdapter` 注释的方法的另一个调用，依此类推。

因此，通过比较使用 `@BindingAdapter` 注释的方法中的新值和旧值，可以打破可能出现的无限循环。

#### 双向数据绑定的内置支持

当您使用下表中的特性时，该平台提供对双向数据绑定的内置支持。有关平台如何提供此类支持的详细信息，请参阅相应绑定适配器的实现：

| 类                                                           | 特性                                                         | 绑定适配器                                                   |
| :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| [`AdapterView`](https://developer.android.com/reference/android/widget/AdapterView?hl=zh-cn) | `android:selectedItemPosition` `android:selection`           | [`AdapterViewBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/AdapterViewBindingAdapter.java) |
| [`CalendarView`](https://developer.android.com/reference/android/widget/CalendarView?hl=zh-cn) | `android:date`                                               | [`CalendarViewBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/CalendarViewBindingAdapter.java) |
| [`CompoundButton`](https://developer.android.com/reference/android/widget/CompoundButton?hl=zh-cn) | [`android:checked`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#checked) | [`CompoundButtonBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/CompoundButtonBindingAdapter.java) |
| [`DatePicker`](https://developer.android.com/reference/android/widget/DatePicker?hl=zh-cn) | `android:year` `android:month` `android:day`                 | [`DatePickerBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/DatePickerBindingAdapter.java) |
| [`NumberPicker`](https://developer.android.com/reference/android/widget/NumberPicker?hl=zh-cn) | [`android:value`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#value) | [`NumberPickerBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/NumberPickerBindingAdapter.java) |
| [`RadioButton`](https://developer.android.com/reference/android/widget/RadioButton?hl=zh-cn) | [`android:checkedButton`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#checkedButton) | [`RadioGroupBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/RadioGroupBindingAdapter.java) |
| [`RatingBar`](https://developer.android.com/reference/android/widget/RatingBar?hl=zh-cn) | [`android:rating`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#rating) | [`RatingBarBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/RatingBarBindingAdapter.java) |
| [`SeekBar`](https://developer.android.com/reference/android/widget/SeekBar?hl=zh-cn) | [`android:progress`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#progress) | [`SeekBarBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/SeekBarBindingAdapter.java) |
| [`TabHost`](https://developer.android.com/reference/android/widget/TabHost?hl=zh-cn) | `android:currentTab`                                         | [`TabHostBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/TabHostBindingAdapter.java) |
| [`TextView`](https://developer.android.com/reference/android/widget/TextView?hl=zh-cn) | [`android:text`](https://developer.android.com/reference/android/R.attr?hl=zh-cn#text) | [`TextViewBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/TextViewBindingAdapter.java) |
| [`TimePicker`](https://developer.android.com/reference/android/widget/TimePicker?hl=zh-cn) | `android:hour` `android:minute`                              | [`TimePickerBindingAdapter`](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/TimePickerBindingAdapter.java) |



## ViewModel 

[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 类旨在以注重生命周期的方式存储和管理界面相关的数据。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 类让数据可在发生屏幕旋转等配置更改后继续留存。

从界面控制器逻辑中分离出视图数据所有权的做法更易行且更高效。

数据恢复：如果系统销毁或重新创建Activity时，对于简单的数据，Activity 可以使用 `onSaveInstanceState()` 方法从 `onCreate()` 中的捆绑包恢复其数据，但此方法仅适合可以序列化再反序列化的少量数据，而不适合数量可能较大的数据，如用户列表或位图。

异步调用存在内存泄漏的隐患：

Activity单一职责问题：

随着数据变得越来越复杂，您可能会选择使用单独的类加载数据。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 的用途是封装界面控制器的数据，以使数据在配置更改后仍然存在。有关如何在配置更改后加载、保留和管理数据的信息，请参阅[保存界面状态](https://developer.android.com/topic/libraries/architecture/saving-states?hl=zh-cn)。

[Android 应用架构指南](https://developer.android.com/topic/libraries/architecture/guide?hl=zh-cn#fetching_data)建议构建存储区类来处理这些功能。

Activity 和 Fragment 通常会在下面三种情况下被销毁:

**从当前界面永久离开**: 用户导航至其他界面或直接关闭 Activity (通过点击返回按钮或执行的操作调用了 finish() 方法)。对应 Activity 实例被永久关闭；

**Activity 配置 (configuration) 被改变**: 例如，旋转屏幕等操作，会使 Activity 需要立即重建；

**应用在后台时，其进程被系统杀死**: 这种情况发生在设备剩余运行内存不足，系统又亟须释放一些内存的时候。当进程在后台被杀死后，用户又返回该应用时，Activity 也需要被重建。

在后两种情况中，我们通常都希望重建 Activity。ViewModel 会帮您处理第二种情况，因为在这种情况下 ViewModel 没有被销毁；而在第三种情况下， ViewModel 被销毁了。所以一旦出现了第三种情况，便需要在 Activity 的 onSaveInstanceState 相关回调中保存和恢复



### 实现 ViewModel

架构组件为界面控制器提供了 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 辅助程序类，该类负责为界面准备数据。在配置更改期间会自动保留 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象，以便它们存储的数据立即可供下一个 Activity 或 Fragment 实例使用。例如，如果您需要在应用中显示用户列表，请确保将获取和保留该用户列表的责任分配给 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn)，而不是 Activity 或 Fragment，如以下示例代码所示：

**注意**：[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 绝不能引用视图、[`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 或可能存储对 Activity 上下文的引用的任何类。

```kotlin
    class MyViewModel : ViewModel() {
        private val users: MutableLiveData<List<User>> by lazy {
            MutableLiveData().also {
                loadUsers()
            }
        }

        fun getUsers(): LiveData<List<User>> {
            return users
        }

        private fun loadUsers() {
            // Do an asynchronous operation to fetch users.
        }
    }
    
```

然后，您可以从 Activity 访问该列表，如下所示：

```kotlin
    class MyActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            // Create a ViewModel the first time the system calls an activity's onCreate() method.
            // Re-created activities receive the same MyViewModel instance created by the first activity.

            // Use the 'by viewModels()' Kotlin property delegate
            // from the activity-ktx artifact
            val model: MyViewModel by viewModels()
            model.getUsers().observe(this, Observer<List<User>>{ users ->
                // update UI
            })
        }
    }
    
```

如果重新创建了该 Activity，它接收的 `MyViewModel` 实例与第一个 Activity 创建的实例相同。当所有者 Activity 完成时，框架会调用 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象的 [`onCleared()`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn#onCleared()) 方法，以便它可以清理资源。

[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象存在的时间比视图或 [`LifecycleOwners`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 的特定实例存在的时间更长。这还意味着，您可以更轻松地编写涵盖 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 的测试，因为它不了解视图和 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 对象。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象可以包含 [`LifecycleObservers`](https://developer.android.com/reference/androidx/lifecycle/LifecycleObserver?hl=zh-cn)，如 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象。但是，[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象绝不能观察对生命周期感知型可观察对象（如 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象）的更改。 如果 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 需要 `Application` 上下文（例如，为了查找系统服务），它可以扩展 [`AndroidViewModel`](https://developer.android.com/reference/androidx/lifecycle/AndroidViewModel?hl=zh-cn) 类并设置用于接收 `Application` 的构造函数，因为 `Application` 类会扩展 `Context`

### ViewModel 的生命周期

[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象存在的时间范围是获取 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 时传递给 [`ViewModelProvider`](https://developer.android.com/reference/androidx/lifecycle/ViewModelProvider?hl=zh-cn) 的 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn)。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 将一直留在内存中，直到限定其存在时间范围的 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 永久消失：对于 Activity，是在 Activity 完成时；而对于 Fragment，是在 Fragment 分离时。

图 1 说明了 Activity 经历屏幕旋转而后结束的过程中所处的各种生命周期状态。该图还在关联的 Activity 生命周期的旁边显示了 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 的生命周期。此图表说明了 Activity 的各种状态。这些基本状态同样适用于 Fragment 的生命周期。

![说明 ViewModel 随着 Activity 状态的改变而经历的生命周期。](https://developer.android.com/images/topic/libraries/architecture/viewmodel-lifecycle.png?hl=zh-cn)

您通常在系统首次调用 Activity 对象的 `onCreate()` 方法时请求 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn)。系统可能会在 Activity 的整个生命周期内多次调用 `onCreate()`，如在旋转设备屏幕时。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 存在的时间范围是从您首次请求 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 直到 Activity 完成并销毁。

### ViewModel 的已保存状态模块

[保存界面状态](https://developer.android.com/topic/libraries/architecture/saving-states#use_onsaveinstancestate_as_backup_to_handle_system-initiated_process_death)这篇文章提到过，[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 对象可以处理配置更改，因此您无需担心旋转时或其他情况下的状态。但是，如果您需要处理系统发起的进程终止，则可以使用 [`onSaveInstanceState()`](https://developer.android.com/reference/android/app/Activity#onSaveInstanceState(android.os.Bundle)) 作为备用方式。

界面状态通常在 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 对象中（而不是 Activity 中）存储或引用；因此，使用 [`onSaveInstanceState()`](https://developer.android.com/reference/android/app/Activity#onSaveInstanceState(android.os.Bundle)) 时需要该模块可以为您处理的某个样板。

模块设置好以后，[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 对象会通过其构造函数接收 [`SavedStateHandle`](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle) 对象。这是一个键值对映射，用于向已保存状态写入对象以及从其中检索对象。这些值会在进程被系统终止后继续保留，并通过同一对象保持可用状态。

**注意**：状态必须是简单的轻量级状态。对于复杂或大型数据，您应该使用[本地持久性存储](https://developer.android.com/topic/libraries/architecture/saving-states#use_local_persistence_to_handle_process_death_for_complex_or_large_data)。

#### 设置和使用

使用 [Fragment `1.2.0`](https://developer.android.com/jetpack/androidx/releases/fragment#1.2.0) 或其传递依赖项 [Activity `1.1.0`](https://developer.android.com/jetpack/androidx/releases/activity#1.1.0) 时，[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 实例的默认出厂设置支持在无任何其他配置的情况下将适当的 `SavedStateHandle` 传递到您的 `ViewModel`。



```kotlin
    // Use the Kotlin property extension in the fragment-ktx / activity-ktx artifacts
    val vm: SavedStateViewModel by viewModels()

    
```

之后，ViewModel 便可以有一个接收 SavedStateHandle 的构造函数：

```kotlin
    class SavedStateViewModel(private val state: SavedStateHandle) : ViewModel() { ... }
    
```

提供自定义 `ViewModelProvider.Factory` 实例时，您可以通过扩展 [`AbstractSavedStateViewModelFactory`](https://developer.android.com/reference/androidx/lifecycle/AbstractSavedStateViewModelFactory) 启用 `SavedStateHandle`。

**注意**：在使用早期版本的 Fragment 时，您可以按照 [Lifecycle 版本说明](https://developer.android.com/jetpack/androidx/releases/lifecycle#declaring_dependencies)中关于声明依赖项的说明，添加 `lifecycle-viewmodel-savedstate` 的依赖项并使用 [`SavedStateViewModelFactory`](https://developer.android.com/reference/androidx/lifecycle/SavedStateViewModelFactory) 作为出厂设置。

#### 存储和检索值

[`SavedStateHandle`](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle) 类包含键值对映射应有的方法：

- `get(String key)`
- `contains(String key)`
- `remove(String key)`
- `set(String key, T value)`
- `keys()`

此外，还有一种特殊的方法：[`getLiveData(String key)`](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle#getLiveData(java.lang.String))，用于返回封装在 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) 可观察对象中的值。

### 在 Fragment 之间共享数据

Activity 中的两个或更多 Fragment 需要相互通信是一种很常见的情况。想象一下主从 Fragment 的常见情况，假设您有一个 Fragment，在该 Fragment 中，用户从列表中选择一项，还有另一个 Fragment，用于显示选定项的内容。这种情况不太容易处理，因为这两个 Fragment 都需要定义某种接口描述，并且所有者 Activity 必须将两者绑定在一起。此外，这两个 Fragment 都必须处理另一个 Fragment 尚未创建或不可见的情况。

可以使用 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象解决这一常见的难点。这两个 Fragment 可以使用其 Activity 范围共享 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 来处理此类通信，如以下示例代码所示：

```kotlin
    class SharedViewModel : ViewModel() {
        val selected = MutableLiveData<Item>()

        fun select(item: Item) {
            selected.value = item
        }
    }

    class MasterFragment : Fragment() {

        private lateinit var itemSelector: Selector

        // Use the 'by activityViewModels()' Kotlin property delegate
        // from the fragment-ktx artifact
        private val model: SharedViewModel by activityViewModels()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            itemSelector.setOnClickListener { item ->
                // Update the UI
            }
        }
    }

    class DetailFragment : Fragment() {

        // Use the 'by activityViewModels()' Kotlin property delegate
        // from the fragment-ktx artifact
        private val model: SharedViewModel by activityViewModels()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            model.selected.observe(viewLifecycleOwner, Observer<Item> { item ->
                // Update the UI
            })
        }
    }
    
```

请注意，这两个 Fragment 都会检索包含它们的 Activity。这样，当这两个 Fragment 各自获取 [`ViewModelProvider`](https://developer.android.com/reference/androidx/lifecycle/ViewModelProvider?hl=zh-cn) 时，它们会收到相同的 `SharedViewModel` 实例（其范围限定为该 Activity）。

此方法具有以下优势：

- Activity 不需要执行任何操作，也不需要对此通信有任何了解。
- 除了 `SharedViewModel` 约定之外，Fragment 不需要相互了解。如果其中一个 Fragment 消失，另一个 Fragment 将继续照常工作。
- 每个 Fragment 都有自己的生命周期，而不受另一个 Fragment 的生命周期的影响。如果一个 Fragment 替换另一个 Fragment，界面将继续工作而没有任何问题。

### 将加载器替换为 ViewModel

`CursorLoader` 等加载器类经常用于使应用界面中的数据与数据库保持同步。您可以将 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 与一些其他类一起使用来替换加载器。使用 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 可将界面控制器与数据加载操作分离，这意味着类之间的强引用更少。

在使用加载器的一种常见方法中，应用可能会使用 `CursorLoader` 观察数据库的内容。当数据库中的值发生更改时，加载器会自动触发数据的重新加载并更新界面：

![img](https://developer.android.com/images/topic/libraries/architecture/viewmodel-loader.png?hl=zh-cn)**图 2.** 使用加载器加载数据

[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 与 [Room](https://developer.android.com/topic/libraries/architecture/room?hl=zh-cn) 和 [LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn) 一起使用可替换加载器。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 确保数据在设备配置更改后仍然存在。[Room](https://developer.android.com/topic/libraries/architecture/room?hl=zh-cn) 在数据库发生更改时通知 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn)，[LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn) 进而使用修订后的数据更新界面。

![img](https://developer.android.com/images/topic/libraries/architecture/viewmodel-replace-loader.png?hl=zh-cn)**图 3.** 使用 ViewModel 加载数据

### 将协程与 ViewModel 一起使用

[在 Android 开发中使用协程 | 背景介绍](https://mp.weixin.qq.com/s?__biz=MzAwODY4OTk2Mg==&mid=2652052998&idx=2&sn=18715a7e33b7f7a5878bd301e9f8f935&chksm=808cbe43b7fb3755e01af29a316c402c8ad70bed5282109516c7f54d70db93013217ceb4e84a&scene=21#wechat_redirect)

[a](https://mp.weixin.qq.com/s?__biz=MzAwODY4OTk2Mg==&mid=2652053382&idx=2&sn=3c9ffe976c69675e9c0e08940afd566f&chksm=808cbfc3b7fb36d54d7f9feb43f4911433b3d1e47d3f055d818607242c4dcdea92303c49c6ea&scene=178&cur_album_id=1349518270751375362#rd)



Kotlin 中的协程提供了一种全新处理并发的方式，您可以在 Android 平台上使用它来简化异步执行的代码。协程是从 Kotlin 1.3 版本开始引入，但这一概念在编程世界诞生的黎明之际就有了，最早使用协程的编程语言可以追溯到 1967 年的 Simula 语言。

在过去几年间，协程这个概念发展势头迅猛，现已经被诸多主流编程语言采用，比如 Javascript、C#、Python、Ruby 以及 Go 等。Kotlin 的协程是基于来自其他语言的既定概念。

`ViewModel` 支持 Kotlin 协程。如需了解详情，请参阅[将 Kotlin 协程与 Android 架构组件一起使用](https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn)。

协程适合解决以下两个常见的编程问题:

1. **处理耗时任务 (Long running tasks)**，这种任务常常会阻塞住主线程；
2. **保证主线程安全 (Main-safety)**，即确保安全地从主线程调用任何 suspend 函数。

为了能够避免协程泄漏，Kotlin 引入了结构化并发 (structured concurrency) 机制，它是一系列编程语言特性和实践指南的结合，遵循它能帮助您追踪到所有运行于协程中的任务。

在 Android 平台上，我们可以使用结构化并发来做到以下三件事:

1. **取消任务** —— 当某项任务不再需要时取消它；
2. **追踪任务** —— 当任务正在执行时，追踪它；
3. **发出错误信号** —— 当协程失败时，发出错误信号表明有错误发生。



当今手机处理代码的速度

以 Pixel 2 为例，单个 CPU 周期耗时低于 0.0000000004 秒

如果将网络请求以 “眨眼间” 来表述，大概是 400 毫秒 (0.4 秒)

一眨眼的功夫内，CPU 就已完成了超过 10 亿次的时钟周期了。





![图片](https://mmbiz.qpic.cn/mmbiz_jpg/Sjl5iagvIVe5Jyg6FS1wicfiaMGfys4Md49ibhuw1ibcWptSpShfDzCqhbE7FCSULd549MicQ7XTRNib2wNvEmwvRtjZg/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

## LiveData

[`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 是一种可观察的数据存储器类。与常规的可观察类不同，LiveData 具有生命周期感知能力，意指它遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期。这种感知能力可确保 LiveData 仅更新处于活跃生命周期状态的应用组件观察者。

**注意**：如需将 LiveData 组件导入您的 Android 项目，请参阅[向项目添加组件](https://developer.android.com/topic/libraries/architecture/adding-components?hl=zh-cn#lifecycle)。

### 使用 LiveData 的优势

使用 LiveData 具有以下优势：

- **确保界面符合数据状态**

  LiveData 遵循观察者模式。当生命周期状态发生变化时，LiveData 会通知 [`Observer`](https://developer.android.com/reference/androidx/lifecycle/Observer?hl=zh-cn) 对象。您可以整合代码以在这些 `Observer` 对象中更新界面。观察者可以在每次发生更改时更新界面，而不是在每次应用数据发生更改时更新界面。

- **不会发生内存泄漏**

  观察者会绑定到 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 对象，并在其关联的生命周期遭到销毁后进行自我清理。

- **不会因 Activity 停止而导致崩溃**

  如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），则它不会接收任何 LiveData 事件。

- **不再需要手动处理生命周期**

  界面组件只是观察相关数据，不会停止或恢复观察。LiveData 将自动管理所有这些操作，因为它在观察时可以感知相关的生命周期状态变化。

- **数据始终保持最新状态**

  如果生命周期变为非活跃状态，它会在再次变为活跃状态时接收最新的数据。例如，曾经在后台的 Activity 会在返回前台后立即接收最新的数据。

- **适当的配置更改**

  如果由于配置更改（如设备旋转）而重新创建了 Activity 或 Fragment，它会立即接收最新的可用数据。

- **共享资源**

  您可以使用单一实例模式扩展 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象以封装系统服务，以便在应用中共享它们。`LiveData` 对象连接到系统服务一次，然后需要相应资源的任何观察者只需观察 `LiveData` 对象。如需了解详情，请参阅[扩展 LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#extend_livedata)。





### 使用 LiveData 对象

请按照以下步骤使用 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象：

1. 创建 `LiveData` 实例以存储某种类型的数据。这通常在 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 类中完成。

2. 创建可定义 [`onChanged()`](https://developer.android.com/reference/androidx/lifecycle/Observer?hl=zh-cn#onChanged(T)) 方法的 [`Observer`](https://developer.android.com/reference/androidx/lifecycle/Observer?hl=zh-cn) 对象，该方法可以控制当 `LiveData` 对象存储的数据更改时会发生什么。通常情况下，您可以在界面控制器（如 Activity 或 Fragment）中创建 `Observer` 对象。

3. 使用 [`observe()`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#observe(android.arch.lifecycle.LifecycleOwner, android.arch.lifecycle.Observer)) 方法将 `Observer` 对象附加到 `LiveData` 对象。`observe()` 方法会采用 [`LifecycleOwner`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 对象。这样会使 `Observer` 对象订阅 `LiveData` 对象，以使其收到有关更改的通知。通常情况下，您可以在界面控制器（如 Activity 或 Fragment）中附加 `Observer` 对象。

   **注意**：您可以使用 [`observeForever(Observer)`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#observeForever(android.arch.lifecycle.Observer)) 方法来注册未关联 [`LifecycleOwner`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 对象的观察者。在这种情况下，观察者会被视为始终处于活跃状态，因此它始终会收到关于修改的通知。您可以通过调用 [`removeObserver(Observer)`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#removeObserver(android.arch.lifecycle.Observer)) 方法来移除这些观察者。

当您更新存储在 `LiveData` 对象中的值时，它会触发所有已注册的观察者（只要附加的 `LifecycleOwner` 处于活跃状态）。

LiveData 允许界面控制器观察者订阅更新。当 `LiveData` 对象存储的数据发生更改时，界面会自动更新以做出响应。

### 创建 LiveData 对象

LiveData 是一种可用于任何数据的封装容器，其中包括可实现 `Collections` 的对象，如 `List`。[`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象通常存储在 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象中，并可通过 getter 方法进行访问，如以下示例中所示：

```kotlin
    class NameViewModel : ViewModel() {

        // Create a LiveData with a String
        val currentName: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        // Rest of the ViewModel...
    }
    
```

最初，`LiveData` 对象中的数据并未经过设置。

> **注意**：请确保用于更新界面的 `LiveData` 对象存储在 `ViewModel` 对象中，而不是将其存储在 Activity 或 Fragment 中，原因如下：避免 Activity 和 Fragment 过于庞大。现在，这些界面控制器负责显示数据，但不负责存储数据状态。将 `LiveData` 实例与特定的 Activity 或 Fragment 实例分离开，并使 `LiveData` 对象在配置更改后继续存在。

您可以在 [ViewModel 指南](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=zh-cn)中详细了解 `ViewModel` 类的好处和用法。

### 观察 LiveData 对象

在大多数情况下，应用组件的 `onCreate()` 方法是开始观察 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象的正确着手点，原因如下：

- 确保系统不会从 Activity 或 Fragment 的 `onResume()` 方法进行冗余调用。
- 确保 Activity 或 Fragment 变为活跃状态后具有可以立即显示的数据。一旦应用组件处于 [`STARTED`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.State?hl=zh-cn#STARTED) 状态，就会从它正在观察的 `LiveData` 对象接收最新值。只有在设置了要观察的 `LiveData` 对象时，才会发生这种情况。

通常，LiveData 仅在数据发生更改时才发送更新，并且仅发送给活跃观察者。此行为的一种例外情况是，观察者从非活跃状态更改为活跃状态时也会收到更新。此外，如果观察者第二次从非活跃状态更改为活跃状态，则只有在自上次变为活跃状态以来值发生了更改时，它才会收到更新。

以下示例代码说明了如何开始观察 `LiveData` 对象：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class NameActivity : AppCompatActivity() {

        // Use the 'by viewModels()' Kotlin property delegate
        // from the activity-ktx artifact
        private val model: NameViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Other code to setup the activity...

            // Create the observer which updates the UI.
            val nameObserver = Observer<String> { newName ->
                // Update the UI, in this case, a TextView.
                nameTextView.text = newName
            }

            // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            model.currentName.observe(this, nameObserver)
        }
    }
    
```

在传递 `nameObserver` 参数的情况下调用 [`observe()`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#observe(android.arch.lifecycle.LifecycleOwner, android.arch.lifecycle.Observer)) 后，系统会立即调用 [`onChanged()`](https://developer.android.com/reference/androidx/lifecycle/Observer?hl=zh-cn#onChanged(T))，从而提供 `mCurrentName` 中存储的最新值。如果 `LiveData` 对象尚未在 `mCurrentName` 中设置值，则不会调用 `onChanged()`。

### 更新 LiveData 对象

LiveData 没有公开可用的方法来更新存储的数据。[`MutableLiveData`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn) 类将公开 [`setValue(T)`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn#setValue(T)) 和 [`postValue(T)`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn#postValue(T)) 方法，如果您需要修改存储在 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象中的值，则必须使用这些方法。通常情况下会在 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 中使用 `MutableLiveData`，然后 `ViewModel` 只会向观察者公开不可变的 `LiveData` 对象。

设置观察者关系后，您可以更新 `LiveData` 对象的值（如以下示例中所示），这样当用户点按某个按钮时会触发所有观察者：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    button.setOnClickListener {
        val anotherName = "John Doe"
        model.currentName.setValue(anotherName)
    }
    
```

在本例中调用 `setValue(T)` 导致观察者使用值 `John Doe` 调用其 [`onChanged()`](https://developer.android.com/reference/androidx/lifecycle/Observer?hl=zh-cn#onChanged(T)) 方法。本例中演示的是按下按钮的方法，但也可以出于各种各样的原因调用 `setValue()` 或 `postValue()` 来更新 `mName`，这些原因包括响应网络请求或数据库加载完成。在所有情况下，调用 `setValue()` 或 `postValue()` 都会触发观察者并更新界面。

**注意**：您必须调用 [`setValue(T)`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn#setValue(T)) 方法以从主线程更新 `LiveData` 对象。如果在 worker 线程中执行代码，则您可以改用 [`postValue(T)`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn#postValue(T)) 方法来更新 `LiveData` 对象。

### 将 LiveData 与 Room 一起使用

[Room](https://developer.android.com/training/data-storage/room?hl=zh-cn) 持久性库支持返回 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象的可观察查询。可观察查询属于数据库访问对象 (DAO) 的一部分。

当数据库更新时，Room 会生成更新 `LiveData` 对象所需的所有代码。在需要时，生成的代码会在后台线程上异步运行查询。此模式有助于使界面中显示的数据与存储在数据库中的数据保持同步。您可以在 [Room 持久性库指南](https://developer.android.com/topic/libraries/architecture/room?hl=zh-cn)中详细了解 Room 和 DAO。

### 将协程与 LiveData 一起使用

`LiveData` 支持 Kotlin 协程。如需了解详情，请参阅[将 Kotlin 协程与 Android 架构组件一起使用](https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn)。

### 扩展 LiveData

如果观察者的生命周期处于 [`STARTED`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.State?hl=zh-cn#STARTED) 或 [`RESUMED`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.State?hl=zh-cn#RESUMED) 状态，则 LiveData 会认为该观察者处于活跃状态。以下示例代码说明了如何扩展 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 类：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
        private val stockManager = StockManager(symbol)

        private val listener = { price: BigDecimal ->
            value = price
        }

        override fun onActive() {
            stockManager.requestPriceUpdates(listener)
        }

        override fun onInactive() {
            stockManager.removeUpdates(listener)
        }
    }
    
```

本例中的价格监听器实现包括以下重要方法：

- 当 `LiveData` 对象具有活跃观察者时，会调用 [`onActive()`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#onActive()) 方法。这意味着，您需要从此方法开始观察股价更新。
- 当 `LiveData` 对象没有任何活跃观察者时，会调用 [`onInactive()`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#onInactive()) 方法。由于没有观察者在监听，因此没有理由与 `StockManager` 服务保持连接。
- [`setValue(T)`](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=zh-cn#setValue(T)) 方法将更新 `LiveData` 实例的值，并将更改通知给任何活跃观察者。

您可以使用 `StockLiveData` 类，如下所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    public class MyFragment : Fragment() {
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val myPriceListener: LiveData<BigDecimal> = ...
            myPriceListener.observe(viewLifecycleOwner, Observer<BigDecimal> { price: BigDecimal? ->
                // Update the UI.
            })
        }
    }
    
```

[`observe()`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn#observe(androidx.lifecycle.LifecycleOwner, androidx.lifecycle.Observer)) 方法将与 Fragment 视图关联的 [`LifecycleOwner`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 作为第一个参数传递。这样做表示此观察者已绑定到与所有者关联的 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 对象，这意味着：

- 如果 `Lifecycle` 对象未处于活跃状态，那么即使值发生更改，也不会调用观察者。
- 销毁 `Lifecycle` 对象后，会自动移除观察者。

`LiveData` 对象具有生命周期感知能力，这一事实意味着您可以在多个 Activity、Fragment 和 Service 之间共享这些对象。为使示例保持简单，您可以将 `LiveData` 类实现为单一实例，如下所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
        private val stockManager: StockManager = StockManager(symbol)

        private val listener = { price: BigDecimal ->
            value = price
        }

        override fun onActive() {
            stockManager.requestPriceUpdates(listener)
        }

        override fun onInactive() {
            stockManager.removeUpdates(listener)
        }

        companion object {
            private lateinit var sInstance: StockLiveData

            @MainThread
            fun get(symbol: String): StockLiveData {
                sInstance = if (::sInstance.isInitialized) sInstance else StockLiveData(symbol)
                return sInstance
            }
        }
    }
    
```

并且您可以在 Fragment 中使用它，如下所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class MyFragment : Fragment() {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            StockLiveData.get(symbol).observe(viewLifecycleOwner, Observer<BigDecimal> { price: BigDecimal? ->
                // Update the UI.
            })

        }
    
```

多个 Fragment 和 Activity 可以观察 `MyPriceListener` 实例。仅当一个或多个系统服务可见且处于活跃状态时，LiveData 才会连接到该服务。

### 转换 LiveData

您可能希望在将 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象分派给观察者之前对存储在其中的值进行更改，或者您可能需要根据另一个实例的值返回不同的 `LiveData` 实例。[`Lifecycle`](https://developer.android.com/reference/android/arch/lifecycle/package-summary?hl=zh-cn) 软件包会提供 [`Transformations`](https://developer.android.com/reference/androidx/lifecycle/Transformations?hl=zh-cn) 类，该类包括可应对这些情况的辅助程序方法。

- [`Transformations.map()`](https://developer.android.com/reference/androidx/lifecycle/Transformations?hl=zh-cn#map(android.arch.lifecycle.LiveData, android.arch.core.util.Function))

  对存储在 `LiveData` 对象中的值应用函数，并将结果传播到下游。

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    val userLiveData: LiveData<User> = UserLiveData()
    val userName: LiveData<String> = Transformations.map(userLiveData) {
        user -> "${user.name} ${user.lastName}"
    }
    
```

- [`Transformations.switchMap()`](https://developer.android.com/reference/androidx/lifecycle/Transformations?hl=zh-cn#switchMap(android.arch.lifecycle.LiveData, android.arch.core.util.Function>))

  与 `map()` 类似，对存储在 `LiveData` 对象中的值应用函数，并将结果解封和分派到下游。传递给 `switchMap()` 的函数必须返回 `LiveData` 对象，如以下示例中所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    private fun getUser(id: String): LiveData<User> {
      ...
    }
    val userId: LiveData<String> = ...
    val user = Transformations.switchMap(userId) { id -> getUser(id) }
    
```

您可以使用转换方法在观察者的生命周期内传送信息。除非观察者正在观察返回的 `LiveData` 对象，否则不会计算转换。因为转换是以延迟的方式计算，所以与生命周期相关的行为会隐式传递下去，而不需要额外的显式调用或依赖项。

如果您认为 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel?hl=zh-cn) 对象中需要有 `Lifecycle` 对象，那么进行转换或许是更好的解决方案。例如，假设您有一个界面组件，该组件接受地址并返回该地址的邮政编码。您可以为此组件实现简单的 `ViewModel`，如以下示例代码所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class MyViewModel(private val repository: PostalCodeRepository) : ViewModel() {

        private fun getPostalCode(address: String): LiveData<String> {
            // DON'T DO THIS
            return repository.getPostCode(address)
        }
    }
    
```

然后，该界面组件需要取消注册先前的 `LiveData` 对象，并在每次调用 `getPostalCode()` 时注册到新的实例。此外，如果重新创建了该界面组件，它会再触发一次对 `repository.getPostCode()` 方法的调用，而不是使用先前调用所得的结果。

您也可以将邮政编码查询实现为地址输入的转换，如以下示例中所示：

[KOTLIN](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#kotlin)[JAVA](https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn#java)

```kotlin
    class MyViewModel(private val repository: PostalCodeRepository) : ViewModel() {
        private val addressInput = MutableLiveData<String>()
        val postalCode: LiveData<String> = Transformations.switchMap(addressInput) {
                address -> repository.getPostCode(address) }

        private fun setInput(address: String) {
            addressInput.value = address
        }
    }
    
```

在这种情况下，`postalCode` 字段定义为 `addressInput` 的转换。只要您的应用具有与 `postalCode` 字段关联的活跃观察者，就会在 `addressInput` 发生更改时重新计算并检索该字段的值。

此机制允许较低级别的应用创建以延迟的方式按需计算的 `LiveData` 对象。`ViewModel` 对象可以轻松获取对 `LiveData` 对象的引用，然后在其基础之上定义转换规则。

### 创建新的转换

有十几种不同的特定转换在您的应用中可能很有用，但默认情况下不提供它们。要实现您自己的转换，您可以使用 [`MediatorLiveData`](https://developer.android.com/reference/androidx/lifecycle/MediatorLiveData?hl=zh-cn) 类，该类可以监听其他 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 对象并处理它们发出的事件。`MediatorLiveData` 正确地将其状态传播到源 `LiveData` 对象。要详细了解此模式，请参阅 [`Transformations`](https://developer.android.com/reference/androidx/lifecycle/Transformations?hl=zh-cn) 类的参考文档。

### 合并多个 LiveData 源

[`MediatorLiveData`](https://developer.android.com/reference/androidx/lifecycle/MediatorLiveData?hl=zh-cn) 是 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData?hl=zh-cn) 的子类，允许您合并多个 LiveData 源。只要任何原始的 LiveData 源对象发生更改，就会触发 `MediatorLiveData` 对象的观察者。

例如，如果界面中有可以从本地数据库或网络更新的 `LiveData` 对象，则可以向 `MediatorLiveData` 对象添加以下源：

- 与存储在数据库中的数据关联的 `LiveData` 对象。
- 与从网络访问的数据关联的 `LiveData` 对象。

您的 Activity 只需观察 `MediatorLiveData` 对象即可从这两个源接收更新。有关详细示例，请参阅[应用架构指南](https://developer.android.com/topic/libraries/architecture/guide?hl=zh-cn)的[附录：公开网络状态](https://developer.android.com/topic/libraries/architecture/guide?hl=zh-cn#addendum)部分。

### 什么情况下不推荐使用LiveData

- 如果您需要大量运算符或流，请使用Rx。
- 如果您的操作与Ul或生命周期无关，请使用回调接口。
-  如果您具有一次性操作链接，请使用协程。



## Hint：依赖项注入

遵循 DI 的原则可以为良好的应用架构奠定基础.依赖项注入会为您的应用提供以下优势：

- 重用类以及分离依赖项：更容易换掉依赖项的实现。由于控制反转，代码重用得以改进，并且类不再控制其依赖项的创建方式，而是支持任何配置。
- 易于重构：依赖项成为 API Surface 的可验证部分，因此可以在创建对象时或编译时进行检查，而不是作为实现详情隐藏。
- 易于测试：类不管理其依赖项，因此在测试时，您可以传入不同的实现以测试所有不同用例。

### 什么是依赖项注入

类通常需要引用其他类。例如，`Car` 类可能需要引用 `Engine` 类。这些必需类称为依赖项，在此示例中，`Car` 类依赖于拥有 `Engine` 类的一个实例才能运行。

类可通过以下三种方式获取所需的对象：

1. 类构造其所需的依赖项。在以上示例中，`Car` 将创建并初始化自己的 `Engine` 实例。
2. 从其他地方抓取。某些 Android API（如 `Context` getter 和 `getSystemService()`）的工作原理便是如此。
3. 以参数形式提供。应用可以在构造类时提供这些依赖项，或者将这些依赖项传入需要各个依赖项的函数。在以上示例中，`Car` 构造函数将接收 `Engine` 作为参数。

第三种方式就是依赖项注入！使用这种方法，您可以获取并提供类的依赖项，而不必让类实例自行获取。

如果使用依赖项注入，代码是什么样子的呢？`Car` 的每个实例在其构造函数中接收 `Engine` 对象作为参数，而不是在初始化时构造自己的 `Engine` 对象：

```kotlin
class Car(private val engine: Engine) {
    fun start() {
        engine.start()
    }
}

fun main(args: Array) {
    val engine = Engine()
    val car = Car(engine)
    car.start()
}
```

`main` 函数使用 `Car`。由于 `Car` 依赖于 `Engine`，因此应用会创建 `Engine` 的实例，然后使用它构造 `Car` 的实例。这种基于 DI 的方法具有以下优势：

- 重用 `Car`。您可以将 `Engine` 的不同实现传入 `Car`。例如，您可以定义一个想要 `Car` 使用的名为 `ElectricEngine` 的新 `Engine` 子类。如果您使用 DI，只需传入更新后的 `ElectricEngine` 子类的实例，`Car` 仍可正常使用，无需任何进一步更改。
- 轻松测试 `Car`。您可以传入测试替身以测试不同场景。例如，您可以创建一个名为 `FakeEngine` 的 `Engine` 测试替身，并针对不同的测试进行配置。

Android 中有两种主要的依赖项注入方式：

- **构造函数注入**。这就是上面描述的方式。您将某个类的依赖项传入其构造函数。
- **字段注入（或 setter 注入）**。某些 Android 框架类（如 Activity 和 Fragment）由系统实例化，因此无法进行构造函数注入。使用字段注入时，依赖项将在创建类后实例化。代码如下所示：

```kotlin
class Car {
    lateinit var engine: Engine

    fun start() {
        engine.start()
    }
}

fun main(args: Array) {
    val car = Car()
    car.engine = Engine()
    car.start()
}
```

**注意**：依赖项注入基于[控制反转](https://en.wikipedia.org/wiki/Inversion_of_control)原则，根据该原则，通用代码控制特定代码的执行。

### 自动依赖项注入

在上一个示例中，您自行创建、提供并管理不同类的依赖项，而不依赖于库。这称为手动依赖项注入或人工依赖项注入。在 `Car` 示例中，只有一个依赖项，但依赖项和类越多，手动依赖项注入就越繁琐。手动依赖项注入还会带来多个问题：

- 对于大型应用，获取所有依赖项并正确连接它们可能需要大量样板代码。在多层架构中，要为顶层创建一个对象，必须提供其下层的所有依赖项。例如，要制造一辆真车，可能需要引擎、传动装置、底盘以及其他部件；而要制造引擎，则需要汽缸和火花塞。
- 如果您无法在传入依赖项之前构造依赖项（例如，当使用延迟初始化或将对象作用域限定为应用流时），则需要编写并维护管理内存中依赖项生命周期的自定义容器（或依赖关系图）。

有一些库通过自动执行创建和提供依赖项的过程解决此问题。它们归为两类：

- 基于反射的解决方案，可在运行时连接依赖项。
- 静态解决方案，可生成在编译时连接依赖项的代码。

[Dagger](https://dagger.dev/) 是适用于 Java、Kotlin 和 Android 的热门依赖项注入库，由 Google 进行维护。Dagger 为您创建和管理依赖关系图，从而便于您在应用中使用 DI。它提供了完全静态和编译时依赖项，解决了基于反射的解决方案（如 [Guice](https://en.wikipedia.org/wiki/Google_Guice)）的诸多开发和性能问题。



### 在 Android 应用中使用 Hilt

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) 是推荐用于在 Android 中实现依赖项注入的 Jetpack 库。Hilt 通过为项目中的每个 Android 类提供容器并自动为您管理其生命周期，定义了一种在应用中执行 DI 的标准方法。Hilt 在热门 DI 库 [Dagger](https://developer.android.com/training/dependency-injection/dagger-basics) 的基础上构建而成，因而能够受益于 Dagger 提供的编译时正确性、运行时性能、可伸缩性和 Android Studio 支持。

```groovy
buildscript {
    ...
    dependencies {
        ...
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'
    }
}



...
apply plugin: 'kotlin-kapt'
apply plugin: 'dagger.hilt.android.plugin'

android {
    ...
}

dependencies {
    implementation "com.google.dagger:hilt-android:2.28-alpha"
    kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
}
```

**注意**：同时使用 Hilt 和[数据绑定](https://developer.android.com/topic/libraries/data-binding)的项目需要 Android Studio 4.0 或更高版本。Hilt 使用 [Java 8 功能](https://developer.android.com/studio/write/java8-support)。如需在项目中启用 Java 8，请将以下代码添加到 `app/build.gradle` 文件中：

```groovy
 compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
```

### Hilt 应用类 @HiltAndroidApp

所有使用 Hilt 的应用都必须包含一个带有 `@HiltAndroidApp` 注释的 `Application` 类。

`@HiltAndroidApp` 会触发 Hilt 的代码生成操作，生成的代码包括应用的一个基类，该基类充当应用级依赖项容器。

```kotlin
@HiltAndroidApp
class ExampleApplication : Application() { ... }
```

生成的这一 Hilt 组件会附加到 `Application` 对象的生命周期，并为其提供依赖项。此外，它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项。

### 将依赖项注入 Android 类

在 `Application` 类中设置了 Hilt 且有了应用级组件后，Hilt 可以为带有 `@AndroidEntryPoint` 注释的其他 Android 类提供依赖项：

```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() { ... }
```

Hilt 目前支持以下 Android 类：

- `Application`（通过使用 `@HiltAndroidApp`）
- `Activity`
- `Fragment`
- `View`
- `Service`
- `BroadcastReceiver`

如果您使用 `@AndroidEntryPoint` 为某个 Android 类添加注释，则还必须为依赖于该类的 Android 类添加注释。例如，如果您为某个 Fragment 添加注释，则还必须为使用该 Fragment 的所有 Activity 添加注释。

**注意**：在 Hilt 对 Android 类的支持方面还要注意以下几点：

- Hilt 仅支持扩展 [`ComponentActivity`](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity) 的 Activity，如 [`AppCompatActivity`](https://developer.android.com/reference/kotlin/androidx/appcompat/app/AppCompatActivity)。

- Hilt 仅支持扩展 `androidx.Fragment` 的 Fragment。
- Hilt 不支持保留的 Fragment。

`@AndroidEntryPoint` 会为项目中的每个 Android 类生成一个单独的 Hilt 组件。这些组件可以从它们各自的父类接收依赖项，如[组件层次结构](https://developer.android.com/training/dependency-injection/hilt-android#component-hierarchy)中所述。

如需从组件获取依赖项，请使用 `@Inject` 注释执行字段注入：

```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {

  @Inject lateinit var analytics: AnalyticsAdapter
  ...
}
```

**注意**：由 Hilt 注入的字段不能为私有字段。尝试使用 Hilt 注入私有字段会导致编译错误。

Hilt 注入的类可以有同样使用注入的其他基类。如果这些类是抽象类，则它们不需要 `@AndroidEntryPoint` 注释。

如需详细了解 Android 类被注入的是哪个生命周期回调，请参阅[组件生命周期](https://developer.android.com/training/dependency-injection/hilt-android#component-lifetimes)。

### 定义 Hilt 绑定

为了执行字段注入，Hilt 需要知道如何从相应组件提供必要依赖项的实例。“绑定”包含将某个类型的实例作为依赖项提供所需的信息。

向 Hilt 提供绑定信息的一种方法是构造函数注入。在某个类的构造函数中使用 `@Inject` 注释，以告知 Hilt 如何提供该类的实例：

```kotlin
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
) { ... }
```

在一个类的代码中，带有注释的构造函数的参数即是该类的依赖项。在本例中，`AnalyticsService` 是 `AnalyticsAdapter` 的一个依赖项。因此，Hilt 还必须知道如何提供 `AnalyticsService` 的实例。

**注意**：在构建时，Hilt 会为 Android 类生成 [Dagger](https://developer.android.com/training/dependency-injection/dagger-basics) 组件。然后，Dagger 会走查您的代码，并执行以下步骤：

- 构建并验证依赖关系图，确保没有未满足的依赖关系且没有依赖循环。
- 生成它在运行时用来创建实际对象及其依赖项的类。

### Hilt 模块

有时，类型不能通过构造函数注入。发生这种情况可能有多种原因。例如，您不能通过构造函数注入接口。此外，您也不能通过构造函数注入不归您所有的类型，如来自外部库的类。在这些情况下，您可以使用 Hilt 模块向 Hilt 提供绑定信息。

Hilt 模块是一个带有 `@Module` 注释的类。与 [Dagger 模块](https://developer.android.com/training/dependency-injection/dagger-android#dagger-modules)一样，它会告知 Hilt 如何提供某些类型的实例。与 Dagger 模块不同的是，您必须使用 `@InstallIn` 为 Hilt 模块添加注释，以告知 Hilt 每个模块将用在或安装在哪个 Android 类中。

#### 使用 @Binds 注入接口实例

以 `AnalyticsService` 为例。如果 `AnalyticsService` 是一个接口，则您无法通过构造函数注入它，而应向 Hilt 提供绑定信息，方法是在 Hilt 模块内创建一个带有 `@Binds` 注释的抽象函数。

`@Binds` 注释会告知 Hilt 在需要提供接口的实例时要使用哪种实现。

带有注释的函数会向 Hilt 提供以下信息：

- 函数返回类型会告知 Hilt 函数提供哪个接口的实例。
- 函数参数会告知 Hilt 要提供哪种实现。

```kotlin
interface AnalyticsService {
  fun analyticsMethods()
}

// Constructor-injected, because Hilt needs to know how to
// provide instances of AnalyticsServiceImpl, too.
class AnalyticsServiceImpl @Inject constructor(
  ...
) : AnalyticsService { ... }

@Module
@InstallIn(ActivityComponent::class)
abstract class AnalyticsModule {

  @Binds
  abstract fun bindAnalyticsService(
    analyticsServiceImpl: AnalyticsServiceImpl
  ): AnalyticsService
}
```

Hilt 模块 `AnalyticsModule` 带有 `@InstallIn(ActivityComponent::class)` 注释，因为您希望 Hilt 将该依赖项注入 `ExampleActivity`。此注释意味着，`AnalyticsModule` 中的所有依赖项都可以在应用的所有 Activity 中使用。

#### 使用 @Provides 注入实例

接口不是无法通过构造函数注入类型的唯一一种情况。如果某个类不归您所有（因为它来自外部库，如 [Retrofit](https://square.github.io/retrofit/)、[`OkHttpClient`](https://square.github.io/okhttp/) 或 [Room 数据库](https://developer.android.com/topic/libraries/architecture/room)等类），或者必须使用[构建器模式](https://en.wikipedia.org/wiki/Builder_pattern)创建实例，也无法通过构造函数注入。

接着前面的例子来讲。如果 `AnalyticsService` 类不直接归您所有，您可以告知 Hilt 如何提供此类型的实例，方法是在 Hilt 模块内创建一个函数，并使用 `@Provides` 为该函数添加注释。

带有注释的函数会向 Hilt 提供以下信息：

- 函数返回类型会告知 Hilt 函数提供哪个类型的实例。
- 函数参数会告知 Hilt 相应类型的依赖项。
- 函数主体会告知 Hilt 如何提供相应类型的实例。每当需要提供该类型的实例时，Hilt 都会执行函数主体。

```kotlin
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

  @Provides
  fun provideAnalyticsService(
    // Potential dependencies of this type
  ): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .build()
               .create(AnalyticsService::class.java)
  }
}
```

#### 为同一类型提供多个绑定

如果您需要让 Hilt 以依赖项的形式提供同一类型的不同实现，必须向 Hilt 提供多个绑定。您可以使用限定符为同一类型定义多个绑定。

限定符是一种注释，当为某个类型定义了多个绑定时，您可以使用它来标识该类型的特定绑定。

首先，定义要用于为 `@Binds` 或 `@Provides` 方法添加注释的限定符：

[KOTLIN](https://developer.android.com/training/dependency-injection/hilt-android#kotlin)[JAVA](https://developer.android.com/training/dependency-injection/hilt-android#java)

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherInterceptorOkHttpClient
```

然后，Hilt 需要知道如何提供与每个限定符对应的类型的实例。在这种情况下，您可以使用带有 `@Provides` 的 Hilt 模块。这两种方法具有相同的返回类型，但限定符将它们标记为两个不同的绑定：

```kotlin
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

  @AuthInterceptorOkHttpClient
  @Provides
  fun provideAuthInterceptorOkHttpClient(
    authInterceptor: AuthInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(authInterceptor)
               .build()
  }

  @OtherInterceptorOkHttpClient
  @Provides
  fun provideOtherInterceptorOkHttpClient(
    otherInterceptor: OtherInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(otherInterceptor)
               .build()
  }
}
```

您可以通过使用相应的限定符为字段或参数添加注释来注入所需的特定类型：

```kotlin
// As a dependency of another class.
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

  @Provides
  fun provideAnalyticsService(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
  ): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .client(okHttpClient)
               .build()
               .create(AnalyticsService::class.java)
  }
}

// As a dependency of a constructor-injected class.
class ExampleServiceImpl @Inject constructor(
  @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
) : ...

// At field injection.
@AndroidEntryPoint
class ExampleActivity: AppCompatActivity() {

  @AuthInterceptorOkHttpClient
  @Inject lateinit var okHttpClient: OkHttpClient
}
```

最佳做法是，如果您向某个类型添加限定符，应向提供该依赖项的所有可能的方式添加限定符。让基本实现或通用实现不带限定符容易出错，并且可能会导致 Hilt 注入错误的依赖项。

#### Hilt 中的预定义限定符

Hilt 提供了一些预定义的限定符。例如，由于您可能需要来自应用或 Activity 的 `Context` 类，因此 Hilt 提供了 `@ApplicationContext` 和 `@ActivityContext` 限定符。

假设本例中的 `AnalyticsAdapter` 类需要 Activity 的上下文。以下代码演示了如何向 `AnalyticsAdapter` 提供 Activity 上下文：

```kotlin
class AnalyticsAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private val service: AnalyticsService
) { ... }
```

如需了解 Hilt 中提供的其他预定义绑定，请参阅[组件默认绑定](https://developer.android.com/training/dependency-injection/hilt-android#component-default)。



### 为 Android 类生成的组件

对于您可以从中执行字段注入的每个 Android 类，都有一个关联的 Hilt 组件，您可以在 `@InstallIn` 注释中引用该组件。每个 Hilt 组件负责将其绑定注入相应的 Android 类。

前面的示例演示了如何在 Hilt 模块中使用 `ActivityComponent`。

Hilt 提供了以下组件：

| Hilt 组件                   | 注入器面向的对象                           |
| :-------------------------- | :----------------------------------------- |
| `ApplicationComponent`      | `Application`                              |
| `ActivityRetainedComponent` | `ViewModel`                                |
| `ActivityComponent`         | `Activity`                                 |
| `FragmentComponent`         | `Fragment`                                 |
| `ViewComponent`             | `View`                                     |
| `ViewWithFragmentComponent` | 带有 `@WithFragmentBindings` 注释的 `View` |
| `ServiceComponent`          | `Service`                                  |

**注意**：Hilt 不会为广播接收器生成组件，因为 Hilt 直接从 `ApplicationComponent` 注入广播接收器。

### 组件生命周期

Hilt 会按照相应 Android 类的生命周期自动创建和销毁生成的组件类的实例。

| 生成的组件                  | 创建时机                 | 销毁时机                  |
| :-------------------------- | :----------------------- | :------------------------ |
| `ApplicationComponent`      | `Application#onCreate()` | `Application#onDestroy()` |
| `ActivityRetainedComponent` | `Activity#onCreate()`    | `Activity#onDestroy()`    |
| `ActivityComponent`         | `Activity#onCreate()`    | `Activity#onDestroy()`    |
| `FragmentComponent`         | `Fragment#onAttach()`    | `Fragment#onDestroy()`    |
| `ViewComponent`             | `View#super()`           | 视图销毁时                |
| `ViewWithFragmentComponent` | `View#super()`           | 视图销毁时                |
| `ServiceComponent`          | `Service#onCreate()`     | `Service#onDestroy()`     |

**注意**：`ActivityRetainedComponent` 在配置更改后仍然存在，因此它在第一次调用 `Activity#onCreate()` 时创建，在最后一次调用 `Activity#onDestroy()` 时销毁。

### 组件作用域

默认情况下，Hilt 中的所有绑定都未限定作用域。这意味着，每当应用请求绑定时，Hilt 都会创建所需类型的一个新实例。

在本例中，每当 Hilt 提供 `AnalyticsAdapter` 作为其他类型的依赖项或通过字段注入提供它（如在 `ExampleActivity` 中）时，Hilt 都会提供 `AnalyticsAdapter` 的一个新实例。

不过，Hilt 也允许将绑定的作用域限定为特定组件。Hilt 只为绑定作用域限定到的组件的每个实例创建一次限定作用域的绑定，对该绑定的所有请求共享同一实例。

下表列出了生成的每个组件的作用域注释：

| Android 类                                 | 生成的组件                  | 作用域                   |
| :----------------------------------------- | :-------------------------- | :----------------------- |
| `Application`                              | `ApplicationComponent`      | `@Singleton`             |
| `View Model`                               | `ActivityRetainedComponent` | `@ActivityRetainedScope` |
| `Activity`                                 | `ActivityComponent`         | `@ActivityScoped`        |
| `Fragment`                                 | `FragmentComponent`         | `@FragmentScoped`        |
| `View`                                     | `ViewComponent`             | `@ViewScoped`            |
| 带有 `@WithFragmentBindings` 注释的 `View` | `ViewWithFragmentComponent` | `@ViewScoped`            |
| `Service`                                  | `ServiceComponent`          | `@ServiceScoped`         |

在本例中，如果您使用 `@ActivityScoped` 将 `AnalyticsAdapter` 的作用域限定为 `ActivityComponent`，Hilt 会在相应 Activity 的整个生命周期内提供 `AnalyticsAdapter` 的同一实例：

[KOTLIN](https://developer.android.com/training/dependency-injection/hilt-android#kotlin)[JAVA](https://developer.android.com/training/dependency-injection/hilt-android#java)

```kotlin
@ActivityScoped
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
) { ... }
```

**注意**：将绑定的作用域限定为某个组件的成本可能很高，因为提供的对象在该组件被销毁之前一直保留在内存中。请在应用中尽量少用限定作用域的绑定。如果绑定的内部状态要求在某一作用域内使用同一实例，或者绑定的创建成本很高，那么将绑定的作用域限定为某个组件是一种恰当的做法。

假设 `AnalyticsService` 的内部状态要求每次都使用同一实例 - 不只是在 `ExampleActivity` 中，而是在应用中的任何位置。在这种情况下，将 `AnalyticsService` 的作用域限定为 `ApplicationComponent` 是一种恰当的做法。结果是，每当组件需要提供 `AnalyticsService` 的实例时，都会提供同一实例。

以下示例演示了如何将绑定的作用域限定为 Hilt 模块中的某个组件。绑定的作用域必须与其安装到的组件的作用域一致，因此在本例中，您必须将 `AnalyticsService` 安装在 `ApplicationComponent` 中，而不是安装在 `ActivityComponent` 中：

[KOTLIN](https://developer.android.com/training/dependency-injection/hilt-android#kotlin)[JAVA](https://developer.android.com/training/dependency-injection/hilt-android#java)

```kotlin
// If AnalyticsService is an interface.
@Module
@InstallIn(ApplicationComponent::class)
abstract class AnalyticsModule {

  @Singleton
  @Binds
  abstract fun bindAnalyticsService(
    analyticsServiceImpl: AnalyticsServiceImpl
  ): AnalyticsService
}

// If you don't own AnalyticsService.
@Module
@InstallIn(ApplicationComponent::class)
object AnalyticsModule {

  @Singleton
  @Provides
  fun provideAnalyticsService(): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .build()
               .create(AnalyticsService::class.java)
  }
}
```

### 组件层次结构

将模块安装到组件后，其绑定就可以用作该组件中其他绑定的依赖项，也可以用作组件层次结构中该组件下的任何子组件中其他绑定的依赖项：

![ViewWithFragmentComponent 位于 FragmentComponent 下。FragmentComponent 和 ViewComponent 位于 ActivityComponent 下。ActivityComponent 位于 ActivityRetainedComponent 下。ActivityRetainedComponent 和 ServiceComponent 位于 ApplicationComponent 下。](https://developer.android.com/images/training/dependency-injection/hilt-hierarchy.svg)**图 1.** Hilt 生成的组件的层次结构。

**注意**：默认情况下，如果您在视图中执行字段注入，`ViewComponent` 可以使用 `ActivityComponent` 中定义的绑定。如果您还需要使用 `FragmentComponent` 中定义的绑定并且视图是 Fragment 的一部分，应将 `@WithFragmentBindings` 注释和 `@AndroidEntryPoint` 一起使用。



### Hilt 和 Dagger

Hilt 在依赖项注入库 [Dagger](https://dagger.dev/) 的基础上构建而成，提供了一种将 Dagger 纳入 Android 应用的标准方法。

关于 Dagger，Hilt 的目标如下：

- 简化 Android 应用的 Dagger 相关基础架构。
- 创建一组标准的组件和作用域，以简化设置、提高可读性以及在应用之间共享代码。
- 提供一种简单的方法来为各种构建类型（如测试、调试或发布）配置不同的绑定。

由于 Android 操作系统会实例化它自己的许多框架类，因此在 Android 应用中使用 Dagger 要求您编写大量的样板。Hilt 可减少在 Android 应用中使用 Dagger 所涉及的样板代码。Hilt 会自动生成并提供以下各项：

- **用于将 Android 框架类与 Dagger 集成的组件** - 您不必手动创建。
- **作用域注释** - 与 Hilt 自动生成的组件一起使用。
- **预定义的绑定** - 表示 Android 类，如 `Application` 或 `Activity`。
- **预定义的限定符** - 表示 `@ApplicationContext` 和 `@ActivityContext`。

Dagger 和 Hilt 代码可以共存于同一代码库中。不过，在大多数情况下，最好使用 Hilt 管理您在 Android 上对 Dagger 的所有使用。如需将使用 Dagger 的项目迁移到 Hilt，请参阅[迁移指南](https://dagger.dev/hilt/migration-guide)和[“将 Dagger 应用迁移到 Hilt”Codelab](https://codelabs.developers.google.com/codelabs/android-dagger-to-hilt)。

### 在多模块应用中使用 Hilt





## Room 持久性库

[Android 开发者 ｜ 使用 Room 将数据保存到本地数据库](https://developer.android.com/training/data-storage/room?hl=zh-cn)



Room 在 SQLite 上提供了一个抽象层，以便在充分利用 SQLite 的强大功能的同时，能够流畅地访问数据库。

```groovy
  def room_version = "2.2.6"
	//K o t lin
  implementation "androidx.room:room-runtime:$room_version"
  kapt "androidx.room:room-compiler:$room_version"
  // optional - Kotlin Extensions and Coroutines support for Room
  implementation "androidx.room:room-ktx:$room_version"
  // optional - Test helpers
  testImplementation "androidx.room:room-testing:$room_version"

  //Java
  implementation "androidx.room:room-runtime:$room_version"
  annotationProcessor "androidx.room:room-compiler:$room_version"
  // optional - RxJava support for Room
  implementation "androidx.room:room-rxjava2:$room_version"
  // optional - Guava support for Room, including Optional and ListenableFuture
  implementation "androidx.room:room-guava:$room_version"
  // optional - Test helpers
  testImplementation "androidx.room:room-testing:$room_version"
```



### Room 不同组件之间的关系

![img](https://developer.android.com/images/training/data-storage/room_architecture.png?hl=zh-cn)

Room 包含 3 个主要组件：

- [**数据库**](https://developer.android.com/reference/androidx/room/Database?hl=zh-cn)：包含数据库持有者，并作为应用已保留的持久关系型数据的底层连接的主要接入点。

  使用 [`@Database`](https://developer.android.com/reference/androidx/room/Database?hl=zh-cn) 注释的类应满足以下条件：

  - 是扩展 [`RoomDatabase`](https://developer.android.com/reference/androidx/room/RoomDatabase?hl=zh-cn) 的抽象类。
  - 在注释中添加与数据库关联的实体列表。
  - 包含具有 0 个参数且返回使用 [`@Dao`](https://developer.android.com/reference/androidx/room/Dao?hl=zh-cn) 注释的类的抽象方法。

  在运行时，您可以通过调用 [`Room.databaseBuilder()`](https://developer.android.com/reference/androidx/room/Room?hl=zh-cn#databaseBuilder(android.content.Context, java.lang.Class, java.lang.String)) 或 [`Room.inMemoryDatabaseBuilder()`](https://developer.android.com/reference/androidx/room/Room?hl=zh-cn#inMemoryDatabaseBuilder(android.content.Context, java.lang.Class)) 获取 [`Database`](https://developer.android.com/reference/androidx/room/Database?hl=zh-cn) 的实例。

  

- [**Entity**](https://developer.android.com/training/data-storage/room/defining-data?hl=zh-cn)：表示数据库中的表。

- [**DAO**](https://developer.android.com/training/data-storage/room/accessing-data?hl=zh-cn)：包含用于访问数据库的方法。

应用使用 Room 数据库来获取与该数据库关联的数据访问对象 (DAO)。然后，应用使用每个 DAO 从数据库中获取实体，然后再将对这些实体的所有更改保存回数据库中。 最后，应用使用实体来获取和设置与数据库中的表列相对应的值。

