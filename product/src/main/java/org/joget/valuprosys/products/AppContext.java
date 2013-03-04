/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.products;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

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
  this.appContext = new ClassPathXmlApplicationContext("productsApplicationContext.xml");
 }

 public AbstractApplicationContext getAppContext() {
  return appContext;
 }
}
