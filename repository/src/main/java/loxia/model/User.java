package loxia.model;
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
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OptimisticLockType;


@Entity
@Table(name="T_SYS_USER")
@org.hibernate.annotations.Proxy(lazy=false)
@org.hibernate.annotations.Entity(optimisticLock=OptimisticLockType.VERSION)
public class User extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4807570774011333657L;
		
	private Long id;
		
	private String loginName;
		
	private String userName;
		
	private String password;
		
	private Boolean isSystem = false;
			
	private Boolean isAvailable = true;
		
	private Boolean isLocked = false;
		
	private Boolean isAccNonExpired = true;
		
	private Boolean isPwdNonExpired = true;
		
	private Boolean isAccNonLocked = true;
		
	private Date createTime;
		
	private Date latestUpdateTime;
		
	private Date latestAccessTime;
		
	private int version;
		
	private OperatingUnit ou;
		
	private List<UserRole> userRoles = new ArrayList<UserRole>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)	
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="LOGIN_NAME", length=50)
	@Index(name="IDX_USR_LNAME")
	@NaturalId
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	@Column(name="USER_NAME", length=50)
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(name="PASSWORD", length=50)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="IS_SYSTEM")
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	@Column(name="IS_AVAILABLE")
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	@Column(name="IS_LOCKED")
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	@Column(name="IS_ACCOUNT_NONE_EXPIRED")
	public Boolean getIsAccNonExpired() {
		return isAccNonExpired;
	}
	public void setIsAccNonExpired(Boolean isAccNonExpired) {
		this.isAccNonExpired = isAccNonExpired;
	}
	
	@Column(name="IS_PWD_NONE_EXPIRED")
	public Boolean getIsPwdNonExpired() {
		return isPwdNonExpired;
	}
	public void setIsPwdNonExpired(Boolean isPwdNonExpired) {
		this.isPwdNonExpired = isPwdNonExpired;
	}
	
	@Column(name="IS_ACCOUNT_NONE_LOCKED")
	public Boolean getIsAccNonLocked() {
		return isAccNonLocked;
	}
	public void setIsAccNonLocked(Boolean isAccNonLocked) {
		this.isAccNonLocked = isAccNonLocked;
	}
	
	@Column(name="CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="LATEST_UPDATE_TIME")
	public Date getLatestUpdateTime() {
		return latestUpdateTime;
	}
	public void setLatestUpdateTime(Date latestUpdateTime) {
		this.latestUpdateTime = latestUpdateTime;
	}
	
	@Column(name="LATEST_ACCESS_TIME")
	public Date getLatestAccessTime() {
		return latestAccessTime;
	}
	public void setLatestAccessTime(Date latestAccessTime) {
		this.latestAccessTime = latestAccessTime;
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
	@JoinColumn(name="OU_ID")
	@Index(name="IDX_USR_OU")
	public OperatingUnit getOu() {
		return ou;
	}
	public void setOu(OperatingUnit ou) {
		this.ou = ou;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user", orphanRemoval=true)
	@OrderBy(value="id")
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

}
