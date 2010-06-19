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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_PRIVILEGE")
@org.hibernate.annotations.Proxy(lazy=false)
public class Privilege extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5930761764103762420L;
	
	private Long id;
		
	private String acl;
		
	private String name;
		
	private OperatingUnitType ouType;
		
	private PrivilegeType type;
		
	private List<Role> roles = new ArrayList<Role>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)	
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="ACL", length=50)
	public String getAcl() {
		return acl;
	}
	public void setAcl(String acl) {
		this.acl = acl;
	}
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_TYPE_ID")
	@Index(name="IDX_PRI_OUTYPE")
	public OperatingUnitType getOuType() {
		return ouType;
	}
	public void setOuType(OperatingUnitType ouType) {
		this.ouType = ouType;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TYPE_ID")
	@Index(name="IDX_PRI_TYPE")
	public PrivilegeType getType() {
		return type;
	}
	public void setType(PrivilegeType type) {
		this.type = type;
	}
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="privileges")
	@OrderBy(value="id")
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
