# SearchHosts

[toc]

#### 介绍

更加便捷地更改hosts 文件以访问类似于 "[Github](https://github.com)"、"[Python 官网](https://www.python.org)"
这种比较难以访问的网站。

可以输入网址获取单个IP，也可以更新整个hosts 文件。

目前暂不支持IPv6(主要是还没看懂格式的区分)

目前暂不支持Windows 以外的系统(主要是没看清楚文件的区别在哪)

感谢：

- [站长之家](https://tool.chinaz.com)
- [站长之家(手机版)](https://mtool.chinaz.com)
- [IP138](https://site.ip138.com)
- [ToolFK](https://www.toolfk.com)

提供的搜索支持——尽管他们不知道我在这个程序里用了他们的网站。

如果想加速游戏软件，为什么不试试`UsbEAm Hosts Editor`呢？他们比我们专业的多，功能和界面也友好的多，速度更是快到不知道哪里去了。

但请注意，使用本软件更新hosts后，`UsbEAm Hosts Editor`将检测不到其曾经写入的内容——它的注释我们删掉了

#### 软件架构

`search-common`模块中简单定义了一个IoC 容器作为DI 的实现，以期替换掉较重的`spring-context`，同时利用SPI
机制对操作的定义与实现进行了解耦。

所有的数据交互都通过消息队列`disruptor`进行，队列中会有几种类型的消息，它们的subject
不同，在实现视图层的时候请注意对其进行分辨；

目前的消息种类有

- `cmd`(操作指令，一般是请求操作类型)
- `config`(设置，包括各种默认设置参数等)
- `log`(用于记录一些日志，这些日志也会存在日志文件中)
- `message`(一般会用来记录执行的情况，哪一些成功，哪一些失败)
- `failed`(用于在执行完一次之后，将所有失败的条目显示出来)
- `oths`(其他一些信息，一般是系统相关的信息)

`search-core`模块是真实执行搜索的模块，用HTTPClient 以及JSoup 进行爬取

`search-ui-*`模块是视图层的定义，如果想新增视图设计，请引用`search-common`模块并实现` `类，操作链接使用`IOpeartor`
接口。由于使用的是无副作用的静态方法进行的搜索，操作是无状态且没有线程安全问题的。

- `swing`：利用FlatF 进行界面美化（<s>在这个角度说，我们有着业界Java 原生UI
  中最为先进的主题风格——比肩JetBrains IDEA 的界面</s>）

同时，有`micrometer`作为埋点工具，用于记录指标

- `http.request.time.mills`: 请求一次的时长(ms)
- `machine.data.cpu`: CPU 使用情况
- `machine.data.memory`: 内存使用情况
- `machine.data.jvm.m1`: JVM 相关数据

`H2`数据库会记录一些历史数据，可以配置这些数据的过期时间；也可以在这里找到历史数据以还原历史Hosts

反应式编程初期使用`Reactor`，之后换成`Flow` 实现

#### 安装教程

<s>首先电脑上必须要有JRE 环境——Java 8做exe 文件挺麻烦的，或者是我们不会做</s>

启动时请使用`run.bat`文件，目前仍不支持Linux

请注意`Hosts.jar`文件同级目录下最好要有`rules.json`文件，否则只会使用内置的`站长之家PC 版`进行搜索，而且必须要有`lib`
目录——那里面是本软件用到的依赖，显而易见的，我们把依赖包放在了外面以减少体积。

[//]: # (使用[jpackage 打包]&#40;https://blog.csdn.net/cheng_fu/article/details/120446710&#41;)

[//]: # (拉一点编译优化)

#### 使用说明

新增规则请在rules.json 或者rules.yml 文件中增加，需要的信息有：

- 网站名称
- get 提交方式的网址（用"`${website}`"代替要查询的网站）
- 可被识别的CSS选择器
- 去除多余字符的正则式（非必需）

一个例子如下：

``` json
{
    "name": "站长之家PC 版",
    "url": "http://tool.chinaz.com/dns/?type=1&host=${website}&ip=",
    "cssQuery": "div.w60-0.tl",
    "replaceRegex": "(\\[(.+?)]|-)"
}
```

```yaml
-   name: 站长之家PC 版
    url: 'http://tool.chinaz.com/dns/?type=1&host=${website}&ip='
    cssQuery: div.w60-0.tl
    replaceRegex: '(\[(.+?)]|-)'
```

或者

``` json
{
    "name": "IP138",
    "url": "https://site.ip138.com/${website}",
    "cssQuery": "div#curadress > p > a"
}
```

```yaml
-   name: IP138
    url: 'https://site.ip138.com/${website}'
    cssQuery: 'div#curadress > p > a'
```
