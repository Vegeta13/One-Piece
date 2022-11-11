package com.VinayShenoy.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long cId;
	
	private String name;
	private String secondName;
	private String work;
	private String email;
	private String password;
	private String image;
	private String phone;
	@Column(length = 1000)
	private String description;
	
	@ManyToOne()
	@JsonIgnore
	private User user;
	
	
	/* constructor */
	public Contact() {
		super();
	}
	/* Getter & Setter*/
	public Long getcId() {
		return cId;
	}
	public void setcId(Long cId) {
		this.cId = cId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	/* Getter & Setter*/
	/*
	 * @Override public String toString() { return "Contact [cId=" + cId + ", name="
	 * + name + ", secondName=" + secondName + ", work=" + work + ", email=" + email
	 * + ", password=" + password + ", image=" + image + ", phone=" + phone +
	 * ", description=" + description + ", user=" + user + "]"; }
	 */
	@Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return this.cId==((Contact)obj).getcId();
    }
	
	
	
	
	
}
