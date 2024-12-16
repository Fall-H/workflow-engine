package org.me.api.controller;

import org.me.core.execute.Actuator;
import org.me.model.PropertyValue;
import org.me.model.PropertyValues;
import org.me.service.WorkflowLoadService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WorkflowController {
    @Resource
    private WorkflowLoadService workflowLoadService;
    private Map<Long, PropertyValue> propertyValueMap = new HashMap<Long, PropertyValue>();

    @Resource
    private Actuator actuator;

    public void loadPropertyValues() {
        PropertyValues propertyValues = workflowLoadService.loadWorkflow("D:\\WorkflowEngine\\api\\src\\main\\resources\\workflow\\Test.xml");
        return;
    }

    public void createActuator() {
        actuator.createFlow("Test", "D:\\WorkflowEngine\\api\\src\\main\\resources\\workflow\\Test.xml");
        return;
    }
}
