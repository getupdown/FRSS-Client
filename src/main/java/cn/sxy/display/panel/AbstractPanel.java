package cn.sxy.display.panel;

import java.util.List;

import javax.swing.JPanel;

import cn.sxy.display.component.AbstractComponent;

public abstract class AbstractPanel {

    protected JPanel jPanel;

    protected List<AbstractComponent> components;

    public void addComponents() {
        for (AbstractComponent component : components) {
            jPanel.add(component.getExternalComponent());
        }
    }

    public List<AbstractComponent> getComponents() {
        return components;
    }

    public void setComponents(List<AbstractComponent> components) {
        this.components = components;
    }
}
