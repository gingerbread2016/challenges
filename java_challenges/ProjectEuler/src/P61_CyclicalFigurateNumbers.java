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
        diagnosticsToStdOut = false;
        String message = "The sum of the only ordered set of six cyclic 4-digit numbers " +
                "for which each polygonal type: " +
                "triangle, square, pentagonal, hexagonal, heptagonal, and octagonal, " +
                "is represented by a different number in the set is %,d\n" +
                "The set is: %s\n";
        P61_CyclicalFigurateNumbers p = new P61_CyclicalFigurateNumbers();
        List<Long> solution = p.find(6);
        long sum = getSum(solution);
        System.out.printf(message, sum, solution.toString());
    }

    private final int MIN_ORDER = 3;
    private final int MAX_ORDER = 8;
    private final int LENGTH = 4;
    private static boolean diagnosticsToStdOut = false;
    private TreeMap<Long, List<Long>> polygonals = buildPolygonals();

    List<Long> find(int size) {
        return find(size, size, 0, 0);
    }

    private List<Long> find(int solutionSize, int size, int seed, int offset) {
        if (size == 1) return makeList(seed, offset);
        List<Long> solution;
        int key = 0;
        Long value = 0L;
        while (true) {
            List<Long> partial = find(solutionSize, size - 1, key, value);
            solution = append(partial, value);
            return solution;
        }
    }

    private List<Long> append(List<Long> partial, Long value) {
        partial.add(value);
        return partial;
    }

    private List<Long> makeList(int seed, int offset) {
        List<Long> list = new ArrayList<>();
        Long head = polygonals.firstKey();
        for (int i = 0; i < seed; i++) head = polygonals.higherKey(head);
        Long tail = polygonals.get(head).get(offset);
        list.add(head);
        list.add(tail);
        return list;
    }

    private TreeMap<Long, List<Long>> buildPolygonals() {
        polygonals = new TreeMap<>();
        int[] polygonal = getStart();
        int count = 0;
        while (true) {
            if (isUnderflow(polygonal)) break;
            if (!isWrongSize(polygonal)) {
                System.out.println("polygonal = " + Arrays.toString(polygonal));
                count++;
                addPolygonal(polygonal);
            }
            polygonal = getNext(polygonal);
        }
        showListCheck(count);
        return polygonals;
    }

    private void addPolygonal(int[] polygonal) {
        long value = CyclicPolygonal.getValue(polygonal);
        Long key = getHead(polygonal);
        List<Long> list = polygonals.get(key);
        if (list == null) {
            list = new ArrayList<>();
            polygonals.put(key, list);
        }
        list.add(value);
    }

    static long getSum(List<Long> list) {
        if (list.size() < 1) return 0;
        Long sum = 0L;
        Long last = list.get(0);
        for (int i = 1, listSize = list.size(); i < listSize; i++) {
            sum += last * 100;
            last = list.get(i);
            sum += last;
        }
        return sum;
    }

    // Solution evaluation:

    List<int[]> checkSolution(int size, List<int[]> partial, int[] polygonal) {
        partial.add(polygonal);
        if (isSolution(size, partial)) return partial;
        return null;
    }

    boolean isSolution(int size, List<int[]> list) {
        switch (list.size()) {
            case 0:
                return true;
            case 1:
                return CyclicPolygonal.getDigitCount(list.get(0)) == LENGTH;
            default:
                return isCyclicSolution(size, list);
        }
    }

    private boolean isCyclicSolution(int size, List<int[]> list) {
        return CyclicPolygonal.hasRequiredDigitCounts(list, LENGTH) &&
                CyclicPolygonal.hasUniqueOrders(list) &&
                CyclicPolygonal.isCyclicAndWraps(size, list);
    }

    // Diagnostics:

    private void showCall(int size, int[] seed) {
        if (!diagnosticsToStdOut) return;
        System.out.printf("size: %d, seed: %s, %d\n", size, Arrays.toString(seed), CyclicPolygonal.getValue(seed));
    }

    private void showStep(int size, int[] seed, int[] anchor, List<int[]> partial) {
        if (!diagnosticsToStdOut) return;
        System.out.printf("size: %d, seed: %s, %,d, anchor: %s, %d\n", size,
                Arrays.toString(seed), CyclicPolygonal.getValue(seed),
                Arrays.toString(anchor), CyclicPolygonal.getValue(anchor));
        showList(partial);
    }

    private void showListCheck(int count) {
        System.out.println("count = " + count);
        int count2 = 0;
        for (Long key : polygonals.keySet()) {
            List<Long> value = polygonals.get(key);
            System.out.printf("%s: %s\n", key, value);
            count2 += value.size();
        }
        System.out.println("count2 = " + count2);
    }

    private void showList(List<int[]> partial) {
        if (!diagnosticsToStdOut) return;
        if (partial != null) {
            for (int i = 0; i < partial.size(); i++) {
                System.out.printf("partial[%d]: %,d\n", i, CyclicPolygonal.getValue(partial.get(i)));
            }
        } else {
            System.out.println("partial: null");
        }
    }

    // CyclicPolygonal wrappers:

    private Long getHead(int[] polygonal) {
        return CyclicPolygonal.getFirstDigits(polygonal, LENGTH / 2);
    }

    private Long getTail(int[] polygonal) {
        return CyclicPolygonal.getLastDigits(polygonal, LENGTH / 2);
    }

    private int[] getStart() {
        return CyclicPolygonal.getStart(MAX_ORDER);
    }

    private boolean isUnderflow(int[] polygonal) {
        return CyclicPolygonal.isUnderflow(polygonal, MIN_ORDER);
    }

    private boolean isWrongSize(int[] polygonal) {
        return CyclicPolygonal.isWrongSize(polygonal, LENGTH);
    }

    private boolean isTooSmall(int[] polygonal) {
        return CyclicPolygonal.isTooSmall(polygonal, LENGTH);
    }

    private boolean isTooLarge(int[] polygonal) {
        return CyclicPolygonal.isTooLarge(polygonal, LENGTH);
    }

    private int[] getNext(int[] polygonal) {
        return CyclicPolygonal.getNext(polygonal, LENGTH);
    }
}
