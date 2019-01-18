package com.zanghongtu.dao;

import com.zanghongtu.file.FileOperator;
import com.zanghongtu.string.CamelCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.sql.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Dao2POJO {
    @Value("${database.name}")
    private String databaseName;

    @Value("${database.host}")
    private String databaseHost;

    @Value("${database.port}")
    private String databasePort;

    @Value("${database.username}")
    private String databaseUserName;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.package}")
    private String databasepackage;

    private String tab = "    ";

    @Test
    public void contextLoads() {
        String connectionStr = "jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionStr, databaseUserName, databasePassword);
            DatabaseMetaData metaData = connection.getMetaData();
            //获取数据库的全部表名
            ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});
            List<String> tableNames = new LinkedList<>();
            while (resultSet.next()) {
                tableNames.add(resultSet.getString("TABLE_NAME"));
            }
            //获取表的字段，拼成POJO
            for (String tableName : tableNames) {
                String sql = "SELECT * FROM " + tableName + " WHERE 1=2;";
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = connection.prepareStatement(sql);
                    rs = ps.executeQuery();
                    ResultSetMetaData resultSetMetaData = rs.getMetaData();
                    System.out.println("================" + tableName + "================");
                    String className = CamelCase.toCamelCaseClassName(tableName);

                    Set<String> setJavaTypes = new HashSet<>();
                    Map<String, String> mapParams = new HashMap<>();
                    Map<String, String> mapParamColumns = new HashMap<>();
                    String idType = "String";
                    for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                        String colName = CamelCase.toCamelCase(resultSetMetaData.getColumnName(i));
                        String colType = DataTypeMapping.mysqlJavaMapping.get(resultSetMetaData.getColumnTypeName(i));
                        if ("TINYINT".equalsIgnoreCase(resultSetMetaData.getColumnTypeName(i)) && resultSetMetaData.getColumnDisplaySize(i) == 1) {
                            colType = "Boolean";
                        }
                        if ("ID".equalsIgnoreCase(resultSetMetaData.getColumnTypeName(i))) {
                            idType = DataTypeMapping.mysqlJavaMapping.get(resultSetMetaData.getColumnTypeName(i));
                        }
                        setJavaTypes.add(colType);
                        mapParams.put(colName, colType);
                        mapParamColumns.put(colName, resultSetMetaData.getColumnName(i));
                    }
                    writePojos(className, tableName, setJavaTypes, mapParams, mapParamColumns);
                    writeRepositories(className, tableName, idType);
                } finally {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeRepositories(String className, String tableName, String idType) {
        String modelPackage = databasepackage + ".repository";
        String repoContent = "package com.zanghongtu.database.repository;\n\n" +
                "import " + databasepackage + ".model." + className + ";\n" +
                "import org.springframework.data.jpa.repository.JpaRepository;\n" +
                "/**\n" +
                " * @author : Auto Generated\n" +
                " */\n" +
                "public interface " + className + "Repository extends JpaRepository<" +
                className + ", " + idType + "> {\n" +
                "}\n";

        String dirPath = System.getProperty("user.dir") + "/src/main/java/" + modelPackage.replace(".", "/");
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = dirPath + "/" + className + "Repository.java";
        FileOperator.writeFile(filePath, repoContent);
    }

    private void writePojos(String className, String tableName, Set<String> setJavaTypes, Map<String, String> mapParams, Map<String, String> mapParamColumns) {
        String modelPackage = databasepackage + ".model";
        String pojo = headers(setJavaTypes, modelPackage) + classDefination(className, tableName) +
                parameters(mapParams, mapParamColumns) +
                getterAndSetter(mapParams) +
                toStrings(className, mapParams);
        String dirPath = System.getProperty("user.dir") + "/src/main/java/" + modelPackage.replace(".", "/");
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = dirPath + "/" + className + ".java";
        FileOperator.writeFile(filePath, pojo);
    }

    private String toStrings(String className, Map<String, String> mapParams) {
        StringBuilder pojo = new StringBuilder();
        pojo.append(tab).append("@Override\n");
        pojo.append(tab).append("public String toString() {\n");
        pojo.append(tab).append(tab).append("return \"").append(className).append("{\" +\n");
        for (String param : mapParams.keySet()) {
            pojo.append(tab).append(tab).append(tab).append(tab).append("\"").append(param)
                    .append("='\" + ").append(param).append(" + '\\'' +\n");
        }
        pojo.append(tab).append(tab).append(tab).append(tab).append("\"}\";\n");
        pojo.append(tab).append("}\n");
        pojo.append("}");
        return pojo.toString();
    }

    private String classDefination(String className, String tableName) {
        return "/**\n" +
                " * @author Auto Generated\n" +
                " */\n" +
                "@Entity\n" +
                "@Table(name = \"" + tableName + "\")\n" +
                "public class " + className + " {\n";
    }

    private String headers(Set<String> setJavaTypes, String modelPackage) {
        StringBuilder pojo = new StringBuilder();
        pojo.insert(0, "\n");
        for (String javaType : setJavaTypes) {
            pojo.insert(0, "import " + DataTypeMapping.typeImportMapping.get(javaType) + ";\n");
        }
        pojo.insert(0, "\n");
        pojo.insert(0, "import javax.persistence.Table;\n");
        pojo.insert(0, "import javax.persistence.Column;\n");
        pojo.insert(0, "import javax.persistence.Entity;\n");
        pojo.insert(0, "import javax.persistence.Id;\n");
        pojo.insert(0, "\n");
        pojo.insert(0, "package " + modelPackage + ";\n");
        return pojo.toString();
    }

    private String parameters(Map<String, String> mapParams, Map<String, String> mapParamColumes) {
        StringBuilder pojo = new StringBuilder();
        for (String param : mapParams.keySet()) {
            if ("id".equalsIgnoreCase(param)) {
                pojo.append(tab).append("@Id\n");
            } else {
                pojo.append(tab).append("@Column(name = \"").append(mapParamColumes.get(param)).append("\")\n");
            }
            pojo.append(tab).append("private ").append(mapParams.get(param))
                    .append(" ").append(param).append(";\n\n");
        }
        return pojo.toString();
    }

    private String getterAndSetter(Map<String, String> mapParams) {
        StringBuilder pojo = new StringBuilder();
        for (String param : mapParams.keySet()) {
            //getter
            pojo.append(tab).append("public ").append(mapParams.get(param)).append(" get")
                    .append(CamelCase.toCamelCaseClassName(param))
                    .append(" () {\n");
            pojo.append(tab).append(tab).append("return this.").append(param).append(";\n");
            pojo.append(tab).append("}\n\n");
            //setter
            pojo.append(tab).append("public void set")
                    .append(CamelCase.toCamelCaseClassName(param))
                    .append(" (").append(mapParams.get(param)).append(" ").append(param).append(") {\n");
            pojo.append(tab).append(tab).append("this.").append(param).append(" = ").append(param).append(";\n");
            pojo.append(tab).append("}\n\n");
        }
        return pojo.toString();
    }

}

