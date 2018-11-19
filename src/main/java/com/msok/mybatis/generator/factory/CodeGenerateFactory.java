package com.msok.mybatis.generator.factory;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msok.mybatis.generator.def.CodeResourceUtil;
import com.msok.mybatis.generator.def.CommonPageParser;
import com.msok.mybatis.generator.def.CreateBean;

public class CodeGenerateFactory {
    private static final Logger log = LoggerFactory.getLogger(CodeGenerateFactory.class);

    private static String url = CodeResourceUtil.URL;

    private static String username = CodeResourceUtil.USERNAME;

    private static String passWord = CodeResourceUtil.PASSWORD;

    private static String buss_package = CodeResourceUtil.bussiPackage;

    private static String projectPath = getProjectPath();

    public static void codeGenerate(CreateBean createBean) {
        createBean.setMysqlInfo(url, username, passWord);
        String tableName = createBean.getTableName();
        String entityPackage =  createBean.getEntityPackage();
        
        String className = StringUtils.isEmpty(createBean.getEntityName()) ? createBean.getTablesNameToClassName(tableName) : createBean.getEntityName();
        String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());

        String srcPath = projectPath + CodeResourceUtil.source_root_package + "/";

        String pckPath = srcPath + CodeResourceUtil.bussiPackageUrl + "/";

        String webPath = projectPath + CodeResourceUtil.web_root_package + "/view/"
                + CodeResourceUtil.bussiPackageUrl + "/";

        String entityPackageName = StringUtils.isEmpty(entityPackage) ? "" : entityPackage + "/";
        String modelPath = "page/" + entityPackageName + className + "Page.java";
        String beanPath = "entity/" + entityPackageName + className + "Entity.java";
        String mapperPath = "mapper/" + entityPackageName + className + "Mapper.java";
        String servicePath = "service/" + entityPackageName + className + "Service.java";
        String serviceImplPath = "service/impl/" + entityPackageName + className + "ServiceImpl.java";
        String controllerPath = "controller/" + entityPackageName + className + "Controller.java";
        String sqlMapperPath = "mapper/" + entityPackageName + className + "Mapper.xml";
        webPath = webPath + entityPackageName;

        String jspPath = lowerName + ".jsp";
        String jsPath = "page-" + lowerName + ".js";

        VelocityContext context = new VelocityContext();
        context.put("className", className);
        context.put("lowerName", lowerName);
        context.put("tableName", tableName);
        context.put("bussPackage", buss_package);
        context.put("entityPackage", entityPackage);
        context.put("createTimeColumnName", createBean.getCreateTimeColumnName());
        context.put("updateTimeColumnName", createBean.getUpdateTimeColumnName());
        try {
            context.put("feilds", createBean.getBeanFeilds(tableName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map sqlMap = createBean.getAutoCreateSql(tableName);
            context.put("columnDatas", createBean.getColumnDatas(tableName));
            context.put("SQL", sqlMap);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        CommonPageParser.WriterPage(context, "EntityTemplate.ftl", pckPath, beanPath);
        //     CommonPageParser.WriterPage(context, "PageTemplate.ftl", pckPath, modelPath);
        CommonPageParser.WriterPage(context, "DaoTemplate.ftl", pckPath, mapperPath);
        CommonPageParser.WriterPage(context, "ServiceTemplate.ftl", pckPath, servicePath);
        CommonPageParser.WriterPage(context, "ServiceTemplateImpl.ftl", pckPath, serviceImplPath);
        CommonPageParser.WriterPage(context, "MapperTemplate.xml", pckPath, sqlMapperPath);
        //     CommonPageParser.WriterPage(context, "ControllerTemplate.ftl", pckPath, controllerPath);

//        CommonPageParser.WriterPage(context, "jspTemplate.ftl", webPath, jspPath);
//        CommonPageParser.WriterPage(context, "jsTemplate.ftl", webPath, jsPath);

        log.info("----------------------------代码生成完毕---------------------------");
    }

    public static String getProjectPath() {
        String path = System.getProperty("user.dir").replace("/", "/") + "/";
        return path;
    }
}
