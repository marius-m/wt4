/*
 * Copyright (C) 2012 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.NoSuchElementException;

/**
 * Helper class for entities.
 *
 * @since v1.0
 */
public class EntityHelper {

	public static Function<IdentifiableEntity<String>, String> GET_ENTITY_STRING_ID_FUNCTION = new Function<IdentifiableEntity<String>, String>() {
		@Override
		public String apply(IdentifiableEntity<String> entity) {
			return entity.getId();
		}
	};

	public static Function<NamedEntity, String> GET_ENTITY_NAME_FUNCTION = new Function<NamedEntity, String>() {
		@Override
		public String apply(NamedEntity entity) {
			return entity.getName();
		}
	};

	public static Iterable<String> toNamesList(Iterable<? extends NamedEntity> items) {
		return Iterables.transform(items, GET_ENTITY_NAME_FUNCTION);
	}

	public static Iterable<String> toFileNamesList(Iterable<? extends Attachment> attachments) {
		return Iterables.transform(attachments, new Function<Attachment, String>() {
			@Override
			public String apply(Attachment a) {
				return a.getFilename();
			}
		});
	}

	@SuppressWarnings("unused")
	public static <T> Iterable<String> toStringIdList(Iterable<IdentifiableEntity<T>> items) {
		return Iterables.transform(items, new Function<IdentifiableEntity<T>, String>() {
			@Override
			public String apply(IdentifiableEntity<T> from) {
				return from.getId() == null ? null : from.getId().toString();
			}
		});
	}

	public static <T extends NamedEntity> T findEntityByName(Iterable<T> entities, final String name) {
		try {
			return Iterables.find(entities, HasNamePredicate.forName(name));
		} catch (NoSuchElementException ex) {
			throw new NoSuchElementException(String.format("Entity with name \"%s\" not found. Entities: %s", name, entities
					.toString()));
		}
	}

	@SuppressWarnings("unused")
	public static <T extends IdentifiableEntity<K>, K> T findEntityById(Iterable<T> entities, final K id) {
		try {
			return Iterables.find(entities, HasIdPredicate.forId(id));
		} catch (NoSuchElementException ex) {
			throw new NoSuchElementException(String.format("Entity with id \"%s\" not found. Entities: %s", id, entities
					.toString()));
		}
	}

	public static <T extends Attachment> T findAttachmentByFileName(Iterable<T> attachments, final String fileName) {
		return Iterables.find(attachments, HasFileNamePredicate.forFileName(fileName));
	}

	public static class HasFileNamePredicate<T extends Attachment> implements Predicate<T> {

		private final String fileName;

		public static <K extends Attachment> HasFileNamePredicate<K> forFileName(String fileName) {
			return new HasFileNamePredicate<K>(fileName);
		}

		private HasFileNamePredicate(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public boolean apply(T attachment) {
			return fileName.equals(attachment.getFilename());
		}
	}


	public static class HasNamePredicate<T extends NamedEntity> implements Predicate<T> {

		private final String name;

		public static <K extends NamedEntity> HasNamePredicate<K> forName(String name) {
			return new HasNamePredicate<K>(name);
		}

		private HasNamePredicate(String name) {
			this.name = name;
		}

		@Override
		public boolean apply(T input) {
			return name.equals(input.getName());
		}
	}

	public static class HasIdPredicate<T extends IdentifiableEntity<K>, K> implements Predicate<T> {

		private final K id;

		public static <X extends IdentifiableEntity<Y>, Y> HasIdPredicate<X, Y> forId(Y id) {
			return new HasIdPredicate<X, Y>(id);
		}

		private HasIdPredicate(K id) {
			this.id = id;
		}

		@Override
		public boolean apply(T input) {
			return id.equals(input.getId());
		}
	}

	public static class AddressEndsWithPredicate implements Predicate<AddressableEntity> {

		private final String stringEnding;

		public AddressEndsWithPredicate(String stringEnding) {
			this.stringEnding = stringEnding;
		}

		@Override
		public boolean apply(final AddressableEntity input) {
			return input.getSelf().getPath().endsWith(stringEnding);
		}
	}
}
