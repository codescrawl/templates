package com.precisely.engage.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.precisely.engage.templates.model.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TemplatesControllerTest {
    private final Template[] templates;
    @Autowired
    private TemplatesController controller;

    public TemplatesControllerTest() {
        try {
            // Read some test templates from a JSON document in test/resources.
            var url = this.getClass().getResource("templates.json");
            assert url != null;
            String json = urlToString(url, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            templates = mapper.readValue(json, Template[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Read the contents of a URL into a String.
    public static String urlToString(URL url, Charset encoding) throws IOException {
        String content;
        try (Scanner scanner = new Scanner(url.openStream(), String.valueOf(encoding))) {
            content = scanner.useDelimiter("\\A").next();
        }
        return content;
    }

    @Test
    void createTemplate() {
        ResponseEntity<Template> response = controller.createTemplate("T1", templates[0]);

        // We should get HTTP OK with a body.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // We should get the expected template back from the tenant.
        Template template = response.getBody();
        assertThat(template.getName()).isEqualTo(templates[0].getName());
        assertThat(template.getContent()).isEqualTo(templates[0].getContent());
        assertThat(template.getId()).isNotNull();

        // Clean up the template to make the test atomic.
        controller.deleteTemplate(template.getId(), "T1");
    }

    @Test
    void getTemplate() {
        // Create a template in the repository.
        Template expected = controller.createTemplate("T1", templates[0]).getBody();
        assert expected != null;

        ResponseEntity<Template> response = controller.getTemplate(expected.getId(), "T1");

        // We should get HTTP OK with a body.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // We should get the expected template back from the tenant.
        Template template = response.getBody();
        assertThat(template).isEqualTo(expected);
    }

    @Test
    void deleteTemplateWithEmptyRepository() {
        ResponseEntity<Void> response = controller.deleteTemplate(99L, "T1");

        // We should get HTTP OK with no body.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void deleteTemplate() {
        // Create a template in a tenant.
        Template expected = controller.createTemplate("T1", templates[0]).getBody();
        assert expected != null;

        ResponseEntity<Void> response = controller.deleteTemplate(expected.getId(), "T1");

        // We should get HTTP OK with no body.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        // Clean up the template to make the test atomic.
        controller.deleteTemplate(expected.getId(), "T1");
    }

    @Test
    void getAllTemplatesWithEmptyRepository() {
        validateGetAllTemplates(List.of(), "T1");
    }

    @Test
    void getAllTemplates() {
        // Create 3 templates, two in one tenant and one in the other.
        Template template1 = controller.createTemplate("T1", templates[0]).getBody();
        Template template2 = controller.createTemplate("T1", templates[1]).getBody();
        Template template3 = controller.createTemplate("T2", templates[2]).getBody();
        assert template1 != null;
        assert template2 != null;
        assert template3 != null;

        validateGetAllTemplates(List.of(template1, template2), "T1");
        validateGetAllTemplates(List.of(template3), "T2");

        // Clean up the templates to make the test atomic.
        controller.deleteTemplate(template1.getId(), "T1");
        controller.deleteTemplate(template2.getId(), "T1");
        controller.deleteTemplate(template3.getId(), "T2");
    }

    // Call getAllTemplates and validate the response against a list of expected templates.
    private void validateGetAllTemplates(List<Template> expected, String tenantId) {
        ResponseEntity<List<Template>> response = controller.getAllTemplates(tenantId);

        // We should get HTTP OK with a body.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // We should get the expected templates back from the tenant.
        List<Template> templates = response.getBody();
        assertThat(templates).isEqualTo(expected);
    }
}