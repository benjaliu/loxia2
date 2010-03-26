package cn.benjamin.loxia.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OptimisticLockType;

@Entity
@Table(name="T_SYS_ROLE")
@org.hibernate.annotations.Proxy(lazy=false)
@org.hibernate.annotations.Entity(optimisticLock=OptimisticLockType.VERSION)
public class Role extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4243144058217068524L;

	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="NAME", length=100)
	private String name;
	
	@Column(name="DESCRIPTION", length=2000)
	private String description;
	
	@Column(name="IS_SYSTEM")
	private Boolean isSystem = false;
	
	@Version
	@Column(name="VERSION")
	private int version;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_TYPE_ID")
	@Index(name="IDX_ROL_OUTYPE")
	private OperatingUnitType ouType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_ID")
	@Index(name="IDX_ROL_OU")
	private OperatingUnit ou;
		
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(
	        name="T_SYS_ROLEPRIV",
	        joinColumns=
	            @JoinColumn(name="ROLE_ID", referencedColumnName="ID"),
	        inverseJoinColumns=
	            @JoinColumn(name="PRIVILEGE_ID", referencedColumnName="ID")
	    )
	@OrderBy(value="acl")
	private List<Privilege> privileges = new ArrayList<Privilege>();
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public OperatingUnitType getOuType() {
		return ouType;
	}
	public OperatingUnit getOu() {
		return ou;
	}
	public void setOu(OperatingUnit ou) {
		this.ou = ou;
	}
	public void setOuType(OperatingUnitType ouType) {
		this.ouType = ouType;
	}
	public List<Privilege> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
	
}
