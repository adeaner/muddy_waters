package io.geobigdata.muddy.services.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Used to bootstrap JAX-RS.  Otherwise this class is
 * not directly used.
 *
 */
@ApplicationPath("/services")
public class RestApplicationConfig extends Application {
	// intentionally empty
}
