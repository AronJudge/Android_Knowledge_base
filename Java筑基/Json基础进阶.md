Json是什么 数据交换格式

轻量级的数据格式

Json  <- Gson/fastJson -> javaObject 

```java
public class JsonAdapterTest {
    public static void main(String[] args) {
        
        Gson gson = new GsonBuilder()
                .setDataFromat()
                .setVersion()
                .setPrettyPrinting()
                .setAdapter();
        
        Gson gson = new Gson();
        User user = new User("gg", "Zero");
        String gsonStr = gson.toJson(user);
        
        user = gson.fromJson(gsonStr, User.class);
    }
}

```

GSON 默认是通过反射解析的
我们也可以自定义一个Adapter类 重写Write 和 read 方法 来实现解析Json

自定义TypeAdapter
1. JsonWrite
2. JsonRead
3. TypeAdapter

JsonDeseriaLizer
JsonElement

Gson是如何解析的
编译原理 
1 分割 语法分析 检查  词性分析 - > tree 

Json的 JsonObject对象 可以堪称name values的集合 

输入Json字符串 
---> Type  基本类型  创建对应的 Typeadatter读取Json串
            对象 创建 ReflectiveTypeAdapter - 遍历对象属性 重复上述步骤

Json适配器模式

把一个类型的东西 加上不同的功能 进行适配

Json解析 全靠 TypeAdapter再 Json和 bean中进行转换  
api
1. write 
2. read
3. nullSafe 返回TypeAdapter

```java
import java.lang.reflect.Type;

public abstract class TypeAdapter {
}

// 任何一种类型都继承与TypeAdapter StringTypeAdapter IntegerTypeAdapter 
// 每种类型都又对象的TypeAdapter  一些基本的数量类型可以提前定义
// 自定义的类 可以使用自己定义的adapter  那每种类型都定义一种Adapter 太麻烦 他会默认使用反射 
// 通过TypeAdapterFactory创建
public interface TypeAdapterFactory {
    // 根据不同类型创建TypeAdapter
    // TypeToken 
    <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type);
}

// 获取T的类型
public class TypeToken<T> {
    final Class<? extends T> rawType;// T 的原始类型
    final Type type; // T 的类型
    final int hashCode;

    protected TypeToken() {
        // 获取父类的参数类型
        this.type = getSuperClassTypeParameter(getClass());
        this.rawType = (Class<? extends T>) 


    }
}
```

排除器 Excluder 
Gson 是门面模式 封装了 TypeAdaper等细节

TypeAdapter List

总体流程
JsonStr - 排除器 - 自定义的 TypeAdapter - gson自带的TypeAdapter - 
    ReflectiveTypeAdapter 反射通过工厂创建TypeAdapter进行解析 - 
        getBountFields 拿到bean类的所有字段 创建好Adapter 后 调用 read方法 
            解析 基本类型直接读  对象 要分解再读 根据Type类型 创建Adapter 递归解析



