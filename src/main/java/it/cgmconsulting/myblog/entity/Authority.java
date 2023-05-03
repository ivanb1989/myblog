package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Setter @Getter @NoArgsConstructor @EqualsAndHashCode
public class Authority{

@Id
@EqualsAndHashCode.Include
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;

	@Column(length = 30,nullable = false)
	private String authorityName;

	public Authority(String authorityName){
		this.authorityName=authorityName;
	}
}
