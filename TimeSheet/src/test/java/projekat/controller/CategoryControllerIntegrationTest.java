package projekat.controller;

import org.junit.Before;
import projekat.models.Category;
import projekat.repository.CategoryRepository;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import lombok.SneakyThrows;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository repository;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllCategories() throws Exception {

        //Arrange
        this.cleanDataBase();
        String firstCatName = "First";
        String secondCatName = "Second";
        this.createTestCategory(firstCatName);
        this.createTestCategory(secondCatName);

        //Act
        MvcResult response = mvc.perform(get("/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Category> categories = Arrays.asList(readResponse(response, Category[].class));

        //Assert
        assertEquals(categories.size(), 2);
        assertEquals(categories.get(0).getCategoryname(), firstCatName);
        assertEquals(categories.get(1).getCategoryname(), secondCatName);
    }

    @Test
    public void getOneCategory() throws Exception {

        //Arrange
        this.cleanDataBase();
        String categoryName = "First";
        Category inserted = this.createTestCategory(categoryName);

        //Act
        MvcResult response = mvc.perform(get("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Category category = readResponse(response, Category.class);

        //Assert
        assertEquals(category.getCategoryname(), categoryName);
        assertEquals(category.getCategoryid(), Integer.valueOf(inserted.getCategoryid()));
    }

    @Test
    public void  getOneCategoryNotFound() throws Exception {
        //Arrange
        this.cleanDataBase();

        //Act
        MvcResult response = mvc.perform(get("/category/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    public void  testCreateCategory() throws Exception {

        //Arange
        this.cleanDataBase();
        String categoryName = "Please insert me";
        Category category = new Category();
        category.setCategoryname(categoryName);

        // Act
        MvcResult response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Category responseCategory = readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), categoryName);
    }

    @Test
    public void  testCreateCategoryBadRequest() throws Exception {

        //Arange
        this.cleanDataBase();
        Category category = new Category();
        category.setCategoryname("   ");

        // Act
        MvcResult response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testCreateCategoryNameNotExist() throws Exception {

        //Arange
        this.cleanDataBase();
        Category category = new Category();

        // Act
        MvcResult response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testCreateCategoryIdExists() throws Exception {

        //Arange
        this.cleanDataBase();
        String categoryName = "Please insert me";
        Category category = new Category();
        category.setCategoryname(categoryName);
        category.setCategoryid(5);

        // Act
        MvcResult response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void  testUpdateCategory() throws Exception {

        //Arange
        this.cleanDataBase();
        String categoryName = "nameForInsert";
        Category inserted = this.createTestCategory(categoryName);
        String updatedName = "nameForUpdate";
        inserted.setCategoryname(updatedName);

        // Act
        MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Category responseCategory = readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), updatedName);
    }

    @Test
    public void  testUpdateCategoryBadRequest() throws Exception {

        //Arange
        this.cleanDataBase();
        String categoryName = "nameForInsert";
        Category inserted = this.createTestCategory(categoryName);
        String updatedName = "   ";
        inserted.setCategoryname(updatedName);

        // Act
        MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }
/*
    @Test
    public void  testUpdateCategoryNoId() throws Exception {

        //Arange
        this.cleanDataBase();

        Category category = new Category();
        category.setCategoryname("Not important");

        // Act
        MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }
*/
    @Test
    public void deleteCategory() throws Exception {
        //Arrange
        this.cleanDataBase();
        String categoryName = "Delete Me";
        Category inserted = this.createTestCategory(categoryName);

        //Act
        MvcResult response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 200);

    }

    @Test
    public void deleteCategoryNotFound() throws Exception {
        //Arrange
        this.cleanDataBase();

        //Act
        MvcResult response = mvc.perform(delete("/category/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);

    }

    private Category createTestCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryname(categoryName);
        return repository.saveAndFlush(category);
    }

    @SneakyThrows
    protected <T> T readResponse(MvcResult result, Class<T> clazz){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = result.getResponse().getContentAsString();

        return objectMapper.readValue(json, clazz);
    }

    private void cleanDataBase() {
        this.repository.deleteAll();
        repository.flush();
    }
}

