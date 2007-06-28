package gov.nih.nci.cabig.caaers.domain;

import static gov.nih.nci.cabig.ctms.domain.CodedEnumHelper.*;
import gov.nih.nci.cabig.ctms.domain.CodedEnum;

/**
 * @author Krikor Krumlian
 */
public enum RadiationAdministration implements CodedEnum<Integer> {
    BT_HDR(1, "Brachytherapy HDR"),
    BT_LDR(2, "Brachytherapy LDR"),
    BT_NOS(3, "Brachytherapy NOS"),
    EB_NOS(4, "External Beam NOS"),
    EB_2D(5,  "External Bean, 2D"),
    EB_3D(6,  "External Bean, 3D"),
    EB_IMRT(7, "External Bean, IMRT"),
    EB_PROTON(8, "External Bean, Proton"),
    SYSTEMIC_RADIOTHERAPY(9, "Systemic radiotherapy")
    ;

    private int code;
    private String displayName;

    RadiationAdministration(int code) {
        this(code, null);
    }

    RadiationAdministration(int code, String longName) {
        this.code = code;
        this.displayName = longName;
        register(this);
    }

    public static RadiationAdministration getByCode(int code) {
        return getByClassAndCode(RadiationAdministration.class, code);
    }

    public Integer getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName == null ? sentenceCasedName(this) : displayName;
    }
}
