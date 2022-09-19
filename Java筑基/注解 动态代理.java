//  Java 注解

@intDef (value = {IntDefTest.SUNDAY, IntDetTest.MONDAY})// 枚举 每个值都会编译成一个对象， 对象的大小 = 对象头（12字节） + 实际数据 + 对齐, 实现方法参数限制  lint语法检查
@Retention(RetentionPolicy.RUNTIME) // 保留时  SOURCE CLASS RUNTIME   Android dex class级别会被抛弃  Java 会被保留到Class 但是会被JVM 抛弃
@Target({ElementType.PARAMETER}) // 元注解 作用目标  FIELD CLASS METHOD PARAMETER
public @interface Lance {
    int a();
    String b();
}

// 注解保留时 的应用场景

源码   编译期能够获取注解与注解声明的类中所有的信息,一般1用于生成额外的辅助类. intDef APT (Gild ARouter)
字节码 字节码增强,在编译出class后, 通过修改class数据以实现修改代码逻辑目的. 对于是否需要修改代码逻辑的目的, 对于是否需要修改的区分或者修改为不同逻辑的判断可以使用注解
运行时 通过反射技术动态获取注解与其元素,从而完成不同的逻辑判定.


(source 级别)APT 注解处理器 JAVAC提供的技术

JavaC在编译的时候可以传递不同的参数
-ClassPath
-Porcessor apt.java

使用
1 创建Java模块
2 public class TestProcessor extends AbstractProcessor{
    public boolean process(Set<? extends  TypeElement> set, RuntimeEvelemt) {
        Messgae message = processingEnv.getMessager();
        message.printMessage(Diagnostic.Kind.NOTE, "");
        // 在processer中写了什么就干嘛 在Java - Class 的时候执行  这里会打印两次 为什么
        // Java 源码  解析和填充字符表  注解处理  分析与字节码生成--> 改过的文件会又从头执行一次
        return  false;
    }

    publice Set<String> getSupprotAnnotationType() {

    }
    // 或者在注解上声明 @supportAnnotationType
}
3 注册 main.resource.META-INF.service



 class 级别注解

路由表 Map<String, Class>
key = network.main
value = xxx.xxx.xxx.MainActivity.class

通过传入key 拿到value, 再通过Intent跳转

问题是 Map里面的数据怎么注册?

1. 实现一个组测IRouteLoad接口, xx.MainAcrivity 实现此接口, 调用接口提供的方法, 向接口中注册自己的信息
2. 接口内部初始化注册过来的信息, 就好像注册好SDK一样, 其他的模块直接路由表就可以拿到对用的SDK对象
3. 问题就是我加一个模块就要维护我的SDK, 手动去注册
4. 用注解处理器去维护我们的注册类, APT 通过字节自动帮我们注册.
5. 还有一个问题就是 我们得通过反射遍历所有的注册类 耗时

字节码插桩 在class中写代码

操作Class, 修改代码中的逻辑 Java有语法检查, Class 需要改对.

AOP切面编程 也是利用字节码插桩

运行时注解, 反射 动态语言的关键

反射可以修改 final 类型的变量吗

可以修改

反射为什么慢
Method.invoke(实例对象, 参数列表).

装箱拆箱的操作

遍历整个类里面的方案

检查方法参数

对动态代码进行优化 如 JIT

反射对性能的影响微乎其微

动态代理原理

静态代理 动态代理

public Interface NDK{

        }

public Lance interface  NDK {
    void ndk() {
        System.out.println("real method");
    }
}
public class ProxyInvokeHandler implements InvocationHandler {
    private Object realObject;
    public ProxyInvokeHandler(Object realObject) {
        this.realObject = realObject;
    }

    public Object invoke(Objec o, Method method, Object[] objects) throws Throwable {
        return method.invoke(realObject, objects);
    }
}

// 参数  1 类加载器 2 要代理的接口 3 回调
NDK ndk = (NDK) Proxy.newProxyInstance(lance.getClass().getClassLoader(),
        lance.getClass().getInterface(), new ProxyInvokeHandler(lance));

扩展
Retrofit Create(service.class)

public interface Service(){
    post
    put
}

Create 通过动态代理 对Service里面所有方法进行代理, 在invoke里面拿到 方法的所有注解 和 参数注解, 就知道了完整的请求URL











