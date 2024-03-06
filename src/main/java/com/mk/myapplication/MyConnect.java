package com.mk.myapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyConnect {
    private final static String url="jdbc:mysql://10.0.2.2:3306/ArtShare?characterEncoding=UTF-8";
    private final static String username="root";
    private final static String password="1234567";
    private Statement mStatement;
    private Connection mConnection;
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println("加载驱动失败!!"+e.getMessage());
        }
    }
    public Connection getConnection(){
        try {
            mConnection= DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            System.out.println("生成连接对象失败!!"+e.getMessage());
        }
        return mConnection;
    }
    public Statement getStatement(){
        try {
            mStatement=mConnection.createStatement();
        } catch (SQLException e) {
            System.out.println("生成statment对象失败!!"+e.getMessage());
        }
        return mStatement;
    }

}
