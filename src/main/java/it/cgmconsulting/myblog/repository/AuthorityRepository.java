package it.cgmconsulting.myblog.repository;
import it.cgmconsulting.myblog.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;


public interface AuthorityRepository extends JpaRepository<Authority,Long>{

	Optional<Authority>findByAuthorityName(String authorityName);

	Set<Authority> findByAuthorityNameIn(Set<String> authorities);//select * from authority where authority_name IN (lore_reader,role_writer,ect

}
