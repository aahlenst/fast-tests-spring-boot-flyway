package com.example.testing;

import java.util.UUID;

public record AuthorId(UUID id) {

	public AuthorId {
		if (id == null) {
			throw new NullPointerException("Required id is null");
		}
	}

	public static AuthorId from(String uuid) {
		return new AuthorId(UUID.fromString(uuid));
	}
}
