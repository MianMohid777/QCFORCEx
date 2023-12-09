package com.jtspringproject.JtSpringProject;

import com.jtspringproject.JtSpringProject.controller.UserController;
import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.cartService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;


    @Mock
    private userService userService;

    @Mock
    private productService productService;

    @Mock
    private cartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        Model model = mock(Model.class);
        String result = userController.registerUser(model);
        Assertions.assertEquals("register", result);
        //User user = new User();
        //verify(model).addAttribute("user", user);
    }

    @Test
    void testUserLogin() {
        Model model = mock(Model.class);
        String result = userController.userlogin(model);
        Assertions.assertEquals("userLogin", result);
    }
    @Test
    void testHome() {
        Model model = mock(Model.class);
        List<Product> mockProducts = new ArrayList<>();
        when(productService.getProducts()).thenReturn(mockProducts);

        String result = userController.home(model);

        Assertions.assertEquals("index", result);
    }
    @Test
    void testBuy() {

        String result = userController.buy();
        Assertions.assertEquals("buy", result);
    }

    @Test
    void testUserlogin() {
        Model model = mock(Model.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        ModelAndView result = userController.userlogin("testUser", "testPassword", model, res, redirectAttributes);

        Assertions.assertEquals("redirect:/", result.getViewName());
    }

   /* @Test
    void testUserLoginWithValidCredentials() {
        Model model = mock(Model.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userService.checkLogin("testUser", "testPassword")).thenReturn(mockUser);

        ModelAndView result = userController.userlogin("testUser", "testPassword", model, res, redirectAttributes);

        Assertions.assertEquals("index", result.getViewName());
        Assertions.assertSame(mockUser, result.getModel().get("user"));
        verify(res).addCookie(any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }
*/
    @Test
    void testUserLoginWithInvalidCredentials() {
        Model model = mock(Model.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(userService.checkLogin("invalidUser", "invalidPassword")).thenReturn(null);

        ModelAndView result = userController.userlogin("invalidUser", "invalidPassword", model, res, redirectAttributes);

        Assertions.assertEquals("redirect:/", result.getViewName());
        verify(res, never()).addCookie(any());
        verify(redirectAttributes).addFlashAttribute("error", "Incorrect ! Username or Password");
    }

    @Test
    void testGetproduct() {
        ModelAndView result = userController.getproduct();

        Assertions.assertEquals("uproduct", result.getViewName());
        Assertions.assertEquals("No products are available",result.getModel().get("msg"));
    }

    @Test
    void testNewUseRegister() {
        Model model = mock(Model.class);
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newUser@example.com");
        newUser.setPassword("newPassword");
        newUser.setRole("ROLE_NORMAL");

        when(userService.getUsers()).thenReturn(new ArrayList<>());

        String result = userController.newUseRegister(newUser, model);

        Assertions.assertEquals("redirect:/", result);
        verify(userService).addUser(newUser);
    }

    @Test
    void testNewUseRegisterWithExistingUsername() {
        Model model = mock(Model.class);
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setEmail("existingUser@example.com");
        existingUser.setPassword("existingPassword");
        existingUser.setRole("ROLE_NORMAL");

        when(userService.getUsers()).thenReturn(List.of(existingUser));

        User newUser = new User();
        newUser.setUsername("existingUser");
        newUser.setEmail("newUser@example.com");
        newUser.setPassword("newPassword");
        newUser.setRole("ROLE_NORMAL");

        String result = userController.newUseRegister(newUser, model);

        Assertions.assertEquals("register", result);
        Assertions.assertNull( model.getAttribute("error"));
        verify(userService, never()).addUser(newUser);
    }


    @Test
    void testGetCartDetail() {
        Model model = mock(Model.class);

        Cart cart = new Cart();
        cart.setId(1);
        User user = new User();
        user.setId(1);
        cart.setCustomer(user);

        when(cartService.getCarts()).thenReturn(List.of(cart));

        String result = userController.getCartDetail(model, 1);

        Assertions.assertEquals("cartproduct", result);
    }
    @Test
    void testTest() {
        Model model = mock(Model.class);
        String result = userController.Test(model);
        Assertions.assertEquals("test", result);
    }
    @Test
    void testUpdateProfile() {
        Model model = mock(Model.class);
        String result = userController.updateProfile(model);
        Assertions.assertEquals("updateProfile", result);
    }


    @Test
    void testTest2() {
        ModelAndView result = userController.Test2();
        Assertions.assertEquals("test2", result.getViewName());
        Assertions.assertEquals("jay gajera 17", result.getModel().get("name"));
        Assertions.assertEquals(40, result.getModel().get("id"));
        List<Integer> marks = (List<Integer>) result.getModel().get("marks");
        Assertions.assertNotNull(marks);
        Assertions.assertEquals(2, marks.size());
        Assertions.assertEquals(10, marks.get(0));
        Assertions.assertEquals(25, marks.get(1));
    }
}
