package io.geobigdata.muddy.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.geobigdata.muddy.services.logging.Logged;
import io.geobigdata.muddy.services.model.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service to handle lat/long selections on a map
 */

@Path("/v1/selection")
public class SelectionService {
    private static final Logger logger = LoggerFactory.getLogger(SelectionService.class);


    @Logged  // this request is logged
    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response saveBoundingBox( BoundingBox boundingBox) throws WebApplicationException {

        logger.info("got bounding box of " + boundingBox.toString() );

        return Response.ok("{\"status\":\"ok\"}").build();
    }
}