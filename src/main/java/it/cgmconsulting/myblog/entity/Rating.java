package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Check(constraints = "rate > 0 AND rate < 6")
public class Rating{

	@EmbeddedId
	@EqualsAndHashCode.Include
private RatingId ratingId;

	private byte rate;

}
