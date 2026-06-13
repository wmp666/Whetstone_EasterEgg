package com.wmp.whetstone;

import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.whetstone.tools.windowsAPI.BlurGlassEffect;
import com.wmp.whetstone.tools.windowsAPI.DesktopAppEnumerator;
import com.wmp.whetstone.tools.windowsAPI.DisableGlassEffect;

import java.util.List;
import java.util.Random;

public class EasterEggUnit extends BasicEasterEggUnit {
    @Override
    public String getID() {
        return "frameGlass";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public String getTargetVersion() {
        return "2.1.0";
    }

    @Override
    public String help() {
        return "在一段时间内,使所有窗口透明";
    }

    @Override
    public FuncHelpUnit[] funcHelps() {
        return new FuncHelpUnit[]{
                new FuncHelpUnit("run", "使所有窗口透明\n可用参数:最长等待时间(保底1分钟)(min), 间隔时间(s), 循环次数")
        };
    }

    @Override
    public void run(String[] args) {
        try {
            Thread.sleep((long) (new Random().nextInt(Integer.parseInt(args[0])) + 1) *60*1000);

            for (int i = 0; i < Integer.parseInt(args[2]); i++) {
                {
                    List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
                    for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                        System.out.println(windowInfo.title);
                        BlurGlassEffect.setWindowLayered(windowInfo.hwnd);
                        BlurGlassEffect.enableDwmGlassEffect(windowInfo.hwnd);
                    }
                }

                Thread.sleep(Integer.parseInt(args[1])*1000L);
            }

            {
                List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
                for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                    DisableGlassEffect.disableAllGlassEffects(windowInfo.hwnd);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }
}
