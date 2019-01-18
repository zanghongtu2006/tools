package com.zanghongtu.string;

/**
 * @author : Hongtu Zang
 * @date : Created in 下午4:40 19-1-16
 */
public class CamelCase {
    /**
     * 修改为驼封命名的变量名
     *
     * @param orig 原始变量名
     * @return 驼封变量名
     */
    public static String toCamelCase(String orig) {
        StringBuilder target = new StringBuilder();
        Boolean upperCase = false;
        for (int i = 0; i < orig.length(); i++) {
            char c = orig.charAt(i);
            if (c == '-' || c == '_') {
                if (target.length() > 0) {
                    upperCase = true;
                }
                continue;
            }
            if (upperCase) {
                target.append(("" + c).toUpperCase());
                upperCase = false;
                continue;
            }
            target.append(c);
        }
        return target.toString();
    }

    /**
     * 修改为驼封命名的类名
     *
     * @param orig 原始变量名
     * @return 驼峰命名的类名
     */
    public static String toCamelCaseClassName(String orig) {
        String target = toCamelCase(orig);
        String firstOrig = "" + target.charAt(0);
        String firstTarget = firstOrig.toUpperCase();
        return target.replaceFirst(firstOrig, firstTarget);
    }
}
