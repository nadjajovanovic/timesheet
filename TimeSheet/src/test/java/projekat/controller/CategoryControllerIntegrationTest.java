package projekat.controller;
import org.springframework.http.HttpStatus;
import projekat.TimeSheetApplication;
import projekat.models.Category;
import projekat.util.ResponseReader;
import projekat.repository.CategoryRepository;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MockMvc;
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
    void doCleanDataBase() {
        cleanDataBase();
    }

    @Test
    void getAllCategories() throws Exception {
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
        assertEquals(2, categories.size());
        assertEquals(firstCatName, categories.get(0).getCategoryname());
        assertEquals(secondCatName, categories.get(1).getCategoryname());
    }

    @Test
    void getOneCategory() throws Exception {
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
        assertEquals(categoryName, category.getCategoryname());
        assertEquals(inserted.getCategoryid(), category.getCategoryid());
    }

    @Test
    void getOneCategoryNotFound() throws Exception {
        //Arrange
        final var categoryId = 100;

        //Act
        final var response = mvc.perform(get("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateCategory() throws Exception {
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
        assertEquals(categoryName, responseCategory.getCategoryname());
    }

    @Test
    void testCreateCategoryBadRequest() throws Exception {
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateCategoryNameNotExist() throws Exception {
        //Arrange
        final var category = new Category();

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testCreateCategoryIdExists() throws Exception {
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateCategory() throws Exception {
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
        assertEquals(updatedName, responseCategory.getCategoryname());
    }

    @Test
    void testUpdateCategoryBadRequest() throws Exception {
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateCategoryNoId() throws Exception {
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test
    void testUpdateCategoryWrongId() throws Exception {
        //Arrange
        final var categoryName = "My Category";
        final var insertedCategory = createTestCategory(categoryName);
        insertedCategory.setCategoryid(9999);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedCategory))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCategory() throws Exception {
        //Arrange
        final var categoryName = "Delete Me";
        final var inserted = createTestCategory(categoryName);

        //Act
        final var response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCategoryNotFound() throws Exception {
        //Arrange
        final var categoryId = 100;

        //Act
        final var response = mvc.perform(delete("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
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

