package org.danwatt.postalvoroni;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tukaani.xz.XZInputStream;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

public class CliVoroni {
	static final GeometryFactory fac = new GeometryFactory(new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE, 2));
	static final WKTReader reader = new WKTReader(fac);

	public static void main(String[] args) throws Exception {
		String inputPath = args[0];
		String outputPath = args[1];

		Source source = new Source(inputPath);
		Set<Coordinate> encountered = new LinkedHashSet<>();
		Map<Coordinate, String> points = source.stream().sequential()//.filter(zc -> zc.getLandArea() > 0.0)
				.map(zc -> Pair.of(new Coordinate(zc.getLongitude(), zc.getLatitude()), zc.getPostalCode()))
				.filter(p-> !encountered.contains(p.getLeft()))
				.map(p -> {encountered.add(p.getLeft()); return p;})
				.collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));

		System.out.println("Loaded " + points.size() + " points");
		VoronoiDiagramBuilder vb = new VoronoiDiagramBuilder();
		Geometry bounding = loadNationalBoundingBox();
		vb.setClipEnvelope(bounding.getEnvelopeInternal());
		vb.setSites(points.keySet());
		Geometry diagram = vb.getDiagram(fac);
		//BZip2CompressorOutputStream os = new BZip2CompressorOutputStream(new FileOutputStream(outputPath));
		PrintWriter pw = new PrintWriter(new FileOutputStream("/tmp/voroni.txt"));
		System.out.println("Writing " + diagram.getNumGeometries() + " geos");
		for (int n = 0; n < diagram.getNumGeometries(); n++) {
			if (n % 1000 ==0 ) {
				System.out.println(n +" of " + diagram.getNumGeometries());
			}
			Geometry subGeo = diagram.getGeometryN(n);
			Geometry bound = subGeo.intersection(bounding);
			pw.println(points.get(subGeo.getUserData())+"\t"+bound.toText());
			//pw.println("insert into debug (zip,simple) values ('" + points.get(subGeo.getUserData()) + "',GeomFromText('" + bound.toText() + "'));");
		}
		pw.close();
	}

	public static Geometry loadNationalBoundingBox() throws ParseException {
		return reader.read(new InputStreamReader(CliVoroni.class.getClassLoader().getResourceAsStream("national.wkt.txt")));
	}

	public static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	private static Iterable<Pair<String, String>> createIterable(final String inputPath, final WKTReader reader) throws Exception {
		final LineIterator li = new LineIterator(new InputStreamReader(new XZInputStream(new FileInputStream(inputPath))));
		return new Iterable<Pair<String, String>>() {

			@Override
			public Iterator<Pair<String, String>> iterator() {
				Iterator<Pair<String, String>> i = new Iterator<Pair<String, String>>() {

					@Override
					public void remove() {
						throw new UnsupportedOperationException();

					}

					@Override
					public Pair<String, String> next() {
						String[] split = StringUtils.split(li.nextLine(), '|');
						hasNext();// Close early
						String userData = split[0];
						String wkt = split[1];
						return Pair.of(userData, wkt);
					}

					@Override
					public boolean hasNext() {
						if (!li.hasNext()) {
							LineIterator.closeQuietly(li);
							return false;
						}
						return true;
					}
				};
				return i;
			}
		};
	}
}
