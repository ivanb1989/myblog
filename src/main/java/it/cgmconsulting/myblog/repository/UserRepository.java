package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{

	boolean existsByUsernameOrEmail(String username,String email);

	Optional<User> findByConfirmCode(String confirmCode);

	Optional<User> findByUsernameOrEmail(String username, String email);

	//query di update
	@Transactional
	@Modifying// serve per le INSERT e UPDATE
	@Query(value ="UPDATE User u SET u.password = :newPassword, u.updatedAt = :now WHERE u.id= :id")//JPQL
	void updatePassword(@Param("id")long id,
						@Param("newPassword") String newPassword,
	                    @Param("now") LocalDateTime now);

	@Transactional
	@Modifying //Serve per scrivere query con INSERT e UPDATE
	@Query(value="UPDATE User u SET u.username = :newUsername, u.updatedAt= :now WHERE u.id= :id")
	void updateUsername(@Param("id") long id, @Param("newUsername") String newUsername, @Param("now")LocalDateTime now);
	boolean existsByUsernameAndIdNot(String username,Long id);


	@Query(value ="SELECT COUNT(u) FROM User u " +
	"inner join u.authorities a ON a.authorityName = 'ROLE_MODERATOR' " +
	"WHERE u.id = :to")
	int getModeratorsToReassignement(long to);


}
