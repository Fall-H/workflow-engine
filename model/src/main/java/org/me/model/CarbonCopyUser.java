package org.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抄送人
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarbonCopyUser {
    private User user;
    /**
     * 抄送方式 0 邮件 1 短信 2 自定义
     */
    private int carbonCopyPattern;
}
