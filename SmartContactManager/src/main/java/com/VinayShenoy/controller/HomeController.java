package com.VinayShenoy.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.VinayShenoy.dao.UserRepository;
import com.VinayShenoy.entity.User;
import com.VinayShenoy.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home- Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About- Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login- Smart Contact Manager");
		return "login";
	}
	
	
	/*handler for user registration*/
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	public String  registerUser(@Valid @ModelAttribute ("user") User user, BindingResult result1,
			@RequestParam(value = "agreement",defaultValue = "false") boolean agreement, Model model,
			HttpSession session ) {
		
		try {
			if(!agreement) {
				System.out.println("You have not agreed to our terms and conditions");
				throw new Exception("You have not agreed to our terms and conditions");
			}
			
			if (result1.hasErrors())
			{
				System.out.println("ERROR "+ result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement" +agreement);
			System.out.println("User" + user);
			
			User result =this.userRepository.save(user);
			model.addAttribute("user" ,new User());
			session.setAttribute("message",new Message("Successfully registered ","alert-success" ));
			return "signup";
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message",new Message("Something went wrong "+e.getMessage(),"alert-danger" ));
			return "signup";
		}
		
		
	}
	
	/*Forgot password starts*/
    @RequestMapping("/forgot")
    public String forgotPassword() {
      return"forgot-password";  
    
    }
    /*Forgot password ends*/
    Random random = new Random(1000);
    /*Send OTP starts*/
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email) {
       
        //generating 4 digit otp
       
        int otp = random.nextInt(99999);
        
        System.out.println("EMAIL ID "+email);
        System.out.println("OTP "+otp);
      return"verify-otp";  
    
    }
    /*Send OTP starts*/
	
}
