INSERT INTO `app_role` (`id`, `role_name`)
VALUES
  (1, 'STANDARD_USER'),
  (2, 'ADMIN_USER');

#Insert sample users in the database. Decrypted password: 'password' (without the quotes)
INSERT INTO `app_user` (`id`, `first_name`, `last_name`, `password`, `username`, `email`)
VALUES
  (1, 'Test', 'User', '$2a$10$VgKED17HMJHX2KNPoaaSiOk9PfUPTkSwJc3LFyWK4Y9NYrHC6FTP6', 'user.name', 'test@test.be'),
  (2, 'Admin', 'admin', '$2a$10$VgKED17HMJHX2KNPoaaSiOk9PfUPTkSwJc3LFyWK4Y9NYrHC6FTP6', 'admin', 'admin@admin.be');

#Admin user is a standard user AND an admin
INSERT INTO `user_role` (`user_id`, `role_id`)
VALUES
  (1, 1),
  (2, 1),
  (2, 2);