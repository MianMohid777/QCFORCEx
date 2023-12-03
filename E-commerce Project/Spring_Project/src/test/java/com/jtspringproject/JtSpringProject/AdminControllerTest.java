package com.jtspringproject.JtSpringProject;

import com.jtspringproject.JtSpringProject.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;
    @Mock
    private Model mockModel;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void returnIndex() {
        adminController.adminlogcheck = 1;
        adminController.usernameforclass = "testUser";
        String result = adminController.returnIndex();
        assertEquals("userLogin", result);
        assertEquals(0, adminController.adminlogcheck);
        assertEquals("", adminController.usernameforclass);
    }

    @Test
    public void testIndex_WhenUsernameIsEmpty_ReturnsUserLogin() {
        adminController.setUsernameforclass(""); // Set usernameforclass to empty string

        String result = adminController.index(mockModel);

        assertEquals("userLogin", result);
    }

    @Test
    public void testIndex_WhenUsernameIsNotEmpty_ReturnsAdminHome() {
        String username = "testUser";
        adminController.setUsernameforclass(username);
        when(mockModel.addAttribute("username", username)).thenReturn(mockModel);

        String result = adminController.index(mockModel);

        assertEquals("adminHome", result);
    }

    @Test
    void adminlogin() {
        String result = adminController.adminlogin(mockModel);

        assertEquals("adminlogin", result); // check if it returns the correct view

        verify(mockModel).addAttribute("message", "Tested By Quality Control Force");
        // Verify if the 'message' is added to the model with the given value
    }

    @Test
    void adminHome() {
    }

    @Test
    void testAdminlogin() {
    }

    @Test
    void getcategory() {
    }

    @Test
    void addCategory() {
    }

    @Test
    void removeCategoryDb() {
    }

    @Test
    void updateCategory() {
    }

    @Test
    void getproduct() {
    }

    @Test
    void addProduct() {
    }

    @Test
    void testAddProduct() {
    }

    @Test
    void updateproduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void removeProduct() {
    }

    @Test
    void postproduct() {
    }

    @Test
    void getCustomerDetail() {
    }

    @Test
    void profileDisplay() {
    }

    @Test
    void updateUserProfile() {
    }
}