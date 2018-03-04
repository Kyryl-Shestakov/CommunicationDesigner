package edu.nau.communicationdesigner.relationshipmanagement;

import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.uml2.uml.Relationship;

public class RelationshipLink {
	private Node source;
	private Node target;
	private Relationship relationship;
	
	public RelationshipLink(
		Relationship relationship,
		Node source
	) {
		this.relationship = relationship;
		this.source = source;
	}
	
	public boolean hasTarget() {
		return this.target != null;
	}
	
	public void setTarget(Node target) {
		this.target = target;
	}
	
	public Node getTarget() {
		return this.target;
	}
	
	public Node getSource() {
		return this.source;
	}
	
	public Relationship getRelationship() {
		return this.relationship;
	}
	
	public boolean contains(Relationship relationship) {
		return this.relationship == relationship;
	}
}
