/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.ts.car.hardkeytest2;
public interface IHardKeyPolicyManager extends android.os.IInterface
{
  /** Default implementation for IHardKeyPolicyManager. */
  public static class Default implements com.ts.car.hardkeytest2.IHardKeyPolicyManager
  {
    @Override public void addKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException
    {
    }
    @Override public void removeKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.ts.car.hardkeytest2.IHardKeyPolicyManager
  {
    private static final java.lang.String DESCRIPTOR = "com.ts.car.hardkeytest2.IHardKeyPolicyManager";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.ts.car.hardkeytest2.IHardKeyPolicyManager interface,
     * generating a proxy if needed.
     */
    public static com.ts.car.hardkeytest2.IHardKeyPolicyManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.ts.car.hardkeytest2.IHardKeyPolicyManager))) {
        return ((com.ts.car.hardkeytest2.IHardKeyPolicyManager)iin);
      }
      return new com.ts.car.hardkeytest2.IHardKeyPolicyManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_addKeyEventCallBack:
        {
          data.enforceInterface(descriptor);
          com.ts.car.hardkeytest2.IKeyEventCallback _arg0;
          _arg0 = com.ts.car.hardkeytest2.IKeyEventCallback.Stub.asInterface(data.readStrongBinder());
          int _arg1;
          _arg1 = data.readInt();
          this.addKeyEventCallBack(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_removeKeyEventCallBack:
        {
          data.enforceInterface(descriptor);
          com.ts.car.hardkeytest2.IKeyEventCallback _arg0;
          _arg0 = com.ts.car.hardkeytest2.IKeyEventCallback.Stub.asInterface(data.readStrongBinder());
          int _arg1;
          _arg1 = data.readInt();
          this.removeKeyEventCallBack(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.ts.car.hardkeytest2.IHardKeyPolicyManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void addKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          _data.writeInt(sceneType);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addKeyEventCallBack, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addKeyEventCallBack(callback, sceneType);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void removeKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          _data.writeInt(sceneType);
          boolean _status = mRemote.transact(Stub.TRANSACTION_removeKeyEventCallBack, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().removeKeyEventCallBack(callback, sceneType);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.ts.car.hardkeytest2.IHardKeyPolicyManager sDefaultImpl;
    }
    static final int TRANSACTION_addKeyEventCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_removeKeyEventCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(com.ts.car.hardkeytest2.IHardKeyPolicyManager impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.ts.car.hardkeytest2.IHardKeyPolicyManager getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void addKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException;
  public void removeKeyEventCallBack(com.ts.car.hardkeytest2.IKeyEventCallback callback, int sceneType) throws android.os.RemoteException;
}
