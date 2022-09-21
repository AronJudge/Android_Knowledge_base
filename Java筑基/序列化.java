序列化的概念
        将数据结构或对象转换成二进制串的过程

        反序列化
        将序列化过程中产生的二进制串转换成数据结构或者对象的过程

        持久化
        数据结构或对象 存储起来

        序列化方案
        1.Serializable Java
        2.Parcelable Android

        合理的序列化方案

public interface Serializable {
    // 空接口如何实现序列化？
    // ObjectOutput
    // ObjectStreamClass  描述一个对象的结构
}

public interface Externalizable extends java.io.Serlizable {
    // 读写顺序要求一致
    // 对鞋的成员变量个数也需要一致
    // 需要无参的构造函数  ？？ 为什么
    void writeExternal(ObjectOutPut out);

    void readExternal(ObjectInput in);
}

// 序列化反序列化示例
public class example {
    // 序列化  ObjectOutStream
    synchronized public static boolean saveObject(Object object, String path) {
        if (object == null) {
            return false;
        }

        ObjectOutStream oos = null;
        try {
            oos = new ObjectOutStream(new FileOutputStream(path));
            oos.writeObject(object);
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    // 反序列化 ObjectInputStream()
    synchronized public static <T> T readObject(String path) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(path));
            return (T) objectInputStream.readObject();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        }
        return null;
    }

}

序列化问题
        1什么时 serialVersionUID?不定义这个会发生什么?
private static final long UID=xxx;用于对象的版本控制,可以指定UID 不指定容易发生 反序列化UID不同会发生java.io.InvalidClassException
        2假设一个类 序列化并持久化存储,修改了该类添加了新的字段 对已序列化的对象反序列化 会发生什么?
        新添加的字段为默认值,UID 相同,UID不同会报错
        3不希望某些成员序列化,怎么办?
        trasient变量 瞬态,瞬态和静态变量被序列化
        4如何类中的一个成员未实现可序列化接口,会发生什么
        对象含有不可序列化的引用 会引发 NotSerializableException
        5如何类是可序列化但是父类不是,反序列化后从父类继承的实例变量的状态如何
        Java序列化仅在对象曾测都是可序列化结构中继续,继承的实例变量都将通过构造函数初始化,反序列化过程中不可序列化超级类 父类必须有一个无参数的构造函数
        6是否可以自定义序列化过程 或者是否可以覆盖java中的默认序列化过程
        两个方法
private void writeObject(ObjectOutputStream out)throws IOExecption{
        out.defaultWriteObject();
        out.writeObject(getSex());
        out.writeInt(getId());
        }
private void readObject(ObjectInputStream in)throws IOException,ClassNotFountException{
        in.defaultReadObject();
        setSex((String)in.readObject());
        setIDd(in.readInt());
        }

        7假设新类的超级类实现可序列化接口,如何避免子类被序列化
        默认会序列化子类
        子类重写上面两个方法 自定义
        8在Java中的序列化和反序列化过程中用那些方法
        主要有ObjectInputStream 和 ObjectOutpintStream 实现


        序列化流程

        ObjectOutputStream--enableOveride=false--writeObject-writeObject0(根据不同的类型进行读写)

        如果重写了上面两个方法 会反射调用上面两个方法


        枚举序列化和反序列化是同一个对象 解决单例失效的情况
        序列化只序列化枚举名字,反序列化的时候会在运行时找

        解决单例序列化失效

// 单例序列化 和 反序列化是不同的对象
class Singleton implements Serializable {
    public static Singleton Instance = new Singleton();

    private Singleton() {
    }

    // 重写 readReslove 返回之前的单例 其他read之前调用
    private Object readResolve() {
        return Instance;
    }
}

// 单例如何防止反射
class SingletonReject {
    private static boolean flag = false;

    private SingletonReject() {
        synchronized (SingletonReject.class) {
            if (!flag) {
                flag = !flag;
            } else {
                throw new RuntimeException("单例被侵犯")
            }
        }
    }
}

    Android Parcelabl
    内存中序列化 用到了 binder

        进程A 进程B
        用户空间 用户空间


        内核空间 内核空间

// 底层原理 到底怎末读的
nativeReadString -> Framework层的 Parcel.cpp -> 最终调到 readAligned()

// 使用实例
public class User implements Parcelable {

    // 反序列化
    public User(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    private String name;
    private int age;

    @Override
    public int describeContents() {
        return 0;
    }

    // 序列化
    @Override
    public void writeToParcel(@android.support.annotation.NonNull android.os.Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeIne(age);
    }

    public static final android.os.Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

Parcelabel 和 Serializable对比

Serializable    Parcelable
IO对硬盘 速度慢    直接在内存操作  效率高 性能号
大小不受限制       一般不能超过1M 修改内核也只能4M  binder可用空间
大量使用反射
产生内存碎片

1 反序列化之后的对象 会调用构造函数重构吗 不会
2 序列化和反序列化是什么 管理 == ? equals ? 枚举是 Equals
3 Android里面为什么设计Bundle而不是Map结构
    bundle内部使用的是ArrayMap 存储空间上比HashMap好
4 Android 中 Inent 和 Bundle的通信原理 和 大小限制
5 Intent为何不能直接传输 需要序列化
6 序列化和持久化的关系是什么


