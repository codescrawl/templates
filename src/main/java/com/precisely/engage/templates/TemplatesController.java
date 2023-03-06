package com.precisely.engage.templates;

import com.precisely.engage.templates.api.TemplatesApi;
import com.precisely.engage.templates.model.Template;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class TemplatesController implements TemplatesApi {
    @Override
    public ResponseEntity<Template> createTemplate(String tenantID, Template template) {
        return TemplatesApi.super.createTemplate(tenantID, template);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(UUID id, String tenantID) {
        return TemplatesApi.super.deleteTemplate(id, tenantID);
    }

    @Override
    public ResponseEntity<List<Template>> getAllTemplates(String tenantID) {
        return TemplatesApi.super.getAllTemplates(tenantID);
    }

    @Override
    public ResponseEntity<Template> getTemplate(UUID id, String tenantID) {
        return TemplatesApi.super.getTemplate(id, tenantID);
    }

    @Override
    public ResponseEntity<Template> updateTemplate(UUID id, String tenantID, Template template) {
        return TemplatesApi.super.updateTemplate(id, tenantID, template);
    }
}
