# 判断应用的Activity是否在前台

```java
package com.ts.voiceactuatorservice.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * ClassName AppOnForegroundUtil
 * Description TODO
 *
 * @Author liu wei
 * @Date 2022/10/13 下午4:18
 * @Version 1.0
 **/
public class AppOnForegroundUtil {

    private final ActivityManager mActivityManager;
    private final Context mContext;

    public AppOnForegroundUtil(Context context) {
        mContext = context;
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public boolean isAppOnForeground(String packageName) {
        //Get all running apps in Android device
        List<ActivityManager.RunningAppProcessInfo> appProcesses = mActivityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance
                    == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && isActivityToForeground(packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isActivityToForeground(String packageName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}

```


## 怎么把栈顶的Activity移除

adb shell input keyevent 4 模拟返回

```html
每个数字与keycode对应表如下：
0 -->  "KEYCODE_UNKNOWN"
1 -->  "KEYCODE_MENU"
2 -->  "KEYCODE_SOFT_RIGHT"
3 -->  "KEYCODE_HOME"
4 -->  "KEYCODE_BACK"
5 -->  "KEYCODE_CALL"
6 -->  "KEYCODE_ENDCALL"
7 -->  "KEYCODE_0"
8 -->  "KEYCODE_1"
9 -->  "KEYCODE_2"
10 -->  "KEYCODE_3"
11 -->  "KEYCODE_4"
12 -->  "KEYCODE_5"
13 -->  "KEYCODE_6"
14 -->  "KEYCODE_7"
15 -->  "KEYCODE_8"
16 -->  "KEYCODE_9"
17 -->  "KEYCODE_STAR"
18 -->  "KEYCODE_POUND"
19 -->  "KEYCODE_DPAD_UP"
20 -->  "KEYCODE_DPAD_DOWN"
21 -->  "KEYCODE_DPAD_LEFT"
22 -->  "KEYCODE_DPAD_RIGHT"
23 -->  "KEYCODE_DPAD_CENTER"
24 -->  "KEYCODE_VOLUME_UP"
25 -->  "KEYCODE_VOLUME_DOWN"
26 -->  "KEYCODE_POWER"
27 -->  "KEYCODE_CAMERA"
28 -->  "KEYCODE_CLEAR"
29 -->  "KEYCODE_A"
30 -->  "KEYCODE_B"
31 -->  "KEYCODE_C"
32 -->  "KEYCODE_D"
33 -->  "KEYCODE_E"
34 -->  "KEYCODE_F"
35 -->  "KEYCODE_G"
36 -->  "KEYCODE_H"
37 -->  "KEYCODE_I"
38 -->  "KEYCODE_J"
39 -->  "KEYCODE_K"
40 -->  "KEYCODE_L"
41 -->  "KEYCODE_M"
42 -->  "KEYCODE_N"
43 -->  "KEYCODE_O"
44 -->  "KEYCODE_P"
45 -->  "KEYCODE_Q"
46 -->  "KEYCODE_R"
47 -->  "KEYCODE_S"
48 -->  "KEYCODE_T"
49 -->  "KEYCODE_U"
50 -->  "KEYCODE_V"
51 -->  "KEYCODE_W"
52 -->  "KEYCODE_X"
53 -->  "KEYCODE_Y"
54 -->  "KEYCODE_Z"
55 -->  "KEYCODE_COMMA"
56 -->  "KEYCODE_PERIOD"
57 -->  "KEYCODE_ALT_LEFT"
58 -->  "KEYCODE_ALT_RIGHT"
59 -->  "KEYCODE_SHIFT_LEFT"
60 -->  "KEYCODE_SHIFT_RIGHT"
61 -->  "KEYCODE_TAB"
62 -->  "KEYCODE_SPACE"
63 -->  "KEYCODE_SYM"
64 -->  "KEYCODE_EXPLORER"
65 -->  "KEYCODE_ENVELOPE"
66 -->  "KEYCODE_ENTER"
67 -->  "KEYCODE_DEL"
68 -->  "KEYCODE_GRAVE"
69 -->  "KEYCODE_MINUS"
70 -->  "KEYCODE_EQUALS"
71 -->  "KEYCODE_LEFT_BRACKET"
72 -->  "KEYCODE_RIGHT_BRACKET"
73 -->  "KEYCODE_BACKSLASH"
74 -->  "KEYCODE_SEMICOLON"
75 -->  "KEYCODE_APOSTROPHE"
76 -->  "KEYCODE_SLASH"
77 -->  "KEYCODE_AT"
78 -->  "KEYCODE_NUM"
79 -->  "KEYCODE_HEADSETHOOK"
80 -->  "KEYCODE_FOCUS"
81 -->  "KEYCODE_PLUS"
82 -->  "KEYCODE_MENU"
83 -->  "KEYCODE_NOTIFICATION"
84 -->  "KEYCODE_SEARCH"
85 -->  "TAG_LAST_KEYCODE"

电话键
KEYCODE_CALL	拨号键	5
KEYCODE_ENDCALL	挂机键	6
KEYCODE_HOME	按键Home	3
KEYCODE_MENU	菜单键	82
KEYCODE_BACK	返回键	4
KEYCODE_SEARCH	搜索键	84
KEYCODE_CAMERA	拍照键	27
KEYCODE_FOCUS	拍照对焦键	80
KEYCODE_POWER	电源键	26
KEYCODE_NOTIFICATION	通知键	83
KEYCODE_MUTE	话筒静音键	91
KEYCODE_VOLUME_MUTE	扬声器静音键	164
KEYCODE_VOLUME_UP	音量增加键	24
KEYCODE_VOLUME_DOWN	音量减小键	25



控制键
KEYCODE_ENTER	回车键	66
KEYCODE_ESCAPE	ESC键	111
KEYCODE_DPAD_CENTER	导航键 确定键	23
KEYCODE_DPAD_UP	导航键 向上	19
KEYCODE_DPAD_DOWN	导航键 向下	20
KEYCODE_DPAD_LEFT	导航键 向左	21
KEYCODE_DPAD_RIGHT	导航键 向右	22
KEYCODE_MOVE_HOME	光标移动到开始键	122
KEYCODE_MOVE_END	光标移动到末尾键	123
KEYCODE_PAGE_UP	向上翻页键	92
KEYCODE_PAGE_DOWN	向下翻页键	93
KEYCODE_DEL	退格键	67
KEYCODE_FORWARD_DEL	删除键	112
KEYCODE_INSERT	插入键	124
KEYCODE_TAB	Tab键	61
KEYCODE_NUM_LOCK	小键盘锁	143
KEYCODE_CAPS_LOCK	大写锁定键	115
KEYCODE_BREAK	Break/Pause键	121
KEYCODE_SCROLL_LOCK	滚动锁定键	116
KEYCODE_ZOOM_IN	放大键	168
KEYCODE_ZOOM_OUT	缩小键	169

组合键
KEYCODE_ALT_LEFT	Alt+Left
KEYCODE_ALT_RIGHT	Alt+Right
KEYCODE_CTRL_LEFT	Control+Left
KEYCODE_CTRL_RIGHT	Control+Right
KEYCODE_SHIFT_LEFT	Shift+Left
KEYCODE_SHIFT_RIGHT	Shift+Right

按键
KEYCODE_0	按键'0'	7
KEYCODE_1	按键'1'	8
KEYCODE_2	按键'2'	9
KEYCODE_3	按键'3'	10
KEYCODE_4	按键'4'	11
KEYCODE_5	按键'5'	12
KEYCODE_6	按键'6'	13
KEYCODE_7	按键'7'	14
KEYCODE_8	按键'8'	15
KEYCODE_9	按键'9'	16
KEYCODE_A	按键'A'	29
KEYCODE_B	按键'B'	30
KEYCODE_C	按键'C'	31
KEYCODE_D	按键'D'	32
KEYCODE_E	按键'E'	33
KEYCODE_F	按键'F'	34
KEYCODE_G	按键'G'	35
KEYCODE_H	按键'H'	36
KEYCODE_I	按键'I'	37
KEYCODE_J	按键'J'	38
KEYCODE_K	按键'K'	39
KEYCODE_L	按键'L'	40
KEYCODE_M	按键'M'	41
KEYCODE_N	按键'N'	42
KEYCODE_O	按键'O'	43
KEYCODE_P	按键'P'	44
KEYCODE_Q	按键'Q'	45
KEYCODE_R	按键'R'	46
KEYCODE_S	按键'S'	47
KEYCODE_T	按键'T'	48
KEYCODE_U	按键'U'	49
KEYCODE_V	按键'V'	50
KEYCODE_W	按键'W'	51
KEYCODE_X	按键'X'	52
KEYCODE_Y	按键'Y'	53
KEYCODE_Z	按键'Z'	54

多媒体键
KEYCODE_MEDIA_PLAY	多媒体键 播放
KEYCODE_MEDIA_STOP	多媒体键 停止
KEYCODE_MEDIA_PAUSE	多媒体键 暂停
KEYCODE_MEDIA_PLAY_PAUSE	多媒体键 播放/暂停
KEYCODE_MEDIA_FAST_FORWARD	多媒体键 快进
KEYCODE_MEDIA_REWIND	多媒体键 快退
KEYCODE_MEDIA_NEXT	多媒体键 下一首
KEYCODE_MEDIA_PREVIOUS	多媒体键 上一首
KEYCODE_MEDIA_CLOSE	多媒体键 关闭
KEYCODE_MEDIA_EJECT	多媒体键 弹出
KEYCODE_MEDIA_RECORD	多媒体键 录音

KEYCODE_F1	按键F1
KEYCODE_F2	按键F2
KEYCODE_F3	按键F3
KEYCODE_F4	按键F4
KEYCODE_F5	按键F5
KEYCODE_F6	按键F6
KEYCODE_F7	按键F7
KEYCODE_F8	按键F8
KEYCODE_F9	按键F9
KEYCODE_F10	按键F10
KEYCODE_F11	按键F11
KEYCODE_F12	按键F12

KEYCODE_PLUS	按键'+'
KEYCODE_MINUS	按键'-'
KEYCODE_STAR	按键'*'
KEYCODE_SLASH	按键'/'
KEYCODE_EQUALS	按键'='
KEYCODE_AT	按键'@'
KEYCODE_POUND	按键'#'
KEYCODE_APOSTROPHE	按键''' (单引号)
KEYCODE_BACKSLASH	按键'\'
KEYCODE_COMMA	按键','
KEYCODE_PERIOD	按键'.'
KEYCODE_LEFT_BRACKET	按键'['
KEYCODE_RIGHT_BRACKET	按键']'
KEYCODE_SEMICOLON	按键';'
KEYCODE_GRAVE	按键'`'
KEYCODE_SPACE	空格键

KEYCODE_NUMPAD_0	小键盘按键'0'
KEYCODE_NUMPAD_1	小键盘按键'1'
KEYCODE_NUMPAD_2	小键盘按键'2'
KEYCODE_NUMPAD_3	小键盘按键'3'
KEYCODE_NUMPAD_4	小键盘按键'4'
KEYCODE_NUMPAD_5	小键盘按键'5'
KEYCODE_NUMPAD_6	小键盘按键'6'
KEYCODE_NUMPAD_7	小键盘按键'7'
KEYCODE_NUMPAD_8	小键盘按键'8'
KEYCODE_NUMPAD_9	小键盘按键'9'
KEYCODE_NUMPAD_ADD	小键盘按键'+'
KEYCODE_NUMPAD_SUBTRACT	小键盘按键'-'
KEYCODE_NUMPAD_MULTIPLY	小键盘按键'*'
KEYCODE_NUMPAD_DIVIDE	小键盘按键'/'
KEYCODE_NUMPAD_EQUALS	小键盘按键'='
KEYCODE_NUMPAD_COMMA	小键盘按键','
KEYCODE_NUMPAD_DOT	小键盘按键'.'
KEYCODE_NUMPAD_LEFT_PAREN	小键盘按键'('
KEYCODE_NUMPAD_RIGHT_PAREN	小键盘按键')'
KEYCODE_NUMPAD_ENTER	小键盘按键回车



```
