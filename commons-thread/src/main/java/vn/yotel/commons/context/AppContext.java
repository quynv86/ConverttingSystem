package vn.yotel.commons.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Simple wrapper class for the spring application context. It defines constants
 * configured in the spring xml configuration. This class can be used to
 * retrieve application beans using a String name.
 *
 * @author Abraham Menacherry
 *
 */
public class AppContext implements ApplicationContextAware {

    // App context
    public static final String APP_CONTEXT = "appContext";
    // The spring application context.
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
    }

    /**
     * This method is used to retrieve a bean by its name. Note that this may
     * result in new bean creation if the scope is set to "prototype" in the
     * bean configuration file.
     *
     * @param beanName The name of the bean as configured in the spring
     * configuration file.
     * @return The bean if it is existing or null.
     */
    public static Object getBean(String beanName) {
        if (null == beanName) {
            return null;
        }
        return applicationContext.getBean(beanName);
    }
    
    /**
     * 
     * @param param
     * @return
     */
    public static String getValue(String param) {
    	return (String) applicationContext.getBean(param);
    }

    /**
     * Called from the main method once the application is initialized. This
     * method is advised by aspectj which will in turn call the start method on
     * the {@link GameStartListener} instance.
     */
    public void initialized() {
    }
}
