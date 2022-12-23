# Binder

## Binder 死亡代理

在进行进程间通信的过程中，如果服务端进程由于某种原因异常终止，我们的远程调用就会失败，
影响我们的功能，那么怎么样能够知道服务端进程是否终止了呢，一种方法就是给Binder设置死亡代理。

Binder两个重要的方法
1. linkToDeath 
2. unlinkToDeath

除设置死亡代理，Binder对象还有两个方法可以判断服务器进行是否存在或存活,返回均为boolean类型

mBookManager.asBinder().pingBinder();

mBookManager.asBinder().isBinderAlive()

也就是常说的死亡代理  当Binder死亡时 我们就会收到通知 这个时候我们就可以重新发起连接请求从而恢复连接

```java

public class AmbienceLightsManager implements IBinder.DeathRecipient {

    public static AmbienceLightsManager getInstance() {
        Log.d(TAG, "AmbienceLightsManager.getInstance");
        if (null == sInstance) {
            synchronized (AmbienceLightsManager.class) {
                if (null == sInstance) {
                    sInstance = new AmbienceLightsManager();
                }
            }
        }
        return sInstance;
    }

    private void tryConnectService() {
        Intent serviceIntent = new Intent(SERVICE_FILTER);
        serviceIntent.setPackage(SERVICE_PACKAGE);
        Log.d(TAG, "tryConnectService: intent = " + serviceIntent);
        boolean s = mContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "tryConnectService: intent = " + serviceIntent + ",connection = " + connection + " is success = " + s);
    }

    /**
     * release.
     */
    public synchronized void release() {
        Log.d(TAG, "release()");
        try {
            if (null != mService && !mService.asBinder().isBinderAlive()) {
                Log.d(TAG, "mService.asBinder().unlinkToDeath(mDeathRecipient, 0)");
                mService.asBinder().unlinkToDeath(AmbienceLightsManager.this, 0);
            }
        } catch (Exception exception) {
            Log.w(TAG, exception);
        }
        if (null != mContext) {
            mContext.unbindService(connection);
        }
        mService = null;
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            try {
                Log.d(TAG, "onServiceConnected: service = " + binder + ", descrip:" + binder.getInterfaceDescriptor());
                mService = IAmbienceLightsService.Stub.asInterface(binder);
                Log.d(TAG, "onServiceConnected: mService = " + mService);
                try {
                    // mService为服务端进行的Service对象，通过asBinder()可以获得Binder对象 死亡了触发binderDied
                    mService.asBinder().linkToDeath(AmbienceLightsManager.this, 0); 
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:  " + name);
            mService = null;
        }
    };

    /**
     * binder died
     */
    @Override
    public void binderDied() {
        Log.d(TAG, "binderDied: tryConnectService!!!");
        if (mService != null) {
            // 解除死亡通知，如果Binder死亡了，不会在触发binderDied方法
            mService.asBinder().unlinkToDeath(this, 0);
        }
        tryConnectService();
    }



}
```