package it.cgmconsulting.myblog.entity.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class CreationUpdate extends Creation  implements Serializable{

	@UpdateTimestamp
	private LocalDateTime updatedAt;

}
