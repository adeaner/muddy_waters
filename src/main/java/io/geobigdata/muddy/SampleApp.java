package io.geobigdata.muddy;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

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
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.xml.sax.SAXException;

/**
 * Hello world!
 */
public class SampleApp {
    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {

        Double bbox[] = {39.84670129520201, -104.99307632446288, 39.801810432481645, -104.92518424987793};

        getOverlay(bbox);
    }

    public static void getOverlay(Double[] bbox) throws IOException {


        // upper left lat/lon, lower right lat/lon
        //BoundingBox upperLeftLatitude=39.92843137829837, upperLeftLongitude=-105.05199104547503, lowerRightLatitude=39.89999167197872, lowerRightLongitude=-104.9971452355385
        // counter clockwise lon/ lat
        String wkt = String.format("POLYGON((%2$f %1$f, %4$f %1$f, %4$f %3$f, %2$f %3$f, %2$f %1$f))", bbox[0], bbox[1], bbox[2], bbox[3]);


        Map<Long, OsmNode> nodesById = getFeatures(bbox);

        String idaho_id_multi = getIdahoId(wkt);

        List<double[]> sample_pixels = getSamplePixels(idaho_id_multi, nodesById);

        System.out.println(sample_pixels);

        List<ClusterablePixel> clusterInput = new ArrayList<>();

        for (double[] pixel : sample_pixels) {
            clusterInput.add( new ClusterablePixel(pixel) );
        }

        // initialize a new clustering algorithm.
        // we use KMeans++ with 10 clusters and 10000 iterations maximum.
        // we did not specify a distance measure; the default (euclidean distance) is used.
        long start = System.currentTimeMillis();
        System.out.println("Computing clusters...");
        //DBSCANClusterer<ClusterablePixel> clusterer = new DBSCANClusterer<ClusterablePixel>(10, 50);
        KMeansPlusPlusClusterer<ClusterablePixel> clusterer = new KMeansPlusPlusClusterer<ClusterablePixel>(10, 1000);
        List<CentroidCluster<ClusterablePixel>> clusterResults = clusterer.cluster(clusterInput);
        long end = System.currentTimeMillis();
        System.out.println("Time to compute clusters: " + (end - start) + " ms.");

        // output the clusters
//        System.out.print("[");
        List<double[]> centroid_clusters = new ArrayList();

        for (int i = 0; i < clusterResults.size(); i++) {
            Cluster<ClusterablePixel> cluster = clusterResults.get(i);
            if (cluster instanceof CentroidCluster) {
                CentroidCluster centroidCluster = (CentroidCluster) cluster;
                double[] point = centroidCluster.getCenter().getPoint();
                // if size of centroid cluster is >= size clusterInput * 10% then add to centroid_cluster list
                if (centroidCluster.getPoints().size() >= clusterInput.size() * 0.10){
                    centroid_clusters.add(point);
                }

//                String centroid = Arrays.toString(centroidCluster.getCenter().getPoint());
//                System.out.print(centroid);
            }
        }
        System.out.println(centroid_clusters);

    }

    /**
     * @param bbox double array of upper left and lower right lat/ lon
     * @return Node hash map with lat/lon
     * @throws IOException
     */
    private static Map<Long, OsmNode> getFeatures(Double bbox[]) throws IOException {
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
    public static String getIdahoId(String wkt) throws IOException {
        CatalogManager catalogManager = new CatalogManager();

        //
        // Spatial search
        //
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.withSearchAreaWkt(wkt)
                .withFilters(Arrays.asList("sensorPlatformName = 'WV03'", "cloudCover < 20", "colorInterpretation = 'WORLDVIEW_8_BAND'"))
                .withTypes(Collections.singletonList("IDAHOImage"));

        SearchResponse response = catalogManager.search(searchRequest);

        System.out.println("got a total of " + response.getStats().getRecordsReturned() + " records returned");
        for (Record nextRecord : response.getResults()) {
            System.out.println("got record id of \"" + nextRecord.getIdentifier() + "\" of type \"" + nextRecord.getType() + "\"");

            return nextRecord.getIdentifier();

        }

        // sort results, most recent

        return ""; //null
    }

    public static List<double[]> getSamplePixels(String idaho_id_multi, Map<Long, OsmNode> nodesById) throws IOException {
        // Get idaho chip
        String baseUrl = "http://idaho.geobigdata.io/v1";
        GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

        String pathUrl = String.format("/chip/centroid/idaho-images/%s?", idaho_id_multi);

        List<double[]> pixelvalues = new ArrayList();
        // Iterate nodes to get pixel values
        for (OsmNode value : nodesById.values()) {

            Double lat = value.getLatitude();
            Double lon = value.getLongitude();


            String idaho_query = String.format("lat=%s&long=%s" +
                    "&width=1&height=1&resolution=0.3&bands=7,6,5,4,3,2,1,0&format=tif" +
                    "&token=%s", lat, lon, gbdxAuthManager.getAccessToken());

            BufferedImage img = null;

            try {
//                Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix("TIF");
//                while(readers.hasNext()){
//                    ImageReader reader = readers.next();
//                    System.out.println(reader.getClass().getName());
//                }

                URL idaho_url = new URL(baseUrl + pathUrl + idaho_query);
                InputStream input = idaho_url.openStream();

                img = ImageIO.read(input);


            } catch (IOException e) {
                System.out.println(e);
            }

            try {
                Raster raster_pixel = img.getData();
                double[] pixel = new double[8];

                raster_pixel.getPixel(0, 0, pixel);

                //img.getData()<- interface method
//                short[] pixels = ((DataBufferUShort) img.getRaster().getDataBuffer()).getData(); //img.getData()<- interface method

                double sum = 0;
                for (double i : pixel) {
                    sum += i;
                }

                if (sum > 0) {
                    pixelvalues.add(pixel);
                }


            } catch (NullPointerException e) {
                System.out.println(e);
            }

        }
        return pixelvalues;
    }
}
