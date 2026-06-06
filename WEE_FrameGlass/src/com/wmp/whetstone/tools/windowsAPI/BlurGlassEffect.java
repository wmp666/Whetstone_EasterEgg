package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

public class BlurGlassEffect {

    // Dwmapi接口
    public interface Dwmapi extends com.sun.jna.Library {
        Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);

        int DwmExtendFrameIntoClientArea(WinDef.HWND hWnd, DwmMargins pMarInset);
        int DwmSetWindowAttribute(WinDef.HWND hwnd, int dwAttribute,
                                  Pointer pvAttribute, int cbAttribute);
        int DwmIsCompositionEnabled(IntByReference pfEnabled);
    }

    // 简化版MARGINS结构
    public static class DwmMargins extends com.sun.jna.Structure {
        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
                    "cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight"
            );
        }
    }

    // 窗口样式常量
    public static final int GWL_EXSTYLE = -20;
    public static final int WS_EX_LAYERED = 0x80000;
    public static final int WS_EX_COMPOSITED = 0x02000000;

    // DWM常量
    public static final int DWMWA_USE_HOSTBACKDROPBRUSH = 17;
    public static final int DWMWA_MICA_EFFECT = 1029;  // Windows 11特定的值
    public static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;

    /**
     * 设置窗口为分层窗口（支持透明度）
     */
    public static void setWindowLayered(WinDef.HWND hWnd) {

        // 获取当前扩展样式
        int exStyle = User32.INSTANCE.GetWindowLong(hWnd, GWL_EXSTYLE);

        // 添加分层窗口和合成样式
        User32.INSTANCE.SetWindowLong(hWnd, GWL_EXSTYLE,
                exStyle | WS_EX_LAYERED | WS_EX_COMPOSITED);

        // 设置分层窗口属性（关键：使用透明度）
        // LWA_ALPHA = 0x2, LWA_COLORKEY = 0x1
        User32.INSTANCE.SetLayeredWindowAttributes(hWnd,
                0,            // 颜色键（不使用）
                (byte)180,    // 透明度：180/255（约70%不透明）
                0x2           // LWA_ALPHA标志
        );
    }

    /**
     * 启用DWM毛玻璃效果
     */
    public static boolean enableDwmGlassEffect(WinDef.HWND hWnd) {
        try {

            // 检查DWM合成
            IntByReference dwmEnabled = new IntByReference(0);
            int result = Dwmapi.INSTANCE.DwmIsCompositionEnabled(dwmEnabled);

            if (result != 0 || dwmEnabled.getValue() == 0) {
                System.out.println("DWM合成未启用");
                return false;
            }

            // 扩展窗口框架
            DwmMargins margins = new DwmMargins();
            margins.cxLeftWidth = -1;
            margins.cxRightWidth = -1;
            margins.cyTopHeight = -1;
            margins.cyBottomHeight = -1;

            result = Dwmapi.INSTANCE.DwmExtendFrameIntoClientArea(hWnd, margins);
            if (result != 0) {
                System.err.println("DwmExtendFrameIntoClientArea失败: " + result);
            }

            // 尝试Windows 10/11现代毛玻璃效果
            enableModernGlassEffects(hWnd);

            return true;
        } catch (Exception e) {
            System.err.println("启用毛玻璃效果时出错: " + e.getMessage());
            return false;
        }
    }

    /**
     * 启用现代Windows毛玻璃效果
     */
    private static void enableModernGlassEffects(WinDef.HWND hWnd) {
        // 方法1：Windows 10 - USE_HOSTBACKDROPBRUSH
        try {
            Memory useHostBackdrop = new Memory(4);
            useHostBackdrop.setInt(0, 1); // TRUE

            int result = Dwmapi.INSTANCE.DwmSetWindowAttribute(
                    hWnd,
                    DWMWA_USE_HOSTBACKDROPBRUSH,
                    useHostBackdrop,
                    4
            );

            if (result == 0) {
                System.out.println("USE_HOSTBACKDROPBRUSH设置成功");
                return;
            }
        } catch (Exception e) {
            System.out.println("不支持USE_HOSTBACKDROPBRUSH: " + e.getMessage());
        }

        // 方法2：Windows 11 - SYSTEMBACKDROP_TYPE（如果可用）
        try {
            Memory backdropType = new Memory(4);
            backdropType.setInt(0, 2); // DWMSBT_MAINWINDOW = 2

            int result = Dwmapi.INSTANCE.DwmSetWindowAttribute(
                    hWnd,
                    DWMWA_SYSTEMBACKDROP_TYPE,
                    backdropType,
                    4
            );

            if (result == 0) {
                System.out.println("SYSTEMBACKDROP_TYPE设置成功");
                return;
            }
        } catch (Exception e) {
            System.out.println("不支持SYSTEMBACKDROP_TYPE: " + e.getMessage());
        }

        // 方法3：Windows 11 - 尝试MICA效果（非官方API）
        try {
            Memory micaValue = new Memory(4);
            micaValue.setInt(0, 1); // 启用Mica

            int result = Dwmapi.INSTANCE.DwmSetWindowAttribute(
                    hWnd,
                    DWMWA_MICA_EFFECT,
                    micaValue,
                    4
            );

            if (result == 0) {
                System.out.println("MICA效果设置成功");
            }
        } catch (Exception e) {
            System.out.println("不支持MICA效果: " + e.getMessage());
        }
    }


}