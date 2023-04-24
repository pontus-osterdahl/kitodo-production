package org.kitodo.production.forms;

import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.MockDatabase;
import org.kitodo.SecurityTestUtils;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.beans.Workflow;
import org.kitodo.data.database.beans.Docket;
import org.kitodo.data.database.beans.Template;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.production.services.ServiceManager;

import java.text.MessageFormat;

public class TemplateFormIT {

    private TemplateForm templateForm = new TemplateForm();
    private Template template;

    @BeforeClass
    public static void prepareDatabase() throws Exception {
        MockDatabase.startNode();
        MockDatabase.insertProcessesFull();
    }

    @Before
    public void setUpTemplate() throws DAOException {
        template = ServiceManager.getTemplateService().getById(1);
        Workflow workflow = template.getWorkflow();
        workflow.setTitle("gateway-test1");
    }

    @Test
    public void shouldRedirect() throws DAOException {
        templateForm.setTemplate(template);
        String redirectUrl = templateForm.save();
        Assert.assertEquals("/pages/projects?faces-redirect=true", redirectUrl);
    }

    @Test
    public void shouldNotRedirectNullRuleset() throws DAOException {
        Ruleset oldRuleset = template.getRuleset();
        template.setRuleset(null);
        templateForm.setTemplate(template);
        String redirectUrl = templateForm.save();
        Assert.assertEquals(null, redirectUrl);

        Template savedTemplate = ServiceManager.getTemplateService().getById(1);
        Assert.assertTrue(Objects.isNull(template.getRuleset()));
        Assert.assertTrue(Objects.nonNull(savedTemplate.getRuleset()));

        template.setRuleset(oldRuleset);
        redirectUrl = templateForm.save();
        Assert.assertEquals("/pages/projects?faces-redirect=true", redirectUrl);
    }

    @Test
    public void shouldNotRedirectNullDocket() throws DAOException {
        Docket oldDocket = template.getDocket();
        template.setDocket(null);
        templateForm.setTemplate(template);
        String redirectUrl = templateForm.save();
        Assert.assertEquals(null, redirectUrl);

        Template savedTemplate = ServiceManager.getTemplateService().getById(1);
        Assert.assertTrue(Objects.isNull(template.getDocket()));
        Assert.assertTrue(Objects.nonNull(savedTemplate.getDocket()));

        template.setDocket(oldDocket);
        redirectUrl = templateForm.save();
        Assert.assertEquals("/pages/projects?faces-redirect=true", redirectUrl);
    }
    
    @Test
    public void shouldNotRedirectEmptyTitle() throws DAOException {
        String oldTitle = template.getTitle();
        template.setTitle("");
        templateForm.setTemplate(template);
        String redirectUrl = templateForm.save();
        Assert.assertEquals(null, redirectUrl);

        Template savedTemplate = ServiceManager.getTemplateService().getById(1);
        Assert.assertTrue(template.getTitle().equals(""));
        Assert.assertTrue(savedTemplate.getTitle().equals(oldTitle));

        template.setTitle(oldTitle);
        redirectUrl = templateForm.save();
        Assert.assertEquals("/pages/projects?faces-redirect=true", redirectUrl);
    }

    @AfterClass
    public static void cleanDatabase() throws Exception {
        MockDatabase.stopNode();
        MockDatabase.cleanDatabase();
    }
}
