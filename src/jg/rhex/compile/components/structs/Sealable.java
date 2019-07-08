package jg.rhex.compile.components.structs;

/**
 * Represents an Rhex structure that can be "sealed" , as in no more
 * changes can be made to it.
 * @author Jose Guaro
 *
 */
public interface Sealable {
  
  /**
   * Seals the structure
   */
  public abstract void seal();
  
  /**
   * Checks if this structure is already sealed
   * @return true if this structure is already sealed
   *         false if else
   */
  public abstract boolean isSealed();

}
