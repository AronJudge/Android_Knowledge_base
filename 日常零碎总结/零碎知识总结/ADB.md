adb获取当前打开的app的包名

adb shell dumpsys window | grep "mCurrentFocus"

adb获取Setting数据库

adb shell settings get system KEY——NAME