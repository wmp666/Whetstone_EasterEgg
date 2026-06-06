package com.wmp.whetstone;

import com.sun.jna.ptr.IntByReference;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.easter_egg_control.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.tools.ResourceLocalizer;
import com.wmp.whetstone.tools.SecurityGuard;

import java.awt.*;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "3600safe";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.0.0";
    }

    @Override
    public String help() {
        return "用于占用内存\n可以设置运行参数：[占用百分比(小数)]";
    }

    @Override
    public void run(String[] args) {
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "3600safe.dll");


        double i = SecurityGuard.INSTANCE.huoqudangqiankeyongneicun();

        double j = 0.5;
        try {
            double temp = Double.parseDouble(args[0]);
            if (temp > 0 && temp <= 1) {
                j = temp;
            }
            Log.info.print("Whetstone", "占用百分比：" + j);
        } catch (Exception e) {
            Log.trayIcon.displayMessage( "Windows 安全中心", "威胁占用内存大小获取失败", TrayIcon.MessageType.ERROR);
        }
        zhanyong(i, j);
    }

    private static void zhanyong(double i, double j) {
        if (j == 0) {
            Log.trayIcon.displayMessage("Windows 安全中心", "发现顽固威胁，无法结束已节省内存", TrayIcon.MessageType.ERROR);
            return;
        }
        try {
            System.out.println("可用内存：" + i + "MB");
            System.out.println("占用:" + i*j);
            SecurityGuard.INSTANCE.fenpeisuoxuneicun(new IntByReference((int) (i*j)));
        } catch (Exception e) {
            zhanyong(i, j>0.1?j-0.1:0);
        }
    }

    @Override
    public void clear() {

    }
}
