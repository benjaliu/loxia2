package loxia.orm.hibernate5;

import org.springframework.core.io.Resource;

public class ConfigLocation {

	private String[] packagesToScan;
	private Resource[] mappingLocations;
	public String[] getPackagesToScan() {
		return packagesToScan;
	}
	public void setPackagesToScan(String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}
	public Resource[] getMappingLocations() {
		return mappingLocations;
	}
	public void setMappingLocations(Resource[] mappingLocations) {
		this.mappingLocations = mappingLocations;
	}
}
