# Rect

这是一个我们常用的一个“绘画相关的工具类”，常用语描述长方形/正方形，他只有4个属性

public int left;

public int top;

public int right;

public int bottom;

这4个属性描述着这一个“方块”，但是这有一个知识点需要理清楚，先看这张图

![Rect](Image/img_26.png)


本Rect最左侧到屏幕的左侧的距离是 left
本Rect最上面到屏幕上方的距离是 top
本Rect最右侧到屏幕左侧的距离是 right
本Rect最下面到屏幕上方的距离是 bottom

这四个属性不单单描述了这个 长方形4个点的坐标，间接的描述出这个长方形的尺寸

长 = bottom - top
宽 = right - left

构造函数

```java
public class Rect {
    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    
    public Rect(Rect r) {
        if (r == null) {
            left = top = right = bottom = 0;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }
}

```

3个构造函数都是围绕着初始化这4个属性来做的，无论是传过来一个新Rect对象，还是传入具体的尺寸。