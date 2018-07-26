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

package org.kitodo.selenium.testframework.pages;

import org.kitodo.data.database.beans.LdapGroup;
import org.kitodo.selenium.testframework.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LdapGroupEditPage extends Page<LdapGroupEditPage> {

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:saveButton")
    private WebElement saveLdapGroupButton;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:title")
    private WebElement titleInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:userDn")
    private WebElement userDnInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:homeDirectory")
    private WebElement homeDirectoryInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:gidNumber")
    private WebElement gidNumberInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:objectClasses")
    private WebElement objectClassesInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaSid")
    private WebElement sambaSidInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sn")
    private WebElement snInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:uid")
    private WebElement uidInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:description")
    private WebElement descriptionInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:displayName")
    private WebElement displayNameInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:gecos")
    private WebElement gecosInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:loginShell")
    private WebElement loginShellInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaAcctFlags")
    private WebElement sambaAcctFlagsInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaLogonScript")
    private WebElement sambaLogonScriptInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaPrimaryGroupSid")
    private WebElement sambaPrimaryGroupSidInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaPwdMustChange")
    private WebElement sambaPwdMustChangeInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaPasswordHistory")
    private WebElement sambaPasswordHistoryInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaLogonHours")
    private WebElement sambaLogonHoursInput;

    @SuppressWarnings("unused")
    @FindBy(id = "editForm:ldabgroupTabView:sambaKickoffTime")
    private WebElement sambaKickoffTimeInput;

    public LdapGroupEditPage() {
        super("pages/ldapgroupEdit.jsf");
    }

    @Override
    public LdapGroupEditPage goTo() {
        return null;
    }

    public LdapGroupEditPage insertLdapGroupData(LdapGroup ldapGroup) {
        titleInput.sendKeys(ldapGroup.getTitle());
        descriptionInput.sendKeys(ldapGroup.getDescription());
        displayNameInput.sendKeys(ldapGroup.getDisplayName());
        gecosInput.sendKeys(ldapGroup.getGecos());
        gidNumberInput.sendKeys(ldapGroup.getGidNumber());
        homeDirectoryInput.sendKeys(ldapGroup.getHomeDirectory());
        loginShellInput.sendKeys(ldapGroup.getLoginShell());
        objectClassesInput.sendKeys(ldapGroup.getObjectClasses());
        sambaAcctFlagsInput.sendKeys(ldapGroup.getSambaAcctFlags());
        sambaKickoffTimeInput.sendKeys(ldapGroup.getSambaKickoffTime());
        sambaLogonHoursInput.sendKeys(ldapGroup.getSambaLogonHours());
        sambaLogonScriptInput.sendKeys(ldapGroup.getSambaLogonScript());
        sambaPasswordHistoryInput.sendKeys(ldapGroup.getSambaPasswordHistory());
        sambaPrimaryGroupSidInput.sendKeys(ldapGroup.getSambaPrimaryGroupSID());
        sambaPwdMustChangeInput.sendKeys(ldapGroup.getSambaPwdMustChange());
        sambaSidInput.sendKeys(ldapGroup.getSambaSID());
        snInput.sendKeys(ldapGroup.getSn());
        uidInput.sendKeys(ldapGroup.getUid());
        userDnInput.sendKeys(ldapGroup.getUserDN());
        return this;
    }

    public LdapGroup readLdapGroup() {
        LdapGroup ldapGroup = new LdapGroup();
        String attributeName = "value";
        ldapGroup.setTitle(titleInput.getAttribute(attributeName));
        ldapGroup.setDescription(descriptionInput.getAttribute(attributeName));
        ldapGroup.setDisplayName(displayNameInput.getAttribute(attributeName));
        ldapGroup.setGecos(gecosInput.getAttribute(attributeName));
        ldapGroup.setGidNumber(gidNumberInput.getAttribute(attributeName));
        ldapGroup.setHomeDirectory(homeDirectoryInput.getAttribute(attributeName));
        ldapGroup.setLoginShell(loginShellInput.getAttribute(attributeName));
        ldapGroup.setObjectClasses(objectClassesInput.getAttribute(attributeName));
        ldapGroup.setSambaAcctFlags(sambaAcctFlagsInput.getAttribute(attributeName));
        ldapGroup.setSambaKickoffTime(sambaKickoffTimeInput.getAttribute(attributeName));
        ldapGroup.setSambaLogonHours(sambaLogonHoursInput.getAttribute(attributeName));
        ldapGroup.setSambaLogonScript(sambaLogonScriptInput.getAttribute(attributeName));
        ldapGroup.setSambaPasswordHistory(sambaPasswordHistoryInput.getAttribute(attributeName));
        ldapGroup.setSambaPrimaryGroupSID(sambaPrimaryGroupSidInput.getAttribute(attributeName));
        ldapGroup.setSambaPwdMustChange(sambaPwdMustChangeInput.getAttribute(attributeName));
        ldapGroup.setSambaSID(sambaSidInput.getAttribute(attributeName));
        ldapGroup.setSn(snInput.getAttribute(attributeName));
        ldapGroup.setUid(uidInput.getAttribute(attributeName));
        ldapGroup.setUserDN(userDnInput.getAttribute(attributeName));
        return ldapGroup;
    }

    public UsersPage save() throws IllegalAccessException, InstantiationException {
        clickButtonAndWaitForRedirect(saveLdapGroupButton, Pages.getUsersPage().getUrl());
        return Pages.getUsersPage();
    }
}
