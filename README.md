客户端完成的功能有：

1.蓝牙接收数据，接收的数据格式为，数据头为字符'$'，1字节,CANID为4字节，数据为8字节，时间戳为6字节，数据尾为字符'#'，1字节，总共20字节

2只有当数据的首尾校验和长度校验均通过，才会被接收并存入本地数据库

3.可以选择连接远程数据库将数据上传到远程数据库中
