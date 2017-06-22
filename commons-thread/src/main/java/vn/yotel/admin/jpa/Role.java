package vn.yotel.admin.jpa;

import java.io.Serializable;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the auth_role database table.
 * 
 */
@Entity
@Table(name="auth_role")
@NamedQuery(name="Role.findAll", query="SELECT r FROM Role r")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String name;

	//bi-directional many-to-many association to Permission
	@ManyToMany(mappedBy="authRoles")
	private List<Permission> authPerms = new ArrayList<Permission>();

	//bi-directional many-to-many association to User
	@ManyToMany
	@JoinTable(name = "auth_user_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<AuthUser> authUsers = new ArrayList<AuthUser>();

	public Role() {
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

	public List<Permission> getAuthPerms() {
		return this.authPerms;
	}

	public void setAuthPerms(List<Permission> authPerms) {
		this.authPerms = authPerms;
	}

	public List<AuthUser> getAuthUsers() {
		return this.authUsers;
	}

	public void setAuthUsers(List<AuthUser> authUsers) {
		this.authUsers = authUsers;
	}

}