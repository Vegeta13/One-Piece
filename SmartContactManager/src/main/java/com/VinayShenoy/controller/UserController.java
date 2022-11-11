package com.VinayShenoy.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import com.VinayShenoy.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.VinayShenoy.dao.ContactRepository;
import com.VinayShenoy.dao.UserRepository;
import com.VinayShenoy.entity.Contact;
import com.VinayShenoy.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	//common data
	@ModelAttribute
	public void  addCommonDate(Model model, Principal principal) {
		String userName= principal.getName();
		System.out.println("USERNAME: "+userName);
		
		
		User  user = userRepository.getUserByUserName(userName);
		System.out.println("User " +user);

		model.addAttribute("user", user); 

	}
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {

		return "normal/user_dashboard";
	}
	
	//taking user-contact input
	@GetMapping("/add-contacts")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/addContactForm";
	}
	
	// processing add contact form
	@PostMapping("/process-contacts")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal,
			HttpSession session) {
		
		try {
			String name= principal.getName();
			User user= this.userRepository.getUserByUserName(name);
			
			// processing and uploading files...
			
			if(file.isEmpty()) {
				// if the file is empty then try your msg 
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}
			else {
				// file the file to folder  and update  the name to contact
				contact.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("IMAGE IS UPLOADED");
			}
			
			user.getContacts().add(contact);
			contact.setUser(user);
			
			 this.userRepository.save(user);
			
			System.out.println("DATA "+ contact);
			System.out.println("Add data to the database");
			//msg success
			session.setAttribute("message", new Message("Your contact is saved", "success"));
			
		} catch (Exception e) {
			System.out.println("ERROR " +e.getMessage());
			e.printStackTrace();
			//error msg
			session.setAttribute("message", new Message("Something went wrong! try again", "danger"));
		}
		return "normal/addContactForm";
	}
	
	// SHOW CONTACT HANDLER
	
		@GetMapping("/show-contacts/{page}")
		public String showContact(@PathVariable("page") int page, 
				Model model,
				Principal principal) {
			model.addAttribute("title","Show all Contact");
			//we have to send the list of all the contact
			String userName = principal.getName();
			
			User user= this.userRepository.getUserByUserName(userName);
			int pageSize=5;
			Pageable pageable=PageRequest.of(page, pageSize);
			
			Page<Contact> contacts=  this.contactRepository.findContactsByUser(user.getId(),pageable);
			
			model.addAttribute("contacts", contacts);
			model.addAttribute("currentPage",page);
			model.addAttribute("totalPages", contacts.getTotalPages());
			
			
			return "normal/showContact";
		}
	
	//showing all contact details in user details

	 @RequestMapping("/{cId}/contact") public String
	 showContactDetails(@PathVariable("cId") Long cId, Model model, Principal principal) {
	 System.out.println("CID"+cId);
	 
	 Optional<Contact> contactOptional= this.contactRepository.findById(cId);
	 Contact contact = contactOptional.get();
	 
	 //check
	 String userName = principal.getName();
	 User user = this.userRepository.getUserByUserName(userName);
	 if(user.getId()==contact.getUser().getId()) {
		 model.addAttribute("contact", contact);
		 model.addAttribute("title",contact.getName());
	 }
	 
	 return "normal/contact-detail";
	 }

	 @GetMapping("/delete/{cId}")
	 @Transactional
	 public String deleteContact(@PathVariable("cId") Long cId,Model model,Principal principal, HttpSession session) {
		 System.out.println("CID"+cId);
		 
		 Contact contact = this.contactRepository.findById(cId).get();
		 
		 //check the id before deleting
		 
        /*
         * contact.setUser(null);
         * this.contactRepository.delete(contact);
         */
		 User user= this.userRepository.getUserByUserName(principal.getName());
		 user.getContacts().remove(contact);
		 this.userRepository.save(user);
		 
		 session.setAttribute("message", new Message("Contact deleted successfully", "success"));
		 return "redirect:/user/show-contacts/0";
	 }

	 
	 //open update form handler
	 @PostMapping("/edit/{cId}")
	 public String updateContact(@PathVariable("cId")Long cId,Model model){
		
	    model.addAttribute("title", "Update Contact");
	    Contact contact= this.contactRepository.findById(cId).get();
	    model.addAttribute("contact" ,contact);
		
		return "normal/updateForm";
	 }
	 
	 
	 
	 // process handler for update form
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,
	 @RequestParam("profileImage") 
	MultipartFile file, 
	Model model,
	Principal principal,
	HttpSession session){
		try {
			//old contact detail
			Contact oldContactDetail= this.contactRepository.findById(contact.getcId()).get();
            //image...
		    if(!file.isEmpty()) {
		        //file work

		        //rewrite
		        // delete old photo
		        File deleteFile = new ClassPathResource("static/img").getFile();
		        File file2=new File(deleteFile,oldContactDetail.getImage());
		        file2.delete();
				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
		    }
			else {
				contact.setImage(oldContactDetail.getImage());
			}
		    
		    User user= this.userRepository.getUserByUserName(principal.getName());
		    contact.setUser(user);
		    
		    this.contactRepository.save(contact);

			session.setAttribute("message",new Message("Saved changes...", "success"));
        }
		catch (Exception e) {
            e.printStackTrace();
        }
		
		System.out.println("Contact name"+contact.getName());
		System.out.println("Contact Id"+contact.getcId());
		return "redirect:/user/" +contact.getcId()+"/contact";
	}
	
	// your profile 
	@GetMapping("/profile")
	public String yourProfile(Model model) {
	    model.addAttribute("title","Profile");
        return"normal/profile";
    }
	
	/*user update and process starts*/
	@PostMapping("/edit-user/{id}")
	public String userUpdate(@PathVariable("id")Long id,Model model) {
	    model.addAttribute("title", "Update User");
	    User user= this.userRepository.findById(id).get();
        model.addAttribute("user" ,user);
	    
	    return "normal/update-user";
    }
	
	@RequestMapping(value = "/user-process-update", method = RequestMethod.POST)
	public String processUserUpdate(@ModelAttribute() User user,MultipartFile file, 
	        Model model,HttpSession session) {
	    this.userRepository.save(user);
	    session.setAttribute("message",new Message("Saved changes...", "success"));
	    
	    System.out.println("User name"+user.getName());
        System.out.println("User Id"+user.getId());
	    return"redirect:/user/profile";
	}
	/*user update and process ends*/
	
	
	/*user delete starts*/
	@GetMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable("id") Long id,Model model, HttpSession session) {
	   
	    this.userRepository.deleteById(id);
	    return"redirect:/";
	}
	/*user delete ends*/
	
	/*Open settings starts*/
	@GetMapping("/settings")
	public String openSetting() {
	    return"normal/settings";
	}
	/*open settings ends*/
	
	/* change password handler starts*/
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,
	                             @RequestParam("newPassword") String newPassword,
	                             Principal principal,
	                             HttpSession session) 
	{
	    System.out.println("OLD PASSWORD "+oldPassword);
	    System.out.println("NEW PASSWORD "+newPassword);
	    
	    String userName = principal.getName();
	    User currentUser =this.userRepository.getUserByUserName(userName);
	    
	    System.out.println("OLD PASSWORD "+currentUser.getPassword());
	    if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())) {
	        // change the password
	        currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
	        this.userRepository.save(currentUser);
	        session.setAttribute("message",new Message("Password changed...", "success"));
	    }
	    else {
            //error...
	        
	        
	        session.setAttribute("message",new Message("Password couldn't be changed...", "danger"));
	        
	        return "redirect:/user/settings";
	        
        }
	    return "redirect:/user/index";
	}
	/* change password handler ends*/
	
	
}
