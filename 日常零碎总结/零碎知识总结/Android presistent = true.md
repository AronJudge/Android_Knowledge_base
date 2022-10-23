# Andorid persistent = "true"



```xml
<application
        android:name="com.xxxx.xxx.Application"
        android:allowBackup="true"
        android:directBootAware="true"
        android:label="xxxService"
        android:persistent="true">

        <service
            android:name="com.xxxx.xxx.Service"
            android:exported="true" />

    </application>
```

在AndroidMainfest.xml 中将 persistent 属性设为true， 可永久性启动应用。

android:persistent="true"
persistent属性定义在frameworks/base/core/res/res/values/attrs_manifest.xml中：

```xml
<!-- Flag to control special persistent mode of an application.  This should
     not normally be used by applications; it requires that the system keep
     your application running at all times. -->
<attr name="persistent" format="boolean" />
```

------

我们知道，persistent一词的意思是“持久”，那么persistent应用的意思又是什么呢？简单地说，这种应用会顽固地运行于系统之中，从系统一启动，一直到系统关机。

​	为了保证这种持久性，persistent应用必须能够在异常出现时，自动重新启动。在Android里是这样实现的。每个ActivityThread中会有一个专门和AMS通信的binder实体——final ApplicationThread mAppThread。这个实体在AMS中对应的代理接口IApplicationThread。当AMS执行到attachApplicationLocked()时，会针对目标用户进程的IApplicationThread接口，注册一个binder讣告监听器，一旦日后用户进程意外挂掉，AMS就能在第一时间感知到，并采取相应的措施。如果AMS发现意外挂掉的应用是persistent的，它会尝试重新启动这个应用。

在我们开发系统级的App时，很有可能就会用persistent属性。当在AndroidManifest.xml中将persistent属性设置为true时，那么该App就会具有如下两个特性：

- 在系统刚起来的时候，该App也会被启动起来

- 该App被强制杀掉后，系统会重启该App。这种情况只针对系统内置的App，第三方安装的App不会被重启。

是一个用于控制应用程序特殊持久模式的标志。通常情况下不应被应用程序使用，它要求系统始终保持应用程序的运行。

#### persistent应用启动时机很早，早于开机广播的发送，以及桌面启动。

```java
public void systemReady(final Runnable goingCallback) 
{
	...
	synchronized (this) {
		// Only start up encryption-aware persistent apps; once user is
		// unlocked we'll come back around and start unaware apps
		startPersistentApps(PackageManager.MATCH_DIRECT_BOOT_AWARE);
		...
		if (skipHome == false) {
			//启动桌面
			startHomeActivityLocked(currentUserId, "systemReady");
		}
		...
		//这里发送完FINISH_BOOTING_MSG后才开始发送开机广播
		postFinishBooting(false, true);
		...
	}
	....
}

```



##### 3.1 persistent属性的解析

属性的解析主要发生在App安装或者系统启动的时候，解析代码的位置在：

```
/frameworks/base/core/java/android/content/pm/PackageParser.java
```

深入到PackageParser.java的parseBaseApplication中：

```java
private boolean parseBaseApplication(Package owner, Resources res,
            XmlPullParser parser, AttributeSet attrs, int flags, String[] outError)
        throws XmlPullParserException, IOException {

	final ApplicationInfo ai = owner.applicationInfo;
    final String pkgName = owner.applicationInfo.packageName;

    TypedArray sa = res.obtainAttributes(attrs,
            com.android.internal.R.styleable.AndroidManifestApplication);

	...省略...

		if ((flags&PARSE_IS_SYSTEM) != 0) {
	            if (sa.getBoolean(
	                    com.android.internal.R.styleable.AndroidManifestApplication_persistent,
	                    false)) {
	                ai.flags |= ApplicationInfo.FLAG_PERSISTENT;
	            }
	        }

	...省略...
}
```

在解析完系统中App的包信息后，会将解析好的信息保存在PMS中的mPackages的map中，ApplicationInfo的flag中有一个bit位用于保存该App是否是persistent。

这里主要是将persistent的flag设置为ApplicationInfo.FLAG_PERSISTENT。

3.2 系统启动persistent为true的App
在系统启动时，会启动persistent属性为true的App，代码位置在：

```java
/frameworks/base//services/core/java/com/android/server/am/ActivityManagerService.java
```

在系统启动时，AMS中的systemReady()方法会将所有在AndroidManifest中设置了persistent为true的App进程拉起来。

深入到AMS的systemReady()方法中：

```java
public void systemReady(final Runnable goingCallback) {

	...省略...

    synchronized (this) {
        if (mFactoryTest != FactoryTest.FACTORY_TEST_LOW_LEVEL) {
            try {
                List apps = AppGlobals.getPackageManager().
                    getPersistentApplications(STOCK_PM_FLAGS);//注释1
                if (apps != null) {
                    int N = apps.size();
                    int i;
                    for (i=0; i<N; i++) {
                        ApplicationInfo info
                            = (ApplicationInfo)apps.get(i);
                        if (info != null &&
                                !info.packageName.equals("android")) {
                            addAppLocked(info, false, null /* ABI override */);//注释2
                        }
                    }
                }
            } catch (RemoteException ex) {
                // pm is in same process, this will never happen.
            }
        }
		...省略...
	}
```

注释说明：

注释1：调用PackageManagerServices的getPersistentApplications方法获取所有在AndroidManifest中设置了persistent属性为true的App

注释2：调用ActivityManagerServcies的addAppLocked方法去启动App

深入到PackageManagerServices的getPersistentApplications方法中：

```java
public List<ApplicationInfo> getPersistentApplications(int flags) {
    final ArrayList<ApplicationInfo> finalList = new ArrayList<ApplicationInfo>();

    // reader
    synchronized (mPackages) {
        final Iterator<PackageParser.Package> i = mPackages.values().iterator();
        final int userId = UserHandle.getCallingUserId();
        while (i.hasNext()) {
            final PackageParser.Package p = i.next();
            if (p.applicationInfo != null
                    && (p.applicationInfo.flags&ApplicationInfo.FLAG_PERSISTENT) != 0
                    && (!mSafeMode || isSystemApp(p))) {
                PackageSetting ps = mSettings.mPackages.get(p.packageName);
                if (ps != null) {
                    ApplicationInfo ai = PackageParser.generateApplicationInfo(p, flags,
                            ps.readUserState(userId), userId);
                    if (ai != null) {
                        finalList.add(ai);
                    }
                }
            }
        }
    }

    return finalList;
}
```

getPersistentApplications方法会遍历mPackages中所有的App，从判断条件中可以看到只有当在解析persistent属性时，ApplicationInfo的flag设置成了FLAG_PERSISTENT，且是系统App；或者是在非安全模式下，才会被选中。

可以看出被选中的情形有两种：

- 系统App，只要ApplicationInfo的flag设置成了FLAG_PERSISTENT

- 第三方安装的App，不仅要ApplicationInfo的flag设置成了FLAG_PERSISTENT，还需要在非安全模式下

继续回到ActivityManagerServcies的addAppLocked方法中：

```java
final ProcessRecord addAppLocked(ApplicationInfo info, boolean isolated,
        String abiOverride) {
    ProcessRecord app;
	
	//传进来的isolated是false，所以就会调用getProcessRecordLocked方法，
	//但由于是第一次启动，所以所有的返回都是app = null
    if (!isolated) {
        app = getProcessRecordLocked(info.processName, info.uid, true);
    } else {
        app = null;
    }

    if (app == null) {
		//为新的app创建新的ProcessRecord对象
        app = newProcessRecordLocked(info, null, isolated, 0);
        updateLruProcessLocked(app, false, null);
        updateOomAdjLocked();
    }

    // This package really, really can not be stopped.
    try {
		//因为是开机第一次启动，所以新的App的启动状态就是将要被启动的状态
		//所以将App的停止状态stoped设置为false
        AppGlobals.getPackageManager().setPackageStoppedState(
                info.packageName, false, UserHandle.getUserId(app.uid));
    } catch (RemoteException e) {
    } catch (IllegalArgumentException e) {
        Slog.w(TAG, "Failed trying to unstop package "
                + info.packageName + ": " + e);
    }

	//如果是系统App，且persistent属性为true，则异常死亡后会重启
    if ((info.flags & PERSISTENT_MASK) == PERSISTENT_MASK) {
        app.persistent = true;
        app.maxAdj = ProcessList.PERSISTENT_PROC_ADJ;
    }

	//如果App已启动，则不处理，否则调用startProcessLocked方法启动App
	//启动App是异步的，因此会将正在启动，但还没启动完成的App添加到mPersistentStartingProcesses列表中，当启动完成后再移除
    if (app.thread == null && mPersistentStartingProcesses.indexOf(app) < 0) {
        mPersistentStartingProcesses.add(app);
		//启动App
        startProcessLocked(app, "added application", app.processName, abiOverride,
                null /* entryPoint */, null /* entryPointArgs */);
    }

    return app;
}
```

在App启动完成后，会在ActivityThread中调用ActivityManagerService的attachApplicationLocked()方法，将该App从mPersistentStartingProcesses移除，并注册一个死亡讣告监听器AppDeathRecipient，用于在App异常被杀后的处理工作。

深入到ActivityManagerService的attachApplicationLocked()方法中：

```java
private final boolean attachApplicationLocked(IApplicationThread thread,
        int pid) {

	...省略...

    final String processName = app.processName;
    try {
		//注册死亡讣告监听器AppDeathRecipient
        AppDeathRecipient adr = new AppDeathRecipient(
                app, pid, thread);
        thread.asBinder().linkToDeath(adr, 0);
        app.deathRecipient = adr;
    } catch (RemoteException e) {
        app.resetPackageList(mProcessStats);
		//如果注册死亡讣告监听器失败，也会重新启动App进程
        startProcessLocked(app, "link fail", processName);
        return false;
    }

	...省略...
	// Remove this record from the list of starting applications.
    mPersistentStartingProcesses.remove(app);

	...省略..
```

到此，persistent属性为true的App在开机时就会启动，并且会注册死亡讣告监听器AppDeathRecipient。

![092653_7LaR_174429.gif](http://static.oschina.net/uploads/space/2014/0527/092653_7LaR_174429.gif)







activity的启动过程：https://my.oschina.net/youranhongcha/blog/269591

