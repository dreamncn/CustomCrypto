/**
 * 此处以Frida导出函数为例
 */
if(Java.available) {
    Java.perform(function(){
        var cryptUtil=Java.use("xxx.DesUtil"); //获取 cryptUtil 加解密类
        var key = "123456";//加密密钥
        rpc.exports = {
            encrypt: function(plaintext)
            {
                //导出加密函数
                return cryptUtil.encrypt(plaintext,key)
            },
            decrypt: function(plaintext)
            {
                //导出解密函数
                return cryptUtil.decrypt(plaintext,key)
            }
        }
    });
}

