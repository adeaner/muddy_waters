//package io.geobigdata.muddy;
//
//import io.geobigdata.analytics.ClusterablePixel;
//import io.geobigdata.idaho.Constants;
//import io.geobigdata.idaho.image.ImageGeoreferencing;
//
//import java.awt.geom.NoninvertibleTransformException;
//import java.awt.geom.Point2D;
//import java.awt.image.Raster;
//import java.awt.image.renderable.ParameterBlock;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.media.jai.JAI;
//import javax.media.jai.RenderedOp;
//
//import org.apache.commons.math3.ml.clustering.CentroidCluster;
//import org.apache.commons.math3.ml.clustering.Cluster;
//import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
//import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
//
//public class ClusterTest {
//
//    public static void main(String[] args) throws NoninvertibleTransformException {
//        ClusterTest ct = new ClusterTest();
//        ct.run();
//    }
//
//    public void run() throws NoninvertibleTransformException {
//        //String imageId = "b6b4c622-f7f4-480c-b6a7-37fddcac6423"; // 2x downsample
//        //String imageId = "aba86166-77b6-4d97-a49c-68814416013c"; // 4x downsample - 1626 ms
//        //String imageId = "4239d48d-1b53-4ccd-ae2a-76c59c69ac85";
//
//        Point2D.Double target = new Point2D.Double(13.6899, 11.0890);
//        //Gowza before
//        String imageId = "374220df-eca6-4c3f-947c-d8ad67ed066b";
//        //Gwoza after
//        //String imageId = "376bfb30-a513-46ee-8e03-0301c94700db";
//
//
//        ParameterBlock pbS1 = new ParameterBlock();
//        pbS1.add(imageId);
//        pbS1.add("idaho-images");
//        pbS1.add(Constants.OS_S3);
//        RenderedOp s1 = JAI.create("IdahoRead", pbS1);
//
//        ParameterBlock pbs2 = new ParameterBlock();
//        pbs2.addSource(s1);
//        RenderedOp ortho = JAI.create("GridOrthorectify", pbs2);
//
//        ImageGeoreferencing georef = (ImageGeoreferencing) ortho.getProperty(Constants.GEOTRANSFORM_PROPERTY_NAME);
//
//        Point2D.Double pixelPoint = new Point2D.Double();
//        georef.toAffineTransform().inverseTransform(target, pixelPoint);
//        System.out.println(pixelPoint);
//
//        ParameterBlock pbC = new ParameterBlock();
//        pbC.addSource(ortho);
//        pbC.add((float) pixelPoint.getX());
//        pbC.add((float) pixelPoint.getY());
//        pbC.add(512f);
//        pbC.add(512f);
//        RenderedOp crop = JAI.create("Crop", pbC);
//
//        //JAI.create("filestore", crop, "/Users/nmcintyr/gwoza_after.tif");
//
//        List<ClusterablePixel> clusterInput = new ArrayList<ClusterablePixel>();
//        clusterInput.addAll(rasterAsArray(crop.getData()));
//
//		/*
//		for(int y=s1.getMinTileY();y<=s1.getMaxTileY();y++){
//			for(int x=s1.getMinTileX();x<=s1.getMaxTileX();x++){
//				clusterInput.addAll(rasterAsArray(s1.getTile(x,y)));
//			}
//		}
//		*/
//
//        // initialize a new clustering algorithm.
//        // we use KMeans++ with 10 clusters and 10000 iterations maximum.
//        // we did not specify a distance measure; the default (euclidean distance) is used.
//        long start = System.currentTimeMillis();
//        System.out.println("Computing clusters...");
//        //DBSCANClusterer<ClusterablePixel> clusterer = new DBSCANClusterer<ClusterablePixel>(10, 50);
//        KMeansPlusPlusClusterer<ClusterablePixel> clusterer = new KMeansPlusPlusClusterer<ClusterablePixel>(10, 1000);
//        List<CentroidCluster<ClusterablePixel>> clusterResults = clusterer.cluster(clusterInput);
//        long end = System.currentTimeMillis();
//        System.out.println("Time to compute clusters: " + (end - start) + " ms.");
//
//        // output the clusters
//        System.out.print("[");
//        for (int i = 0; i < clusterResults.size(); i++) {
//            Cluster<ClusterablePixel> cluster = clusterResults.get(i);
//            if (cluster instanceof CentroidCluster) {
//                CentroidCluster centroidCluster = (CentroidCluster) cluster;
//                String centroid = Arrays.toString(centroidCluster.getCenter().getPoint());
//                System.out.print(centroid);
//            }
//        }
//        System.out.println("]");
//    }
//
//    private List<ClusterablePixel> rasterAsArray(Raster r) {
//        List<ClusterablePixel> pixels = new ArrayList<ClusterablePixel>();
//        for (int y = r.getMinY(); y < r.getMinY() + r.getHeight(); y++) {
//            for (int x = r.getMinX(); x < r.getMinX() + r.getWidth(); x++) {
//                double[] pixel = new double[r.getNumBands()];
//                r.getPixel(x, y, pixel);
//                pixels.add(new ClusterablePixel(pixel));
//            }
//        }
//        return pixels;
//    }
//
//
//}