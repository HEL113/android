package com.mk.myapplication;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Resident_dao {
    private static final String TAG = "mysql11111";



    //登录验证
    public boolean login(String contact_number, String password) {
        Connection conn = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        Boolean t=false;

        conn = MyConnect.getConn();

        String sql = "SELECT password FROM resident WHERE  contact_number= ?";
        try  {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contact_number);
            rs = pt.executeQuery();
            if (rs.next()) {
                t= true;
            } else {
                t=false;
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
            MyConnect.close(conn,pt,rs);
            return t;
    }

    public boolean register(Resident resident) {
        Connection conn = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        Boolean t=false;

        String sql = "insert into resident(resident_name, contact_number,password,house_number,gender,age,id) values (?,?,?,?,?,?,?)";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, resident.getResident_name());
            pt.setString(2, resident.getContact_number());
            pt.setString(3, resident.getPassword());
            pt.setInt(4, resident.getHouse_number());
            pt.setString(5, resident.getGender());
            pt.setInt(6, resident.getAge());
            pt.setString(7, resident.getId());
            int value = pt.executeUpdate();
            if (value > 0) {
                t= true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            MyConnect.close(conn,pt);
        return t;
    }

    public boolean isUserExist(String contact_number) {

        Connection conn = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        Boolean t=false;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://192.168.61.163:3306/community", "root", "1234567");
            String sql = "SELECT contact_number FROM resident WHERE contact_number = ?";
            pt = conn.prepareStatement(sql);
            Log.d(TAG, "数据库连接成功");
            pt = conn.prepareStatement(sql);
            pt.setString(1, contact_number);

            rs = pt.executeQuery();
            if (rs.next()) {
                t= true;
            } else {
                t=false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        MyConnect.close(conn,pt,rs);
        return t;
    }

        public Resident getUserInfo(String username) {
            System.out.println("name："+username);
            Resident resident = null;
            Connection conn = null;
            PreparedStatement pt = null;
            ResultSet rs = null;

            try {
                conn = DriverManager.getConnection("jdbc:mysql://192.168.61.163:3306/community", "root", "1234567");
                String sql = "SELECT * FROM resident WHERE contact_number = ?";
                pt = conn.prepareStatement(sql);
                pt.setString(1, username);

                rs = pt.executeQuery();

                if (rs.next()) {
                    resident = new Resident();
                    resident.setResident_name(rs.getString("resident_name"));
                    resident.setContact_number(rs.getString("contact_number"));
                    resident.setPassword(rs.getString("password"));
                    resident.setHouse_number(rs.getInt("house_number"));
                    resident.setGender(rs.getString("gender"));
                    resident.setAge(rs.getInt("age"));
                    resident.setId(rs.getString("id"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // 关闭数据库连接
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pt != null) {
                        pt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("resident："+resident);
            return resident;
        }
}