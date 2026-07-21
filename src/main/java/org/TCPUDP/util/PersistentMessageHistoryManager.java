package org.TCPUDP.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersistentMessageHistoryManager {
    // 保存消息到数据库
    public void saveMessage(String sender, String receiver, String content) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)");
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 获取私聊历史记录
    public List<String> getPrivateChatHistory(String user1, String user2, int limit) {
        List<String> history = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT sender, content, timestamp FROM messages " +
                    "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                    "ORDER BY timestamp LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            pstmt.setInt(5, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String formattedMessage = "[" + timestamp + "] " + sender + ": " + content;
                history.add(formattedMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return history;
    }

    // 获取群聊历史记录
    public List<String> getChatRoomHistory(int limit) {
        List<String> history = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT sender, content, timestamp FROM messages " +
                    "WHERE receiver = 'chatRoom' " +
                    "ORDER BY timestamp LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String formattedMessage = "[" + timestamp + "] " + sender + ": " + content;
                history.add(formattedMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return history;
    }
}