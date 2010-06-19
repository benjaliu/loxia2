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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OptimisticLockType;

@Entity
@Table(name="T_SYS_OPERATING_UNIT")
@org.hibernate.annotations.Proxy(lazy=false)
@org.hibernate.annotations.Entity(optimisticLock=OptimisticLockType.VERSION)
public class OperatingUnit extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8842330664431987968L;
		
	private Long id;
		
	private String code;
		
	private String name;
		
	private String fullName;
		
	private Boolean isAvailable = true;
		
	private int version;
		
	private OperatingUnitType type;
		
	private OperatingUnit parentUnit;
		
	private List<OperatingUnit> childrenUnit = new ArrayList<OperatingUnit>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="CODE", length=50)
	@Index(name="IDX_OPU_CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="FULL_NAME")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Column(name="IS_AVAILABLE")
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
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
	@JoinColumn(name="TYPE_ID")
	@Index(name="IDX_OPU_TYPE")
	public OperatingUnitType getType() {
		return type;
	}
	public void setType(OperatingUnitType type) {
		this.type = type;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	@Index(name="IDX_OPU_PARENT")
	public OperatingUnit getParentUnit() {
		return parentUnit;
	}
	public void setParentUnit(OperatingUnit parentUnit) {
		this.parentUnit = parentUnit;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="parentUnit", orphanRemoval=true)
	@OrderBy(value="id")
	public List<OperatingUnit> getChildrenUnit() {
		return childrenUnit;
	}
	public void setChildrenUnit(List<OperatingUnit> childrenUnit) {
		this.childrenUnit = childrenUnit;
	}

}
