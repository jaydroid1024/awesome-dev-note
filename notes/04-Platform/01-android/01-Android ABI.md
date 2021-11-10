

## ABI

ABI | 应用二进制接口 | Application binary interface

[ARM 架构的应用二进制接口 (ABI)详情](https://developer.arm.com/architectures/system-architectures/software-standards/abi)



## Android ABI 

https://developer.android.com/ndk/guides/abis#sa
不同的 Android 设备使用不同的 CPU，而不同的 CPU 支持不同的指令集。CPU 与指令集的每种组合都有专属的应用二进制接口 (ABI)。ABI 包含以下信息：

- 可使用的 CPU 指令集（和扩展指令集）。

- 运行时内存存储和加载的字节顺序。Android 始终是 little-endian。
- 在应用和系统之间传递数据的规范（包括对齐限制），以及系统调用函数时如何使用堆栈和寄存器。
- 可执行二进制文件（例如程序和共享库）的格式，以及它们支持的内容类型。Android 始终使用 ELF。
- 如何重整 C++ 名称。

### NDK 支持的 ABI以及每个 ABI 的运行原理

| ABI                                                          | 支持的指令集                           | 备注                     |
| :----------------------------------------------------------- | :------------------------------------- | :----------------------- |
| [`armeabi-v7a`](https://developer.android.com/ndk/guides/abis#v7a) | armeabiThumb-2VFPv3-D16                | 与 ARMv5/v6 设备不兼容。 |
| [`arm64-v8a`](https://developer.android.com/ndk/guides/abis#arm64-v8a) | AArch64                                |                          |
| [`x86`](https://developer.android.com/ndk/guides/abis#x86)   | x86 (IA-32)MMXSSE/2/3SSSE3             | 不支持 MOVBE 或 SSE4。   |
| [`x86_64`](https://developer.android.com/ndk/guides/abis#86-64) | x86-64MMXSSE/2/3SSSE3SSE4.1、4.2POPCNT |                          |

**注意**：NDK 以前支持 ARMv5 (armeabi) 以及 32 位和 64 位 MIPS，但 NDK r17 已不再支持。

#### armeabi-v7a

此 ABI 适用于基于 32 位 ARM 的 CPU。Android 变体包含 Thumb-2 和 VFP 硬件浮点指令（具体而言就是 VFPv3-D16），其中包含 16 个专用 64 位浮点寄存器。

#### arm64-v8a

此 ABI 适用于基于 ARMv8-A 的 CPU，支持 64 位 AArch64 架构。它包含高级 SIMD (Neon) 架构扩展指令集。

#### x86

此 ABI 适用于支持通常称为“x86”、“i386”或“IA-32”的指令集的 CPU。

#### x86_64

此 ABI 适用于支持通常称为“x86-64”的指令集的 CPU



### 常用adb指令

查看apk的so以及所在目录：

```shell
zipinfo -1 flashman_lite_v1.4.5_beta.apk | grep \.so$


xuejiewang@xuejiedeMacBook-Pro v1.4.5 % zipinfo -1 flashman_lite_v1.4.5_beta.apk | grep \.so$
lib/armeabi/libocr-sdk.so
lib/armeabi-v7a/libtnet-3.1.14.so
lib/armeabi/libumeng-spy.so
lib/armeabi/libalicomphonenumberauthsdk_core.so
lib/armeabi-v7a/libFaceSDK.so
lib/armeabi/libidl_license.so
lib/armeabi/liblbs.so

```

指定安装哪一种ABI，此处是安装32位的

```shell
adb install --abi armeabi-v7a flashman_lite_v1.4.5.apk

xuejiewang@xuejiedeMacBook-Pro v1.4.5 % adb install --abi armeabi-v7a flashman_lite_v1.4.5.apk
Performing Streamed Install
Success
```

可以通过安装判断apk是否支持指定ABI

```
xuejiewang@xuejiedeMacBook-Pro v1.4.5 % adb install --abi arm64-v8a flashman_lite_v1.4.5.apk
Performing Streamed Install
adb: failed to install flashman_lite_v1.4.5.apk: Failure [INSTALL_FAILED_NO_MATCHING_ABIS: Failed to extract native libraries, res=-113]
```





## 参考

[Android ABI](https://developer.android.com/ndk/guides/abis#v7a)



