CREATE TABLE authors
(
	id   UUID                   NOT NULL,
	name CHARACTER VARYING(255) NOT NULL,
	CONSTRAINT pkey_authors PRIMARY KEY (id)
);

CREATE TABLE authors_to_books
(
	author_id uuid NOT NULL,
	book_id   uuid NOT NULL,
	CONSTRAINT ukey_a2b_author_id_book_id UNIQUE (author_id, book_id),
	CONSTRAINT fkey_a2b_author_id FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE,
	CONSTRAINT fkey_a2b_book_id FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
);

INSERT INTO authors(id, name)
VALUES ('1a0f9c80-1309-4e9d-a291-752354b51c51'::uuid, 'Joshua Bloch'),
	   ('990a2c15-dc5b-4904-8bdd-5aacd1e2e456'::uuid, 'Kathy Sierra'),
	   ('917acb74-b159-4471-95b0-8fc4c3d659ba'::uuid, 'Bert Bates'),
	   ('34cdcade-ad4a-4530-b0bf-c6b5abb25e3f'::uuid, 'Trisha Gee');

INSERT INTO authors_to_books(author_id, book_id)
VALUES ('1a0f9c80-1309-4e9d-a291-752354b51c51'::uuid, 'b98f18a1-74df-456b-9a24-1632ee44df54'::uuid),
	   ('990a2c15-dc5b-4904-8bdd-5aacd1e2e456'::uuid, '28f33f57-44b3-4553-9daa-d262123b6f49'::uuid),
	   ('917acb74-b159-4471-95b0-8fc4c3d659ba'::uuid, '28f33f57-44b3-4553-9daa-d262123b6f49'::uuid),
	   ('34cdcade-ad4a-4530-b0bf-c6b5abb25e3f'::uuid, '28f33f57-44b3-4553-9daa-d262123b6f49'::uuid);
