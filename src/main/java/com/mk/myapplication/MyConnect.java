package com.mk.myapplication;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyConnect {

    private static final String TAG = "mysql11111";

    public static final String url="jdbc:mysql://192.168.137.1:3306/community";
    public static final String user="root";
    public static final String pas="1234567";
    public static Connection conn = null;
    public PreparedStatement pt = null;
    public ResultSet rs = null;


    public static Connection getConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,pas);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn,PreparedStatement pt,ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pt != null) {
            try {
                pt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void close(Connection conn,PreparedStatement pt) {

        if (pt != null) {
            try {
                pt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}


