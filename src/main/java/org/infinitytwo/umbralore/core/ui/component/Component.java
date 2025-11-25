package org.infinitytwo.umbralore.core.ui.component;

public interface Component {
    void draw();
    
    void setAngle(float angle);
    
    void setDrawOrder(int z);
    int getDrawOrder();
}
