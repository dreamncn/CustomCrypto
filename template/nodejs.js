const RequestFromClient = "0";// 日志/Interrupt收到请求（请求包解密）
const RequestToServer = "1";// Repeater/Interrupt发出请求（请求包加密）
const ResponseFromServer = "2";// 日志/Repeater/Interrupt收到响应（响应包解密）
const ResponseToClient = "3";// Repeater/Interrupt发出响应（响应包加密）

//从命令行获取参数
var args = process.argv.slice(2);
if(args.length!==2){
    throw "错误，至少有两个参数！"
}

//数据
var path = args[1];

const fs = require('fs');

//获取
function getPath() {
    return fs.readFileSync(path + '/path.txt').toString();
}
//设置
function setPath(data) {
     fs.writeFileSync(path + '/path.txt',data);
}
function getResponseBody() {
    return fs.readFileSync(path + '/response_body.txt').toString();
}
//设置
function setResponseBody(data) {
    fs.writeFileSync(path + '/response_body.txt',data);
}

if(args[0]===RequestToServer){
    setPath(getPath()+"&sign=12345678")
}

if(args[0]===ResponseFromServer){
    setResponseBody(getResponseBody()+"\r\n\r\n-----------\r\n changed by ankio 2023.")
}


console.log("success")
