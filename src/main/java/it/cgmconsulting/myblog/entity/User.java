package it.cgmconsulting.myblog.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Setter @Getter @NoArgsConstructor @EqualsAndHashCode
public class User extends CreationUpdate{

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(length = 20, nullable = false, unique = true)
	private String username;

	@JsonIgnore//evita di far comparire il dato all'interno del json
	@Column(nullable = false)
	private String password;

	@Column(nullable = false,unique = true)
	private String email;

	@Column(nullable = false)
	private boolean enabled=true;

	private String confirmCode;

	private LocalDate endBanOn;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="user_authorities",
			joinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")})
	private Set<Authority> authorities = new HashSet<>();

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(long id) {
		this.id=id;
	}
}

