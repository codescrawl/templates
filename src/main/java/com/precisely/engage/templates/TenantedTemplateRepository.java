package com.precisely.engage.templates;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TenantedTemplateRepository extends CrudRepository<TenantedTemplate, Long> {
    TenantedTemplate findByIdAndTenantId(long id, String tenantId);

    @Transactional
    void deleteByIdAndTenantId(long id, String tenantId);

    List<TenantedTemplate> findAllByTenantId(String tenantId);
}
