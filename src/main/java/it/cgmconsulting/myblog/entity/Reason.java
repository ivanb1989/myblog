package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Reason{

	@Id
	@EqualsAndHashCode.Include
	@Column(length = 30)
	private String id;


}
