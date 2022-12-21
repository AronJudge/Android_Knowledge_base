# 2021.10.28

- 理解android多用户
- 2021.10.28_TS-BUG-84592_VR_bug

英语单词：

```xml
tearDown   n:拆卸
Performance n: 表现 性能 

countDownLatch : 倒计时锁存器
Task Reparenting :  任务栈再分配
Foundation ： 地基 
overlay  ： 覆盖
```

测试apk

adb shell am instrument -w com.ts.vradpter.pts/android.support.test.runner.AndroidJUnitRunner

## Android 系统多用户

Android 4.0开始：UserManager 诞生  但没有 对应的binder服务

1. Uid（用户Id）：在Linux上，一个用户Uid标识着一个给定的用户。Android上也沿用了Linux用户的概念，Root用户Uid为0，System Uid为1000，并且，每个应用程序在安装时也被赋予了单独的Uid，这个Uid将伴随着应用从安装到卸载。
2. Gid（用户组Id）：Linux上规定每个应用都应该有一个用户组，对于Android应用程序来说，每个应用的所属**用户组与Uid相同**。
3. Gids：应用在安装后所获得权限的Id集合。在Android上，每个权限都可能对应一个或多个group，每个group有个gid name，gids就是通过对每个gid name计算得出的id集合，一个UID可以关联GIDS，表明该UID拥有多种权限。

### 多用户特征

Android在创建每个用户时，都会分配一个整型的userId。对于主用户（正常下的默认用户）来说，userId为0，之后创建的userId将从10开始计算，每增加一个userId加1：

```adb
root@virgo:/ # pm list users
Users:
        UserInfo{0:机主:13} running
        UserInfo{10:security space:11} running
```

创建一个名为"ulangch"的用户：

```
root@virgo:/ # pm create-user "ulangch"
Success: created user id 11

root@virgo:/ # pm list users
Users:
        UserInfo{0:机主:13} running
        UserInfo{10:security space:11} running
        UserInfo{11:ulangch:10} running
```

启动和切换到该用户：

```
root@virgo:/ # am start-user 11
Success: user started
root@virgo:/ # am switch-user 11
```

![img](https://img-blog.csdnimg.cn/20190704212535608.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE0OTc4MTEz)

![img](https://img-blog.csdnimg.cn/20190704212544138.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE0OTc4MTEz)

3.3 独立的权限控制
不同用户具有的权限不同，如：访客用户的默认权限限制就有：

```
perseus:/ $ dumpsys user
...
  Guest restrictions:
    no_sms                        // 限制发送短信
    no_install_unknown_sources    // 限制安装
    no_config_wifi                // 限制配置WiFi
    no_outgoing_calls             // 限制拨打电话

```

（注：使用 “adb shell dumpsys user” 可以查看所有的用户信息，如userId、name、restrictions等）

这些权限可以在创建用户时规定，也可以后期由系统动态设置。

不同用户下App的应用权限是独立的

前面说到，uid与userId存在一种计算关系（uid = userId * 1000000 + appId），而在系统中对于权限控制也是根据uid和对应的userId来判定的，因此不同用户下相同应用可以具有不同的权限。

### 多用户安装

虽然前面说到，App的文件存储和数据目录在不同用户下都是独立的，但是对于App的安装，多个用户下同一个App却保持着同一个安装目录，即：

1. 普通三方app：/data/app/
2. 普通系统应用：/system/app/
3. 特权系统应用：/system/priv-app/

```
root@virgo:/ # ls /data/app/com.ulangch.multiuser-1/
base.apk
lib
oat
```



normal：普通权限，在AndroidManifest.xml中声明就可以获取的权限，如INTERNET权限
dangerous：敏感权限，需要动态申请告知用户才能获取
signature|privileged：具有系统签名的系统应用才可以获取的权限，对应上方的 “/system/priv-app”
因此，多用户下的应用其实只安装一次，不同用户下同一个应用的版本和签名都应该相同，不同用户下相同App能够独立运行是因为系统为他们创造了不同的运行环境和权限。



### 3.5 kernel及系统进程的不变性



在不同用户下，虽然能够看到不同的桌面，不同的运行环境，一切都感觉是新的，但是我们系统本身并没有发生改变，kernel进程、system_server进程以及所有daemon进程依然是同一个，并不会重启。

而如果我们在不同用户中开启相同的app，我们可以看到可以有多个app进程，而他们的父进程都是同一个，即 zygote：

```
root@virgo:/ # ps |grep multiuser
u11_a110  9805  357   788188 54628 sys_epoll_ b6d2c99c S com.ulangch.multiuser
u10_a110  13335 357   816516 54588 sys_epoll_ b6d2c99c S com.ulangch.multiuser
u0_a110   13746 357   788448 54056 sys_epoll_ b6d2c99c S com.ulangch.multiuser

root@virgo:/ # ps |grep 357
root      357   1     1542716 65560 poll_sched b6d2cb64 S zygote

```

#### 多用户的创建





CountDownLatch 两种解决场景

先来看看 CountDownLatch 的源码注释；

描述如下：它是一个同步工具类，允许一个或多个线程一直等待，直到其他线程运行完成后再执行。

```java
/**
 * Audio.md synchronization aid that allows one or more threads to wait until
 * a set of operations being performed in other threads completes.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class CountDownLatch {
}
```



通过描述，可以清晰的看出，CountDownLatch的两种使用场景：

- 场景1：让多个线程等待
- 场景2：和让单个线程等待。



### **场景1 让多个线程等待：模拟并发，让并发线程一起执行**

为了模拟高并发，让一组线程在指定时刻(秒杀时间)执行抢购，这些线程在准备就绪后，进行等待(CountDownLatch.await())，直到秒杀时刻的到来，然后一拥而上；
这也是本地测试接口并发的一个简易实现。

在这个场景中，CountDownLatch充当的是一个`发令枪`的角色；
就像田径赛跑时，运动员会在起跑线做准备动作，等到发令枪一声响，运动员就会奋力奔跑。和上面的秒杀场景类似，代码实现如下：

```
CountDownLatch countDownLatch = new CountDownLatch(1);
```







































=========================





