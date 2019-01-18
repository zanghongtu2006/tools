package com.zanghongtu.sysenv;

/**
 * @author : Hongtu Zang
 * @date : Created in 下午4:03 19-1-16
 */
public class Path {
    public static String currentDir() {
        return System.getProperty("user.dir");
    }
}
