package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Category{

	@Id
	@EqualsAndHashCode.Include
	@Column(length = 50)
	private String categoryName;

	private boolean visible=true;

	public Category(String categoryName) {
		this.categoryName = categoryName;
	}
}
