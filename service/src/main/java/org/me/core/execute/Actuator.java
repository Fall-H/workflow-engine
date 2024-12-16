package org.me.core.execute;

import org.me.model.PropertyValue;
import org.me.model.PropertyValues;
import org.me.service.WorkflowLoadService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行器
 */
@Component
public class Actuator {
    private Map<String, PropertyValues> propertiesMap = new HashMap<>();
    private Map<String, Runnable> workflowRunable = new HashMap<>();

    @Resource
    private WorkflowLoadService workflowLoadService;

    public void createFlow(String name, String filePath) {
        PropertyValues propertyValues = workflowLoadService.loadWorkflow(filePath);
        propertyValues.createIndex();
        propertiesMap.put(name, propertyValues);

        workflowRunable.put(name, new ExecuteRunnable(propertyValues));
        workflowRunable.get(name).run();
    }

    public class ExecuteRunnable extends ActuatorHandle implements Runnable {
        private PropertyValues propertyValues;

        public ExecuteRunnable(PropertyValues propertyValues) {
            this.propertyValues = propertyValues;
        }

        @Override
        public void run() {

        }

        public void execute(String name) {
            PropertyValue propertyValue = propertyValues.getPropertyValueIndex().get(propertyValues.getCurrentId());

            // 执行块
            if (propertyValue == null) {
                throw new RuntimeException("未查询到执行块");
            }

            switch (propertyValue.getPattern()) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    }


}
