package projekat.controller;

import projekat.TimeSheetApplication;
import projekat.models.Category;
import projekat.repository.CategoryRepository;

import org.junit.Test;
import java.io.IOException;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository repository;

    @Test
    public void getAllCategories() throws IOException, Exception {

        //Arrange
        String firstCatName = "First";
        String secondCatName = "Second";

        this.createTestCategory(firstCatName);
        this.createTestCategory(secondCatName);

        //Act & Assert

        mvc.perform(get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].categoryname", is(firstCatName)))
                        .andExpect(jsonPath("$[1].categoryname", is(secondCatName)))
        ;

    }

    private void createTestCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryname(categoryName);
        repository.saveAndFlush(category);
    }
}