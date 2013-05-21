package org.joget.valuprosys.mobile;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(mobileApi.class.getName(), new mobileApi(), null));
        registrationList.add(context.registerService(mobileWorkflowApi.class.getName(), new mobileWorkflowApi(), null));
        registrationList.add(context.registerService(MobileDeviceApi.class.getName(), new MobileDeviceApi(), null));
        registrationList.add(context.registerService(MobileNotificationsPush.class.getName(), new MobileNotificationsPush(), null));
        registrationList.add(context.registerService(JogetUsersUpdate.class.getName(), new JogetUsersUpdate(), null));

    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}