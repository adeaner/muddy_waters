package io.geobigdata.muddy;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import io.geobigdata.ipe.IPEGraph;
import io.geobigdata.ipe.IPEGraphNode;
//import io.geobigdata.idaho.image.ImageMetadata;
import io.geobigdata.muddy.services.idahoImage;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.xml.sax.SAXException;

/**
 * Muddy Waters
 */
public class BoundingBoxWaterMask {
    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException, ParseException, com.vividsolutions.jts.io.ParseException {

        // upper left lat/lon, lower right lat/lon
        Double[] bbox;

//        Double bbox[] = {39.84670129520201, -104.99307632446288, 39.801810432481645, -104.92518424987793}; // Commerce City Platte River
//        Double bbox[] = {39.945172035117984, -104.87874984741211, 39.92161054620153, -104.85591888427734}; //
//        Double bbox[] = {39.96977788803444, -105.2490234375, 39.93003569725961, -105.20387649536133 }; //marshal rd
//        Double bbox[] = {30.412077166683314, -81.49520874023438, 30.37772538837059, -81.44233703613281}; //jackson beach
//        Double bbox[] = {39.779040683054156, -105.24593353271484, 39.75449829691315, -105.18928527832031 }; //golden
        bbox = new Double[]{40.76004940214887, -106.32499694824219, 40.710621542994936, -106.26697540283203}; //

//        Double bbox[] = {36.43238395557654, -76.32991790771484 , 36.405862003277065, -76.28082275390625}; // Elizabeth City, NC

//        String cat_id = "103001005B3EEE00";
//        String cat_id = "1040010020787A00";
        // 498018dd-7d00-481b-abc5-04d7aed04c0b wv03 beautiul

        idahoImage img = new idahoImage();
//        img.setByIdahoImageId("5c0280c5-3cd6-4214-a349-2bcbea5b25ad"); wv02 baton rouge
//        String aoi = "POLYGON((-90.98670959472656 30.106041238914163,-90.8294677734375 30.106041238914163,-90.8294677734375 30.006196189088108,-90.98670959472656 30.006196189088108,-90.98670959472656 30.106041238914163))";

//        img.setByIdahoImageId("2ef9bcd0-e219-41bc-adf5-4c4ce2fd0305"); // wv03 w/ clouds
//        String aoi = "POLYGON((-91.01898193359375 30.161269281485744,-90.89057922363281 30.161269281485744,-90.89057922363281 30.07098807994365,-91.01898193359375 30.07098807994365,-91.01898193359375 30.161269281485744))";

//        img.setByIdahoImageId("2c75460c-0712-4d98-a2a0-322fd7df9044"); // wv03 w/ clouds
//        String aoi = "POLYGON((-91.24214172363281 30.2206197762358,-91.00181579589844 30.2206197762358,-91.00181579589844 30.106635253152803,-91.24214172363281 30.106635253152803,-91.24214172363281 30.2206197762358))";

        img.setByIdahoImageId("2bb19b93-86aa-46c5-b2dc-a683c015e6d8"); // wv03 w/ clouds in the tree
        String aoi = "POLYGON((-90.6866455078125 30.23664291004952,-90.65574645996094 30.23664291004952,-90.65574645996094 30.209647267420458,-90.6866455078125 30.209647267420458,-90.6866455078125 30.23664291004952))";


//        img.setByIdahoImageId("8099ac52-4337-4ff8-b7b1-b38c332042e6"); // wv03 hazy
//        String aoi = "POLYGON((-91.06636047363281 30.250874750415438,-90.97160339355469 30.250874750415438,-90.97160339355469 30.158300818026124,-91.06636047363281 30.158300818026124,-91.06636047363281 30.250874750415438))";


//        img.setByCatalogId(cat_id);
//        String aoi = "POLYGON((-90.99838256835938 30.20519208886129,-90.8074951171875 30.20519208886129,-90.8074951171875 30.10307111415961,-90.99838256835938 30.10307111415961,-90.99838256835938 30.20519208886129))";

        getWaterMask(img, aoi);
    }

    public static String getOverlay(Double[] bbox) throws IOException, ParserConfigurationException, ParseException, com.vividsolutions.jts.io.ParseException {


        // counter clockwise lon/ lat
        String wkt = String.format("POLYGON((%2$f %1$f, %4$f %1$f, %4$f %3$f, %2$f %3$f, %2$f %1$f))", bbox[0], bbox[1], bbox[2], bbox[3]);
        idahoImage img = new idahoImage();
        img.setByWKT(wkt);

        return getWaterMask(img, wkt);

    }

    public static String getWaterMask(idahoImage img, String... aoi_wkt) throws ParseException, ParserConfigurationException, com.vividsolutions.jts.io.ParseException, IOException {


        Boolean DEBUG = false;
        String spectral_angle_signatures;
        String overlapping_wkt;
        if (!DEBUG) {
            // Get water features from OSM
            Map<Long, OsmNode> nodesById = getFeatures(img.getBoundingBox());


//            String[] idaho_info = getIdahoId(wkt);
//            idaho_id_multi = idaho_info[0];
//            String idaho_id_footprint = idaho_info[1];

            // Calculate overlapping wkt to crop in IPE
            WKTReader reader = new WKTReader();
            WKTWriter writer = new WKTWriter();
            if (aoi_wkt.length > 0) {

                Geometry aoi_geometry = reader.read(aoi_wkt[0]);
                aoi_geometry.setSRID(4326);

                Geometry idaho_id_geometry = reader.read(img.metadata.getImageBoundsWGS84());
                idaho_id_geometry.setSRID(4326);
                Geometry overlapping_geometry = idaho_id_geometry.intersection(aoi_geometry);
                Geometry buffered_overlapping_geometry = overlapping_geometry.buffer(-0.002);

                overlapping_wkt = writer.write(buffered_overlapping_geometry);
            } else {
                Geometry idaho_id_geometry = reader.read(img.metadata.getImageBoundsWGS84());
                idaho_id_geometry.setSRID(4326);

                idaho_id_geometry.getFactory().toGeometry(idaho_id_geometry.getEnvelopeInternal());
                // this gets largest rectangle, I need MAR

                Geometry idaho_id_envelope = idaho_id_geometry.getBoundary().getEnvelope();


                idaho_id_geometry.getEnvelope().buffer(-0.002);
                overlapping_wkt = writer.write(idaho_id_geometry);
            }


            // Get sample pixels of water features out of idaho image
            List<double[]> sample_pixels = getSamplePixels(img.metadata.getImageId(), nodesById);
//            System.out.println(sample_pixels);

            // Cluster samples to get significant values
            List<double[]> centroid_clusters = clusterPixels(sample_pixels);

            // Create water mask
            spectral_angle_signatures = new Gson().toJson(centroid_clusters);
            //
        } else

        {
//            idaho_id_multi = "4bb1dfb3-e252-414b-8f52-a41ce0ef774d";
            String idaho_id = "5c0280c5-3cd6-4214-a349-2bcbea5b25ad";
//        spectral_angle_signatures = "[[161.69565217391303,221.07246376811594,206.91304347826087,103.53623188405797,126.71014492753623,62.7536231884058,85.27536231884058,31.028985507246375],[164.43103448275863,230.3793103448276,225.25862068965517,115.8103448275862,145.56896551724137,78.65517241379311,106.10344827586206,37.53448275862069],[168.63636363636363,237.22727272727272,235.4090909090909,127.36363636363636,160.54545454545453,103.63636363636364,157.22727272727272,54.86363636363637]]";
            spectral_angle_signatures = "[[387.3835616438356,220.97260273972603,233.65753424657535,238.3972602739726,154.82191780821918,181.41095890410958,166.3835616438356,125.9041095890411],[413.3666666666667,246.7,293.7,324.6666666666667,217.7,422.2,560.2,491.0],[546.7586206896551,350.86206896551727,451.41379310344826,549.551724137931,369.0689655172414,624.2068965517242,759.8965517241379,665.3448275862069]]";
//        overlapping_wkt = "POLYGON ((-104.93042908658934 39.80181, -104.993076 39.80827130494398, -104.993076 39.846701, -104.925184 39.846701, -104.925184 39.80181, -104.93042908658934 39.80181))";
            overlapping_wkt = "POLYGON ((-91.0043682 30.23128629, -90.80189875 30.20764962, -90.80252901 30.07958263, -91.00382784 30.10251119, -91.0043682 30.23128629))";
            return RenderNode(idaho_id, spectral_angle_signatures, overlapping_wkt);
        }

        return RenderNode(img.metadata.getImageId(), spectral_angle_signatures, overlapping_wkt);
    }

    /**
     * @param bbox double array of upper left and lower right lat/ lon
     * @return Node hash map with lat/lon
     * @throws IOException
     */
    private static Map<Long, OsmNode> getFeatures(Double bbox[]) throws IOException {
        // bounding box - lower left, upper right
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

        String feature_url = "http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(feature_query, "UTF-8");

        Map<Long, OsmNode> nodesById = new HashMap<>();
        Map<Long, OsmWay> waysById = new HashMap<>();

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
    public static String[] getIdahoId(String wkt) throws IOException {
        CatalogManager catalogManager = new CatalogManager();

        // Spatial search
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.withSearchAreaWkt(wkt)
                .withFilters(Arrays.asList("sensorPlatformName = 'WV03'", "cloudCover < 20", "colorInterpretation = 'WORLDVIEW_8_BAND'"))
                .withTypes(Collections.singletonList("IDAHOImage"));

        SearchResponse response = catalogManager.search(searchRequest);

        System.out.println("got a total of " + response.getStats().getRecordsReturned() + " records returned");
        // this for loop is broken...
        for (Record nextRecord : response.getResults()) {
            System.out.println("got record id of \"" + nextRecord.getIdentifier() + "\" of type \"" + nextRecord.getType() + "\"");

            return new String[]{nextRecord.getIdentifier(), nextRecord.getProperties().get("footprintWkt")};

        }

        // sort results, most recent

        return new String[]{};
    }

    /**
     * Get sample pixels of water features out of idaho image
     *
     * @param idaho_id_multi idaho image id
     * @param nodesById      node
     * @return List of Double Array
     * @throws IOException
     */
    public static List<double[]> getSamplePixels(String idaho_id_multi, Map<Long, OsmNode> nodesById) throws IOException {
        // Get idaho chip
        System.out.println("Getting sample pixels...");
        long start = System.currentTimeMillis();
        String baseUrl = "http://idaho.geobigdata.io/v1";
        GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

        String pathUrl = String.format("/chip/centroid/idaho-images/%s?", idaho_id_multi);

        List<double[]> pixelvalues = new ArrayList();
        // Iterate nodes to get pixel values

        System.out.println("got " + nodesById.values().size() + " nodes");
        int nodeNumber = 0;
        for (OsmNode value : nodesById.values()) {

            Double lat = value.getLatitude();
            Double lon = value.getLongitude();

            // Get Idaho image
            String idaho_query = String.format("lat=%s&long=%s" +
                    "&width=1&height=1&resolution=0.3&bands=0,1,2,3,4,5,6,7&format=tif" +
                    "&token=%s", lat, lon, gbdxAuthManager.getAccessToken());
            BufferedImage img = null;
            try {
                URL idaho_url = new URL(baseUrl + pathUrl + idaho_query);
                InputStream input = idaho_url.openStream();
                img = ImageIO.read(input);
                input.close();

                System.out.println("got node " + nodeNumber++);

            } catch (IOException e) {
                System.out.println(e);
            }

            // Get pixel and check to see if it's black
            try {
                Raster raster_pixel = img.getData();
                double[] pixel = new double[8];

                raster_pixel.getPixel(0, 0, pixel);

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
        long end = System.currentTimeMillis();
        System.out.println("time to sample pixels: " + (end - start) + " ms.");
        return pixelvalues;
    }

    /**
     * Cluster Pixels
     *
     * @param sample_pixels List of Double array
     * @return List of Double array
     */
    private static List<double[]> clusterPixels(List<double[]> sample_pixels) {
        List<ClusterablePixel> clusterInput = new ArrayList<>();

        for (double[] pixel : sample_pixels) {
            clusterInput.add(new ClusterablePixel(pixel));
        }

        // initialize a new clustering algorithm.
        // we use KMeans++ with 10 clusters and 10000 iterations maximum.
        // we did not specify a distance measure; the default (euclidean distance) is used.
        long start = System.currentTimeMillis();
        System.out.println("Computing clusters...");
        KMeansPlusPlusClusterer<ClusterablePixel> clusterer = new KMeansPlusPlusClusterer<>(10, 1000);
        List<CentroidCluster<ClusterablePixel>> clusterResults = clusterer.cluster(clusterInput);

        Comparator<CentroidCluster<ClusterablePixel>> sort_clusters =
                (CentroidCluster<ClusterablePixel> o1, CentroidCluster<ClusterablePixel> o2) -> (Integer.valueOf(o2.getPoints().size()).compareTo(Integer.valueOf(o1.getPoints().size())));

        Collections.sort(clusterResults, sort_clusters);

        List<CentroidCluster<ClusterablePixel>> three_largestclusters = clusterResults.subList(0, 3);

        long end = System.currentTimeMillis();
        System.out.println("Time to compute clusters: " + (end - start) + " ms.");

        // output the clusters
//        System.out.print("[");
        List<double[]> centroid_clusters = new ArrayList();

        for (int i = 0; i < three_largestclusters.size(); i++) {
            Cluster<ClusterablePixel> cluster = three_largestclusters.get(i);
            if (cluster instanceof CentroidCluster) {
                CentroidCluster centroidCluster = (CentroidCluster) cluster;
                double[] point = centroidCluster.getCenter().getPoint();
                // if size of centroid cluster is >= size clusterInput * 10% then add to centroid_cluster list

//                if (centroidCluster.getPoints().size() >= clusterInput.size() * 0.10) {
//                    centroid_clusters.add(point);
//                }
                centroid_clusters.add(point);

//                String centroid = Arrays.toString(centroidCluster.getCenter().getPoint());
//                System.out.print(centroid);
            }
        }
//        System.out.println(centroid_clusters.toString());

        return centroid_clusters;
    }

    /**
     * Render IPE node
     * <p>
     * IDAHO > Ortho > Spectral Angle Mapper > Min value of each band > Threshold > Invert
     * <p>
     * Notes:
     * - You get a band per signature from spectral angle mapper. Darker = better match
     * - recursively get the minimum of each band
     * - threshold/ binarize, mask of land, invert, mask of water
     * overlay
     */
    private static String RenderNode(String idaho_id, String spectral_angle_signatures, String overlapping_wkt) throws
            ParserConfigurationException, ParseException, IOException {
        ObjectMapper om = new ObjectMapper();

        System.out.println("reading and updating graph...");
        File file = new File("graph.json");

        IPEGraph graph = om.readValue(file, IPEGraph.class);

        //update idaho id
        Map<String, String> idaho_read_parameters = new HashMap<>();
        idaho_read_parameters.put("bucketName", "idaho-images");
        idaho_read_parameters.put("imageId", idaho_id);
        idaho_read_parameters.put("objectStore", "S3");

        for (IPEGraphNode node : graph.getNodes()) {
            if (node.getId().equals("IdahoRead_nas220")) {
                node.setParameters(idaho_read_parameters);
            }
        }

        //update spectral angles
        Map<String, String> spectral_angle_parameters = new HashMap<>();
        spectral_angle_parameters.put("signatures", spectral_angle_signatures);

        for (IPEGraphNode node : graph.getNodes()) {
            if (node.getId().equals("SpectralAngle_mz58by")) {
                node.setParameters(spectral_angle_parameters);
            }
        }

        // update geo crop
        Map<String, String> crop_parameters = new HashMap<>();
        crop_parameters.put("geospatialWKT", overlapping_wkt);

        for (IPEGraphNode node : graph.getNodes()) {
            if (node.getId().equals("GeospatialCrop_aqe9xq")) {
                node.setParameters(crop_parameters);
            }
        }

        RenderedImage image = graph.getVertexAsRenderedOp("Invert_a2hsnk");

//        ParameterBlock pbC = new ParameterBlock( );
//        pbC.addSource(image);
//        pbC.add(9311f); //upper left x
//        pbC.add(5652f); //upper left y
//        pbC.add(5000f);
//        pbC.add(5000f);
//        RenderedImage crop = JAI.create("Crop", pbC);

        System.out.println(image.getMinX() + " " + image.getMinY() + "  " + image.getWidth() + " " + image.getHeight());

        // Write tif
        System.out.println("writing tif...");
        long start = System.currentTimeMillis();
        ImageIO.write(image, "TIF", new File("baton_rouge_wv03_clouds_trees.tif"));
//        ImageIO.write(image, "TIF", new File("/tmp/file.tif"));
        long end = System.currentTimeMillis();
        System.out.println("time to write: " + (end - start) + " ms.");

//        // write png
//        System.out.println("writing png..");
//        long start_png = System.currentTimeMillis();
//        ImageIO.write(image, "png", new File("/tmp/file.png"));
//        long end_png = System.currentTimeMillis();
//        System.out.println("tie to write: " + (end_png - start_png) + " ms.");

        return "file.png";
    }
}