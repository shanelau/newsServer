package com.liux.db;

import com.liux.bean.PreForumPost;
import com.liux.bean.RSSItemBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: liuxing
 * Date: 13-11-5
 * Time: 下午4:41
 * To change this template use File | Settings | File Templates.
 */
public class RssDao {

    /* 插入数据记录，并输出插入的数据记录数*/
    public void insert(RSSItemBean rss) {
        int lastPid = getLastPostPid();
        int lastTid = getLastPostTid();
        Connection conn = DBUtil.getConnection();    // 首先要获取连接，即连接到数据库

        int fid = rss.getFid();
        int time = 0;

        if (!checkExist(rss.getTitle())) {                //检查内容是否已经被抓取了
            int currentPId =lastPid;
            int currentTid = lastTid;
            PreparedStatement st;
            try {
                DBUtil.beginTransaction(conn);  //开启事物
                //values(15,36,13,1,'狂飙蜗牛',3,'aa',1383553857,'bb','1270.0.1',2998,1,-1,0,1);
                String sql = "INSERT INTO pre_forum_post(pid, fid, tid,first, author,authorid,subject,dateline,message,useip,port,usesig,htmlon,smileyoff,attachment,position) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                st = (PreparedStatement) conn.prepareStatement(sql);    // 创建用于执行静态sql语句的Statement对象
                st.setInt(1, ++currentPId);    //        pid
                st.setInt(2, fid);                  //       fid
                st.setInt(3, ++currentTid);        //     tid
                st.setBoolean(4, true);               //   first
                st.setString(5, "狂飙蜗牛");          //   author
                st.setInt(6, 3);                           // authorid
                st.setString(7, rss.getTitle());             // subject
                time = (int) (System.currentTimeMillis() / 1000);
                st.setInt(8, time);     // dateline
                st.setString(9, rss.getContent());              // message
                st.setString(10, "127.0.0.1");                 // useip
                st.setInt(11, 2998);                            //  port
                st.setBoolean(12, true);                        //   usesig
                st.setBoolean(13, true);                     //htmlon
                st.setBoolean(14, false);                        //  smileyoff
                st.setBoolean(15, false);                         //   attachment
                st.setInt(16, 1);                                    //  position
                st.executeUpdate();


                //插入第二个表
                String sql2 = "INSERT INTO pre_forum_thread (`fid`, `posttableid`, `typeid`, `sortid`, `readperm`, `price`, `author`,`authorid`, `subject`, `dateline`, `lastpost`, `lastposter`, `views`,`replies`, `displayorder`, `highlight`, `digest`, `rate`, `special`,`attachment`, `moderated`, `closed`, `stickreply`, `recommends`,`recommend_add`, `recommend_sub`, `heats`, `status`, `isgroup`, `favtimes`,`sharetimes`, `stamp`, `icon`, `pushedaid`, `cover`, `replycredit`) VALUES\n" +
                        "( ?, 0, 0, 0, 0, 0, '狂飙蜗牛', 3, ?,?, ?, '狂飙蜗牛', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,32, 0, 0, 0, -1, -1, 0, 0, 0)";

                st = (PreparedStatement) conn.prepareStatement(sql2);
                //st.setInt(1,currentPId);
                st.setInt(1, fid);
                st.setString(2, rss.getTitle());   //subject
                st.setInt(3, time);
                st.setInt(4, time);
                st.executeUpdate();


                //插入第三个表
                String sql3 = "INSERT INTO pre_forum_post_tableid(`pid`) VALUES (" + currentPId + ")";
                st = (PreparedStatement) conn.prepareStatement(sql3);
                st.executeUpdate();


                //插入第四个表
                //首先要查找一下forum_post (forum_post_tableid 和他的pid是一样的) 的 pid最大值 、 forum_thread 的 tid 最大值 ，由查出来的起始id+1 作为新的id。
                String sql4 = "UPDATE `pre_forum_forum` SET threads=threads+1, posts=posts+1,todayposts=todayposts+1 ,lastpost='" + currentPId + " " + rss.getTitle() + " " + time + " 狂飙蜗牛" + "' WHERE fid=" + fid;
                st = (PreparedStatement) conn.prepareStatement(sql4);
                st.executeUpdate();
                DBUtil.commitTransaction(conn);

            } catch (Exception e) {
                e.printStackTrace();
                DBUtil.rollBackTransaction(conn);
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println("success add subject：" + rss.getTitle());
        } else {
            System.out.println("this subject is exist ：" + rss.getTitle());
        }
    }

    public int getLastPostPid() {
        String sql = "select pid from pre_forum_post order by pid desc limit 0,1";
        Connection conn = DBUtil.getConnection();
        PreForumPost pf = null;
        try {
            PreparedStatement prest = conn.prepareStatement(sql);
            ResultSet rs = prest.executeQuery();
            //List<PreForumPost> list = new ArrayList<PreForumPost>();
            while (rs.next()) {
                return rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getLastPostTid() {
        String sql = "select tid from pre_forum_post order by tid desc limit 0,1";
        Connection conn = DBUtil.getConnection();
        PreForumPost pf = null;
        try {
            PreparedStatement prest = conn.prepareStatement(sql);
            ResultSet rs = prest.executeQuery();
            //List<PreForumPost> list = new ArrayList<PreForumPost>();
            while (rs.next()) {
                return rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据title  判断内容是否已经抓取
     *
     * @param title
     */

    public Boolean checkExist(String title) {
        String sql = "select * from pre_forum_post p where p.subject = ?";
        Connection conn = DBUtil.getConnection();
        PreForumPost pf = null;
        try {
            PreparedStatement prest = conn.prepareStatement(sql);
            prest.setString(1, title);
            ResultSet rs = prest.executeQuery();
            if (rs.next()) {
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
