package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.repository.RatingIdRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService{

	private final RatingIdRepository ratingIdRepository;
	public RatingService(RatingIdRepository ratingIdRepository) {
		this.ratingIdRepository = ratingIdRepository;
	}

	public void save(Rating rating){
		ratingIdRepository.save(rating);
	}



}
