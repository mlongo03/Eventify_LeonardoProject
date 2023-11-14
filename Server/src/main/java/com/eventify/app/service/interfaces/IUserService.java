package com.eventify.app.service.interfaces;

import java.util.Optional;

import com.eventify.app.model.User;

public interface IUserService {

	public Iterable<User> getAll();
	public Optional<User> getById(Long id);
	public User create(User photo);
	public Optional<User> update(Long id, User photo);
	public Boolean delete(Long id);
}
