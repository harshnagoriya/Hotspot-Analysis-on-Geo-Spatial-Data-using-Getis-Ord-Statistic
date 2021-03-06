package cse512

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
{
  // Load the original data from a data source
  var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter",";").option("header","false").load(pointPath);
  pickupInfo.createOrReplaceTempView("nyctaxitrips")
  pickupInfo.show()

  // Assign cell coordinates based on pickup points
  spark.udf.register("CalculateX",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 0)
    )))
  spark.udf.register("CalculateY",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 1)
    )))
  spark.udf.register("CalculateZ",(pickupTime: String)=>((
    HotcellUtils.CalculateCoordinate(pickupTime, 2)
    )))
  pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
  var newCoordinateName = Seq("x", "y", "z")
  pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
  pickupInfo.show()

  // Define the min and max of x, y, z
  val minX = -74.50/HotcellUtils.coordinateStep
  val maxX = -73.70/HotcellUtils.coordinateStep
  val minY = 40.50/HotcellUtils.coordinateStep
  val maxY = 40.90/HotcellUtils.coordinateStep
  val minZ = 1
  val maxZ = 31
  val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)

  // YOU NEED TO CHANGE THIS PART
  pickupInfo.createOrReplaceTempView("pickupInfo")
  
  val mmp = spark.sql("SELECT x,y,z,count(*) as pickupCount from pickupInfo where x>=" + minX + " and x<=" + maxX + " and y<=" + maxY + " and y>=" + minY + " and z<=" + maxZ + " and z>=" + minZ + " group by x,y,z").orderBy("z", "y", "x")
  mmp.createOrReplaceTempView("hotCellInfo")
  
  var auxVar = spark.sql("SELECT sum(pickupCount) as sumPickUp,sum(pickupCount*pickupCount) as sumSqr from hotCellInfo")    
  
  var sumT = auxVar.first().getLong(0)
  
  var SqSum = auxVar.first().getLong(1)
  
  var meanVal = sumT / numCells;
  
  var stdd = Math.sqrt( (SqSum / numCells) - Math.pow(meanVal, 2) )
  
  val neighbourData = spark.sql("SELECT hc1.x as x, hc1.y as y, hc1.z as z, count(*) neighbourCount, sum(hc2.pickupCount) as weight from hotCellInfo as hc1, hotCellInfo as hc2 where abs(hc1.z-hc2.z)<=1 and abs(hc1.y-hc2.y)<=1 and abs(hc1.x-hc2.x)<=1 group by hc1.x,hc1.y,hc1.z")
  
  neighbourData.createOrReplaceTempView("neighbourData")
  
  spark.udf.register("getGIS", (stdd: Double, meanVal: Double, weight: Int, numCells: Int, x: Int, y:Int, z: Int, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) =>
    (HotcellUtils.getGIS(stdd, meanVal, weight, numCells, x, y, z, minX, minY, minZ, maxX, maxY, maxZ))
  )
  
  val scoreGIS = spark.sql("SELECT x,y,z,getGIS(" + stdd + "," + meanVal + ",weight," + numCells + ",x,y,z, "+minX+","+minY+","+minZ+","+maxX+","+maxY+","+maxZ+") as gScore from neighbourData ")
  scoreGIS.createOrReplaceTempView("scoreGIS")
  
  val op = spark.sql("SELECT x,y,z from scoreGIS order by gScore desc")
  
  return op
}
}
