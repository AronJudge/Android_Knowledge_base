## Android Bitmap详解


## 一、Bitmap
Bitmap是Android系统中图像处理的最重要类之一。用它可以获取图像信息，对图像进行剪切、旋转、缩放等操作，并可以指定格式保存图像文件。

### 常用方法：
public int getDensity()：获取图片的像素密度

public void reconfigure(int width, int height, Config config)：重新设置图片的宽高及解码格式

public void setWidth(int width)

public void setHeight(int height)

public void setConfig(Config config)

public void recycle()：回收位图占用的内存空间，把位图标记为Dead

public final boolean isRecycled()：判断位图内存是否已释放

public int getGenerationId()：位图的id，当图片改变id也随之发生变化，可以用来判断图片是否发生变化

public Bitmap copy(Config config, boolean isMutable)：根据参数设置拷贝一个相应的新的bitmap

public static Bitmap createScaledBitmap(@NonNull Bitmap src, int dstWidth, int dstHeight, boolean filter)：如果进行了缩放，返回一个新的bitmap，没有缩放，返回原bitmap src。

filter：是否使用双线性滤波进行缩放。默认值true，表示使用，以更差的性能获取更好的图片质量；false表示不使用，会使用临近缩放代替，会有较差的图像质量，但更快。一般使用默认值true，因为双线性滤波的成本通常是最小的，改善的图像质量是显著的。
public static Bitmap createBitmap(@NonNull Bitmap src)：以src为原图生成不可变得新图像。

public static Bitmap createBitmap(@NonNull Bitmap source, int x, int y, int width, int height, @Nullable Matrix m, boolean filter)：以source为原图，创建新的图片，指定起始坐标以及新图像的高宽，选择矩阵，是否可过滤（filter为true只适用于矩阵包含不止平移的情况。）

public static Bitmap createBitmap(int width, int height, @NonNull Config config)：创建指定格式、大小的位图

public boolean compress(CompressFormat format, int quality, OutputStream stream)：运行于工作线程，按指定的图片格式以及画质，将图片转换为输出流。

format：压缩图像的格式,如Bitmap.CompressFormat.PNG或Bitmap.CompressFormat.JPEG等。
quality：画质。取值范围0-100，100表示最高画质压缩，即不压缩。对于PNG等无损格式的图片，会忽略此项设置。
stream：OutputStream中写入压缩数据。
return: 是否成功压缩到指定的流。
public final boolean isMutable()： 图片是否可修改

public final int getWidth()

public final int getHeight()

public int getScaledWidth(int targetDensity)：获取指定密度转换后的图像的宽度。

public int getScaledHeight(int targetDensity)：获取指定密度转换后的图像的高度。

public final int getRowBytes()：返回位图像素的行与行之间的字节数。

public final int getByteCount()：返回可用于存储该位图像素的最小字节数。

public final int getAllocationByteCount()：返回用于存储该位图像素的已分配内存的大小。如果一个位图被重用来解码其他较小的位图，或者通过手动重新配置，这个值可以大于byteCount的结果。如果没有修改图片的尺寸和图片格式Bitmap.Config，这个值和byteCount结果一样。

public final boolean hasAlpha()：返回位图是否支持像素透明。

public final ColorSpace getColorSpace()：返回与此位图相关联的颜色空间。

public void eraseColor(@ColorLong long color)：用指定颜色填充位图像素。

public Color getColor(int x, int y)：获取指定位置的颜色

public int getPixel(int x, int y)：获取指定位置的颜色

public boolean sameAs(Bitmap other)：判断两个图片是否是相同的dimensions, config and pixel data

public void prepareToDraw()：生成与用于绘制位图的位图相关联的缓存。

二、BitmapFactory工厂类：
Option参数类属性：
以in开头的都是设置属性值，以out开头的都是获取属性值。

public Bitmap inBitmap：主要作用是复用之前bitmap在内存中申请的内存，使用的是对象池的原理，以解决对象频繁创建再回收的效率问题。在SDK11之前不可使用；11-18之间，重用的bitmap大小必须一致；从19开始，新申请的bitmap大小必须小于等于已经赋值过的bitmap大小。且解码格式必须相同。

public boolean inMutable：设置位图可变性

public boolean inJustDecodeBounds：该属性设置为true的时候，表示BitmapFactory通过decodeResource等方式解码图片时，会返回空的bitmap对象，只包含图片的附属信息，对于只需要获取图片的附加信息（如图片宽高等）的情况下，使用该参数不会为bitmap分配内存大小；默认该属性值为false，会正常解码该图片，为图片分配内存空间。这个属性的目的是，如果你只想知道一个bitmap的尺寸，但又不想将其加载到内存时，这将是一个非常有用的属性。

public int inSampleSize：图片缩放的倍数。

public int inDensity：用于位图的像素压缩比。

public int inTargetDensity：用于目标位图的像素压缩比（要生成的位图）

public int inScreenDensity：实际使用的屏幕的像素密度。

public boolean inScaled：设置为true时进行图片压缩，从inDensity到inTargetDensity。

public int outWidth：获取图片的宽度值

public int outHeight：获取图片的高度值（表示这个Bitmap的宽和高，一般和inJustDecodeBounds一起使用来获得Bitmap的宽高，但是不加载到内存。）

public String outMimeType：获取解码后图像的minetype。

public Bitmap.Config outConfig：获取图像解码格式

工厂方法：
public static Bitmap decodeFile(String pathName, Options opts)：从文件读取图片

public static Bitmap decodeResourceStream(@Nullable Resources res, @Nullable TypedValue value, @Nullable InputStream is, @Nullable Rect pad, @Nullable Options opts)：从输入流中解码一个新的位图。这个InputStream是从resources中获取的，同时可以相应地缩放位图。

public static Bitmap decodeResource(Resources res, int id, Options opts)：从资源文件读取图片

public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts)：从字节数组读取图片

public static Bitmap decodeStream(@Nullable InputStream is, @Nullable Rect outPadding, @Nullable Options opts)：从输入流读取图片

public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, Options opts)：从文件读取文件，与decodeFile不同的是这个直接调用JNI函数进行读取效率比较高。

### 三、图片的加载方式
Bitmap的加载获取方式主要有有从Resource资源加载、本地SD卡加载及网络加载等方式。

1.从Resource资源加载图片

方式1：从drawable或mipmap中读取图片
BitmapFactory.decodeResource(resources, R.mipmap.drawable_bg)
方式2：从raw中读取图片
BitmapFactory.decodeStream(resources.openRawResource(R.raw.drawable_bg))
方式3：从assets中读取图片
BitmapFactory.decodeStream(resources.assets.open("drawable_bg.jpeg"))
方式4：从byte[]读取图片
val inputStream = resources.assets.open("drawable_bg.jpeg")
val bytes = inputStream.readBytes()
return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
使用BitmapFactory.decodeResource方式加载图片如果要经过缩放，该缩放是在java层进行的，效率比较低，会消耗java层的内存，因此如果大量使用会导致OOM。

BitmapFactory.decodeStream一般用于二进制文件图片的读取。

2.从本地SD卡加载图片
方式1：通过decodeFile方式加载图片
BitmapFactory.decodeFile(path)
方式2：通过decodeFileDescriptor方式加载图片（效率高于方式1）
private fun getBitmapFromFile(path: String): Bitmap {
val fileInputStream = FileInputStream(path)
return BitmapFactory.decodeFileDescriptor(fileInputStream.fd)
}
3.从网络加载图片
从网络加载图片本质上也是从网络读取图片数据流，通过BitmapFactory.decodeStream方式加载图片。

### 四、Bitmap|Drawable|InputStream|Byte[]之间的相互转化
1.Bitmap转Drawable
private fun bitmap2Drawable(bitmap: Bitmap): Drawable {
return BitmapDrawable(resources, bitmap)
}
2.Drawable转Bitmap
private fun drawable2Bitmap(drawable: Drawable): Bitmap {
val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, if(drawable.alpha == 255) Bitmap.Config.RGB_565 else Bitmap.Config.ARGB_8888)
val canvas = Canvas(bitmap)
drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
drawable.draw(canvas)
return bitmap
}
3.Bitmap转byte[]
private fun bitmap2Byte(bitmap: Bitmap) : ByteArray {
val baos = ByteArrayOutputStream()
bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
return baos.toByteArray()
}
4.byte[]转Bitmap
private fun byte2Bitmap(byteArray: ByteArray): Bitmap {
return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
5.byte[]转InputStream
private fun byte2Stream(byteArray: ByteArray): InputStream {
return ByteArrayInputStream(byteArray)
}
6.InputStream转byte[]
private fun stream2Byte(inStream: InputStream) : ByteArray {
val baos = ByteArrayOutputStream()
val byte = ByteArray(2 * 1024)
var len = 0
while (inStream.read(byte, 0, byte.size).also { len = it } != -1) {
baos.write(byte, 0, len)
baos.flush()
}
return baos.toByteArray()
}
7.InputStream转Bitmap
private fun stream2Bitmap(inStream: InputStream) : Bitmap {
return BitmapFactory.decodeStream(inStream)
}
8.Bitmap转InputStream
private fun bitmap2Stream(bitmap: Bitmap) : InputStream {
val baos = ByteArrayOutputStream()
bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
return ByteArrayInputStream(baos.toByteArray())
}
其他的转换就不一一列举了，不能直接转换的可以间接进行转换。

五、Bitmap的一些其他操作
1.设置图片的旋转角度
在拍照上传图片过程中，我们可能会遇到上传的图片被旋转了，需要给他复原进行旋转。

public Bitmap rotaingImageView(int angle , Bitmap bitmap) {
if(angle > 0) {
//旋转图片 动作
Matrix matrix = new Matrix();
matrix.postRotate(angle);
// 创建新的图片
return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
}else {
return bitmap;
}
}
获取图片的旋转角度：

public int readPictureDegree(String path){
int degree  = 0;
try {
ExifInterface exifInterface = new ExifInterface(path);
int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
switch (orientation) {
case ExifInterface.ORIENTATION_ROTATE_90:
degree = 90;
break;
case ExifInterface.ORIENTATION_ROTATE_180:
degree = 180;
break;
case ExifInterface.ORIENTATION_ROTATE_270:
degree = 270;
break;
}
} catch (IOException e) {
e.printStackTrace();
}
return degree;
}
2.根据View控件获取Bitmap
public static Bitmap getBitmapFromView(View view){
Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
Canvas canvas = new Canvas(returnedBitmap);
Drawable bgDrawable = view.getBackground();
if (bgDrawable != null)
bgDrawable.draw(canvas);
else
canvas.drawColor(Color.WHITE);
view.draw(canvas);
return returnedBitmap;
}
public static Bitmap convertViewToBitMap(View view){
// 打开图像缓存
view.setDrawingCacheEnabled(true);
// 必须调用measure和layout方法才能成功保存可视组件的截图到png图像文件
// 测量View大小
view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
// 发送位置和尺寸到View及其所有的子View
view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
// 获得可视组件的截图
Bitmap bitmap = view.getDrawingCache();
return bitmap;
}