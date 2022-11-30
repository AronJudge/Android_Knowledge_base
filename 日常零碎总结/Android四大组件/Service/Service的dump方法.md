Android Service dump使用

dumpsys是Android用来查看后台Service状态的工具，在我们分析调试问题时，非常好用，避免每次修改都要加log然后编译。

可以借助这个命令列出当前有哪些service可以进行dump分析

```shell
# dumpsys | grep "DUMP OF SERVICE"
DUMP OF SERVICE SurfaceFlinger:
DUMP OF SERVICE accessibility:
DUMP OF SERVICE account:
DUMP OF SERVICE activity:

# dumpsys activity s <package>  // 查看app的所有service状态
# dumpsys activity b <package>  // 查看app的所有广播状态
# dumpsys activity top          // 查看app的界面状态
# dumpsys activity oom          // 查看app的oom信息
```

当然要这么使用是有前提的, 得先在ServiceManager中addService后才可以进行dump。然而ServiceManager是@hide的, 基于sdk开发的app找不到ServiceManager类。

开发Android对Service类肯定不陌生，但Service类自带dump方法可能都没用到过（我也是最近才用到）, 
App只需要继承Service后重写dump方法就可以进行dumpsys打印了, 完美解决了调用不到ServiceManager的烦恼。

```java
public class TestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("Test dump");
    }
}

```