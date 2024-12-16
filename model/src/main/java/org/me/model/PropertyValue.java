package org.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

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
    private List<Long> propertyIds;
    /**
     * 执行逻辑 0 当审批判断为真执行 第一个模块 否则 反之
     */
    private int executionLogic;

    /**
     * 审批人
     */
    private List<ApproverUser> approverUsers;
    /**
     * 审批模式
     */
    private ApproverPattern approverPattern;
    /**
     * 抄送人
     */
    private CarbonCopyUser carbonCopyUser;
    /**
     * 本单元文本内容
     */
    private String msg;

    /**
     * 文件
     */
    private List<File> files;
}
