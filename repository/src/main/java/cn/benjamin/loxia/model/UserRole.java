package cn.benjamin.loxia.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OptimisticLockType;

@Entity
@Table(name="T_SYS_USERROLE")
@org.hibernate.annotations.Proxy(lazy=false)
@org.hibernate.annotations.Entity(optimisticLock=OptimisticLockType.VERSION)
public class UserRole extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6349555368018755912L;

	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="IS_DEFAULT")
	private Boolean isDefault = false;
	
	@Version
	@Column(name="VERSION")
	private int version;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID")
	@Index(name="IDX_UR_USER")
	private InnerUser user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID")
	@Index(name="IDX_UR_ROLE")
	private Role role;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_ID")
	@Index(name="IDX_UR_OU")
	private OperatingUnit ou;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public InnerUser getUser() {
		return user;
	}
	public void setUser(InnerUser user) {
		this.user = user;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public OperatingUnit getOu() {
		return ou;
	}
	public void setOu(OperatingUnit ou) {
		this.ou = ou;
	}
	
}
