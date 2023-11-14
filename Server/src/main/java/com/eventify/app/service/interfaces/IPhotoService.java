package com.eventify.app.service.interfaces;

import java.util.Optional;

import com.eventify.app.model.Photo;

public interface IPhotoService {

	public Iterable<Photo> getAll();
	public Optional<Photo> getById(Long id);
	public Photo create(Photo photo);
	public Optional<Photo> update(Long id, Photo photo);
	public Boolean delete(Long id);
}
