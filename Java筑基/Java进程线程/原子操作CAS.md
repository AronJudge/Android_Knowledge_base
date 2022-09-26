线程的生命周期

初始

就绪
    等待 等待超时
运行
    阻塞 只有synchronized 没有获取到是阻塞状态 其他都是等待
终止

死锁
两个或两个以上线程由于竞争资源 或者 由于 彼此通信而造成的一种阻塞状况 
条件
1 多个竞争这
2 争夺资源的顺序不对
3 拿到资源不放手

1 互斥条件 
2 请求和保持
3 不剥夺
4 环路等待  

可以用尝试去那锁的机制去避免死锁, 记得怎加休眠时间 避免活锁 不断去尝试拿锁

CAS 基本原理

Compare and swap
现代处理器都支持CAS的指令
Atomic开头的这些类 都是原子遍历类 不可再分的
ABA问题  加一个版本戳就行
开销问题
只能保证一个共享变量

使用:
JDK 提供了很多接口
基本类型
AtomicBoolean AtomicInteger AtomicLong
AtomicIntegerArray AtomicLongArray AtomicLongArray AtomicRefenceArray

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class UseAtomicInt {
    static AtomicInteger ai = new AtomicInteger(10);

    public static void main(String[] args) {
        ai.getAndIncrement();
        ai.incrementAndGet();
        ai.addAndGet(24);
        ai.getAndAdd(24);
    }
}
```

