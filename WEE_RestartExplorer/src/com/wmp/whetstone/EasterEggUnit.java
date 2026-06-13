package com.wmp.whetstone;

import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;

import java.io.IOException;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "restartExplorer";
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
        return "重启文件资源管理器\n可用参数: 次数, 等待时间(ms), 间隔时间(s)";
    }

    @Override
    public void run(String[] args) {
        new Thread(()->{
            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                {
                    WinAPIEntireFunction.killProcess("explorer.exe", 0);
                }
                try {
                    Thread.sleep(Integer.parseInt(args[1]));
                } catch (InterruptedException _) {

                }

                {
                    try {
                        Runtime.getRuntime().exec(new String[]{"explorer.exe"});
                        Runtime.getRuntime().exec(new String[]{"explorer.exe"});
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(Integer.parseInt(args[2])* 1000L);
                } catch (InterruptedException _) {

                }
            }
        }).start();
    }
    @Override
    public void clear() {

    }
}
