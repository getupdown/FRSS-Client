package cn.sxy.display.frame;

import java.util.List;

import javax.swing.JFrame;

import cn.sxy.display.panel.AbstractPanel;

public abstract class AbstractFrame {

    protected JFrame externalFrame;

    protected List<AbstractPanel> components;

    public List<AbstractPanel> getComponents() {
        return components;
    }

    public void setComponents(List<AbstractPanel> components) {
        this.components = components;
    }
}
