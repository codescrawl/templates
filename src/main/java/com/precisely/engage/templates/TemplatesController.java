package com.precisely.engage.templates;

import com.precisely.engage.templates.api.TemplatesApi;
import com.precisely.engage.templates.model.Template;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TemplatesController implements TemplatesApi {
    TenantedTemplateRepository repository;

    public TemplatesController(TenantedTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Template> createTemplate(String tenantId, Template template) {
        var tenanted = TenantedTemplate.fromTemplate(template, tenantId);
        var saved = repository.save(tenanted);
        return ResponseEntity.ok(saved.asTemplate());
    }

    @Override
    public ResponseEntity<Template> getTemplate(Long id, String tenantID) {
        TenantedTemplate tenanted = repository.findByIdAndTenantId(id, tenantID);
        if (tenanted != null) {
            return ResponseEntity.ok(tenanted.asTemplate());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //    @Override
//    public ResponseEntity<Template> getTemplate(Long id, String tenantId) {
//        TenantedTemplate tenanted = repository.findByIdAndTenantId(id, tenantId);
//        if (tenanted != null) {
//            return ResponseEntity.ok(tenanted.asTemplate());
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    @Override
    public ResponseEntity<Void> deleteTemplate(Long id, String tenantId) {
        try {
            repository.deleteByIdAndTenantId(id, tenantId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<Template>> getAllTemplates(String tenantId) {
        var tenanted = repository.findAllByTenantId(tenantId);
        List<Template> result = new ArrayList<>();
        tenanted.forEach(tenantedTemplate -> result.add(tenantedTemplate.asTemplate()));
        return ResponseEntity.ok(result);
    }
}
