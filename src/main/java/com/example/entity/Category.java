package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String categoryname;
	private String description;
	private Boolean isactive=true;
	private LocalDateTime createdat;
	private LocalDateTime updatedat;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsactive() {
		return isactive;
	}
	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}
	public LocalDateTime getCreatedat() {
		return createdat;
	}
	public void setCreatedat(LocalDateTime createdat) {
		this.createdat = createdat;
	}
	public LocalDateTime getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(LocalDateTime updatedat) {
		this.updatedat = updatedat;
	}
	
}
