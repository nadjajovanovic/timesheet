package projekat.controller;
import projekat.TimeSheetApplication;
import projekat.models.Category;
import projekat.util.ResponseReader;
import projekat.repository.CategoryRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import  static  org.junit.jupiter.api.Assertions.assertEquals;
import  static  org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MvcResult;
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
        final String firstCatName = "First";
        final String secondCatName = "Second";
        createTestCategory(firstCatName);
        createTestCategory(secondCatName);

        //Act
        final MvcResult response = mvc.perform(get("/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final List<Category> categories = Arrays.asList(ResponseReader.readResponse(response, Category[].class));

        //Assert
        assertEquals(categories.size(), 2);
        assertEquals(categories.get(0).getCategoryname(), firstCatName);
        assertEquals(categories.get(1).getCategoryname(), secondCatName);
    }

    @Test
    public void getOneCategory() throws Exception {
        //Arrange
        final String categoryName = "First";
        final Category inserted = createTestCategory(categoryName);

        //Act
        final MvcResult response = mvc.perform(get("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final Category category = ResponseReader.readResponse(response, Category.class);

        //Assert
        assertEquals(category.getCategoryname(), categoryName);
        assertEquals(category.getCategoryid(), Integer.valueOf(inserted.getCategoryid()));
    }

    @Test
    public void  getOneCategoryNotFound() throws Exception {
        //Arrange

        //Act
        final MvcResult response = mvc.perform(get("/category/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    public void  testCreateCategory() throws Exception {
        //Arange
        final String categoryName = "Please insert me";
        final Category category = new Category();
        category.setCategoryname(categoryName);

        // Act
        final MvcResult response = mvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        final Category responseCategory = ResponseReader.readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), categoryName);
    }

    @Test
    public void  testCreateCategoryBadRequest() throws Exception {

        //Arange
        final Category category = new Category();
        category.setCategoryname("   ");

        // Act
        final MvcResult response = mvc.perform(post("/category")
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
        final Category category = new Category();

        // Act
        final MvcResult response = mvc.perform(post("/category")
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
        final String categoryName = "Please insert me";
        final Category category = new Category();
        category.setCategoryname(categoryName);
        category.setCategoryid(5);

        // Act
        final MvcResult response = mvc.perform(post("/category")
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
        final String categoryName = "nameForInsert";
        final Category inserted = createTestCategory(categoryName);
        final String updatedName = "nameForUpdate";
        inserted.setCategoryname(updatedName);

        // Act
        final MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final Category responseCategory = ResponseReader.readResponse(response, Category.class);

        // Assert
        assertNotNull(responseCategory.getCategoryid());
        assertEquals(responseCategory.getCategoryname(), updatedName);
    }

    @Test
    public void  testUpdateCategoryBadRequest() throws Exception {
        //Arange
        final String categoryName = "nameForInsert";
        final Category inserted = createTestCategory(categoryName);
        final String updatedName = "   ";
        inserted.setCategoryname(updatedName);

        // Act
        final MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inserted))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test @Disabled
    public void  testUpdateCategoryNoId() throws Exception {
        //Arange
        final Category category = new Category();
        category.setCategoryname("Not important");

        // Act
        final MvcResult response = mvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(response.getResponse().getStatus(), 400);
    }

    @Test
    public void deleteCategory() throws Exception {
        //Arrange
        final String categoryName = "Delete Me";
        final Category inserted = createTestCategory(categoryName);

        //Act
        final MvcResult response = mvc.perform(delete("/category/{id}", inserted.getCategoryid())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 200);
    }

    @Test
    public void deleteCategoryNotFound() throws Exception {
        //Arrange

        //Act
        final MvcResult response = mvc.perform(delete("/category/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //Assert
        assertEquals(response.getResponse().getStatus(), 404);
    }

    private Category createTestCategory(String categoryName) {
        final Category category = new Category();
        category.setCategoryname(categoryName);
        return repository.saveAndFlush(category);
    }

    private void cleanDataBase() {
        repository.deleteAll();
        repository.flush();
    }
}

