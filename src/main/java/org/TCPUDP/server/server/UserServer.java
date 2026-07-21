package org.TCPUDP.server.server;

import org.TCPUDP.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;

public class UserServer {
    // 用户注册
    public int userRegister(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 检查用户是否已存在
            pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return 1; // 用户已存在
            }
            // 添加新用户
            pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, password); // 实际应用中应加密
            pstmt.executeUpdate();
            return 0; // 注册成功
        } catch (SQLException e) {
            e.printStackTrace();
            return 2; // 注册失败
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // 用户登录
    public boolean userLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // 更新登录状态
                pstmt = conn.prepareStatement("UPDATE users SET is_login = TRUE WHERE username = ?");
                pstmt.setString(1, username);
                pstmt.executeUpdate();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // 用户登出
    public void userLogout(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("UPDATE users SET is_login = FALSE WHERE username = ?");
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 获取在线用户列表
    public ArrayList<String> getOnlineUsers() {
        ArrayList<String> onlineUsers = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT username FROM users WHERE is_login = TRUE");
            while (rs.next()) {
                onlineUsers.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return onlineUsers;
    }

    // 检查用户是否存在
    public boolean isUserExist(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}