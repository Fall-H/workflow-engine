package org.me;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.me.core.execute.Actuator;
import org.me.service.WorkflowLoadService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    private WorkflowLoadService workflowLoadService;

    @Resource
    private Actuator actuator;

    private String name;

    @Before
    public void generateName() {
        name = UUID.randomUUID().toString();
    }

    @Test
    public void testJudgeActuator() {
        testCreateActuator(name);
        testUpdateActuator(name);
        Assert.assertEquals(actuator.getPropertyMap().get(name).isHasExecutionCompleted(), true);
    }

    public void testCreateActuator(String name) {
        actuator.createFlow(name, "classpath:workflow/Test.xml");
        return;
    }

    public void testUpdateActuator(String name) {
        actuator.getPropertyMap().get(name).getPropertyValueIndex().get(2L).getParams().put("approverUser_0", "{\"approved\":1}");
        actuator.getEventManager().notify(name);
        return;
    }

    @Test
    public void testGenerate() {
        testGenerateXml("test");
    }

    public void testGenerateXml(String name) {
        actuator.createFlow(name, "classpath:workflow/filex.xml");
    }

}
