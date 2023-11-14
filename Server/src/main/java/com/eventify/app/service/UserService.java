package com.eventify.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eventify.app.service.interfaces.IUserService;
import com.eventify.app.model.User;
import com.eventify.app.repository.IUserRepository;

@Service
public class UserService implements IUserService {

	@Autowired
    private IUserRepository userRepository;

	public UserService() {

	}

	@Override
	public Iterable<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> getById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User create(User User) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		String EncryptedPwd = bcrypt.encode(User.getPassword());
		User.setPassword(EncryptedPwd);
		return userRepository.save(User);
	}

	@Override
	public Optional<User> update(Long id, User User) {

		Optional<User> foundUser = userRepository.findById(id);

		if (foundUser.isEmpty()) {
			return Optional.empty();
		}

		foundUser.get().setFirstname(User.getFirstname());
		foundUser.get().setLastname(User.getLastname());
		foundUser.get().setDob(User.getDob());
		foundUser.get().setEmail(User.getEmail());
		foundUser.get().setPassword(User.getPassword());
		foundUser.get().setProfilePicture(User.getProfilePicture());
		userRepository.save(foundUser.get());

		return foundUser;
	}

	@Override
	public Boolean delete(Long id) {

		Optional<User> foundUser = userRepository.findById(id);

		if (foundUser.isEmpty()) {
			return false;
		}

		userRepository.delete(foundUser.get());

		return true;
	}

	public Optional<User> findByEmail(String email) {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

	public Optional<User> findByOtp(Integer otp) {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if (user.getOtp() != null && user.getOtp().intValue() == otp) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
