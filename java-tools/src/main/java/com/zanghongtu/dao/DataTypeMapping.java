package com.zanghongtu.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Hongtu Zang
 * @date : Created in 上午11:15 19-1-16
 */
public class DataTypeMapping {
    public static Map<String, String> mysqlJavaMapping = new HashMap<String, String>(){
        {
            put("VARCHAR", "String");
            put("CHAR", "String");
            put("TEXT", "String");

            put("INTEGER", "Integer");
            put("INT", "Integer");
            put("TINYINT", "Integer");
            put("SMALLINT", "Integer");
            put("MEDIUMINT", "Integer");

            put("BIT", "Boolean");

            put("BIGINT", "BigInteger");

            put("FLOAT", "Float");
            put("DOUBLE", "Double");
            put("DECIMAL", "BigDecimal");

            put("DATE", "Date");
            put("TIME", "Date");
            put("DATETIME", "Date");
            put("TIMESTAMP", "Date");
        }
    };

    public static Map<String, String> typeImportMapping = new HashMap<String, String>() {
        {
            put("String", "java.lang.String");
            put("Integer", "java.lang.Integer");
            put("Boolean", "java.lang.Boolean");
            put("BigInteger", "java.math.BigInteger");
            put("BigDecimal", "java.math.BigDecimal");
            put("Date", "java.util.Date");
        }
    };

}
