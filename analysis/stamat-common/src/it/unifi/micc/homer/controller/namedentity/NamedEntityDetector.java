package it.unifi.micc.homer.controller.namedentity;


import java.util.ArrayList;

import stamat.model.KeywordType;
import stamat.model.NamedEntity;

/**
 * 
 * @author alisi
 *
 */
public interface NamedEntityDetector {

	/** Find the Entity specified in type from the text */
	public abstract ArrayList<NamedEntity> extractEntity(String text, ArrayList<KeywordType> type);

	public abstract ArrayList<NamedEntity> getEntities();

}