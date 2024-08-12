-- Insert test date in users table
INSERT INTO users (email, password, id)
VALUES ('user1@example.com', 'password1', 1),
       ('user2@example.com', 'password2', 2),
       ('user3@example.com', 'password3', 3);

SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));


-- Insert test date in users task
INSERT INTO task (header, description, status, priority, author, id)
VALUES ('Task 1', 'Description for task 1', 0, 0, 1, 1),
       ('Task 2', 'Description for task 2', 1, 1, 2, 2),
       ('Task 3', 'Description for task 3', 2, 0, 3, 3);

SELECT SETVAL('task_id_seq', (SELECT MAX(id) FROM task));


-- Insert test date in users task_assignee
INSERT INTO task_assignee (task_id, assignee_id)
VALUES (1, 2),
       (2, 3),
       (2, 1),
       (3, 1);


-- Insert test date in users comment
INSERT INTO comment (text, author, task_id, id)
VALUES ('Comment for task 1 by user 1', 1, 1, 1),
       ('Comment for task 2 by user 2', 2, 2, 2),
       ('Comment for task 3 by user 3', 3, 3, 3),
       ('Another comment for task 1 by user 2', 2, 1, 4);

SELECT SETVAL('comment_id_seq', (SELECT MAX(id) FROM comment));

