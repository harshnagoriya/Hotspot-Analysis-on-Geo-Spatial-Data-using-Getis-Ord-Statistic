
#  Hotspot Analysis on Geo-Spatial Data using Getis-Ord Statistic

## Description

Geospatial data, often known as spatial data, is a resource with a geographic component. In other terms, the information in this type of data set often comprises 2 or more dimensional attributes such as graph coordinates. Two spatial query mechanisms shall be implemented in this case namely
- Hot zone analysis
- Hot cell/hotspot analysis.
The level of geographical data collected has increased dramatically in recent years. The most significant characteristics of a cab company are the client and driver locations. As a result, they must strive to query and analyze such massive volumes of spatial data in order to achieve high performance and low latency. The ‘hot cell analysis’ applies spatial statistics to Spatio-temporal Big Data in order to identify statistically significant hot spots using Apache Spark.

### More details in the project report

## Compilation Steps

1. Go to the project root folder
2. Run ```sbt clean assembly```. You may need to install sbt in order to run this command.
3. Find the packaged jar in "./target/scala-2.11/CSE512-Project-Hotspot-Analysis-Template-assembly-0.1.0.jar"
4. Submit the jar to Spark using the Spark command "./bin/spark-submit". A pseudo-code example: ```./bin/spark-submit ~/GitHub/CSE512-Project-Hotspot-Analysis-Template/target/scala-2.11/CSE512-Project-Hotspot-Analysis-Template-assembly-0.1.0.jar test/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_tripdata_2009-01_point.csv```

#### Contributors

- [Harsh Nagoriya](https://www.linkedin.com/in/harshnagoriya/)
- [Krushali Shah](https://www.linkedin.com/in/krushali-shah/)
- [Savan Doshi](https://www.linkedin.com/in/savand/)

