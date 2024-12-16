package org.me.core.execute;

import org.me.core.observer.EventListener;
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

        workflowRunable.put(name, new ExecuteRunnable(name, propertyValues));
        workflowRunable.get(name).run();
    }

    public class ExecuteRunnable extends ActuatorHandle implements Runnable, EventListener {
        private PropertyValues propertyValues;
        private String name;

        public ExecuteRunnable(String name, PropertyValues propertyValues) {
            this.name = name;
            this.propertyValues = propertyValues;
        }

        @Override
        public void run() {
            execute(name);
        }

        public void execute(String name) {
            long currentId = propertyValues.getCurrentId();

            while (true) {
                PropertyValue propertyValue = propertyValues.getPropertyValueIndex().get(currentId);

                if (propertyValue == null) {
                    // 未查询到块
                    break;
                }

                propertyValue.setCurrentRunnable(this);

                // 执行块
                if (propertyValue == null) {
                    throw new RuntimeException("未查询到执行块");
                }

                switch (propertyValue.getPattern()) {
                    case 0:
                        currentId = startHandle(name, propertyValue);
                        break;
                    case 1:
                        currentId = executionProcessHandle(name, propertyValue);
                        break;
                    case 2:
                        currentId = judgeHandle(name, propertyValue);
                        break;
                    case 3:
                        currentId = endHandle(name, propertyValue);
                        break;
                }
            }
        }

        @Override
        public void update() {
            notify();
        }
    }


}
