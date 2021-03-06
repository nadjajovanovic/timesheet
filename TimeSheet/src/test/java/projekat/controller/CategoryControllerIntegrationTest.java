package projekat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import projekat.TimeSheetApplication;
import projekat.api.model.CategoryDTO;
import projekat.enums.ErrorCode;
import projekat.enums.TeamMemberRoles;
import projekat.exception.ErrorResponse;
import projekat.mapper.CategoryMapper;
import projekat.models.Category;
import projekat.repository.CategoryRepository;
import projekat.repository.TeamMemberRepository;
import projekat.util.BaseUT;
import projekat.util.ResponseReader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TimeSheetApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CategoryControllerIntegrationTest extends BaseUT{

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private static ObjectMapper objectMapper;

    private final String usernameAdmin = "adminTest";
    private final String usernameWorker = "workerTest";

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void doCleanDataBase() {
        cleanDataBase();
        cleanDataBase();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();


        registerUser(usernameAdmin, TeamMemberRoles.ROLE_ADMIN);
        registerUser(usernameWorker, TeamMemberRoles.ROLE_WORKER);
    }

    @Test
    void getAllCategories() throws Exception {
        //Arrange
        final var firstCatName = "Backend";
        final var secondCatName = "Frontend";
        saveTestCategory(firstCatName);
        saveTestCategory(secondCatName);
        testAuthFactory.loginUser(usernameWorker);

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
        testAuthFactory.loginUser(usernameAdmin);

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
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(get("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void testCreateCategory() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var category = new CategoryDTO();
        category.setName(categoryName);
        testAuthFactory.loginUser(usernameAdmin);

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
    void testCreateCategoryForbidden() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var category = new CategoryDTO();
        category.setName(categoryName);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseCategory = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseCategory.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseCategory.getErrorCode());
    }

    @Test
    void testCreateCategoryBadRequest() throws Exception {
        //Arrange
        final var category = new CategoryDTO();
        category.setName("");
        testAuthFactory.loginUser(usernameAdmin);

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
        testAuthFactory.loginUser(usernameAdmin);

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
        final var categoryName = "Backend";
        final var category = new CategoryDTO();
        category.setName(categoryName);
        category.setId(5);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateCategory() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        final var updatedName = "Backend Application";
        inserted.setCategoryname(updatedName);
        testAuthFactory.loginUser(usernameAdmin);

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
    void testUpdateCategoryForbidden() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        final var updatedName = "Backend Application";
        inserted.setCategoryname(updatedName);
        testAuthFactory.loginUser(usernameWorker);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveTestCategoryDTO(inserted, updatedName)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var responseCategory = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), responseCategory.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), responseCategory.getErrorCode());
    }

    @Test
    void testUpdateCategoryBadRequest() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        final var updatedName = " ";
        inserted.setCategoryname(updatedName);
        testAuthFactory.loginUser(usernameAdmin);

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
        final var category = new CategoryDTO();
        category.setName("Backend");
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var responseObject = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseObject.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), responseObject.getErrorCode());
    }

    @Test
    void testUpdateCategoryWrongId() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var insertedCategory = saveTestCategory(categoryName);
        insertedCategory.setCategoryid(9999);
        testAuthFactory.loginUser(usernameAdmin);

        // Act
        final var response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CategoryMapper.toCategoryDTO(insertedCategory)))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
    }

    @Test
    void deleteCategory() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    void deleteCategoryForbidden() throws Exception {
        //Arrange
        final var categoryName = "Backend";
        final var inserted = saveTestCategory(categoryName);
        testAuthFactory.loginUser(usernameWorker);

        //Act
        final var response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        final var error = ResponseReader.readResponse(response, ErrorResponse.class);
        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatusCode());
        assertEquals(ErrorCode.FORBIDDEN.toString(), error.getErrorCode());
    }

    @Test
    void deleteCategoryNotFound() throws Exception {
        //Arrange
        final var categoryId = 100;
        testAuthFactory.loginUser(usernameAdmin);

        //Act
        final var response = mvc.perform(delete("/category/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        final var error = ResponseReader.readResponse(response, ErrorResponse.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode());
        assertEquals(ErrorCode.NOT_FOUND.toString(), error.getErrorCode());
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
        teamMemberRepository.deleteAll();
        teamMemberRepository.flush();
    }
}

