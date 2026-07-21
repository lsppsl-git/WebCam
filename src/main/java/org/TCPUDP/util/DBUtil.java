package org.TCPUDP.util;

import java.sql.*;

public class DBUtil {
    // 修改URL，添加allowPublicKeyRetrieval=true参数
    private static final String URL = "jdbc:mysql://localhost:3306/chat_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    static {
        try {
            // 使用新的驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); }
        catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); }
        catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
}