package com.jtspringproject.JtSpringProject;

import com.jtspringproject.JtSpringProject.controller.AdminController;
import com.jtspringproject.JtSpringProject.dao.productDao;
import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;
    @Mock
    private categoryService categoryService;
    @Mock
    private Model mockModel;
    private userService mockUserService;
    private RedirectAttributes mockRedirectAttributes;
    @Mock
    private productService productService;
    productDao productDaoMock;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.initMocks(this);
        mockUserService = mock(userService.class);
        mockRedirectAttributes = mock(RedirectAttributes.class);
        adminController = new AdminController();
        productService=new productService();
        adminController.setUserService(mockUserService);
        adminController.setCategoryService(categoryService);
        adminController.setProductService(productService);
        productDaoMock= Mockito.mock(productDao.class);
        productService.setProductDao(productDaoMock);

    }
    @Test
    void returnIndex() {
        adminController.adminlogcheck = 1;
        adminController.usernameforclass = "testUser";
        String result = adminController.returnIndex();
        Assertions.assertEquals("userLogin", result);
        Assertions.assertEquals(0, adminController.adminlogcheck);
        Assertions.assertEquals("", adminController.usernameforclass);
    }

    @Test
    public void testIndex_WhenUsernameIsEmpty_ReturnsUserLogin() {
        adminController.setUsernameforclass(""); // Set usernameforclass to empty string

        String result = adminController.index(mockModel);

        Assertions.assertEquals("userLogin", result);
    }

    @Test
    public void testIndex_WhenUsernameIsNotEmpty_ReturnsAdminHome() {
        String username = "testUser";
        adminController.setUsernameforclass(username);
        when(mockModel.addAttribute("username", username)).thenReturn(mockModel);

        String result = adminController.index(mockModel);

        Assertions.assertEquals("adminHome", result);
    }

    @Test
    void adminlogin() {
        String result = adminController.adminlogin(mockModel);

        Assertions.assertEquals("adminlogin", result); // check if it returns the correct view

        verify(mockModel).addAttribute("message", "Tested By Quality Control Force");
        // Verify if the 'message' is added to the model with the given value
    }

    @Test
    void testAdminHome_WhenAdminLogged() {
        adminController.adminlogcheck = 1;

        String result = adminController.adminHome(mockModel);

        Assertions.assertEquals("adminHome", result);
        // You can also verify that no interactions happen with the mockModel in this case
        verifyNoInteractions(mockModel);
    }

    @Test
    void testAdminHome_WhenAdminNotLogged() {
        adminController.adminlogcheck = 0;

        String result = adminController.adminHome(mockModel);

        Assertions.assertEquals("redirect:/admin/login", result);
        // You can also verify that no interactions happen with the mockModel in this case
        verifyNoInteractions(mockModel);
    }

    @Test
    public void testAdminLogin_InvalidCredentials() {
        userService mockUserService = mock(userService.class);
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);

        // Stubbing the userService behavior
        when(mockUserService.checkLogin(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(null);

        // Create the controller and invoke the method
        AdminController adminController = new AdminController();
        adminController.setUserService(mockUserService);

        ModelAndView result = adminController.adminlogin("Username", "Password", mockRedirectAttributes);

        // Verify behavior
        verify(mockRedirectAttributes).addFlashAttribute(ArgumentMatchers.eq("error"), ArgumentMatchers.anyString());
        assertEquals("redirect:/admin/login", result.getViewName());
    }

    @Test
    public void testValidAdminLogin() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("password123");
        user.setRole("ROLE_ADMIN");

        when(mockUserService.checkLogin("admin", "123")).thenReturn(user);
        ModelAndView modelAndView = adminController.adminlogin("admin", "123", mockRedirectAttributes);

        assertEquals("adminHome", modelAndView.getViewName());
        assertEquals(1, adminController.adminlogcheck);
        assertEquals(user, modelAndView.getModel().get("admin"));
    }

    @Test
    void testGetCategory_AdminNotLoggedIn() {
        adminController.adminlogcheck = 0;

        ModelAndView result = adminController.getcategory();

        assertNotNull(result);
        assertEquals("adminlogin", result.getViewName());
        verify(categoryService, never()).getCategories();
    }

    @Test
    void testGetCategory_AdminLoggedIn() {
        adminController.setCategoryService(categoryService);
        adminController.adminlogcheck = 1;
        List<Category> mockCategories =  new ArrayList<>();
        // Add some mock categories if needed
        // ...

        when(categoryService.getCategories()).thenReturn(mockCategories);

        ModelAndView result = adminController.getcategory();

        assertNotNull(result);
        assertEquals("categories", result.getViewName());
        verify(categoryService, times(1)).getCategories();
        assertEquals(mockCategories, result.getModel().get("categories"));
    }



    @Test
    void testAddCategory() {
        // Mocking category name and service behavior
        String categoryName = "TestCategory";
        adminController.setCategoryService(categoryService);

        // Mocking RedirectAttributes
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        when(categoryService.addCategory(categoryName)).thenReturn(new Category());

        // Invoking the controller method
        String result = adminController.addCategory(categoryName, redirectAttributes);

        // Verifying the behavior
        assertEquals("redirect:/admin/categories", result);


    }


    @Test
    void removeCategoryDb() {
        int categoryId = 10;

        String result = adminController.removeCategoryDb(categoryId);

        assertEquals("redirect:/admin/categories", result);
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }

    @Test
    void testUpdateCategory() {
        // Mocking category ID and name
        int categoryId = 10;
        String categoryName = "UpdatedCategory";
        // Invoking the controller method
        String result = adminController.updateCategory(categoryId, categoryName);

        // Verifying the behavior
        assertEquals("redirect:/admin/categories", result);
        verify(categoryService, times(1)).updateCategory(eq(categoryId), eq(categoryName.trim()));
    }


    @Test
    void testGetProduct_AdminNotLoggedIn() {

        adminController.adminlogcheck = 0;

        // Invoking the controller method
        ModelAndView result = adminController.getproduct();

        // Verifying the behavior
        assertEquals("adminlogin", result.getViewName());
    }

    @Test
    void testGetProduct_AdminLoggedInWithProducts() {
        // Set adminlogcheck to 1 (admin logged in)
        adminController.adminlogcheck = 1;

        Product p1=new Product();
        p1.setName("Product1");
        p1.setPrice(10);
        Product p2=new Product();
        p2.setName("Product2");
        p2.setPrice(15);
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(p1);
        mockProducts.add(p2);


        when(productDaoMock.getProducts()).thenReturn(mockProducts);

        ModelAndView result = adminController.getproduct();

        assertEquals("products", result.getViewName());
        assertTrue(result.getModel().containsKey("products"));
        assertEquals(mockProducts, result.getModel().get("products"));
    }

    @Test
    void testaddProduct() {
        int categoryId = 1;
        String name = "Test Product";
        int price = 50;
        int weight = 100;
        int quantity = 10;
        String description = "Test description";
        String productImage = "image.jpg";

        Category category = new Category();
        category.setId(categoryId);

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(productImage);
        product.setWeight(weight);
        product.setQuantity(quantity);

        List<Category> categories = new ArrayList<>();
        categories.add(category);
        when(categoryService.getCategories()).thenReturn(categories);

        when(categoryService.getCategory(categoryId)).thenReturn(category);

        String result = adminController.addProduct(name, categoryId, price, weight, quantity, description, productImage,new RedirectAttributesModelMap());

        // Verifying behavior
        assertEquals("redirect:/admin/products", result);

    }

    @Test
    public void testUpdateProduct_GetMethod() {
        int productId = 1;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Test Product");
        // Set other properties for the mockProduct

        List<Category> mockCategories = new ArrayList<>();
        // Add mock categories

        Mockito.when(productService.getProduct(productId)).thenReturn(mockProduct);
        Mockito.when(categoryService.getCategories()).thenReturn(mockCategories);

        ModelAndView modelAndView = adminController.updateproduct(productId);

        assertEquals("productsUpdate", modelAndView.getViewName());
        assertEquals(mockCategories, modelAndView.getModel().get("categories"));
        assertEquals(mockProduct, modelAndView.getModel().get("product"));
    }
    @Test
    public void testRemoveProduct() {
        int productId = 1;

        String redirectUrl = adminController.removeProduct(productId);

        assertEquals("redirect:/admin/products", redirectUrl);

        /*ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(productService).deleteProduct(idCaptor.capture());

        assertEquals(productId, idCaptor.getValue().intValue());*/
    }
    @Test
    public void testPostProductRedirect() {
        String redirectUrl = adminController.postproduct();

        assertEquals("redirect:/admin/categories", redirectUrl);
    }

    @Test
    public void testGetCustomerDetail_AdminNotLoggedIn() {
        MockitoAnnotations.initMocks(this);
        adminController.setAdminlogcheck(0);

        ModelAndView result = adminController.getCustomerDetail();

        assertEquals("adminlogin", result.getViewName());
    }

    @Test
    public void testGetCustomerDetail_AdminLoggedIn() {
        MockitoAnnotations.initMocks(this);
        adminController.setAdminlogcheck(1);

        List<User> users = new ArrayList<>(); // Mock list of users for testing
        // Mock userService.getUsers() method call
        when(mockUserService.getUsers()).thenReturn(users);

        ModelAndView result = adminController.getCustomerDetail();

        assertEquals("displayCustomers", result.getViewName());
        assertEquals(users, result.getModel().get("customers"));
    }
    @Mock
    private Model model;
    @Test
    public void testProfileDisplay() {
        MockitoAnnotations.initMocks(this);
        String usernameForClass = "testUsername";

        try {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(1);
            when(mockResultSet.getString(2)).thenReturn("TestUser");
            when(mockResultSet.getString(3)).thenReturn("test@example.com");
            when(mockResultSet.getString(4)).thenReturn("testPassword");
            when(mockResultSet.getString(5)).thenReturn("Test Address");

            when(model.addAttribute(anyString(), any())).thenReturn(model);

            // Mock your JDBC driver loading and connection creation here

            adminController.profileDisplay(1,model);

            // Verify that the model attributes were added
            verify(model).addAttribute("userid", 1);
            verify(model).addAttribute("username", "User");
            verify(model).addAttribute("email", "test@example.com");
            verify(model).addAttribute("password", "password");
            verify(model).addAttribute("address", "Address");
        } catch (Exception e) {
        }
    }

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    public void testUpdateUserProfile() {
        MockitoAnnotations.initMocks(this);
        int userId = 1;
        String username = "testUser";
        String email = "test@example.com";
        String password = "testPassword";
        String address = "Test Address";

        try {
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Assuming one row updated

            adminController.updateUserProfile(userId, username, email, password, address);

            // Verify that appropriate methods were called
            verify(mockPreparedStatement).setString(1, username);
            verify(mockPreparedStatement).setString(2, email);
            verify(mockPreparedStatement).setString(3, password);
            verify(mockPreparedStatement).setString(4, address);
            verify(mockPreparedStatement).setInt(5, userId);
            verify(mockPreparedStatement).executeUpdate();
        } catch (Exception e) {
            // Handle the exception or fail the test
        }
    }

    @Test
    public void testRemoveCustomer() {
        MockitoAnnotations.initMocks(this);
        int customerId = 123;

        adminController.removeCustomer(customerId);

        verify(mockUserService, times(1)).deleteUser(customerId);
    }

    @Test
    public void testGetAdminlogcheck() {

        int result = adminController.getAdminlogcheck();

        // Assert that the initial value matches the expected value of 0
        assertEquals(0, result);
    }
    @Test
    public void testGetProductService() {
        productService productServiceInstance = adminController.getProductService();

        // Assert that the productService instance is not null
        assertNotNull(productServiceInstance);
    }
}