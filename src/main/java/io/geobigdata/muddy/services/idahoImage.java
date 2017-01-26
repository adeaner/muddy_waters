package io.geobigdata.muddy.services;

import com.digitalglobe.gbdx.tools.auth.GBDXAuthManager;
import com.digitalglobe.gbdx.tools.catalog.CatalogManager;
import com.digitalglobe.gbdx.tools.catalog.model.SearchRequest;
import com.digitalglobe.gbdx.tools.catalog.model.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import io.geobigdata.idaho.image.ImageMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by DGashleydeaner on 25/1/17.
 */
public class idahoImage { //extends ImageMetadata{

    public ImageMetadata metadata;

//    public idahoImage() {
//        super();
//    }

    public void setByIdahoImageId(String image_id) throws IOException {

        String url = String.format("http://idaho.geobigdata.io/v1/metadata/idaho-images/%s/image.json", image_id);

        // Get Idaho image

        ObjectMapper jacksonMapper = new ObjectMapper();
        GBDXAuthManager gbdxAuthManager = new GBDXAuthManager();

        URL idaho_url = new URL(url);
        HttpURLConnection idaho_url_connection = (HttpURLConnection) idaho_url.openConnection();
        idaho_url_connection.setRequestProperty("Authorization", String.format("Bearer %s", gbdxAuthManager.getAccessToken()));

        InputStream imgJSON = idaho_url_connection.getInputStream();


        metadata = jacksonMapper.readValue(imgJSON, ImageMetadata.class);

//        super(metadata);

    }

    public void setByCatalogId(String catalogId) throws IOException, com.vividsolutions.jts.io.ParseException {
        CatalogManager catalogManager = new CatalogManager();

        // Spatial search
        SearchRequest searchRequest = new SearchRequest();

        List<String> filter = Collections.singletonList(String.format("vendorDatasetIdentifier3 = '%s'", catalogId));
        searchRequest.withFilters(filter)
                .withTypes(Collections.singletonList("IDAHOImage"));


        SearchResponse response = catalogManager.search(searchRequest);

        System.out.println("got a total of " + response.getStats().getRecordsReturned() + " records returned");
        this.setByIdahoImageId(response.getResults().get(0).getIdentifier());

    }

    public void setByWKT(String wkt) throws IOException, com.vividsolutions.jts.io.ParseException {
        // Get "best" idaho image

        CatalogManager catalogManager = new CatalogManager();

        // Spatial search
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.withSearchAreaWkt(wkt)
                .withFilters(Arrays.asList("sensorPlatformName = 'WV03'", "cloudCover < 20", "colorInterpretation = 'WORLDVIEW_8_BAND'"))
                .withTypes(Collections.singletonList("IDAHOImage"));

        SearchResponse response = catalogManager.search(searchRequest);

        System.out.println("got a total of " + response.getStats().getRecordsReturned() + " records returned");
        // sort results, most recent
        this.setByIdahoImageId(response.getResults().get(0).getIdentifier());

    }

    public Double[] getBoundingBox() throws com.vividsolutions.jts.io.ParseException {
//        String wkt = response.getResults().get(0).getProperties().get("footprintWkt");
        WKTReader reader = new WKTReader();
        Geometry geometry = reader.read(this.metadata.getImageBoundsWGS84());
        geometry.setSRID(4326);

        return new Double[]{geometry.getCoordinates()[0].y, geometry.getCoordinates()[0].x,
                geometry.getCoordinates()[2].y, geometry.getCoordinates()[2].x};
    }
}