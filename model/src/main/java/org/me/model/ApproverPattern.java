package org.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审批模式
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproverPattern {
    /**
     * 审批模式 0 所有审批人同时通过 1 通过大于不通过
     */
    private int pattern;
}
