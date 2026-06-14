package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.ArrayList;
import java.util.List;

public class DesktopAppEnumerator {

    private static final List<WindowInfo> windowList = new ArrayList<>();
    // 1. 定义 User32 接口
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer userData);
        int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
        boolean IsWindowVisible(WinDef.HWND hWnd);
        int GetClassNameW(WinDef.HWND hWnd, char[] lpClassName, int nMaxCount);
    }

    // 2. 存储窗口信息的简单类
    public static class WindowInfo {
        public final WinDef.HWND hwnd;
        public final String title;
        public final String className;
        public WindowInfo(WinDef.HWND hwnd, String title, String className) {
            this.hwnd = hwnd; this.title = title; this.className = className;
        }
        @Override public String toString() { return String.format("标题: %s | 类名: %s", title, className); }
    }

    // 3. 枚举回调函数
    private static final WinUser.WNDENUMPROC enumProc = (hWnd, arg1) -> {
        if (!User32.INSTANCE.IsWindowVisible(hWnd)) { return true; } // 跳过不可见窗口

        char[] titleBuffer = new char[512];
        char[] classBuffer = new char[256];
        User32.INSTANCE.GetWindowTextW(hWnd, titleBuffer, titleBuffer.length);
        User32.INSTANCE.GetClassNameW(hWnd, classBuffer, classBuffer.length);

        String title = Native.toString(titleBuffer);
        String className = Native.toString(classBuffer);

        // 4. 基础过滤（可根据需要调整）
        if (!title.isEmpty() && !className.isEmpty()) {
            // 过滤掉一些已知的系统窗口类，如任务栏、桌面等
            //if (!className.equals("Shell_TrayWnd") && !className.equals("Progman")) {
                windowList.add(new WindowInfo(hWnd, title, className));
            //}
        }
        return true; // 继续枚举
    };



    public static List<WindowInfo> getVisibleWindows() {
        windowList.clear();
        User32.INSTANCE.EnumWindows(enumProc, null);
        return new ArrayList<>(windowList);
    }

    public static void main(String[] args) {
        List<WindowInfo> apps = getVisibleWindows();
        System.out.println("当前桌面应用窗口 (" + apps.size() + " 个):");
        for (WindowInfo info : apps) { System.out.println("  - " + info); }
    }
}
