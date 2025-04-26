package com.mycompany.mozixx;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@ApplicationPath("/resources")
public class JAXRSConfiguration extends Application {

    //@Override
    //public Set<Class<?>> getClasses() {
        //Set<Class<?>> resources = new HashSet<>();
        // Adja hozzá a többi erőforrás osztályát is itt
        //resources.add(com.mycompany.mozixx.FavoritesOptionsResource.class); // Feltételezve, hogy van ilyen
        //resources.add(filters.CORSFilter.class); // Győződjön meg róla, hogy a szűrő is regisztrálva van
        //resources.add(com.mycompany.mozixx.FavoritesOptionsResource.class); // Az új OPTIONS kezelő
        //return resources;
    //}
}