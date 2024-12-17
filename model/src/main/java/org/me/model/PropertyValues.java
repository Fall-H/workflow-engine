package org.me.model;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 整个工作流
 */
@XmlRootElement(name = "propertyValues")
public class PropertyValues {
    /**
     * 当前执行到的块
     */
    @Getter
    private long currentId;
    /**
     * 索引
     */
    @Getter
    private Map<Long, PropertyValue> propertyValueIndex = new HashMap<Long, PropertyValue>();

    private List<PropertyValue> propertyValue;

    /**
     * 当前线程
     */
    @Getter
    @XmlTransient
    private Object lock = new Object();

    @XmlElement(name = "propertyValue")
    public List<PropertyValue> getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(List<PropertyValue> propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void createIndex() {
        for (PropertyValue temp : propertyValue) {
            propertyValueIndex.put(temp.getPropertyId(), temp);
        }
    }

    public void setCurrentId(long currentId) {
        this.currentId = currentId;
    }

    public void setPropertyValueIndex(Map<Long, PropertyValue> propertyValueIndex) {
        this.propertyValueIndex = propertyValueIndex;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }
}