/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.api;

public interface Service extends java.rmi.Remote {
    public boolean blockUser(java.lang.String adminUsername, java.lang.String username) throws java.rmi.RemoteException;
    public boolean newUser(java.lang.String username, net.etfbl.model.User user) throws java.rmi.RemoteException;
}
