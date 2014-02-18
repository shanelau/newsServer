package com.liux.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static PropertiesUtil instance;

    public static PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

    //初始化读取配置文件信息
    public Properties getProerties() {
        InputStream in = null;
        Properties p = new Properties();
        try {
            InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream("resource.properties");
            // in = new BufferedInputStream(new FileInputStream("/resource.properties"));
            p.load(is);
            return p;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}