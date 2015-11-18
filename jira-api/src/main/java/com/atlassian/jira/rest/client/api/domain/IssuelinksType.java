package com.atlassian.jira.rest.client.api.domain;

import com.google.common.base.Objects;

import java.net.URI;

/**
 * @since v0.5
 */
public class IssuelinksType extends AddressableNamedEntity {
	private final String id;
	private final String inward;
	private final String outward;

	public IssuelinksType(URI self, String id, String name, String inward, String outward) {
		super(self, name);
		this.id = id;
		this.inward = inward;
		this.outward = outward;
	}

	public String getId() {
		return id;
	}

	public String getInward() {
		return inward;
	}

	public String getOutward() {
		return outward;
	}

	@Override
	public String toString() {
		return getToStringHelper().
				add("id", id).
				add("inward", inward).
				add("outward", outward).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IssuelinksType) {
			IssuelinksType that = (IssuelinksType) obj;
			return super.equals(obj) && Objects.equal(this.id, that.id)
					&& Objects.equal(this.inward, that.inward)
					&& Objects.equal(this.outward, that.outward);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, name, inward, outward);
	}

}
