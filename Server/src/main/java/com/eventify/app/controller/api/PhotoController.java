package com.eventify.app.controller.api;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.eventify.app.service.interfaces.IPhotoService;
import com.eventify.app.model.Photo;

@RestController
public class PhotoController {

	@Autowired
	@Qualifier("mainService")
	private IPhotoService photoService;

	public PhotoController() {

	}

	@RequestMapping("/api/photos")
	public Iterable<Photo> getAll() {
		return photoService.getAll();
	}

	@RequestMapping("/api/photos/{id}")
	public Photo getById(@PathVariable Long id) {

		Optional<Photo> photo = photoService.getById(id);

		if (photo.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
		}
		return photo.get();
	}

	@GetMapping("/api/download/{id}")
    public ResponseEntity<Resource> DownloadImage(@PathVariable Long id) throws Exception {

        Photo photo = photoService.getById(id).get();
        return ResponseEntity.created(null)
        .contentType(MediaType.parseMediaType(photo.getPhotoType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
                "photo; filename=\"" + photo.getPhotoName()
                + "\"").body(new ByteArrayResource(photo.getData()));
    }
}
