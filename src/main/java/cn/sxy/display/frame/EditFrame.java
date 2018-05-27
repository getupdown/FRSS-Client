package cn.sxy.display.frame;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;

public class EditFrame extends AbstractFrame {

    @PostConstruct
    public void init() {
        externalFrame = new JFrame();
        JFrame jFrame = (JFrame) externalFrame;
        jFrame.setVisible(true);
    }
}
