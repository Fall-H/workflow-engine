package org.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 一个流程模块最小单元
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue {
    private long propertyId;
    /**
     * 本单元类型 0 开始 1 执行过程 2 判断 3 结束
     */
    private int pattern;
    /**
     * 被链接的下一个模块
     */
    private List<Long> nextPropertyIds;
    /**
     * 执行逻辑
     */
    private String executionLogic;
    /**
     * 参数表
     */
    private volatile Map<String, String> params;
    /**
     * 本单元文本内容
     */
    private String msg;
    /**
     * 文件
     */
    private List<File> files;

    private double x;
    private double y;
    private double width;
    private double height;
}
