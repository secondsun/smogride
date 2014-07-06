/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.aerogear.demo.smogride.vo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(name = "ride", uniqueConstraints = {@UniqueConstraint(columnNames = {"owner", "dateOfRide"})})
public class Ride implements Serializable, Owner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String owner;

    private int metersTravelled;
    private int duration;
    
    @Temporal(TemporalType.DATE)
    private Date dateOfRide;
    
    @Version
    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMetersTravelled() {
        return metersTravelled;
    }

    public void setMetersTravelled(int metersTravelled) {
        this.metersTravelled = metersTravelled;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getDateOfRide() {
        return dateOfRide;
    }

    public void setDateOfRide(Date dateOfRide) {
        this.dateOfRide = dateOfRide;
    }
    
    
    
}
