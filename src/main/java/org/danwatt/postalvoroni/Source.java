package org.danwatt.postalvoroni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.base.Splitter;

public class Source {
	private static final Splitter splitter = Splitter.on("\t");
	private String path;

	public Source(String path) {
		this.path = path;
	}

	// ZipCode,Latitude,Longitude,State,City,CityAliasName,CityType,FacilityCode,LandArea,WaterArea,PopulationEstimate,ClassificationCode
	public Stream<PostalCode> stream() {
		try {
			return Files.readAllLines(new File(path).toPath()).stream().map(l -> splitter.splitToList(l)).map(parts -> PostalCode.fromGeonames(parts));
		} catch (IOException e) {
			return Arrays.<PostalCode> asList().stream();
		}
	}
}
