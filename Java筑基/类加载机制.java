虚拟机和类机制机制

JVM 和 Dalvik
Dalvik 虚拟机和 Java虚拟机有差不多的特性, 两只的指令集不一样 前者的指令集是基于寄存器 后者的指令集是基于栈

栈的虚拟机
每一个运行时的线程 都有一个独立的栈, 栈中记录了方法调用的历史, 每个方法时一个栈帧, 栈顶是当前执行的方法
寄存器的虚拟机
寄存器是有限的存储容量的告诉存储不见 暂存指令 地址 和 数据
数据寄存器
指令寄存器
地址寄存器
ALU 算数逻辑单元

基于寄存器的虚拟机 指令数少了  数据移动次数也少了

Art 和 Dalvik的区别
Dalvik
解释执行 和 JIT 即时编译 在安装的时候会执行Dex Opt的操作 生成Odex文件

ART 是在 Android4.4 引入  执行本地机器码
dex2oat 预处理 预先编译机制 编译成本地机器码  -> 混编   应用安装不 AOT了, 对经常执行的带啊吗JIT, JIT 编译的方法记录到profile配置文件中
对配置文件中的代码进行AOT 编译

类加载
public abstract class ClassLoader {

    static private class SystemClassLoader {
        public static ClassLoader loader = ClassLoader.createSystemClassLoader();
    }
}
                    ClassLoader

        BootClassLoader     DexBassClassLoader   DexPathList
        framework文件
                    pathClassLoader        DexClassLoader 额外提供的动态加载器
                    Android应用程序加载器

// 双亲委派机制
pathClassLoader
        BassClassLoader
                ClassLoader
                    loadClass(className, boolean reslove)
                         // 找缓存
                        Class = findLoadedClass(className) -> JNI
                        Class == null {
                                parent.loadClass(className, false) // pathClassLoader 的 parent是 BootClassLoader
                            }
                        Class == null {
                            findClass(className) - DexBassClassLoader(DexpathLast.findClass(name))
                            // 构造方法实例化 pathList =  new DexPathList(this, dexPath,)
                            // Elemet[] dexElement =  makePathElement(dexPath) 分割出list集合
                            // findClass{
                            // for (Element dexFile : dexElement)  dexFile.loadClassBinaryName(name)
                            // }
                            }
                        return Class
这样设计的意义
1. 避免重复的加载
2. 安全 防止核心API被篡改

dex插桩 热修复原理

主要是上面提到的DexElements数组
class0.dex  Class1.dex  class2.dex   class3.dex

热修复方案的 前提 就是这个类没有被加载过   用这个类之前修复掉
Demo 补丁包 - dex文件 把他插在dexElement 数组的最前面
因为加载是for循环 插在最前面 就加载了

流程
1 获取PathClassLoader
2 反射获得 DexpathList属性对象 pathList
3 反射修改 pathList属性对象 dexElemetnts
    1. 把补丁包path.dex 转化未 Element[](patch)
    2. 获得pathList的dexElements属性old
    3. path + old 合并, 并反射赋值给pathList的dexElements


