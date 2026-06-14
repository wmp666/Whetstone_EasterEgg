package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.CTColor;
import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.JarDevelopEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;
import com.wmp.whetstone.tools.windowsAPI.BlurGlassEffect;
import com.wmp.whetstone.tools.windowsAPI.DesktopAppEnumerator;
import com.wmp.whetstone.tools.windowsAPI.DisableGlassEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class EasterEggUnit extends JarDevelopEasterEggUnit {

    private final JDialog textFrame = new JDialog(new Frame(), "班主任");

    private Thread restartExplorerThread;
    @Override
    public String getID() {
        return "NJHacking";
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.2.0";
    }

    @Override
    public String help() {
        return "前台彩蛋合集";
    }


    @Override
    public FuncHelpUnit[] funcHelps() {
        return new FuncHelpUnit[]{
                new FuncHelpUnit("frameGlass", "使所有窗口透明\n可用参数:最长等待时间(保底1分钟)(min), 间隔时间(s), 循环次数"),
                new FuncHelpUnit("showText", "显示一段文字在左上角\n可用参数:文字, 显示时间(min)"),
                new FuncHelpUnit("restartExplorer", "重启文件资源管理器\n可用参数: 次数, 等待时间(ms), 间隔时间(s)"),
                new FuncHelpUnit("screenBlocking", "在过一段时间后遮挡屏幕\n可用参数: 启动前最长等待时间(min) 显示时间(s) 显示次数"),
                new FuncHelpUnit("showDesktop", "用于在一瞬间显示桌面并恢复\n输入参数: 次数(整数)"),
        };
    }

    public void showText(String[] args){
        Log.trayIcon.displayMessage("班主任", "班主任已成功监管电脑,不要搞小动作", TrayIcon.MessageType.WARNING);

        {
            JDialog frame = new JDialog();
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }
            });

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/nj_dunk_2.png"), screenSize.width, screenSize.height, false));
            frame.add(label);


            frame.pack();
            frame.setLocation(0, 0);

            frame.setVisible(true);
        }




        textFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        textFrame.setUndecorated(true);
        textFrame.setAlwaysOnTop(true);
        textFrame.setBackground(new Color(0,0,0,0));
        textFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new Thread(()->{
                    try {
                        Thread.sleep(Integer.parseInt(args[1])*60*1000);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }).start();

            }
        });

        JLabel label = new JLabel(args[0]);
        label.setForeground(CTColor.mainColor);
        label.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.BIG));
        textFrame.add(label);

        ((JPanel)textFrame.getContentPane()).setOpaque(false);

        textFrame.pack();
        textFrame.setLocation((int) (100 * CTInfo.dpi), (int) (100 *CTInfo.dpi));

        textFrame.setVisible(true);
    }

    public void frameGlass(String[] args){
        try {
            Thread.sleep((long) (new Random().nextInt(Integer.parseInt(args[0])) + 1) *60*1000);

            for (int i = 0; i < Integer.parseInt(args[2]); i++) {
                {
                    java.util.List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
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

    public void restartExplorer(String[] args){
        restartExplorerThread = new Thread(()->{
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
        });
        restartExplorerThread.start();
    }

    public void screenBlocking(String[] args){
        try {
            for(int i = 0;i < Integer.parseInt(args[2]);i++){
                blocking(args);
            }
        } catch (Exception e) {
            Log.err.print(EasterEggUnit.class, "遮挡屏幕时发生错误", e);
        }
    }

    public void showDesktop(String[] args){
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
    public void clear(String funcName) {
        if (funcName.equals("showText")){
            textFrame.dispose();
        }else if (funcName.equals("restartExplorer")){
            restartExplorerThread.interrupt();
            try {
                Runtime.getRuntime().exec(new String[]{"explorer.exe"});
            } catch (IOException _) {}

        }
    }

    private static void blocking(String[] args) throws InterruptedException, AWTException {
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();


        int waitTime = (new Random().nextInt(Integer.parseInt(args[0])) + 1);
        Log.info.print(ClassFormPanel.class.toString(), String.format("预启动：屏幕遮挡|参数：启动前等待时间:%smin;显示时间:%ss;大小：%s", waitTime, args[1], d));

        Thread.sleep((long) waitTime * 60 * 1000);

        //创建一个robot对象
        Robot robot = new Robot();

        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        BufferedImage bufferedImage = robot.createScreenCapture(screenRect);

        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        final boolean[] b = {false};
        Object temp = 0;
        Runnable r = () -> {
            synchronized (temp){
                if (b[0]) return;
                b[0] = true;
                try {
                    Thread.sleep(Integer.parseInt(args[1]) * 1000L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                dialog.setVisible(false);
            }
        };

        JLabel image = new JLabel(new ImageIcon(bufferedImage));
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                r.run();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                r.run();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                r.run();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                r.run();
            }
        });
        dialog.add(image);

        dialog.pack();

        dialog.setVisible(true);
        Log.info.print(ClassFormPanel.class.toString(), "屏幕遮挡完毕！");
    }

}
