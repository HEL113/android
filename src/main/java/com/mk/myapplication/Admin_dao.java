package com.mk.myapplication;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Admin_dao {
    private Connection conn = null;
    private PreparedStatement pt = null;
    private ResultSet rs = null;
    private static final String TAG = "mysql11111";

    public Resident getUser(String contactNumber) {
        Resident resident = null;
        conn=MyConnect.getConn();

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

    // 获取所有用户信息
    public List<Resident> getAllUsers() {
        List<Resident> userList = new ArrayList<>();
        String sql = "SELECT * FROM resident";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            rs = pt.executeQuery();
            while (rs.next()) {
                Resident resident = new Resident();
                resident.setResident_name(rs.getString("resident_name"));
                resident.setContact_number(rs.getString("contact_number"));
                resident.setPassword(rs.getString("password"));
                resident.setHouse_number(rs.getInt("house_number"));
                resident.setGender(rs.getString("gender"));
                resident.setAge(rs.getInt("age"));
                resident.setId(rs.getString("id"));
                resident.setCheckInDate(rs.getString("check_in_date"));

                userList.add(resident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt, rs);
        }
        return userList;
    }

    // 删除用户
    public boolean deleteUser(String contactNumber) {
        boolean isSuccess = false;
        String sql = "DELETE FROM resident WHERE contact_number = ?";
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

    // 更新用户信息
    public boolean updateUserInfo(String name, String id,String contactNumber, int houseNumber,String password,String gender,int age)
        {
        boolean isSuccess = false;
        String sql = "UPDATE resident SET check_in_date = check_in_date,resident_name = ?,contact_number = ?, id = ?, house_number = ? ,password=?,gender=?,age=? WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, name);
            pt.setString(2, contactNumber);
            pt.setString(3, id);
            pt.setInt(4, houseNumber);
            pt.setString(5,password);
            pt.setString(6,gender);
            pt.setInt(7,age);
            pt.setString(8, contactNumber);

            int rowsAffected = pt.executeUpdate();
            isSuccess = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt);
        }
        return isSuccess;
    }
    public boolean insertUserInfo(String name, String id,String contactNumber, int houseNumber,String password,String gender,int age) {
        boolean isSuccess = false;
        String sql = "INSERT INTO resident(resident_name,id ,gender, age,contact_number, house_number, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1,name);
            pt.setString(2, id);
            pt.setString(3, gender);
            pt.setInt(4, age);
            pt.setString(5, contactNumber);
            pt.setInt(6, houseNumber);
            pt.setString(7, password);
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
}
