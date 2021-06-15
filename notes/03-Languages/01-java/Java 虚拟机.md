Java 虚拟机

## JVM

- JVM概述
  - 认识JVM
    - 是什么
      - Java Virtual Machine
      - Java程序的运行环境
    - 有什么
    - 能干什么
  - Java如何实现平台无关
- JVM规范
  - 理解JVM规范的作用
  - 了解JVM规范里面规定的主要内容
- Class文件格式
  - 阅读class字节码文件
  - 阅读“虚拟机汇编语言"表示的Java类
  - ASM开发实战
    - 认识ASM
    -  ASM编程模型和核心API
    - ASM开发实战:实现统计方法的运行时间
- 类加载、连接和初始化
  - 理解类从加载、连接、初始化到卸载的生命
  - 类加载与类加载器
  - 双亲委派模型
  - 类连接
  - 类初始化
  - 类卸载
- JVM内存分配
  - JVM简化架构、内存模型
  - 理解栈、堆、方法区之间的交互关系
- JVM堆内存
  - 堆的结构
  - 对象的内存布局
  - 内存分配参数
    - Trace跟踪参数
    - GC日志格式
    - Java堆的参数
    - Java栈的参数
    - 元空间的参数
- 字节码执行引擎
  - 栈帧
  - 局部变量表
  - 操作数栈
  - 动态连接
  - 方法返回地址
  - 字节码执行引擎
  - 栈帧、运行期操作数栈和局部变量表之间的交互关系
  - 方法调用
    - 静态分派
    - 动态分派
- 垃圾回收基础
  - 什么是垃圾
  - 如何判定是垃圾
  - 如何回收
  - 根搜索算法
  - 引用分类
  - 跨代引用
  - 记忆集
  - 写屏障
  - GC类型
  - Stop-The-World
  - 垃圾收集类型
- 垃圾收集算法
  - 标记清除法
  - 复制算法
  - 分配担保
  - 标记整理法
- 垃圾收集器
  - HotSpot中的收集器
  - 串行收集器
  - 并行收集器
  - 新生代Parallel Scavenge收集器
  - CMS收集器
  - G1收集器
    - G1收集器新生代回收过程
    - G1收集器老年代回收过程
  - 了解ZGC收集器
  - GC性能指标
  -  JVM内存配置原则
- JVM对高效并发的支持
  - Java内存模型
  - 内存间的交互操作
  - 多线程的可见性
  - 有序性
  - 指令重排
  - 线程安全的处理方法
  - 锁优化
  - 自旋锁
  - 锁消除
  - 锁粗化
  - 轻量级锁
  - 偏向锁
- 性能监控与故障处理工具
  - 命令行工具: jps、 jinfo. jstack. jmap、jstat、jstatd、 jcmd 
  - 图形化工具: jconsole、 jmc、 visualvm
  - 两种远程连接方式: JMX、 jstatd
- JVM调优实战
  - JVM调优
    - 调什么
    - 如何调
    - 调优的目标是什么
  - JVM调优策略
  - JVM调优实战
  - JVM调优冷思考
  - JVM调优经验
  - 分析和处理内存泄漏
  - 调优实战





## JVM-理论+实战 构建完整JVM知识体系

### JVM概述

- 认识JVM

  - 是什么
    - Java Virtual Machine
    - 所谓虚拟机是指:通过软件模拟的具有完整硬件系统功能的、运行在一个完全隔离环境中的计算机系统
    - JVM是通过软件来模拟Java字节码的指令集,是Java程序的运行环境
  - 有什么
    - JVM 组成架构图 todo
  - 能干什么
    - 通过ClassLoader寻找和装载class文件
    - 解释字节码成为指令并执行,提供class文件的运行环境
    - 进行运行期间的内存分配和垃圾回收
    - 提供与硬件交互的平台

- Java如何实现平台无关

  - JVM是Java平台无关的保障

  <div align="center"> <img src="https://tva1.sinaimg.cn/large/008eGmZEly1gpf4srk9q7j30s00momzx.jpg" width="400px"> </div><br>

### JVM规范

- 理解JVM规范的作用
  - Java虚拟机规范为不同的硬件平台提供了一种编译 Java技术代码的规范
  - 该规范使Java软件独立于平台,因为编译是针对作为虚拟机的“一般机器”而做
  - 这个“一般机器”可用软件模拟并运行于各种现存的计算机系统,也可用硬件来实现
- 了解JVM规范里面规定的主要内容
  - 参考书籍
    - Java虚拟机规范.Java SE-8-中文版 
      - 链接: https://pan.baidu.com/s/1yf1-FL5hnC2Rm4kirOP0Pw  密码: v5g3
    - [The Java Virtual Machine Specification, Java SE 8 Edition](https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf)
  - 主要内容
    - 字节码指令集：目录：2.11，6，7
    - Class 文件格式：目录：2.1
    - 数据类型和值：目录：2.2，2.3，2.4
    - 运行时数据区：目录：2.5
    - 栈帧：目录：2.6
    - 特殊方法：目录：2.9
      - < init>  实例初始化方法,通过JVM的invokespecial指令来调用
      - < clinit> 类或接口的初始化方法,不包含参数,返回void
    - 类库：目录：2.12
    - 异常：目录：2.10

### Class文件格式

- 概述

  - Class文件是JVM的输入, Java虚拟机规范中定义了Class文件的结构。Class文件是JVM实现平台无关、技术无关的基础
  - Class文件是一组以8字节为单位的字节流,各个数据项目按顺序紧凑排列
  - 对于占用空间大于8字节的数据项,按照高位在前的方式分割成多个8字节进行存储
  - Class文件格式里面只有两种类型:无符号数、表
    - 无符号数:基本数据类型,以u1、u2、u4、u8来代表几个字节的无符号数
    - 表:由多个无符号数和其它表构成的复合数据类型,通常以“_info" 结尾

- 阅读class字节码文件

  - IDE
  - 16进制查看软件：[UltraEdit](https://www.ultraedit.com/downloads/uex.html)
  - javap 

- 字节码信息描述规则

  - 对于 JVM 来说，其 **采用了字符串的形式来描述数据类型、成员变量及成员函数 这三类**
  - 原始数据类型
    -  `byte、char、double、float、int、long、short、boolean` 
    - `"B"、"C"、"D"、"F"、"I"、"J"、"S"、"Z"`。
  - 引用数据类型
    - **ClassName => L + 全路径类名（其中的 "." 替换为 "/"，最后加分号）**，
    - 例如 `String => Ljava/lang/String;`。
  - 数组（引用类型）
    - **不同类型的数组 => "[该类型对应的描述名"**，
    - 例如 `int 数组 => "[I"，`
    - `String 数组 => "[Ljava/lang/Sting;"，`
    - `二维 int 数组 => "[[I"`。
  - 变量类型描述符
    - FiledType 的可选类型    BaseType | ObjectType | ArrayType    
    - BaseType：    B | C | D | F | I | J | S | Z   
    -  ObjectType：    L + 全路径ClassName；    
    - ArrayType：    [ComponentType：
    - `ComponentType` 是一种 JVM 规范中新定义的类型，不过它是 **由 FiledType 构成，其可选类型也包含 BaseType、ObjectType、ArrayType 这三种**
  - 方法类型描述符
    -  ( ParameterDescriptor* ) ReturnDescriptor
    - **MethodDescriptor 由两个部分组成，括号内的是参数的数据类型描述，表示有 0 至多个 ParameterDescriptor，最后是返回值类型描述**。
    - **void 的描述规则为 "V"**。例如，一个 `void hello(String str)` 的函数 => `（Ljava/lang/String;)V`。

- 阅读“虚拟机汇编语言"表示的Java类

  - 目录：第四章
  - 4.1：ClassFile 结构

  ```
  ClassFile {
      u4             magic;	//魔数
      u2             minor_version;	//副版本号
      u2             major_version;	//主版本号
      u2             constant_pool_count;	//常量池计数器
      cp_info        constant_pool[constant_pool_count-1];	///常量池
      u2             access_flags;	//访问标志
      u2             this_class;	//类索引
      u2             super_class;	//父类索引
      u2             interfaces_count;	//接口计数器
      u2             interfaces[interfaces_count];	//接口表
      u2             fields_count;	//字段计数器
      field_info     fields[fields_count];	//字段表
      u2             methods_count;	//方法计数器
      method_info    methods[methods_count];	//方发表
      u2             attributes_count;	//属性计数器
      attribute_info attributes[attributes_count];	//属性表
  }
  ```

  - `magic`：每个 Class 文件的头 4 个字节被称为魔数（Magic Number），它的唯一作用是确定这个文件是否为一个能被虚拟机所接受的 Class 文件，值固定为：**0xCAFEBABE**（咖啡宝贝）

  - `minor_version`：2 个字节，表示当前 Class 文件的次版号

  - `major_version`：2 个字节，表示当前 Class 文件的主版本号，例如 JDK 1.8 就是 52.0

  - `constant_pool_count`：**常量池数组元素个数**。

  - `constant_pool`：**常量池，是一个存储了 cp_info 信息的数组，每一个 Class 文件都有一个与之对应的常量池。（注意：cp_info 数组的索引从 1 开始）**

    - 假设一个常量池的容量（偏移地址:0x00000008）为十六进制数 0x0016，即十进制的 22，这就代表常量池中有 21 项常量，索引值范围为 1~21。**在 Class 文件格式规范制定之时，设计者将第 0 项常量空出来是有特殊考虑的，这样做的目的在 于满足后面某些指向常量池的索引值的数据在特定情况下需要表达 “不引用任何一个常量池项”的含义**。

    - 而常量池中主要存放两大类常量：**字面量**（Literal）和**符号引用**（Symbolic References）。

      - 字面量比较接近于 Java 语言层面的常量概念，如文本字符串、声明为 final 的常量值等。
      - 每一个 cp_info 的第一个字节（即一个 u1 类型的标志位）标识了当前常量项的类型，其后才是具体的常量项内容。

      | **常量项的类型**                 | 标志 | 描述                                                         |
      | -------------------------------- | ---- | ------------------------------------------------------------ |
      | CONSTANT_Utf8_info               | 1    | 用于存储UTF-8编码的字符串，它真正包含了字符串的内容。        |
      | CONSTANT_Integer_info            | 3    | 表示int型数据的信息                                          |
      | CONSTANT_Float_info              | 4    | 表示float型数据的信息                                        |
      | CONSTANT_Long_info               | 5    | 表示long型数据的信息                                         |
      | CONSTANT_Double_info             | 6    | 表示double型数据的信息                                       |
      | CONSTANT_Class_info              | 7    | 表示类或接口的信息                                           |
      | CONSTANT_String_info             | 8    | 表示字符串，但该常量项本身不存储字符串的内容，它仅仅只存储了一个索引值 |
      | CONSTANT_Fieldref_info           | 9    | 字段的符号引用                                               |
      | CONSTANT_Methodref_info          | 10   | 类中方法的符号引用                                           |
      | CONSTANT_InterfaceMethodref_info | 11   | 接口中方法的符号引用                                         |
      | CONSTANT_NameAndType_info        | 12   | 描述类的成员域或成员方法相关的信息                           |
      | CONSTANT_MethodHandle_info       | 15   | 表示方法句柄信息，其和反射相关                               |
      | CONSTANT_MethodType_info         | 16   | 标识方法类型，仅包含方法的参数类型和返回值类型               |
      | CONSTANT_InvokeDynamic_info      | 18   | 表示一个动态方法调用点，用于 invokeDynamic 指令，Java 7引入  |

    - ```
      常量项 Utf8 的数据结构如下所示：
      CONSTANT_Utf8_info {
              u1 tag; 
              u2 length; 
              u1 bytes[length]; 
      }
      1）、tag：值为 1，表示是 CONSTANT_Utf8_info 类型表。
      2）、length：length 表示 bytes 的长度，比如 length = 10，则表示接下来的数据是 10 个连续的 u1 类型数据。
      3）、bytes：u1 类型数组，保存有真正的常量数据。
      
      
      常量项 Class、Filed、Method、Interface、String 的数据结构分别如下所示：
      CONSATNT_Class_info {
              u1 tag;
              u2 name_index; 
          }
      
          CONSTANT_Fieldref_info {
              u1 tag;
              u2 class_index;
              u2 name_and_type_index;
          }
      
          CONSTANT_MethodType_info {
              u1 tag;
              u2 descriptor_index;
          }
      
          CONSTANT_InterfaceMethodref_info {
              u1 tag;
              u2 class_index;
              u2 name_and_type_index;
          }
      
          CONSTANT_String_info {
              u1 tag;
              u2 string_index;
          }
      
          CONSATNT_NameAndType_info {
              u1 tag;
              u2 name_index;
              u2 descriptor_index
          }
          
          其元素含义如下所示：
      
      
      name_index：指向常量池中索引为 name_index 的常量表。比如 name_index = 6，表明它指向常量池中第 6 个常量。
      
      
      class_index：指向当前方法、字段等的所属类的引用。
      
      
      name_and_type_index：指向当前方法、字段等的名字和类型的引用。
      
      
      name_index：指向某字段或方法等的名称字符串的引用。
      
      
      descriptor_index：指向某字段或方法等的类型字符串的引用。
      
      
      
      常量项 Integer、Long、Float、Double 对应的数据结构如下所示：
          CONSATNT_Integer_info {
              u1 tag;
              u4 bytes;
          }
      
          CONSTANT_Long_info {
              u1 tag;
              u4 high_bytes;
              u4 low_bytes;
          }
      
          CONSTANT_Float_info {
              u1 tag;
              u4 bytes;
          }
      
          CONSTANT_Double_info {
              u1 tag;
              u4 high_bytes;
              u4 low_bytes;
          }
      
      
      
      ```

      

  - `access_flags`：**表示当前类的访问权限，例如：public、private**。

    - Class 的 access_flags 取值类型如下表

    - | 标志名         | 标志值 | 标志含义                  |
      | -------------- | ------ | ------------------------- |
      | ACC_PUBLIC     | 0x0001 | public类型                |
      | ACC_FINAL      | 0x0010 | final类型                 |
      | ACC_SUPER      | 0x0020 | 使用新的invokespecial语义 |
      | ACC_INTERFACE  | 0x0200 | 接口类型                  |
      | ACC_ABSTRACT   | 0x0400 | 抽象类型                  |
      | ACC_SYNTHETIC  | 0x1000 | 该类不由用户代码生成      |
      | ACC_ANNOTATION | 0x2000 | 注解类型                  |
      | ACC_ENUM       | 0x4000 | 枚举类型                  |

      Filed 的 access_flag 取值类型如下表所示：

      | 名称          | 值     | 描述                    |
      | ------------- | ------ | ----------------------- |
      | ACC_PUBLIC    | 0x0001 | public                  |
      | ACC_PRIVATE   | 0x0002 | private                 |
      | ACC_PROTECTED | 0x0004 | protected               |
      | ACC_STATIC    | 0x0008 | static                  |
      | ACC_FINAL     | 0x0010 | final                   |
      | ACC_VOLATILE  | 0x0040 | volatile                |
      | ACC_TRANSIENT | 0x0080 | transient，不能被序列化 |
      | ACC_SYNTHETIC | 0x1000 | 由编译器自动生成        |
      | ACC_ENUM      | 0x4000 | enum，字段为枚举类型    |

    - Method 的 access_flag 取值如下表所示：

     

    | 名称             | 值     | 描述                     |
    | ---------------- | ------ | ------------------------ |
    | ACC_PUBLIC       | 0x0001 | public                   |
    | ACC_PRIVATE      | 0x0002 | private                  |
    | ACC_PROTECTED    | 0x0004 | protected                |
    | ACC_STATIC       | 0x0008 | static                   |
    | ACC_FINAL        | 0x0010 | final                    |
    | ACC_SYNCHRONIZED | 0x0020 | synchronized             |
    | ACC_BRIDGE       | 0x0040 | bridge，方法由编译器产生 |
    | ACC_VARARGS      | 0x0080 | 该方法带有变长参数       |
    | ACC_NATIVE       | 0x0100 | native                   |
    | ACC_ABSTRACT     | 0x0400 | abstract                 |
    | ACC_STRICT       | 0x0800 | strictfp                 |
    | ACC_SYNTHETIC    | 0x1000 | 方法由编译器生成         |

    

  - `this_class 和 super_class`：**存储了指向常量池数组元素的索引，this_class  中索引指向的内容为当前类名，而 super_class 中索引则指向其父类类名**。

  - `interfaces_count 和 interfaces`：**同上，它们存储的也只是指向常量池数组元素的索引。其内容分别表示当前类实现了多少个接口和对应的接口类类名**。

  - `fields_count 和 fields`：**表示成员变量的数量和其信息，信息由  field_info 结构体表示**。

    - 字段表（field_info）用于描述接口或者类中声明的变量。字段（field）包括类级变量以及实例级变量，但 **不包括在方法内部声明的局部变量**。

    - ```java
      field_info {
              u2              access_flags;
              u2              name
              u2              descriptor_index
              u2              attributes_count
              attribute_info  attributes[attributes_count]
       }
      ```

      

  - `methods_count 和 methods`：**表示成员函数的数量和它们的信息，信息由 method_info 结构体表示**。

    - ```
          method_info {
              u2              access_flags;
              u2              name
              u2              descriptor_index
              u2              attributes_count
              attribute_info  attributes[attributes_count]
          }
      filed_info 与 method_info 都包含有 访问标志、名字引用、描述信息、属性数量与存储属性 的数据结构。对于 method_info 所描述的成员函数来说，它的内容经过编译之后得到的 Java 字节码会保存在属性之中。
      注意：类构造器为 “< clinit >” 方法，而实例构造器为 “< init >” 方法。
      
      ```

      

  - `attributes_count 和 attributes`：**表示当前类的属性信息，每一个属性都有一个与之对应的 attribute_info 结构。常见的属性信息如调试信息，它需要记录某句代码对应源代码的哪一行，此外，如函数对应的 JVM 字节码、注解信息也是属性信息**。

    - attribute_info 的数据结构伪代码如下所示：

      ```jvm
          attribute_info {  
              u2 attribute_name_index;
              u4 attribute_length;
              u1 info[attribute_length];
          }
      ```

    - attribute_info 中的各个元素的含义如下所示：

      - `attribute_name_index`：**为 CONSTANT_Utf8 类型常量项的索引，表示属性的名称**。
      - `attribute_length`：**属性的长度**。
      - `info`：**属性具体的内容**。

    - attribute_name_index

      - 1）、`ConstantValue`：**仅出现在 filed_info 中，描述常量成员域的值，通知虚拟机自动为静态变量赋值。对于非 static 类型的变量（也就是实例变量）的赋值是在实例构造器方法中进行的;而对 于类变量，则有两种方式可以选择：在类构造器方法中或者使用 ConstantValue 属性。如果变量没有被 final 修饰，或者并非基本类型及字 符串，则将会选择在方法中进行初始化**。

        2）、`Code`：**仅出现 method_info 中，描述函数内容，即该函数内容编译后得到的虚拟机指令，try/catch 语句对应的异常处理表等等**。

        3）、`StackMapTable`：**在 JDK 1.6 发布后增加到了 Class 文件规范中，它是一个复杂的变长属性。这个属性会在虚拟机类加载的字节码验证阶段被新类型检查验证器（Type Checker）使用，目的在于代替以前比较消耗性能的基于数据流 分析的类型推导验证器。它省略了在运行期通过数据流分析去确认字节码的行为逻辑合法性的步骤，而是在编译阶 段将一系列的验证类型（Verification Types）直接记录在 Class 文件之中，通过检查这些验证类型代替了类型推导过程，从而大幅提升了字节码验证的性能。这个验证器在 JDK 1.6 中首次提供，并在 JDK 1.7 中强制代替原本基于类型推断的字节码验证器。StackMapTable 属性中包含零至多个栈映射帧（Stack Map Frames），其中的类型检查验证器会通过检查目标方法的局部变量和操作数栈所需要的类型来确定一段字节码指令是否符合逻辑约束**。

        4）、`Exceptions`：**当函数抛出异常或错误时，method_info 将会保存此属性**。

        5）、InnerClasses：用于记录内部类与宿主类之间的关联。

        6）、EnclosingMethod

        7）、Synthetic：标识方法或字段为编译器自动生成的。

        8）、`Signature`：**JDK 1.5 中新增的属性，用于支持泛型情况下的方法签名，由于 Java 的泛型采用擦除法实现，在为了避免类型信息被擦除后导致签名混乱，需要这个属性记录泛型中的相关信息**。

        9）、`SourceFile`：**包含一个指向 Utf8 常量项的索引，即 Class 对应的源码文件名**。

        10）、SourceDebugExtension：用于存储额外的调试信息。

        11）、`LineNumberTable`：**Java 源码的行号与字节码指令的对应关系**。

        12）、`LocalVariableTable`：**局部变量数组/本地变量表，用于保存变量名，变量定义所在行**。

        13）、`LocalVariableTypeTable`：**JDK 1.5 中新增的属性，它使用特征签名代替描述符，是为了引入泛型语法之后能描述泛型参数化类型而添加**。

        14）、Deprecated

        15）、RuntimeVisibleAnnotations

        16）、RuntimeInvisibleAnnotations

        17）、RuntimeVisibleParameterAnnotations

        18）、RuntimeInvisibleParameterAnnotations

        19）、AnnotationDefault

        20）、BootstrapMethods：JDK 1.7中新增的属性，用于保存 invokedynamic 指令引用的引导方法限定符。切记，类文件的属性表中最多也只能有一个 BootstrapMethods 属性。

    - Code_attribute

      - Code_attribute 的数据结构伪代码如下所示：

        ```jvm
            Code_attribute {  
                u2 attribute_name_index; 
                u4 attribute_length;
                u2 max_stack;
                u2 max_locals;
                u4 code_length;
                u1 code[code_length];
                u2 exception_table_length; 
                { 
                    u2 start_pc;
                    u2 end_pc;
                    u2 handler_pc;
                    u2 catch_type;
                } exception_table[exception_table_length];
                u2 attributes_count;
                attribute_info attributes[attributes_count];
            }
        复制代码
        ```

        - Code_attribute 中的各个元素的含义如下所示：
        - `attribute_name_index、attribute_length`：**attribute_length 的值为整个 Code 属性减去 attribute_name_index 和 attribute_length 的长度**。
        - `max_stack`：**为当前方法执行时的最大栈深度，所以 JVM 在执行方法时，线程栈的栈帧（操作数栈，operand satck）大小是可以提前知道的。每一个函数执行的时候都会分配一个操作数栈和局部变量数组，而 Code_attribure 需要包含它们，以便 JVM 在执行函数前就可以分配相应的空间**。
        - `max_locals`：**为当前方法分配的局部变量个数，包括调用方式时传递的参数。long 和 double 类型计数为 2，其他为 1。max_locals 的单位是 Slot,Slot 是

        虚拟机为局部变量分配内存所使用的最小单位。局部变量表中的 Slot 可以重用，当代码执行超出一个局部变量的作用域时，这个局部变量 所占的 Slot 可以被其他局部变量所使用，Javac 编译器会根据变量的作用域来分配 Slot 给各个 变量使用，然后计算出 max_locals 的大小**。

        - `code_length`：**为方法编译后的字节码的长度**。
        - **code**：**用于存储字节码指令的一系列字节流。既然叫字节码指令，那么每个指令就是一个 u1 类型的单字节。一个 u1 数据类型的取值范围为 0x00~~0xFF，对应十进制的 0~~255，也就是一共可以表达 256 条指令**。
        - `exception_table_length`：**表示 exception_table 的长度**。
        - `exception_table`：**每个成员为一个 ExceptionHandler，并且一个函数可以包含多个 try/catch 语句，一个 try/catch 语句对应 exception_table 数组中的一项**。
        - `start_pc、end_pc`：**为异常处理字节码在 code[] 的索引值。当程序计数器在 [start_pc, end_pc) 内时，表示异常会被该 ExceptionHandler 捕获**。
        - `handler_pc`：**表示 ExceptionHandler 的起点，为 code[] 的索引值**。
        - `catch_type`：**为 CONSTANT_Class 类型常量项的索引，表示处理的异常类型。如果该值为 0，则该 ExceptionHandler 会在所有异常抛出时会被执行，可以用来实现 finally 代码。当 catch_type 的值为 0 时，代表任意异常情况都需要转向到 handler_pc 处进行处理。此外，编译器使用异常表而不是简单的跳转命令来实现 Java 异常及 finally 处理机制**。
        - `attributes_count 和 attributes`：**表示该 exception_table 拥有的 attribute 数量与数据**。
    
  - JVM 指令码

  - Code_attribute 中的 code 数组存储了一个函数源码经过编译后得到的 JVM 字节码，其中仅包含如下 **两种** 类型的信息：
    - 1)、`JVM 指令码`：**用于指示 JVM 执行的动作，例如加操作/减操作/new 对象。其长度为 1 个字节，所以 JVM 指令码的个数不会超过 255 个（0xFF）**。
    - 2)、`JVM 指令码后的零至多个操作数`：**操作数可以存储在 code 数组中，也可以存储在操作数栈（Operand stack）中**



### ASM开发实战

- 认识ASM

  - [官网](https://asm.ow2.io/)   

  - [远程依赖 mvnrepository](https://mvnrepository.com/artifact/org.ow2.asm)   

  - [源码地址](https://gitlab.ow2.org/asm/asm)

  - ```
    implementation "org.ow2.asm:asm:7.0"
    implementation "org.ow2.asm:asm-util:7.0"
    ```

  - ASM是一个Java字节码操纵框架，它能被用来**动态生成类**或者**增强既有类的功能**

    - 动态生成类的应用：一些动态功能封装成类实现时
    - 增强既有类功能的应用场景：AOP ， 安全检查、记录日志等

  - 实现原理：ASM可以直接产生二进制class文件,也可以在类被加载入虚拟机之前动态改变类行为, ASM从类文件中读入信息后能够改变类行为,分析类信息,甚至能根据要求生成新类

  - 目前许多框架如cglib、Hibernate、 Spring都直接或间接地使用ASM操作字节码

- ASM编程模型

  - Core API :提供了基于事件形式的编程模型。该模型不需要一次性将整个类的结构读取到内存中 ,
    - 优缺点：因此这种方式更快,需要更少的内存,但这种编程方式难度较大
  - Tree API :提供了基于树形的编程模型。该模型需要一次性将一个类的完整结构全部读取到内存当中,
    - 优缺点：所以这种方法需要更多的内存,这种编程方式较简单

- ASM核心API

  - ASM Core API中操纵字节码的功能基于ClassVisitor接口。这个接口中的每个方法对应了class文件中的每一项
  - ASM提供了三个基于ClassVisitor接口的类来实现class文件的生成和转换
    - ClassVisitor：访问者模式
    - ClassReader : ClassReader解析一个类的class字节码
    - ClassAdapter : ClassAdapter是ClassVisitor的实现类,实现要变化的功能
    - ClassWriter : ClassWriter也是ClassVisitor的实现类,可以用来输出变化后的字节码
      - 结合ClassLoader 不一定需要写回去这一步骤，可以再内存中修改字节码再让classlaoder 装载
  - ASM给我们提供了ASMifier工具来帮助开发，可使用ASMifier工具生成ASM结构来对比
  - ![image-20210603234231905](/Users/xuejiewang/Library/Application Support/typora-user-images/image-20210603234231905.png)

- ASM开发实战:实现统计方法的运行时间

  - todo

#### ASM FAQ

##### 1. 如何开始使用 ASM？

- 动态生成类
  - 如果你想用ASM从头开始生成类，写一个代表你要生成的类的Java源文件，编译它，然后在编译后的类上运行[ASMifier](https://asm.ow2.io/faq.html#Q10)可以看到Java源代码用 ASM 生成这个类。
- 增强既有类
  - 编写两个 Java 源文件分别是包含和不包含必须添加或删除的功能，编译它们，然后在[它们](https://asm.ow2.io/faq.html#Q10)上运行[ASMifier](https://asm.ow2.io/faq.html#Q10)。然后将结果与一些视觉差异工具进行比较。
- 插件与工具
  - [ASMifier](https://asm.ow2.io/javadoc/org/objectweb/asm/util/ASMifier.html)
    - A Printer that prints the ASM code to generate the classes if visits
    - java -classpath "asm.jar;asm-util.jar" org.objectweb.asm.util.ASMifier org/domain/package/YourClass.class
  - [ASM Bytecode Outline](https://plugins.jetbrains.com/plugin/5918-asm-bytecode-outline)
    - Displays bytecode for Java classes and ASMified code which will help you in your class generation.
    - Compatible with IntelliJ IDEA, Android Studio
  - [ASM Bytecode Viewer](https://plugins.jetbrains.com/plugin/10302-asm-bytecode-viewer)
    - Displays bytecode for Java classes and ASMified code which will help you in your class generation.
    - Compatible with IntelliJ IDEA, Android Studio
- 注意事项
  - javac 可能会为不同的 -target 生成不同的代码，因此您必须针对您的目标环境进行编译，对所有必需的目标重复该练习，或者尽可能使用最早的字节码版本。

##### 2. 如何删除方法/字段？

使用 ClassVisitor 并且不返回任何内容：

```java
 public FieldVisitor visitField(String name, ...) {
    if (removeField(name)) {
      // Do nothing, in order to remove this field.
      return null;
    } else {
      // Make the next visitor visit this field, in order to keep it.
      return super.visitField(name, ...);
    }
  }
```

##### 3. 如何替换方法/字段？避免得到重复的成员！

使用 ClassVisitor 访问原始方法/字段时返回替换方法/字段，或者必须首先删除 ClassVisitor 中的原始方法/字段，以及然后通过调用 ClassWriter 上的访问方法来添加新方法/字段。

##### 4. 如何让 ASM 为我计算visitMaxs？

调用 ClassWriter 的构造函数时，请使用 COMPUTE_MAXS 标志。您还必须包括visitMaxs 方法调用，但您提供的值将被忽略，因此visitMaxs(0,0) 没问题。

##### 5. 如何重命名我的班级？

仅重命名类是不够的，您还必须重命名对类成员的所有引用。org.objectweb.asm.commons 中的 ClassRemapper 可以为您做到这一点。

##### 6. 方法描述符是如何工作的？

要最好地理解这一点，最好阅读**org.objectweb.asm.Type.java**  的源代码。这是一个快速概述：

- Primitive representations:
  - 'V' - void
  - 'Z' - boolean
  - 'C' - char
  - 'B' - byte
  - 'S' - short
  - 'I' - int
  - 'F' - float
  - 'J' - long
  - 'D' - double
- Class representations:
  - L<class>;
  - Ljava/io/ObjectOutput;
  - Ljava/lang/String;

- Examples:
  - public void method(): `()V`
  - public void method(String s, int i): `(Ljava/lang/String;I)V`
  - public String method(String s, int i, boolan flag):`(Ljava/lang/String;IZ)Ljava/lang/String;`

##### 7. ASM 如何帮助我创建描述符类型？

**org.objectweb.asm.Type.java**  提供了静态方法 Type.getDescriptor，它接受一个 Class 作为参数。

例子：

```java
String desc = Type.getDescriptor(String.class);

String desc = Type.getDescriptor(int.class);

String desc = Type.getDescriptor(java.io.ObjectOutput.class);
```

##### 8. 字节码指令是如何工作的？

所有字节码指令都在Java 虚拟机规范的[第 6 章](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html)中指定。

##### 9. ASM 线程安全吗？

Type 和 ClassReader 类是线程安全的，即多个线程可以毫无问题地同时使用单个 Type 对象或单个 ClassReader 对象。ClassWriter 和 MethodWriter 类*不是* 线程安全的，即单个类不能由多个并发线程生成（当然，如果每个线程使用自己的 ClassWriter 实例，则多个线程可以同时生成不同的类）。为了使用多个并发线程生成单个类，应该使用委托给普通 ClassWriter 和 MethodWriter 实例的 ClassVisitor 和 MethodVisitor 实例，并且它们的方法都是同步的。

更一般地说，ClassVisitor 和 MethodVisitor 实现（例如 ClassWriter）不必是线程安全的。然而，非线程安全的访问者可以通过在他们面前使用同步类适配器来实现线程安全。

##### 10. 最早使用ASM需要什么JDK？

ASM 需要 JDK 1.5 或更高版本。

##### 11. 如何解决不同ASM版本之间的冲突？

您可以在自己的命名空间内重新打包 ASM，这可以使用[Jar Jar Links](https://github.com/shevek/jarjar) 工具自动完成。

[Jar Jar Links](https://github.com/shevek/jarjar)  是一个实用程序，可以轻松地重新打包 Java 库并将它们嵌入到您自己的发行版中。

### 类加载的生命周期

- 类从加载、连接、初始化到卸载的生命

- 类加载

  - 作用：查找并加载类文件的二进制数据
  - 功能
    - 通过类的全限定名来获取该类的二进制字节流
    - 把二进制字节流转化为方法区的运行时数据结构
    - 在堆上创建一个java.lang.Class对象,用来封装类在方法区内的数据结构,并向外提供了访问方法区内数据结构的接口
  - 加载类的方式
    - 最常见的方式:本地文件系统中加载、从jar等归档文件中加载
    - 动态的方式:将java源文件动态编译成class，如通过ASM生成
    - 其它方式:网络下载、从专有数据库中加载等等
  - 类加载器
    - Java虚拟机自带的加载器包括如下几种
      - 启动类加载器( BootstrapClassLoader )
      - Java8之前：扩展类加载器（ExtensionClassLoader）
      - Java9之后：平台类加载器( PlatformClassLoader )
      - 应用程序类加载器( AppClassLoader )
    - 用户自定义的加载器
      - 是java.lang.ClassLoader的子类,用户可以定制类的加载方式
      - 自定义类加载器其加载的顺序是在所有系统类加载器的最后
    - 类加载器之间的关系
      - 启动类加载器<-扩展类加载器/平台类加载器<-应用程序类加载器<-自定义的加载器
    - 类加载器说明
      - 启动类加载器
        - JDK8-: 负责将<JAVA_HOME>/lib ,或者Xbootclasspath参数指定的路径中的，且是虚拟机识别的类库加载到内存中(按照名字识别，比如rt.jar ,对于不能识别的文件不予装载)
        - JDK9+: 用于加载启动的基础模块类,比如:java.base、java.management、 java.xml 等等
      - 扩展类加载器
        - JDK8-: 负责加载<JRE_HOME> /lib/ext，或者java.ext.dirs系统变量所指定路径中的所有类库
      - 平台类加载器
        - JDK9+: 用于加载一些平台相关的模块,比如:java.scripting、java.compiler*、 java.corba* 等等
      - 应用程序类加载器
        - JDK8-: 负责加载classpath路径中的所有类库
        - JDK9+: 用于加载应用级别的模块,比如:jdk.compiler、jdk.jartool、 jdk.jshell 等等;还加载classpath路径中的所有类库
    - 类加载注意事项
      - Java程序不能直接引用启动类加载器,直接设置classLoader为null ,默认就使用启动类加载器
      - 类加载器并不需要等到某个类"首次主动使用”的时候才加载它, Jvm规范允许类加载器在预料到某个类将要被使用的时候就预先加载它
      - 如果在加载的时候.class文件缺失,会在该类首次主动使用时报告LinkageError错误,如果一直没有被使用 ,就不会报错
  - 双亲委派模型
    - JDK8-: 
      - 如果被加载的class没有在子级加载器中找到,那么将会委托给父级加载器，直到启动类加载器
      - 如果父级加载器反馈它不能完成加载请求,比如在它的搜索路径下找不到这个类,那子的类加载器才自己来加载
    - JDK9+: 
      - 如果class没有在这些加载器定义的具名模块中找到,那么将会委托给父级加载器，直到启动类加载器
      - 如果父级加载器反馈它不能完成加载请求,比如在它的搜索路径下找不到这个类,那子的类加载器才自己来加载
      - 在类路径下找到的类将成为这些加载器的无名模块
    - 注意事项
      - 双亲委派模型对于保证Java程序的稳定运作很重要
      - 实现双亲委派的代码在java.lang.ClassLoader的loadClass()方法中,如果自定义类加载器的话,推荐覆盖实现findClass()方法
      - 如果有一个类加载器能加载某个类,称为定义类加载器所有能成功返回该类的Class的类加载器都被称为初始类加载器
      - 如果没有指定父加载器,默认就是启动加载器
      - 每个类加载器都有自己的命名空间,命名空间由该加载器及其所有父加载器所加载的类构成,不同的命名空间，可以出现类的全路径名相同的情况
      - 运行时包由同一个类加载器的类构成,决定两个类是否属于同一个运行时包,不仅要看全路径名是否一样,还要看定义类加载器是否相同。只有属于同- -个运行时包的类才能实现相互包内可见
    - 破坏双亲委派模型
      - 双亲模型有个问题:父加载器无法向下识别子加载器加载的资源
      - 为了解决这个问题,引入了线程上下文类加载器,可以通过Thread的setContextClassLoader()进行设置
      - 另外一种典型情况就是实现热替换,比如OSG的模块化热部署,它的类加载器就不再是严格按照双亲委派模型,很多可能就在平级的类加载器中执行了

- 类连接

  - 作用:就是将已经读入内存的类的二进制数据合并到JVM运行时环境中去,包含如下几个步骤:
  - 验证
    - 作用：确保被加载类的正确性
    - 验证内容
      - 类文件结构检查:按照JVM规范规定的类文件结构进行
      - 元数据验证:对字节码描述的信息进行语义分析,保证其符合Java语言规范要求
      - 字节码验证:通过对数据流和控制流进行分析,确保程序语义是合法和符合逻辑的。这里主要对方法体进行校验
      - 符号引用验证:对类自身以外的信息,也就是常量池中的各种符号引用,进行匹配校验
  - 准备
    - 作用：为类的静态变量分配内存,并初始化它们
  - 解析
    - 作用：把常量池中的符号引用转换成直接引用
    - 符号引用:以一组无歧义的符号来描述所引用的目标，与虚拟机的实现无关
    - 直接引用:直接指向目标的指针、相对偏移量、或是能间接定位到目标的句柄,是和虚拟机实现相关的
    - 主要针对:类、接口、字段、类方法、接口方法、方法类型方法句柄、调用点限定符

- - 验证
  - 准备
  - 解析

- 类初始化

  - 作用：为静态变量赋初始值
  - 注意事项
    - 类的初始化就是为类的静态变量赋初始值,或者说是执行类构造器< clinit>方法的过程
    - 如果类还没有加载和连接,就先加载和连接
    - 如果类存在父类,且父类没有初始化,就先初始化父类
    - 如果类中存在初始化语句,就依次执行这些初始化语句
    - 如果类中实现了接口
      - 初始化一个类的时候,并不会先初始化它实现的接口
      - 初始化一个接口时,并不会初始化它的父接口
      - 只有当程序首次使用接口里面的变量或者是调用接口方法的时候,才会导致接口初始化
    - 调用Classloader类的loadClass方法来装载一个类,并不会初始化这个类,不是对类的主动使用

  - 类的初始化时机
    - Java程序对类的使用方式分成:主动使用和被动使用, JVM必须在每个类或接口"首次主动使用”时才初始化它们;被动使用类不会导致类的初始化,主动使用的情况: .
      - 创建类实例
      - 访问类或接口的静态变量
      - 调用类的静态方法
      - 反射某个类
      - 初始化某个类的子类,而父类还没有初始化
      - JVM启动的时候运行的主类
      - 定义了default方法的接口,当接口实现类初始化时
    - 被动使用情况
      - 子类中调用父类的静态变量不会导致子类的初始化
      - 初始化类数组时不会导致类的初始化
      - 访问类的常量不会导致类的初始化，常量在编译器确定

- 类卸载

  - 当代表一个类的Class对象不再被引用 ,那么Class对象的生命周期就结束了,对应的在方法区中的数据也会被卸载
  - Jvm自带的类加载器装载的类,是不会卸载的，由用户自定义的类加载器加载的类是可以卸载的

- JVM内存分配

  - JVM简化架构
    - 类装载器
    - 运行时数据区
      - 方法区（Method Area）
        - 方法区是线程共享的,通常用来保存装载的类的结构信息
        - 通常和元空间关联在一起,但具体的跟JVM实现和版本有关
        - JVM规范把方法区描述为堆的一个逻辑部分,但它有一个别名称为Non-heap (非堆) , 应是为了与Java堆区分开
        - 运行时常量池
          - 是Class文件中每个类或接口的常量池表,在运行期间的表示形式,通常包括:类的版本、字段、方法、接口等信息
          - 在方法区中分配
          - 通常在加载类和接口到JVM后,就创建相应的运行时常量池
      - 虚拟机栈（VM Stack）
        - 栈由一系列帧( Frame )组成(因此Java栈也叫做帧栈),是线程私有的
        - 帧用来保存一个方法的局部变量、操作数栈( Java没有寄存器,所有参数传递使用操作数栈)、常量池指针、动态链接、方法返回值等
        - 每一次方法调用创建一个帧,并压栈,退出方法的时候,修改栈顶指针就可以把栈帧中的内容销毁
        - 局部变量表存放了编译期可知的各种基本数据类型和引用类型,每个slot存放32位的数据, long、double占两个槽位
        - 栈的优点:存取速度比堆快,仅次于寄存器
        - 栈的缺点:存在栈中的数据大小、生存期是在编译期决定的，缺乏灵活性
      - 本地方法栈（Native Method Stack）
        - 在JVM中用来支持native方法执行的栈就是本地方法栈
      - 堆（Heap）
        - 用来存放应用系统创建的对象和数组,所有线程共享Java堆
        - GC主要就管理堆空间,对分代GC来说,堆也是分代的
        - 堆的优点:运行期动态分配内存大小,自动进行垃圾回收;
        - 堆的缺点:效率相对较慢
      - 程序计数器（Program Counter Register）
        - 每个线程拥有一个PC寄存器,是线程私有的，用来存储指向下一条指令的地址
        - 在创建线程的时候,创建相应的PC寄存器
        - 执行本地方法时, PC寄存器的值为undefined
        - 是一块较小的内存空间,是唯一一 个在JVM规范中没有规定OutOfMemoryError的内存区域
    - 垃圾收集器
    - 本地库接口、本地方法库
    - 执行引擎
  - 栈、堆、方法区之间的交互关系
    - 栈：user
    - 堆：User 对象、User类的元数据信息、实例数据
    - 方法区：User类定义、字段、方法

- JVM堆内存

  - 概述

    - Java堆用来存放应用系统创建的对象和数组,所有线程共享Java堆
    - Java堆是在运行期动态分配内存大小,自动进行垃圾回收
    - Java垃圾回收( GC )主要就是回收堆内存,对分代GC来说,堆也是分代的
    
  - 堆的分代结构

    - 新生代 (Young Generation)
      - Eden Space
      - Survivor Space
        - From Space
        - To Space
    - 老年代 (Old Generation)
      - Tenured Space
    - 新生代用来放新分配的对象;新生代中经过垃圾回收,没有回收掉的对象,被复制到老年代
    - 老年代存储对象比新生代存储对象的年龄大得多,老年代存储一些大对象
    - 整个堆大小=新生代+老年代
    - 新生代= Eden +存活区
    - 从前的持久代,用来存放Class、Method等元信息的区域从JDK8开始去掉了,取而代之的是元空间(MetaSpace)元空间并不在虚拟机里面,而是直接使用本地内存

  - 对象的内存布局

    - 对象在内存中存储的布局(这里以HotSpot虚拟机为例来说明) , 分为:对象头、实例数据和对齐填充
    - 对象头,包含两个部分:
      - MarkWord:存储对象自身的运行数据,如:HashCode、GC分代年龄、锁状态标志等
      - 类型指针:对象指向它的类元数据的指针
    - 实例数据: 真正存放对象实例数据的地方
    - 对齐填充: 这部分不一定存在,也没有什么特别含义,仅仅是占位符。因为HotSpot要求对象起始地址都是8字节的整数倍, 如果不是，就对齐

  - 对象的访问定位

    - 对象的访问定位：在JVM规范中只规定了reference类型是- - 一个指向对象的引用,但没有规定这个引用具体如何去定位、访问堆中对象的具体位置

    - 因此对象的访问方式取决于JVM的实现,目前主流的有:使用句柄或使用指针两种方式

      - 使用句柄: Java堆中会划分出一-块内存来做为句柄池reference中存储句柄的地址,句柄中存储对象的实例数据和类元数据的地址,如下图所示:

      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpij8a45lzj30p80dljst.jpg)

      - 使用指针: Java堆中会存放访问类元数据的地址,reference存储的就直接是对象的地址,如下图所示:

      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpij8p0ov6j30ss0cqdh9.jpg)

  - 内存分配参数

    - [Java 命令官方文档](https://docs.oracle.com/en/java/javase/13/docs/specs/man/java.html)
    - 配置再IDE的VM arguments 输入框中
    - Trace跟踪参数（trace/debug/level/info）
      - 打印GC的简要信息：-Xlog:gc
      - 打印GC的详细信息：-Xlog:gc*
      - 将GC日志输出到文件：-Xlog:gc:dir/filename  //文件夹需要预先建好
      - 每一次GC后都打印堆信息：-Xlog:gc+heap=debug/level
    - GC日志格式
      - GC发生的时间,也就是JVM从启动以来经过的秒数
      - 日志级别信息,和日志类型标记
      - GC识别号
      - GC类型和说明GC的原因
      - 容量: GC前容量-> GC后容量(该区域总容量)
      - GC持续时间,单位秒。有的收集器会有更详细的描述,比如: user表示应用程序消耗的时间, sys表示系统内核消耗的时间、real表示操作从开始到结束的时间
    - Java堆的参数
      - `-Xms` size
        - 初始堆大小,默认物理内存的1/64
        - 等价：`-XX:MinHeapSize` to set the minimum size and `-XX:InitialHeapSize` to set the initial size.
      - `-Xmx` size
        - 最大堆大小,默认物理内存的1/4
        - 等价：The `-Xmx` option is equivalent to `-XX:MaxHeapSize`
      - `-Xmn ` size
        - 新生代大小，默认整个堆的3/8
        - 等价：you can use `-XX:NewSize` to set the initial size and `-XX:MaxNewSize` to set the maximum size.
      - G1收集器新生代内存划分不明显，可以切换到 CMS 收集器，
        - 命令为：`-XX:+UseConcMarkSweepGC` option and the `-XX:+UseG1GC` option.
        - 演示：-XX:+UseConcMarkSwcepGC -XX:MinHeapSizem 8m -XX:lnitialHeapSize-9m -Xmx10m -Xmn 2m -Xlog:gc+heap= debug
      - -XX:+ HeapDumpOnOutOfMemoryError : OOM时导出堆到文件
      - -XX:OnOutOfMemoryError :在OOM时,执行一个脚本
      - -XX:+ HeapDumpPath : 导出OOM的路径
      - -XX:NewRatio :老年代与新生代的比值, 如果xms=xmx ,且设置了xmn的情况下, 该参数不用设置
      - -XX:SurvivorRatio : Eden区和Survivor区的大小比值， 设置为8 ,则两个Survivor区与-一个Eden区的比值为2:8个Survivor占整个新生的1/10
    - Java栈的参数
      - -Xss :通常只有几百K ,决定了函数调用的深度
    - 元空间的参数
      - -XX:MetaspaceSize :初始空间大小
      - -XX:MaxMetaspaceSize :最大空间,默认是没有限制的
      - -XX:MinMetaspaceFreeRatio :在GC之后,最小的Metaspace剩余空间容量的百分比
      - -XX:MaxMetaspaceFreeRatio :在GC之后,最大的Metaspace剩余空间容量的百分比

- 字节码执行引擎

  - 概述

    - JVM的字节码执行引擎,功能基本就是输入字节码文件,然后对字节码进行解析并处理,最后输出执行的结果
    - 实现方式可能有通过解释器直接解释执行字节码, 或者是通过即时编译器产生本地代码,也就是编译执行,当然也可能两者皆有

  - 栈帧

    - 栈帧是用于支持JVM进行方法调用和方法执行的数据结构
    - 栈帧随着方法调用而创建,随着方法结束而销毁
    - 栈帧里面存储了方法的局部变量、操作数栈、动态连接、方法返回地址等信息

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpjpgny471j30gm0fwgqi.jpg)

  - 局部变量表

    - 局部变量表:用来存放方法参数和方法内部定义的局部变量的存储空间
    - 以变量槽slot为单位,目前一-个slot存放32位以内的数据类型
    - 对于64位的数据占2个slot
    - 对于实例方法,第0位slot存放的是this ,然后从1到n ,依次分配给参数列表
    - 然后根据方法体内部定义的变量顺序和作用域来分配slot
    - slot是复用的,以节省栈帧的空间,这种设计可能会影响到系统的垃圾收集行为

  - 操作数栈

    - 操作数栈:用来存放方法运行期间,各个指令操作的数据。
    - 操作数栈中元素的数据类型必须和字节码指令的顺序严格匹配
    - 虚拟机在实现栈帧的时候可能会做一些优化,让两个栈帧出现部分重叠区域,以存放公用的数据

  - 动态连接

    - 动态连接:每个栈帧持有一个指向运行时常量池中该栈帧所属方法的引用,以支持方法调用过程的动态连接
    - 静态解析:类加载的时候,符号弓|用就转化成直接引|用
    - 动态连接:运行期间转换为直接引用

  - 方法返回地址

    - 方法执行后返回的地址

  - 方法调用

    - 方法调用:方法调用就是确定具体调用那一-个方法,并不涉.及方法内部的执行过程
    - 部分方法是直接在类加载的解析阶段,就确定了直接引用关系
    - 但是对于实例方法,也称虚方法,因为重载和多态需要运行期动态委派
    - 分派:又分成静态分派和动态分派
      - 静态分派:所有依赖静态类型来定位方法执行版本的分派方式，比如:重载方法
      - 动态分派:根据运行期的实际类型来定位方法执行版本的分派方式,比如:覆盖方法
      - 单分派和多分派:就是按照分派思考的纬度,多余- -个的就算多分派，只有一个的称为单分派
      - 如何执行方法中的字节码指令: JVM通过基于栈的字节码解释执行引|擎来执行指令, JVM的指令集也是基于栈的

- 垃圾回收基础

  - 概述
    - 什么是垃圾
      - 简单说就是内存中已经不再被使用到的内存空间就是垃圾.
    - 如何判定是垃圾
      - 根搜索算法判断不可用
      - 看是否有必要执行finalize方法
      - 两个步骤走完后对象仍然没有人使用,那就属于垃圾
    - 判断类无用的条件
      - JVM中该类的所有实例都已经被回收
      - 加载该类的ClassLoader已经被回收
      - 没有任何地方弓|用该类的Class对象
      - 无法在任何地方通过反射访问这个类
    - 如何回收
      - 回收算法
      - 回收器
  - 引用计数法
    - 弓|用计数法:给对象添加一个弓|用计数器,有访问就加1引用失效就减1
    - 优点:实现简单、效率高;缺点:不能解决对象之间循环引用的问题
  - 根搜索算法
    - 也叫可达性分析算法
    - 从根( GC Roots )节点向下搜索对象节点,搜索走过的路经称为引用链,当- -个对象到根之间没有连通的话,则该对象不可用
    - 可作为GC Roots的对象包括:
      - 虚拟机栈(栈帧局部变量)中引用的对象
      - 方法区类静态属性引用的对象
      - 方法区中常量引用的对象
      - 本地方法栈中JNI引用的对象
      - 同步锁持有的对象
    - 对象很大从更搜索效率慢，怎么办
      - HotSpot使用了一-组叫做OopMap的数据结构达到准确式GC的目的
      - 在OopMap的协助下, JVM可以很快的做完GC Roots枚举但是JVM并没有为每一条指令生 成一个OopMap
      - 记录OopMap的这些"特定位置”被称为安全点,即当前线程执行到安全点后才允许暂停进行GCW。90yrDU。
      - 如果一-段代码中,对象弓|用关系不会发生变化,这个区域中任何地方开始GC都是安全的,那么这个区域称为安全区域
  - 引用分类
    - 如果根据可达性分析算法可以将引用分为有引用和没有引用来划分，但是这样就显得功能很单薄
    - 还需要考虑内存是否充足，是否发生GC这些维度
    - 强引用:类似于Object a = new A()这样的,不会被回收,当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足的问题。
    - 软引用:还有用但并不必须的对象, 内存不足时被回收，用SoftReference来实现软引用, 如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它, 软引用可用来实现内存敏感的高速缓存。软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收器回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中。
    - 弱引用:非必须对象,比软引用还要弱, 垃圾回收时会回收掉。用WeakReference来实现弱引用, 在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。
    - 虚引用:也称为幽灵引用或幻影引用,是最弱的引用。垃圾回收时会回收掉。用PhantomReference来实现虚引用, 如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。 虚引用主要用来跟踪对象被垃圾回收器回收的活动。虚引用与软引用和弱引用的一个区别在于：虚引用必须和引用队列 （ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之 关联的引用队列中。
    - finalize()函数是在JVM回收内存时执行的，但JVM并不保证在回收内存时一定会调用finalize()。
  - 跨代引用
    - 跨代引用:也就是一个代中的对象弓|用另-个代中的对象
    - 跨代弓|用假说:跨代引用相对于同代引|用来说只是极少数
    - 隐含推论:存在互相引用关系的两个对象,是应该倾向于同时生存或同时消亡的
  - 记忆集
    - 记忆集( Remembered Set ) : 一种用于记录从非收集区域指向收集区域的指针集合的抽象数据结构
    - 字长精度:每个记录精确到-一个机器字长,该字包含跨代指针
    - 对象精度:每个记录精确到一一个对象,该对象里有字段含有跨代指针
    - 卡精度:每个记录精确到一块内存区域,该区域内有对象含有跨代指针
      - 卡表(Card Table ) : 是记忆集的一-种具体实现，定义了记,忆集的记录精度和与堆内存的映射关系等
      - 卡表的每个元素都对应着其标识的内存区域中-块特定大小的内存块,这个内存块称为卡颍( Card Page )
  - 写屏障
    - 写屏障可以看成是JVM对“引用类型字段赋值”这个动作的AOP
    - 通过写屏障来实现当对象状态改变后,维护卡表状态.
  - GC类型
    - MinorGC/YoungGC :发生在新生代的收集动作
    - MajorGC / OldGC :发生在老年代的GC ,目前只有CMS收集器会有单独收集老年代的行为
    - MixedGC :收集整个新生代以及部分老年代,目前只有G1收集器会有这种行为
    - FullGC :收集整个Java堆和方法区的GC
  - Stop-The-World
    - STW是Java中一种全局暂停的现象,多半由于GC引起。所谓全局停顿,就是所有Java代码停止运行, native代码可以执行,但不能和JVM交互
    - 其危害是长时间服务停止,没有响应;对于HA系统,可能弓|起主备切换,严重危害生产环境
  - 垃圾收集类型
    - 串行收集: GC单线程内存回收、会暂停所有的用户线程,如: Serial
    - 并行收集:多个GC线程并发工作,此时用户线程是暂停的如: Parallel
    - 并发收集:用户线程和GC线程同时执行(不一定是并行可能交替执行) , 不需要停顿用户线程,如: CMS

- 垃圾收集算法

  - 标记清除法

    - 标记清除法( Mark- Sweep )算法分成标记和清除两个阶段先标记出要回收的对象,然后统- -回收这些对象
      - 优点是简单
      - 缺点
        - 效率不高,标记和清除的效率都不高不高
        - 标记清除后会产生大量不连续的内存碎片,从而导致在分配大对象时触发GC

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn13q6gz9j30q60c142h.jpg)

  - 复制算法

    - 复制算法( Copying ) : 把内存分成两块完全相同的区域每次使用其中一块,当一块使用完了,就把这块上还存活的对象拷贝到另外-块,然后把这块清除掉, 新生代的From, To
    - 优点是:实现简单,运行高效,不用考虑内存碎片问题
    - 缺点是:内存有些浪费

    - JVM实际实现中,是将内存分为一块较大的Eden区和两块较小的Survivor空间,每次使用Eden和- -块Survivor ,回收时,把存活的对象复制到另一块Survivor
    - HotSpot默认的Eden和Survivor比是8:1 ,也就是每次能用90%的新生代空间
    - 如果Survivor空间不够,就要依赖老年代进行分配担保,把放不下的对象直接进入老年代

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn18oni1zj30py0c50xm.jpg)

  - 分配担保

  - 分配担保是:当新生代进行垃圾回收后,新生代的存活区放置不下,那么需要把这些对象放置到老年代去的策略,也就是老年代为新生代的GC做空间分配担保，步骤如下:

    - 在发生MinorGC前, JVM会检查老年代的最大可用的连续空间,是否大于新生代所有对象的总空间，如果大于，可以确保MinorGC是安全的
    - 如果小于,那么JVM会检查是否设置了允许担保失败,如果允许,则继续检查老年代最大可用的连续空间,是否大于历次晋升到老年代对象的平均大小
    - 如果大于,则尝试进行一次MinorGC
    - 如果不大于，则改做一次Full GC

  - 标记整理法

    - 标记整理算法( Mark-Compact ) :由于复制算法在存活对象比较多的时候,效率较低,且有空间浪费,因此老年代一般不会选用复制算法,老年代多选用标记整理算法
      - 优缺点

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn1krv3xdj30nl0cmdk0.jpg)

- 垃圾收集器

  - 概述
  - 前面讨论的垃圾收集算法只是内存回收的方法,垃圾收集器就来具体实现这些这些算法并实现内存回收
    - 不同厂商、不同版本的虚拟机实现差别很大, HotSpot中包含的收集器如下图所示:
  - HotSpot中的收集器

  ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn5zf3u18j30mh0ddn0g.jpg)

  - 串行收集器

    - 优点是简单,对于单cpu ,由于没有多线程的交互开销,可能更高效,是默认的Client模式下的新生代收集器
    - 使用 `-XX:+ UseSerialGC` 来开启,会使用: Serial + SerialOld的收集器组合
    - 新生代使用复制算法,老年代使用标记整理算法

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn61dicjfj30ub0dgtcs.jpg)

  - 并行收集器

    - ParNew (并行)收集器:使用多线程进行垃圾回收,在垃圾收集时,会Stop-the-World
    - 在并发能力好的CPU环境里,它停顿的时间要比串行收集器短;但对于单cpu或并发能力较弱的CPU ,由于多线程的交.互开销，可能比串行回收器更差
    - 是Server模式下首选的新生代收集器，且能和CMS收集器配合使用
    - 不再使用-XX:+UseParNewGC来单独开启
    - -XX:ParallelGCThreads :指定线程数,最好与CPU数量一致
    - 新生代使用复制算法

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpn7430671j30t70db78g.jpg)

  - 新生代Parallel Scavenge收集器

    - 新生代Parallel Scavenge收集器/Parallel Old收集器:是一个应用于新生代的、使用复制算法的、并行的收集器
    - 跟ParNew很类似,但更关注吞吐量,能最高效率的利用CPU ,适合运行后台应用
    - 使用 -XX:+ UseParallelGC来开启
    - 使用-XX:+ UseParallelOldGC来开启老年代使用ParallelOld收集器,使用Parallel Scavenge + Parallel Old的收集器组合
    - -XX:MaxGCPauseMillis :设置GC的最大停顿时间

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpoa58mxuwj30rj0d20xj.jpg)

  - CMS收集器

    - CMS ( Concurrent Mark and Sweep并发标记清除)收集器分为:
      - 初始标记:只标记GC Roots能直接关联到的对象; 
      - 并发标记:进行GC Roots Tracing的过程
      - 重新标记:修正并发标记期间,因程序运行导致标记发生变化的那一部分对象
      - 并发清除:并发回收垃圾对象
    - 在初始标记和重新标记两个阶段还是会发生Stop-the-World
    - 使用标记清除算法,多线程并发收集的垃圾收集器
    - 最后的重置线程,指的是清空跟收集相关的数据并重置,为下一次收集做准备
    - 优点:低停顿、并发执行
    - 缺点:
      - 并发执行,对CPU资源压力大;
      - 无法处理在处理过程中产生的垃圾,可能导致FullGC
      - 采用的标记清除算法会导致大量碎片,从而在分配大对象是可能触发FullGC
    - 开启 : -XX:UseConcMarkSweepGC : 使用ParNew +CMS + Serial Old的收集器组合, Serial Old将作为CMS出错的后备收集器
    - -XX:CMSInitiatingOccupancyFraction :设置CMS收集器在老年代空间被使用多少后触发回收,默认80%

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpoagf6b7ej30uj0ckahp.jpg)

  - G1收集器

    - G1 ( Garbage-First )收集器:是一款面向服务端应用的收集器,与其它收集器相比,具有如下特点:

      - G1把内存划分成多 个独立的区域(Region)
      - G1仍采用分代思想,保留了新生代和老年代,但它们不再是物理隔离的,而是一部分Region的集合 ,且不需要Region是连续的
      - G1能充分利用多CPU、多核环境硬件优势,尽量缩短STW
      - G1整体_上采用标记-整理算法,局部是通过复制算法不会产生内存碎片
      - G1的停顿可预测,能明确指定在一个时间段内 ,消耗在垃圾收集.上的时间不能超过多长时间
      - G1跟踪各个Region里面垃圾堆的价值大小,在后台维护一个优先列表,每次根据允许的时间来回收价值最大的区域,从而保证在有限时间内的高效收集

      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpob0008hsj30m70ckad4.jpg)

    - 跟CMS类似,也分为四个阶段:

      - 初始标记:只标记GCRoots能直接关联到的对象
      - 并发标记:进行GC Roots Tracing的过程
      - 最终标记:修正并发标记期间,因程序运行导致标记发生变化的那一部分对象
      - 筛选回收:根据时间来进行价值最大化的回收

      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpob5tlbiwj30sv0c8dki.jpg)

    - G1收集器新生代回收过程

      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpobbtd5asj31an0djtdg.jpg)

    - G1收集器老年代回收过程

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpobharau2j30jt0engnr.jpg)

    - G1开启与配置
      - 使用和配置G1 : -XX:+UseG1GC :开启G1 ,默认就是G1.-XX:MaxGCPauseMillis=n :最大GC停顿时间,这是个软目标，JVM将尽可能(但不保证)停顿小于这个时间
      - -XX:InitiatingHeapOccupancyPercent=n :堆占用了多少的时候就触发GC ,默认为45
      - -XX:ParallelGCThreads=n :并行GC的线程数,默认值会根据平台不同而不同
      - -XX:ConcGCThreads=n :并发GC使用的线程数
      - -XX:G1ReservePercent=n :设置作为空闲空间的预留内存百分比,以降低目标空间溢出的风险,默认值是10%
    - -XX:G1HeapRegionSize=n :设置的G1区域的大小。值是2的幂,范围是1MB到32MB。目标是根据最小的Java堆大小划分出约2048个区域

  - 了解ZGC收集器

    - ZGC收集器: JDK11加入的具有实验性质的低延迟收集器
    - ZGC的设计目标是:支持TB级内存容量,暂停时间低(<10ms ) , 对整个程序吞吐量的影响小于15%
    - ZGC里面的新技术:着色指针和读屏障

  - GC性能指标

    - 吞吐量=应用代码执行的时间/运行的总时间
    - GC负荷,与吞吐量相反,是GC时间/运行的总时间
    - 暂停时间,就是发生Stop-the-World的总时间
    - GC频率,就是GC在一个时间段发生的次数
    - 反应速度,就是从对象成为垃圾到被回收的时间
    - 交互式应用通常希望暂停时间越少越好

  - JVM内存配置原则

    - 新生代尽可能设置大点,如果太小会导致:
      - YGC次数更加频繁
      - 可能导致YGC后的对象进入老年代,如果此时老年代满了，会触发FGC
    - 对老年代,针对响应时间优先的应用:由于老年代通常采用并发收集器,因此其大小要综合考虑并发量和并发持续时间等参数
      - 如果设置小了,可能会造成内存碎片,高回收频率会导致应用暂停
      - 如果设置大了，会需要较长的回收时间
    - 对老年代,针对吞吐量优先的应用:通常设置较大的新生代和较小的老年代,这样可以尽可能回收大部分短期对象,减少中期对象,而老年代尽量存放长期存活的对象
    - 依据对象的存活周期进行分类,对象优先在新生代分配,长时间存活的对象进入老年代
    - 根据不同代的特点,选取合适的收集算法:少量对象存活，适合复制算法;大量对象存活,适合标记清除或者标记整理

- JVM对高效并发的支持

  - Java内存模型

    - JCP定义了- -种Java内存模型,以前是在JVM规范中,后来独立出来成为JSR-133 ( Java内存模型和线程规范修订)
    - 内存模型:在特定的操作协议下,对特定的内存或高速缓存进行读写访问的过程抽象
    - Java内存模型主要关注JVM中把变量值存储到内存和从内存中取出变量值这样的底层细节
    - 所有变量(共享的)都存储在主内存中,每个线程都有自己的工作内存;工作内存中保存该线程使用到的变量的主内存副本拷贝
    - 线程对变量的所有操作(读、写)都应该在工作内存中完成
    - 不同线程不能相互访问工作内存,交互数据要通过主内存

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpoc3t9i7ej30f40ccjut.jpg)

  - 内存间的交互操作

    - Java内存模型规定了一些操作来实现内存间交互 ，JVM会保证它们是原子的
    - lock :锁定,把变量标识为线程独占,作用于主内存变量，
    - unlock :解锁,把锁定的变量释放,别的线程才能使用,作用于主内存变量
    - read :读取,把变量值从主内存读取到工作内存
    - load :载入,把read读取到的值放入工作内存的变量副本中
    - use :使用,把工作内存中一个变量的值传递给执行引|擎
    - assign :赋值,把从执行引|擎接收到的值赋给工作内存里面的变量
    - store :存储,把工作内存中一个变量的值传递到主内存中
    - write :写入，把store进来的数据存放如主内存的变量中

    ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpocdon1rbj30ix0c4wj4.jpg)

    - 内存见交互规则
      - 不允许read和load、store和write操作之一-单独出现, 以上两个操作必须按顺序执行,但不保证连续执行,也就是说，read与load之间、store 与write之间是可插入其他指令的
      - 不允许一个线程丢弃它的最近的assign操作,即变量在工作内存中改变了之后必须把该变化同步回主内存
      - 不允许一个线程无原因地(没有发生过任何assign操作)把数据从线程的工作内存同步回主内存中
      - -个新的变量只能从主内存中"诞生”, 不允许在工作内存中直接使用一个未被初始化的变量,也就是对一个变量实施use和store操作之前,必须先执行过了assign和load操作
      - 一个变量在同一个时刻只允许一条线程对其执行lock操作 ,但lock操作可以被同一-个条线程重复执行多次,多次执行lock后,只有执行相同次数的unlock操作,变量才会被解锁
      - 如果对一个变量执行lock操作,将会清空工作内存中此变量的值,在执行引|擎使用这个变量前,需要重新执行load或assign操作初始化变量的值
      - 如果一-个变量没有被lock操作锁定,则不允许对它执行↑unlock操作,也不能unlock-一个被其他线程锁定的变量
      - 对一个变量执行unlock操作之前,必须先把此变量同步回主内存(执行store和write操作)

  - 多线程的可见性

    - 可见性:就是一-个线程修改了变量,其他线程可以知道
    - 保证可见性的常见方法: volatile、synchronized: final(一旦初始化完成,其他线程就可见)
    - volatile
      - volatile基本_上是JVM提供的最轻量级的同步机制,用volatile修饰的变量,对所有的线程可见,即对volatile变量所做的写操作能立即反映到其它线程中
      - 用volatile修饰的变量,在多线程环境下仍然是不安全的
      - volatile修饰的变量,是禁止指令重排优化的
      - 适合使用valatile的场景:
        - 运算结果不依赖变量的当前值
        - 能确定当前只有一个线程对该变量修改

  - 有序性

    - 在本线程内,操作都是有序的
    - 在线程外观察,操作都是无序的,因为存在指令重排或主内存同步延时

  - 指令重排

    - 指令重排:指的是JVM为了优化,在条件允许的情况下,对指令进行一定的重新排列,直接运行当前能够立即执行的后续指令,避开获取下一 条指令所需数据造成的等待
    - 线程内串行语义,不考虑多线程间的语义
    - 不是所有的指令都能重拍,比如:
      - 写后读a= 1;b= a;写一 个变量之后,再读这个位置
      - 写后写a= 1;a= 2; 写一个变量之后,再写这个变量
      - 读后写a= b;b= 1;读- -个变量之后，再写这个变量
      - 以上语句不可重排,但是分别写：a=1;b= 2;是可以重排的
    - 指令重排基本规则
      - 程序顺序原则:一个线程内保证语义的串行性
      - volatile规则: volatile变量的写,先发生于读
      - 锁规则:解锁(unlock)必然发生在随后的加锁(lock)前
      - 传递性: A先于B , B先于C那么A必然先于C
      - 线程的start方法先于它的每一个动作
      - 线程的所有操作先于线程的终结( Thread.join() )
      - 线程的中断( interrupt() )先于被中断线程的代码
      - 对象的构造函数执行结束先于finalize()方法

  - 线程安全的处理方法

    - 不可变是线程安全的
    - 互斥同步(阻塞同步) : synchronized.java.util.concurrent.ReentrantLock。目前这两个方法性能已经差不多了,建议优先选用synchronized 
    - ReentrantLock增加了如下特性:
      - 等待可中断:当持有锁的线程长时间不释放锁,正在等待的线程可以选择放弃等待
      - 公平锁:多个线程等待同- -个锁时,须严格按照申请锁的时间顺序来获得锁，缺省是非公平锁
      - 锁绑定多个条件:一个ReentrantLock对象可以绑定多个condition对象,而synchronized是针对一个条件的,如果要多个,就得有多个锁
    - 非阻塞同步:是一-种基于冲突检查的乐观锁定策略,通常是先操作,如果没有冲突,操作就成功了,有冲突再采取其它方式进行补偿处理
    - 无同步方案:其实就是在多线程中,方法并不涉及共享数据，自然也就无需同步了

  - 锁优化

    - 自旋锁
      - 自旋:如果线程可以很快获得锁,那么可以不在OS层挂起线程,而是让线程做几个忙循环,这就是自旋
      - 自适应自旋:自旋的时间不再固定,而是由前一次在同一个锁上的自旋时间和锁的拥有者状态来决定
      - 如果锁被占用时间很短，自旋成功,那么能节省线程挂起、以及切换时间,从而提升系统性能
      - 如果锁被占用时间很长,自旋失败,会白白耗费处理器资源,降低系统性能
    - 锁消除
      - 在编译代码的时候,检测到根本不存在共享数据竞争，自然也就无需同步加锁了;通过-XX:+ EliminateLocks来开启
      - 同时要使用.XX:+ DoEscapeAnalysis开启逃逸分析,所谓逃逸分析
        - 如果-一个方法中定义的一-个对象,可能被外部方法引用,称为方法逃逸
        - 如果对象可能被其它外部线程访问,称为线程逃逸，比如赋值给类变量或者可以在其它线程中访问的实例变量
    - 锁粗化
    - 通常我们都要求同步块要小，但-系列连续的操作导致对一个对象反复的加锁和解锁,这会导致不必要的性能损耗。这种情况建议把锁同步的范围加大到整个操作序列
    - 轻量级锁
      - 轻量级是相对于传统锁机制而言,本意是没有多线程竞争的情况下,减少传统锁机制使用OS实现互斥所产生的性能损耗
      - 其实现原理很简单,就是类似乐观锁的方式
      - 如果轻量级锁失败,表示存在竞争,升级为重量级锁,导致性能下降
    - 偏向锁
      - 偏向锁是在无竞争情况下,直接把整个同步消除了,连乐观锁都不用,从而提高性能;所谓的偏向,就是偏心,即锁会偏向于当前已经占有锁的线程
      - 只要没有竞争,获得偏向锁的线程,在将来进入同步块,也不需要做同步
      - 当有其它线程请求相同的锁时,偏向模式结束
      - 如果程序中大多数锁总是被多个线程访问的时候,也就是竞争比较激烈,偏向锁反而会降低性能
      - 使用-XX:-UseBiasedLocking来禁用偏向锁,默认开启

  - JVM中获取锁的步骤

    - 会先尝试偏向锁;然后尝试轻量级锁
    - 再然后尝试自旋锁
    - 最后尝试普通锁,使用OS互斥量在操作系统层挂起

  - 同步代码的基本规则

    - 尽量减少锁持有的时间
    - 尽量减小锁的粒度

- 性能监控与故障处理工具

  - 命令行工具: jps、 jinfo. jstack. jmap、jstat、jstatd、 jcmd 
    - jps
      - jps(JVM Process Status Tool) : 主要用来输出JVM中运行的进程状态信息,语法格式如下: jps [options] [hostid]
      - hostid字符串的语法与URI的语法基本一致:[protocol:][/]hostname]:port]/servername] , 如果不指定hostid ,默认为当前主机或服务器。
    - jinfo
      - 打印给定进程或核心文件或远程调试服务器的配置信息。语法格式: jinfo [ option ] pid #指定进程号(pid)的进程
      - jinfo [ option] < executable <core> #指定核心文件
      - jinfo [ option ] [server-id@]<remate-hostname- or-IP>指定远程调试服务器
    - jstack
      - jstack主要用来查看某个Java进程内的线程堆栈信息。语法格式如下 : jstack [option] pid
      - jstack [option] executable core
      - jstack [option] [server-id@]remote-hostname-or-ip
    - jmap
      - jmap用来查看 堆内存使用状况,语法格式如 下:jmap[option] pid
      -  jmap [option] executable core
      - jmap [option] [server-id@]remote-hostname-or-ip
    - jstat 
      - JVM统计监测工具,查看各个区内存和GC的情况
      - 语法格式如下: jstat [ generalOption I outputOptions vmid [interval[sms] [count]] ]
    - jstated
      - 虚拟机的jstat守护进程,主要用于监控JVM的创建与终止，并提供一个接口,以允许远程监视工具附加到在本地系统上运行的JVM
    - jcmd
      - JVM诊断命令工具,将诊断命令请求发送到正在运行的Java虚拟机,比如可以用来导出堆,查看java进程,导出线程信息，执行GC等
  - 图形化工具: jconsole、 jmc、 visualvm
    - jconsole
      - 一个用于监视Java虚拟机的符合JMX的图形工具。它可以监视本地和远程JVM ,还可以监视和管理应用程序
    - jmc
      - jmc ( JDK Mission Control ) Java任务控制( JMC )客户端包括用于监视和管理Java应用程序的工具,而不会引入通常与这些类型的工具相关联的性能开销
      - 新版jmc已经从jdk中独立出来
      - JFR需要JDK的商业证书,需要解锁jdk的商业特性,例如:jcmd 1152 VM.unlock_ _commercial features
      - 可以直接使用命令行来启动JFR ,例如: jcmd 41250JFR.start delay= 10s duration= 1mfjlename=/Users/cc/Desktop/log.jfr
    - jvisualvm
      - 个图形工具,它提供有关在Java虚拟机中运行的基于Java技术的应用程序的详细信息
      - Java VisualVM提供内存和CPU分析,堆转储分析,内存泄漏检测,访问MBean和垃圾回收。
      - 新版visualvm已经从jdk中独立出来
  - 两种远程连接方式: JMX、 jstatd

- JVM调优实战
  - JVM调优
    - 调什么
    - 如何调
    - 调优的目标是什么
    
  - JVM调优策略
  
    - 减少创建对象的数量
    - 减少使用全局变量和大对象
    - 调整新生代、老年代的大小到最合适
    - 选择合适的GC收集器,并设置合理的参数
  
  - JVM调优冷思考
  
    - 多数的Java应用不需要在服务器上进行GC优化
    - 多数导致GC问题的Java应用,都不是因为参数设置错误而是代码问题
    - 在应用上线之前,先考虑将机器的JVM参数设置到最优(最适合)
    - JVM优化是到最后不得已才采用的手段
    - 在实际使用中,分析JVM情况优化代码比优化JVM本身要多得多
    - 如下情况通常不用优化:
      - Minor GC执行时间不到50ms
      - Minor GC执行不频繁, 约10秒- -次
      - Full GC执行时间不到1s
      - Full GC执行频率不算频繁,不低于10分钟1次
  
  - JVM调优经验
  
    - 要注意32位和64位的区别,通常32位的仅支持2-3g左右的内存
    - 要注意client模式和Server模式的选择
    - 要想GC时间小必须要一个更小的堆;而要保证GC次数足够少,又必须保证一个更大的堆,这两个是有冲突的,只能取其平衡
    - 针对JVM堆的设置,一般可以通过-Xms -Xmx限定其最小最大值,为了防止垃圾收集器在最小、最大之间收缩堆而产生额外的时间,通常把最大、最小设置为相同的值
    - 新生代和老年代将根据默认的比例( 1 : 2 )分配堆内存，可以通过调整二者之间的比率NewRadio来调整,也可以通过-XX:newSize -XX:MaxNewSize来设置其绝对大小，同样为了防止新生的堆收缩,通常会把XX:newSize -XX:MaxNewSize设置为同样大小
    - 合理规划新生代和老年代的大小
    - 如果应用存在大量的临时对象,应该选择更大的新生代;如果存在相对较多的持久对象，老年代应该适当增大。在抉择时应该本着Full GC尽量少的原则,让老年代尽量缓存常用对象, JVM的默认比例1 : 2也是这个道理
    - 通过观察应用一段时间,看其在峰值时老年代会占多少内存在不影响Full GC的前提下,根据实际情况加大新生代，但应该给老年代至少预留1/3的增长空间
    - 线程堆栈的设置:每个线程默认会开启1M的堆栈,用于存放栈帧、调用参数、局部变量等,对大多数应用而言这个默认值太大了，-般256K就足用。在内存不变的情况下,减少每个线程的堆栈,可以产生更多的线程
  
  - 分析和处理内存泄漏
  
    - 内存泄露导致系统崩溃前的一些现象,比如:
      - 每次垃圾回收的时间越来越长, FulIGC时间也延长到好几秒
      - FullGC的次数越来越多, 最频繁时隔不到1分钟就进行-次FullGC
      - 老年代的内存越来越大,并且每次FullGC后年老代没有内存被释放
      - 老年代堆空间被占满的情况
      - 这种情况的解决方式: - -般就是根据垃圾回收前后情况对比同时根据对象引用情况分析,辅助去查找泄漏点
      - 堆栈溢出的情况
      - 通常抛出java.lang.StackOverflowError例外
      - -般就是递归调用没退出,或者循环调用造成
  
  - 调优实战
  
    - 知道字节码吗? Integer x =5,inty =5比较x ==y都经过哪些步骤?
  
      ![](https://tva1.sinaimg.cn/large/008eGmZEly1gpqoneiyfsj30qy0d67a3.jpg)





























<!-- GFM-TOC -->

* [Java 虚拟机](#java-虚拟机)
    * [一、运行时数据区域](#一运行时数据区域)
        * [程序计数器](#程序计数器)
        * [Java 虚拟机栈](#java-虚拟机栈)
        * [本地方法栈](#本地方法栈)
        * [堆](#堆)
        * [方法区](#方法区)
        * [运行时常量池](#运行时常量池)
        * [直接内存](#直接内存)
    * [二、垃圾收集](#二垃圾收集)
        * [判断一个对象是否可被回收](#判断一个对象是否可被回收)
        * [引用类型](#引用类型)
        * [垃圾收集算法](#垃圾收集算法)
        * [垃圾收集器](#垃圾收集器)
    * [三、内存分配与回收策略](#三内存分配与回收策略)
        * [Minor GC 和 Full GC](#minor-gc-和-full-gc)
        * [内存分配策略](#内存分配策略)
        * [Full GC 的触发条件](#full-gc-的触发条件)
    * [四、类加载机制](#四类加载机制)
        * [类的生命周期](#类的生命周期)
        * [类加载过程](#类加载过程)
        * [类初始化时机](#类初始化时机)
        * [类与类加载器](#类与类加载器)
        * [类加载器分类](#类加载器分类)
        * [双亲委派模型](#双亲委派模型)
        * [自定义类加载器实现](#自定义类加载器实现)
    * [参考资料](#参考资料)
    <!-- GFM-TOC -->


本文大部分内容参考   **周志明《深入理解 Java 虚拟机》**  ，想要深入学习的话请看原书。

## 一、运行时数据区域

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/5778d113-8e13-4c53-b5bf-801e58080b97.png" width="400px"> </div><br>

### 程序计数器

记录正在执行的虚拟机字节码指令的地址（如果正在执行的是本地方法则为空）。

### Java 虚拟机栈

每个 Java 方法在执行的同时会创建一个栈帧用于存储局部变量表、操作数栈、常量池引用等信息。从方法调用直至执行完成的过程，对应着一个栈帧在 Java 虚拟机栈中入栈和出栈的过程。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/8442519f-0b4d-48f4-8229-56f984363c69.png" width="400px"> </div><br>

可以通过 -Xss 这个虚拟机参数来指定每个线程的 Java 虚拟机栈内存大小，在 JDK 1.4 中默认为 256K，而在 JDK 1.5+ 默认为 1M：

```java
java -Xss2M HackTheJava
```

该区域可能抛出以下异常：

- 当线程请求的栈深度超过最大值，会抛出 StackOverflowError 异常；
- 栈进行动态扩展时如果无法申请到足够内存，会抛出 OutOfMemoryError 异常。

### 本地方法栈

本地方法栈与 Java 虚拟机栈类似，它们之间的区别只不过是本地方法栈为本地方法服务。

本地方法一般是用其它语言（C、C++ 或汇编语言等）编写的，并且被编译为基于本机硬件和操作系统的程序，对待这些方法需要特别处理。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/66a6899d-c6b0-4a47-8569-9d08f0baf86c.png" width="300px"> </div><br>

### 堆

所有对象都在这里分配内存，是垃圾收集的主要区域（"GC 堆"）。

现代的垃圾收集器基本都是采用分代收集算法，其主要的思想是针对不同类型的对象采取不同的垃圾回收算法。可以将堆分成两块：

- 新生代（Young Generation）
- 老年代（Old Generation）

堆不需要连续内存，并且可以动态增加其内存，增加失败会抛出 OutOfMemoryError 异常。

可以通过 -Xms 和 -Xmx 这两个虚拟机参数来指定一个程序的堆内存大小，第一个参数设置初始值，第二个参数设置最大值。

```java
java -Xms1M -Xmx2M HackTheJava
```

### 方法区

用于存放已被加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。

和堆一样不需要连续的内存，并且可以动态扩展，动态扩展失败一样会抛出 OutOfMemoryError 异常。

对这块区域进行垃圾回收的主要目标是对常量池的回收和对类的卸载，但是一般比较难实现。

HotSpot 虚拟机把它当成永久代来进行垃圾回收。但很难确定永久代的大小，因为它受到很多因素影响，并且每次 Full GC 之后永久代的大小都会改变，所以经常会抛出 OutOfMemoryError 异常。为了更容易管理方法区，从 JDK 1.8 开始，移除永久代，并把方法区移至元空间，它位于本地内存中，而不是虚拟机内存中。

方法区是一个 JVM 规范，永久代与元空间都是其一种实现方式。在 JDK 1.8 之后，原来永久代的数据被分到了堆和元空间中。元空间存储类的元信息，静态变量和常量池等放入堆中。

### 运行时常量池

运行时常量池是方法区的一部分。

Class 文件中的常量池（编译器生成的字面量和符号引用）会在类加载后被放入这个区域。

除了在编译期生成的常量，还允许动态生成，例如 String 类的 intern()。

### 直接内存

在 JDK 1.4 中新引入了 NIO 类，它可以使用 Native 函数库直接分配堆外内存，然后通过 Java 堆里的 DirectByteBuffer 对象作为这块内存的引用进行操作。这样能在一些场景中显著提高性能，因为避免了在堆内存和堆外内存来回拷贝数据。

## 二、垃圾收集

垃圾收集主要是针对堆和方法区进行。程序计数器、虚拟机栈和本地方法栈这三个区域属于线程私有的，只存在于线程的生命周期内，线程结束之后就会消失，因此不需要对这三个区域进行垃圾回收。

### 判断一个对象是否可被回收

#### 1. 引用计数算法

为对象添加一个引用计数器，当对象增加一个引用时计数器加 1，引用失效时计数器减 1。引用计数为 0 的对象可被回收。

在两个对象出现循环引用的情况下，此时引用计数器永远不为 0，导致无法对它们进行回收。正是因为循环引用的存在，因此 Java 虚拟机不使用引用计数算法。

```java
public class Test {

    public Object instance = null;

    public static void main(String[] args) {
        Test a = new Test();
        Test b = new Test();
        a.instance = b;
        b.instance = a;
        a = null;
        b = null;
        doSomething();
    }
}
```

在上述代码中，a 与 b 引用的对象实例互相持有了对象的引用，因此当我们把对 a 对象与 b 对象的引用去除之后，由于两个对象还存在互相之间的引用，导致两个 Test 对象无法被回收。

#### 2. 可达性分析算法

以 GC Roots 为起始点进行搜索，可达的对象都是存活的，不可达的对象可被回收。

Java 虚拟机使用该算法来判断对象是否可被回收，GC Roots 一般包含以下内容：

- 虚拟机栈中局部变量表中引用的对象
- 本地方法栈中 JNI 中引用的对象
- 方法区中类静态属性引用的对象
- 方法区中的常量引用的对象

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/83d909d2-3858-4fe1-8ff4-16471db0b180.png" width="350px"> </div><br>


#### 3. 方法区的回收

因为方法区主要存放永久代对象，而永久代对象的回收率比新生代低很多，所以在方法区上进行回收性价比不高。

主要是对常量池的回收和对类的卸载。

为了避免内存溢出，在大量使用反射和动态代理的场景都需要虚拟机具备类卸载功能。

类的卸载条件很多，需要满足以下三个条件，并且满足了条件也不一定会被卸载：

- 该类所有的实例都已经被回收，此时堆中不存在该类的任何实例。
- 加载该类的 ClassLoader 已经被回收。
- 该类对应的 Class 对象没有在任何地方被引用，也就无法在任何地方通过反射访问该类方法。

#### 4. finalize()

类似 C++ 的析构函数，用于关闭外部资源。但是 try-finally 等方式可以做得更好，并且该方法运行代价很高，不确定性大，无法保证各个对象的调用顺序，因此最好不要使用。

当一个对象可被回收时，如果需要执行该对象的 finalize() 方法，那么就有可能在该方法中让对象重新被引用，从而实现自救。自救只能进行一次，如果回收的对象之前调用了 finalize() 方法自救，后面回收时不会再调用该方法。

### 引用类型

无论是通过引用计数算法判断对象的引用数量，还是通过可达性分析算法判断对象是否可达，判定对象是否可被回收都与引用有关。

Java 提供了四种强度不同的引用类型。

#### 1. 强引用

被强引用关联的对象不会被回收。

使用 new 一个新对象的方式来创建强引用。

```java
Object obj = new Object();
```

#### 2. 软引用

被软引用关联的对象只有在内存不够的情况下才会被回收。

使用 SoftReference 类来创建软引用。

```java
Object obj = new Object();
SoftReference<Object> sf = new SoftReference<Object>(obj);
obj = null;  // 使对象只被软引用关联
```

#### 3. 弱引用

被弱引用关联的对象一定会被回收，也就是说它只能存活到下一次垃圾回收发生之前。

使用 WeakReference 类来创建弱引用。

```java
Object obj = new Object();
WeakReference<Object> wf = new WeakReference<Object>(obj);
obj = null;
```

#### 4. 虚引用

又称为幽灵引用或者幻影引用，一个对象是否有虚引用的存在，不会对其生存时间造成影响，也无法通过虚引用得到一个对象。

为一个对象设置虚引用的唯一目的是能在这个对象被回收时收到一个系统通知。

使用 PhantomReference 来创建虚引用。

```java
Object obj = new Object();
PhantomReference<Object> pf = new PhantomReference<Object>(obj, null);
obj = null;
```

### 垃圾收集算法

#### 1. 标记 - 清除

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/005b481b-502b-4e3f-985d-d043c2b330aa.png" width="400px"> </div><br>

在标记阶段，程序会检查每个对象是否为活动对象，如果是活动对象，则程序会在对象头部打上标记。

在清除阶段，会进行对象回收并取消标志位，另外，还会判断回收后的分块与前一个空闲分块是否连续，若连续，会合并这两个分块。回收对象就是把对象作为分块，连接到被称为 “空闲链表” 的单向链表，之后进行分配时只需要遍历这个空闲链表，就可以找到分块。

在分配时，程序会搜索空闲链表寻找空间大于等于新对象大小 size 的块 block。如果它找到的块等于 size，会直接返回这个分块；如果找到的块大于 size，会将块分割成大小为 size 与 (block - size) 的两部分，返回大小为 size 的分块，并把大小为 (block - size) 的块返回给空闲链表。

不足：

- 标记和清除过程效率都不高；
- 会产生大量不连续的内存碎片，导致无法给大对象分配内存。

#### 2. 标记 - 整理

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/ccd773a5-ad38-4022-895c-7ac318f31437.png" width="400px"> </div><br>

让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存。

优点:

- 不会产生内存碎片

不足:

- 需要移动大量对象，处理效率比较低。

#### 3. 复制

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/b2b77b9e-958c-4016-8ae5-9c6edd83871e.png" width="400px"> </div><br>

将内存划分为大小相等的两块，每次只使用其中一块，当这一块内存用完了就将还存活的对象复制到另一块上面，然后再把使用过的内存空间进行一次清理。

主要不足是只使用了内存的一半。

现在的商业虚拟机都采用这种收集算法回收新生代，但是并不是划分为大小相等的两块，而是一块较大的 Eden 空间和两块较小的 Survivor 空间，每次使用 Eden 和其中一块 Survivor。在回收时，将 Eden 和 Survivor 中还存活着的对象全部复制到另一块 Survivor 上，最后清理 Eden 和使用过的那一块 Survivor。

HotSpot 虚拟机的 Eden 和 Survivor 大小比例默认为 8:1，保证了内存的利用率达到 90%。如果每次回收有多于 10% 的对象存活，那么一块 Survivor 就不够用了，此时需要依赖于老年代进行空间分配担保，也就是借用老年代的空间存储放不下的对象。

#### 4. 分代收集

现在的商业虚拟机采用分代收集算法，它根据对象存活周期将内存划分为几块，不同块采用适当的收集算法。

一般将堆分为新生代和老年代。

- 新生代使用：复制算法
- 老年代使用：标记 - 清除 或者 标记 - 整理 算法

### 垃圾收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/c625baa0-dde6-449e-93df-c3a67f2f430f.jpg" width=""/> </div><br>

以上是 HotSpot 虚拟机中的 7 个垃圾收集器，连线表示垃圾收集器可以配合使用。

- 单线程与多线程：单线程指的是垃圾收集器只使用一个线程，而多线程使用多个线程；
- 串行与并行：串行指的是垃圾收集器与用户程序交替执行，这意味着在执行垃圾收集的时候需要停顿用户程序；并行指的是垃圾收集器和用户程序同时执行。除了 CMS 和 G1 之外，其它垃圾收集器都是以串行的方式执行。

#### 1. Serial 收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/22fda4ae-4dd5-489d-ab10-9ebfdad22ae0.jpg" width=""/> </div><br>

Serial 翻译为串行，也就是说它以串行的方式执行。

它是单线程的收集器，只会使用一个线程进行垃圾收集工作。

它的优点是简单高效，在单个 CPU 环境下，由于没有线程交互的开销，因此拥有最高的单线程收集效率。

它是 Client 场景下的默认新生代收集器，因为在该场景下内存一般来说不会很大。它收集一两百兆垃圾的停顿时间可以控制在一百多毫秒以内，只要不是太频繁，这点停顿时间是可以接受的。

#### 2. ParNew 收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/81538cd5-1bcf-4e31-86e5-e198df1e013b.jpg" width=""/> </div><br>

它是 Serial 收集器的多线程版本。

它是 Server 场景下默认的新生代收集器，除了性能原因外，主要是因为除了 Serial 收集器，只有它能与 CMS 收集器配合使用。

#### 3. Parallel Scavenge 收集器

与 ParNew 一样是多线程收集器。

其它收集器目标是尽可能缩短垃圾收集时用户线程的停顿时间，而它的目标是达到一个可控制的吞吐量，因此它被称为“吞吐量优先”收集器。这里的吞吐量指 CPU 用于运行用户程序的时间占总时间的比值。

停顿时间越短就越适合需要与用户交互的程序，良好的响应速度能提升用户体验。而高吞吐量则可以高效率地利用 CPU 时间，尽快完成程序的运算任务，适合在后台运算而不需要太多交互的任务。

缩短停顿时间是以牺牲吞吐量和新生代空间来换取的：新生代空间变小，垃圾回收变得频繁，导致吞吐量下降。

可以通过一个开关参数打开 GC 自适应的调节策略（GC Ergonomics），就不需要手工指定新生代的大小（-Xmn）、Eden 和 Survivor 区的比例、晋升老年代对象年龄等细节参数了。虚拟机会根据当前系统的运行情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间或者最大的吞吐量。

#### 4. Serial Old 收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/08f32fd3-f736-4a67-81ca-295b2a7972f2.jpg" width=""/> </div><br>

是 Serial 收集器的老年代版本，也是给 Client 场景下的虚拟机使用。如果用在 Server 场景下，它有两大用途：

- 在 JDK 1.5 以及之前版本（Parallel Old 诞生以前）中与 Parallel Scavenge 收集器搭配使用。
- 作为 CMS 收集器的后备预案，在并发收集发生 Concurrent Mode Failure 时使用。

#### 5. Parallel Old 收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/278fe431-af88-4a95-a895-9c3b80117de3.jpg" width=""/> </div><br>

是 Parallel Scavenge 收集器的老年代版本。

在注重吞吐量以及 CPU 资源敏感的场合，都可以优先考虑 Parallel Scavenge 加 Parallel Old 收集器。

#### 6. CMS 收集器

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/62e77997-6957-4b68-8d12-bfd609bb2c68.jpg" width=""/> </div><br>

CMS（Concurrent Mark Sweep），Mark Sweep 指的是标记 - 清除算法。

分为以下四个流程：

- 初始标记：仅仅只是标记一下 GC Roots 能直接关联到的对象，速度很快，需要停顿。
- 并发标记：进行 GC Roots Tracing 的过程，它在整个回收过程中耗时最长，不需要停顿。
- 重新标记：为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，需要停顿。
- 并发清除：不需要停顿。

在整个过程中耗时最长的并发标记和并发清除过程中，收集器线程都可以与用户线程一起工作，不需要进行停顿。

具有以下缺点：

- 吞吐量低：低停顿时间是以牺牲吞吐量为代价的，导致 CPU 利用率不够高。
- 无法处理浮动垃圾，可能出现 Concurrent Mode Failure。浮动垃圾是指并发清除阶段由于用户线程继续运行而产生的垃圾，这部分垃圾只能到下一次 GC 时才能进行回收。由于浮动垃圾的存在，因此需要预留出一部分内存，意味着 CMS 收集不能像其它收集器那样等待老年代快满的时候再回收。如果预留的内存不够存放浮动垃圾，就会出现 Concurrent Mode Failure，这时虚拟机将临时启用 Serial Old 来替代 CMS。
- 标记 - 清除算法导致的空间碎片，往往出现老年代空间剩余，但无法找到足够大连续空间来分配当前对象，不得不提前触发一次 Full GC。

#### 7. G1 收集器

G1（Garbage-First），它是一款面向服务端应用的垃圾收集器，在多 CPU 和大内存的场景下有很好的性能。HotSpot 开发团队赋予它的使命是未来可以替换掉 CMS 收集器。

堆被分为新生代和老年代，其它收集器进行收集的范围都是整个新生代或者老年代，而 G1 可以直接对新生代和老年代一起回收。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/4cf711a8-7ab2-4152-b85c-d5c226733807.png" width="600"/> </div><br>

G1 把堆划分成多个大小相等的独立区域（Region），新生代和老年代不再物理隔离。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/9bbddeeb-e939-41f0-8e8e-2b1a0aa7e0a7.png" width="600"/> </div><br>

通过引入 Region 的概念，从而将原来的一整块内存空间划分成多个的小空间，使得每个小空间可以单独进行垃圾回收。这种划分方法带来了很大的灵活性，使得可预测的停顿时间模型成为可能。通过记录每个 Region 垃圾回收时间以及回收所获得的空间（这两个值是通过过去回收的经验获得），并维护一个优先列表，每次根据允许的收集时间，优先回收价值最大的 Region。

每个 Region 都有一个 Remembered Set，用来记录该 Region 对象的引用对象所在的 Region。通过使用 Remembered Set，在做可达性分析的时候就可以避免全堆扫描。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/f99ee771-c56f-47fb-9148-c0036695b5fe.jpg" width=""/> </div><br>

如果不计算维护 Remembered Set 的操作，G1 收集器的运作大致可划分为以下几个步骤：

- 初始标记
- 并发标记
- 最终标记：为了修正在并发标记期间因用户程序继续运作而导致标记产生变动的那一部分标记记录，虚拟机将这段时间对象变化记录在线程的 Remembered Set Logs 里面，最终标记阶段需要把 Remembered Set Logs 的数据合并到 Remembered Set 中。这阶段需要停顿线程，但是可并行执行。
- 筛选回收：首先对各个 Region 中的回收价值和成本进行排序，根据用户所期望的 GC 停顿时间来制定回收计划。此阶段其实也可以做到与用户程序一起并发执行，但是因为只回收一部分 Region，时间是用户可控制的，而且停顿用户线程将大幅度提高收集效率。

具备如下特点：

- 空间整合：整体来看是基于“标记 - 整理”算法实现的收集器，从局部（两个 Region 之间）上来看是基于“复制”算法实现的，这意味着运行期间不会产生内存空间碎片。
- 可预测的停顿：能让使用者明确指定在一个长度为 M 毫秒的时间片段内，消耗在 GC 上的时间不得超过 N 毫秒。

## 三、内存分配与回收策略

### Minor GC 和 Full GC

- Minor GC：回收新生代，因为新生代对象存活时间很短，因此 Minor GC 会频繁执行，执行的速度一般也会比较快。

- Full GC：回收老年代和新生代，老年代对象其存活时间长，因此 Full GC 很少执行，执行速度会比 Minor GC 慢很多。

### 内存分配策略

#### 1. 对象优先在 Eden 分配

大多数情况下，对象在新生代 Eden 上分配，当 Eden 空间不够时，发起 Minor GC。

#### 2. 大对象直接进入老年代

大对象是指需要连续内存空间的对象，最典型的大对象是那种很长的字符串以及数组。

经常出现大对象会提前触发垃圾收集以获取足够的连续空间分配给大对象。

-XX:PretenureSizeThreshold，大于此值的对象直接在老年代分配，避免在 Eden 和 Survivor 之间的大量内存复制。

#### 3. 长期存活的对象进入老年代

为对象定义年龄计数器，对象在 Eden 出生并经过 Minor GC 依然存活，将移动到 Survivor 中，年龄就增加 1 岁，增加到一定年龄则移动到老年代中。

-XX:MaxTenuringThreshold 用来定义年龄的阈值。

#### 4. 动态对象年龄判定

虚拟机并不是永远要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升老年代，如果在 Survivor 中相同年龄所有对象大小的总和大于 Survivor 空间的一半，则年龄大于或等于该年龄的对象可以直接进入老年代，无需等到 MaxTenuringThreshold 中要求的年龄。

#### 5. 空间分配担保

在发生 Minor GC 之前，虚拟机先检查老年代最大可用的连续空间是否大于新生代所有对象总空间，如果条件成立的话，那么 Minor GC 可以确认是安全的。

如果不成立的话虚拟机会查看 HandlePromotionFailure 的值是否允许担保失败，如果允许那么就会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小，如果大于，将尝试着进行一次 Minor GC；如果小于，或者 HandlePromotionFailure 的值不允许冒险，那么就要进行一次 Full GC。

### Full GC 的触发条件

对于 Minor GC，其触发条件非常简单，当 Eden 空间满时，就将触发一次 Minor GC。而 Full GC 则相对复杂，有以下条件：

#### 1. 调用 System.gc()

只是建议虚拟机执行 Full GC，但是虚拟机不一定真正去执行。不建议使用这种方式，而是让虚拟机管理内存。

#### 2. 老年代空间不足

老年代空间不足的常见场景为前文所讲的大对象直接进入老年代、长期存活的对象进入老年代等。

为了避免以上原因引起的 Full GC，应当尽量不要创建过大的对象以及数组。除此之外，可以通过 -Xmn 虚拟机参数调大新生代的大小，让对象尽量在新生代被回收掉，不进入老年代。还可以通过 -XX:MaxTenuringThreshold 调大对象进入老年代的年龄，让对象在新生代多存活一段时间。

#### 3. 空间分配担保失败

使用复制算法的 Minor GC 需要老年代的内存空间作担保，如果担保失败会执行一次 Full GC。具体内容请参考上面的第 5 小节。

#### 4. JDK 1.7 及以前的永久代空间不足

在 JDK 1.7 及以前，HotSpot 虚拟机中的方法区是用永久代实现的，永久代中存放的为一些 Class 的信息、常量、静态变量等数据。

当系统中要加载的类、反射的类和调用的方法较多时，永久代可能会被占满，在未配置为采用 CMS GC 的情况下也会执行 Full GC。如果经过 Full GC 仍然回收不了，那么虚拟机会抛出 java.lang.OutOfMemoryError。

为避免以上原因引起的 Full GC，可采用的方法为增大永久代空间或转为使用 CMS GC。

#### 5. Concurrent Mode Failure

执行 CMS GC 的过程中同时有对象要放入老年代，而此时老年代空间不足（可能是 GC 过程中浮动垃圾过多导致暂时性的空间不足），便会报 Concurrent Mode Failure 错误，并触发 Full GC。

## 四、类加载机制

类是在运行期间第一次使用时动态加载的，而不是一次性加载所有类。因为如果一次性加载，那么会占用很多的内存。

### 类的生命周期

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/335fe19c-4a76-45ab-9320-88c90d6a0d7e.png" width="600px"> </div><br>

包括以下 7 个阶段：

-   **加载（Loading）**  
-   **验证（Verification）**  
-   **准备（Preparation）**  
-   **解析（Resolution）**  
-   **初始化（Initialization）**  
- 使用（Using）
- 卸载（Unloading）

### 类加载过程

包含了加载、验证、准备、解析和初始化这 5 个阶段。

#### 1. 加载

加载是类加载的一个阶段，注意不要混淆。

加载过程完成以下三件事：

- 通过类的完全限定名称获取定义该类的二进制字节流。
- 将该字节流表示的静态存储结构转换为方法区的运行时存储结构。
- 在内存中生成一个代表该类的 Class 对象，作为方法区中该类各种数据的访问入口。


其中二进制字节流可以从以下方式中获取：

- 从 ZIP 包读取，成为 JAR、EAR、WAR 格式的基础。
- 从网络中获取，最典型的应用是 Applet。
- 运行时计算生成，例如动态代理技术，在 java.lang.reflect.Proxy 使用 ProxyGenerator.generateProxyClass 的代理类的二进制字节流。
- 由其他文件生成，例如由 JSP 文件生成对应的 Class 类。

#### 2. 验证

确保 Class 文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机自身的安全。

#### 3. 准备

类变量是被 static 修饰的变量，准备阶段为类变量分配内存并设置初始值，使用的是方法区的内存。

实例变量不会在这阶段分配内存，它会在对象实例化时随着对象一起被分配在堆中。应该注意到，实例化不是类加载的一个过程，类加载发生在所有实例化操作之前，并且类加载只进行一次，实例化可以进行多次。

初始值一般为 0 值，例如下面的类变量 value 被初始化为 0 而不是 123。

```java
public static int value = 123;
```

如果类变量是常量，那么它将初始化为表达式所定义的值而不是 0。例如下面的常量 value 被初始化为 123 而不是 0。

```java
public static final int value = 123;
```

#### 4. 解析

将常量池的符号引用替换为直接引用的过程。

其中解析过程在某些情况下可以在初始化阶段之后再开始，这是为了支持 Java 的动态绑定。

#### 5. 初始化

<div data="modify -->"></div>
初始化阶段才真正开始执行类中定义的 Java 程序代码。初始化阶段是虚拟机执行类构造器 &lt;clinit\>() 方法的过程。在准备阶段，类变量已经赋过一次系统要求的初始值，而在初始化阶段，根据程序员通过程序制定的主观计划去初始化类变量和其它资源。

&lt;clinit\>() 是由编译器自动收集类中所有类变量的赋值动作和静态语句块中的语句合并产生的，编译器收集的顺序由语句在源文件中出现的顺序决定。特别注意的是，静态语句块只能访问到定义在它之前的类变量，定义在它之后的类变量只能赋值，不能访问。例如以下代码：

```java
public class Test {
    static {
        i = 0;                // 给变量赋值可以正常编译通过
        System.out.print(i);  // 这句编译器会提示“非法向前引用”
    }
    static int i = 1;
}
```

由于父类的 &lt;clinit\>() 方法先执行，也就意味着父类中定义的静态语句块的执行要优先于子类。例如以下代码：

```java
static class Parent {
    public static int A = 1;
    static {
        A = 2;
    }
}

static class Sub extends Parent {
    public static int B = A;
}

public static void main(String[] args) {
     System.out.println(Sub.B);  // 2
}
```

接口中不可以使用静态语句块，但仍然有类变量初始化的赋值操作，因此接口与类一样都会生成 &lt;clinit\>() 方法。但接口与类不同的是，执行接口的 &lt;clinit\>() 方法不需要先执行父接口的 &lt;clinit\>() 方法。只有当父接口中定义的变量使用时，父接口才会初始化。另外，接口的实现类在初始化时也一样不会执行接口的 &lt;clinit\>() 方法。

虚拟机会保证一个类的 &lt;clinit\>() 方法在多线程环境下被正确的加锁和同步，如果多个线程同时初始化一个类，只会有一个线程执行这个类的 &lt;clinit\>() 方法，其它线程都会阻塞等待，直到活动线程执行 &lt;clinit\>() 方法完毕。如果在一个类的 &lt;clinit\>() 方法中有耗时的操作，就可能造成多个线程阻塞，在实际过程中此种阻塞很隐蔽。

### 类初始化时机

#### 1. 主动引用

虚拟机规范中并没有强制约束何时进行加载，但是规范严格规定了有且只有下列五种情况必须对类进行初始化（加载、验证、准备都会随之发生）：

- 遇到 new、getstatic、putstatic、invokestatic 这四条字节码指令时，如果类没有进行过初始化，则必须先触发其初始化。最常见的生成这 4 条指令的场景是：使用 new 关键字实例化对象的时候；读取或设置一个类的静态字段（被 final 修饰、已在编译期把结果放入常量池的静态字段除外）的时候；以及调用一个类的静态方法的时候。

- 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行初始化，则需要先触发其初始化。

- 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化。

- 当虚拟机启动时，用户需要指定一个要执行的主类（包含 main() 方法的那个类），虚拟机会先初始化这个主类；

- 当使用 JDK 1.7 的动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果为 REF_getStatic, REF_putStatic, REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化；

#### 2. 被动引用

以上 5 种场景中的行为称为对一个类进行主动引用。除此之外，所有引用类的方式都不会触发初始化，称为被动引用。被动引用的常见例子包括：

- 通过子类引用父类的静态字段，不会导致子类初始化。

```java
System.out.println(SubClass.value);  // value 字段在 SuperClass 中定义
```

- 通过数组定义来引用类，不会触发此类的初始化。该过程会对数组类进行初始化，数组类是一个由虚拟机自动生成的、直接继承自 Object 的子类，其中包含了数组的属性和方法。

```java
SuperClass[] sca = new SuperClass[10];
```

- 常量在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化。

```java
System.out.println(ConstClass.HELLOWORLD);
```

### 类与类加载器

两个类相等，需要类本身相等，并且使用同一个类加载器进行加载。这是因为每一个类加载器都拥有一个独立的类名称空间。

这里的相等，包括类的 Class 对象的 equals() 方法、isAssignableFrom() 方法、isInstance() 方法的返回结果为 true，也包括使用 instanceof 关键字做对象所属关系判定结果为 true。

### 类加载器分类

从 Java 虚拟机的角度来讲，只存在以下两种不同的类加载器：

- 启动类加载器（Bootstrap ClassLoader），使用 C++ 实现，是虚拟机自身的一部分；

- 所有其它类的加载器，使用 Java 实现，独立于虚拟机，继承自抽象类 java.lang.ClassLoader。

从 Java 开发人员的角度看，类加载器可以划分得更细致一些：

- 启动类加载器（Bootstrap ClassLoader）此类加载器负责将存放在 &lt;JRE_HOME\>\lib 目录中的，或者被 -Xbootclasspath 参数所指定的路径中的，并且是虚拟机识别的（仅按照文件名识别，如 rt.jar，名字不符合的类库即使放在 lib 目录中也不会被加载）类库加载到虚拟机内存中。启动类加载器无法被 Java 程序直接引用，用户在编写自定义类加载器时，如果需要把加载请求委派给启动类加载器，直接使用 null 代替即可。

- 扩展类加载器（Extension ClassLoader）这个类加载器是由 ExtClassLoader（sun.misc.Launcher$ExtClassLoader）实现的。它负责将 &lt;JAVA_HOME\>/lib/ext 或者被 java.ext.dir 系统变量所指定路径中的所有类库加载到内存中，开发者可以直接使用扩展类加载器。

- 应用程序类加载器（Application ClassLoader）这个类加载器是由 AppClassLoader（sun.misc.Launcher$AppClassLoader）实现的。由于这个类加载器是 ClassLoader 中的 getSystemClassLoader() 方法的返回值，因此一般称为系统类加载器。它负责加载用户类路径（ClassPath）上所指定的类库，开发者可以直接使用这个类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。

### 双亲委派模型

应用程序是由三种类加载器互相配合从而实现类加载，除此之外还可以加入自己定义的类加载器。

下图展示了类加载器之间的层次关系，称为双亲委派模型（Parents Delegation Model）。该模型要求除了顶层的启动类加载器外，其它的类加载器都要有自己的父类加载器。这里的父子关系一般通过组合关系（Composition）来实现，而不是继承关系（Inheritance）。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/0dd2d40a-5b2b-4d45-b176-e75a4cd4bdbf.png" width="500px"> </div><br>

#### 1. 工作过程

一个类加载器首先将类加载请求转发到父类加载器，只有当父类加载器无法完成时才尝试自己加载。

#### 2. 好处

使得 Java 类随着它的类加载器一起具有一种带有优先级的层次关系，从而使得基础类得到统一。

例如 java.lang.Object 存放在 rt.jar 中，如果编写另外一个 java.lang.Object 并放到 ClassPath 中，程序可以编译通过。由于双亲委派模型的存在，所以在 rt.jar 中的 Object 比在 ClassPath 中的 Object 优先级更高，这是因为 rt.jar 中的 Object 使用的是启动类加载器，而 ClassPath 中的 Object 使用的是应用程序类加载器。rt.jar 中的 Object 优先级更高，那么程序中所有的 Object 都是这个 Object。

#### 3. 实现

以下是抽象类 java.lang.ClassLoader 的代码片段，其中的 loadClass() 方法运行过程如下：先检查类是否已经加载过，如果没有则让父类加载器去加载。当父类加载器加载失败时抛出 ClassNotFoundException，此时尝试自己去加载。

```java
public abstract class ClassLoader {
    // The parent class loader for delegation
    private final ClassLoader parent;

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    c = findClass(name);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }
}
```

### 自定义类加载器实现

以下代码中的 FileSystemClassLoader 是自定义类加载器，继承自 java.lang.ClassLoader，用于加载文件系统上的类。它首先根据类的全名在文件系统上查找类的字节代码文件（.class 文件），然后读取该文件内容，最后通过 defineClass() 方法来把这些字节代码转换成 java.lang.Class 类的实例。

java.lang.ClassLoader 的 loadClass() 实现了双亲委派模型的逻辑，自定义类加载器一般不去重写它，但是需要重写 findClass() 方法。

```java
public class FileSystemClassLoader extends ClassLoader {

    private String rootDir;

    public FileSystemClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    private byte[] getClassData(String className) {
        String path = classNameToPath(className);
        try {
            InputStream ins = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String classNameToPath(String className) {
        return rootDir + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";
    }
}
```

## 参考资料

- 周志明. 深入理解 Java 虚拟机 [M]. 机械工业出版社, 2011.
- [Chapter 2. The Structure of the Java Virtual Machine](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5.4)
- [Jvm memory](https://www.slideshare.net/benewu/jvm-memory)
[Getting Started with the G1 Garbage Collector](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/G1GettingStarted/index.html)
- [JNI Part1: Java Native Interface Introduction and “Hello World” application](http://electrofriends.com/articles/jni/jni-part1-java-native-interface/)
- [Memory Architecture Of JVM(Runtime Data Areas)](https://hackthejava.wordpress.com/2015/01/09/memory-architecture-by-jvmruntime-data-areas/)
- [JVM Run-Time Data Areas](https://www.programcreek.com/2013/04/jvm-run-time-data-areas/)
- [Android on x86: Java Native Interface and the Android Native Development Kit](http://www.drdobbs.com/architecture-and-design/android-on-x86-java-native-interface-and/240166271)
- [深入理解 JVM(2)——GC 算法与内存分配策略](https://crowhawk.github.io/2017/08/10/jvm_2/)
- [深入理解 JVM(3)——7 种垃圾收集器](https://crowhawk.github.io/2017/08/15/jvm_3/)
- [JVM Internals](http://blog.jamesdbloom.com/JVMInternals.html)
- [深入探讨 Java 类加载器](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/index.html#code6)
- [Guide to WeakHashMap in Java](http://www.baeldung.com/java-weakhashmap)
- [Tomcat example source code file (ConcurrentCache.java)](https://alvinalexander.com/java/jwarehouse/apache-tomcat-6.0.16/java/org/apache/el/util/ConcurrentCache.java.shtml)
