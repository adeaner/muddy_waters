package io.geobigdata.muddy.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.digitalglobe.gbdx.tools.config.ConfigurationManager;
import io.geobigdata.muddy.BoundingBoxWaterMask;
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

    @Logged
    @GET
    @Path("/getToken")
    @Produces("application/json")
    public Response getToken() throws WebApplicationException {
        ConfigurationManager gbdxAuthManager = new ConfigurationManager();

        return Response.ok("{\"token\": \"" + gbdxAuthManager.getAccessToken() + "\"}").build();
    }

    @Logged
    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response waterMaskFromBoundingBox(BoundingBox boundingBox) throws WebApplicationException {

        logger.info("got bounding box of " + boundingBox.toString());

        Double[] bbox = new Double[4];
        bbox[0] = boundingBox.getUpperLeftLatitude();
        bbox[1] = boundingBox.getUpperLeftLongitude();
        bbox[2] = boundingBox.getLowerRightLatitude();
        bbox[3] = boundingBox.getLowerRightLongitude();

        String graphId;
        try {
            idahoImage img = new idahoImage();
            String wkt = String.format("POLYGON((%2$f %1$f, %4$f %1$f, %4$f %3$f, %2$f %3$f, %2$f %1$f))", bbox[0], bbox[1], bbox[2], bbox[3]);
            img.setByWKT(wkt);
//            img.setByIdahoImageId("befcf8eb-02d3-4bb6-a367-d57ee457d5c2");

            BoundingBoxWaterMask bb = new BoundingBoxWaterMask();
//            graphId = "b93b20e9-2147-4768-a30b-5718b70ed98b";
            graphId = bb.getWaterMask(img, wkt);
        } catch (Exception e) {
            return Response.serverError().entity("{\"errorMessage\": \"" + e + "\"}").build();
        }

        return Response.ok("{\"graphId\": \"" + graphId + "\", \"node\": \"Invert\"}").build();
    }

}