## 说说 getMainLooper

关于 Handler、Looper、MessageQueue，我想大家都了解的差不多了，
简单来说就是一个 Handler 对应一个 Looper，一个 Looper 对应一个 Message。那么再想个问题，
一个 Handler 可以对应多个 Looper 吗？ 一个 Looper 可以对应多个 Handler 吗？

```java

    /*
     * 返回应用主线程中的 Looper
     */
    public static Looper getMainLooper() {
        synchronized (Looper.class) {
            return sMainLooper;
        }
    }
```


其实我们平时最常用的是无构造参数的 Handler，其实 Handler 还有构造参数的构造方法，如下：

```java
    public Handler(Looper looper, Callback callback, boolean async) {
        mLooper = looper;
        mQueue = looper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }
```
在此注意构造函数中第一个参数是 Looper 就可以了，那么也就是说，我们可以传递一个已有的 Looper 来创建 Handler。
这里先不写示例代码了，填个坑，以后有时间再写，大概是下面这样：

```java
   Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
```

注意上面的 Looper.getMainLooper()，将主线程中的 Looper 扔进去了，也就是说 handleMessage 会运行在主线程中，那么这样有什么用呢？
这样可以在主线程中更新 UI 而不用把 Handler 定义在主线程中。

当然刚才提到的作用只是对应于主线程中的 sMainLooper 了，其实各种 Looper 都可以往 Handler 的构造方法这里扔，从而使得 handleMessage 运行在你想要的线程中，进而实现线程间通信。

那么想到另一篇文章 HandlerThread源码解析 中 HandlerThread#getLooper() 的作用了吗？

文章开头也提到了一个问题，那么答案就应该是：一个 Handler 中只能有一个 Looper，而一个 Looper 则可以对应多个 Handler，
只要把 Looper 往 Handler 的构造方法里扔扔扔就好了。

今天再看了看 AsyncTask 的源码，发现其中也用到了 getMainLooper()，来更新 UI，源码如下：

```java
private static class InternalHandler extends Handler {
        public InternalHandler() {
            // 使用主线程的 Looper 扔给 Handler
            super(Looper.getMainLooper());
        }
}
```


## new Handler().obtainMessage().sendToTarget()过程分析

new Handler().obtainMessage().sendToTarget()这句话用着真爽，一行代码就能搞定异步消息了！所以在代码中使用的算是非常频繁的了，那这又是一个什么样的过程呢？ 

这个过程中又有什么玄机呢？ 这篇文章，我们来一步步的分析一下这三句话。

### 1、new Handler()的分析

new Handler()会辗转来到public Handler(Callback callback, boolean async)这个构造方法。

在这个构造方法中会获取当前Looper： mLooper = Looper.myLooper();

而此时的Looper是默认的那个Looper，即在ActivityThread的main方法中prepare的Looper

在ActivityThread.main中：

Looper.prepareMainLooper();
...
Looper.loop();

此时Handler和Looper有了关联。

### 2、obtainMessage()的分析

在Handler.obtainMessage()中会调用Message.obtain(this)。
Message.obtain()的源码：

public static Message obtain(Handler h) {
        Message m = obtain();
        m.target = h;
        return m;
  }

obtain从消息池中构造一个message，并将message的target置为传进来的handler。此时handler和message有了关联。

sendToTarget()的分析

public void sendToTarget() {
        target.sendMessage(this);
}

直接调用target.sendMessage，而target正是当前的Handler。
继续跟踪target.sendMessage(this) target中发送消息会辗转来到
private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis)
在这个方法中有去调用queue.enqueueMessage.

继续跟踪queue.enqueueMessage,这个方法有点长，主要是将当前Message压入消息队列中：

```java
...
msg.when = when;
Message p = mMessages;
boolean needWake;
if (p == null || when == 0 || when < p.when) {
    // New head, wake up the event queue if blocked.
    msg.next = p;
    mMessages = msg;
    needWake = mBlocked;
} else {
    // Inserted within the middle of the queue.  Usually we don't have to wake
    // up the event queue unless there is a barrier at the head of the queue
    // and the message is the earliest asynchronous message in the queue.
    needWake = mBlocked && p.target == null && msg.isAsynchronous();
    Message prev;
    for (;;) {
         prev = p;
         p = p.next;
         if (p == null || when < p.when) {
                break;
          }
         if (needWake && p.isAsynchronous()) {
               needWake = false;
          }
     }
     msg.next = p; // invariant: p == prev.next
     prev.next = msg;
 }
 
// We can assume mPtr != 0 because mQuitting is false.
if (needWake) {
     nativeWake(mPtr);
}
...

```

这里面的MessageQueue就是在Handler保存的那个MessageQueue，也就是说此时，
这个Message已经保存到Handler中的那个消息队列中了。而，我们Handler中的MessageQueue哪来的呢？ 来看看这行代码：
```java
    public Handler(Callback callback, boolean async) {
    ...
    mLooper = Looper.myLooper();
    ...
    mQueue = mLooper.mQueue;
    ...
    }
```



Handler中的MessageQueue正式从Looper中获取的，这个Looper当然就是在ActivityThread中prepare的那个。


顺一下此时的关系：Handler中保存了ActivityThread中的Looper，并从该Looper中获取了MessageQueue；调用obtainMessage，实质上是创建了一个Message对象，并将Message对象的target设置为现在的Handler；调用Message.sendToTarget()实际是调用了Message.target.sendMessage()，即Handler.sendMessage，而Handler.sendMessage会来到enqueueMessage方法，在这个方法中调用MessageQueue.enqueueMessage将消息压缩刚开始我们获取的那个MessageQueue。

此时，再来看看Looper.loop()是怎么将消息回调到handleMessage中的：
```java
    for (;;) {
            Message msg = queue.next(); // might block
            ...
            msg.target.dispatchMessage(msg);
            ...
            msg.recycle();
     }
```



继续看看Handler.dispatchMessage():

```java
    public void dispatchMessage(Message msg) {
            if (msg.callback != null) {
                handleCallback(msg);
            } else {
                if (mCallback != null) {
                    if (mCallback.handleMessage(msg)) {
                        return;
                    }
                }
                handleMessage(msg);
            }
    }
```


