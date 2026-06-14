package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface User32 extends Library {
    User32 INSTANCE = Native.load(Platform.isWindows() ? "user32" : "c", User32.class);
    // 定义用于操作窗口的函数
    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    short GetAsyncKeyState(int vKey);

}
