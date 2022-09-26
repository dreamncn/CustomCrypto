
<p align="center">
<img src="https://socialify.git.ci/dreamncn/CustomCrypto/image?description=1&descriptionEditable=%F0%9F%9A%80%20%20Burp%E8%87%AA%E5%AE%9A%E4%B9%89%E5%8A%A0%E8%A7%A3%E5%AF%86%E6%8F%92%E4%BB%B6&font=Source%20Code%20Pro&forks=1&issues=1&language=1&name=1&pattern=Overlapping%20Hexagons&pulls=1&stargazers=1&theme=Light">
</p>




## 项目简介

做一些App测试、Web测试、小程序测试的时候，通常会遇到流量加密的问题，对于流量加密自然是要去解密的。

通常办法是提取对应的加解密部分，然后手动复制加解密字段进行加解密。

或者是Hook加解密函数，在调用的时候进行加解密。

> 上面无论哪种方法都比较麻烦，前者需要不断复制加密字段去解密，后者没法复用，需要重新调用才能执行方法。

于是乎，`CustomCrypto`诞生了。这是一个强大的加解密代理工具，可以将Burp流量代理到任意位置进行加解密。

## 原理



## 使用

- 

## 设置


## 已知问题

- 超长字符请求包或者返回包可能导致执行失败。

### Settings

插件安装后，只有两个要填写的地方，`需要监控的网址`以及`需要执行的命令`。



`需要监控的网址`：指的是需要去做加解密的网址。比如只有`http://localhost/login`需要加解密，就只填写`http://localhost/login`,如果整站都需要进行加解密，你就填写`http://localhost/login`，必须要带上Http头。



`需要执行的命令`：这个功能赋予了这个插件无限的可能。你可以用任何你喜欢的语言去编写加解密脚本，只要它可以在命令行调用。调用方式很简单，以nodejs为例，只填写这一部分就好了`node /your/path/crypto.js `，如果是java的话，得先编译，也可以运行时编译（不过那样响应速度就太慢了）



记得点`保存`，配置才会生效，本插件支持多个站点不同加解密方案调用。

### Scripts

> 这里以PHP为例（PHP天下第一）

系统的调用脚本为：

```shell
php crypto.php 2 abcdefjhijklmnopq....
```

其中，第一个参数 `2` 指的是请求类型，我在Burp中定义了四种请求类型：

```java
const REQUEST_RECEIVE = "0";// burp收到请求,就是显示在log列表里面或者显示在burp页面的时候
const REQUEST_SEND = "1";// burp发出请求,就是burp收到后又发出去的请求
const RESPONSE_RECEIVE = "2";// burp收到响应，就是显示在log列表里面或者显示在burp页面的时候
const RESPONSE_SEND = "3";// burp发出响应，就是返回给客户端的时候
```

第二个参数，指的是需要加解密的body部分，这个字段采用` url编码(base64编码(body))`进行传递，所以进行加解密前得先进行解码。目前是不支持get参数解密的，想来也没人用GET传递加密数据吧。

> 注意：
>
> 如果你在`REQUEST_RECEIVE`或者`RESPONSE_RECEIVE`阶段进行了加解密操作
>
> 那么你在`REQUEST_SEND`或者`RESPONSE_SEND`阶段收到的body就是你上个阶段修改后的值
>
> 简单来说，就是`REQUEST_RECEIVE`、`RESPONSE_RECEIVE`阶段如果做了解密，那么`REQUEST_SEND`、`RESPONSE_SEND`阶段就必须进行加密，这样服务器/客户端才能收到正确的响应。



Burp流量的生命周期如下：

![image-20220417094346847](https://cdn.jsdelivr.net/gh/dreamncn/picBed@master/uPic/2022_04_17_09_43_47_1650159827_1650159827204_dn9QpZ.png)



测试的PHP脚本：

```php 
<?php
const REQUEST_RECEIVE = "0";// 收到请求
const REQUEST_SEND = "1";// 发出请求
const RESPONSE_RECEIVE = "2";// 收到响应
const RESPONSE_SEND = "3";// 发出响应

if(sizeof($argv)!==3){
    throw new Exception("错误，至少需要两个参数！");
}



// 这是判断加解密阶段
//是否加密，只有发出阶段进行加密操作
$isEncrypton = ($argv[1]==REQUEST_SEND||$argv[1]==RESPONSE_SEND);



//数据
$data = base64_decode(urldecode($argv[2]));


//加密函数
function encode($d){
    //TODO 换成你自己的加解密函数
    return base64_encode($d);
}
function decode($d){
    //TODO 换成你自己的加解密函数
    return base64_decode($d);
}


if($isEncrypton){
    echo encode($data);
}else{
    echo decode($data);
}
```



## :chestnut: Examples

### :one: Web前端流量加解密

- 提取前端流量加解密脚本为 crypto.js，需要加解密的网址为：`http://example.com/login`

- 套入CustomCrypto脚本模板

- 需要监控的网址处填写：`http://example.com/login`

- 需要执行的命令处填写：`node crypto.js`

- 保存

- 返回网页重新发包即可

- 这边的脚本对返回页面做了base64编码

- ![image-20220417094748044](https://cdn.jsdelivr.net/gh/dreamncn/picBed@master/uPic/2022_04_17_09_47_48_1650160068_1650160068583_euW30Q.png)

### :two:App流量加解密 （对应的脚本暂未提供）

- 如果是APP自己搞的比较复杂的加解密建议使用Frida或者Xposed植入代码，在App起一个Http服务，这个服务用于主动调用App中的加解密。
- 如果采用公开算法如RSA、SM2之类的，建议直接在本地实现对应代码，或者直接拷贝他的加解密类到本地java文件中来。

- 套入CustomCrypto脚本模板（python/php/java随意）

- 需要监控的网址处填写：`http://example.com/login`
- 需要执行的命令处填写：`php crypto.php`
- 保存
- 返回App重新发包即可

## Defect

缺点也是有的，因为多了四步加解密流程，所以Web页面响应速度会慢几秒钟。

  
