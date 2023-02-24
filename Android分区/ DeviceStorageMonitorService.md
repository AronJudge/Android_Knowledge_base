什么是DeviceStorageMonitorService

Service that monitors and maintains free space on storage volumes.

As the free space on a volume nears the threshold defined by this service 

will clear out cached data to keep the disk from entering this low state

DeviceStorageMonitorService 实现一个服务来监视设备上的磁盘存储空间量。

如果设备上的可用存储小于可调阈值（安全设置参数；默认值为10%），则会显示内存不足通知以提醒用户。

如果用户单击低内存通知，则会启动应用程序管理器应用程序，让用户释放存储空间。

当存储空间不足时，android系统会发送一条存储空间不足的广播，同时通知栏会弹出消息，

普通应用可以捕获此广播，从而得知系统处于低存储的状态。


监视和维护存储卷上可用空间的服务。当卷上的可用空间接近此服务定义的阈值时，将清除缓存数据以防止磁盘进入此低状态