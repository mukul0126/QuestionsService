package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.requestdto.CategoryDto;
import com.example.QuestionsService.entities.Category;
import com.example.QuestionsService.repositories.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Category createCategory(CategoryDto categoryDto){
        Category category =new Category();
        BeanUtils.copyProperties(categoryDto,category);
       return categoryRepository.save(category);
    }
}
