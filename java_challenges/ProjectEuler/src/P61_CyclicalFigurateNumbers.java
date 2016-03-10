import java.util.*;

public class P61_CyclicalFigurateNumbers {
    // Project Euler - Problem 61 - Cyclical figurate numbers
    // Triangle, square, pentagonal, hexagonal, heptagonal, and octagonal numbers are all figurate (polygonal)
    // numbers and are generated by the following formulae:
    // Triangle	 	P3,n=n(n+1)/2	 	1, 3, 6, 10, 15, ...
    // Square	 	P4,n=n2	 	        1, 4, 9, 16, 25, ...
    // Pentagonal	P5,n=n(3n−1)/2	 	1, 5, 12, 22, 35, ...
    // Hexagonal	P6,n=n(2n−1)	 	1, 6, 15, 28, 45, ...
    // Heptagonal	P7,n=n(5n−3)/2	 	1, 7, 18, 34, 55, ...
    // Octagonal	P8,n=n(3n−2)	 	1, 8, 21, 40, 65, ...
    // The ordered set of three 4-digit numbers: 8128, 2882, 8281, has three interesting properties.
    // The set is cyclic, in that the last two digits of each number is the first two digits of the next number
    // (including the last number with the first).
    // Each polygonal type: triangle P(3,127)=8128, square P(4,91)=8281, and pentagonal P(5,44)=2882, is
    // represented by a different number in the set.
    // This is the only set of 4-digit numbers with this property.
    // Find the sum of the only ordered set of six cyclic 4-digit numbers for which each polygonal type:
    // triangle, square, pentagonal, hexagonal, heptagonal, and octagonal,
    // is represented by a different number in the set.

    public static void main(String[] args) {
        String message = "The sum of the only ordered set of six cyclic 4-digit numbers " +
                "for which each polygonal type: " +
                "triangle, square, pentagonal, hexagonal, heptagonal, and octagonal, " +
                "is represented by a different number in the set is %,d\n" +
                "The set is: %s\n";
        List<int[]> solution = P61_CyclicalFigurateNumbers.findSolutionList(6);
        long sum = P61_CyclicalFigurateNumbers.getSum(solution);
        System.out.printf(message, sum, solution.toString());
    }

    private static Polygonal triangular = new Triangular();
    private static Polygonal square = new Square();
    private static Polygonal pentagonal = new Pentagonal();
    private static Polygonal hexagonal = new Hexagonal();
    private static Polygonal heptagonal = new Heptagonal();
    private static Polygonal octagonal = new Octagonal();

    static List<int[]> findSolutionList(int size) {
        // iterate through octagonal seeds
        // for each polygonal (3 .. 7): attempt to add one new cyclical polygonal
        // track 1) partial solution, 2) polygonal id (3 .. 7) and 3) n in f(n) to attempt
        // recursive call
        List<int[]> solution = P61_CyclicalFigurateNumbers.findSolutionList(size, size, 8, 1);
        long sum = P61_CyclicalFigurateNumbers.getSum(solution);
        showList(solution);
        System.out.printf("sum: %d\n", sum);
        return solution;
//        return findSolutionList(size, 8, 1);
    }

    static List<int[]> findSolutionList(int solutionSize, int size, int partialOrder, int partialOffset) {
        // order and offset define polygonal
        // anchor: for size 1, get valid polygonal, for this order, and valid offset
        // partialOrder and partialOffset define partial
        // iterate over partials modifying partialOrder and partialOffset
        // if partial is null, increment offset
        // modify partialOrder, partialOffset if partial is valid but does not form solution with polygonal
        // return null if no partials work for this order, i.e., partialOrder underflow
//        showCall(size, order, offset);
        int[] polygonal;
        int[] partialPolygonal;
        List<int[]> solution;
        List<int[]> partial;
        int order = 8;
        int offset = 1;
        while (true) {
            polygonal = makePolygonal(order, offset);
//            showStep(order, offset, polygonal);
            if (order < 3) {
                System.out.printf("no polygonal found for size: %d, order: %d, offset: %d\n", size, order, offset);
                return null;
            } else if (digitCount(polygonal) < 4) {
                offset++;
            } else if (digitCount(polygonal) > 4) {
                order--;
                offset = 1;
            } else if (size == 1) {
                return makeList(order, offset); // fixme: for size 1 always returns lowest offset for highest order
            } else {
                partialPolygonal = makePolygonal(partialOrder, partialOffset);
                if (partialOrder < 3) {
                    return null;
                } else if (digitCount(partialPolygonal) < 4) {
                    partialOffset++;
                } else if (digitCount(partialPolygonal) > 4) {
                    partialOrder--;
                    partialOffset = 1;
                } else {
                    // todo: avoid making the same partial again if order and offset did not change:
                    partial = findSolutionList(solutionSize, size - 1, partialOrder, partialOffset);
                    if (partial == null) {
                        offset++;
                    } else {
//                        showList(partial);
                        solution = checkSolution(solutionSize, partial, polygonal);
                        if (solution == null) {
                            partialOffset++;
                        } else {
                            return solution;
                        }
                    }
                }
            }
        }
    }

    static List<int[]> checkSolution(int size, List<int[]> partial, int[] polygonal) {
        partial.add(polygonal);
        if (isSolution(size, partial)) return partial;
        return null;
    }

    static boolean isSolution(int size, List<int[]> list) {
        // todo: check that each order occurs only once
        switch (list.size()) {
            case 0:
                return true;
            case 1:
                return digitCount(list.get(0)) == 4;
            default:
                return isCyclicSolution(size, list);
        }
    }

    private static boolean isCyclicSolution(int size, List<int[]> list) {
        return hasRequiredDigitCounts(list) &&
                isCyclicList(list) &&
                hasUniqueOrders(list) &&
                isPartialOrWraps(size, list);
    }

    static boolean hasUniqueOrders(List<int[]> list) {
        Set<Integer> orders = new HashSet<>();
        for (int[] polygonal : list) {
            int order = polygonal[0];
            if (orders.contains(order)) return false;
            orders.add(order);
        }
        return true;
    }

    static boolean isPartialOrWraps(int size, List<int[]> list) {
        return list.size() != size ||
                isCyclicSolutionPair(list, list.size() - 1, 0);
    }

    static boolean isCyclicList(List<int[]> list) {
        for (int i = 1; i < list.size(); i++) if (!isCyclicWithPrevious(list, i)) return false;
        return true;
    }

    static boolean hasRequiredDigitCounts(List<int[]> list) {
        for (int[] item : list) if (digitCount(item) != 4) return false;
        return true;
    }

    static boolean isCyclicWithPrevious(List<int[]> list, int i) {
        return isCyclicSolutionPair(list, i - 1, i);
    }

    static boolean isCyclicSolutionPair(List<int[]> list, int index1, int index2) {
        try {
            return isCyclic(list.get(index1), list.get(index2));
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean isCyclic(int[] polygonal1, int[] polygonal2) {
        return getLastDigits(polygonal1, 2).equals(getFirstDigits(polygonal2, 2));
    }

    static String getFirstDigits(int[] polygonal, int count) {
        String s = "" + P61_CyclicalFigurateNumbers.getValue(polygonal);
        if (s.length() < count) return s;
        return s.substring(0, count);
    }

    static String getLastDigits(int[] polygonal1, int count) {
        String s = "" + P61_CyclicalFigurateNumbers.getValue(polygonal1);
        if (s.length() < count) return s;
        return s.substring(s.length() - count);
    }

    static List<int[]> makeList(int order, Integer n) {
        List<int[]> list = new ArrayList<>();
        while (digitCount(makePolygonal(order, n)) < 4) n++;
        list.add(makePolygonal(order, n));
        return list;
    }

    static int[] makePolygonal(int order, Integer n) {
        return new int[]{order, n};
    }

    static long getSum(List<int[]> list) {
        if (list == null) return 0;
        long sum = 0;
        long value;
        for (int[] item : list) {
            value = getValue(item);
            sum += value;
        }
        return sum;
    }

    static int digitCount(int[] polygonal) {
        long value = getValue(polygonal);
        if (value < 0) return 0;
        return ("" + value).length();
    }

    static long getValue(int[] item) {
        switch (item[0]) {
            case 3:
                return triangular.function(item[1]);
            case 4:
                return square.function(item[1]);
            case 5:
                return pentagonal.function(item[1]);
            case 6:
                return hexagonal.function(item[1]);
            case 7:
                return heptagonal.function(item[1]);
            case 8:
                return octagonal.function(item[1]);
            default:
                return -1;
        }
    }

    private static void showCall(int size, int order, int offset) {
        System.out.printf("size: %d, order: %d, offset: %d\n", size, order, offset);
    }

    private static void showStep(int order, Integer offset, int[] polygonal) {
        long value = P61_CyclicalFigurateNumbers.getValue(polygonal);
        int length = digitCount(polygonal);
        System.out.printf("polygonal: %s, value: %,d, length: %d, order: %d, offset: %d\n",
                Arrays.toString(polygonal), value, length, order, offset);
    }

    static void showList(List<int[]> partial) {
        if (partial != null) {
            for (int i = 0; i < partial.size(); i++) {
                System.out.printf("partial[%d]: %,d\n", i, getValue(partial.get(i)));
            }
        } else {
            System.out.println("partial: null");
        }
    }
}
