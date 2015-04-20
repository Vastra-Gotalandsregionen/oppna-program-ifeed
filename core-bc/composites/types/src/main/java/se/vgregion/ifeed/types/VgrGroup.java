package se.vgregion.ifeed.types;


import org.apache.commons.collections.BeanMap;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.persistence.*;

/**
 * Created by clalu4 on 2014-06-24.
 */
@Entity
@Table(name = "vgr_ifeed_group")
public class VgrGroup extends AbstractEntity<Long> implements Serializable, Comparable<VgrGroup> {

    @Id
    @GeneratedValue
    protected Long id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private VgrDepartment department;

    @Transient
    private final List<IFeed> memberFeeds = new ArrayList<IFeed>();

    @Override
    public int compareTo(VgrGroup o) {
        if (o == null) {
            return 1;
        }
        return hashCode() - o.hashCode();
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<IFeed> getMemberFeeds() {
        return memberFeeds;
    }

    public VgrDepartment getDepartment() {
        return department;
    }

    public void setDepartment(VgrDepartment department) {
        this.department = department;
    }
}
