package org.jboss.aerogear.demo.smogride.secure;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;


@RequestScoped
public class SmogrideSecurityContext  {
    @Inject
    @Context private HttpServletRequest servletRequest;
    
    private String username;
    private org.keycloak.KeycloakPrincipal context;
    
    @PostConstruct
    public void fetchCredentials() {
        this.username = "secondsun";
        this.context = ((org.keycloak.KeycloakPrincipal)servletRequest.getUserPrincipal());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public org.keycloak.KeycloakPrincipal getContext() {
        return context;
    }

    public void setContext(org.keycloak.KeycloakPrincipal context) {
        this.context = context;
    }
    
    
    
}
