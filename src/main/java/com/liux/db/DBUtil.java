package com.liux.db;


import com.liux.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    // 创建静态全局变量
    static Connection conn;
    private static DBUtil dbUtil;
    static PreparedStatement st;
    private static String driver;
    private static String url;
    // 连接数据库的用户名
    private static String username;
    // 连接数据库的密码
    private static String password;

    static {
        PropertiesUtil pu = PropertiesUtil.getInstance();
        Properties p = pu.getProerties();
        driver = p.getProperty("driver").trim();
        url = p.getProperty("url").trim();
        username = p.getProperty("username").trim();
        password = p.getProperty("password").trim();
    }

    /* 获取数据库连接的函数*/
    public static Connection getConnection() {
        Connection con = null;    //创建用于连接数据库的Connection对象
        try {
            Class.forName(driver);// 加载Mysql数据驱动
            con = DriverManager.getConnection(url, username, password);// 创建数据连接
        } catch (Exception e) {
            System.out.println("数据库连接失败" + e.getMessage());
        }
        return con;    //返回所建立的数据库连接
    }

    /**
     * 开始事务
     *
     * @param cnn
     */
    public static void beginTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (cnn.getAutoCommit()) {
                    cnn.setAutoCommit(false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交事务
     *
     * @param cnn
     */
    public static void commitTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (!cnn.getAutoCommit()) {
                    cnn.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 回滚事务
     *
     * @param cnn
     */
    public static void rollBackTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (!cnn.getAutoCommit()) {
                    cnn.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

