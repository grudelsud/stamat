package it.unifi.micc.homer.controller.namedentity;

import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;

import java.util.ArrayList;

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