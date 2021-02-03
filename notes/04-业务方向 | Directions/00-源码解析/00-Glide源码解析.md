[Glide官方文档 | 英文版](http://bumptech.github.io/glide/)

[Glide官方文档 | 中文版](https://muyangmin.github.io/glide-docs-cn/)

[Glide Github地址](https://github.com/bumptech/glide)

[本文链接](https://github.com/Jay-Droid/base-dev-design/blob/master/doc/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F-Remix.md)

------

分析版本：Glide V4

## Glide-速高效的Android图片加载库 

Glide提供了易用的API，高性能、可扩展的图片解码管道（`decode pipeline`），以及自动的资源池技术。

Glide 支持拉取，解码和展示视频快照，图片，和GIF动画。

Glide使用的是一个定制化的基于`HttpUrlConnection`的栈，但同时也提供了与 Volley和 OkHttp快速集成的工具库。

Glide 的主要目标是让任何形式的图片列表的滚动尽可能地变得更快、更平滑。

Glide 充分考虑了Android图片加载性能的两个关键方面：

- 图片解码速度
- 解码图片带来的资源压力

Glide使用了多个步骤来确保在Android上加载图片尽可能的快速和平滑：

- 自动、智能地下采样(`downsampling`)和缓存(`caching`)，以最小化存储开销和解码次数；
- 积极的资源重用，例如字节数组和Bitmap，以最小化昂贵的垃圾回收和堆碎片影响；
- 深度的生命周期集成，以确保仅优先处理活跃的Fragment和Activity的请求，并有利于应用在必要时释放资源以避免在后台时被杀掉。

### Glide设置注意事项

- 如果你正在从 URL 加载图片，Glide 可以自动帮助你处理片状网络连接：它可以监听用户的连接状态并在用户重新连接到网络时重启之前失败的请求。如果 Glide 检测到你的应用拥有 `ACCESS_NETWORK_STATE` 权限，Glide 将自动监听连接状态而不需要额外的改动。

- 要从本地文件夹或 DCIM 或图库中加载图片，你将需要添加 `READ_EXTERNAL_STORAGE` 权限：而如果要使用 [`ExternalPreferredCacheDiskCacheFactory`](https://muyangmin.github.io/glide-docs-cn/javadocs/431/com/bumptech/glide/load/engine/cache/ExternalPreferredCacheDiskCacheFactory.html) 来将 Glide 的缓存存储到公有 SD 卡上，你还需要添加 `WRITE_EXTERNAL_STORAGE` 权限：
- 如果你在 Kotlin 编写的类里使用 Glide 注解，你需要引入一个 `kapt` 依赖，以代替常规的 `annotationProcessor` 依赖：

### Glide基本使用

```Kotlin
// 加载
Glide.with(this).load(image_url).into(iv)
// 取消加载,传入的 Activity 或 Fragment 实例销毁时，Glide 也会自动取消加载并回收资源。
Glide.with(this).clear(iv)
```

