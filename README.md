
<p align="center">
<img src="https://socialify.git.ci/dreamncn/CustomCrypto/image?description=1&descriptionEditable=%F0%9F%9A%80%20%20Burp%E8%87%AA%E5%AE%9A%E4%B9%89%E5%8A%A0%E8%A7%A3%E5%AF%86%E6%8F%92%E4%BB%B6&font=Source%20Code%20Pro&forks=1&issues=1&language=1&name=1&pattern=Overlapping%20Hexagons&pulls=1&stargazers=1&theme=Light">
</p>


## 项目简介

做一些app测试经常会遇到加密、签名的问题，这个插件可以帮助你进行重新签名、数据包解密、偷天换日...


## 使用

- 启用插件
- [编写你的脚本](#脚本编写指南)
- 在`Proxy`的`History`中，可以看到被修改后的请求，Repeater部分也可以自动进行修改
  ![image-20220926231527312](https://pic.dreamn.cn/uPic/2022_09_26_23_15_28_1664205328_1664205328301_RN4xqs.png)

### 自动加解密配置
![img.png](img.png)

### 手动加解密
> 本来这里想用`createMessageEditor`实现的，但是测试过程中发现在渲染`MessageEditor`的过程中也会渲染`Select extension`导致出现递归异常。

![img_1.png](img_1.png)
## 脚本编写指南

插件调用脚本为：

```shell
执行命令 请求类型 临时文件夹
```

其中，第一个参数为 `请求类型`，一共有四种类型：

```js
const RequestFromClient = "0";// 日志/Interrupt收到请求（请求包解密）
const RequestToServer = "1";// Repeater/Interrupt发出请求（请求包加密）
const ResponseFromServer = "2";// 日志/Repeater/Interrupt收到响应（响应包解密）
const ResponseToClient = "3";// Repeater/Interrupt发出响应（响应包加密
```

可以根据burp的生命周期来理解这四种类型：

![7338E56B-BB55-4C6B-B1B6-BD486C4BCEA4](https://pic.dreamn.cn/uPic/2022_09_26_22_57_11_1664204231_1664204231936_3uIT8e.png)

第二个参数为临时文件夹，数据如下：
![img_2.png](img_2.png)

脚本在收到请求后，去修改对应临时文件夹的数据，处理成功，必须输出`success`字样

### 文件释义

| 名称                 | 解释                     | 举例                  | 在哪种请求下存在 |
| :------------------- | ------------------------ | --------------------- | ---------------- |
| body.txt             | 请求包的body部分         | id=1                  | Request/Response |
| headers.txt          | 请求包的headers部分      | Host: 127.0.0.1 等    | Request/Response |
| method.txt           | 请求包的请求方法         | GET                   | Request/Response |
| path.txt             | 请求包的请求路径         | /index.php            | Request/Response |
| version.txt          | 请求包使用的Http协议版本 | HTTP/2                | Request/Response |
| response_body.txt    | 响应包的body部分         | {"body":"sssss"}      | Response         |
| response_headers.txt | 响应包的headers部分      | Set-cookle: www=12333 | Response         |
| response_version.txt | 响应包的Http协议版本     | HTTP/2                | Response         |
| state.txt         | 响应包的响应代码         | 404                   | Response         |
| state_msg.txt    | 响应包的响应消息         | Not Found             |Response|

## 脚本调试指南

- 多关注`Extender`中`CustomCrypto`的`output`内容，如果脚本生效，则会在这里输出处理的请求包响应包等。
- 如果你发现脚本未生效，也可以复制`CustomCrypto`输出的命令内容，直接在命令行进行测试，以便于调试脚本。

## 案例&模板

- [Gzip](./scripts/Gzip)
- [加解密代理](./scripts/加解密代理)
- [文件代理](./scripts/文件代理)
- [直接进行加解密](./scripts/直接进行加解密)
- [签名](./scripts/签名)

## 协议

MIT

