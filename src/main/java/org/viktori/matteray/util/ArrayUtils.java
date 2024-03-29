package org.viktori.matteray.util;

import org.viktori.matteray.Array;
import org.viktori.matteray.ImmutableArray;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class consists exclusively of static methods that operate on or return
 * arrays. It contains polymorphic algorithms that operate on
 * arrays, "wrappers", which return a new array backed by a
 * specified array, and a few other odds and ends. It can be seen as an
 * extension of the {@link Collections class}, for arrays.
 *
 * <p>The methods of this class all throw a {@code NullPointerException}
 * if the arrays or class objects provided to them are null.
 *
 * @author Viktor Ingemansson
 * @see Collections
 * @see Array
 */
public final class ArrayUtils {

    private ArrayUtils() {
        // static class
    }

    /**
     * Apply a function on each element of an array and return a new immutable array with the result.
     * There is no strict type requirement, so it is possible to return a different type. This works
     * similar to the {@link Stream#map} function.
     *
     * @param array    array to apply the function on
     * @param function function to apply on each element
     * @return a new immutable array based on the result of the function
     * @throws NullPointerException if the array or the function is null
     */
    public static <E, R> Array<R> applyForEach(Array<E> array, Function<E, R> function) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(function);
        return new ImmutableArray<>(array.size(), i -> function.apply(array.get(i)));
    }

    /**
     * Merge two arrays of equal size into a new immutable array with the result of the merge.
     * There is no strict requirements regarding type, so it is possible to have two arrays of
     * different type returning a third type. If the arrays are of different size, the resulting
     * array will be based on the size of the shortest array,
     * i.e. {@code min(array1.size(), array2.size())}.
     *
     * @param array1        first array to merge
     * @param array2        second array to merge
     * @param mergeFunction function to apply on each pair of elements
     * @return a new immutable array based on the result of the merge function
     * @throws NullPointerException if any of the arrays, or the function is null
     */
    public static <E1, E2, R> Array<R> mergeForEach(Array<E1> array1, Array<E2> array2, BiFunction<E1, E2, R> mergeFunction) {
        Objects.requireNonNull(array1);
        Objects.requireNonNull(array2);
        Objects.requireNonNull(mergeFunction);
        return new ImmutableArray<>(Math.min(array1.size(), array2.size()), i -> mergeFunction.apply(array1.get(i), array2.get(i)));
    }

    /**
     * Sort a comparable array according to its natural order.
     *
     * @param array array to sort
     * @return a new immutable array, sorted by its natural order
     */
    public static <E extends Comparable<E>> Array<E> toSorted(Array<E> array) {
        Object[] rawArray = array.toArray();
        Arrays.sort(rawArray);
        return new SortedImmutableArray<>(rawArray);
    }

    /**
     * Sort a comparable array based on a comparator.
     *
     * @param array array to sort
     * @param comparator comparator to sort with
     * @return a new immutable array, sorted according to comparator
     */
    @SuppressWarnings("unchecked")
    public static <E> Array<E> toSorted(Array<E> array, Comparator<? super E> comparator) {
        Object[] rawArray = array.toArray();
        Arrays.sort(rawArray, (Comparator<Object>) Objects.requireNonNull(comparator));
        return new SortedImmutableArray<>(rawArray);
    }

    private static class SortedImmutableArray<E> extends ImmutableArray<E> {
        private SortedImmutableArray(Object[] elements) {
            super(elements, true);
        }
    }

    /**
     * Return a new array where the indices are reversed.
     *
     * <p>Example: [15, 20, 10] -> [10, 20, 15]
     *
     * @param array array to reverse
     * @return a new immutable array where indices are reversed
     */
    public static <E extends Comparable<E>> Array<E> toReversed(Array<E> array) {
        return new ImmutableArray<>(array.size(), i -> array.get(array.size() - 1 - i));
    }

    /**
     * Return a new array based on the mapping function for each index.
     *
     * @param array array to map
     * @param mappingFunction function to map each element with
     * @return a new immutable array based on the mapping function
     */
    public static <E1, E2> Array<E2> toMapped(Array<E1> array, Function<E1, E2> mappingFunction) {
        return new ImmutableArray<>(array.size(), i -> mappingFunction.apply(array.get(i)));
    }

    /**
     * Return a new array with the result based of an accumulator function. This could be used to
     * calculate things like min, max, sum, or concat. It works similarly
     * to {@link Stream#reduce} but it never creates a stream. Null values are also supported but
     * will be ignored in the calculation. An identity value can be provided for the case when the
     * array is empty, or when it only contain {@code null} values.
     *
     * @param array array to aggregate
     * @param accumulator function to accumulate values with
     * @param identity value to return if there is nothing to aggregate
     * @return the aggregated result, or identity if array contains no values to aggregate
     */
    public static <E> E aggregate(Array<E> array, BinaryOperator<E> accumulator, E identity) {
        E current = null;
        for (E element : array) {
            if (element != null) {
                if (current == null) {
                    current = element;
                } else {
                    current = accumulator.apply(current, element);
                }
            }
        }
        return current != null? current : identity;
    }

    /**
     * Return a new array with the result based of an accumulator function. This could be used to
     * calculate things like min, max, sum, or concat. It works similarly to {@link Stream#reduce}
     * but it never creates a stream. {@code Null} values are ignored in the calculation.
     * {@code Optional.empty()} is returned when array is empty or only contain {@code null} values.
     *
     * @param array array to aggregate
     * @param accumulator function to accumulate values with
     * @return the aggregated result, or {@code Optional.empty()} if array contains no values to aggregate
     */
    public static <E> Optional<E> aggregate(Array<E> array, BinaryOperator<E> accumulator) {
       return Optional.ofNullable(aggregate(array, accumulator, null));
    }

    /**
     * Return a new array representing the dot product between two arrays. The dot product is
     * calculated by multiplying each pair of values and then summing them.
     *
     * @param vector1 first vector
     * @param vector2 second vector
     * @param productFunction function to calculate the product of two values, i.e. (x, y) -> x * y
     * @param sumFunction function to calculate a sum of two values, i.e. (x, y) -> x + y
     * @return the dot product of the array
     * @throws IllegalArgumentException if vectors are of different size, or empty
     */
    public static <E> E dotProduct(Array<E> vector1, Array<E> vector2, BinaryOperator<E> productFunction, BinaryOperator<E> sumFunction) {
        Objects.requireNonNull(vector1);
        Objects.requireNonNull(vector2);
        Objects.requireNonNull(productFunction);
        Objects.requireNonNull(sumFunction);
        validateArraysOfEqualSize(vector1, vector2);
        validateArraysNotEmptyWhenNoIdentityProvided(vector1, vector2);

        E result = null;
        for (int i = 0; i < vector1.size(); i++) {
            E product = productFunction.apply(vector1.get(i), vector2.get(i));
            result = i == 0 ? product : sumFunction.apply(result, product);
        }
        return result;
    }

    /**
     * Return a new array representing the dot product between two arrays. The dot product is
     * calculated by multiplying each pair of values and then summing them.
     *
     * @param vector1 first vector
     * @param vector2 second vector
     * @param productFunction function to calculate the product of two values, i.e. (x, y) -> x * y
     * @param sumFunction function to calculate a sum of two values, i.e. (x, y) -> x + y
     * @param identity value to return if the arrays are empty
     * @return the dot product of the array
     * @throws IllegalArgumentException if vectors are of different size
     */
    public static <E> E dotProduct(Array<E> vector1, Array<E> vector2, BinaryOperator<E> productFunction, BinaryOperator<E> sumFunction, E identity) {
        Objects.requireNonNull(vector1);
        Objects.requireNonNull(vector2);
        Objects.requireNonNull(productFunction);
        Objects.requireNonNull(sumFunction);
        validateArraysOfEqualSize(vector1, vector2);

        E result = identity;
        for (int i = 0; i < vector1.size(); i++) {
            E product = productFunction.apply(vector1.get(i), vector2.get(i));
            result = sumFunction.apply(result, product);
        }
        return result;
    }

    private static void validateArraysOfEqualSize(Array<?> vector1, Array<?> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors for must be of equal size, but sizes were " + vector1.size() + " and " + vector2.size());
        }
    }

    private static void validateArraysNotEmptyWhenNoIdentityProvided(Array<?> vector1, Array<?> vector2) {
        if (vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors must not be empty when no identity provided");
        }
    }
}
