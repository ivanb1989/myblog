package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@Embeddable@Getter@Setter
@EqualsAndHashCode@AllArgsConstructor@NoArgsConstructor
public class RatingId implements Serializable{


	@EqualsAndHashCode.Include
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;


	@EqualsAndHashCode.Include
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;


}
