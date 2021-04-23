/**
 * Represents an object that has a managed life cycle. This allows for the use of the observer
 * pattern so that functionality can be specified on the object's close.
 */
public interface ManagedLifeCycle extends Closeable {

  /**
   * Adds an action that should be implemented when the resource is closed.
   * @param action The action that should be taken when the resource is closed.
   */
  void addOnCloseAction(Runnable action);

}
