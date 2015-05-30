package org.danwatt.postalvoroni;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/*
 * country code      : iso country code, 2 characters
 postal code       : varchar(20)
 place name        : varchar(180)
 admin name1       : 1. order subdivision (state) varchar(100)
 admin code1       : 1. order subdivision (state) varchar(20)
 admin name2       : 2. order subdivision (county/province) varchar(100)
 admin code2       : 2. order subdivision (county/province) varchar(20)
 admin name3       : 3. order subdivision (community) varchar(100)
 admin code3       : 3. order subdivision (community) varchar(20)
 latitude          : estimated latitude (wgs84)
 longitude         : estimated longitude (wgs84)
 accuracy          : accuracy of lat/lng from 1=estimated to 6=centroid
 */
public class PostalCode {
	public static PostalCode fromGeonames(List<String> parts) {
		return new PostalCode(parts);
	}

	private String countryCode;
	private String postalCode;
	private String placeName;// City
	private String state;
	private String stateCode;
	private String county;
	private String countyCode;
	private String community;
	private String communityCode;
	private double latitude;
	private double longitude;

	private PostalCode(List<String> parts) {
		List<String> trimmed = parts.stream().map(s -> StringUtils.trim(StringUtils.defaultString(s))).collect(Collectors.toList());
		countryCode = trimmed.get(0);
		postalCode = trimmed.get(1);
		placeName = trimmed.get(2);
		state = trimmed.get(3);
		stateCode = trimmed.get(4);
		county = trimmed.get(5);
		countyCode = trimmed.get(6);
		community = trimmed.get(7);
		communityCode = trimmed.get(8);
		latitude = Double.parseDouble(trimmed.get(9));
		longitude = Double.parseDouble(trimmed.get(10));
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String zipCode) {
		this.postalCode = zipCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCommunity() {
		return community;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCounty() {
		return county;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
}
