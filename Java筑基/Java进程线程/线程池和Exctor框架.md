#阻塞队列和线程池原理

BlockingQueue
add()   非阻塞 抛出异常
remove() 非阻塞 抛出异常

offer()
poll()  null

put() 阻塞
take() 阻塞

常见的BlockingQueue
1. ArrayBlockingQueue 数组结构的有界阻塞队列
2. LinkedBlockingQueue 链表结构的有界阻塞队列
3. PriorityBlockingQueue 支持优先级的无界阻塞队列
4. DelayQueue 优先级实现的无界阻塞队列
5. SynchronousQueue 不存储元素的阻塞队列
6. LinkedTransferQueue 链表存储的无界阻塞队列
7 LinkedBollockingDeque 
   
线程池
Thread 线程
创建 销毁 资源消耗
线程是稀缺而昂贵的资源
分配和创建统一管理起来

```java
import java.util.concurrent.*;

// 不关心结果 execute
public interface Executor {
    void execute(Runnable command);
}
// 关系结果
public abstract class AbstractExecutorService implements ExecutorService {
    summit(Runnable, Future);
    summit(Callable);
    showDown() 关闭 之后中断没有执行任务的线程
    showDownNow 关闭线程 发送中断    
}

// 合理配置线程参数 
// 任务特性  
// 1 CPU 密集型 计算(最大线程不要超过CPU的核心数 + 1, 机器的内存有限 磁盘的一部分当虚拟内存 缺页中断 CPU阻塞，+ 1 为了让CPU充分利用起来 ) Runtime.getRuntime().availableProcessors() 
// 2 IO 密集型  网络通信 读写磁盘  最大线程数 为 CPU核心数 * 2
// 3 混合
public class ThreadPoolExecutor {
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        // 直接丢弃最老的 队列最前面的拒绝策略
        java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
        // 直接抛出异常
        java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
        // 调用者线程执行任务
        java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
        // 直接丢弃
        java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
    }
}

```

