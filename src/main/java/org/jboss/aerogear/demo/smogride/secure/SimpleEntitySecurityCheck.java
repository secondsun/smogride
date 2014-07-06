package org.jboss.aerogear.demo.smogride.secure;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.jboss.aerogear.demo.smogride.vo.Owner;

@RequestScoped
public class SimpleEntitySecurityCheck {
    
    @Inject SmogrideSecurityContext security;
    
    @PrePersist
    @PreRemove
    @PreUpdate
    @PostLoad
    public void checkIsNewOrIsOwner(Object entity) {
        if (entity instanceof Owner) {
            Owner owned = (Owner) entity;
            if (owned.getOwner() == null || owned.getOwner().isEmpty()) {
                return;
            } else if (security.getUsername().equals(owned.getOwner())) {
                return;
            } else {
                throw new IllegalAccessError( security.getUsername() + " is not owner of" + entity);
            }
        }
        
    }
    
    @PostPersist
    @PostRemove
    @PostUpdate
    public void doNothing(Object entity) {
        Logger.getAnonymousLogger().log(Level.FINER, "Did something to {0}", entity);
    }
    
    
    
    
}
