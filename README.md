# Muddy Waters
Ashley Deaner & Scott Dunbar
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
