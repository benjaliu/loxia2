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

import org.hibernate.annotations.Index;

@Entity
@Table(name="T_SYS_PRIVILEGE_TYPE")
@org.hibernate.annotations.Proxy(lazy=false)
public class PrivilegeType extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9195278319372661808L;
		
	private Long id;
		
	private String name;
		
	private PrivilegeType parent;
		
	private List<PrivilegeType> children = new ArrayList<PrivilegeType>();
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	
	@Column(name="NAME", length=100)
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	@Index(name="IDX_PT_PARENT")
	public PrivilegeType getParent() {
		return parent;
	}
	public void setParent(PrivilegeType parent) {
		this.parent = parent;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="parent", orphanRemoval=true)
	@OrderBy(value="id")
	public List<PrivilegeType> getChildren() {
		return children;
	}
	public void setChildren(List<PrivilegeType> children) {
		this.children = children;
	}

}
