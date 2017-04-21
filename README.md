# Muddy Waters
## April 2017 Hackathon

Raster mask of water including muddy flood waters with PDAP.

1. draw a bounding box, send to backend service via REST API
    - or your client can just use the backend service (such as RADD!!)

2. get OSM water features from vector services 
    - samples along feature (linestring)instead of each vertex

3. calculate overlapping wkt of bounding box and image footprint

4. get pixel samples from idaho image (tms centroid chipper) at water feature locations

5. cluster pixels to find significant samples

6. IPE Spectral Angle Mapper (idahoapi.geobigdata.io) graph

7. backend service returns graph id to leaflet client via JSON response

8. Leaflet client calculates tile geo locations and gets all tiles


other awesome features added to PDAP because of Hackathon:
- catalog v2 in idahodev.geobigdata.io - me
- pngs from idahoapitest.geobigdata.io - me
- CORS headers from idahoapitest.geobigdata.io - Scott
- acomp tms chipper and operator  - Sam

TODO:
- use vector services for OSM data - DONE
- use idahoapitest.geobigdata.io to get pngs - DONE
- use RADD to visualize results - DONE
- use acomp operator 
- acomp centroid tms
- set date range on search
- leaflet map - DONE need auth in url
- put on lambda
- add additional water feature types
- acomp on tms centroid chipper
- idahoapi.geobigdata.io GET tile needs token in the URL for leaflet to grab em


## Ashley Deaner & Scott Dunbar
Jan 2017 Hackathon

Bounty: Water detection app: draw a box, show me water	

1. leaflet map -> draw a bounding box 

2. Get water features from Open Street Map feature server (http://overpass-turbo.eu/, http://wiki.openstreetmap.org/wiki/Map_Features#Waterway)

3. Get "the best" idaho image id from catalog/v1 (WV03 with low cloud cover)

4. Calculate overlapping wkt of bounding box and image footprint

5. Get pixel samples from idaho image at water feature locations

6. Cluster pixels to find significant samples
        10 KMeans Plus Plus Clusters, keep 3 largest clusters
        
7. IPE Spectral Angle Mapper
        Idaho Image -> Ortho -> crop to interset of bounding box -> Spectral Angle Mapper -> Min of each band (each sample) -> Threshold (0.03) -> Invert
        
        todo: Run this on Batch Service -> TMS 

8. Display as layer on leaflet map


run server:
mvn package wildfly:run
