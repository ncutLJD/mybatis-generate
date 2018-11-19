package com.msok.mybatis.generator.def;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CreateBean {

    private String url;

    private String username;

    private String password;

    private String entityPackage;

    private String entityName;

    private String tableName;

    private String idColumnName;

    private String createTimeColumnName;

    private String updateTimeColumnName;

    private String versionColumnName;

    private String rt = "\r\t";

    private String SQLTables = "show tables";

    private String method;

    private String argv;

    static String selectStr = "select ";

    static String from = " from ";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMysqlInfo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public List<String> getTables() throws SQLException {
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(this.SQLTables);
        ResultSet rs = ps.executeQuery();
        List<String> list = new ArrayList<String>();
        while (rs.next()) {
            String tableName = rs.getString(1);
            list.add(tableName);
        }
        rs.close();
        ps.close();
        con.close();
        return list;
    }

    public List<ColumnData> getColumnDatas(String tableName) throws SQLException {
        String SQLColumns = "select column_name ,data_type,column_comment,0,0,character_maximum_length,is_nullable nullable from information_schema.columns where table_name =  '"
                + tableName + "' " + "and table_schema =  '" + CodeResourceUtil.DATABASE_NAME + "'";

        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(SQLColumns);
        List<ColumnData> columnList = new ArrayList<ColumnData>();
        ResultSet rs = ps.executeQuery();
        StringBuffer str = new StringBuffer();
        StringBuffer getset = new StringBuffer();
        while (rs.next()) {
            String name = rs.getString(1);
            String type = rs.getString(2);
            String comment = rs.getString(3);
            String precision = rs.getString(4);
            String scale = rs.getString(5);
            String charmaxLength = rs.getString(6) == null ? "" : rs.getString(6);
            String nullable = TableConvert.getNullAble(rs.getString(7));
            type = getType(type, precision, scale);

            ColumnData cd = new ColumnData();
            cd.setColumnName(name);
            cd.setDataType(type);
            cd.setColumnType(rs.getString(2));
            cd.setColumnComment(comment);
            cd.setPrecision(precision);
            cd.setScale(scale);
            cd.setCharmaxLength(charmaxLength);
            cd.setNullable(nullable);
            formatFieldClassType(cd);
            if (name.equalsIgnoreCase(idColumnName)) {
                columnList.add(columnList.get(0));
                columnList.set(0, cd);
            } else {
                columnList.add(cd);
            }
        }
        this.argv = str.toString();
        this.method = getset.toString();
        rs.close();
        ps.close();
        con.close();
        return columnList;
    }

    public String getBeanFeilds(String tableName) throws SQLException {
        List<ColumnData> dataList = getColumnDatas(tableName);
        StringBuffer str = new StringBuffer();
        StringBuffer getset = new StringBuffer();
        for (ColumnData d : dataList) {
            String name = d.getFieldName();
            String type = d.getDataType();
            String comment = d.getColumnComment();

            //尝试从注释中匹配枚举类型，如#AccountTypeEnum
            Pattern pattern4Enum = Pattern.compile("#(\\w+Enum)");
            Matcher matcher = pattern4Enum.matcher(comment);
            if (matcher.find()) {
                type = matcher.group(1);
            } else {
                int index = type.lastIndexOf(".");
                if (index > -1) {
                    type = type.substring(index + 1);// 使用短类型
                }
            }

            if (!name.equals("id")&&!name.equals("createDate")&&!name.equals("modifyDate")) {
                String maxChar = name.substring(0, 1).toUpperCase();
                str.append("\r\t").append("/**");
                str.append("\r\t").append("*").append(comment.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)",""));
                str.append("\r\t").append("**/");
                str.append("\r\t").append("private ").append(type + " ").append(name).append(";");
                String method = maxChar + name.substring(1, name.length());
                getset.append("\r\t").append("public ").append(type + " ").append("get" + method + "() {\r\t");
                getset.append("    return this.").append(name).append(";\r\t}");
                getset.append("\r");
                getset.append("\r\t").append("public void ").append("set" + method + "(" + type + " " + name + ") {\r\t");
                getset.append("    this." + name + " = ").append(name).append(";\r\t}");
                getset.append("\r");
            }
        }
        this.argv = str.append("\r").toString();
        this.method = getset.toString();
        return this.argv + this.method;
    }

    private void formatFieldClassType(ColumnData columnt) {
        String fieldType = columnt.getColumnType();
        String scale = columnt.getScale();

        if ("N".equals(columnt.getNullable())) {
            columnt.setOptionType("required:true");
        }
        if (("datetime".equals(fieldType)) || ("time".equals(fieldType))) {
            columnt.setClassType("easyui-datetimebox");
        } else if ("date".equals(fieldType)) {
            columnt.setClassType("easyui-datebox");
        } else if ("int".equals(fieldType)) {
            columnt.setClassType("easyui-numberbox");
        } else if ("number".equals(fieldType)) {
            if ((StringUtils.isNotBlank(scale)) && (Integer.parseInt(scale) > 0)) {
                columnt.setClassType("easyui-numberbox");
                if (StringUtils.isNotBlank(columnt.getOptionType()))
                    columnt.setOptionType(columnt.getOptionType() + "," + "precision:2,groupSeparator:','");
                else
                    columnt.setOptionType("precision:2,groupSeparator:','");
            } else {
                columnt.setClassType("easyui-numberbox");
            }
        } else if (("float".equals(fieldType)) || ("double".equals(fieldType)) || ("decimal".equals(fieldType))) {
            columnt.setClassType("easyui-numberbox");
            if (StringUtils.isNotBlank(columnt.getOptionType()))
                columnt.setOptionType(columnt.getOptionType() + "," + "precision:2,groupSeparator:','");
            else
                columnt.setOptionType("precision:2,groupSeparator:','");
        } else {
            columnt.setClassType("easyui-validatebox");
        }
    }

    public String getType(String dataType, String precision, String scale) {
        dataType = dataType.toLowerCase();
        if (dataType.contains("char"))
            dataType = "String";
        else if (dataType.contains("bigint"))
            dataType = "Long";
        else if (dataType.contains("int"))
            dataType = "Integer";
        else if (dataType.contains("float"))
            dataType = "Float";
        else if (dataType.contains("double"))
            dataType = "Double";
        else if (dataType.contains("number")) {
            if ((StringUtils.isNotBlank(scale)) && (Integer.parseInt(scale) > 0))
                dataType = "java.math.BigDecimal";
            else if ((StringUtils.isNotBlank(precision)) && (Integer.parseInt(precision) > 6))
                dataType = "Long";
            else
                dataType = "Integer";
        } else if (dataType.contains("decimal"))
            dataType = "BigDecimal";
        else if (dataType.contains("date"))
            dataType = "Date";
        else if (dataType.contains("time"))
            dataType = "Timestamp";
        else if (dataType.contains("clob"))
            dataType = "Clob";
        else {
            dataType = "Object";
        }
        return dataType;
    }

    public void getPackage(int type, String createPath, String content, String packageName, String className,
            String extendsClassName, String[] importName) throws Exception {
        if (packageName == null) {
            packageName = "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packageName).append(";\r");
        sb.append("\r");
        for (int i = 0; i < importName.length; i++) {
            sb.append("import ").append(importName[i]).append(";\r");
        }
        sb.append("\r");
        sb.append("/**\r *  entity. @author wolf Date:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + "\r */");
        sb.append("\r");
        sb.append("\rpublic class ").append(className);
        if (extendsClassName != null) {
            sb.append(" extends ").append(extendsClassName);
        }
        if (type == 1)
            sb.append(" ").append("implements java.io.Serializable {\r");
        else {
            sb.append(" {\r");
        }
        sb.append("\r\t");
        sb.append("private static final long serialVersionUID = 1L;\r\t");
        String temp = className.substring(0, 1).toLowerCase();
        temp = temp + className.substring(1, className.length());
        if (type == 1) {
            sb.append("private " + className + " " + temp + "; // entity ");
        }
        sb.append(content);
        sb.append("\r}");
        System.out.println(sb.toString());
        createFile(createPath, "", sb.toString());
    }

    public String getTablesNameToClassName(String tableName) {
        String[] split = tableName.split("_");
        if (split.length > 1) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < split.length; i++) {
                String tempTableName = split[i].substring(0, 1).toUpperCase()
                        + split[i].substring(1, split[i].length());
                sb.append(tempTableName);
            }

            return sb.toString();
        }
        String tempTables = split[0].substring(0, 1).toUpperCase() + split[0].substring(1, split[0].length());
        return tempTables;
    }

    public void createFile(String path, String fileName, String str) throws IOException {
        FileWriter writer = new FileWriter(new File(path + fileName));
        writer.write(new String(str.getBytes("utf-8")));
        writer.flush();
        writer.close();
    }

    public Map<String, Object> getAutoCreateSql(String tableName) throws Exception {
        Map sqlMap = new HashMap();
        List<ColumnData> columnDatas = getColumnDatas(tableName);
        String columns = getColumnSplit(columnDatas);
        String fields = getFieldSplit(columnDatas);
        String[] columnList = getColumnList(columns);
        String columnFields = getColumnFields(columns);
        String insert = "insert into\n\t\t" + tableName + "(" + columns.replaceAll("\\|", ",") + ")\n\t\tvalues(#{"
                + fields.replaceAll("\\|", "},#{") + "})";
        String update = getUpdateSql(tableName, columnList);
        String updateSelective = getUpdateSelectiveSql(tableName, columnDatas);
        String selectById = getSelectByIdSql(tableName, columnList);
        String delete = getDeleteSql(tableName, columnList);
        sqlMap.put("columnList", columnList);
        sqlMap.put("columnFields", columnFields);
        String insertReplaced = insert.replace("#{" + toCamelCase(createTimeColumnName) + "}", "now()");
        insertReplaced = insertReplaced.replace("#{" + toCamelCase(updateTimeColumnName) + "}", "now()");
        insertReplaced = insertReplaced.replace("#{" + toCamelCase(versionColumnName) + "}", "0");
        sqlMap.put("insert", insertReplaced);
        String updateReplaced = update.replace("#{" + toCamelCase(versionColumnName) + "}", versionColumnName + "+1");
        sqlMap.put("update", updateReplaced);
        sqlMap.put("delete", delete);
        sqlMap.put("updateSelective", updateSelective);
        sqlMap.put("selectById", selectById);
        return sqlMap;
    }

    public String getDeleteSql(String tableName, String[] columnsList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("delete from ").append(tableName).append(" where\n\t\t");
        sb.append(columnsList[0]).append("=#{").append(toCamelCase(columnsList[0])).append("}");
        return sb.toString();
    }

    public String getSelectByIdSql(String tableName, String[] columnsList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("select\n\t\t<include refid=\"baseColumnList\" />\n");
        sb.append("\t\tfrom ").append(tableName).append(" where ");
        sb.append(columnsList[0]).append("=#{").append(toCamelCase(columnsList[0])).append("}");
        return sb.toString();
    }

    public String getColumnFields(String columns) throws SQLException {
        String fields = columns;
        if ((fields != null) && (!"".equals(fields))) {
            fields = fields.replaceAll("[|]", ",");
        }
        return fields;
    }

    public String[] getColumnList(String columns) throws SQLException {
        String[] columnList = columns.split("[|]");
        return columnList;
    }

    public String getUpdateSql(String tableName, String[] columnsList) throws SQLException {
        StringBuffer sb = new StringBuffer();

        for (int i = 1; i < columnsList.length; i++) {
            String column = columnsList[i];
            if (!createTimeColumnName.equalsIgnoreCase(column)) {
                if (updateTimeColumnName.equalsIgnoreCase(column.toUpperCase())) {
                    sb.append(column + "=now()");
                } else if (versionColumnName.equalsIgnoreCase(column.toUpperCase())) {
                    sb.append(column + "=" + versionColumnName + "+1");
                } else {
                    sb.append(column + "=#{" + toCamelCase(column) + "}");
                }
                if (i + 1 < columnsList.length)
                    sb.append(",");
            }
        }
        String update = "update " + tableName + " set\n\t\t" + sb.toString() + "\n\t\twhere " + columnsList[0] + "=#{"
                + toCamelCase(columnsList[0]) + "}";
        return update;
    }

    public String getUpdateSelectiveSql(String tableName, List<ColumnData> columnList) throws SQLException {
        ColumnData cd = (ColumnData) columnList.get(0);
        String update = "update " + tableName + " set\n\t\t<include refid=\"selectiveSetClause\" />\n\t\twhere "
                + cd.getColumnName() + "=#{" + cd.getFieldName() + "}";
        return update;
    }

    public String getColumnSplit(List<ColumnData> columnList) throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            commonColumns.append(data.getColumnName() + "|");
        }
        return commonColumns.delete(commonColumns.length() - 1, commonColumns.length()).toString();
    }

    public String getFieldSplit(List<ColumnData> columnList) throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            commonColumns.append(data.getFieldName() + "|");
        }
        return commonColumns.delete(commonColumns.length() - 1, commonColumns.length()).toString();
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    public String getCreateTimeColumnName() {
        return createTimeColumnName;
    }

    public void setCreateTimeColumnName(String createTimeColumnName) {
        this.createTimeColumnName = createTimeColumnName;
    }

    public String getUpdateTimeColumnName() {
        return updateTimeColumnName;
    }

    public void setUpdateTimeColumnName(String updateTimeColumnName) {
        this.updateTimeColumnName = updateTimeColumnName;
    }

    public String getVersionColumnName() {
        return versionColumnName;
    }

    public void setVersionColumnName(String versionColumnName) {
        this.versionColumnName = versionColumnName;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
