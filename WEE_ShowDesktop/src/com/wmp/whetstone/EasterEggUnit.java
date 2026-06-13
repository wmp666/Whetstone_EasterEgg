package com.wmp.whetstone;

import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "showDesktop";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.1.0";
    }

    @Override
    public String help() {
        return "用于在一瞬间显示桌面并恢复\n输入参数: 次数(整数)";
    }

    @Override
    public void run(String[] args) {
        for (int i = 0; i < Integer.parseInt(args[0]); i++){
            WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN, WinAPIEntireFunction.VK_D);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN, WinAPIEntireFunction.VK_D);
        }
    }

    @Override
    public void clear() {

    }
}
