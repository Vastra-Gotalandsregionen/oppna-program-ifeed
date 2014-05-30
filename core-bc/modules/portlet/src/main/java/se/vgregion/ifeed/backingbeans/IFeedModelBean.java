package se.vgregion.ifeed.backingbeans;


import net.sf.cglib.beans.BeanMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Monica on 2014-03-28.
 */
@Component(value = "iFeedModelBean")
@Scope("session")
public class IFeedModelBean extends IFeed implements Serializable {

    private static final long serialVersionUID = -2277251806545192506L;

    private String ownershipUserIds;

    public IFeedModelBean() {

    }

    private String newOwnershipName;

    private List<String> newOwnershipNames = new MirrorOwnershipToTextList(ownerships);

    /*
    public void copyNamesToNewOwnershipNames() {
        for (Ownership ownership : getOwnershipList()) {
            newOwnershipNames.add(ownership.getName());
        }
    }

    public void copyFromNewOwnershipNamesToOwnerships() {
        getOwnershipList().clear();
        for (String name : newOwnershipNames) {
            Ownership ownership = new Ownership();
            ownership.setName(name);
            getOwnershipList().add(ownership);
        }
    }*/

    private void copy(Object from, Object into) {
        BeanMap fromMap = BeanMap.create(from);
        BeanMap intoMap = BeanMap.create(into);
        intoMap.putAll(fromMap);
    }

    public void copyValuesFromIFeed(IFeed iFeed) {
        copy(iFeed, this);
        getOwnerships().addAll(iFeed.getOwnerships());
        newOwnershipNames = new MirrorOwnershipToTextList(getOwnerships());
    }

    //List for display purposes
    private final List<Ownership> ownershipList = new AbstractList<Ownership>() {
        @Override
        public int size() {
            return ownerships.size();
        }

        @Override
        public Ownership get(int index) {
            return new ArrayList<Ownership>(ownerships).get(index);
        }

        @Override
        public Iterator<Ownership> iterator() {
            return ownerships.iterator();
        }

        @Override
        public boolean add(Ownership ownership) {
            //return super.add(ownership);
            System.out.println("add");
            return !ownerships.contains(ownership);
        }

        @Override
        public void add(int index, Ownership element) {
            ownerships.add(element);
        }

        @Override
        public boolean addAll(Collection<? extends Ownership> c) {
            return ownerships.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Ownership> c) {
            return ownerships.addAll(c);
        }
    };

    /**
     *  Returns a list of Ownerships from the set of Ownerships
     */
    public List<Ownership> getOwnershipList() {
       /*if(ownerships != null) {
           for (Ownership ownership : ownerships) {
               ownershipList.add(ownership);
           }
       }*/
        return ownershipList;
    }

    public String getOwnershipUserIds() {
        String ownershipUserId = "";
        if(ownerships != null) {
            for (Ownership ownership : ownerships) {
                if(ownershipUserId == ""){
                    ownershipUserId = ownership.getUserId();
                } else {
                    ownershipUserId = ownershipUserId + ", " + ownership.getUserId();
                }
            }
        }
        return ownershipUserId;
    }

    public void setOwnershipUserIds(String ownershipUserIds) {
        this.ownershipUserIds = ownershipUserIds;
    }

    /**
     * Clears fields in IFeedModelBean so that the input fields
     * will be empty in the gui and no incorrect data will be saved.
     */
    public void clearBean() {
        BeanMap bm = BeanMap.create(this);

        for (Object key : bm.keySet()) {
            if (!"class".equals(key)) {
                try {
                    Class type = bm.getPropertyType((String) key);
                    if (!type.isPrimitive()) {
                        Object value = bm.get(key);
                        if (value instanceof Collection) {
                            ((Collection) value).clear();
                        } else {
                            bm.put(key, null);
                        }
                    } else {
                        if (type == Long.TYPE) {
                            bm.put(key, 0l);
                        }
                    }
                } catch (UnsupportedOperationException uoe) {
                    System.out.println("Key " + key + " caused UnsupportedOperationException");
                }
            }
        }
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IFeedModelBean that = (IFeedModelBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }*/

    public List<String> getNewOwnershipNames() {
        return newOwnershipNames;
    }

    public void setNewOwnershipNames(List<String> newOwnershipNames) {
        this.newOwnershipNames = newOwnershipNames;
    }

    public IFeed toIFeed() {
        IFeed iFeed = new IFeed();
        copy(this, iFeed);
        return iFeed;
    }

}