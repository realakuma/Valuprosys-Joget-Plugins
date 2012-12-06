package org.joget.valuprosys.adinte;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

public class ldapConnection {
 public final String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
 private  String HOST_URL = "ldap://128.8.1.8:389";
 
 public static ldapConnection connector=null;
 
 private DirContext ctx;

 private Hashtable<String,String> env;
   

 public DirContext initctx(String principal, String credentials)
 {
      env = new Hashtable();
  try {
   env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
   env.put(Context.PROVIDER_URL, HOST_URL);
   env.put(Context.SECURITY_AUTHENTICATION, "simple");
   env.put(Context.REFERRAL, "throw");
   env.put(Context.SECURITY_PRINCIPAL, principal);
   env.put(Context.SECURITY_CREDENTIALS, credentials);
   ctx = new InitialDirContext(env);
  } catch (Exception ex) {
   ex.printStackTrace();
  }
  return ctx;
 }
 
 public void setHostUrl(String hosturl)
 {
     this.HOST_URL=hosturl;
 }
 
 public String getHostUrl()
 {
     return this.HOST_URL;
 }
 
 
 public  DirContext getDirContext(){
  return ctx;
 }
 
 public static SearchControls getSearchControls(){
  SearchControls constraints;
  constraints = new SearchControls();
  constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
  return constraints;
 }
 
 
 public static ldapConnection  init(String principal, String credentials){
     synchronized (ldapConnection.class) {
      if(connector==null)
    connector=new ldapConnection(); 
 }  
   return connector;
 }
}

