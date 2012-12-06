package org.joget.valuprosys.adinte;
import java.util.ArrayList;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

public class ldapTest {

 /**
  * @param args
  */
public ArrayList getADuserList(String principal, String credentials,String ldap_url,String searchBase)
{
   
    
  ArrayList list=new ArrayList();
  String cn="";
  String dn="";
  String filter="";
  filter = "(&(objectCategory=person)(objectClass=user)(name=*))";
 //String searchBase="";
 NamingEnumeration<SearchResult> results;
 //String principal="VALUPROSYSZB\\Administrator";
 //String credentials="abcd1234";
 //String ldapURL = "LDAP://192.168.152.128:389";
 ldapConnection lc=new ldapConnection();
 lc.setHostUrl(ldap_url);

DirContext ctx =lc.initctx(principal, credentials);

        //先拿CN值 ================
        //searchBase="DC=VALUPROSYSZB,DC=com";
try {
results = ctx.search(searchBase, filter, ldapConnection.getSearchControls());
  while (results != null && results.hasMore()) {
   SearchResult sr2 =  results.next();
   cn=(String)sr2.getAttributes().get("cn").get();
   list.add(cn);
   dn=sr2.getName();
   System.out.println(dn);
  }

  }
catch(javax.naming.NamingException ex)
{
    //ex.printStackTrace();
}
finally
{
    try{
    ctx.close();
    }
    catch(javax.naming.NamingException ex)
{
    //ex.printStackTrace();
}  
}
  return list;
}
/*
 public static void main(String[] args) throws Exception{
  // TODO Auto-generated method stub
  
        //=======================
  /*  
        java.util.List<String> uidList=new ArrayList<String>();
  
        filter =
   "(&(objectClass=dominoGroup)(cn=IT事业部))";
       
   results =
   ctx.search(searchBase, filter, ldapConnection.getSearchControls());
  
  while (results != null && results.hasMore()) {
   SearchResult sr =  results.next();
   System.out.println(sr.getName());
   
   Attributes attrs = sr.getAttributes();
   NamingEnumeration attrsEnum=attrs.get("member").getAll();
   System.out.println("组织里的人员有："+attrs.get("member"));
   while (attrsEnum != null && attrsEnum.hasMore()) {
    String str=(String)attrsEnum.next();
    System.out.println(str.equalsIgnoreCase(dn));
    NamingEnumeration<SearchResult> resultsPerson =
     ctx.search(str, "(objectClass=*)", ldapConnection.getSearchControls());//根据DN，再重新查找
    while (resultsPerson != null && resultsPerson.hasMore()) { 
     SearchResult srPerson =  resultsPerson.next();
     System.out.println(srPerson.getAttributes().get("cn").get());//CN
     System.out.println(srPerson.getAttributes().get("uid").get()+"@");//UID
     uidList.add(srPerson.getAttributes().get("uid").get().toString().toLowerCase());
    }
   }
   System.out.println("本组织是否存在这用户 :"+uidList.contains("lilb"));
   
   
   
  }
 }
*/
}