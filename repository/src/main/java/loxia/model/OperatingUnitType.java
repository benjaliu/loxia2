package loxia.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_OPERATING_UNIT_TYPE")
@org.hibernate.annotations.Proxy(lazy=false)
public class OperatingUnitType extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6073515644265892839L;
	
	private Long id;
		
	private String name;
		
	private String displayName;
		
	private Boolean isAvailable = true;
		
	private String description;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="NAME")
	@Index(name="IDX_OPUT_NAME")
	@org.hibernate.annotations.NaturalId
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="DISPLAY_NAME", length=100)
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Column(name="IS_AVAILABLE")
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	@Column(name="DESCRIPTION", length=2000)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
