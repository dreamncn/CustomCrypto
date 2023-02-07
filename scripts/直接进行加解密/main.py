#!/usr/bin/python
# -*- coding: UTF-8 -*-
# use python 3
import sys
import requests
RequestFromClient = "0"  # 日志/Interrupt收到请求（请求包解密）
RequestToServer = "1"  # Repeater/Interrupt发出请求（请求包加密）
ResponseFromServer = "2"  # 日志/Repeater/Interrupt收到响应（响应包解密）
ResponseToClient = "3"  # Repeater/Interrupt发出响应（响应包加密）

if len(sys.argv) != 3:
    raise RuntimeError('错误，至少需要两个参数')
# 是否加密
isEncrypt = (sys.argv[1] == RequestToServer or sys.argv[1] == ResponseToClient)

isRequest = sys.argv[1] == RequestFromClient or sys.argv[1] == RequestToServer

# 构建文件名
file = sys.argv[2] + "/" + 'body.txt' if isRequest else 'response_body.txt'

# 此处假设是body部分

def encrypt(str):
    return '加密：'+str

def decrypt(str):
    return '解密：'+str


with open(file,'r',encoding='utf-8') as f:
    data = f.read()

if isEncrypt :
    data = encrypt(data)
else:
    data = decrypt(data)


with open(file,'w',encoding='utf-8') as f:
    f.write()
print("success")