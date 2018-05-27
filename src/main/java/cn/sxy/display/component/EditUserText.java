package cn.sxy.display.component;

import javax.annotation.PostConstruct;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EditUserText extends AbstractComponent {

    private JTextArea userText;

    @PostConstruct
    public void init() {
        userText = new JTextArea();
        userText.setBounds(60, 60, 550, 430);
        userText.setRows(20);
        userText.setColumns(40);

        externalComponent = new JScrollPane(userText);
        JScrollPane jScrollPane = (JScrollPane) externalComponent;
        jScrollPane.setBounds(60, 60, 550, 430);
        jScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    }
}
