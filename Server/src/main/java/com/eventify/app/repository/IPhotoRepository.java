package com.eventify.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventify.app.model.Photo;

@Repository
public interface IPhotoRepository extends JpaRepository<Photo, Long> {
}


