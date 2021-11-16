package projekat.controller;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.HttpStatus;
import projekat.TimeSheetApplication;
import projekat.api.model.CategoryDTO;
import projekat.mapper.CategoryMapper;
import projekat.models.Category;
import projekat.util.BaseUT;
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
class CategoryControllerIntegrationTest extends BaseUT{

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
        final var firstCatName = "Backend";
        final var secondCatName = "Frontend";
        saveTestCategory(firstCatName);
        saveTestCategory(secondCatName);

        //Act
        final var response = mvc.perform(get("/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var categories = Arrays.asList(ResponseReader.readResponse(response, CategoryDTO[].class));

        //Assert
        assertEquals(2, categories.size());
        assertEquals(firstCatName, categories.get(0).getName());
        assertEquals(secondCatName, categories.get(1).getName());
    }

    @Test
    void getOneCategory() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);

        //Act
        final var response = mvc.perform(get("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var category = ResponseReader.readResponse(response, CategoryDTO.class);

        //Assert
        assertEquals(categoryName, category.getName());
        assertEquals(inserted.getCategoryid(), category.getId());
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
        final var categoryName = "Backend";
        final var category = new CategoryDTO();
        category.setName(categoryName);

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, CategoryDTO.class);

        // Assert
        assertNotNull(responseCategory.getId());
        assertEquals(categoryName, responseCategory.getName());
    }

    @Test
    void testCreateCategoryBadRequest() throws Exception {
        //Arrange
        final var category = new CategoryDTO();
        category.setName("");

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
        final var category = new CategoryDTO();

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
    }

    @Test @Disabled
    void testCreateCategoryIdExists() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var category = new CategoryDTO();
        category.setName(categoryName);
        category.setId(5);

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
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        final var updatedName = "Backend Application";
        inserted.setCategoryname(updatedName);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTestCategoryDTO(inserted, updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, CategoryDTO.class);

        // Assert
        assertNotNull(responseCategory.getId());
        assertEquals(updatedName, responseCategory.getName());
    }

    @Test
    void testUpdateCategoryBadRequest() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        final var updatedName = " ";
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
        category.setCategoryname("Backend");

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
        final var categoryName = "Backend";
        final var insertedCategory = saveTestCategory(categoryName);
        insertedCategory.setCategoryid(9999);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CategoryMapper.toCategoryDTO(insertedCategory)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCategory() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);

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

    private Category saveTestCategory(String categoryName) {
        final var category = createTestCategory(categoryName);
        return repository.saveAndFlush(category);
    }

    private CategoryDTO saveTestCategoryDTO(Category c, String categoryName) {
        final var category = new CategoryDTO();
        category.setId(c.getCategoryid());
        category.setName(categoryName);
        return category;
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}

