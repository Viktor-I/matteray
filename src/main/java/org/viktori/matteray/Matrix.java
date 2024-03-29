package org.viktori.matteray;

import org.viktori.matteray.function.MatrixIndexFunction;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * An ordered grid of arrays, with a fixed size in two dimensions and random access.
 * It is very similar to two-dimensional raw java array but gives you more methods to work with,
 * and can be used as a collection in itself or converted into multiple arrays of rows or columns.
 *
 * <p>Since matrices are of fixed size, any operation that affects the size of the
 * collection is prohibited, and will result in an {@link UnsupportedOperationException}.
 * These include, but is not limited to, {@code add} and {@code remove} methods.
 *
 * <p>Some matrix implementations have restrictions on the elements that
 * they may contain. For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements. Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * {@code NullPointerException} or {@code ClassCastException}. Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.
 *
 * <h2><a id="unmodifiable">Unmodifiable Matrices</a></h2>
 * <p>The {@link Matrix#of(Array...) Matrix.of} and
 * {@link Matrix#copyOf Matrix.copyOf} static factory methods
 * provide a convenient way to create unmodifiable matrices. The {@code Matrix}
 * instances created by these methods have the following characteristics:
 *
 * <ul>
 * <li>They are <a href="Collection.html#unmodifiable"><i>unmodifiable</i></a>. Elements cannot
 * replaced. Calling any mutator method on the Array
 * will always cause {@code UnsupportedOperationException} to be thrown.
 * However, if the contained elements are themselves mutable,
 * this may cause the Matrix's contents to appear to change.
 * <li>They disallow {@code null} elements. Attempts to create them with
 * {@code null} elements result in {@code NullPointerException}.
 * <li>They are serializable if all elements are serializable.
 * <li>The order of elements in the matrix is the same as the order of the
 * provided arguments, or of the elements in the provided raw two-dimensional array.
 * <li>The matrices and their {@link #subMatrix(int, int, int, int) subMatrix} views implement the
 * {@link RandomAccess} interface.
 * <li>They are <a href="../lang/doc-files/ValueBased.html">value-based</a>.
 * Programmers should treat instances that are {@linkplain #equals(Object) equal}
 * as interchangeable and should not use them for synchronization, or
 * unpredictable behavior may occur. For example, in a future release,
 * synchronization may fail. Callers should make no assumptions about the
 * identity of the returned instances. Factories are free to
 * create new instances or reuse existing ones.
 * </ul>
 *
 * <p>This interface is an extension upon the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements in this matrix
 *
 * @author Viktor Ingemansson
 * @see Collection
 * @see Array
 */
public interface Matrix<E> extends Collection<E> {

    // Positional Access Operation

    /**
     * Get the element at a given index in this list.
     *
     * @param rowIndex    the row index of the element to be returned
     * @param columnIndex the column index of the element to be returned
     * @return the element at (rowIndex, columnIndex) in this matrix
     * @throws ArrayIndexOutOfBoundsException if any index &lt; 0 || index &gt;= rows()/columns()
     */
    E get(int rowIndex, int columnIndex);

    // View

    /**
     * Returns a new matrix of the portion of this matrix between the specified
     * {@code fromRow}, inclusive, and {@code toRow}, exclusive, as well as the specified
     * {@code fromColumn}, inclusive, and {@code toColumn}, exclusive. (If
     * {@code fromRow} and {@code toRow} are equal, or {@code fromColumn and @toColumn} are equal
     * , the returned matrix is empty.)
     *
     * @param fromRow    low row endpoint (inclusive) of the subMatrix
     * @param toRow      high row endpoint (exclusive) of the subMatrix
     * @param fromColumn low column endpoint (inclusive) of the subMatrix
     * @param toColumn   high row endpoint (exclusive) of the subMatrix
     * @return a view of the specified rectangle within this matrix
     * @throws ArrayIndexOutOfBoundsException for an illegal endpoint index value
     *                                        ({@code fromIndex < 0 || toIndex > rows/columns})
     * @throws IllegalArgumentException       if the endpoint indices are out of order
     *                                        {@code (fromIndex > toIndex)}
     */
    Matrix<E> subMatrix(int fromRow, int toRow, int fromColumn, int toColumn);

    /**
     * Returns an array containing all of the elements in this matrix for
     * a specified row index, in proper column sequence (from first to last element).
     *
     * <p>The returned array will act as a view into the current matrix.
     * (In other words, if this matrix is mutable, changes must be reflected in the
     * returned array).
     *
     * @param rowIndex the row index of the row to be returned
     * @return an array containing all of the elements of this row in the matrix in proper
     * column sequence
     */
    Array<E> row(int rowIndex);

    /**
     * Returns an array containing all of the elements in this matrix for
     * a specified column index, in proper row sequence (from first to last element).
     *
     * <p>The returned array will act as a view into the current matrix.
     * (In other words, if this matrix is mutable, changes must be reflected in the
     * returned array).
     *
     * @param columnIndex the column index of the row to be returned
     * @return an array containing all of the elements of this column in the matrix in proper
     * row sequence
     */
    Array<E> column(int columnIndex);

    /**
     * Returns an array containing all rows of elements in this matrix,
     * in proper order.
     *
     * <p>The returned array will act as a view into the current matrix.
     * (In other words, if this matrix is mutable, changes must be reflected in the
     * returned array).
     *
     * @return an array containing all rows of elements in the matrix
     */
    Array<Array<E>> rowArray();

    /**
     * Returns an array containing all columns of elements in this matrix,
     * in proper order.
     *
     * <p>The returned array will act as a view into the current matrix.
     * (In other words, if this matrix is mutable, changes must be reflected in the
     * returned array).
     *
     * @return an array containing all columns of elements in the matrix
     */
    Array<Array<E>> columnArray();

    // Query operations

    /**
     * Returns the number of elements in this matrix.  If this matrix
     * contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this matrix
     */
    int size();

    /**
     * Returns the number of rows in this matrix.
     *
     * @return the number of rows in this matrix
     */
    int rows();

    /**
     * Returns the number of columns in this matrix.
     *
     * @return the number of columns in this matrix
     */
    int columns();

    /**
     * Returns {@code true} if this matrix is a square matrix. It is a square matrix if
     * the number of rows equals the number of columns. (If rows == columns).
     *
     * @return {@code true} if this matrix has the same amount of rows as columns
     */
    boolean isSquare();

    /**
     * Returns an array containing all of the elements in this matrix.
     * If this matrix makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order. The returned array's {@linkplain Class#getComponentType
     * runtime component type} is {@code Object}.
     *
     * <p>Note that the returned array is not two-dimensional and will thus return all
     * elements in all rows. Use {@link #toArray2D()} for a two-dimensional array.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this matrix.  (In other words, this method must
     * allocate a new array even if this matrix is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * @return an array, whose {@linkplain Class#getComponentType runtime component
     * type} is {@code Object}, containing all of the elements in this matrix
     * @apiNote This method acts as a bridge between array-based and collection-based APIs.
     * It returns an array whose runtime type is {@code Object[]}.
     * Use {@link #toArray(Object[]) toArray(T[])} to reuse an existing
     * array, or use {@link #toArray(IntFunction)} to control the runtime type
     * of the array.
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this matrix;
     * the runtime type of the returned array is that of the specified array.
     * If the matrix fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this matrix.
     *
     * <p>If this matrix fits in the specified array with room to spare
     * (i.e., the array has more elements than this matrix), the element
     * in the array immediately following the end of the matrix is set to
     * {@code null}.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this matrix does
     * not contain any {@code null} elements.)
     *
     * <p>If this matrix makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Note that the returned array is not two-dimensional and will thus return all
     * elements in all rows. Use {@link #toArray2D(Object[][])} for a two-dimensional array.
     *
     * @param <T> the component type of the array to contain the matrix
     * @param a   the array into which the elements of this matrix are to be
     *            stored, if it is big enough; otherwise, a new array of the same
     *            runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this matrix
     * @throws ArrayStoreException  if the runtime type of any element in this
     *                              matrix is not assignable to the {@linkplain Class#getComponentType
     *                              runtime component type} of the specified array
     * @throws NullPointerException if the specified array is null
     * @apiNote This method acts as a bridge between array-based and collection-based APIs.
     * It allows an existing array to be reused under certain circumstances.
     * Use {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(IntFunction)} to control the runtime type of
     * the array.
     *
     * <p>Suppose {@code x} is a matrix known to contain only strings.
     * The following code can be used to dump the matrix into a previously
     * allocated {@code String} array:
     *
     * <pre>
     *     String[] y = new String[SIZE];
     *     ...
     *     y = x.toArray(y);</pre>
     *
     * <p>The return value is reassigned to the variable {@code y}, because a
     * new array will be allocated and returned if the matrix {@code x} has
     * too many elements to fit into the existing array {@code y}.
     *
     * <p>Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     */
    <T> T[] toArray(T[] a);

    /**
     * Returns an array containing all of the elements in this collection,
     * using the provided {@code generator} function to allocate the returned array.
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Note that the returned array is not two-dimensional and will thus return all
     * elements in all rows. Use {@link #toArray2D(IntFunction)} for a two-dimensional array.
     *
     * @param <T>       the component type of the array to contain the collection
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException  if the runtime type of any element in this
     *                              matrix is not assignable to the {@linkplain Class#getComponentType
     *                              runtime component type} of the generated array
     * @throws NullPointerException if the generator function is null
     * @apiNote This method acts as a bridge between array-based and collection-based APIs.
     * It allows creation of an array of a particular runtime type. Use
     * {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(Object[]) toArray(T[])} to reuse an existing array.
     *
     * <p>Suppose {@code x} is a matrix known to contain only strings.
     * The following code can be used to dump the matrix into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(String[]::new);</pre>
     * @implSpec The default implementation calls the generator function with zero
     * and then passes the resulting array to {@link #toArray(Object[]) toArray(T[])}.
     */
    default <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(0));
    }

    /**
     * Returns a two-dimensional array containing all of the elements in this matrix.
     * The outer array contains the rows and the inner array each column for every row.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this matrix.  (In other words, this method must
     * allocate a new array even if this matrix is backed by an array).
     * The caller is thus free to modify the returned two-dimensional array.
     *
     * @return a row-based array with column-arrays inside, whose {@linkplain Class#getComponentType runtime component
     * type} is {@code Object}, containing all of the elements in this matrix
     */
    Object[][] toArray2D();

    /**
     * Returns a two-dimensional array containing all of the elements in this matrix.
     * The outer array contains the rows and the inner array each column for every row.
     *
     * <p>If this matrix fits in the specified array with room to spare
     * (i.e., the array has more elements than this matrix), the element
     * in the array immediately following the end of the matrix is set to
     * {@code null}.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this matrix does
     * not contain any {@code null} elements.)
     *
     * <p>If this matrix makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Suppose {@code x} is a matrix known to contain only strings.
     * The following code can be used to dump the matrix into a previously
     * allocated {@code String} two-dimensional-array:
     *
     * <pre>
     *     String[][] y = new String[ROWS][COLUMNS];
     *     ...
     *     y = x.toArray2D(y);</pre>
     *
     * <p>The return value is reassigned to the variable {@code y}, because a
     * new two-dimensional array will be allocated and returned if the matrix {@code x} has
     * too many elements to fit into the existing two-dimensional array {@code y}.
     *
     * <p>Note that {@code toArray2D(new Object[0][0])} is identical in function to
     * {@code toArray2D()}.
     *
     * @param <T> the component type of the two-dimensional array to contain the matrix
     * @param a   the two-dimensional array into which the elements of this matrix are to be
     *            stored, if it is big enough; otherwise, a new two-dimensional array of the same
     *            runtime type is allocated for this purpose.
     * @return a row-based array with column-arrays inside containing all of the elements in this matrix
     * @throws ArrayStoreException  if the runtime type of any element in this
     *                              matrix is not assignable to the {@linkplain Class#getComponentType
     *                              runtime component type} of the specified array
     * @throws NullPointerException if the specified array is null
     */
    <T> T[][] toArray2D(T[][] a);

    /**
     * Returns a two-dimensional array containing all of the elements in this collection,
     * using the provided {@code generator} function to allocate the returned two-dimensional array.
     * The outer array contains the rows and the inner array each column for every row.
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Suppose {@code x} is a matrix known to contain only strings.
     * The following code can be used to dump the matrix into a newly
     * allocated two-dimensional array of {@code String}:
     *
     * <pre>
     *     String[][] y = x.toArray2D(String[][]::new);</pre>
     *
     * @param <T>       the component type of the array to contain the matrix
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length
     * @return an array containing all of the elements in this matrix
     * @throws ArrayStoreException  if the runtime type of any element in this
     *                              matrix is not assignable to the {@linkplain Class#getComponentType
     *                              runtime component type} of the generated array
     * @throws NullPointerException if the generator function is null
     * @implSpec The default implementation calls the generator function with zero
     * and then passes the resulting array to {@link #toArray2D(Object[][]) toArray2D(T[][])}.
     */
    default <T> T[][] toArray2D(IntFunction<T[][]> generator) {
        return toArray2D(generator.apply(0));
    }

    /**
     * Returns an iterator over the elements in this matrix. The elements are
     * returned ordered by row index first, then column index for each row.
     * I.e. it finishes one row before it continues with the next one.
     *
     * @return an {@code Iterator} over the elements in this matrix
     */
    Iterator<E> iterator();

    // Comparison and hashing

    /**
     * Compares the specified object with this matrix for equality.  Returns
     * {@code true} if and only if the specified object is also a matrix
     * implementing this interface, both matrices have the same size, and all
     * corresponding pairs of elements in the two matrices are <i>equal</i>.
     * (Two elements {@code e1} and {@code e2} are <i>equal</i> if
     * {@code Objects.equals(e1, e2)}.). In other words, two matrices are defined to be
     * equal if they contain the same elements in the same order.  This
     * definition ensures that the equals method works properly across
     * different implementations of the {@code Matrix} interface.
     *
     * @param o the object to be compared for equality with this matrix
     * @return {@code true} if the specified object is equal to this matrix
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this matrix.  The hash code of an array
     * is defined to be the result of the following calculation:
     * <pre>{@code
     *     int hashCode = 1;
     *     for (Array elements : rows)
     *         for (E e : elements)
     *             hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
     * }</pre>
     * This ensures that {@code matrix1.equals(matrix2)} implies that
     * {@code matrix1.hashCode()==matrix2.hashCode()} for any two matrices,
     * {@code matrix1} and {@code matrix2}, as required by the general
     * contract of {@link Object#hashCode}.
     *
     * @return the hash code value for this matrix
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();

    /**
     * Adding an object to a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean add(E e) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adding objects to a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param c – collection containing elements to be added to this collection
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing an object from a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param o element to be removed from this collection, if present
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing objects from a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param filter a predicate which returns {@code true} for elements to be removed
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing objects from a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param c collection containing elements to be removed from this collection
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing objects from a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @param c collection containing elements to be retained in this collection
     * @return nothing, as it will always throw an exception
     * @throws UnsupportedOperationException always
     */
    @Override
    default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing objects from a fixed size matrix is not supported, but it is part of the
     * {@link Collection} API. This action will always throw an {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    default void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an immutable matrix containing zero elements.
     *
     * @param <E> the {@code Matrix}'s element type
     * @return an empty {@code Matrix}
     */
    static <E> Matrix<E> of() {
        return new ImmutableMatrix<>();
    }

    /**
     * Returns an immutable matrix containing one row of non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param row1 the single row of elements
     * @return a {@code Matrix} containing the specified element
     * @throws NullPointerException if the row or an element is {@code null}
     */
    static <E> Matrix<E> of(Array<E> row1) {
        return new ImmutableMatrix<>(requireNonNull(row1));
    }

    /**
     * Returns an immutable matrix containing two rows of non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param row1 the first row of elements
     * @param row2 the second row of elements
     * @return a {@code Matrix} containing the specified elements
     * @throws NullPointerException     if a row or an element is {@code null}
     * @throws IllegalArgumentException if the rows are of different size
     */
    static <E> Matrix<E> of(Array<E> row1, Array<E> row2) {
        return new ImmutableMatrix<>(requireNonNull(row1, row2));
    }

    /**
     * Returns an immutable matrix containing three non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param row1 the first row of elements
     * @param row2 the second row of elements
     * @param row3 the third row elements
     * @return a {@code Matrix} containing the specified elements
     * @throws NullPointerException     if a row or an element is {@code null}
     * @throws IllegalArgumentException if the rows are of different size
     */
    static <E> Matrix<E> of(Array<E> row1, Array<E> row2, Array<E> row3) {
        return new ImmutableMatrix<>(requireNonNull(row1, row2, row3));
    }

    /**
     * Returns an immutable matrix containing four non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param row1 the first row of elements
     * @param row2 the second row of elements
     * @param row3 the third row of elements
     * @param row4 the fourth row of elements
     * @return a {@code Matrix} containing the specified elements
     * @throws NullPointerException     if a row or an element is {@code null}
     * @throws IllegalArgumentException if the rows are of different size
     */
    static <E> Matrix<E> of(Array<E> row1, Array<E> row2, Array<E> row3, Array<E> row4) {
        return new ImmutableMatrix<>(requireNonNull(row1, row2, row3, row4));
    }

    /**
     * Returns an immutable matrix containing five non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param row1 the first row of elements
     * @param row2 the second row of elements
     * @param row3 the third row of elements
     * @param row4 the fourth row of elements
     * @param row5 the fifth row of elements
     * @return a {@code Matrix} containing the specified elements
     * @throws NullPointerException     if a row or an element is {@code null}
     * @throws IllegalArgumentException if the rows are of different size
     */
    static <E> Matrix<E> of(Array<E> row1, Array<E> row2, Array<E> row3, Array<E> row4, Array<E> row5) {
        return new ImmutableMatrix<>(requireNonNull(row1, row2, row3, row4, row5));
    }

    /**
     * Returns an immutable matrix containing an arbitrary number of rows of non-null elements.
     *
     * @param <E>  the {@code Matrix}'s element type
     * @param rows the rows of elements to be contained in the matrix
     * @return a {@code Matrix} containing the specified elements
     * @throws NullPointerException     if a row or an element is {@code null}
     * @throws IllegalArgumentException if the rows are of different size
     */
    @SafeVarargs
    static <E> Matrix<E> of(Array<E>... rows) {
        return new ImmutableMatrix<>(requireNonNull(rows));
    }

    /**
     * Returns an immutable square matrix with the specified length (width and height), and function
     * to populate values with.
     *
     * @param length       the row and column count the matrix (i.e its height and width)
     * @param initFunction the function to initialize values in the matrix
     * @return a {@code Matrix} of size {@code length}&times;{@code length}
     *         containing the elements given by the init function
     * @throws NullPointerException     if an element is {@code null} or if the initFunction is {@code null}
     * @throws IllegalArgumentException if the specified length is negative
     */
    static <E> Matrix<E> of(int length, MatrixIndexFunction<E> initFunction) {
        return requireNonNull(new ImmutableMatrix<>(length, initFunction));
    }

    /**
     * Returns an immutable matrix with the specified length (width and height), and function
     * to populate values with. When given explicit rows and columns like this, it is allowed to
     * create a matrix with multiple rows and zero columns, or vice versa.
     *
     * @param rows         row count in the matrix (i.e. its height)
     * @param columns      column count in the matrix (i.e. its width)
     * @param initFunction the function to initialize values in the matrix
     * @return a {@code Matrix} of size {@code rows}&times;{@code columns}
     *         containing the elements given by the init function
     * @throws NullPointerException     if an element is {@code null} or if the initFunction is {@code null}
     * @throws IllegalArgumentException if the specified rows, or specified columns is negative
     */
    static <E> Matrix<E> of(int rows, int columns, MatrixIndexFunction<E> initFunction) {
        return requireNonNull(new ImmutableMatrix<>(rows, columns, initFunction));
    }

    /**
     * Returns an immutable matrix with a single row.
     *
     * @param rowArray array to get row values from
     * @return a {@code Matrix} of size {@code 1}&times;{@code rowArray.size}
     *         containing the elements of the given row
     * @throws NullPointerException if an element is {@code null}
     */
    static <E> Matrix<E> fromRow(Array<E> rowArray) {
        return of(1, rowArray.size(), (r, c) -> rowArray.get(c));
    }

    /**
     * Returns an immutable matrix with a single column.
     *
     * @param columnArray array to get column values from
     * @return a {@code Matrix} of size {@code columnArray.size}&times;{@code 1}
     *         containing the elements of the given column
     * @throws NullPointerException if an element is {@code null}
     */
    static <E> Matrix<E> fromColumn(Array<E> columnArray) {
        return of(columnArray.size(), 1, (r, c) -> columnArray.get(r));
    }

    /**
     * Returns an immutable matrix containing the elements of
     * the given matrix, in its iteration order. The given Collection must not be null,
     * and it must not contain any null elements. If the given Collection is subsequently
     * modified, the returned Matrix will not reflect such modifications.
     *
     * @implNote If the given Matrix is an immutable matrix,
     * calling copyOf may return the same instance, as it is safe for reuse.
     *
     * @param <E>    the {@code Matrix}'s element type
     * @param matrix a {@code Matrix} from which elements are drawn, must be non-null
     * @return an {@code Matrix} containing the elements of the given {@code Matrix}
     * @throws NullPointerException if matrix is null, or if it contains any nulls
     */
    static <E> Matrix<E> copyOf(Matrix<E> matrix) {
        if (matrix instanceof ImmutableMatrix<E> ia) {
            return requireNonNull(ia);
        }
        return new ImmutableMatrix<>(requireNonNull(matrix));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <E> Array<E>[] requireNonNull(Array<E>... elementRows) {
        for (Array<?> row : Objects.requireNonNull(elementRows)) {
            requireNonNull((Collection<E>) row);
        }
        return elementRows;
    }

    private static <C extends Collection<E>, E> C requireNonNull(C coll) {
        Objects.requireNonNull(coll);
        for (Object e : coll) {
            Objects.requireNonNull(e);
        }
        return coll;
    }

    /**
     * Matrix rotation.
     *
     * <p>Following options exist:
     * <ul>
     *     <li>{@link #NONE} - no rotation.</li>
     *     <li>{@link #LEFT} - rotate left.</li>
     *     <li>{@link #HALF} - rotate halfway.</li>
     *     <li>{@link #RIGHT} - rotate right.</li>
     * </ul>
     */
    enum Rotation {
        /**
         * No rotation, 0&deg;. (rowIndex = rowIndex, columnIndex = columnIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [4, 5, 6]<br>
         * [4, 5, 6] -> [4, 5, 6]<br>
         * [7, 8, 9]    [7, 8. 9]<br>
         * </pre>
         */
        NONE,

        /**
         * Left rotation, -90&deg;. (rowIndex = columns - 1 - columnIndex, columnIndex = rowIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [3, 6, 9]<br>
         * [4, 5, 6] -> [2, 5, 8]<br>
         * [7, 8, 9]    [1, 4. 7]<br>
         * </pre>
         */
        LEFT,

        /**
         * Halfway rotation, &plusmn;180&deg;. (rowIndex = rows - 1 - rowIndex, columnIndex = columns - 1 - columnIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [9, 8, 7]<br>
         * [4, 5, 6] -> [6, 5, 4]<br>
         * [7, 8, 9]    [3, 2. 1]<br>
         * </pre>
         */
        HALF,

        /**
         * Right rotation, +90&deg;. (rowIndex = columnIndex, columnIndex = rows - 1 - rowIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [7, 4, 1]<br>
         * [4, 5, 6] -> [8, 5, 2]<br>
         * [7, 8, 9]    [9, 6. 3]<br>
         * </pre>
         */
        RIGHT
    }

    /**
     * Matrix axis, which can be used for mirroring/reflection.
     *
     * <p>Following options exist:
     * <ul>
     *     <li>{@link #ROWS} - mirror rows.</li>
     *     <li>{@link #COLUMNS} - mirror columns.</li>
     * </ul>
     */
    enum Axis {
        /**
         * Mirror rows, i.e. inverse column index. (rowIndex = rowIndex, columnIndex = columns - 1 - columnIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [3, 2, 1]<br>
         * [4, 5, 6] -> [6, 5, 3]<br>
         * [7, 8, 9]    [9, 8. 7]<br>
         * </pre>
         */
        ROWS,

        /**
         * Mirror columns, i.e. inverse row index. (rowIndex = rows - 1 - rowIndex, columnIndex = columnIndex)
         *
         * <p>Example:<br>
         * <pre>
         * [1, 2, 3]    [7, 8, 9]<br>
         * [4, 5, 6] -> [4, 5, 6]<br>
         * [7, 8, 9]    [1, 2. 3]<br>
         * </pre>
         */
        COLUMNS,
    }

    /**
     * Iterator implementation for matrices
     * @param <E> element type
     */
    final class MatrixIterator<E> implements Iterator<E> {
        private final Matrix<E> matrix;
        int row = 0;
        int column = 0;

        MatrixIterator(final Matrix<E> matrix) {
            this.matrix = matrix;
        }

        @Override
        public boolean hasNext() {
            return row < matrix.rows() && matrix.columns() > 0;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            E next = matrix.get(row, column);
            if (column < matrix.columns()) {
                column++;
                if (column == matrix.columns()) {
                    column = 0;
                    row++;
                }
            }
            return next;
        }
    }
}
