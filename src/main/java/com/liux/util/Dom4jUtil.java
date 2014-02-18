package com.liux.util;

import com.liux.bean.Website;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author hongliang.dinghl
 *         Dom4j 生成XML文档与解析XML文档
 */
public class Dom4jUtil {
    public List<Website> parserXml(String fileName) {
        SAXReader saxReader = new SAXReader();
        List<Website> list = new ArrayList<Website>();
        try {
            URL url = getClass().getResource("/");
            //System.out.println(url.getPath());
            String path = url.getFile().replace("%20", " ") + fileName;
            Document document = saxReader.read(new File(path));
            Element websites = document.getRootElement();
            for (Iterator i = websites.elementIterator(); i.hasNext(); ) {
                Element employee = (Element) i.next();
                Website website = new Website();

                for (Iterator j = employee.elementIterator(); j.hasNext(); ) {
                    Element node = (Element) j.next();
                    String name = node.getName();
                    // System.out.println(name + ":" + node.getText());
                    String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = website.getClass().getMethod(methodName, String.class);
                    method.invoke(website, node.getText());
                }
                list.add(website);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }
}