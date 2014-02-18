package com.liux;

import com.liux.bean.RSSItemBean;
import com.liux.bean.Website;
import com.liux.db.RssDao;
import com.liux.util.Dom4jUtil;
import com.liux.util.FeedReader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-11-5
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public void init(){
        start();
        spiderRun();
        end();
    }
    public static void main(String[] args){
        new Main().init();
    }

    public void spiderRun() {
        List<Website>  websiteList = new Dom4jUtil().parserXml("website.xml");
        for (Website we : websiteList) {
            if (we.getOpen().equals("true")) {         //只对开启的website  spide
                System.out.println("==========begin spide " + we.getName() + ".==============");
                rssInsert(we);
                System.out.println("==========end spide " + we.getName() + ".==============");
            }
        }
    }

    public void rssInsert(Website website) {
        List<RSSItemBean> rssList = null;
        try {
            rssList = new FeedReader().getContent(website);
            RssDao rssDao = new RssDao();
            if (rssList != null) {
                int size = rssList.size();
                for (int i = 0; i < size; i++) {
                    RSSItemBean rs = rssList.get(i);
                    if(rs.getContent().equals("")){
                        continue;
                    }
                    rssDao.insert(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start() {
        System.out.println("==========web spider running.==============");
    }

    public void end() {
        System.out.println("=============insert complemt==============");
        System.out.println("==============task success!!.==============");
    }
}
