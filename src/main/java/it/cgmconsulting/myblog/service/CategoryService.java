package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService{

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;

	}
	public void save(Category category){
		categoryRepository.save(category);
	}

	public Optional<Category> findById(String categoryName){

return categoryRepository.findById(categoryName);
	}

	public List<String> findAllByVisibleTrue(){
		return categoryRepository.findAllByVisibleTrue();
	}

	public List<Category>findAllByOrderByCategoryName(){
		return categoryRepository.findAllByOrderByCategoryName();
	}

	public Set<Category> findByCategoryNameInAndVisibleTrue(Set<String>categories){
		return categoryRepository.findByCategoryNameInAndVisibleTrue(categories);
	}
}
