package com.eventify.app.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eventify.app.service.interfaces.IPhotoService;
import com.eventify.app.model.Photo;

@RestController
public class AdminPhotoController {

	@Autowired
	@Qualifier("mainService")
	private IPhotoService adminPhotoService;

	public AdminPhotoController() {

	}

	@RequestMapping("/admin/api/photos")
	public Iterable<Photo> getAll() {
		return adminPhotoService.getAll();
	}

	@RequestMapping("/admin/api/photos/{id}")
	public Photo getById(@PathVariable Long id) {

		Optional<Photo> photo = adminPhotoService.getById(id);

		if (photo.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
		}
		return photo.get();
	}

	@RequestMapping(value = "/admin/api/photos", method = RequestMethod.POST)
	public Photo create(@RequestBody Photo photo) {

		return (adminPhotoService.create(photo));
	}

	@RequestMapping(value = "/admin/api/photos/{id}", method = RequestMethod.PUT)
	public Photo update(@PathVariable Long id, @RequestBody Photo photo) {

		Optional<Photo> updatedPhoto = adminPhotoService.update(id, photo);

		if (updatedPhoto.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
		}

		return (updatedPhoto.get());
	}

	@RequestMapping(value = "/admin/api/photos/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable Long id) {

		Boolean isDeleted = adminPhotoService.delete(id);

		if (isDeleted == false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
		}
	}
}
