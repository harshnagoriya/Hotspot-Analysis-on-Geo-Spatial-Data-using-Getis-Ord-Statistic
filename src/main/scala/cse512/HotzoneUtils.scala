package cse512

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, ptString: String ): Boolean = {
    // YOU NEED TO CHANGE THIS PART
    
    if( queryRectangle.isEmpty() || ptString.isEmpty() || ptString == null || queryRectangle == null )
      return false
	
	val pt = ptString.split(",").map(_.toDouble)
	val rctcoord = queryRectangle.split(",").map(_.toDouble)
    var xMaxVal = rctcoord(0).max(rctcoord(2))
	var yMaxVal = rctcoord(1).max(rctcoord(3))
    var xMinVal = rctcoord(0).min(rctcoord(2))
    var yMinVal = rctcoord(1).min(rctcoord(3))

    if( ( yMinVal <= pt(1) && yMaxVal >= pt(1)) && ( xMinVal <= pt(0) && xMaxVal >= pt(0) ) )
      return true
    else
      return false
  }

  // YOU NEED TO CHANGE THIS PART

}
