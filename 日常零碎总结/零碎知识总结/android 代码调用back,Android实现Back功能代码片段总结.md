实现Back键功能方法有：

一：重写onBackPressed方法

@Override

public void onBackPressed() {

// do something what you want

super.onBackPressed();

}

二：使用测试框架Instrumentation，模拟任意键按下动作，注意的是该方法不能在主线程中使用，只能开启新线程，带来的问题就是反应速度较慢，项目中不建议使用。

调用onBack()方法;产生back键单击效果

public void onBack(){

new Thread(){

public void run() {

try{

Instrumentation inst = new Instrumentation();

inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

}

catch (Exception e) {

Log.e("Exception when onBack", e.toString());

}

}

}.start();

}

三：此方法是网络上搜集的，没有代码验证。

try{

Runtime runtime=Runtime.getRuntime();

runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);

}catch(IOException e){

Log.e("Exception when doBack", e.toString());

}

四：重写dispatchKeyEvent

@Override

public boolean dispatchKeyEvent(KeyEvent event) {

// TODO Auto-generated method stub

if (event.getAction() == KeyEvent.ACTION_DOWN

&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

//do something what you want

return true;//返回true，把事件消费掉，不会继续调用onBackPressed

}

return super.dispatchKeyEvent(event);

}

五：这个方法算不上是完全意义的Back键的功能了，此方法只能关闭当前的 Activity ，也就是对于一个只有单个 Activity 的应用程序有效，如果对于有多外 Activity 的应用程序它就无能为力了。

public void exitProgrames(){

android.os.Process.killProcess(android.os.Process.myPid());

}

使用此方法需要追加权限：