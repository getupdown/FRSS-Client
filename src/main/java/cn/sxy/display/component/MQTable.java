package cn.sxy.display.component;

import javax.annotation.PostConstruct;
import javax.swing.JTable;

public class MQTable extends AbstractComponent {

    @PostConstruct
    public void init() {
        externalComponent = new JTable();
        JTable jTable = (JTable) externalComponent;
        jTable.setBounds(708, 60, 220, 430);
    }
}
