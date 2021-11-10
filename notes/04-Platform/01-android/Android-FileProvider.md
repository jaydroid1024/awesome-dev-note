# Android FileProvider 解析



## ContentProvider

- 初始化时机
  - ContentProvider.onCreate方法调用介于Application.attachBaseContext和Application.onCreate之间。
- 合并问题
  - 如果项目中的ContentProvider只是重写了onCreate方法是可以被合并的。如果重写了增删改查和call方法，是不能被合并的。
- App Startup 中对 ContentProvider 的实践
  - Jetpack组件中一个简单、高效的应用启动初始化组件，它是基于ContentProvider实现的
- Uri.fromFile()与 FIleProvider.getUriFromFile()获取文件的Uri

|     方式     | 值                                                  |
| :----------: | :-------------------------------------------------- |
|     Uri      | file:///storage/emulated/0/toutiao/toutiao.apk      |
| FileProvider | content://com.toutiao.install/bytedance/toutiao.apk |

Uri方式获取到的文件路径很容易被猜出文件所在位置，这样暴露给第三方程序，可能会带来风险。

FileProvider获取到的文件路径就不容易暴露文件所在位置。

引入FileProvider机制的原因就是为安全考虑。



- xml的tag对应的文件存储位置

| NAME               | VALUE               | PATH                                           |
| :----------------- | :------------------ | :--------------------------------------------- |
| TAG_ROOT_PATH      | root-path           | /                                              |
| TAG_FILES_PATH     | files-path          | /data/user/0/com.xxx/files                     |
| TAG_CACHE_PATH     | cache-path          | /data/user/0/com.xxx/cache                     |
| TAG_EXTERNAL       | external-path       | /storage/emulated/0                            |
| TAG_EXTERNAL_FILES | external-files-path | /storage/emulated/0/Android/data/com.xxx/files |
| TAG_EXTERNAL_CACHE | external-cache-path | /storage/emulated/0/Android/data/com.xxx/cache |

- xml中Uri和路径的映射表
  - FileProvider有一个静态变量sCache。key存放的是FileProvider的authority，value存放的 PathStrategy 代表的是FileProvider对应的xml中的内容。
    - private static HashMap<String, PathStrategy> sCache = new HashMap<String, PathStrategy>();
  - SimplePathStrategy是PathStrategy 的实现类，其中有个 mRoots
    - private final HashMap<String, File> mRoots = new HashMap<String, File>();
    - mRoots也是hashMap，key对应的是xml中的name节点。value对应的是tag+path的组合。

- sCache和mRoots对应关系如下图

  ![图片](https://raw.githubusercontent.com/jaydroid1024/jay_image_repo/main/img/20210816232205.png)



- FileProvider解析xml过程
  - 步骤1：FileProvider自动安装后调用attachInfo 
  - 步骤2：调用parsePathStrategy解析xml 
  - 步骤3：getPathStrategy方法将解析的PathStrategy放入sCache





参考

- [性能优化：如何高效处理多个ContentProvider](https://mp.weixin.qq.com/s/phfYJa99S3_aXSo6Ol_bBw)

