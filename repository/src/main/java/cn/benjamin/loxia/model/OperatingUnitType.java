package cn.benjamin.loxia.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_OPERATING_UNIT_TYPE")
@org.hibernate.annotations.Proxy(lazy=false)
public class OperatingUnitType extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6073515644265892839L;

	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="NAME")
	@Index(name="IDX_OPUT_NAME")
	@org.hibernate.annotations.NaturalId
	private String name;
	
	@Column(name="DISPLAY_NAME", length=100)
	private String displayName;
	
	@Column(name="IS_AVAILABLE")
	private Boolean isAvailable = true;
	
	@Column(name="DESCRIPTION", length=2000)
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
