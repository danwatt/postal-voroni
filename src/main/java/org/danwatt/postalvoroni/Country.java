package org.danwatt.postalvoroni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class Country {

	static GeometryFactory fac = new GeometryFactory();

	public static void main(String[] args) throws IOException {
		List<Geometry> geos = Files.lines(new File(args[0]).toPath()).sequential().skip(1).map(Country::toGeometry).collect(Collectors.toList());
		
		GeometryCollection gc = fac.createGeometryCollection((Geometry[]) geos.toArray(new Geometry[geos.size()]));
		
		Geometry country = gc.buffer(0.0).union().buffer(0.2).union();
		Geometry simple = TopologyPreservingSimplifier.simplify(country, 0.025);
		System.out.println(simple.getNumPoints() +" : " + simple.toText());
	}

	public static Geometry toGeometry(String line) {
		WKTReader reader = new WKTReader(fac);
		String wkt = StringUtils.substringBetween(line, "\"","\"");
		System.out.println("Doing " + StringUtils.substringAfterLast(line, "\""));
		try {
			return reader.read(wkt).union();
		} catch (Exception e) {
		}
		return fac.createPolygon((LinearRing) null);
	}
}
