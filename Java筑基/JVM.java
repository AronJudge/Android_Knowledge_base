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
方法区 运行时常量池
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


    java类加载过程  https://www.jianshu.com/p/dd39654231e0
            1 加载
                将class字节码文件加载到内存中 并将这些数据转换成方法区中的运行时数据 静态变量 静态代码块 常量池等
                在堆中生成一个Class类对象代表这个类 反射原理 作为方法区类数据的访问入口
            2 连接    将Java类的二进制代码合并到JVM的运行状态之中
                验证  确保加载的类信息符合JVM规范 没有安全方面的问题
                准备  正式为类变量(static变量)分配内存并设置类变量初始值的阶段
                     这些内存都将在方法区中进行分配 注意此时的设置初始值为默认值
                     具体赋值在初始化阶段完成
                解析
                     虚拟机常量池内的符号引用替换为直接引用 地址引用 的过程
3 初始化
        初始化阶段是执行类构造器<clinit>()方法的过程 类构造器<clinit>()方法是由编译器自动收集类中的所有类变量的赋值动作和静态语句块(static块)中的语句合并产生的
        当初始化一个类的时候 如果发现其父类还没有进行过初始化 则需要先初始化其父类
            虚拟机会保证一个类的<clinit>()方法在多线程环境中被正确加锁和同步
4 使用
5 卸载

    JVM 创建对象的过程

1 检查加载  符号引用(常量池)  转换为 直接引用==真实的地址
2 分配内存
    划分内存方式   指针碰撞  空闲列表
    解决并发问题 ->
            1 CAS(CPU 指令 原子性, 底层lock只给一个CPU执行)
        2 维护一个本地线程缓冲列表Thread Local Allocation Buffer  +UserTLAB 默认开启
    并发安全问题
3 初始化
4 设置对象头
5 初始化对象头


            对象的引用
    两种方式

            句柄池
    直接指针  访问更快 不用重定位

            垃圾回收

    自动化垃圾回收  判断那些是垃圾  System.GC() 强制回收
                1 引用计数法
                2 GCRoot   静态变量 线程变量  常量池  JNI指针  内部引用(Class 对象, 异常对象 类加载器 synchronized) 内部对象 JMXBean 临时对象  跨代引用
    Class 回收的条件
    Class 回收比较苛刻   new 出来的所有对象都需要回收  类加载器也要回收   Java.lang.class 任何地方没有被引用 无法通过反射调用  参数控制 XnoClassGC

            C malloc  free
            C++ new   delete


    finalize 拯救对象  不太行
    1. finalize 优先级比较低 GC 后需要先休眠 才能拯救成功
    2. finalize 只执行一次

    各种引用
            强引用
    软引用 SortReference<User> userSoft   即将OOM  图片缓存
    弱引用 WeakReference<User> userWeak   只要垃圾回收 内存就会回收 weakHashMap
    虚引用 PhantomReference               垃圾回收是否正常工作

            对象的分配策略

    虚拟机的优化技术
    1 热点数据 出发JIT 方法执行的次数很多 1000次可能就触发了
    2 逃逸分析  其他的方法调不到我 栈上分配            -DoEscapeAnalysis 默认是开启的, 只有HotStopService有这个功能
    3 本地线程分配缓冲  减少线程冲突

    几乎所有的对象都在堆中进行分配
    4 分代模型
        5 对象优先分配的Eden分配  大对象会直接分配到老年代(Serial 和 ParNew 垃圾回收期) -xx:PretenureSizeThreshold = 4m 才分配
        6 长期存活的对象进入老年代  age = 15  -XX:MaxTenuringThreshold 对象年龄 四个2进制 1111  CMS 垃圾回收器默认是6
        7 动态年龄判断? 年龄相同的对象 大于等于交换区的一般 就直接晋级老年代 把 这个年龄设置为新的阈值
        8 空间分配担保  新生代 yangGC  老年代 FullGC 分代回收  担保值JVM 统计历次 老年代回收


            分代回收

    垃圾回收 新生代(Eden From to):yangGC MinorGc 老年代(Tenured) MajorGc OldGC  {Full GC + 方法区 新生代 老年代}

    eden区做第一次YGC时，放入s0、s1时是随机的。这里存在一个from和to的概念，
    from是接收从eden区来YGC对象，to是由from占满时时转移的目的地，当转移之后，to会变成from，而from会变成to。

    垃圾回收算法
    复制算法 只复制存活  实现简单 运行高效 绝大多数对象朝生夕死  空间利用率只有一半(Eden + S0 + S1) 空间利用率90% 需要暂停  更新局部变量表里面的引用
    标记清楚 老年代  空间不连续 产生碎片  可以做到不暂停
    标记整理算法 标记先整理再清除  清除就可以整体清除  需要暂停 指针需要移动
    可达性分析很快  对象移动 很慢


    JVM 垃圾回收和性能调优

    单线程
            多线程并行垃圾回收
    并发垃圾回收器

    Java -XX:+PrintFlagsFinal -version JVM 所有参数打印出来
            历史
    一开始只有
1. Serial  新生代 Serial Old老年代 这对组合  单线程 几M - 几百M 因为要暂停  暂停时间比较长 所有的用户线程
2. 多线程垃圾回收 PS 组合 ParallelScvenge 新生代 ParellOld 老年代  可以设置暂停时间 -xx:MaxGCPauseMillis = 500ms  吞吐量优先
    自适应 UserAdaptiveSizePolicy 自适应堆空间大小 自动扩容 伴随着GC进行扩容 不用指定分代的比例
    MaxHeapFreeRatio 70% 缩容  空闲大于堆空间的70% 缩容, 扩容同理  但会带来性能影响 建议把堆空间的最小值 和 最大值设置为一样
    MinHeapFreeRatio 20% 扩容
3. 响应优先 对于游戏服务器来说 注重响应速度 GC就要业务线程暂停 stop the world 减少STW时间 -> CMS
    CMS Concurrent Mark Swap 单独针对老年代的垃圾回收器 导致系统卡顿的注意原因 新生代的都是复制算法 很快
    ParNew 新生代 CMS 回收老年代 这一套
    原理 就是把垃圾回收的过程进行拆分 标记清理算法 不用暂停
    标记
    初始标记   首先找到根  直连接的对象 标记不多 根本身有限 暂停时间比较短 需要短暂暂停
    并发标记   找可达  链路比较复制 耗时比较长  引用的变化 重新标记
    预清理  参数控制  CMSPrecleaningEnable = true 默认开启 业务线程不暂停 并发阶段
    Eden 区引用了老年代没有标记的对象 做标记  原理是放在重新标记里面做
    老年代内部引用变化 分区  把有垃圾的分区标记出来 类似查表的实现方式 只清理分区, 而不是整理老年代
    并发可中断预清理  CMSScheduleRemarkedEdenPentration
    重新标记   标记并发标记变化的 必须暂停 业务暂停不停止 引用关系没办法确定
    并发清除   清理后 重置线程为业务线程
    CMS优化问题  预清理  并发可中断预清理

    CMS问题  CPU敏感 多线程
    浮动垃圾  并发清理  又产生 新产生的需要下次再清理 CMS 因为浮动垃圾 要提前触发

    JVM 内存调优
    新生代 老年代 持久代(元空间)这个区域会存储包括类定义,结构,字段,方法（数据及代码）以及常量在内的类相关数据,
    它可以通过-XX:PermSize及-XX:MaxPermSize来进行调节, 如果它的空间用完了,会导致java.lang.OutOfMemoryError: PermGen space的异常。

    总大小  3 - 4 倍活跃数据大小
    新生代  1 - 1.5 倍活跃数据大小
    老年代  2 - 3 倍活跃数据大小
    永久代/元空间 1.2 - 1.5 Full GC 后的永久代空间占用

    扩展新生代能不能提供GC效率? 可以的 绝大多数对象都是短周期对象  扩容可以减少对象扩容
    Eden 复制到 S0 区
            1 扫描 Eden
            2 复制到 S0
    JVM 是如果避免Minor GC时扫描全堆的
    跨代引用 可能会扫描全堆
    解决办法 卡表  把老年代分区  跨代引用的区 有跨代引用的标记为脏 然后只扫描脏数据

    常量池
    Class常量池 class method
    运行时常量池 字面量 符号引用 -> 直接引用  类或接口常量池运行时表示 符号表
    字符串常量池 String 不是基本数据类型 依靠一个Final Char[]数组 JDK1.8  byte[]数组 JDK1.9  不可变 确保唯一性
    String a = new String("Liuwei").intern();  // 字符串常量池创建
    String b = new String("Liuwei").intern();
    a == b return true

    JDK 发展