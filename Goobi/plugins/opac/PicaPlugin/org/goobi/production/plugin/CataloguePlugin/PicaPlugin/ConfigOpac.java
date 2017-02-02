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

package org.goobi.production.plugin.CataloguePlugin.PicaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FilenameUtils;

class ConfigOpac {
	private static XMLConfiguration config;

	protected static XMLConfiguration getConfig() {
		if (config != null) {
			return config;
		}
		String configPfad = FilenameUtils.concat(PicaPlugin.getConfigDir(), PicaPlugin.OPAC_CONFIGURATION_FILE);
		if (!new File(configPfad).exists()) {
			String message = "File not found: ".concat(configPfad);
			throw new RuntimeException(message, new FileNotFoundException(message));
		}
		try {
			config = new XMLConfiguration(configPfad);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			config = new XMLConfiguration();
		}
		config.setListDelimiter('&');
		config.setReloadingStrategy(new FileChangedReloadingStrategy());
		return config;
	}

	static List<String> getAllCatalogues(){
		List<String> catalogueTitles = new ArrayList<>();
		XMLConfiguration conf = getConfig();
		for(int i = 0; i <= conf.getMaxIndex("catalogue"); i++){
			catalogueTitles.add(conf.getString("catalogue(" + i + ")[@title]"));
		}
		return catalogueTitles;
	}


	/**
	 * find Catalogue in Opac-Configurationlist
	 * ================================================================
	 */
	static Catalogue getCatalogueByName(String inTitle) {
		int countCatalogues = getConfig().getMaxIndex("catalogue");
		for (int i = 0; i <= countCatalogues; i++) {
			String title = getConfig().getString("catalogue(" + i + ")[@title]");
			if (title.equals(inTitle)) {
				String description = getConfig().getString("catalogue(" + i + ").config[@description]");
				String address = getConfig().getString("catalogue(" + i + ").config[@address]");
				String database = getConfig().getString("catalogue(" + i + ").config[@database]");
				String ucnf = getConfig().getString("catalogue(" + i + ").config[@ucnf]", "");
				if (!ucnf.equals("")) {
					ucnf = "&" + ucnf;
				}
				int port = getConfig().getInt("catalogue(" + i + ").config[@port]");
				String charset = "iso-8859-1";
				if (getConfig().getString("catalogue(" + i + ").config[@charset]") != null) {
					charset = getConfig().getString("catalogue(" + i + ").config[@charset]");
				}

				// Opac-Beautifier einlesen und in Liste zu jedem Catalogue packen

				ArrayList<Setvalue> beautyList = new ArrayList<>();
				for (int j = 0; j <= getConfig().getMaxIndex("catalogue(" + i + ").beautify.setvalue"); j++) {
					/* Element, dessen Wert geändert werden soll */
					String prefix = "catalogue(" + i + ").beautify.setvalue(" + j + ")";
					String tag = getConfig().getString(prefix + "[@tag]");
					String subtag = getConfig().getString(prefix + "[@subtag]");
					String value = getConfig().getString(prefix + "[@value]").replaceAll("\u2423", " ");
					String mode = getConfig().getString(prefix + "[@mode]", "replace");

					// Elemente, die bestimmte Werte haben müssen, als Prüfung, ob das zu ändernde Element geändert werden soll

					ArrayList<Condition> proofElements = new ArrayList<>();
					for (int k = 0; k <= getConfig().getMaxIndex(prefix + ".condition"); k++) {
						String tempK = prefix + ".condition(" + k + ")";
						Condition oteProof = new Condition(
								getConfig().getString(tempK + "[@tag]"), getConfig().getString(tempK + "[@subtag]"),
								getConfig().getString(tempK + "[@value]").replaceAll("\u2423", " "), getConfig()
										.getString(tempK + "[@mode]", "matches"));
						proofElements.add(oteProof);
					}
					beautyList.add(new Setvalue(tag, subtag, value, mode, proofElements));
				}

				return new Catalogue(title, description, address, database, port, charset, ucnf, beautyList);
			}
		}
		return null;
	}

	/**
	 * return all configured Doctype-Titles from Configfile
	 * ================================================================
	 */
	private static ArrayList<String> getAllDoctypeTitles() {
		ArrayList<String> myList = new ArrayList<>();
		int countTypes = getConfig().getMaxIndex("doctypes.type");
		for (int i = 0; i <= countTypes; i++) {
			String title = getConfig().getString("doctypes.type(" + i + ")[@title]");
			myList.add(title);
		}
		return myList;
	}

	/**
	 * return all configured Doctype-Titles from Configfile
	 * ================================================================
	 */
	static ArrayList<ConfigOpacDoctype> getAllDoctypes() {
		ArrayList<ConfigOpacDoctype> myList = new ArrayList<>();
		for (String title : getAllDoctypeTitles()) {
			myList.add(getDoctypeByName(title));
		}
		return myList;
	}

	/**
	 * get doctype from mapping of opac response first check if there is a
	 * special mapping for this
	 * ================================================================
	 */
	static ConfigOpacDoctype getDoctypeByMapping(String inMapping, String inCatalogue) {
		int countCatalogues = getConfig().getMaxIndex("catalogue");
		for (int i = 0; i <= countCatalogues; i++) {
			String title = getConfig().getString("catalogue(" + i + ")[@title]");
			if (title.equals(inCatalogue)) {

				// alle speziell gemappten DocTypes eines Kataloges einlesen

				HashMap<String, String> labels = new HashMap<>();
				int countLabels = getConfig().getMaxIndex("catalogue(" + i + ").specialmapping");
				for (int j = 0; j <= countLabels; j++) {
					String type = getConfig().getString("catalogue(" + i + ").specialmapping[@type]");
					String value = getConfig().getString("catalogue(" + i + ").specialmapping");
					labels.put(value, type);
				}
				if (labels.containsKey(inMapping)) {
					return getDoctypeByName(labels.get(inMapping));
				}
			}
		}

		// falls der Katalog kein spezielles Mapping für den Doctype hat, jetzt in den Doctypes suchen

		for (String title : getAllDoctypeTitles()) {
			ConfigOpacDoctype tempType = getDoctypeByName(title);
			if (tempType.getMappings().contains(inMapping)) {
				return tempType;
			}
		}
		return null;
	}

	/**
	 * get doctype from title
	 * ================================================================
	 */
	@SuppressWarnings("unchecked")
	private static ConfigOpacDoctype getDoctypeByName(String inTitle) {
		int countCatalogues = getConfig().getMaxIndex("doctypes.type");
		for (int i = 0; i <= countCatalogues; i++) {
			String title = getConfig().getString("doctypes.type(" + i + ")[@title]");
			if (title.equals(inTitle)) {
				boolean periodical = getConfig().getBoolean("doctypes.type(" + i + ")[@isPeriodical]");
				boolean multiVolume = getConfig().getBoolean("doctypes.type(" + i + ")[@isMultiVolume]");
				boolean containedWork = getConfig().getBoolean("doctypes.type(" + i + ")[@isContainedWork]");
				ArrayList<String> mappings = (ArrayList<String>) getConfig()
						.getList("doctypes.type(" + i + ").mapping");

				ConfigOpacDoctype cod = new ConfigOpacDoctype(title, periodical, multiVolume, containedWork, mappings);
				return cod;
			}
		}
		return null;
	}
}
