package com.wmp.whetstone.tools.windowsAPI;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;

public interface GDI32 extends StdCallLibrary {
    GDI32 INSTANCE = Native.load("gdi32", GDI32.class);

    boolean PatBlt(WinDef.HDC hdc, int nXLeft, int nYLeft, int nWidth, int nHeight, int dwRop);
}