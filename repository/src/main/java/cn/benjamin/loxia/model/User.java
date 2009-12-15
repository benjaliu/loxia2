package cn.benjamin.loxia.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OptimisticLockType;

import cn.benjamin.loxia.model.BaseModel;

@Entity
@Table(name="T_SYS_USER")
@org.hibernate.annotations.Proxy(lazy=false)
@org.hibernate.annotations.Entity(optimisticLock=OptimisticLockType.VERSION)
public class User extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4807570774011333657L;
	
	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="LOGIN_NAME", length=50)
	@Index(name="IDX_USR_LNAME")
	@NaturalId
	private String loginName;
	
	@Column(name="USER_NAME", length=50)
	private String userName;
	
	@Column(name="PASSWORD", length=50)
	private String password;
	
	@Column(name="IS_SYSTEM")
	private Boolean isSystem = false;
		
	@Column(name="IS_AVAILABLE")
	private Boolean isAvailable = true;
	
	@Column(name="IS_LOCKED")
	private Boolean isLocked = false;
	
	@Column(name="IS_ACCOUNT_NONE_EXPIRED")
	private Boolean isAccNonExpired = true;
	
	@Column(name="IS_PWD_NONE_EXPIRED")
	private Boolean isPwdNonExpired = true;
	
	@Column(name="IS_ACCOUNT_NONE_LOCKED")
	private Boolean isAccNonLocked = true;
	
	@Column(name="CREATE_TIME")
	private Date createTime;
	
	@Column(name="LATEST_UPDATE_TIME")
	private Date latestUpdateTime;
	
	@Column(name="LATEST_ACCESS_TIME")
	private Date latestAccessTime;
	
	@Version
	@Column(name="VERSION")
	private int version;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OU_ID")
	@Index(name="IDX_USR_OU")
	private OperatingUnit ou;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user")
	@OrderBy(value="id")
	private List<UserRole> userRoles = new ArrayList<UserRole>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	public Boolean getIsAccNonExpired() {
		return isAccNonExpired;
	}
	public void setIsAccNonExpired(Boolean isAccNonExpired) {
		this.isAccNonExpired = isAccNonExpired;
	}
	public Boolean getIsPwdNonExpired() {
		return isPwdNonExpired;
	}
	public void setIsPwdNonExpired(Boolean isPwdNonExpired) {
		this.isPwdNonExpired = isPwdNonExpired;
	}
	public Boolean getIsAccNonLocked() {
		return isAccNonLocked;
	}
	public void setIsAccNonLocked(Boolean isAccNonLocked) {
		this.isAccNonLocked = isAccNonLocked;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLatestUpdateTime() {
		return latestUpdateTime;
	}
	public void setLatestUpdateTime(Date latestUpdateTime) {
		this.latestUpdateTime = latestUpdateTime;
	}
	public Date getLatestAccessTime() {
		return latestAccessTime;
	}
	public void setLatestAccessTime(Date latestAccessTime) {
		this.latestAccessTime = latestAccessTime;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public OperatingUnit getOu() {
		return ou;
	}
	public void setOu(OperatingUnit ou) {
		this.ou = ou;
	}
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

}
