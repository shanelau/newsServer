package com.liux.util;

import com.liux.bean.RSSItemBean;
import com.liux.bean.Website;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It Reads and prints any RSS/Atom feed type.
 */
public class FeedReader {
    private String CLASS_PAHT;
    private String relative_path;

    public FeedReader() {
        Properties proerties = PropertiesUtil.getInstance().getProerties();
        CLASS_PAHT= proerties.getProperty("image_path");
        relative_path = proerties.getProperty("relative_path");
    }

    /**
     * @param url rss 网站地址  比如：http://www.ithome.com/rss/
     * @return 所有文章对象
     * @throws Exception
     */
    public List<RSSItemBean> getRss(String url) throws Exception {
        URL feedUrl = new URL(url);//SyndFeedInput:从远程读到xml结构的内容转成SyndFeedImpl实例
        URLConnection conn = feedUrl.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); //欺骗服务器
        SyndFeedInput input = new SyndFeedInput();//rome按SyndFeed类型生成rss和atom的实例,

        SyndFeed feed = input.build(new XmlReader(conn));   //SyndFeed是rss和atom实现类SyndFeedImpl的接口
        List<SyndEntryImpl> entries = feed.getEntries();
        RSSItemBean item = null;
        List<RSSItemBean> rssItemBeans = new ArrayList<RSSItemBean>();
        for (SyndEntryImpl entry : entries) {
            item = new RSSItemBean();
            item.setTitle(entry.getTitle().trim());
            item.setType(feed.getTitleEx().getValue().trim());
            item.setUri(entry.getUri());
            item.setPubDate(entry.getPublishedDate());
            item.setAuthor(entry.getAuthor());
            rssItemBeans.add(item);
        }
        return rssItemBeans;
    }

    /**
     * 从html 中获取 新闻正文
     *
     * @param website 网站对象，我自己定义的
     * @return 加入了新闻正文的 RSS对象  对象链表
     * @throws Exception
     */
    public List<RSSItemBean> getContent(Website website) throws Exception {
        String content;
        List<RSSItemBean> rssList = getRss(website.getUrl());
        FindHtml findHtml = new FindHtml(website.getStartTag(), website.getEndTag(), website.getEncoding());
        for (RSSItemBean rsItem : rssList) {
            String link = rsItem.getUri();

            content = findHtml.getContent(link);   //关键方法，获取新闻征文
            content = processImages(content);          //转换图片
            rsItem.setContent(content);
            //break;
            rsItem.setFid(Integer.parseInt(website.getFid()));
        }
        return rssList;
    }

    /**
     * 去掉文章中的<a>
     *
     * @param input
     * @return
     */
    private String removeLinks(String input) {
        String output = input;
        // 开头的<a>的正则表达式
        String regEx = "<a [^>]*>";
        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        output = m.replaceAll("");
        // 结尾的</a>的正则表达式
        regEx = "</a>";
        p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        m = p.matcher(output);
        output = m.replaceAll("");
        return output;
    }
    public static void main(String[] args){
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        System.out.println(uuid.toString());
    }

    /**
     * 处理文章中的图片
     *
     * @param input
     * @return
     */
    private String processImages(String input) {
        String output = input;
        String regEx = "<img [^>]*>";
        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        List<String> imgs = new ArrayList<String>();
        // 读取所有<img>标签
        while (m.find()) {
            imgs.add(m.group());
        }
        // 把图存到本地，并替换<img>标签的src值
        for (String img : imgs) {
            int begin = -1;
            int end = -1;
            String path = "";
            if (img.indexOf("src=\"") != -1) {
                begin = img.indexOf("src=\"");
                path = img.substring(begin + 5);
                end = path.indexOf("\"");
                if (end != -1) {
                    path = path.substring(0, end);
                } else {
                    path = "";
                }
            }
            if (img.indexOf("src='") != -1) {
                begin = img.indexOf("src='");
                path = img.substring(begin + 5);
                end = path.indexOf("'");
                if (end != -1) {
                    path = path.substring(0, end);
                } else {
                    path = "";
                }
            }
            if (!path.equals("")) {
                // String filepath = this.writeImageToServer(path);
                String filepath = writeToFile(path);
                while (filepath.indexOf('\\') != -1) {
                    filepath = filepath.replace('\\', '/');
                }
                output = output.replaceAll(path, filepath);
            }
        }
        // System.out.println(output);
        return output;
    }
    /**
     * 把图片写到数据库
     *
     * @param path 原图片路径
     * @return 本地图片路径
     */
    public String writeToFile(String path) {
        String dirName = "";
        String fileName = "";
        OutputStreamWriter osw = null;
        File directory = null;
        File file = null;
        try {
            // 取图像的格式
            int begin = path.lastIndexOf(".");
            String suffix = path.substring(begin + 1);
            if(suffix.contains("!")){                       //有些网站图片 jyijaktkyzkk.jpg!292x420
                int index = suffix.indexOf("!");
                suffix = suffix.substring(0,index);
            }
            // 读取图像
            URL url = new URL(path);
            BufferedImage image = ImageIO.read(url);
            dirName = CLASS_PAHT;                         //文件目录
            directory = new File(dirName);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            if (directory.exists()) {
                String name= UUID.randomUUID() + "." + suffix;
                fileName = dirName + name;
                file = new File(fileName);    //真正文件名
                FileOutputStream fos = new FileOutputStream(file);
                ImageIO.write(image, suffix, fos);
                fos.close();
                return relative_path+name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}