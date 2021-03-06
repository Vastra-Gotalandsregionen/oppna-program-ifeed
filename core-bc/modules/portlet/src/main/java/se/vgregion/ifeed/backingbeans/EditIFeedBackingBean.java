package se.vgregion.ifeed.backingbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ifeed.types.Ownership;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Monica on 2014-03-28.
 */
@Component(value = "editIFeedBackingBean")
@Scope("request")
public class EditIFeedBackingBean implements Serializable {

    @Autowired
    private IFeedService iFeedService;
//    @Autowired
//    private ResourceLocalService resourceLocalService;
    @Autowired
    private LdapPersonService ldapPersonService;

    @Value("#{iFeedModelBean}")
    private IFeedModelBean iFeedModelBean;

    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;
    private String newOwnershipName;

    @Value("#{filterModelBean}")
    private FilterModelBean filterModelBean;

    @Value("#{app}")
    private Application app;

    public FilterModelBean getFilterModelBean() {
        return filterModelBean;
    }

    public void setFilterModelBean(FilterModelBean filterModelBean) {
        this.filterModelBean = filterModelBean;
    }

    public IFeedService getiFeedService() {
        return iFeedService;
    }

    public void setiFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    public EditIFeedBackingBean() {
    }

    public void addIFeed(Application app) {
        app.viewIFeed(addIFeed());
        // navigationModelBean.setUiNavigation("EDIT_IFEED");
    }

    public Long addIFeed() {
        //iFeedModelBean.clearBean();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();

        IFeed iFeed = iFeedModelBean.toIFeed();
        /*iFeed.setName(iFeedModelBean.getName());
        iFeed.setDescription(iFeedModelBean.getDescription());
        iFeed.setUserId(app.getCurrentUser().getScreenName());*/

        /*
        Ownership ownership = new Ownership();
        ownership.setIfeed(iFeed);
        ownership.setUserId(app.getCurrentUser().getScreenName());
        iFeed.getOwnerships().add(ownership);
        */
        iFeed.setUserId(app.getCurrentUser().getId());

        iFeed = iFeedService.addIFeed(iFeed);
        iFeedModelBean.copyValuesFromIFeed(iFeed);
//            ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
//            long companyId = themeDisplay.getCompanyId();
//            long userId = themeDisplay.getUserId();
//            resourceLocalService.addResources(companyId, 0, userId, IFeed.class.getName(), iFeed.getId().longValue(), false, false, true);


        return iFeed.getId();
        // navigationModelBean.setUiNavigation("VIEW_IFEED");
    }


    /*private String getRemoteUserId(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }*/

    public IFeedService getIFeedService() {
        return iFeedService;
    }

    public void setIFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

//    public ResourceLocalService getResourceLocalService() {
//        return resourceLocalService;
//    }

//    public void setResourceLocalService(ResourceLocalService resourceLocalService) {
//        this.resourceLocalService = resourceLocalService;
//    }

    public IFeedModelBean getiFeedModelBean() {
        return iFeedModelBean;
    }

    public void setiFeedModelBean(IFeedModelBean iFeedModelBean) {
        this.iFeedModelBean = iFeedModelBean;
    }

    public void addOwnership() {

        Set<Ownership> target = iFeedModelBean.getOwnerships();
        Ownership ownership = new Ownership();
        ownership.setName(newOwnershipName.trim());
        ownership.setIfeed(iFeedModelBean);
        target.add(ownership);
    }

    public List<Person> completeUser(String incompleteUserName) {
        //List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);
        List<Person> people = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person("A" + i, "B" + i, "C" + i, "D" + i, "E" + i);
            people.add(person);
        }

        return people;
        /*List<String> result = new ArrayList<String>();
        for (Person person : people) {
            result.add(person.getUserName());
        }
        return result;*/
    }

    public List<String> completeUserName(String incompleteUserName) {
        try {
            List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);
            List<String> result = new ArrayList<String>();
            for (Person person : people) {
                result.add(person.getUserName());
            }
            return result;

        } catch (Exception e) {
            return Arrays.asList("a", "b", "c");
        }
    }

    private String fetchNameOfPersonIfMatch(String key) {
        try {
            List<Person> persons = ldapPersonService.getPeople(key, 2);
            if (persons.size() == 1) {
                Person person = persons.get(0);
                return person.getFirstName() + " " + person.getLastName();
            }
        } catch (Exception e) {

        }
        return "";
    }

    public List asList(Collection c) {
        return new ArrayList(c);
    }

    public void update() {
        try {
            iFeedService.update(iFeedModelBean.toIFeed());
            goBackToIFeedList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void goBackToIFeedList() {
        navigationModelBean.setUiNavigation("USER_IFEEDS");
        iFeedModelBean.clearBean();
    }

    public void addNewOwnershipName() {
        if (newOwnershipName == null || "".equals(newOwnershipName)) {
            return;
        }
        Ownership item = new Ownership();
        item.setUserId(newOwnershipName);
        iFeedModelBean.getOwnerships().add(item);
        item.setIfeed(iFeedModelBean);
        newOwnershipName = "";
    }

    public void removeOwnership(Ownership ownership) {
        List<Ownership> workList = new ArrayList<Ownership>(iFeedModelBean.getOwnerships());

        for (int i = workList.size() - 1; i >= 0; i--) {
            Ownership o = workList.get(i);
            if (o.getUserId().equals(ownership.getUserId())) {
                workList.remove(i);
            }
        }

        iFeedModelBean.getOwnerships().clear();
        iFeedModelBean.getOwnerships().addAll(workList);
    }

    public String getNewOwnershipName() {
        return newOwnershipName;
    }

    public void setNewOwnershipName(String newOwnershipName) {
        this.newOwnershipName = newOwnershipName;
    }

    public LdapPersonService getLdapPersonService() {
        return ldapPersonService;
    }

    public void setLdapPersonService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    public NavigationModelBean getNavigationModelBean() {
        return navigationModelBean;
    }

    public void setNavigationModelBean(NavigationModelBean navigationModelBean) {
        this.navigationModelBean = navigationModelBean;
    }


    public void addFilter() {
        if (filterModelBean.getFilterValue() == null || "".equals(filterModelBean.getFilterValue().trim())) {
            return;
        }
        IFeedFilter filter = new IFeedFilter(filterModelBean.getFilterValue(), filterModelBean.getFieldInf().getName());
        iFeedModelBean.addFilter(filter);
        filterModelBean.setFilterValue(null);
        filterModelBean.setFieldInf(null);
    }


}
