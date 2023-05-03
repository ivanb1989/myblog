package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category,String>
{
//sql nativo
@Query(value="SELECT category_name FROM category WHERE visible=true",nativeQuery = true)
	List<String> findAllByVisibleTrue();



List<Category>findAllByOrderByCategoryName();


Set<Category>findByCategoryNameInAndVisibleTrue(Set<String>categories);

}
