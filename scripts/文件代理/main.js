/**
 * 文件代理，目的是将服务端文件替换成本地文件
 * @type {string}
 */
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
const mime = require('mime');
if(args[2]===ResponseToClient){
    //替换body

    var file  = "./replace/"+fs.readFileSync(path[1]+"/path.txt");
    if(fs.existsSync(file)){
        fs.writeFileSync(path[1]+"/response_body.txt",fs.readFileSync(file));
        //Content-type
        var header = fs.readFileSync(path[1]+"/response_header.txt");
        //替换headers
        fs.writeFileSync(path[1]+"/response_header.txt",header.replace('/content-type:.*?/i',mime.getType(file)));
    }

}
console.log("success");
