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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_PRIVILEGE_TYPE")
@org.hibernate.annotations.Proxy(lazy=false)
public class PrivilegeType extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9195278319372661808L;
	
	@Id @GeneratedValue(generator="tablegen", strategy=GenerationType.TABLE)
	@TableGenerator(name="tablegen", allocationSize=1, table="T_SYS_TABLE_GENERATOR")
	@Column(name="ID")
	private Long id;
	
	@Column(name="NAME", length=100)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	@Index(name="IDX_PT_PARENT")
	private PrivilegeType parent;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="parent")
	private List<PrivilegeType> children = new ArrayList<PrivilegeType>();
	
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
	public PrivilegeType getParent() {
		return parent;
	}
	public void setParent(PrivilegeType parent) {
		this.parent = parent;
	}
	public List<PrivilegeType> getChildren() {
		return children;
	}
	public void setChildren(List<PrivilegeType> children) {
		this.children = children;
	}

}
