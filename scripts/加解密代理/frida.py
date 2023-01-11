import frida
from urllib import parse
from wsgiref.simple_server import make_server

# 使用usb设备
a = frida.get_usb_device(1)
# 附加指定进程
session = a.attach("xxx")
# 加载js脚本
f = open("./proxy.js", "r", encoding="utf-8")
script = session.create_script(f.read())
f.close()


# 映射加解密函数
def encode(message):
    return script.exports.encrypt(message)


def decode(message):
    return script.exports.decrypt(message)


# 脚本执行
script.load()


# Web服务器入口
def app(env, start_response):
    start_response("200 ok", [("Content-Type", "text/plain")])
    return [cryptController(env['wsgi.input'].read())]  # 获取post请求


# Web服务器处理函数
def cryptController(requestParameter):
    par = {}
    ParameterList = requestParameter.split("&")
    for parameter in ParameterList:
        par[parameter.split("=")[0]] = parameter.split("=")[1]
    if 'type' in par.keys() and 'file' in par.keys():
        with open(par['file'], 'r', encoding='utf-8') as fr:
            data = fr.read()  # 读取数据
        if par['type'] == 'encrypt':  # 处理加密
            with open(par['file'], 'w', encoding='utf-8') as fe:
                fe.write(encode(data))
        else:  # 处理解密
            with open(par['file'], 'w', encoding='utf-8') as fd:
                fd.write(decode(data))
    return b'success'


# 启动Web服务器
sever = make_server("", 5555, app)
sever.serve_forever()
