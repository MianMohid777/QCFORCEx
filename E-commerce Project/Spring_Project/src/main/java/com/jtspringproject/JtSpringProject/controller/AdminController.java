package com.jtspringproject.JtSpringProject.controller;

import java.sql.*;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import com.mysql.cj.protocol.Resultset;

import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.OffsetMapping.ForOrigin.Renderer.ForReturnTypeName;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private userService userService;

	public com.jtspringproject.JtSpringProject.services.userService getUserService() {
		return userService;
	}

	public void setUserService(com.jtspringproject.JtSpringProject.services.userService userService) {
		this.userService = userService;
	}

	public com.jtspringproject.JtSpringProject.services.categoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(com.jtspringproject.JtSpringProject.services.categoryService categoryService) {
		this.categoryService = categoryService;
	}

	public com.jtspringproject.JtSpringProject.services.productService getProductService() {
		return productService;
	}

	public void setProductService(com.jtspringproject.JtSpringProject.services.productService productService) {
		this.productService = productService;
	}

	public int getAdminlogcheck() {
		return adminlogcheck;
	}

	public void setAdminlogcheck(int adminlogcheck) {
		this.adminlogcheck = adminlogcheck;
	}

	public String getUsernameforclass() {
		return usernameforclass;
	}

	public void setUsernameforclass(String usernameforclass) {
		this.usernameforclass = usernameforclass;
	}

	@Autowired
	private categoryService categoryService;
	
	@Autowired
	private productService productService;
	
	public static int adminlogcheck = 0;
	public String usernameforclass = "";
	@RequestMapping(value = {"return"})
	public String returnIndex() {
		adminlogcheck =0;
		usernameforclass = "";
		return "userLogin";
	}
	
	
	
	@GetMapping("log")
	public String index(Model model) {
		if(usernameforclass.equalsIgnoreCase(""))
			return "userLogin";
		else {
			model.addAttribute("username", usernameforclass);
			return "adminHome";
		}
			
	}

	@GetMapping("login")
	public String adminlogin(Model model) {

		model.addAttribute("message","Tested By Quality Control Force");
		return "adminlogin";
	}
	@GetMapping("/Dashboard")
	public String adminHome(Model model) {
		if(adminlogcheck==1)
			return "adminHome";
		else
			return "redirect:/admin/login";
	}
	@RequestMapping(value = "admin/loginvalidate", method = RequestMethod.POST)
	public ModelAndView adminlogin( @RequestParam("username") String username, @RequestParam("password") String pass,RedirectAttributes redirectAttributes) {
		User user=this.userService.checkLogin(username, pass);

		if (user == null) {
			redirectAttributes.addFlashAttribute("error","Incorrect ! Username or Password");
			return new ModelAndView("redirect:/admin/login");

		} else if(user.getRole().equals("ROLE_ADMIN")) {
			ModelAndView mv = new ModelAndView("adminHome");
			adminlogcheck=1;
			mv.addObject("admin", user);
			return mv;
		}
		return new ModelAndView("adminlogin");
	}
	@GetMapping("categories")
	public ModelAndView getcategory() {
		if(adminlogcheck==0){
			ModelAndView mView = new ModelAndView("adminlogin");
			return mView;
		}
		else {
			ModelAndView mView = new ModelAndView("categories");
			List<Category> categories = this.categoryService.getCategories();
			mView.addObject("categories", categories);
			return mView;
		}
	}
	@RequestMapping(value = "categories",method = RequestMethod.POST)
	public String addCategory(@RequestParam("categoryname") String category_name,RedirectAttributes redirectAttributes) {
		System.out.println(category_name);

		if (!category_name.trim().isEmpty()) {
			String cat = category_name.trim();
			Category category = this.categoryService.addCategory(cat);

			if (category != null && category.getName() != null && category.getName().equals(cat)) {
				// Category added successfully
				redirectAttributes.addFlashAttribute("success", "Category Added Successfully");
			} else {
				// Failed to add category
				redirectAttributes.addFlashAttribute("failure", "Failed to add Category");
			}
		}

		return "redirect:/admin/categories";
	}

	@RequestMapping(value = "categories/delete",method = RequestMethod.POST)
	public String removeCategoryDb(@RequestParam("id") int id)
	{	
			this.categoryService.deleteCategory(id);
			return "redirect:/admin/categories";
	}

	@RequestMapping(value = "categories/update",method = RequestMethod.POST)
	public String updateCategory(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryname)
	{
		if(!categoryname.trim().isEmpty())
		{Category category = this.categoryService.updateCategory(id, categoryname.trim());}
		return "redirect:/admin/categories";
	}

	
//	 --------------------------Remaining --------------------
	@GetMapping("products")
	public ModelAndView getproduct() {
		if(adminlogcheck==0){
			ModelAndView mView = new ModelAndView("adminlogin");
			return mView;
		}
		else {
			ModelAndView mView = new ModelAndView("products");

			List<Product> products = this.productService.getProducts();

			if (products.isEmpty()) {
				mView.addObject("msg", "No products are available");
			} else {
				mView.addObject("products", products);
			}
			return mView;
		}

	}
	@GetMapping("products/add")
	public ModelAndView addProduct() {
		ModelAndView mView = new ModelAndView("productsAdd");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories",categories);
		return mView;
	}

	@RequestMapping(value = "products/add",method=RequestMethod.POST)
	public String addProduct(@RequestParam("name") String name,@RequestParam("categoryid") int categoryId ,@RequestParam("price") int price,@RequestParam("weight") int weight, @RequestParam("quantity")int quantity,@RequestParam("description") String description,@RequestParam("productImage") String productImage,RedirectAttributes redirectAttributes) {
		System.out.println(categoryId);
		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();

		if (!name.trim().isEmpty() && category != null && price > 0 && quantity > 0) {
			product.setName(name);
			product.setCategory(category);
			product.setDescription(description);
			product.setPrice(price);
			product.setImage(productImage);
			product.setWeight(weight);
			product.setQuantity(quantity);

			if (this.productService.addProduct(product) != null)
				redirectAttributes.addFlashAttribute("success", "Product Added Successfully");
			else {

				redirectAttributes.addFlashAttribute("failure", "Product, Already Added !");
			}
		}


		return "redirect:/admin/products";
	}

	@GetMapping("products/update/{id}")
	public ModelAndView updateproduct(@PathVariable("id") int id) {
		
		ModelAndView mView = new ModelAndView("productsUpdate");
		Product product = this.productService.getProduct(id);
		List<Category> categories = this.categoryService.getCategories();

		mView.addObject("categories",categories);
		mView.addObject("product", product);
		return mView;
	}
	
	@RequestMapping(value = "products/update/{id}",method=RequestMethod.POST)
	public String updateProduct(@PathVariable("id") int id ,@RequestParam("name") String name,@RequestParam("categoryid") int categoryId ,@RequestParam("price") int price,@RequestParam("weight") int weight, @RequestParam("quantity")int quantity,@RequestParam("description") String description,@RequestParam("productImage") String productImage)
	{
		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();
		product.setName(name);
		product.setCategory(category);
		product.setDescription(description);
		product.setPrice(price);
		product.setImage(productImage);
		product.setWeight(weight);
		product.setQuantity(quantity);
		this.productService.updateProduct(id,product);
		return "redirect:/admin/products";
	}
	
	@GetMapping("products/delete")
	public String removeProduct(@RequestParam("id") int id)
	{
		this.productService.deleteProduct(id);
		return "redirect:/admin/products";
	}
	
	@PostMapping("products")
	public String postproduct() {
		return "redirect:/admin/categories";
	}
	
	@GetMapping("customers")
	public ModelAndView getCustomerDetail() {
		if(adminlogcheck==0){
			ModelAndView mView = new ModelAndView("adminlogin");
			return mView;
		}
		else {
			ModelAndView mView = new ModelAndView("displayCustomers");
			List<User> users = this.userService.getUsers();
			mView.addObject("customers", users);
			return mView;
		}
	}
	
	
	@GetMapping("profileDisplay")
	public String profileDisplay(@RequestParam("id") int id,Model model) {


		User u = userService.findUserById(id);

		if(u != null)
		{
			model.addAttribute("user",u);
			return "updateProfileByAdmin";
		}
		else
			return "displayCustomers";


	}
	
	@RequestMapping(value = "updateuser",method=RequestMethod.POST)
	public String updateUserProfile(@RequestParam("id") int id,@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("address") String address)
	{

		User u = userService.findUserById(id);

		if(u != null) {
			u.setUsername(username);
			u.setPassword(password);
			u.setEmail(email);
			u.setAddress(address);

			userService.updateUser(u);
		}

		return "adminHome";
	}

	@GetMapping("displayCustomers/delete")
	public String removeCustomer(@RequestParam("id") int id)
	{
		if(!userService.findUserById(id).getRole().equalsIgnoreCase("ROLE_ADMIN"))
		   this.userService.deleteUser(id);
		return "redirect:/admin/customers";
	}



		public static void main(String[] args) {
			String workingDirectory = System.getProperty("user.dir");
			System.out.println("Working Directory: " + workingDirectory);
		}

}
