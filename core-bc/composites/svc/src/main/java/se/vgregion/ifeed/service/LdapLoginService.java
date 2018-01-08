package se.vgregion.ifeed.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.service.ifeed.ObjectRepo;
import se.vgregion.ifeed.types.User;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.Properties;

/**
 * @author Patrik Björk
 */
@Service
@Transactional
public class LdapLoginService {

    @Autowired
    private ObjectRepo objectRepo;

    @Value("${ldap.service.user.dn}")
    private String serviceUserDN;

    @Value("${ldap.service.user.password}")
    private String serviceUserPassword;

    @Value("${ldap.search.base}")
    private String base;

    @Value("${ldap.url}")
    private String ldapUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapLoginService.class);

    public LdapLoginService() {
    }

    public User login(String username, String password) throws FailedLoginException {
        return login(username, password, true);
    }

    public User loginWithoutPassword(String username) throws FailedLoginException {
        try {
            return login(username, null, false);
        } catch (Exception e) {
            return objectRepo.findByPrimaryKey(User.class, username);
        }
    }

    public User loginOffline(String username) throws FailedLoginException {
        return objectRepo.findByPrimaryKey(User.class, username);
    }

    private User login(String username, String password, boolean verifyPassword) throws FailedLoginException {
        username = username.trim().toLowerCase();

        // first create the service context
        DirContext serviceCtx = null;
        try {
            // use the service user to authenticate
            serviceCtx = createInitialDirContext();

            NamingEnumeration<SearchResult> results = findUser(username, serviceCtx);

            if (results.hasMore()) {
                // generate the users DN (distinguishedName) from the result
                SearchResult result = results.next();
                String distinguishedName = result.getNameInNamespace();

                if (verifyPassword) {
                    verifyPassword(password, distinguishedName);
                }

                User user = new User();
                user.setId(username);
                user.setFirstName((String) (result).getAttributes().get("givenName").get());
                user.setLastName((String) (result).getAttributes().get("sn").get());
                user.setMail((String) (result).getAttributes().get("mail").get());
                user.setDisplayName((String) (result).getAttributes().get("displayName").get());

                Attribute thumbnailPhoto = result.getAttributes().get("thumbnailPhoto");
                if (thumbnailPhoto != null) {
                    user.setThumbnailPhoto((byte[]) thumbnailPhoto.get());
                }

                /*
                Attributes attributes = result.getAttributes();
                NamingEnumeration<? extends Attribute> en = attributes.getAll();
                System.out.println("Försöker få alla värden: ");
                while (en.hasMore()) {
                    Attribute a = en.next();
                    System.out.println(a.getID() + " = " + a.get());
                }*/

                // The first user to register will be admin.
                /*
                if (objectRepo.findAll(User.class).size() == 0) {
                    user.setRole(Role.ADMIN);
                } else {
                    user.setRole(Role.USER);
                }*/

                user = syncUser(user);

                return user;
            } else {
                throw new AccountNotFoundException("Nu user found with given username.");
            }
        } catch (CommunicationException e) {
            throw new RuntimeException(e);
        } catch (AccountNotFoundException e) {
            throw new FailedLoginException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FailedLoginException(e.getMessage());
        } finally {
            if (serviceCtx != null) {
                try {
                    serviceCtx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void verifyPassword(String password, String distinguishedName) throws NamingException {
        // attempt another authentication, now with the user
        Properties authEnv = new Properties();
        authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        authEnv.put(Context.PROVIDER_URL, ldapUrl);
        authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
        authEnv.put(Context.SECURITY_CREDENTIALS, password);
        new InitialDirContext(authEnv);
    }

    private NamingEnumeration<SearchResult> findUser(String username, DirContext serviceCtx) throws NamingException {
        String identifyingAttribute = "cn";
        String identifier = username;

        // we don't need all attributes, just let it generate the identifying one
        //String[] attributeFilter = {identifyingAttribute, "givenName", "mail", "sn", "displayName", "thumbnailPhoto"};
        String[] attributeFilter = {identifyingAttribute, "*"};
        SearchControls sc = new SearchControls();
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // use a search filter to find only the user we want to authenticate
        String searchFilter = "(" + identifyingAttribute + "=" + identifier + ")";
        return serviceCtx.search(base, searchFilter, sc);
    }

    private InitialDirContext createInitialDirContext() throws NamingException {
        Properties serviceEnv = new Properties();
        serviceEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        serviceEnv.put(Context.PROVIDER_URL, ldapUrl);
        serviceEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        serviceEnv.put(Context.SECURITY_PRINCIPAL, serviceUserDN);
        serviceEnv.put(Context.SECURITY_CREDENTIALS, serviceUserPassword);
        serviceEnv.put("com.sun.jndi.ldap.connect.timeout", "5000");

        return new InitialDirContext(serviceEnv);
    }

    private User syncUser(User user) throws NamingException {
        User foundUser = objectRepo.findByPrimaryKey(User.class, user.getId());

        if (foundUser != null) {
            user.setAdmin(foundUser.isAdmin());
            objectRepo.merge(user);
            objectRepo.flush();
            System.out.println("merging the user");
            return user;
        } else {
            objectRepo.persist(user);
            System.out.println("persisting user");
            return user;
        }
    }

    public void setServiceUserDN(String serviceUserDN) {
        this.serviceUserDN = serviceUserDN;
    }

    public void setServiceUserPassword(String serviceUserPassword) {
        this.serviceUserPassword = serviceUserPassword;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public void setObjectRepo(ObjectRepo objectRepo) {
        this.objectRepo = objectRepo;
    }
}
