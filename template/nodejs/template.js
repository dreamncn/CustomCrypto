//封装简单的base64
var base64=function(){var BASE64_MAPPING=['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'];var URLSAFE_BASE64_MAPPING=['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','-','_'];var _toBinary=function(ascii){var binary=[];while(ascii>0){var b=ascii%2;ascii=Math.floor(ascii/2);binary.push(b)}binary.reverse();return binary};var _toDecimal=function(binary){var dec=0;var p=0;for(var i=binary.length-1;i>=0;--i){var b=binary[i];if(b==1){dec+=Math.pow(2,p)}++p}return dec};var _toUTF8Binary=function(c,binaryArray){var mustLen=(8-(c+1))+((c-1)*6);var fatLen=binaryArray.length;var diff=mustLen-fatLen;while(--diff>=0){binaryArray.unshift(0)}var binary=[];var _c=c;while(--_c>=0){binary.push(1)}binary.push(0);var i=0,len=8-(c+1);for(;i<len;++i){binary.push(binaryArray[i])}for(var j=0;j<c-1;++j){binary.push(1);binary.push(0);var sum=6;while(--sum>=0){binary.push(binaryArray[i++])}}return binary};var _toBinaryArray=function(str){var binaryArray=[];for(var i=0,len=str.length;i<len;++i){var unicode=str.charCodeAt(i);var _tmpBinary=_toBinary(unicode);if(unicode<0x80){var _tmpdiff=8-_tmpBinary.length;while(--_tmpdiff>=0){_tmpBinary.unshift(0)}binaryArray=binaryArray.concat(_tmpBinary)}else if(unicode>=0x80&&unicode<=0x7FF){binaryArray=binaryArray.concat(_toUTF8Binary(2,_tmpBinary))}else if(unicode>=0x800&&unicode<=0xFFFF){binaryArray=binaryArray.concat(_toUTF8Binary(3,_tmpBinary))}else if(unicode>=0x10000&&unicode<=0x1FFFFF){binaryArray=binaryArray.concat(_toUTF8Binary(4,_tmpBinary))}else if(unicode>=0x200000&&unicode<=0x3FFFFFF){binaryArray=binaryArray.concat(_toUTF8Binary(5,_tmpBinary))}else if(unicode>=4000000&&unicode<=0x7FFFFFFF){binaryArray=binaryArray.concat(_toUTF8Binary(6,_tmpBinary))}}return binaryArray};var _toUnicodeStr=function(binaryArray){var unicode;var unicodeBinary=[];var str="";for(var i=0,len=binaryArray.length;i<len;){if(binaryArray[i]==0){unicode=_toDecimal(binaryArray.slice(i,i+8));str+=String.fromCharCode(unicode);i+=8}else{var sum=0;while(i<len){if(binaryArray[i]==1){++sum}else{break}++i}unicodeBinary=unicodeBinary.concat(binaryArray.slice(i+1,i+8-sum));i+=8-sum;while(sum>1){unicodeBinary=unicodeBinary.concat(binaryArray.slice(i+2,i+8));i+=8;--sum}unicode=_toDecimal(unicodeBinary);str+=String.fromCharCode(unicode);unicodeBinary=[]}}return str};var _encode=function(str,url_safe){var base64_Index=[];var binaryArray=_toBinaryArray(str);var dictionary=url_safe?URLSAFE_BASE64_MAPPING:BASE64_MAPPING;var extra_Zero_Count=0;for(var i=0,len=binaryArray.length;i<len;i+=6){var diff=(i+6)-len;if(diff==2){extra_Zero_Count=2}else if(diff==4){extra_Zero_Count=4}var _tmpExtra_Zero_Count=extra_Zero_Count;while(--_tmpExtra_Zero_Count>=0){binaryArray.push(0)}base64_Index.push(_toDecimal(binaryArray.slice(i,i+6)))}var base64='';for(var i=0,len=base64_Index.length;i<len;++i){base64+=dictionary[base64_Index[i]]}for(var i=0,len=extra_Zero_Count/2;i<len;++i){base64+='='}return base64};var _decode=function(_base64Str,url_safe){var _len=_base64Str.length;var extra_Zero_Count=0;var dictionary=url_safe?URLSAFE_BASE64_MAPPING:BASE64_MAPPING;if(_base64Str.charAt(_len-1)=='='){if(_base64Str.charAt(_len-2)=='='){extra_Zero_Count=4;_base64Str=_base64Str.substring(0,_len-2)}else{extra_Zero_Count=2;_base64Str=_base64Str.substring(0,_len-1)}}var binaryArray=[];for(var i=0,len=_base64Str.length;i<len;++i){var c=_base64Str.charAt(i);for(var j=0,size=dictionary.length;j<size;++j){if(c==dictionary[j]){var _tmp=_toBinary(j);var _tmpLen=_tmp.length;if(6-_tmpLen>0){for(var k=6-_tmpLen;k>0;--k){_tmp.unshift(0)}}binaryArray=binaryArray.concat(_tmp);break}}}if(extra_Zero_Count>0){binaryArray=binaryArray.slice(0,binaryArray.length-extra_Zero_Count)}var str=_toUnicodeStr(binaryArray);return str};var __BASE64={encode:function(str){return _encode(str,false)},decode:function(base64Str){return _decode(base64Str,false)},urlsafe_encode:function(str){return _encode(str,true)},urlsafe_decode:function(base64Str){return _decode(base64Str,true)}};return __BASE64};

const REQUEST_RECEIVE = "0";// 日志/Interrupt收到请求（请求包解密）
const REQUEST_SEND = "1";// Repeater/Interrupt发出请求（请求包加密）
const RESPONSE_RECEIVE = "2";// 日志/Repeater/Interrupt收到响应（响应包解密）
const RESPONSE_SEND = "3";// Repeater/Interrupt发出响应（响应包加密）
//从命令行获取参数
var args = process.argv.slice(2);
if(args.length!==2){
    throw "错误，至少有两个参数！"
}


//数据
var data = JSON.parse(base64().decode(args[1]));

//这里的json数据格式如下：可以根据需要自行处理，最后也要输出Json


//
//{
//     "request": {
//         "path": "/Member/Account",
//         "headers": {
//             "Cookie": ".ASPXAUTH=EBE357DDD033378D64C6CC681C90A8C0C0AEC990E72CFBE26AA9F0C2A8920AB3C7BE24EF064D7760912BD09AA20A7ED28184F00F5993C961947AD8DD748ED8FD857163E2D42AFC46A364BF6F952D75C73C94176AFB4D67144545515231FA0EB459D2FF432A218E1C721AD5A854DF37A068641A147658F50F0AC00EAE2FB5625CFDA2FDF3595A4F46FDE2819FCF78E9E5D186CEF7CF9704141F357F6AADB4AACCB7A6482DFEFF69191EF720647C053FB2",
//             "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
//             "Upgrade-Insecure-Requests": "1",
//             "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
//             "Referer": "http://www.abc.com/Member/Message",
//             "Connection": "close",
//             "Host": "www.abc.com",
//             "DNT": "1",
//             "Accept-Encoding": "gzip, deflate",
//             "Accept-Language": "zh,zh-CN;q=0.9,en;q=0.8,zh-TW;q=0.7,ru;q=0.6"
//         },
//         "method": "GET",
//         "http_version": "HTTP/1.1",
//         "body": "\n\nxxxxxx"
//     },
//     "response": {
//         "headers": {
//             "Server": "Microsoft-IIS/8.5",
//             "Access-Control-Allow-Origin": "*",
//             "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
//             "Connection": "close",
//             "Access-Control-Allow-Headers": "Content-Type",
//             "Date": "Thu, 22 Sep 2022 05:58:18 GMT",
//             "X-AspNetMvc-Version": "5.2",
//             "Cache-Control": "private, s-maxage=0",
//             "X-AspNet-Version": "4.0.30319",
//             "Vary": "Accept-Encoding",
//             "Content-Length": "18446",
//             "Content-Type": "text/html; charset=utf-8",
//             "X-Powered-By": "ASP.NET"
//         },
//         "state_msg": "OK",
//         "http_version": "HTTP/1.1",
//         "state": "200",
//         "body": "xxxx"
//     }
// }


//日志/Interrupt收到请求（请求包解密）
if(args[0] === REQUEST_RECEIVE){
    console.log(JSON.stringify(data))
   //Repeater/Interrupt发出请求（请求包加密）
}else if(args[0] === REQUEST_SEND){
    console.log(JSON.stringify(data))
    //日志/Repeater/Interrupt收到响应（响应包解密）
}else if(args[0] === RESPONSE_RECEIVE){
    console.log(JSON.stringify(data))
}else{
    // Repeater/Interrupt发出响应（响应包加密）
    console.log(JSON.stringify(data))
}

