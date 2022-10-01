# View的绘制流程

view是什么时候添加到屏幕上的？
setContentView = 创建DecorView xml添加到DecorView上面, 但是还没有绘制到屏幕上去

来看个实例, 请问那些能拿到 高度值
```java
public class MainActivity extends AppCompatActvity {
    private static final String TAG = "leo";
    parvate TextView mTextView;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCtreate();
        setContectView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv);

        android.util.Log.e(TAG, "height1 = ", + mTextView.getMeasureHeight());
        
        // handler 取消息 取到的时候测量已经完成了
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                android.util.Log.e(TAG, "height2 = ", + mTextView.getMeasureHeight());
            }
        });
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.e(TAG, "height3 = ", + mTextView.getMeasureHeight());
    }
    
}

```

实际只有 Height2 可以拿到 值
