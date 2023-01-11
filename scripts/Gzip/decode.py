import gzip
RequestFromClient = "0"  # 日志/Interrupt收到请求（请求包解密）
RequestToServer = "1"  # Repeater/Interrupt发出请求（请求包加密）
ResponseFromServer = "2"  # 日志/Repeater/Interrupt收到响应（响应包解密）
ResponseToClient = "3"  # Repeater/Interrupt发出响应（响应包加密）

def decode(data):
    return gzip.decompress(data).decode('utf8')

if len(sys.argv) != 3:
    raise RuntimeError('错误，至少需要两个参数')
# 这里就不管请求类型了
isRequest = sys.argv[1] == RequestFromClient or sys.argv[1] == RequestToServer
# 构建文件名
file = sys.argv[2] + "/" + 'body.txt' if isRequest else 'response_body.txt'

with open(file,'r',encoding="utf-8") as f:
    data  = f.read()
with open(file, 'w', encoding='utf-8') as fe:
    fe.write(decode(data))