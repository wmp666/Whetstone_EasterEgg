package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class WinAPIEntireFunction {

    public static final byte VK_LWIN = (byte) 0x5B;
    public static final byte VK_D = (byte) 0x44;

    public static void pressKey(byte... keyCodes) {
        //模拟按下
        for (byte keyCode: keyCodes) {
            User32.INSTANCE.keybd_event(keyCode, (byte) 0, 0x0000, 0);
        }

        // 短暂延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //模拟松开
        for (byte keyCode : keyCodes) {
            User32.INSTANCE.keybd_event(keyCode, (byte) 0, 0x0002, 0);
        }
    }



    // 光栅操作码
    public static final int DSTINVERT = 0x00550009;  // 反转目标矩形

    public static void invertScreenWithJNA() throws InterruptedException {
        // 获取桌面窗口句柄
        WinDef.HWND desktop = com.sun.jna.platform.win32.User32.INSTANCE.GetDesktopWindow();

        // 获取屏幕设备上下文
        WinDef.HDC hdc = com.sun.jna.platform.win32.User32.INSTANCE.GetDC(desktop);

        // 获取屏幕尺寸
        int width = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(
                com.sun.jna.platform.win32.User32.SM_CXSCREEN);
        int height = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(
                com.sun.jna.platform.win32.User32.SM_CYSCREEN);

        // 使用PatBlt反转整个屏幕
        GDI32.INSTANCE.PatBlt(hdc, 0, 0, width, height, DSTINVERT);

        Thread.sleep(3000);
    }

    public static void clearInvertScreen() {
        // 获取桌面窗口句柄
        WinDef.HWND desktop = com.sun.jna.platform.win32.User32.INSTANCE.GetDesktopWindow();

        // 获取屏幕设备上下文
        WinDef.HDC hdc = com.sun.jna.platform.win32.User32.INSTANCE.GetDC(desktop);


        // 清理资源
        com.sun.jna.platform.win32.User32.INSTANCE.ReleaseDC(desktop, hdc);
    }

    // 定义访问权限 (PROCESS_TERMINATE = 0x0001)
    private static final int PROCESS_TERMINATE = 0x0001;
    // 定义快照标志 (TH32CS_SNAPPROCESS = 0x00000002)
    private static final WinDef.DWORD TH32CS_SNAPPROCESS = new WinDef.DWORD(0x00000002);

    public static void killProcess(String processName, int exitCode) {
        WinNT.HANDLE snapShot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        if (snapShot == null || snapShot.getPointer() == Pointer.NULL) {
            System.err.println("创建快照失败");
            return;
        }

        try {
            Tlhelp32.PROCESSENTRY32 processEntry = new Tlhelp32.PROCESSENTRY32();
            processEntry.dwSize = new WinDef.DWORD(processEntry.size());

            // 2. 获取第一个进程
            if (Kernel32.INSTANCE.Process32First(snapShot, processEntry)) {
                do {
                    // 提取进程名 (去掉结尾的空字符)
                    String currentName = Native.toString(processEntry.szExeFile);
                    if (currentName.equalsIgnoreCase(processName)) {
                        WinDef.DWORD pid = processEntry.th32ProcessID;
                        System.out.println("找到进程: " + currentName + " (PID: " + pid + ")");

                        // 3. 打开进程获取句柄
                        WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(PROCESS_TERMINATE, false, pid.intValue());
                        if (processHandle != null && processHandle.getPointer() != Pointer.NULL) {
                            // 4. 结束进程
                            boolean result = Kernel32.INSTANCE.TerminateProcess(processHandle, exitCode);
                            if (result) {
                                System.out.println("成功结束进程: " + currentName);
                            } else {
                                int err = Native.getLastError();
                                System.err.println("结束进程失败, 错误码: " + err);
                            }
                            Kernel32.INSTANCE.CloseHandle(processHandle);
                        } else {
                            System.err.println("无法打开进程, 可能权限不足, PID: " + pid);
                        }
                        // 注意：如果你想结束所有匹配的进程，不要 break；
                        // 如果只想结束第一个找到的，取消下面的注释
                        // break;
                    }
                } while (Kernel32.INSTANCE.Process32Next(snapShot, processEntry));
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(snapShot);
        }
    }
}
