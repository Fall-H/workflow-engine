package org.me.core.logic;

import com.alibaba.fastjson.JSONObject;
import org.me.model.ApproverUser;
import org.me.model.PropertyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserLogic {
    public int verify(PropertyValue propertyValue) {
        Map<String, String> data = propertyValue.getParams();

        List<ApproverUser> approverUsers = new ArrayList<ApproverUser>();

        data.forEach((k, v) -> {
            if (!(k.split("_")[0].equals("approverUser"))) {
                return;
            }

            approverUsers.add(JSONObject.parseObject(v, ApproverUser.class));
        });

        ApproverUser approverUser = approverUsers.get(0);

        return approverUser.getApproved();
    }
}
