package by.vorivoda.matvey.controller.util.operating.system;

import java.awt.*;
import java.io.File;

public class CurrentOS
{
    private static final boolean isWindows;
    private static final boolean isLinux;
    private static final boolean isMac;

    static {
        System.load("D:\\University\\KSIS\\FileStorage\\client\\bin\\CurrentOS.dll");
    }
    //"C:\Program Files\mingw-w64\x86_64-8.1.0-posix-seh-rt_v6-rev0\mingw64\bin\gcc" -m64 -I "C:\Program Files (x86)\Java\corretto-11.0.11\include" -I "C:\Program Files (x86)\Java\corretto-11.0.11\include\win32" -shared .\CurrentOS.c -o CurrentOS.dll
    static
    {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isLinux = os.contains("nux") || os.contains("nix");
        isMac = os.contains("mac");
    }

    public static boolean isWindows() { return isWindows; }
    public static boolean isLinux() { return isLinux; }
    public static boolean isMac() { return isMac; };

    public static boolean open(File file) {
        try {
            if (CurrentOS.isWindows()) {
                openInWinApi(file.getAbsolutePath());
                return true;
            } else if (CurrentOS.isLinux() || CurrentOS.isMac()) {
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
                return true;
            } else {
                // Unknown OS, try with desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    public static native void openInWinApi(String path);
}