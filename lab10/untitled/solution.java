/*
Throughout this interview, we'll pretend we're building a new analytical
database. Don't worry about actually building a database though �C these will
all be toy problems.

Here's how the database works: all records are represented as maps, with string
keys and integer values. The records are contained in an array, in no
particular order.

To begin with, the database will support just one function: min_by_key. This
function scans the array of records and returns the record that has the minimum
value for a specified key. Records that do not contain the specified key are
considered to have value 0 for the key. Note that keys may map to negative values!

Here's an example use case: each of your records contains data about a school
student. You can use min_by_key to answer questions such as "who is the youngest
student?" and "who is the student with the lowest grade-point average?"

Implementation notes:
* You should handle an empty array of records in an idiomatic way in your
  language of choice.
* If several records share the same minimum value for the chosen key, you
  may return any of them.

### Java function signature:
```
public static Map<String, Integer> minByKey(String key, List<Map<String, Integer>> records);
```

### Examples (in Python):
```
assert min_by_key("a", [{"a": 1, "b": 2}, {"a": 2}]) == {"a": 1, "b": 2}
assert min_by_key("a", [{"a": 2}, {"a": 1, "b": 2}]) == {"a": 1, "b": 2}
assert min_by_key("b", [{"a": 1, "b": 2}, {"a": 2}]) == {"a": 2}
assert min_by_key("a", [{}]) == {}
assert min_by_key("b", [{"a": -1}, {"b": -1}]) == {"b": -1}
```

 */

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

class Solution {
    // Copy from here
    public static void main(String[] args) {
        testMinByKey();
        testFirstByKey();
        testRecordComparator();
        testFirstBySortOrder();
    }

    public static Map<String, Integer> firstBySortOrder(LinkedHashMap<String, String> sortOrder,
                                                        List<Map<String, Integer>> records) {
        if (records == null || records.isEmpty() || sortOrder.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> res = records.get(0);
        for (Map<String, Integer> record : records) {
            Iterator<Map.Entry<String, String>> iter = sortOrder.entrySet().iterator();
            while (iter.hasNext()) {
                RecordComparator comparator = createComparator(iter.next());
                int compare = comparator.compare(record, res);
                if (compare == 0) {
                    continue;
                }
                if (compare < 0) {
                    res = record;
                }
                // compare > 0 or compare < 0
                break;
            }
        }

        return res;
    }

    private static RecordComparator createComparator(Map.Entry<String, String> sort) {
        String key = sort.getKey();
        String direction = sort.getValue();
        return new RecordComparator(key, direction);
    }

    public static Map<String, Integer> firstBySortOrder2(
        LinkedHashMap<String, String> sortOrder,
        List<Map<String, Integer>> records) {
        if (records == null || records.isEmpty() || sortOrder.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> res = records.get(0);
        List<Map.Entry<String, String>> sortOrderIter = sortOrder.entrySet().stream().collect(Collectors.toList());
        int index = 0;

        for (Map<String, Integer> record : records) {
            RecordComparator comparator = createComparator(sortOrderIter, index);
            while (index < sortOrderIter.size()) {
                int compare = comparator.compare(record, res);
                if (compare == 0) {
                    index++;
                    continue;
                }
                if (compare < 0) {
                    res = record;
                }
                break;
            }

            index = 0;
        }

        return res;
    }

    private static RecordComparator createComparator(List<Map.Entry<String, String>> sortOrderIter, int index) {
        Map.Entry<String, String> sort = sortOrderIter.get(index);
        String key = sort.getKey();
        String direction = sort.getValue();
        RecordComparator comparator = new RecordComparator(key, direction);
        return comparator;
    }

    public static void testFirstBySortOrder() {
        List<Map<String, Integer>> example1 = Arrays.asList(Maps.of("a", 5), Maps.of("a", 6));
        List<Map<String, Integer>> example2 = Arrays.asList(
            Maps.of("a", -5, "b", 10),
            Maps.of("a", -4, "b", 10)
        );

        System.out.println("firstBySortOrder");
        assertEqual(example1.get(1), firstBySortOrder(Maps.ordered("a", "desc"), example1));
        assertEqual(
            example2.get(0),
            firstBySortOrder(Maps.ordered("b", "asc", "a", "asc"), example2)
        );
        assertEqual(
            example2.get(1),
            firstBySortOrder(Maps.ordered("a", "desc", "b", "asc"), example2)
        );
    }
    // To here

    static class RecordComparator implements Comparator<Map<String, Integer>> {
        private final String key;
        private final String direction;
        public RecordComparator(String key, String direction) {
            this.key = key;
            this.direction = direction;
        }

        public int compare(Map<String, Integer> left, Map<String, Integer> right) {
            int leftVal = left.getOrDefault(this.key, 0);
            int rightVal = right.getOrDefault(this.key, 0);
            if (DESC.equals(this.direction)) {
                return Integer.compare(rightVal, leftVal);
            }

            return Integer.compare(leftVal, rightVal);
        }
    }

    public static void testRecordComparator() {
        RecordComparator cmp = new RecordComparator("a", "asc");
        Map<String, Integer> a1 = Maps.of("a", 1);
        Map<String, Integer> a2 = Maps.of("a", 2);
        System.out.println("RecordComparator");
        assertEqual(-1, cmp.compare(a1, a2));
        assertEqual(1, cmp.compare(a2, a1));
        assertEqual(0, cmp.compare(a1, a1));
    }
    // To here

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    public static Map<String, Integer> firstByKey(
        String key, String direction, List<Map<String, Integer>> records) {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> res = records.get(0);
        RecordComparator comparator = new RecordComparator(key, direction);
        for (Map<String, Integer> record : records) {
            if (comparator.compare(record, res) < 0) {
                res = record;
            }
        }

        return res;
    }

    public static Map<String, Integer> firstByKey2(
        String key, String direction, List<Map<String, Integer>> records) {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> res = records.get(0);
        int target = res.getOrDefault(key, 0);
        for (Map<String, Integer> record : records) {
            int val = record.getOrDefault(key, 0);
            if ((ASC.equals(direction) && val < target) ||
                (DESC.equals(direction) && val > target)) {
                res = record;
                target = val;
            }
        }

        return res;
    }

    public static void testFirstByKey() {
        List<Map<String, Integer>> example1 = Arrays.asList(Maps.of("a", 1));
        List<Map<String, Integer>> example2 = Arrays.asList(
            Maps.of("b", 1),
            Maps.of("b", -2),
            Maps.of("a", 10)
        );
        List<Map<String, Integer>> example3 = Arrays.asList(
            Maps.of(),
            Maps.of("a", 10, "b", -10),
            Maps.of(),
            Maps.of("a", 3, "c", 3)
        );

        System.out.println("firstByKey");
        assertEqual(example1.get(0), firstByKey("a", "asc", example1));
        assertEqual(example2.get(0), firstByKey("a", "asc", example2));  // example2.get(1) ok too
        assertEqual(example2.get(2), firstByKey("a", "desc", example2));
        assertEqual(example2.get(1), firstByKey("b", "asc", example2));
        assertEqual(example2.get(0), firstByKey("b", "desc", example2));
        assertEqual(example3.get(1), firstByKey("a", "desc", example3));
    }
    // To here

    /**
     */
    public static Map<String, Integer> minByKey(String key, List<Map<String, Integer>> records) {
        return firstByKey(key, ASC, records);
    }

    public static Map<String, Integer> minByKey2(String key, List<Map<String, Integer>> records) {
        Map<String, Integer> res = Maps.of();
        int min = Integer.MAX_VALUE;
        for (Map<String, Integer> record : records) {
            int val = record.getOrDefault(key, 0);
            if (val <= min) {
                min = val;
                res = record;
            }
        }
        return res;
    }

    /* Tests start here */
    public static <T> void assertEqual(T expected, T actual) {
        if (expected == null && actual == null || actual != null && actual.equals(expected)) {
            System.out.println("PASSED");
        } else {
            throw new AssertionError("Expected:\n  " + expected + "\nActual:\n  " + actual + "\n");
        }
    }

    public static void testMinByKey() {
        List<Map<String, Integer>> example1 = Arrays.asList(
            Maps.of("a", 1, "b", 2),
            Maps.of("a", 2)
        );
        List<Map<String, Integer>> example2 = Arrays.asList(example1.get(1), example1.get(0));
        List<Map<String, Integer>> example3 = Arrays.asList(Maps.of());
        List<Map<String, Integer>> example4 = Arrays.asList(
            Maps.of("a", -1),
            Maps.of("b", -1)
        );
        List<Map<String, Integer>> example5 = Arrays.asList(Maps.of("a", Integer.MAX_VALUE));
        List<Map<String, Integer>> example6 = Arrays.asList(Maps.of("a", Integer.MIN_VALUE));

        System.out.println("minByKey");
        assertEqual(example1.get(0), minByKey("a", example1));
        assertEqual(example2.get(1), minByKey("a", example2));
        assertEqual(example1.get(1), minByKey("b", example1));
        assertEqual(example3.get(0), minByKey("a", example3));
        assertEqual(example4.get(1), minByKey("b", example4));
        assertEqual(example5.get(0), minByKey("a", example5));
        assertEqual(example6.get(0), minByKey("a", example6));
    }
}

/*
 * Helpers to quickly create and populate new Maps.
 *
 * Inspired by ImmutableMap.of in guava.
 */

class Maps {
    public static <K, V> Map<K, V> of() {
        return new HashMap<K, V>();
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1, K k2, V v2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1, K k2, V v2, K k3, V v3) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
}
