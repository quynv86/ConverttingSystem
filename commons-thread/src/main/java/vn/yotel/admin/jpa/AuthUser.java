package vn.yotel.admin.jpa;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the auth_user database table.
 * 
 */
@Entity
@Table(name="auth_user")
@NamedQuery(name="AuthUser.findAll", query="SELECT u FROM AuthUser u")
public class AuthUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date")
	private Date createdDate;

	private String email;

	@Column(name="first_name")
	private String firstName;

	@Column(name="full_name")
	private String fullName;

	private String gender;

	@Column(name="is_verified")
	private byte isVerified;

	@Column(name="last_name")
	private String lastName;

	@Column(name="middle_name")
	private String middleName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date")
	private Date modifiedDate;

	private String password;

	private String salt;

	private byte status;
	
	@Column(name="user_type")
	private byte userType;

	@Column(name="user_name")
	private String userName;
	
	//bi-directional many-to-many association to Role
	@ManyToMany(mappedBy="authUsers")
	private List<Role> authRoles = new ArrayList<Role>();

	//bi-directional many-to-one association to UserMeta
	@OneToMany(mappedBy="authUser")
	private List<UserMeta> authUsermetas = new ArrayList<UserMeta>();

	public AuthUser() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public byte getIsVerified() {
		return this.isVerified;
	}

	public void setIsVerified(byte isVerified) {
		this.isVerified = isVerified;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public List<Role> getAuthRoles() {
		return authRoles;
	}

	public void setAuthRoles(List<Role> authRoles) {
		this.authRoles = authRoles;
	}

	public List<UserMeta> getAuthUsermetas() {
		return this.authUsermetas;
	}

	public void setAuthUsermetas(List<UserMeta> authUsermetas) {
		this.authUsermetas = authUsermetas;
	}

	public UserMeta addAuthUsermeta(UserMeta authUsermeta) {
		getAuthUsermetas().add(authUsermeta);
		authUsermeta.setAuthUser(this);

		return authUsermeta;
	}

	public UserMeta removeAuthUsermeta(UserMeta authUsermeta) {
		getAuthUsermetas().remove(authUsermeta);
		authUsermeta.setAuthUser(null);

		return authUsermeta;
	}

	public byte getUserType() {
		return userType;
	}

	public void setUserType(byte userType) {
		this.userType = userType;
	}


}