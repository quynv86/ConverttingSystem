package vn.yotel.admin.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the auth_usermeta database table.
 * 
 */
@Entity
@Table(name="auth_usermeta")
@NamedQuery(name="UserMeta.findAll", query="SELECT u FROM UserMeta u")
public class UserMeta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="meta_key")
	private String metaKey;

	@Lob
	@Column(name="meta_value")
	private String metaValue;

	//bi-directional many-to-one association to User
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private AuthUser authUser;

	public UserMeta() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetaKey() {
		return this.metaKey;
	}

	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}

	public String getMetaValue() {
		return this.metaValue;
	}

	public void setMetaValue(String metaValue) {
		this.metaValue = metaValue;
	}

	public AuthUser getAuthUser() {
		return this.authUser;
	}

	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}

}