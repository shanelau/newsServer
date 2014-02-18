package com.liux.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-10-31
 * Time: 下午12:34
 * To change this template use File | Settings | File Templates.
 */
public class FindHtml {
    private String startTag;
    private String endTag;
    static String url = "http://www.ithome.com/html/it/57338.htm";
    private String pageEncoding;

    public FindHtml(String startTag, String endTag, String pageEncoding) {
        this.startTag = startTag;
        this.endTag = endTag;
        this.pageEncoding = pageEncoding;
    }

    /**
     * http 请求获取到页面的源码
     *
     * @param surl 正文url
     * @return 页面源码
     */
    public String getStaticPage(String surl) {
        String htmlContent = "";
        try {
            java.io.InputStream inputStream;
            java.net.URL url = new java.net.URL(surl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); //欺骗服务器
            connection.connect();
            inputStream = connection.getInputStream();
            byte bytes[] = new byte[1024 * 4000];
            int index = 0;
            int count = inputStream.read(bytes, index, 1024 * 4000);
            while (count != -1) {
                index += count;
                count = inputStream.read(bytes, index, 1);
            }
            htmlContent = new String(bytes, pageEncoding);
            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return htmlContent.trim();
    }


    /**
     * 根据url 获取 正文内容
     *
     * @param url 指向正文的url地址
     * @return 正文源码
     */
    public String getContent(String url) {
        String src = getStaticPage(url);
        int startIndex = src.indexOf(startTag);   //开始标签
        int endIndex = src.lastIndexOf(endTag);        //结束标签
        //System.out.println(src);
        //System.out.println(startTag+"\t"+endTag);
        System.out.println(startIndex+"\t"+endIndex);
        if (startIndex != -1 && endIndex != -1) {
            return src.substring(startIndex, endIndex);
        }
        return "";
    }

    public void saveFile(String filePath, String content) {
        File file = new File(filePath);
        FileWriter resultFile = null;
        try {
            resultFile = new FileWriter(file);
            PrintWriter myFile = new PrintWriter(resultFile);
            myFile.println(content);
            myFile.close();
            resultFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStartTag() {
        return startTag;
    }

    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    public String getPageEncoding() {
        return pageEncoding;
    }

    public void setPageEncoding(String pageEncoding) {
        this.pageEncoding = pageEncoding;
    }
}
