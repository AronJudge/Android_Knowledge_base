JVM  C++ 写的

C++ 解释器: // 需要一次翻译
if ("new") {
    C++ 代码完成JAVA new
}

方法 次数达到一定次数后
JIT执行
java - 机器码(汇编码, 编译期比较长)

JVM 运行时数据区

hotSport

线程共享
方法区
        字段信息  方法信息 类变量  方法表  类型的信息  指向class实例的引用  指向类加载的引用 Class的常量池  运行时常量池
堆

线程私有:
虚拟机栈(方法栈帧, 方法执行完了, 方法栈帧出栈)
        局部变量表
        操作数栈
        动态连接
        方法出口
本地方法栈
程序计数器

堆外内存  直接内存

-Xss 设置线程堆栈大小 Linux/64 1024k默认
-Xss 1m  不可以动态调整
有大小限制,死递归 会爆掉

内存比较吃紧 -> 虚拟机栈使用

500 个线程 - 500M  我们可以缩栈 250K -- 100M ? 单线程一般用不了多大

private int work() {
        int x = 1;
        int y = 2;
        int z = (x+y) * 10;
        return z;
}

javap 反汇编
work 栈帧
0 iconst_1 int 常量 压入操作数栈  (const -1 -- 5)
1 istore_1 存储命令 存储到局部变量表下标1的位置 0 默认存this
2 iconst_2
3 istore_2
4 iload_1 压入操作数栈
5 iload_2 压入操作数栈
6 iadd    针对操作数栈的加法 三部
7 bipush  10 常量压入操作数栈
8 imul
9 istore_3
10 iload_3
11 ireturn

程序计数器 只记录本方法栈帧的程序行号  偏移量     正在执行的地址
完成出口  记录跳入方法的方法地址  方法执行完后 可以通过上次的记录 从上次调入的方法后面开始执行
动态连接  多态有关

本地方法栈 在hotstop 合二为一了 不区分本地方法栈 和 虚拟机栈, 一致的
java不能直接操作线程, 操作系统 特殊的库 提供的一些接口

public class ObjectAndClass { // class 放方法区

    static { // 类加载的时候会执行

    }
    static int age = 18;  // 方法区
    final static int sex = 1; // 方法区
    ObjectAndClass objectAndClass = new ObjectAndClass(); // 类加载的时候不会执行   对象2
    private boolean isKing; // 也不会 成员对象跟随对象

    public static void main(String[] args) { // 静态方法 局部变量表 0 不是this(object)
        int x = 18; // 局部变量 放在虚拟机栈 栈帧 稳定存贮 局部变量表
        long y = 1; // 局部变量 放在虚拟机栈 栈帧 稳定存贮 局部变量表
        ObjectAndClass objectAndClass = new ObjectAndClass(); // 堆中分配  引用在虚拟机栈的局部变量表 对象1(object 再初始化对象 2)
        objectAndClass.isKing = true; // 跟随对象 堆空间
        objectAndClass.hashCode(); // 本地方法栈
        ByteBuffer bb = ByteBuffer.allocateDirect(128*1024*1024);// 128M 直接内存 unsafe 操作内存 玩弄对象 底层操作 内存屏障
        // 直接内存绕过垃圾回收 速度稍微快一点 有可能方剂回收
    }


    内存屏障扩展
    https://zhuanlan.zhihu.com/p/212268670

代码运行的时候
jps打印JVM参数
jinfo -flags 10004 打印JVM参数 JDK 根据操作系统进行配置

1 jvm 申请内存
2 初始化运行时数据区 (方法区)
3 类加载 类 常量 放进方法区
4 执行方法  创建虚拟机栈  创建方法栈帧
5 创建对象  在堆中创建对象  1 生 2 2 生 3 3 生 世界

垃圾回收

堆空间 划分两个空间

        新生代 和 老年代

JHSDB 工具是可以去 查看内存的工具

深入理解什么时运行时数据区
真实内存虚拟化

jdk1.8.0/lib> java -cp .\sa-jdi.jar sun. jvm. hotsport.HSDB

10004 JVMObject  -- JVM进程
Attach进程 你会发现 有多个线程 jvm

monitor Ctrl-Break
Attach Listener
Singal Dispatcher
Surrogate Locker Thread(Concurent GC)
Fianlizer
Reference Handler
Main

gen 0 新生代
    0x00000013000  - 0x 0000000015200000
    细分 : Eden 0x 000 1300 - 13C From  13C --- 13D   To 13D -- 13 E
gen 1 老年代  13 E - 152










设置对象头
    对象头 1 存储对象自身的运行时数据区 Mark Work 包括 哈希码  GC分代年龄  锁状态标识  线程持有的锁  偏向线程ID  偏向时间戳
          2 类型指针  指向方法区里面的类变量
          3 若为对象数组, 记录数组长度的数据
    实例数据
    对齐填充   8 字节倍数
