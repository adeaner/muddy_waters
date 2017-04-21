package io.geobigdata.muddy;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import com.digitalglobe.gbdx.tools.config.ConfigurationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import io.geobigdata.ipe.IPEGraph;
import io.geobigdata.ipe.IPEGraphNode;
import io.geobigdata.muddy.services.idahoImage;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Muddy Waters
 */
public class BoundingBoxWaterMask {
    private static final Logger logger = LoggerFactory.getLogger(BoundingBoxWaterMask.class);

    public void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException, ParseException, com.vividsolutions.jts.io.ParseException,
            org.json.simple.parser.ParseException, FactoryException,
            org.opengis.referencing.operation.TransformException, URISyntaxException {

        // upper left lat/lon, lower right lat/lon

//        Double bbox[] = {39.84670129520201, -104.99307632446288, 39.801810432481645, -104.92518424987793}; // Commerce City Platte River
//        Double bbox[] = {39.945172035117984, -104.87874984741211, 39.92161054620153, -104.85591888427734}; //
//        Double bbox[] = {39.96977788803444, -105.2490234375, 39.93003569725961, -105.20387649536133 }; //marshal rd
//        Double bbox[] = {30.412077166683314, -81.49520874023438, 30.37772538837059, -81.44233703613281}; //jackson beach
//        Double bbox[] = {39.779040683054156, -105.24593353271484, 39.75449829691315, -105.18928527832031 }; //golden
//        bbox = new Double[]{40.76004940214887, -106.32499694824219, 40.710621542994936, -106.26697540283203}; // upper, left, lower, right

//        Double bbox[] = {36.43238395557654, -76.32991790771484 , 36.405862003277065, -76.28082275390625}; // Elizabeth City, NC

//        String cat_id = "103001005B3EEE00";
//        String cat_id = "1040010020787A00";
        // 498018dd-7d00-481b-abc5-04d7aed04c0b wv03 beautiul

        idahoImage img = new idahoImage();
        img.setByIdahoImageId("5c0280c5-3cd6-4214-a349-2bcbea5b25ad"); //wv02 baton rouge
        String aoi = "POLYGON((-90.98670959472656 30.106041238914163,-90.8294677734375 30.106041238914163,-90.8294677734375 30.006196189088108,-90.98670959472656 30.006196189088108,-90.98670959472656 30.106041238914163))";

//        img.setByIdahoImageId("2bb19b93-86aa-46c5-b2dc-a683c015e6d8"); // wv02 w/ clouds
//        String aoi = "POLYGON((-90.6866455078125 30.23664291004952,-90.65574645996094 30.23664291004952,-90.65574645996094 30.209647267420458,-90.6866455078125 30.209647267420458,-90.6866455078125 30.23664291004952))";

//        img.setByIdahoImageId("2ef9bcd0-e219-41bc-adf5-4c4ce2fd0305"); // wv03 w/ clouds
//        String aoi = "POLYGON((-91.01898193359375 30.161269281485744,-90.89057922363281 30.161269281485744,-90.89057922363281 30.07098807994365,-91.01898193359375 30.07098807994365,-91.01898193359375 30.161269281485744))";

//        img.setByIdahoImageId("2c75460c-0712-4d98-a2a0-322fd7df9044"); // wv03 w/ clouds
//        String aoi = "POLYGON((-91.24214172363281 30.2206197762358,-91.00181579589844 30.2206197762358,-91.00181579589844 30.106635253152803,-91.24214172363281 30.106635253152803,-91.24214172363281 30.2206197762358))";

//        img.setByIdahoImageId("2bb19b93-86aa-46c5-b2dc-a683c015e6d8"); // wv03 w/ clouds in the tree
//        String aoi = "POLYGON((-90.6866455078125 30.23664291004952,-90.65574645996094 30.23664291004952,-90.65574645996094 30.209647267420458,-90.6866455078125 30.209647267420458,-90.6866455078125 30.23664291004952))";


//        img.setByIdahoImageId("8099ac52-4337-4ff8-b7b1-b38c332042e6"); // wv03 hazy
//        String aoi = "POLYGON((-91.06636047363281 30.250874750415438,-90.97160339355469 30.250874750415438,-90.97160339355469 30.158300818026124,-91.06636047363281 30.158300818026124,-91.06636047363281 30.250874750415438))";


//        img.setByCatalogId(cat_id);
//        String aoi = "POLYGON((-90.99838256835938 30.20519208886129,-90.8074951171875 30.20519208886129,-90.8074951171875 30.10307111415961,-90.99838256835938 30.10307111415961,-90.99838256835938 30.20519208886129))";

        getWaterMask(img, aoi);
    }


    public String getWaterMask(idahoImage img, String... aoi_wkt) throws ParseException,
            ParserConfigurationException, com.vividsolutions.jts.io.ParseException, IOException,
            org.json.simple.parser.ParseException, FactoryException,
            org.opengis.referencing.operation.TransformException, URISyntaxException {


        Boolean DEBUG = false;
        String spectral_angle_signatures;
        String overlapping_wkt;
        if (!DEBUG) {
            // Get water features from OSM
            List<Point> nodesById = getFeatures(img.getBoundingBox());

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

                idaho_id_geometry.getEnvelope().buffer(-0.002);
                overlapping_wkt = writer.write(idaho_id_geometry);
            }

            // Get sample pixels of water features out of idaho image
            List<double[]> sample_pixels = getSamplePixels(img.metadata.getImageId(), nodesById);

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

    private List<Point> createSegments(Geometry track, double segmentLength) throws
            FactoryException {

        GeodeticCalculator calculator = new GeodeticCalculator(CRS.decode("EPSG:4326")); // KML uses WGS84
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

        LinkedList<Coordinate> coordinates = new LinkedList<>();
        Collections.addAll(coordinates, track.getCoordinates());

        double accumulatedLength = 0;
//        List<Coordinate> lastSegment = new ArrayList<>();
//        List<LineString> segments = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        Iterator<Coordinate> itCoordinates = coordinates.iterator();

        for (int i = 0; itCoordinates.hasNext() && i < coordinates.size() - 1; i++) {
            Coordinate c1 = coordinates.get(i);
            Coordinate c2 = coordinates.get(i + 1);

//            lastSegment.add(c1);

            calculator.setStartingGeographicPoint(c1.x, c1.y);
            calculator.setDestinationGeographicPoint(c2.x, c2.y);

            double length = calculator.getOrthodromicDistance();

            if (length + accumulatedLength >= segmentLength) {
                double offsetLength = segmentLength - accumulatedLength;
                double ratio = offsetLength / length;
                double dx = c2.x - c1.x;
                double dy = c2.y - c1.y;

                Coordinate segmentationPoint = new Coordinate(c1.x + (dx * ratio),
                        c1.y + (dy * ratio));

//                lastSegment.add(segmentationPoint); // Last point of the segment is the segmentation point
//                segments.add(geometryFactory.createLineString(lastSegment.toArray(new Coordinate[lastSegment.size()])));
                points.add(geometryFactory.createPoint(segmentationPoint));

//                lastSegment = new ArrayList<Coordinate>(); // Resets the variable since a new segment will be built
                accumulatedLength = 0D;
                coordinates.add(i + 1, segmentationPoint);
            } else {
                accumulatedLength += length;
            }
        }

//        lastSegment.add(coordinates.getLast()); // Because the last one is never added in the loop above
//        segments.add(geometryFactory.createLineString(lastSegment.toArray(new Coordinate[lastSegment.size()])));
        points.add(geometryFactory.createPoint(coordinates.getLast()));

        return points;
    }

    /**
     * @param bbox double array of upper left and lower right lat/ lon
     * @return Node hash map with lat/lon
     * @throws IOException, org.json.simple.parser.ParseException,
     *                      FactoryException, org.opengis.referencing.operation.TransformException, NullPointerException
     */
    private List<Point> getFeatures(Double bbox[]) throws IOException, org.json.simple.parser.ParseException,
            FactoryException, org.opengis.referencing.operation.TransformException, NullPointerException {
        // bounding box - lower left, upper right
        String feature_query = "ingest_source:OSM AND item_type:River";

        String feature_url = "https://vector.geobigdata.io/insight-vector/api/vectors/query/items?q=" +
                URLEncoder.encode(feature_query, "UTF-8") + "&left=" + bbox[1] + "&right=" + bbox[3] +
                "&upper=" + bbox[0] + "&lower=" + bbox[2] + "&count=100";

        // Open a stream
        ConfigurationManager gbdxAuthManager = new ConfigurationManager();
        HttpGet imageRequest = new HttpGet(feature_url);
        imageRequest.setHeader("Authorization", "Bearer " + gbdxAuthManager.getAccessToken());
        logger.info("Getting features...");
        HttpResponse imageResponse = HttpClientBuilder.create().build().execute(imageRequest);

        assert imageResponse.getStatusLine().getStatusCode() == 200;

        String features_string = EntityUtils.toString(imageResponse.getEntity());
        String feature_collection_string = "{\"type\": \"FeatureCollection\", \"features\": " + features_string + "}";


        FeatureJSON jfc = new FeatureJSON();
        org.geotools.feature.FeatureCollection fc;
        try {
            fc = jfc.readFeatureCollection(feature_collection_string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FeatureIterator<SimpleFeature> iter = fc.features();
        List<Point> allpoints = new ArrayList<>();
        while (iter.hasNext()) {
            SimpleFeature sf = iter.next();
            Geometry geom = (Geometry) sf.getDefaultGeometry();
            allpoints.addAll(createSegments(geom, 500.0));
            logger.info(geom.toString());
        }

        return allpoints;

    }


    /**
     * Get sample pixels of water features out of idaho image
     *
     * @param idaho_id_multi idaho image id
     * @param nodesById      node
     * @return List of Double Array
     * @throws IOException, description
     */
    private List<double[]> getSamplePixels(String idaho_id_multi, List<Point> nodesById) throws IOException,
            NullPointerException {
        // Get idaho chip
        logger.info("Getting sample pixels...");
        long start = System.currentTimeMillis();
        String baseUrl = "http://idaho.geobigdata.io/v1";
        ConfigurationManager gbdxAuthManager = new ConfigurationManager();

        String pathUrl = String.format("/chip/centroid/idaho-images/%s?", idaho_id_multi);

        List<double[]> pixelvalues = new ArrayList();
        // Iterate nodes to get pixel values

        logger.info("got " + nodesById.size() + " nodes");

        if (nodesById.size() == 0) {
            throw new RuntimeException("No features found");
        }
        int nodeNumber = 0;
        for (Point value : nodesById) {

            Double lat = value.getY();
            Double lon = value.getX();

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

                logger.info("got node " + nodeNumber++);

            } catch (IOException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }

        }
        long end = System.currentTimeMillis();
        logger.info("time to sample pixels: " + (end - start) + " ms.");
        return pixelvalues;
    }

    /**
     * Cluster Pixels
     *
     * @param sample_pixels List of Double array
     * @return List of Double array
     */
    private List<double[]> clusterPixels(List<double[]> sample_pixels) {
        List<ClusterablePixel> clusterInput = new ArrayList<>();

        for (double[] pixel : sample_pixels) {
            clusterInput.add(new ClusterablePixel(pixel));
        }

        // initialize a new clustering algorithm.
        // we use KMeans++ with 10 clusters and 10000 iterations maximum.
        // we did not specify a distance measure; the default (euclidean distance) is used.
        long start = System.currentTimeMillis();
        logger.info("Computing clusters...");
        KMeansPlusPlusClusterer<ClusterablePixel> clusterer = new KMeansPlusPlusClusterer<>(10, 1000);
        List<CentroidCluster<ClusterablePixel>> clusterResults = clusterer.cluster(clusterInput);

        Comparator<CentroidCluster<ClusterablePixel>> sort_clusters =
                (CentroidCluster<ClusterablePixel> o1, CentroidCluster<ClusterablePixel> o2) -> (Integer.valueOf(o2.getPoints().size()).compareTo(Integer.valueOf(o1.getPoints().size())));

        Collections.sort(clusterResults, sort_clusters);

        List<CentroidCluster<ClusterablePixel>> three_largestclusters = clusterResults.subList(0, 3);

        long end = System.currentTimeMillis();
        logger.info("Time to compute clusters: " + (end - start) + " ms.");

        // output the clusters
        List<double[]> centroid_clusters = new ArrayList();

        for (int i = 0; i < three_largestclusters.size(); i++) {
            Cluster<ClusterablePixel> cluster = three_largestclusters.get(i);
            if (cluster instanceof CentroidCluster) {
                CentroidCluster centroidCluster = (CentroidCluster) cluster;
                double[] point = centroidCluster.getCenter().getPoint();
                centroid_clusters.add(point);

            }
        }

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
    private String RenderNode(String idaho_id, String spectral_angle_signatures, String overlapping_wkt) throws
            ParserConfigurationException, ParseException, IOException, URISyntaxException {

        ObjectMapper om = new ObjectMapper();

        logger.info("reading and updating graph...");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("graph.json");

        IPEGraph graph = om.readValue(is, IPEGraph.class);

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

        long start = System.currentTimeMillis();

        String graph_string = new ObjectMapper().writeValueAsString(graph);
        logger.info(graph_string);

        // register graph
        ConfigurationManager gbdxAuthManager = new ConfigurationManager();
        HttpPost registerGraphRequest = new HttpPost("http://idahoapi.geobigdata.io/v1/graph");
        registerGraphRequest.setHeader("Authorization", "Bearer " + gbdxAuthManager.getAccessToken());
        registerGraphRequest.setHeader("Content-type", "application/json");
        StringEntity graphFileStringEntity = new StringEntity(graph_string);
        registerGraphRequest.setEntity(graphFileStringEntity);

        HttpResponse registerGraphResponse = HttpClientBuilder.create().build().execute(registerGraphRequest);

        String graphId = EntityUtils.toString(registerGraphResponse.getEntity());
        logger.info(graphId);
//        String idahoUrl = "http://idahoapitest.geobigdata.io/v1/tile/virtual-idaho/" + graphId + "/Invert/10/10.png";
//
//        HttpGet imageRequest = new HttpGet(idahoUrl);
//        imageRequest.setHeader("Authorization", "Bearer " + gbdxAuthManager.getAccessToken());
//        HttpResponse imageResponse = HttpClientBuilder.create().build().execute(imageRequest);
//
//        InputStream imageStream = imageResponse.getEntity().getContent();
//        BufferedImage img = ImageIO.read(imageStream);
//
//        if (img == null){
//            throw new RuntimeException("img is null");
//        }
//
//        logger.info("writing tif...");
//        ImageIO.write(img, "png", new File("/tmp/file.png"));
//        long end = System.currentTimeMillis();
//        logger.info("time to write: " + (end - start) + " ms.");

        return graphId;
    }
}