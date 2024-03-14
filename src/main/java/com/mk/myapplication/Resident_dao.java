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
import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO resident(resident_name, contact_number, password, house_number, gender, age, id,token) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        String sqlToken = "INSERT INTO token(contact_number, token) VALUES (?, ?)";
        String sqlexp = "INSERT INTO express(contact_number, token) VALUES (?, ?)";
        Connection conn = MyConnect.getConn();
        try {
            conn.setAutoCommit(false); // 设置手动提交事务

            pt = conn.prepareStatement(sql);
            pt.setString(1, resident.getResident_name());
            pt.setString(2, resident.getContact_number());
            pt.setString(3, resident.getPassword());
            pt.setInt(4, resident.getHouse_number());
            pt.setString(5, resident.getGender());
            pt.setInt(6, resident.getAge());
            pt.setString(7, resident.getId());
            pt.setString(8, "0");
            int residentResult = pt.executeUpdate();

            pt = conn.prepareStatement(sqlToken);
            pt.setString(1, resident.getContact_number());
            pt.setString(2, "0");
            int tokenResult = pt.executeUpdate();

            pt = conn.prepareStatement(sqlexp);
            pt.setString(1, resident.getContact_number());
            pt.setString(2, "0");
            int tokenexp = pt.executeUpdate();

            if (residentResult > 0 && tokenResult > 0&& tokenexp>0) {
                conn.commit(); // 提交事务
                isSuccess = true;
            } else {
                conn.rollback(); // 回滚事务
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback(); // 出现异常时回滚事务
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {

                MyConnect.close(conn, pt);
                }

        return isSuccess;
    }


    // 检查用户是否存在
    public boolean isUserExist(String contactNumber) {
        boolean isExist = false;
        String sql = "SELECT * FROM resident WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();
            isExist = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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

    public String getuser(String contactNumber) {
        String residentName = null;
        String sql1 = "SELECT resident_name FROM resident WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql1);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();
            if (rs.next()) {
                residentName = rs.getString("resident_name");
                System.out.println(residentName + " 123123123");
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
        return residentName;
    }


    // 获取用户信息
    public Resident getUserInfo(String contactNumber) {
        Resident resident = null;
        String sql = "SELECT *, DATE_FORMAT(check_in_date, '%Y-%m-%d %H:%i:%s') AS checkin_date_formatted FROM resident WHERE contact_number = ?";
        conn = MyConnect.getConn();
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
                resident.setToken(rs.getString("token"));
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
    public boolean updatephone(String name, String id,String contactNumber, String contactNumber1 ) {
        boolean isSuccess = false;
        String sql = "UPDATE resident SET resident_name = ?, id = ?, contact_number = ? WHERE contact_number = ?";
        String sql1 = "UPDATE express SET contact_number = ? WHERE contact_number = ?";
        String sql2 = "UPDATE token SET contact_number = ? WHERE contact_number = ?";

        conn = MyConnect.getConn();

        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, name);
            pt.setString(2, id);
            pt.setString(3, contactNumber1);
            pt.setString(4, contactNumber);

            int rowsAffected = pt.executeUpdate();

            // 更新第二个表
            pt = conn.prepareStatement(sql1);
            pt.setString(1, contactNumber1);
            pt.setString(2, contactNumber);

            int rowsAffected1 = pt.executeUpdate();

            // 更新第三个表
            pt = conn.prepareStatement(sql2);
            pt.setString(1, contactNumber1);
            pt.setString(2, contactNumber);

            int rowsAffected2 = pt.executeUpdate();

            isSuccess = (rowsAffected > 0 && rowsAffected1 > 0 && rowsAffected2 > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt);
        }
        return isSuccess;
    }
    public boolean setphone(String name, String id, String contactNumber) {
        boolean isExist = false;

        String sql = "SELECT * FROM resident WHERE resident_name = ? AND id = ? AND contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, name);
            pt.setString(2, id);
            pt.setString(3, contactNumber);
            rs = pt.executeQuery();
            isExist = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt, rs);
        }
        return isExist;
    }

    //更新
    public boolean updateUserInfo(String contactNumber, String name, String id, int houseNumber, String password) {
        boolean isSuccess = false;
        String sql = "UPDATE resident SET resident_name = ?,id=?, house_number = ?, password = ? WHERE contact_number = ?";
        String sql1 = "UPDATE express SET name = ?  WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, name);
            pt.setString(2, id);
            pt.setInt(3, houseNumber);
            pt.setString(4, password);
            pt.setString(5, contactNumber);
            int rowsAffected = pt.executeUpdate();
            pt = conn.prepareStatement(sql1);
            pt.setString(1, name);
            pt.setString(2, contactNumber);
            int row = pt.executeUpdate();
            isSuccess = (rowsAffected > 0&& row>0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt);
        }
        return isSuccess;
    }
    public List<Express> seexp(String contactNumber) throws SQLException {
        List<Express> expressList = new ArrayList<>();
        String sql = "SELECT * FROM express WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();

            while (rs.next()) {
                Express express = new Express();
                express.setName(rs.getString("name"));
                express.setContact_number(rs.getString("contact_number"));
                express.setExname(rs.getString("exname"));
                expressList.add(express);
            }
        } finally {
            MyConnect.close(conn, pt, rs);
        }

        return expressList;
    }

    public boolean infotoken(String contactNumber, String token) {
        boolean isSuccess = false;
        String sqlToken = "UPDATE token SET token=? WHERE contact_number = ?";
        String sqlResident = "UPDATE resident SET token=? WHERE contact_number = ?";
        String sqlexp="UPDATE express SET token=? WHERE contact_number = ?";
        conn = MyConnect.getConn();
        try {
            // 更新 token 表
            pt = conn.prepareStatement(sqlToken);
            pt.setString(1, token);
            pt.setString(2, contactNumber);
            int valueToken = pt.executeUpdate();

            // 更新 resident 表
            pt = conn.prepareStatement(sqlResident);
            pt.setString(1, token);
            pt.setString(2, contactNumber);
            int valueResident = pt.executeUpdate();
            //更新 express
            pt=conn.prepareStatement(sqlexp);
            pt.setString(1, token);
            pt.setString(2, contactNumber);
            int valueexpress = pt.executeUpdate();
            // 判断两次更新操作是否都成功
            isSuccess = (valueToken > 0 && valueResident > 0 &&valueexpress>0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MyConnect.close(conn, pt);
        }

        return isSuccess;
    }
    //验证token
    public boolean iftoken(String contactNumber, String token) {
        boolean isValid = false;
        conn = MyConnect.getConn();
        String sql = "SELECT token FROM express WHERE contact_number= ?";
        try {
            pt = conn.prepareStatement(sql);
            pt.setString(1, contactNumber);
            rs = pt.executeQuery();

            if (rs.next()) {
                String dbToken = rs.getString("token");
                if (dbToken.equals(token)) {
                    isValid = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MyConnect.close(conn, pt, rs);

        return isValid;
    }
    public boolean ifex(String contactNumber) {
        boolean isValid = false;
        conn = MyConnect.getConn();
        String sql = "SELECT contact_number FROM express WHERE contact_number= ?";
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

    public boolean deleteexp(String contactNumber) {
        boolean isSuccess = false;
        String sql = "DELETE FROM express WHERE contact_number = ?";
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
    public void deleteexpInBackground(final String username) {
        new Thread(() -> {
            deleteexp(username);
        }).start();
    }


}
