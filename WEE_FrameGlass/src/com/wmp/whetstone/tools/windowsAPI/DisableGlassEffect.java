package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

public class DisableGlassEffect {

    // ====================== 1. 定义DWM API接口 ======================
    public interface Dwmapi extends com.sun.jna.Library {
        Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);

        // 核心：禁用模糊背景
        int DwmEnableBlurBehindWindow(WinDef.HWND hWnd, DWM_BLURBEHIND pBlurBehind);
        // 核心：撤销/修改框架扩展
        int DwmExtendFrameIntoClientArea(WinDef.HWND hWnd, MARGINS pMarInset);
        // 设置窗口属性 (用于禁用新式效果，如Windows 11 Mica)
        int DwmSetWindowAttribute(WinDef.HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
        // 检查DWM是否启用 (辅助判断)
        int DwmIsCompositionEnabled(IntByReference pfEnabled);
    }

    // ====================== 2. 定义所需的结构体 ======================
    // 用于 DwmEnableBlurBehindWindow
    public static class DWM_BLURBEHIND extends Structure {
        public static final int DWM_BB_ENABLE = 0x00000001;
        public int dwFlags;
        public int fEnable; // 关键：0 = 禁用，1 = 启用
        public WinDef.HRGN hRgnBlur;
        public int fTransitionOnMaximized;

        public DWM_BLURBEHIND() {
            dwFlags = DWM_BB_ENABLE; // 只操作“启用/禁用”标志
            fEnable = 0;             // 明确设置为0以禁用
            hRgnBlur = null;
            fTransitionOnMaximized = 0;
        }
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwFlags", "fEnable", "hRgnBlur", "fTransitionOnMaximized");
        }
    }

    // 用于 DwmExtendFrameIntoClientArea
    public static class MARGINS extends Structure {
        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;
        public MARGINS(int all) { this(all, all, all, all); }
        public MARGINS(int l, int r, int t, int b) {
            cxLeftWidth = l; cxRightWidth = r; cyTopHeight = t; cyBottomHeight = b;
        }
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight");
        }
    }

    // ====================== 3. 定义关键常量 ======================
    // 分层窗口样式 (用于恢复窗口不透明)
    public static final int GWL_EXSTYLE = -20;
    public static final int WS_EX_LAYERED = 0x00080000;
    public static final int LWA_ALPHA = 0x00000002;

    // DWM窗口属性常量
    public static final int DWMWA_USE_HOSTBACKDROPBRUSH = 17; // Win10+
    public static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;   // Win11 22H2+
    public static final int DWMSBT_NONE = 1; // 表示“无背景效果”

    // ====================== 4. 核心禁用方法 ======================
    /**
     * 主方法：综合禁用各种毛玻璃效果
     * @param hWnd 目标窗口的句柄 (long类型，可从Swing/JavaFX窗口获取)
     * @return 成功返回true，否则返回false
     */
    public static boolean disableAllGlassEffects(WinDef.HWND hWnd) {
        boolean allSuccess = true;

        // 步骤A: 撤销框架扩展 (最通用有效的方法)
        System.out.print("[1] 撤销DWM框架扩展...");
        MARGINS zeroMargins = new MARGINS(0); // 将四个边距都设为0
        int result = Dwmapi.INSTANCE.DwmExtendFrameIntoClientArea(hWnd, zeroMargins);
        if (result == 0) {
            System.out.println("成功");
        } else {
            System.out.println("失败 (代码: 0x" + Integer.toHexString(result) + ")，但继续执行");
            allSuccess = false;
        }

        // 步骤B: 禁用 DWM_BLURBEHIND (针对传统Aero Glass)
        System.out.print("[2] 禁用传统模糊背景...");
        DWM_BLURBEHIND blurBehind = new DWM_BLURBEHIND(); // 构造函数已设置 fEnable = 0
        result = Dwmapi.INSTANCE.DwmEnableBlurBehindWindow(hWnd, blurBehind);
        if (result == 0) {
            System.out.println("成功");
        } else {
            // 此API在Win8+上可能已过时，失败是正常的
            System.out.println("可能不支持或无需操作 (代码: 0x" + Integer.toHexString(result) + ")");
        }

        // 步骤C: 禁用Windows 10/11的现代背景效果
        // 方法C1: 禁用 USE_HOSTBACKDROPBRUSH (Win10)
        System.out.print("[3.1] 禁用Win10主机背景刷...");
        try (Memory attributeValue = new Memory(4)) {
            attributeValue.setInt(0, 0); // 0 = FALSE
            result = Dwmapi.INSTANCE.DwmSetWindowAttribute(
                    hWnd,
                    DWMWA_USE_HOSTBACKDROPBRUSH,
                    attributeValue,
                    4);
            System.out.println(result == 0 ? "成功" : "失败或无影响");
        }

        // 方法C2: 设置背景类型为“无” (Win11 22H2+)
        System.out.print("[3.2] 设置Win11背景类型为无...");
        try (Memory attributeValue = new Memory(4)) {
            attributeValue.setInt(0, DWMSBT_NONE); // 1 = DWMSBT_NONE
            result = Dwmapi.INSTANCE.DwmSetWindowAttribute(
                    hWnd,
                    DWMWA_SYSTEMBACKDROP_TYPE,
                    attributeValue,
                    4);
            System.out.println(result == 0 ? "成功" : "失败或无影响 (可能为旧系统)");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("跳过 (当前系统不支持此API)");
        }

        // 步骤D: (可选) 移除窗口的透明属性，恢复不透明
        System.out.print("[4] 恢复窗口不透明...");
        try {
            // 注意：需导入 com.sun.jna.platform.win32.User32
            com.sun.jna.platform.win32.User32 user32 = com.sun.jna.platform.win32.User32.INSTANCE;
            int exStyle = user32.GetWindowLong(hWnd, GWL_EXSTYLE);
            if ((exStyle & WS_EX_LAYERED) != 0) {
                // 如果窗口是分层的，可以移除分层样式，或重置透明度
                user32.SetLayeredWindowAttributes(hWnd, 0, (byte) 255, LWA_ALPHA);
                System.out.println("已重置透明度");
            } else {
                System.out.println("无需操作 (非分层窗口)");
            }
        } catch (Exception e) {
            System.out.println("操作失败: " + e.getMessage());
            allSuccess = false;
        }

        return allSuccess;
    }


}