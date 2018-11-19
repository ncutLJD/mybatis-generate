package com.msok.mybatis.generator.def;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.msok.mybatis.generator.factory.CodeGenerateFactory;

public class CommonPageParser {
    private static VelocityEngine ve;

    private static final String CONTENT_ENCODING = "UTF-8";

    private static final Logger log = LoggerFactory.getLogger(CodeGenerateFactory.class);

    private static boolean isReplace = true;

    static {
        try {
            ClassPathResource templateResource = new ClassPathResource("template");
            String templateBasePath = templateResource.getFile().getAbsolutePath();
            Properties properties = new Properties();
            properties.setProperty("resource.loader", "file");
            properties.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
            properties.setProperty("file.resource.loader.path", templateBasePath);
            properties.setProperty("file.resource.loader.cache", "true");
            properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
            properties.setProperty("directive.set.null.allowed", "true");
            properties.setProperty("runtime.log", "logs/velocity.log");
            properties.setProperty("input.encoding", CONTENT_ENCODING);
            properties.setProperty("output.encoding ", CONTENT_ENCODING);
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init(properties);
            ve = velocityEngine;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void WriterPage(VelocityContext context, String templateName, String fileDirPath, String targetFile) {
        try {
            File file = new File(fileDirPath + targetFile);
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
            } else if (isReplace) {
                log.info("替换文件:" + file.getAbsolutePath());
            }

            Template template = ve.getTemplate(templateName, "UTF-8");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            template.merge(context, writer);
            writer.flush();
            writer.close();
            fos.close();
            log.info("生成文件：" + file.getAbsolutePath());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}