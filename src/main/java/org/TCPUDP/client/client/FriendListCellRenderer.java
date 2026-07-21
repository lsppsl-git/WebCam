package org.TCPUDP.client.client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//列表渲染器
class FriendListCellRenderer extends JPanel implements ListCellRenderer<Friend>{
    private JLabel avatarLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel statusLabel = new JLabel();
    private JLabel onlineIndicator = new JLabel("●");

    public FriendListCellRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));//透明度
        // 状态指示器（在线/离线）
        onlineIndicator.setFont(new Font("Arial", Font.BOLD, 12));
        // 右侧信息面板
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 0));//网格布局：行列，间距
        infoPanel.add(nameLabel);
        infoPanel.add(statusLabel);

        // 添加组件
        add(avatarLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(onlineIndicator, BorderLayout.EAST);
    }

    public Component getListCellRendererComponent(JList<? extends Friend> list, Friend friend, int index, boolean isSelected, boolean cellHasFocus) {
        nameLabel.setText(friend.getName());
        statusLabel.setText(friend.getStatus());
        avatarLabel.setIcon(friend.getAvatar());
        // 设置在线状态颜色
        if (friend.isOnline()) {
            onlineIndicator.setForeground(Color.GREEN);
            nameLabel.setForeground(Color.BLACK);
        } else {
            onlineIndicator.setForeground(Color.GRAY);
            nameLabel.setForeground(Color.GRAY);
        }
        // 设置选中状态样式
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setOpaque(true);//设置不透明
        return this;
    }
}
