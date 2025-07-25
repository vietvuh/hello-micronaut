package vvu.centrauthz.utilities;

public interface SupplierWithThrowable<T, E extends Throwable> {
    T get() throws E;
}
