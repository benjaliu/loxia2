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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_PRIVILEGE")
@org.hibernate.annotations.Proxy(lazy=false)
public class Privilege extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5930761764103762420L;

	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="ACL", length=50)
	private String acl;
	
	@Column(name="NAME")
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_TYPE_ID")
	@Index(name="IDX_PRI_OUTYPE")
	private OperatingUnitType ouType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TYPE_ID")
	@Index(name="IDX_PRI_TYPE")
	private PrivilegeType type;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="privileges")
	@OrderBy(value="id")
	private List<Role> roles = new ArrayList<Role>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAcl() {
		return acl;
	}
	public void setAcl(String acl) {
		this.acl = acl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OperatingUnitType getOuType() {
		return ouType;
	}
	public void setOuType(OperatingUnitType ouType) {
		this.ouType = ouType;
	}
	public PrivilegeType getType() {
		return type;
	}
	public void setType(PrivilegeType type) {
		this.type = type;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
