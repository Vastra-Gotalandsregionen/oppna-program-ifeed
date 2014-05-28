package se.vgregion.ifeed.formbean;

import java.io.Serializable;
import java.util.regex.Pattern;

import se.vgregion.ldap.HasCommonLdapFields;

public class VgrOrganization implements Serializable, HasCommonLdapFields {

    private String dn;
    private String ou;
    private String io;
    private String cn;
    private String type;
    private String hsaIdentity;
    private boolean leaf;

    @Override
    public String getDn() {
        return dn;
    }

    @Override
    public void setDn(String dn) {
        this.dn = dn;
    }

    @Override
    public String getOu() {
        return ou;
    }

    @Override
    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getLabel() {
        if (ou != null) {
            return ou;
        }
        return cn;
    }

    public String getId() {
        String result = dn;
        if (result != null) {
            result = result.replaceAll(Pattern.quote(","), " ");
        }
        return result;
    }

    public String getIo() {
        return io;
    }

    public void setIo(String io) {
        this.io = io;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getHsaIdentity() {
        return hsaIdentity;
    }

    public void setHsaIdentity(String hsaIdentity) {
        this.hsaIdentity = hsaIdentity;
    }

    public String getQuery() {
        return getHsaIdentity() + "|" + getLabel();
    }

}