package net.etfbl.api;

public class ServiceProxy implements net.etfbl.api.Service {
  private String _endpoint = null;
  private net.etfbl.api.Service service = null;
  
  public ServiceProxy() {
    _initServiceProxy();
  }
  
  public ServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceProxy();
  }
  
  private void _initServiceProxy() {
    try {
      service = (new net.etfbl.api.ServiceServiceLocator()).getService();
      if (service != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)service)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (service != null)
      ((javax.xml.rpc.Stub)service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.etfbl.api.Service getService() {
    if (service == null)
      _initServiceProxy();
    return service;
  }
  
  public boolean blockUser(java.lang.String adminUsername, java.lang.String username) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.blockUser(adminUsername, username);
  }
  
  public boolean newUser(java.lang.String username, net.etfbl.model.User user) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.newUser(username, user);
  }
  
  
}