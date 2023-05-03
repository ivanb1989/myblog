package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.Creation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter@Setter
@EqualsAndHashCode@NoArgsConstructor
public class Comment extends Creation{


	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)//non può essere nulla= NOT NULL di sql
	private String comment;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="post_id",nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author",nullable = false)
	private User author;

	private boolean censored=false;

	public Comment(String comment, Post postId, User author) {
		this.comment = comment;
		this.post = postId;
		this.author = author;
	}
}
