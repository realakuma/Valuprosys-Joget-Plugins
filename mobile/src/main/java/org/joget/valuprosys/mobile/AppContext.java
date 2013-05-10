/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author realakuma
 */
public class AppContext {
    private static AppContext instance;

 private AbstractApplicationContext appContext;

 public synchronized static AppContext getInstance() {
  if (instance == null) {
   instance = new AppContext();
  }
  return instance;
 }

 private AppContext() {
  System.out.print(this.getClass().getResource("/"));
  //this.appContext = new ClassPathXmlApplicationContext("productsApplicationContext.xml");
  Thread currentThread = Thread.currentThread();
ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
try {
    currentThread.setContextClassLoader(this.getClass().getClassLoader());
    this.appContext = new ClassPathXmlApplicationContext(new String[]{"/mobileApplicationContext.xml"}, this.getClass(), AppUtil.getApplicationContext());
} finally {
    currentThread.setContextClassLoader(threadContextClassLoader);
}
 }

 public AbstractApplicationContext getAppContext() {
  return appContext;
 }
}
