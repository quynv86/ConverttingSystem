package vn.yotel.admin.jpa;

import java.io.Serializable;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the auth_perm database table.
 * 
 */
@Entity
@Table(name="auth_perm")
@NamedQuery(name="Permission.findAll", query="SELECT p FROM Permission p")
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String name;

	//bi-directional many-to-many association to Role
	@ManyToMany
	@JoinTable(name = "auth_role_perm", joinColumns = { @JoinColumn(name = "perm_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private List<Role> authRoles = new ArrayList<Role>();

	public Permission() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Role> getAuthRoles() {
		return this.authRoles;
	}

	public void setAuthRoles(List<Role> authRoles) {
		this.authRoles = authRoles;
	}

}