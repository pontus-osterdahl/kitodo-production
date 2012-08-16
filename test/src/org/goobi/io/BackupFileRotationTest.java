/*
 * This file is part of the Goobi Application - a Workflow tool for the support of
 * mass digitization.
 *
 * Visit the websites for more information.
 *     - http://gdz.sub.uni-goettingen.de
 *     - http://www.goobi.org
 *     - http://launchpad.net/goobi-production
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package org.goobi.io;

import org.goobi.log4j.AssertFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.goobi.log4j.AssertFileSystem.*;

import org.apache.log4j.BasicConfigurator;

public class BackupFileRotationTest {

	public static final String BACKUP_FILE_NAME = "File-BackupFileRotationTest.xml";
	public static final String BACKUP_FILE_PATH = "./";

	@BeforeClass
	public static void oneTimeSetUp() {
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
	}

	@After
	public void tearDown() throws Exception {
		deleteFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		deleteFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
		deleteFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".2");
		deleteFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".3");
	}

	@Test
	public void shouldCreateSingleBackupFile() throws Exception {
		int numberOfBackups = 1;
		runBackup(numberOfBackups);
		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
	}

	@Test
	public void backupFileShouldContainSameContentAsOriginalFile() throws IOException {
		int numberOfBackups = 1;
		String content = "Test One.";
		writeFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME, content);
		runBackup(numberOfBackups);
		assertFileHasContent(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1", content);
	}

	@Test
	public void modifiedDateShouldNotChangedOnBackup() {
		int numberOfBackups = 1;
		long originalModifiedDate = getLastModifiedFileDate(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);
		assertLastModifiedDate(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1", originalModifiedDate);
	}

	@Test
	public void shouldWriteTwoBackupFiles() throws IOException {
		int numberOfBackups = 2;

		runBackup(numberOfBackups);

		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);

		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".2");
	}

	@Test
	public void initialContentShouldEndUpInSecondBackupFileAfterTwoBackupRuns() throws IOException {
		String content1 = "Test One.";
		String content2 = "Test Two.";
		int numberOfBackups = 2;

		writeFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME, content1);
		runBackup(numberOfBackups);

		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);

		assertFileHasContent(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".2", content1);
	}

	@Test
	public void secondBackupFileCorrectModifiedDate() throws IOException {
		long expectedLastModifiedDate;
		int numberOfBackups = 2;
		expectedLastModifiedDate = getLastModifiedFileDate(BACKUP_FILE_PATH + BACKUP_FILE_NAME);

		runBackup(numberOfBackups);
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);

		assertLastModifiedDate(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".2", expectedLastModifiedDate);
	}

	@Test
	public void threeBackupRunsCreateThreeBackupFiles() throws IOException {
		int numberOfBackups = 3;

		runBackup(numberOfBackups);
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);

		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".2");
		assertFileExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".3");
	}

	@Test
	public void initialContentShouldEndUpInThirdBackupFileAfterThreeBackupRuns() throws IOException {
		int numberOfBackups = 3;
		String content1 = "Test One.";

		writeFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME, content1);
		runBackup(numberOfBackups);
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);
		createFile(BACKUP_FILE_PATH + BACKUP_FILE_NAME);
		runBackup(numberOfBackups);

		assertFileHasContent(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".3", content1);
	}

	@Test
	public void noBackupIsPerformedWithNumberOfBackupsSetToZero() throws Exception {
		int numberOfBackups = 0;
		runBackup(numberOfBackups);
		assertFileNotExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
	}
	
	@Test
	public void nothingHappensIfFilePatternDontMatch() throws Exception {
		int numberOfBackups = 1;
		runBackup(numberOfBackups, "");
		assertFileNotExists(BACKUP_FILE_PATH + BACKUP_FILE_NAME + ".1");
	}

	private void runBackup(int numberOfBackups) {
		runBackup(numberOfBackups, BACKUP_FILE_NAME);
	}

	private void runBackup(int numberOfBackups, String format) {
		BackupFileRotation bfr = new BackupFileRotation();
		bfr.setNumberOfBackups(numberOfBackups);
		bfr.setProcessDataDirectory(BACKUP_FILE_PATH);
		bfr.setFormat(format);
		bfr.performBackup();
	}

	private void createFile(String fileName) throws IOException {
		File testFile = new File(fileName);
		FileWriter writer = new FileWriter(testFile);
		writer.close();
	}

	private void deleteFile(String fileName) {
		File testFile = new File(fileName);
		testFile.delete();
	}

	private void writeFile(String fileName, String content) throws IOException {
		File testFile = new File(fileName);
		FileWriter writer = new FileWriter(testFile);
		writer.write(content);
		writer.close();
	}

}
