package loxia.model;

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
	
	private Long id;
		
	private String name;
		
	private String description;
		
	private Boolean isSystem = false;
		
	private int version;
		
	private OperatingUnitType ouType;
		
	private OperatingUnit ou;
			
	private List<Privilege> privileges = new ArrayList<Privilege>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="NAME", length=100)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="DESCRIPTION", length=2000)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="IS_SYSTEM")
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	@Version
	@Column(name="VERSION")
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_TYPE_ID")
	@Index(name="IDX_ROL_OUTYPE")
	public OperatingUnitType getOuType() {
		return ouType;
	}
	public void setOuType(OperatingUnitType ouType) {
		this.ouType = ouType;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_ID")
	@Index(name="IDX_ROL_OU")
	public OperatingUnit getOu() {
		return ou;
	}
	public void setOu(OperatingUnit ou) {
		this.ou = ou;
	}
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(
	        name="T_SYS_ROLEPRIV",
	        joinColumns=@JoinColumn(name="ROLE_ID", referencedColumnName="ID"),
	        inverseJoinColumns=@JoinColumn(name="PRIVILEGE_ID", referencedColumnName="ID")
	    )
	@OrderBy(value="acl")
	public List<Privilege> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
	
}
