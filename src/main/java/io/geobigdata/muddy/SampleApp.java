package io.geobigdata.muddy;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class SampleApp
{
    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException
    {
        String query = "http://overpass-api.de/api/interpreter?data=%2F*%0AThis%20has%20been%20generated%20by%20the%20overpass-turbo%20wizard.%0AThe%20original%20search%20was%3A%0A%E2%80%9Csport%3Dtennis%E2%80%9D%0A*%2F%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Csport%3Dtennis%E2%80%9D%0A%20%20node%5B%22sport%22%3D%22tennis%22%5D%2839.760172851807184%2C-105.13907432556151%2C39.77251009589789%2C-105.11881828308105%29%3B%0A%20%20way%5B%22sport%22%3D%22tennis%22%5D%2839.760172851807184%2C-105.13907432556151%2C39.77251009589789%2C-105.11881828308105%29%3B%0A%20%20relation%5B%22sport%22%3D%22tennis%22%5D%2839.760172851807184%2C-105.13907432556151%2C39.77251009589789%2C-105.11881828308105%29%3B%0A%29%3B%0A%2F%2F%20print%20results%0Aout%20body%3B%0A%3E%3B%0Aout%20skel%20qt%3B";
        //String query = "http://overpass-api.de/api/interpreter?data=%2F*%0AThis%20has%20been%20generated%20by%20the%20overpass-turbo%20wizard.%0AThe%20original%20search%20was%3A%0A%E2%80%9Csport%3Dtennis%E2%80%9D%0A*%2F%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Csport%3Dtennis%E2%80%9D%0A%20%20node%5B%22sport%22%3D%22tennis%22%5D%2839.769425992128724%2C-105.10977923870087%2C39.773074950200034%2C-105.10484397411346%29%3B%0A%20%20way%5B%22sport%22%3D%22tennis%22%5D%2839.769425992128724%2C-105.10977923870087%2C39.773074950200034%2C-105.10484397411346%29%3B%0A%29%3B%0A%2F%2F%20print%20results%0Aout%20body%3B%0A%3E%3B%0Aout%20skel%20qt%3B";
        // Define a query to retrieve some data
        //String query = "http://www.overpass-api.de/api/xapi?map?bbox="
        //  + "13.465661,52.504055,13.469817,52.506204";
        //String query = "http://api.openstreetmap.org/api/0.6/map?bbox=-122.19,37.61,-122.05,37.75";

        Map<Long, OsmNode> nodesById = new HashMap<Long, OsmNode>();
        Map<Long, OsmWay> waysById = new HashMap<Long, OsmWay>();

        // Open a stream
        InputStream input = new URL(query).openStream();

        // Create a reader for XML data
        OsmIterator iterator = new OsmXmlIterator(input, false);

        // Iterate contained entities
        for (EntityContainer container : iterator) {

            if(EntityType.Node.equals(container.getType())){
                OsmNode node = (OsmNode) container.getEntity();
                nodesById.put(node.getId(), node);
                System.out.println("Added node: "+node.getId());
            } else if(EntityType.Way.equals(container.getType())){
                OsmWay way = (OsmWay) container.getEntity();
                waysById.put(way.getId(), way);
                System.out.println("Added way: "+way.getId());
            }
        }

        for(Long key : waysById.keySet()){
            OsmWay way = waysById.get(key);
            GeometryFactory gf = new GeometryFactory();
            Coordinate[] coords = new Coordinate[way.getNumberOfNodes()];
            for(int i=0;i<way.getNumberOfNodes();i++){
                Long nodeId = way.getNodeId(i);
                OsmNode node = nodesById.get(nodeId);
                coords[i] = new Coordinate(node.getLongitude(), node.getLatitude());
            }
            Polygon polygon = gf.createPolygon(coords);
            WKTWriter writer = new WKTWriter();
            String wkt = writer.write(polygon);
            System.out.println(wkt);
        }
    }
}
