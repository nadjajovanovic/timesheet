package projekat.controller;
import org.springframework.http.HttpStatus;
import projekat.TimeSheetApplication;
import projekat.models.Category;
import projekat.util.ResponseReader;
import projekat.repository.CategoryRepository;
import java.util.Arrays;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import  static  org.junit.jupiter.api.Assertions.assertEquals;
import  static  org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MockMvc;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository repository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void doCleanDataBase() {
        cleanDataBase();
    }

    @Test
    public void getAllCategories() throws Exception {
        //Arrange
        final var firstCatName = "First";
        final var secondCatName = "Second";
        createTestCategory(firstCatName);
        createTestCategory(secondCatName);

        //Act
        final var response = mvc.perform(get("/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var categories = Arrays.asList(ResponseReader.readResponse(response, Category[].class));

        //Assert
        assertEquals(categories.size(), 2);
        assertEquals(categories.get(0).getCategoryname(), firstCatName);
        assertEquals(categories.get(1).getCategoryname(), secondCatName);
    }

    @Test
    public void getOneCategory() throws Exception {
        //Arrange
        final var categoryName = "First";
        final var inserted = createTestCategory(categoryName);

        //Act
        final var response = mvc.perform(get("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var category = ResponseReader.readResponse(response, Category.class);

        //Assert
        assertEquals(category.getCategoryname(), categoryName);
        assertEquals(category.getCategoryid(), Integer.valueOf(inserted.getCategoryid()));
    }

    @Test
    public void  getOneCategoryNotFound() throws Exception {
        //Arrange
        final var categoryId = 100;

        //Act
        final var response = mvc.perform(get("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void  testCreateCategory() throws Exception {
        //Arrange
        final var categoryName = "Please insert me";
        final var category = new Category();
        category.setCategoryname(categoryName);

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), categoryName);
    }

    @Test
    public void  testCreateCategoryBadRequest() throws Exception {

        //Arrange
        final var category = new Category();
        category.setCategoryname("   ");

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateCategoryNameNotExist() throws Exception {
        //Arrange
        final var category = new Category();

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testCreateCategoryIdExists() throws Exception {
        //Arrange
        final var categoryName = "Please insert me";
        final var category = new Category();
        category.setCategoryname(categoryName);
        category.setCategoryid(5);

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void  testUpdateCategory() throws Exception {
        //Arrange
        final var categoryName = "nameForInsert";
        final var inserted = createTestCategory(categoryName);
        final var updatedName = "nameForUpdate";
        inserted.setCategoryname(updatedName);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), updatedName);
    }

    @Test
    public void  testUpdateCategoryBadRequest() throws Exception {
        //Arrange
        final var categoryName = "nameForInsert";
        final var inserted = createTestCategory(categoryName);
        final var updatedName = "   ";
        inserted.setCategoryname(updatedName);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test @Disabled
    public void  testUpdateCategoryNoId() throws Exception {
        //Arrange
        final var category = new Category();
        category.setCategoryname("Not important");

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteCategory() throws Exception {
        //Arrange
        final var categoryName = "Delete Me";
        final var inserted = createTestCategory(categoryName);

        //Act
        final var response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void deleteCategoryNotFound() throws Exception {
        //Arrange
        final var categoryId = 100;

        //Act
        final var response = mvc.perform(delete("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }

    private Category createTestCategory(String categoryName) {
        final var category = new Category();
        category.setCategoryname(categoryName);
        return repository.saveAndFlush(category);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}

