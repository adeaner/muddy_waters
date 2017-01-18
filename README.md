# muddy_waters
Jan 2017 Hackathon


given a bbox

feature query osm

get idaho catid(s) from catalog/v1

get pixel values from feature lat/lon
        get all the bands, don't use pan band or pan sharpen
        list of pixel values

pixel values massaged with clusters (ex 10 clusters and throw away insignificant samples), cluster to vector

ipe bbox with spectral angle mapper from above pixel values

display on map


run server:
mvn package wildfly:run
