

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

