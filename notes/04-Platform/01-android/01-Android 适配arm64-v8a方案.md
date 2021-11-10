# Android 适配arm64-v8a方案

背景

- 32位和64位的差异主要体现在内存寻址上，32位最高只支撑4GB内存，而64位则能够最高支撑128GB内存。
- 目前三星、华为、小米、VIVO、OPPO等手机厂商应用商店已经支持32位和64位。
- 添加 64 位的应用版本不仅可以提升性能、为未来创新创造条件，还能针对仅支持 64 位架构的设备做好准备。

> 尊敬的开发者：
>
> 您好！
>
> 为更好地提升APP性能体验，降低APP功耗影响，小米应用商店与OPPO应用商店、vivo应用商店共同推进国内安卓生态对64位架构的升级支持。行业适配节奏如下：
>
> 2021年12月底：现有和新发布的应用/游戏，需上传包含64位包体的APK包（支持双包在架，和64位兼容32位的两个形式，不再接收仅支持32位的APK包）
> 2022年8月底：硬件支持64位的系统，将仅接收含64位版本的APK包
> 2023年底：硬件将仅支持64位APK，32位应用无法在终端上运行



## Android ABI 


不同的 Android 设备使用不同的 CPU，而不同的 CPU 支持不同的指令集。CPU 与指令集的每种组合都有专属的应用二进制接口 (ABI)。ABI 包含以下信息：

- 可使用的 CPU 指令集（和扩展指令集）。

- 运行时内存存储和加载的字节顺序。Android 始终是 little-endian。
- 在应用和系统之间传递数据的规范（包括对齐限制），以及系统调用函数时如何使用堆栈和寄存器。
- 可执行二进制文件（例如程序和共享库）的格式，以及它们支持的内容类型。Android 始终使用 ELF。
- 如何重整 C++ 名称。





## ABI Match 流程

- 64位适配，就是令APP在支持64位系统的设备上启动64位进程来运行。
- Android手机系统在启动的过程中，会根据设备的ro.zygote属性值决定启动哪类Zygote。如果是支持64位系统的设备，会有两个Zygote(一个32位,一个64位)进程同时运行。

- 在APP安装的过程中，PMS里面的scanPackageDirtyLI方法通过遍历APK文件夹里面的lib下面的so库根目录，再结合该手机硬件支持的abilists列表，来决定primaryCpuAbi的值。
- 在APP启动的过程中，AMS根据前面得到的primaryCpuAbi的值作为参考，通过调用Process的start()方法来确定，该APP是从64位还是32位的Zygote进程fork出子进程。如果子进程来自64位Zygote，该 APP 就运行在 64 位进程。
- 例如：假如 APK 里面的 so 库有 armeabi-v7a 和 arm64-v8a 这两个目录，而某一个手机硬件支持的 abilists 是 armeabi,armeabi-v7a,arm64-v8a；这时候，primaryCpuAbi 的取值就是 arm64-v8a；那么当 APP 启动后，就会运行在 64 位进程。



## SO 库的适配

64 位适配的重点就是 so 库的适配。

APP 里面 so 库的存在方式一般有三种：

- APK 里面自带的静态 so 库；

- 通过插件中心加载的插件内的 so 库；

- 动态下发的 so 库;

### APK 里面 so 库的适配

APK 里面的 so 库有两种适配方案：

- 方案一，这种是比较粗暴的，即构建一个支持所有的 ABI 类型的 APK，优点是可以满足任意机型的安装，缺点是包体积可能变得非常大。

- 方案二，这种是比较轻量级的，即为每个 ABI 类型都单独构建一个 APK，不同 ABI 类型的设备安装对应的 APK，前提条件是应用市场需要根据用户的手机设备 ABI，下发对应合适的 APK 包，目前 Google Play 和国内应用市场都是支持的。
- 微信apk是采用的第二种方案：weixin8015android2020_arm64.apk 和 weixin8015android2020.apk

### 插件内的 so 库的适配

不管是通过何种方式实现的插件化，插件 APK 里面都可能会包含 so 库。插件里面的 so 库加载有三种方案：

方案一，提供包含所有 ABI 类型 so 库的插件包，由插件中心根据 APP 运行的进程情况来加载对应的 so 库；

方案二，提供多个包含不同 ABI 架构的插件包；插件中心在下载的时候，再决定下载哪一种对应的插件 APK；

方案三，提供一个基础的不包含 so 库的 base 插件包，再把每种 CPU 架构的 so 库生成一个 APK，插件中心最后下载 base 插件包以及对应 ABI 类型的 so 库文件；

### 云端下发的 so 库适配

客户端需要根据当前应用运行的进程情况，去获取相应类型的 so 库进行加载。

so 分发管理平台分为两部分：前端 SDK 部分以及后端发布平台部分，详情参考[爱奇艺App架构升级之路64位适配探索与实践](https://mp.weixin.qq.com/s?__biz=MzI0MjczMjM2NA==&mid=2247487720&idx=2&sn=92d1523423c5e45ce5a9b0ea962eca1f&chksm=e9768ccbde0105dd851bbfbb686b1d144e40d0ec226f6588cf91ec45a429ee4dd79ced9cd816&scene=27#wechat_redirect)



## 总结

- 所有引入的三方库，凡是有 armeabi-v7a 的那么就一定要有 arm64-v8a 的 so；
- 动态下载的 so 一定要根据当前 app 的运行位数来确定；
- 应用升级也要注意传递设备支持的位数，下发合适的 apk；
- armeabi目录可以匹配所有的arm架构的cpu,意思是指所有的arm架构的cpu的安卓手机如果没有找到最优的对应的目录，则会去匹配armeabi目录。但是设备与ABI不匹配会影响性能。



## 参考

- [ARM 架构的应用二进制接口 (ABI)详情](https://developer.arm.com/architectures/system-architectures/software-standards/abi)

- [Android ABI](https://developer.android.com/ndk/guides/abis#v7a)
- [爱奇艺App架构升级之路64位适配探索与实践](https://mp.weixin.qq.com/s?__biz=MzI0MjczMjM2NA==&mid=2247487720&idx=2&sn=92d1523423c5e45ce5a9b0ea962eca1f&chksm=e9768ccbde0105dd851bbfbb686b1d144e40d0ec226f6588cf91ec45a429ee4dd79ced9cd816&scene=27#wechat_redirect)

- [贝壳&掌链64位架构适配实践](https://mp.weixin.qq.com/s/JaTv8-gvT0-jSMyeq0FhMw)

- [贝壳&掌链64位架构适配实践](https://mp.weixin.qq.com/s/JaTv8-gvT0-jSMyeq0FhMw)

