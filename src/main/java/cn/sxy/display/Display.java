package cn.sxy.display;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Display {

    private static Logger logger = LoggerFactory.getLogger(Display.class);

    public void show() {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("Login Example");
        // Setting the width and height of frame
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的asd div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Display display = new Display();
        display.show();
    }

    private void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        /*
         * 创建文本域用于用户输入
         */

        JTextArea userText = new JTextArea();
        userText.setBounds(60, 60, 550, 430);
        userText.setRows(20);
        userText.setColumns(40);

        JScrollPane js = new JScrollPane(userText);
        js.setBounds(60, 60, 550, 430);
        js.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(js);

        JTable jtAble = new JTable();
        jtAble.setBounds(708, 60, 220, 430);
        panel.add(jtAble);

        userText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                logger.info("在{}插入{}个字符", e.getOffset(), e.getLength());
                // 构造插入信息
                //Message msg = MessageFactory.newInsertMessage(e.getOffset(), e.getLength());
                //fileMessageHandler.Receive(msg);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                logger.info("删掉从{}以后{}个字符", e.getOffset(), e.getLength());
                // 构造删除信息
                // Message msg = MessageFactory.newRemoveMessage(e.getOffset(), e.getLength());
                // fileMessageHandler.Receive(msg);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("修改");
                System.out.println(e.getOffset());
                System.out.println(e.getLength());
            }
        });

        JButton jButton = new JButton();
        jButton.setBounds(200, 515, 150, 40);
        jButton.setText("保存");
        panel.add(jButton);

    }
}
