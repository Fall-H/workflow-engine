package org.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproverUser {
    private User user;
    /**
     * 是否通过审批
     * 0 通过 1 不通过 2 未审批
     */
    private int approved;
}
