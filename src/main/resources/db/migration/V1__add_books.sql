CREATE TABLE books
(
	id      UUID                   NOT NULL,
	isbn_13 CHARACTER VARYING(255) NOT NULL,
	title   CHARACTER VARYING(255) NOT NULL,
	CONSTRAINT pkey_books PRIMARY KEY (id)
);

INSERT INTO books(id, isbn_13, title)
VALUES ('b98f18a1-74df-456b-9a24-1632ee44df54'::uuid, '9780134686097', 'Effective Java, 3rd Edition'),
	   ('28f33f57-44b3-4553-9daa-d262123b6f49'::uuid, '9781491910771', 'Head First Java, 3rd Edition');
