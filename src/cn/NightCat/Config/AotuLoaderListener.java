package cn.NightCat.Config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class AotuLoaderListener
 *
 */
@WebListener
public class AotuLoaderListener implements ServletContextListener {

    public AotuLoaderListener() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		NCConfig.Config_.ServiceDestroyed();
		DatabaseConfig.CloseDatabase();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		NCConfig.Web_Path = arg0.getServletContext().getRealPath("/").replace("%20", " ");
		NCConfig.Init();
	}	
}
