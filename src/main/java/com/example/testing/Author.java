package com.example.testing;

public record Author(AuthorId id, String name) {
	public Author {
		if (id == null) {
			throw new NullPointerException("Required id is null");
		}
		if (name == null) {
			throw new NullPointerException("Required name is null");
		}
		if (name.isBlank()) {
			throw new IllegalArgumentException("Required name is blank");
		}
	}
}
