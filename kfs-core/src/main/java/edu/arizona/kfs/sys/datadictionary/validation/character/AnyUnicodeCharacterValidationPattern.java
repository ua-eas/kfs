package edu.arizona.kfs.sys.datadictionary.validation.character;

import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.datadictionary.validation.CharacterLevelValidationPattern;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Pattern for matching any unicode character
 * Eclipse identifies CharacterLevelValidationPattern and ExportMap as deprecated.
 */
@SuppressWarnings("deprecation")
public class AnyUnicodeCharacterValidationPattern extends CharacterLevelValidationPattern {

    private static final long serialVersionUID = -6423706739857278961L;

    @Override
    protected String getRegexString() {
        return ".";
    }

    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "anyCharacter");
    }

    @Override
    protected String getValidationErrorMessageKeyOptions() {
        return KRADConstants.EMPTY_STRING;
    }
}
