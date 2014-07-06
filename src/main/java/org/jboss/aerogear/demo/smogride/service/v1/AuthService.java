/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.aerogear.demo.smogride.service.v1;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.aerogear.demo.smogride.vo.Account;
import org.jboss.annotation.security.SecurityDomain;

/**
 *
 * @author summers
 */
@Path("auth")
@Stateless
@SecurityDomain("keycloak")
public class AuthService {

    @PersistenceUnit(unitName = "smogride")
    private EntityManagerFactory emf;
    
    @GET
    @Path("exchange")
    public String exchangeToken(@QueryParam("ex_code") String code) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("http://localhost:8080/auth/realms/smogride/tokens/access/codes");

            String body = "code=" + URLEncoder.encode(code) + "&client_id=CLI&redirect_uri="+URLEncoder.encode("urn:ietf:wg:oauth:2.0:oob") + "&grant_type=authorization_code";
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            request.setEntity(new StringEntity(body));
            try (CloseableHttpResponse result = httpclient.execute(request)) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(result.getEntity().getContent(), writer, "UTF-8");
                String theString = writer.toString();
                return theString;
            }
        }

    }
    
    @GET
    @Path("createAccount")
    @RolesAllowed(value = {"user"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createAccount(@QueryParam("username") String username) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Account> query = em.createQuery("from Account where owner = :username", Account.class);
            query.setParameter("username", username);
            
            List<Account> result = query.getResultList();
            if (result.size() > 0) {
                return "{\"status\":\"fail.  Already exists\"}";
            } else {
                Account account = new Account();
                account.setOwner(username);
                em.persist(account);
                em.flush();
            }
            
        } finally {
            em.close();
        }
        
        return "{\"status\":\"success\"}";
        
    }
    
}
