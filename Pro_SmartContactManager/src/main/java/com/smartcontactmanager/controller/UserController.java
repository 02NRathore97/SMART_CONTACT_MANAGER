package com.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired	
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	//this method will run all time
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		//get the username(email)
		String username = principal.getName();
		System.out.println("username = " + username);
		
		//get the user using username(email)
		User user = userRepository.getUserByUserName(username);
		System.out.println(user);
		
		
		//set the user
		model.addAttribute("user", user);
	}
	
	
	
		@RequestMapping("/index")
		public String dashboard(Model model, Principal principal) {
			
			model.addAttribute("title", "UserDashboard - Smart Contact Manager");
			
			String userName = principal.getName();
			//System.out.println("USERNAME " + userName);
			User user = userRepository.getUserByUserName(userName);
			model.addAttribute("user", user);
			//return to user dashboard
			return "normal/dashboard";
		}
		
		
		
		//open add form handler
		@GetMapping("/addcontact")
		public String openAddContactFormHandler(Model model){
			model.addAttribute("title", "AddContactForm - Smart Contact Manager");
			model.addAttribute("contact", new Contact());
			
			return "normal/addcontactform";
		}
		
		//processing contact form
		@PostMapping("/processcontact")
		public String processcontact(@ModelAttribute Contact contact,
									@RequestParam("profileimage") MultipartFile file, 
									Principal principal,
									HttpSession session) {
			
			try {
			
			//get username
			String name = principal.getName();
			//get userdetails by username
			User user = this.userRepository.getUserByUserName(name);
			
			//to check error message
			//if(3>2) {
			//	throw new Exception();
			//}			
			
			//processing and uploading file......
			if(file.isEmpty()) {
				//if file is empty then try our message
				System.out.println("empty file");
				contact.setImage("contact.png");
			}else {
				contact.setImage(file.getOriginalFilename());
				
				
				File saveFile = new ClassPathResource("/static/Img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("image is uploaded");
			}
			
			
			user.getContacts().add(contact);
			contact.setUser(user);
			
			//save data to database
			this.userRepository.save(user);
		
			System.out.println("Saved successfully");
			
			
			//printing cantact value
			System.out.println("data = "+ contact);
			
			
			//success message........
			session.setAttribute("message", new Message("your contact is added successfully !!","success"));
			
			}catch(Exception e){
				e.printStackTrace();
				
				//error message........
				session.setAttribute("message", new Message("Something went wronge plz try again !!","danger"));
				
			}
			return "normal/addcontactform";
		}

		//show contact handler
		@GetMapping("/showcontacts/{page}")
		public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
			model.addAttribute("title", "ShowContacts - Smart Contact Manager");
			
			
			//get list using username
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			
			
			//List<Contact> contacts = user.getContacts();
			
					//OR
			
			//get user details using contactRepository
			//List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
			
			
			//for pagination
			Pageable pageable = PageRequest.of(page, 2);
			Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
			
			//send list of contacts
			model.addAttribute("contacts",contacts);
			model.addAttribute("currentpage", page);
			model.addAttribute("totalpages", contacts.getTotalPages());
			
			return "normal/showcontacts";
		}
		
		//showing particular Contact Details
		@RequestMapping("/{cId}/contact")
		public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
			
			System.out.println("CID "+ cId);
			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact = contactOptional.get();
			
			
			//to prevent unauthorized user to access the contacts of any other user
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			if(user.getId()== contact.getUser().getId()){
				model.addAttribute("contact", contact);
				model.addAttribute("title", contact.getName());
			}
			
			return "normal/contactdetail";
		}
		
		//delete contact handler
		@GetMapping("/delete/{cid}")
		public String deleteContact(@PathVariable("cid") Integer cid,Model model, Principal principal,HttpSession session ) {
			
			Optional<Contact> contactOptional = this.contactRepository.findById(cid);
			Contact contact = contactOptional.get();
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			List<Contact> contacts = user.getContacts();
			contacts.remove(contact);
			this.userRepository.save(user);

			
			session.setAttribute("message", new Message("contact deleted successfully....", "success"));
			
			return "redirect:/user/showcontacts/0";
		}

		
		//open update contact form
		@PostMapping("/updatecontact/{cid}")
		public String updateForm(@PathVariable("cid") Integer cid, Model model) {
			model.addAttribute("title", "UpdateContact - Smart Contact Manager");
			
			
			Contact contact = this.contactRepository.findById(cid).get();
			model.addAttribute("contact", contact);
			
			
			return "normal/updateform";
			
		}
		
		//update contact handler
		@RequestMapping(value="/processupdate", method=RequestMethod.POST)
		public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file, Model model, HttpSession session, Principal principal) {
			System.out.println("CONTACT NAME "+ contact.getName());
			System.out.println("CONTACT ID "+ contact.getCid());
			
			try {
				//old contact details
				Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get(); 
				
				//image...
				if(!file.isEmpty()) {
					//file work...rewrite
					
					
					//delete old photo
					
					File deleteFile = new ClassPathResource("/static/Img").getFile();
					File file1 = new File(deleteFile, oldContactDetail.getImage());
					file1.delete();
					
					//update new photo

					
					File saveFile = new ClassPathResource("/static/Img").getFile();
					Path path = Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImage(file.getOriginalFilename());
				}else {
					contact.setImage(oldContactDetail.getImage());
				}
				User user = this.userRepository.getUserByUserName(principal.getName());
				contact.setUser(user);
				this.contactRepository.save(contact);
				session.setAttribute("message", new Message("Your contact is updated.....", "success"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "redirect:/user/"+contact.getCid()+"/contact";
		}
		
		
		//Your profile handler
		@GetMapping("/profile")
		public String yourProfile(Model model) {
			model.addAttribute("title","Profile - Smart Contact Manager");
			return "normal/profile";
		}

		
		//Open setting handler
		@GetMapping("/settings")
		public String openSetting() {
			return "normal/settings";
		}

		//change password handler
		@PostMapping("/changepassword")
		public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
			
			System.out.println(oldPassword);
			System.out.println(newPassword);
			String userName = principal.getName();
			User currentUser = this.userRepository.getUserByUserName(userName);
			System.out.println(currentUser.getPassword());
			
			if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
				//change password
				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				session.setAttribute("message", new Message("Your Password is successfully changed", "success"));
			}else {
				//error
				session.setAttribute("message", new Message("Please Enter Correct old password.....", "danger"));
				return "redirect:/user/settings";
			}
			
			return "redirect:/user/index";
		}
}
