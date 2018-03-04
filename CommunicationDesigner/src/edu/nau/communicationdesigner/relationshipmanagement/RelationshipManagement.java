package edu.nau.communicationdesigner.relationshipmanagement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.uml2.uml.Relationship;

public class RelationshipManagement {
	private List<RelationshipLink> relationshipLinks;
	
	public RelationshipManagement() {
		this.relationshipLinks = new ArrayList<RelationshipLink>();
	}
	
	public void addRelationshipLink(Relationship relationship, Node node) {
		boolean isPresent = false;
		int index = 0;
		
		while (
			index < relationshipLinks.size() 
			&& 
			!isPresent
		) {
			if (relationshipLinks.get(index).contains(relationship)) {
				isPresent = true;
			} else {
				++index;
			}
		}
		
		RelationshipLink relationshipLink;
		
		if (isPresent) {
			relationshipLink = relationshipLinks.get(index);
			relationshipLink.setTarget(node);
		} else {
			relationshipLink = new RelationshipLink(relationship, node);
			this.relationshipLinks.add(relationshipLink);
		}
	}
	
	public List<RelationshipLink> getRelationshipLinks() {
		return this.relationshipLinks;
	}
	
	public int relationshipLinkCount() {
		return this.relationshipLinks.size();
	}
}
