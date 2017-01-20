package io.geobigdata.muddy.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.digitalglobe.gbdx.tools.auth.GBDXAuthManager;
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
        GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

        return Response.ok("{\"token\": \"" + gbdxAuthManager.getAccessToken() + "\"}").build();
    }


    @Logged  // this request is logged
    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response saveBoundingBox( BoundingBox boundingBox) throws WebApplicationException {

        logger.info("got bounding box of " + boundingBox.toString() );

        Double[] bbox = new Double[4];
        bbox[0] = boundingBox.getUpperLeftLatitude();
        bbox[1] = boundingBox.getUpperLeftLongitude();
        bbox[2] = boundingBox.getLowerRightLatitude();
        bbox[3] = boundingBox.getLowerRightLongitude();

        String idahoId = "blah";
       try {
            BoundingBoxWaterMask.getOverlay(bbox);
        }
        catch( Exception e ) {
            return Response.serverError().build();
        }

        return Response.ok("{\"idahoId\":\"" + idahoId + "\"}").build();
    }




        // "timestamp" -> "2016-12-31T00:00:00.000Z"

  /*      private class ResponseSorter implements Comparator<Record> {
            @Override
            public int compare(Record first, Record second) {
                ZonedDateTime firstTimestamp = ZonedDateTime.parse(first.getProperties().get("timestamp"),
                                                    DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault()));
                ZonedDateTime secondTimestamp = ZonedDateTime.parse(second.getProperties().get("timestamp"),
                        DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault()));

                return firstTimestamp.compareTo(secondTimestamp);
            }

        } */

}