package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.payload.response.PostDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

	boolean existsByTitle(String title);
	boolean existsByTitleAndIdNot(String title, long id);
	Optional<Post> findByIdAndPublishedTrue(long id);

	@Query(value="SELECT new it.cgmconsulting.myblog.payload.response.PostDetailResponse(" +
			"p.id, " +
			"p.title, " +
			"p.content, " +
			"p.image, " +
			"p.author.username, " +
			"(SELECT COALESCE(ROUND(AVG(r.rate),1), 0.0) FROM Rating r WHERE r.ratingId.post.id = :id) AS avg" +
			") FROM Post p " +
			"WHERE p.id= :id AND p.published=true")
	PostDetailResponse getByIdAndPublishedTrue(long id);

	@Query(value="SELECT new it.cgmconsulting.myblog.payload.response.PostBoxesResponse("+
			"p.id, " +
			"p.title, " +
			"p.overview," +
			"p.image) " +
			"FROM Post p " +
			"WHERE p.published = true " +
			"ORDER BY p.updatedAt DESC" )
	List<PostBoxesResponse> getPostBoxes();

	@Query("SELECT cat.categoryName " +
			"FROM Post p " +
			"LEFT JOIN p.categories cat " +
			"WHERE p.id = :id AND cat.visible=true")
	Set<String> getCategoriesByPostId(@Param("id") long id);
}
