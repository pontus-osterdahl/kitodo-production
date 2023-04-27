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

package org.kitodo.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kitodo.api.dataeditor.rulesetmanagement.MetadataViewInterface;

/**
 * Gathers the characteristics for displaying an error message for an error that
 * occurs only in an extremely rare case of a misconfiguration. But since the
 * error occurs in a different part of the application than the part that prints
 * the error message, complex passing of details must be in place to
 * successfully populate the error message.
 */
public class RecordIdentifierMissingDetail {

    private String division;
    private String recordIdentifierMetadata;
    private String allowedMetadata;

    public RecordIdentifierMissingDetail(String division, Collection<String> recordIdentifierMetadata,
            Collection<MetadataViewInterface> allowedMetadata) {
        this.division = division;
        this.recordIdentifierMetadata = String.join(", ", recordIdentifierMetadata);
        this.allowedMetadata = allowedMetadata.stream().map(MetadataViewInterface::getId)
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns the division of the detail of the missing disc identifier.
     * 
     * @return the division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Returns the record identifier metadata of the missing record identifier
     * detail.
     * 
     * @return as a string, the record identifier metadata
     */
    public String getRecordIdentifierMetadata() {
        return recordIdentifierMetadata;
    }

    /**
     * Returns the allowed metadata of the missing record identifier detail.
     * 
     * @return as a string, the allowed metadata
     */
    public String getAllowedMetadata() {
        return allowedMetadata;
    }

    @Override
    public String toString() {
        return "RecordIdentifierMissingDetail [division=" + division + ", recordIdentifierMetadata="
                + recordIdentifierMetadata + ", allowedMetadata=" + allowedMetadata + "]";
    }
}
