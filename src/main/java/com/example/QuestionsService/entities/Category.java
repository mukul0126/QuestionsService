package com.example.QuestionsService.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "Category")
public class Category {
   @Id
    String categoryId;
//    String categoryName;
//    List<String> tags;
}
