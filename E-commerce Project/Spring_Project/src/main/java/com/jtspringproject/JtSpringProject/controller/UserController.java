package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.*;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.jtspringproject.JtSpringProject.services.cartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class UserController{
	
	@Autowired
	private userService userService;

	@Autowired
	private productService productService;

	@Autowired
	private cartService cartService;

	private  static User user;
	private  static Cart cart;

	@Autowired
	public UserController(userService userService, productService productService, cartService cartService) {
		this.userService = userService;
		this.productService = productService;
		this.cartService = cartService;
		this.cart = new Cart();
	}

	@GetMapping("/register")
	public String registerUser(Model model)
	{
		User user = new User();
		model.addAttribute("user",user);
		return "register";
	}

	@GetMapping("/buy")
	public String buy()
	{
		return "buy";
	}
	

	@GetMapping(value = "/index")
	public String home(Model model)
	{
		model.addAttribute("user",user);

		List<Product> products = this.productService.getProducts();

		if (products.isEmpty()) {
			model.addAttribute("msg", "No products are available");
		} else {
			model.addAttribute("products", products);
		}
		return "index";

	}
	@GetMapping("/")
	public String userlogin(Model model) {

		cart = null;
		user = null;
		model.addAttribute("message","Tested By Quality Control Force");
		model.addAttribute("errorMsg", "Please enter correct email and password");

		return "userLogin";
	}
	@RequestMapping(value = "userloginvalidate", method = RequestMethod.POST)
	public ModelAndView userlogin(@RequestParam("username") String username, @RequestParam("password") String pass, Model model, HttpServletResponse res, RedirectAttributes redirectAttributes) {

		System.out.println(pass);
		User u = this.userService.checkLogin(username, pass);

		if (u == null || u.getRole().equalsIgnoreCase("Role_Admin") ){

			redirectAttributes.addFlashAttribute("error","Incorrect ! Username or Password");
			return new ModelAndView("redirect:/");

		} else {

			System.out.println("Username = " + u.getUsername());
			if (u.getUsername().equalsIgnoreCase(username.trim())) {

				user = u;

				res.addCookie(new Cookie("username", u.getUsername()));
				ModelAndView mView = new ModelAndView("index");
				mView.addObject("user", u);


				List<Product> products = this.productService.getProducts();

				if (products.isEmpty()) {
					mView.addObject("msg", "No products are available");
				} else {
					mView.addObject("products", products);
				}
				return mView;

			}
		}

		return new ModelAndView("userLogin");
	}
	
	
	@GetMapping("/user/products")
	public ModelAndView getproduct() {

		ModelAndView mView = new ModelAndView("uproduct");

		List<Product> products = this.productService.getProducts();

		if(products.isEmpty()) {
			mView.addObject("msg","No products are available");
		}else {
			mView.addObject("products",products);
		}

		return mView;
	}
	@RequestMapping(value = "newuserregister", method = RequestMethod.POST)
	public String newUseRegister(@ModelAttribute User user, Model model)
	{

		List<User> allUsers = userService.getUsers();

		for(User u : allUsers)
		{
			if(u.getUsername().equalsIgnoreCase(user.getUsername()))
			{
				model.addAttribute("error","Error !, Username already taken !!!!");
				return "register";
			}
		}
		System.out.println(user.getEmail());
		user.setRole("ROLE_NORMAL");
		this.userService.addUser(user);
		
		return "redirect:/";
	}

	@GetMapping("carts")
	public String getCartDetail(Model model,@RequestParam("id") int id)
	{

		List<Cart> carts = cartService.getCarts();


		if(cart == null)
			cart = new Cart();
		cart.setCustomer(user);

		for(Cart c : carts)
		{
			if(c.getCustomer().getId() == id)
			{
				cart = c;
				model.addAttribute("cart",cart);
				return "cartproduct";
			}
		}

		model.addAttribute("cart",cart);

		return "cartproduct";

    }

	@PostMapping("cartAdd")
	public String addToCart(@RequestParam("id") int id)
	{
		Product p = productService.getProduct(id);

		//System.out.println(cart.getId());

		if(cart.getCustomer() == null)
			cart.setCustomer(user);

		if(cart.getProducts().isEmpty()) {
			cart.addProduct(p);
			cartService.addCart(cart);
		}
		else {
			cart.addProduct(p);
			cartService.updateCart(cart);
		}


		return "redirect:/carts?id=" + cart.getId();
	}

	@PostMapping("delete")
	public String deleteFromCart(@RequestParam("id") int id)
	{
		Product p = productService.getProduct(id);

		if(!cart.getProducts().isEmpty()) {
			cart.removeProduct(p);
			cartService.updateCart(cart);
		}

		return "redirect:/carts?id=" + cart.getId();
	}

	@GetMapping("/user/profile")
	public String updateProfile(Model model)
	{
		model.addAttribute("user",user);

		return "updateProfile";
	}

	@RequestMapping(value = "/user/updateuser", method = RequestMethod.POST)
	public String updateProfile(@RequestParam("id") int id,@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("email") String email,@RequestParam("address") String address,Model model)
	{

		if(user != null) {
			user.setUsername(username.trim());
			user.setPassword(password);
			user.setEmail(email);
			user.setAddress(address);

			userService.updateUser(user);

			model.addAttribute("user", user);

			List<Product> products = this.productService.getProducts();

			if (products.isEmpty()) {
				model.addAttribute("msg", "No products are available");
			} else {
				model.addAttribute("products", products);
			}
		}

		return "redirect:/index";
	}
	//for Learning purpose of model
	@GetMapping("/test")
	public String Test(Model model)
	{
		System.out.println("test page");
		model.addAttribute("author","jay gajera");
		model.addAttribute("id",40);

		List<String> friends = new ArrayList<String>();
		model.addAttribute("f",friends);
		friends.add("xyz");
		friends.add("abc");

		return "test";
	}

	// for learning purpose of model and view ( how data is pass to view)

	@GetMapping("/test2")
	public ModelAndView Test2()
	{
		System.out.println("test page");
		//create modelandview object
		ModelAndView mv=new ModelAndView();
		mv.addObject("name","jay gajera 17");
		mv.addObject("id",40);
		mv.setViewName("test2");

		List<Integer> list=new ArrayList<Integer>();
		list.add(10);
		list.add(25);
		mv.addObject("marks",list);
		return mv;


	}


}
