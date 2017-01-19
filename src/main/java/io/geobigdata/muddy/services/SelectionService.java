package io.geobigdata.muddy.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.digitalglobe.gbdx.tools.auth.GBDXAuthManager;
import com.digitalglobe.gbdx.tools.catalog.CatalogManager;
import com.digitalglobe.gbdx.tools.catalog.model.Record;
import com.digitalglobe.gbdx.tools.catalog.model.SearchRequest;
import com.digitalglobe.gbdx.tools.catalog.model.SearchResponse;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
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

        Double[] bbox = new Double[4];
        bbox[0] = boundingBox.getUpperLeftLatitude();
        bbox[1] = boundingBox.getUpperLeftLongitude();
        bbox[2] = boundingBox.getLowerRightLatitude();
        bbox[3] = boundingBox.getLowerRightLongitude();

        try {
            getOverlay(bbox);
        }
        catch( IOException ioe ) {
            return Response.serverError().build();
        }

        return Response.ok("{\"status\":\"ok\"}").build();
    }

    private void getOverlay(Double[] bbox) throws IOException {

            // upper left lat/lon, lower right lat/lon
            // Double bbox[] = {39.84670129520201, -104.99307632446288, 39.801810432481645, -104.92518424987793};
            //BoundingBox upperLeftLatitude=39.92843137829837, upperLeftLongitude=-105.05199104547503, lowerRightLatitude=39.89999167197872, lowerRightLongitude=-104.9971452355385
            // counter clockwise lon/ lat
            String wkt = String.format("POLYGON((%2$f %1$f, %4$f %1$f, %4$f %3$f, %2$f %3$f, %2$f %1$f))", bbox[0], bbox[1], bbox[2], bbox[3]);

            Map<Long, OsmNode> nodesById = getFeatures(bbox);

            List<String> idaho_id_multi = getIdahoId(wkt);


            // Get idaho chip
            String baseUrl = "http://idaho.geobigdata.io/v1";
            GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

            for( String nextIdahoIdentifier: idaho_id_multi) {

                String pathUrl = String.format("/chip/centroid/idaho-images/%s?", nextIdahoIdentifier);

                List pixelvalues = new ArrayList();
                // Iterate nodes to get pixel values
                for (OsmNode value : nodesById.values()) {

                    Double lat = value.getLatitude();
                    Double lon = value.getLongitude();


                    String idaho_query = String.format("lat=%s&long=%s" +
                            "&width=1000&height=1000&resolution=0.3&bands=2,1,0&format=png" +
                            "&token=%s", lat, lon, gbdxAuthManager.getAccessToken());

                    BufferedImage img = null;

                    try {
                        URL idaho_url = new URL(baseUrl + pathUrl + idaho_query);

                        logger.info("idaho_url is " + idaho_url.toString());

                        InputStream input = idaho_url.openStream();

                        img = ImageIO.read(input);

                        input.close();

                        ImageIO.write(img, "png", new File("/Users/sdunbar/Desktop/idaho/" + nextIdahoIdentifier + "-output.png"));

                    } catch (IOException e) {
                        logger.error("got IOException:", e);
                    }

/*                    try {
                        final short[] pixels = ((DataBufferUShort) img.getRaster().getDataBuffer()).getData();
                        pixelvalues.add(pixels);
                    } catch (NullPointerException e) {
                        System.out.println(e);
                    }  */

                }
            }

           // System.out.println(pixelvalues);
        }

        /**
         * @param bbox double array of upper left and lower right lat/ lon
         * @return Node hash map with lat/lon
         * @throws IOException
         */
        private Map<Long, OsmNode> getFeatures(Double bbox[]) throws IOException {
            //bbox lower left, upper right
            String feature_query = String.format(
                    "/*\n" +
                            "Waterways\n" +
                            "*/\n" +
                            "way\n" +
                            "  [waterway=river]\n" +
                            "  (%f,%f,%f,%f);\n" +
                            "/*add way to node*/\n" +
                            "(._;>;);\n" +
                            "out;", bbox[2], bbox[1], bbox[0], bbox[3]);
            //(39.80959097923673,-105.1779556274414,39.98895805956577,-104.90638732910156)

            String feature_url = "http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(feature_query, "UTF-8");

            Map<Long, OsmNode> nodesById = new HashMap<Long, OsmNode>();
            Map<Long, OsmWay> waysById = new HashMap<Long, OsmWay>();

            // Open a stream
            InputStream input = new URL(feature_url).openStream();

            // Create a reader for XML data
            OsmIterator iterator = new OsmXmlIterator(input, false);

            // Iterate contained entities
            // Collect feature nodes
            for (EntityContainer container : iterator) {

                if (EntityType.Node.equals(container.getType())) {
                    OsmNode node = (OsmNode) container.getEntity();
                    nodesById.put(node.getId(), node);
//                System.out.println("Added node: "+node.getId());
                } else if (EntityType.Way.equals(container.getType())) {
                    OsmWay way = (OsmWay) container.getEntity();
                    waysById.put(way.getId(), way);
                    System.out.println("Added way: " + way.getId());
                }
            }

            return nodesById;

        }

        /**
         * Get IDAHO id
         *
         * @param wkt string of the POLYGON(())
         * @return string of an IDAHO id
         * @throws IOException
         */
        private List<String> getIdahoId(String wkt) throws IOException {
            CatalogManager catalogManager = new CatalogManager();

            //
            // Spatial search
            //
            SearchRequest searchRequest = new SearchRequest();

            searchRequest.withSearchAreaWkt(wkt)
                    .withFilters(Arrays.asList("sensorPlatformName = 'WV03'", "cloudCover < 20", "colorInterpretation = 'WORLDVIEW_8_BAND'"))
                    .withTypes(Collections.singletonList("IDAHOImage"));

            SearchResponse response = catalogManager.search(searchRequest);

            logger.info("got a total of " + response.getStats().getRecordsReturned() + " records returned");
            List<String> identifiers = new ArrayList<>();

            for (Record nextRecord : response.getResults()) {
                System.out.println("got record id of \"" + nextRecord.getIdentifier() + "\" of type \"" + nextRecord.getType() + "\"");

                identifiers.add(nextRecord.getIdentifier());
            }


            // sort results, most recent

            return identifiers;
        }

        // "timestamp" -> "2016-12-31T00:00:00.000Z"

        private class ResponseSorter implements Comparator<Record> {
            @Override
            public int compare(Record first, Record second) {
                ZonedDateTime firstTimestamp = ZonedDateTime.parse(first.getProperties().get("timestamp"),
                                                    DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault()));
                ZonedDateTime secondTimestamp = ZonedDateTime.parse(second.getProperties().get("timestamp"),
                        DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault()));

                return firstTimestamp.compareTo(secondTimestamp);
            }

        }

}