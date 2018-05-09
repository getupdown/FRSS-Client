import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class A {
    int count = 10;//文本框1的字符数量控制
    JFrame javaFrame = new JFrame("JAVA窗体");
    JPanel javaZongPanel = new JPanel();
    JTextField javaTextField1 = new JTextField();
    JTextField javaTextField2 = new JTextField();

    MyKeyListener myKeyListener = new MyKeyListener();
    private boolean fromInputMethod = false;

    public A() {
        javaFrame.setBounds(200, 150, 500, 300);
        javaTextField1.setBounds(20, 50, 400, 20);
        javaTextField2.setBounds(20, 80, 400, 20);
        javaFrame.setLayout(null);
        javaTextField1.addKeyListener(myKeyListener);
        javaTextField1.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                fromInputMethod = true;
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                // do nothing
            }
        });

        javaFrame.add(javaTextField1);
        javaFrame.add(javaTextField2);

        javaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        javaFrame.setVisible(true);
    }


    class MyKeyListener implements KeyListener {
        int position = 0;
        String newStr = null;

        public void keyReleased(KeyEvent e) {
            if (32 == e.getKeyCode()) {
                System.out.println("输入法");
                fromInputMethod = false;
            }
            if (fromInputMethod) return;

            newStr = javaTextField1.getText();

            if (newStr.length() > count) {
                position = javaTextField1.getCaretPosition();
                javaTextField1.setText(newStr.substring(0, count));
                javaTextField2.setText(newStr.substring(count, newStr.length())+javaTextField2.getText());
                if (position >= count) {
                    javaTextField2.requestFocus();
                    javaTextField2.setCaretPosition(position-count);
                }else{
                    javaTextField1.setCaretPosition(position);
                }
            }
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    public static void main(String[] args) {
        new A();
    }
}