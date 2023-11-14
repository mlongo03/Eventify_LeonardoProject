package com.eventify.app.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eventify.app.service.interfaces.IPhotoService;
import com.eventify.app.model.Photo;
import com.eventify.app.repository.IPhotoRepository;

@Service("mainService")
public class PhotoService implements IPhotoService{

	private IPhotoRepository iPhotoRepository;

    public PhotoService(IPhotoRepository iPhotoRepository) {
		this.iPhotoRepository = iPhotoRepository;
    }

	@Override
	public Iterable<Photo> getAll() {
		return iPhotoRepository.findAll();
	}

	@Override
	public Optional<Photo> getById(Long id) {
		return iPhotoRepository.findById(id);
	}

	@Override
	public Photo create(Photo photo) {
		return iPhotoRepository.save(photo);
	}

	@Override
	public Optional<Photo> update(Long id, Photo photo) {

		Optional<Photo> foundPhoto = iPhotoRepository.findById(id);

		if (foundPhoto.isEmpty()) {
			return Optional.empty();
		}

		foundPhoto.get().setPhotoName(photo.getPhotoName());
		foundPhoto.get().setPhotoType(photo.getPhotoType());
		foundPhoto.get().setData(photo.getData());
		foundPhoto.get().setIsDeleted(photo.getIsDeleted());
		iPhotoRepository.save(foundPhoto.get());

		return foundPhoto;
	}

	@Override
	public Boolean delete(Long id) {
		Optional<Photo> foundPhoto = iPhotoRepository.findById(id);
		if (foundPhoto.isEmpty()) {
			return false;
		}
		iPhotoRepository.delete(foundPhoto.get());
		return true;
	}

	public Photo uploadPhoto(MultipartFile image) throws Exception {

		String fileName = StringUtils.cleanPath(image.getOriginalFilename());

		try {
			if (fileName.contains("..")) {
				throw new Exception("Filename contains invalid path sequence " + fileName);
			}

			Photo photo = new Photo(image.getContentType(), fileName, Boolean.FALSE, image.getBytes());
			return photo;
		} catch (Exception e) {
			throw new Exception("Could not save file : " + fileName);
		}
	}
}
