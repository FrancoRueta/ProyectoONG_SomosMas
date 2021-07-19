package com.restteam.ong.services.impl;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restteam.ong.controllers.dto.NewsDTO;
import com.restteam.ong.models.Categories;
import com.restteam.ong.models.News;
import com.restteam.ong.repositories.NewsRepository;
import com.restteam.ong.services.CategoriesService;
import com.restteam.ong.services.NewsService;

import java.util.Date;

@Service

public class NewsServicelmpl implements NewsService {
	@Autowired
	NewsRepository newsRepository;

	@Autowired
	CategoriesService categoriesService;
	ModelMapper modelMapper = new ModelMapper();

	@Override
	public News postNews(NewsDTO newsDTO) {
		News news = new News();
		Categories categories;
		if(!categoriesService.existCategoryByName(newsDTO.getCategoryRequest().getName())){
			categories = categoriesService.postCategory(newsDTO.getCategoryRequest());
		}else{
			categories = categoriesService.getCategoriesByName(newsDTO.getCategoryRequest().getName());
		}

		modelMapper.map(newsDTO, news);
		news.setRegDate(new Date().getTime() / 1000);
		news.setUpDateDate(news.getRegDate());
		news.setCategories(categories);
		return newsRepository.save(news);

	}

	@Override
	public String deleteNewsById(Long id) {
		try {
			newsRepository.deleteById(id);
			return "La noticia se elimino con la id : " + id;
		} catch (Exception error) {
			return "No existe la noticia con esa id, ingrese otro";
		}
	}

	@Transactional
	public News updateNews(NewsDTO newsDTO, Long id) {
		var newsToUpdate = getNewsById(id);
		var categorie = categoriesService.getCategoriesByName(newsDTO.getCategoryRequest().getName());
		newsToUpdate.setCategories(categorie);

		if (newsDTO.getContent() != null && !newsDTO.getContent().isBlank()) {
			newsToUpdate.setContent(newsDTO.getContent());
		}
		if (newsDTO.getName() != null && !newsDTO.getName().isBlank()) {
			newsToUpdate.setName(newsDTO.getName());
		}
		if (newsDTO.getImage() != null && !newsDTO.getImage().isBlank()) {
			newsToUpdate.setImage(newsDTO.getImage());
		}
		// Se agrega la ultima vez que fue actualizado.
		newsToUpdate.setUpDateDate(System.currentTimeMillis() / 1000);
		return newsToUpdate;
	}

	@Override
	public News getNewsByName(String name) {
		return newsRepository.findByName(name).orElseThrow(() -> new IllegalStateException(String.format("News with name: %s not found.", name)));
	}

	@Override
	public Boolean existId(Long id) {
		return newsRepository.existsById(id);
	}

	@Override
	public News getNewsById(Long id) {
		return newsRepository.findById(id).orElseThrow(() -> new IllegalStateException(String.format("News with ID %n doesn't exist %s", id)));
	}

	@Override
	public NewsDTO getNewsDTO(Long id) {
		var newsDTO = new NewsDTO();
		try {
			var news = this.getNewsById(id);
			newsDTO.setName(news.getName());
			newsDTO.setImage(news.getImage());
			newsDTO.setContent(news.getContent());
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
		return newsDTO;
	}

	@Override
	public News findByNameOrElseCreateNewNews(NewsDTO newsDTO) {
		var modelMapper = new ModelMapper();
		var news = newsRepository.findByName(newsDTO.getName()).orElse(modelMapper.map(newsDTO, News.class));
		news.setCategories(modelMapper.map(newsDTO.getCategoryRequest(), Categories.class));
		return news;
	}
}
