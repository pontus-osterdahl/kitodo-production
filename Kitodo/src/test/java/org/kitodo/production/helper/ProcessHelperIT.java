/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.MockDatabase;
import org.kitodo.SecurityTestUtils;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.SimpleMetadataViewInterface;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.exceptions.ProcessGenerationException;
import org.kitodo.production.forms.createprocess.ProcessFieldedMetadata;
import org.kitodo.production.process.TitleGenerator;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.utils.ProcessTestUtils;

public class ProcessHelperIT {

    public static final String DOCTYPE = "Monograph";
    private static final String ACQUISITION_STAGE_CREATE = "create";

    private static List<Locale.LanguageRange> priorityList;

    /**
     * Function to run before test is executed.
     *
     * @throws Exception
     *         the exception when set up test
     */
    @BeforeClass
    public static void setUp() throws Exception {
        MockDatabase.startNode();
        MockDatabase.insertProcessesFull();
        MockDatabase.setUpAwaitility();
        User userOne = ServiceManager.getUserService().getById(1);
        SecurityTestUtils.addUserDataToSecurityContext(userOne, 1);
        priorityList = ServiceManager.getUserService().getCurrentMetadataLanguage();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        MockDatabase.stopNode();
        MockDatabase.cleanDatabase();
    }

    /**
     * Tests the ProcessHelper functions of generating the atstsl fields.
     *
     * @throws DAOException
     *         the database exception.
     * @throws ProcessGenerationException
     *         the exception when process is generated.
     * @throws IOException
     *         the exception when ruleset is opened.
     */
    @Test
    public void generateAtstslFields() throws DAOException, ProcessGenerationException, IOException {
        Process process = new Process();
        process.setProject(ServiceManager.getProjectService().getById(1));
        process.setRuleset(ServiceManager.getRulesetService().getById(1));
        TempProcess tempProcess = new TempProcess(process, new Workpiece());
        RulesetManagementInterface rulesetManagementInterface = ServiceManager.getRulesetService()
                .openRuleset(tempProcess.getProcess().getRuleset());

        testGenerationOfAtstslByCurrentTempProcess(tempProcess, rulesetManagementInterface);

        testForceRegenerationOfAtstsl(tempProcess, rulesetManagementInterface);

        testForceRegenerationByTempProcessParents(tempProcess, rulesetManagementInterface);

        testForceRegenerationByParentProcess(tempProcess, rulesetManagementInterface);
    }
    
    @Test
    public void shouldReturnProcessTitleMetadata() throws DAOException, IOException {
        Process process = ServiceManager.getProcessService().getById(2);
        List<SimpleMetadataViewInterface> titleMetadataViews = (List) ProcessHelper.getProcessTitleMetadata(process, "create", priorityList);
        assertEquals(titleMetadataViews.get(0).getId(),"process_title");
    }

    private void testForceRegenerationByParentProcess(TempProcess tempProcess,
            RulesetManagementInterface rulesetManagementInterface) throws ProcessGenerationException, DAOException {
        ProcessHelper.generateAtstslFields(tempProcess, tempProcess.getProcessMetadata().getProcessDetailsElements(),
                null, DOCTYPE, rulesetManagementInterface, ACQUISITION_STAGE_CREATE, priorityList,
                ServiceManager.getProcessService().getById(2), true);
        assertEquals("Secopr", tempProcess.getAtstsl());
    }

    private void testForceRegenerationByTempProcessParents(TempProcess tempProcess,
            RulesetManagementInterface rulesetManagementInterface) throws DAOException, ProcessGenerationException {
        TempProcess tempProcessParent = new TempProcess(ServiceManager.getProcessService().getById(2), new Workpiece());
        tempProcess.getProcessMetadata().setProcessDetails(new ProcessFieldedMetadata() {
            {
                treeNode.getChildren()
                        .add(ProcessTestUtils.getTreeNode(TitleGenerator.TSL_ATS, TitleGenerator.TSL_ATS, ""));
            }
        });
        ProcessHelper.generateAtstslFields(tempProcess, tempProcess.getProcessMetadata().getProcessDetailsElements(),
                Collections.singletonList(tempProcessParent), DOCTYPE, rulesetManagementInterface,
                ACQUISITION_STAGE_CREATE, priorityList, null, true);
        assertEquals("Secopr", tempProcess.getAtstsl());
    }

    private void testForceRegenerationOfAtstsl(TempProcess tempProcess,
            RulesetManagementInterface rulesetManagementInterface) throws ProcessGenerationException {
        ProcessHelper.generateAtstslFields(tempProcess, tempProcess.getProcessMetadata().getProcessDetailsElements(),
                null, DOCTYPE, rulesetManagementInterface, ACQUISITION_STAGE_CREATE, priorityList, null, false);
        assertEquals("test", tempProcess.getAtstsl());
        ProcessHelper.generateAtstslFields(tempProcess, tempProcess.getProcessMetadata().getProcessDetailsElements(),
                null, DOCTYPE, rulesetManagementInterface, ACQUISITION_STAGE_CREATE, priorityList, null, true);
        assertEquals("test2", tempProcess.getAtstsl());
    }

    private void testGenerationOfAtstslByCurrentTempProcess(TempProcess tempProcess,
            RulesetManagementInterface rulesetManagementInterface) throws ProcessGenerationException {
        tempProcess.getProcessMetadata().setProcessDetails(new ProcessFieldedMetadata() {
            {
                treeNode.getChildren()
                        .add(ProcessTestUtils.getTreeNode(TitleGenerator.TSL_ATS, TitleGenerator.TSL_ATS, "test"));
            }
        });
        assertNull(tempProcess.getAtstsl());
        ProcessHelper.generateAtstslFields(tempProcess, tempProcess.getProcessMetadata().getProcessDetailsElements(),
                null, DOCTYPE, rulesetManagementInterface, ACQUISITION_STAGE_CREATE, priorityList, null, false);
        assertEquals("test", tempProcess.getAtstsl());
        tempProcess.getProcessMetadata().setProcessDetails(new ProcessFieldedMetadata() {
            {
                treeNode.getChildren()
                        .add(ProcessTestUtils.getTreeNode(TitleGenerator.TSL_ATS, TitleGenerator.TSL_ATS, "test2"));
            }
        });
    }

}
