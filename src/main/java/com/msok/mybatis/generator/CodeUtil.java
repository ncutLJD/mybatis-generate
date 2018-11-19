package com.msok.mybatis.generator;

import com.msok.mybatis.generator.def.CreateBean;
import com.msok.mybatis.generator.factory.CodeGenerateFactory;


public class CodeUtil {

    /**       
     * main(生成mybatis映射代码)         
     * 使用方法：       
     * 1. 修改数据库连接属性：src/main/resources/jeecg_database.properties     
     * 2. 修改包名：src/main/resources/jeecg_config.properties      
     * 3. 修改为自己表名
     * 4. 如果需要生成枚举类型的映射，需要在建表语句的列comment中包含#号开头，Enum结尾的类名（例如"#TradeTypeEnum"）。
     * 5. 特殊字段：createTime和updateTime的值更新使用的是数据库时间   
    */
    public static void main(String[] args) {

        String[] arr = new String[]{"activity_config"};

        for(String tableName:arr)
        {
            CreateBean createBean = new CreateBean();
            createBean.setTableName(tableName); // 要生成映射的表名
            createBean.setEntityName(null); // 要映射的entity类名，null则为表名的驼峰命名
            createBean.setIdColumnName(null); // 表的id列的名称，null则为第一列
            createBean.setCreateTimeColumnName("createTime"); // 特殊字段：数据库创建时间
            createBean.setUpdateTimeColumnName("modifyTime"); // 特殊字段：数据库更新时间
            createBean.setVersionColumnName("version"); // 特殊字段：版本号
            CodeGenerateFactory.codeGenerate(createBean);
        }
    }
}
