package io.geobigdata.muddy;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.xml.parsers.ParserConfigurationException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import com.digitalglobe.gbdx.tools.auth.GBDXAuthManager;

import java.io.IOException;
import java.util.Collections;

import com.digitalglobe.gbdx.tools.catalog.CatalogManager;
import com.digitalglobe.gbdx.tools.catalog.model.Record;
import com.digitalglobe.gbdx.tools.catalog.model.SearchRequest;
import com.digitalglobe.gbdx.tools.catalog.model.SearchResponse;

/**
 * Hello world!
 */
public class SampleApp {
    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {

        Double bbox[] = {41.87205347180925, 12.441844940185547, 41.9155632172071, 12.509737014770506};
        String wkt = "POLYGON ((-122.41189956665039 37.59415685597818, -122.41189956665039 37.64460175855099, -122.34529495239259 37.64460175855099, -122.34529495239259 37.59415685597818, -122.41189956665039 37.59415685597818))";


        Map<Long, OsmNode> nodesById = getFeatures(bbox);

        String idaho_id_multi = getIdahoId(wkt);


        List pixelvalues = new ArrayList();
        // Iterate nodes to get pixel values
        for (OsmNode value : nodesById.values()) {
            System.out.println("value = " + value);

            Double lat = value.getLatitude();
            Double lon = value.getLongitude();

            // Get idaho chip
            String baseUrl = "http://idaho.geobigdata.io/v1";
            GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

            String pathUrl = String.format("/chip/centroid/idaho-images/%s?", idaho_id_multi);

            String idaho_query = String.format("lat=%s&long=%s" +
                    "&width=10&height=10&resolution=0.3" +
                    "&token=%s", lat, lon, gbdxAuthManager.getAccessToken());

            BufferedImage img = null;

            try {
                URL idaho_url = new URL(baseUrl + pathUrl + idaho_query);
                img = ImageIO.read(idaho_url);
            } catch (IOException e) {
            }


            pixelvalues.add(img);

        }

        System.out.println(pixelvalues);
    }

    /**
     * @param bbox double array of upper left and lower right lat/ lon
     * @return Node hash map with lat/lon
     * @throws IOException
     */
    private static Map<Long, OsmNode> getFeatures(Double bbox[]) throws IOException {
        String feature_query = String.format(
                "/*\n" +
                        "Waterways\n" +
                        "*/\n" +
                        "way\n" +
                        "  [waterway=river]\n" +
                        "  (%f,%f,%f,%f);\n" +
                        "/*add way to node*/\n" +
                        "(._;>;);\n" +
                        "out;", bbox[0], bbox[1], bbox[2], bbox[4]);

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
    private static String getIdahoId(String wkt) throws IOException {
        CatalogManager catalogManager = new CatalogManager();

        //
        // Spatial search
        //
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.withSearchAreaWkt(wkt)
                .withFilters(Collections.singletonList("cloudCover < 20, sensorPlatformName = 'WV03'"))
                .withTypes(Collections.singletonList("IDAHOImage"));

        SearchResponse response = catalogManager.search(searchRequest);

        System.out.println("got a total of " + response.getStats().getRecordsReturned() + " records returned");
        for (Record nextRecord : response.getResults()) {
            System.out.println("got record id of \"" + nextRecord.getIdentifier() + "\" of type \"" + nextRecord.getType() + "\"");

            return nextRecord.getIdentifier();

        }

        // sort results, most recent

        return "";
    }
}
