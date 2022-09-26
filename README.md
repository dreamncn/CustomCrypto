
<p align="center">
<img src="https://socialify.git.ci/dreamncn/CustomCrypto/image?description=1&descriptionEditable=%F0%9F%9A%80%20%20Burp%E8%87%AA%E5%AE%9A%E4%B9%89%E5%8A%A0%E8%A7%A3%E5%AF%86%E6%8F%92%E4%BB%B6&font=Source%20Code%20Pro&forks=1&issues=1&language=1&name=1&pattern=Overlapping%20Hexagons&pulls=1&stargazers=1&theme=Light">
</p>


## 项目简介

做一些app测试经常会遇到加密、签名的问题，这个插件可以帮助你进行重新签名，数据包解密。


## 使用

- 启用插件
- [编写你的脚本](#脚本编写指南)
- 配置监控，如下：

![image-20220926224251101](https://pic.dreamn.cn/uPic/2022_09_26_22_42_51_1664203371_1664203371837_ljBFc1.png)

- 在`Proxy`的`History`中，可以看到被修改后的请求

  ![image-20220926231527312](https://pic.dreamn.cn/uPic/2022_09_26_23_15_28_1664205328_1664205328301_RN4xqs.png)

## 脚本编写指南

插件调用脚本为：

```shell
执行命令 请求类型 base64编码内容
```

其中，第一个参数为 `请求类型`，一共有四种类型：

```java
const REQUEST_RECEIVE = "0"; 日志/Interrupt收到请求（请求包解密）
const REQUEST_SEND = "1"; Repeater/Interrupt发出请求（请求包加密）
const RESPONSE_RECEIVE = "2"; 日志/Repeater/Interrupt收到响应（响应包解密）
const RESPONSE_SEND = "3"; Repeater/Interrupt发出响应（响应包加密）
```

可以根据burp的生命周期来理解这四种类型：

![7338E56B-BB55-4C6B-B1B6-BD486C4BCEA4](https://pic.dreamn.cn/uPic/2022_09_26_22_57_11_1664204231_1664204231936_3uIT8e.png)

第二个参数为字符串类型的`JSON`数据，格式如下：

```json
{
     "request": {  											//请求内容
         "path": "/",										//请求路径，如果有参数，参数部分也会包含在内
         "headers": { 									//请求头
         
         },
         "method": "GET",								// http协议请求类型
         "http_version": "HTTP/1.1",		// 使用的http协议版本
         "body": ""											// 请求的body部分
     },
     "response": {											//响应内容
         "headers": {										//响应头
           
         },
         "state_msg": "OK",							//响应状态信息
         "http_version": "HTTP/1.1",		//响应的http协议版本
         "state": "200",								//响应的状态码
         "body": ""											//响应的body部分
     }
 }
```

插件所需的输出内容：

使用对应编程语言的`print`、`echo`、`system.out.println`等，将上述`Json`数据直接输出到输出缓冲区即可。

## 脚本样例与实用脚本

参考[Template](https://github.com/dreamncn/CustomCrypto/tree/master/template)文件夹内容，其中不同命名目录表示对不同编程语言的脚本样例，每一个文件夹中的`example文件`或者`example文件夹`表示的是空的脚本样例，其他的则是实用脚本。

### NodeJs

- 样本

### Java

- 样例
- 某app签名自动更新
- 自动进行Gzip压缩解压

## 脚本调试指南

- 多关注`Extender`中`CustomCrypto`的`output`内容，如果脚本生效，则会在这里输出处理的请求包响应包等。
- 如果你发现脚本未生效，也可以复制`CustomCrypto`输出的命令内容，直接在命令行进行测试，以便于调试脚本。

## 已知问题

- 超长字符请求包或者返回包可能导致执行失败。

## 协议

MIT

