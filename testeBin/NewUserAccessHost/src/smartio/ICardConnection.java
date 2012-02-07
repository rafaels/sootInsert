package smartio;

/** A generic interface to be implemented 
 *  
 * @author bruno
 *
 */
public interface ICardConnection {
	/** Procedures for card initialisation */
	public void cardInit();
	
	/** Treatment of card*/
	public void cardInserted();
	
	/** Cleanup operations when card removal */
	public void cardRemoved();
	
	/** A reference to an object that represents
	 * 	the card instance
	 */
	public Object getCardReference();
	
}
