package com.mk.myapplication;

import android.util.Log;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Resident_dao {
    private static final String TAG = "mysql11111";
    private Connection conn = null;
    private PreparedStatement pt = null;
    private ResultSet rs = null;

    // 登录验证
    public boolean login(String contactNumber, String password) {
        boolean isValid = false;
        conn = MyConnect.getConn();
        String sql = "SELECT password FROM resident WHERE contact_number= ?";
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();
            isValid = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MyConnect.close(conn, pt, rs);
        return isValid;
    }

    // 注册
    public boolean register(Resident resident) {
        boolean isSuccess = false;
        String sql = "INSERT INTO resident(resident_name, contact_number, password, house_number, gender, age, id) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            isSuccess = (value > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MyConnect.close(conn, pt);
        return isSuccess;
    }

    // 检查用户是否存在
    public boolean isUserExist(String contactNumber) {
        boolean isExist = false;
        String sql = "SELECT * FROM resident WHERE contact_number = ?";
        conn=MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();
            isExist = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            MyConnect.close(conn, pt, rs);
        }
        return isExist;
    }
    // 更新用户登录日期
    public boolean updateCheckInDate(String contactNumber) {
        boolean isSuccess = false;
        String sql = "UPDATE resident SET check_in_date = CURRENT_TIMESTAMP WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            int rowsAffected = pt.executeUpdate();
            isSuccess = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt);
        }
        return isSuccess;
    }

    // 获取用户信息
    public Resident getUserInfo(String contactNumber) {
        Resident resident = null;
        String sql = "SELECT *, DATE_FORMAT(check_in_date, '%Y-%m-%d %H:%i:%s') AS checkin_date_formatted FROM resident WHERE contact_number = ?";        conn=MyConnect.getConn();
        try {

            pt = conn.prepareStatement(sql);

            pt.setString(1, contactNumber);
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
                resident.setCheckInDate(rs.getString("checkin_date_formatted"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return resident;
    }
    public boolean rootlogin(String usname, String password) {
        boolean isValid = false;
        conn = MyConnect.getConn();
        String sql = "SELECT password FROM root WHERE username= ?";
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, usname);
            rs = pt.executeQuery();
            isValid = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MyConnect.close(conn, pt, rs);
        return isValid;
    }
    public boolean isrootExist(String usname) {
        boolean isExist = false;
        String sql = "SELECT * FROM root WHERE username = ?";
        conn=MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, usname);
            rs = pt.executeQuery();
            isExist = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            MyConnect.close(conn, pt, rs);
        }
        return isExist;
    }
}
