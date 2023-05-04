package com.precisely.engage.templates;

import com.precisely.engage.templates.model.Template;
import jakarta.persistence.Entity;

@Entity
public class TenantedTemplate extends Template {
    private String tenantId;

    public TenantedTemplate() {
        super();
    }

    public static TenantedTemplate fromTemplate(Template template, String tenantId) {
        TenantedTemplate tenanted = new TenantedTemplate();
        tenanted.setId(template.getId());
        tenanted.setName(template.getName());
        tenanted.setContent(template.getContent());
        tenanted.setTenantId(tenantId);
        return tenanted;
    }

    public Template asTemplate() {
        Template template = new Template();
        template.setId(getId());
        template.setName(getName());
        template.setContent(getContent());
        return template;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
