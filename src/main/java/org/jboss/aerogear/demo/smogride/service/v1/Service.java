/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.aerogear.demo.smogride.service.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.aerogear.demo.smogride.secure.SmogrideSecurityContext;
import org.jboss.aerogear.demo.smogride.vo.Ride;
import org.jboss.annotation.security.SecurityDomain;

/**
 * REST Web Service
 *
 * @author summers
 */
@Path("v1")
//@SecurityDomain("keycloak")
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class Service {

    private static final Logger log = Logger.getLogger(Service.class.getSimpleName());
//
//    @Inject
//    SmogrideSecurityContext context;

    @PersistenceContext(name = "smogride", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    /**
     * Creates a new instance of Service
     */
    public Service() {
    }

    /**
     * Retrieves representation of an instance of
     * org.jboss.aerogear.demo.smogride.Service
     *
     * @return an instance of java.lang.String
     *
     * @param servletRequest the request
     */
    @GET
    @Path("ride")
//    @RolesAllowed(value = "user")
    @Produces("application/json")
    public Response getSecure() {
        Query query = em.createQuery("from Ride where owner = :owner");
        query.setParameter("owner", "secondsun");
        return Response.ok(query.getResultList()).build();
    }

    /**
     * PUT method for updating or creating an instance of Service
     *
     * @param ride representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("ride")
    @Produces("application/json")
    @Consumes("application/json")
//    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response addRide(Ride ride) {
        Map<String, String> responseMap = new HashMap<>();        
        Response.ResponseBuilder builder;
        try {
            //ride.setOwner(context.getUsername());
            em.persist(ride);
            em.flush();
            builder = Response.ok(ride);
        } catch (OptimisticLockException e) {
            builder = Response.status(Response.Status.CONFLICT).entity(e.getEntity());
        } catch (ConstraintViolationException e) {
            builder = Response.status(Response.Status.CONFLICT).entity(getConflictingRide(ride));
        } catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                builder = Response.status(Response.Status.CONFLICT).entity(getConflictingRide(ride));                
                return builder.build();
            }
            log.info("Exception - " + e.toString());
            log.throwing("Service", "addRide", e);
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * PUT method for updating or creating an instance of Service
     *
     * @param id the id for the ride
     * @param ride representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Path("ride/{id:[0-9][0-9]*}")
    @Produces("application/json")
    @Consumes("application/json")
//    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response updateRide(@PathParam("id") Long id, Ride ride) {
        Map<String, String> responseMap = new HashMap<>();        
        Response.ResponseBuilder builder;
        try {
            
            Ride currentRide = em.find(Ride.class, id);
            
//            if (!currentRide.getOwner().equals(context.getUsername())) {
//                throw new IllegalAccessException("Not Authorized");
//            }
//            
            if (!((Long)currentRide.getVersion()).equals(ride.getVersion())) {
                throw new OptimisticLockException(currentRide);
            }
            
            
            
            
            currentRide.setDuration(ride.getDuration());
            currentRide.setMetersTravelled(ride.getMetersTravelled());
            
            em.merge(currentRide);
            em.flush();
            builder = Response.ok(currentRide);
        } catch (OptimisticLockException e) {
            builder = Response.status(Response.Status.CONFLICT).entity(e.getEntity());
        } catch (ConstraintViolationException e) {
            builder = Response.status(Response.Status.CONFLICT).entity(getConflictingRide(ride));
        } catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                builder = Response.status(Response.Status.CONFLICT).entity(getConflictingRide(ride));                
                return builder.build();
            }
            log.info("Exception - " + e.toString());
            log.throwing("Service", "addRide", e);
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }
    
    private Ride getConflictingRide(Ride ride) {
        if (ride.getId() != null) {
            return em.find(Ride.class, ride.getId());
        } else {
            Query query = em.createQuery("from Ride where owner = :owner and dateOfRide = :dateOfRide");
            query.setParameter("owner", ride.getOwner());
            query.setParameter("dateOfRide", ride.getDateOfRide());
            return (Ride) query.getSingleResult();
        }
    }
}
