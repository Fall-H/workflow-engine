package org.me.service.impl;

import org.me.model.PropertyValues;
import org.me.service.WorkflowLoadService;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;

@Component
public class DefaultWorkflowLoadServiceImpl implements WorkflowLoadService {
    @Override
    public PropertyValues loadWorkflow(String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PropertyValues.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            FileInputStream fis = new FileInputStream(filePath);
            PropertyValues propertyValues = (PropertyValues) unmarshaller.unmarshal(fis);
            fis.close();
            propertyValues.setCurrentId(propertyValues.getPropertyValue().get(0).getPropertyId());
            return propertyValues;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
