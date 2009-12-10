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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
	
	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="CODE", length=50)
	@Index(name="IDX_OPU_CODE")
	private String code;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="FULL_NAME")
	private String fullName;
	
	@Column(name="IS_AVAILABLE")
	private Boolean isAvailable = true;
	
	@Version
	@Column(name="VERSION")
	private int version;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TYPE_ID")
	@Index(name="IDX_OPU_TYPE")
	private OperatingUnitType type;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	@Index(name="IDX_OPU_PARENT")
	private OperatingUnit parentUnit;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="parentUnit")
	@OrderBy(value="ID")
	private List<OperatingUnit> childrenUnit = new ArrayList<OperatingUnit>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public OperatingUnitType getType() {
		return type;
	}
	public void setType(OperatingUnitType type) {
		this.type = type;
	}
	public OperatingUnit getParentUnit() {
		return parentUnit;
	}
	public void setParentUnit(OperatingUnit parentUnit) {
		this.parentUnit = parentUnit;
	}
	public List<OperatingUnit> getChildrenUnit() {
		return childrenUnit;
	}
	public void setChildrenUnit(List<OperatingUnit> childrenUnit) {
		this.childrenUnit = childrenUnit;
	}

}
