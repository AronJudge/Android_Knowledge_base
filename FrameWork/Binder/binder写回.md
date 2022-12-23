## Binder写回

oneway

oneway可以用来修饰在interface之前，这样会造成interface内所有的方法都隐式地带上oneway；
oneway也可以修饰在interface里的各个方法之前。
被oneway修饰了的方法不可以有返回值，也不可以有带out或inout的参数。

带oneway的实现

带oneway的方法，不会生成局部变量_reply。
且Proxy中transact中第四个参数必为android.os.IBinder.FLAG_ONEWAY。

```java
//proxy类
    @Override public void testOneway(int pa) throws android.os.RemoteException
    {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(pa);
            mRemote.transact(Stub.TRANSACTION_testOneway, _data, null, android.os.IBinder.FLAG_ONEWAY);
        }
        finally {
            _data.recycle();
        }
    }

//stub类
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            case TRANSACTION_testOneway:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.testOneway(_arg0);
                    return true;
                }
        }


```

不带oneway的实现

不带oneway的方法，会生成局部变量_reply，但当方法返回值为void时，
不会生成局部变量_result，这个才是真正的返回值。且Proxy中transact中第四个参数必为0。

```java
//proxy类
	@Override public byte SerTestIn(byte[] pa) throws android.os.RemoteException
	{
	    android.os.Parcel _data = android.os.Parcel.obtain();
	    android.os.Parcel _reply = android.os.Parcel.obtain();
	    byte _result;
	    try {
	        _data.writeInterfaceToken(DESCRIPTOR);
	        _data.writeByteArray(pa);
	        mRemote.transact(Stub.TRANSACTION_SerTestIn, _data, _reply, 0);
	        _reply.readException();
	        _result = _reply.readByte();
	    }
	    finally {
	        _reply.recycle();
	        _data.recycle();
	    }
	    return _result;
	}

        //stub类
        case TRANSACTION_SerTestIn:
            {
                data.enforceInterface(descriptor);
                byte[] _arg0;
                _arg0 = data.createByteArray();
                byte _result = this.SerTestIn(_arg0);
                reply.writeNoException();
                reply.writeByte(_result);
                return true;
            }

```

为了方便起见，把proxy类称为调用方，stub类称为实现方。在跨进程调用中，参数和返回值肯定都是会有复制的过程的，
由于系统的设计，将内核空间再映射到用户空间，这样复制过程只需要一次。下面代码都是从自动生成的java代码抽取而来。


proxy调用了_data.writeByteArray(pa);，说明pa这个参数确实传递到了服务方。但stub里只有reply.writeByte(_result);
说明服务方对pa参数的任何改变都不会反应到调用方。

## In 参数
```java
//proxy类
	@Override public byte SerTestIn(byte[] pa) throws android.os.RemoteException
	{
	    android.os.Parcel _data = android.os.Parcel.obtain();
	    android.os.Parcel _reply = android.os.Parcel.obtain();
	    byte _result;
	    try {
	        _data.writeInterfaceToken(DESCRIPTOR);
	        _data.writeByteArray(pa);
	        mRemote.transact(Stub.TRANSACTION_SerTestIn, _data, _reply, 0);
	        _reply.readException();
	        _result = _reply.readByte();
	    }
	    finally {
	        _reply.recycle();
	        _data.recycle();
	    }
	    return _result;
	}
//stub类
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            ...
            case TRANSACTION_SerTestIn:
            {
                data.enforceInterface(descriptor);
                byte[] _arg0;
                _arg0 = data.createByteArray();
                byte _result = this.SerTestIn(_arg0);
                reply.writeNoException();
                reply.writeByte(_result);
                return true;
            }
            ...
        }

```

## out参数

```java
//proxy类
    @Override public byte SerTestOut(byte[] pa) throws android.os.RemoteException
    {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        byte _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            if ((pa==null)) {
                _data.writeInt(-1);
            }
            else {
                _data.writeInt(pa.length);
            }
            mRemote.transact(Stub.TRANSACTION_SerTestOut, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readByte();
            _reply.readByteArray(pa);
        }
        finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

//stub类
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
        ...
        case TRANSACTION_SerTestOut:
        {
            data.enforceInterface(descriptor);
            byte[] _arg0;
            int _arg0_length = data.readInt();
            if ((_arg0_length<0)) {
                _arg0 = null;
            }
            else {
                _arg0 = new byte[_arg0_length];
            }
            byte _result = this.SerTestOut(_arg0);
            reply.writeNoException();
            reply.writeByte(_result);
            reply.writeByteArray(_arg0);
            return true;
        }
        ...

```
proxy类里面，发现读取参数的时候，仅仅是_data.writeInt(pa.length);读取一下数组的长度，而不是读取数组的内容。

stub类里面，发现它居然用之前读取的数组长度新建了一个数组_arg0 = new byte[_arg0_length];，然后把这个新建的同样长度的数组传递给了真正的方法，难道我们pa参数这个数组的内容都不重要吗，仅仅是为了告诉对方数组的长度吗？（你眉头一皱发现事情并不简单==）事实上，就是这么简单，pa数组的长度才是有用信息。

stub类里面，开始设置返回值时，发现多了一步reply.writeByteArray(_arg0);，其实是服务方对新建的数组赋值了，然后要作为返回值返回。

proxy类里面，既然服务方要返回调用方一个数组，那就接受吧，_reply.readByteArray(pa);，然后把pa进行赋值。

## inout参数
```java
//proxy类
    @Override public byte SerTestInout(byte[] pa) throws android.os.RemoteException
    {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        byte _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeByteArray(pa);
            mRemote.transact(Stub.TRANSACTION_SerTestInout, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readByte();
            _reply.readByteArray(pa);
        }
        finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }
//stub类
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
        ...
        case TRANSACTION_SerTestInout:
            {
                data.enforceInterface(descriptor);
                byte[] _arg0;
                _arg0 = data.createByteArray();
                byte _result = this.SerTestInout(_arg0);
                reply.writeNoException();
                reply.writeByte(_result);
                reply.writeByteArray(_arg0);
                return true;
            }
        ...
        }

```

分析和上面类似了。
服务方会完好无损地收到调用方发来的pa数组。
服务方可能会对pa数组进行修改，然后再返回给调用方。

总结

in参数使得实参顺利传到服务方，但服务方对实参的任何改变，不会反应回调用方。

out参数使得实参不会真正传到服务方，只是传一个实参的初始值过去（这里实参只是作为返回值来使用的，这样除了return那里的返回值，还可以返回另外的东西），
但服务方对实参的任何改变，在调用结束后会反应回调用方。

inout参数则是上面二者的结合，实参会顺利传到服务方，且服务方对实参的任何改变，在调用结束后会反应回调用方。
其实inout，都是相对于服务方。in参数使得实参传到了服务方，所以是in进入了服务方；out参数使得实参在调用结束后从服务方传回给调用方，所以是out从服务方出来。
