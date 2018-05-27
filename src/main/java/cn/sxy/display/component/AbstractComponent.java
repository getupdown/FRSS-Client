package cn.sxy.display.component;

import java.awt.Component;

public abstract class AbstractComponent {
    // 对外暴露的component
    protected Component externalComponent;

    // 操作号，用于建立
    private int opeNum;

    public Component getExternalComponent() {
        return externalComponent;
    }

    public void setExternalComponent(Component externalComponent) {
        this.externalComponent = externalComponent;
    }
}
