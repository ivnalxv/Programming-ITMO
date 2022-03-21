package info.kgeorgiy.ja.alekseev.student;


import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StudentDB implements AdvancedQuery {
    private final Comparator<Student> nameComparator = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .reversed()
            .thenComparing(Comparator.naturalOrder());

    private static final String DEFAULT_STRING = "";

    private String getFullName(Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }

    private <E> E getMaxFromMap(final Map<E, Integer> map,
                                final Comparator<E> comparator,
                                final E orElse) {
        return map.entrySet().stream()
                .max(Map.Entry.<E, Integer>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(comparator))
                )
                .map(Map.Entry::getKey)
                .orElse(orElse);
    }


    private <E> GroupName getLargestGroupWithStudentsSortedBy(final Collection<Student> students,
                                                              final Function<List<Student>, Collection<E>> mapper,
                                                              final Comparator<GroupName> comparator) {
        return getMaxFromMap(
                students.stream()
                        .collect(Collectors.groupingBy(Student::getGroup))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, e -> mapper.apply(e.getValue()).size())
                        ),
                comparator, null);
    }

    public String getMostPopularName(final Collection<Student> students) {
        return getMaxFromMap(
                students.stream().collect(
                        Collectors.groupingBy(Student::getFirstName,
                                Collectors.mapping(
                                        Student::getGroup,
                                        // :NOTE: Stream::distinct
                                        Collectors.collectingAndThen(Collectors.toSet(), Set::size)
                                ))),
                Comparator.naturalOrder(),
                DEFAULT_STRING);
    }

    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public String getMaxStudentFirstName(final List<Student> students) {
        return students.stream()
                .max(Comparator.comparingInt(Student::getId))
                .map(Student::getFirstName)
                .orElse(DEFAULT_STRING);
    }

    // getLargestGroup... implementation

    public GroupName getLargestGroup(final Collection<Student> students) {
        // :NOTE: identity
        return getLargestGroupWithStudentsSortedBy(students, s -> s, Comparator.naturalOrder());
    }

    public GroupName getLargestGroupFirstName(final Collection<Student> students) {
        return getLargestGroupWithStudentsSortedBy(students, this::getDistinctFirstNames, Comparator.reverseOrder());
    }


    // getGroupsBy... implementation

    private static List<Group> getGroupsWithStudentsSortedBy(final Collection<Student> students,
                                                             final Comparator<Student> comparator) {
        return students.stream()
                .sorted(comparator)
                .collect(Collectors.groupingBy(Student::getGroup))
                .entrySet().stream()
                .map(e -> new Group(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public List<Group> getGroupsByName(final Collection<Student> students) {
        return getGroupsWithStudentsSortedBy(students, nameComparator);
    }

    public List<Group> getGroupsById(final Collection<Student> students) {
        return getGroupsWithStudentsSortedBy(students, Comparator.comparingInt(Student::getId));
    }


    // getStudentsMappedBy... implementation

    private static <E> List<E> getStudentsMappedBy(final Collection<Student> students,
                                                   final Function<Student, E> mapper) {
        return students.stream().map(mapper).toList();
    }

    public List<String> getFirstNames(final List<Student> students) {
        return getStudentsMappedBy(students, Student::getFirstName);
    }

    public List<String> getLastNames(final List<Student> students) {
        return getStudentsMappedBy(students, Student::getLastName);
    }

    public List<GroupName> getGroups(final List<Student> students) {
        return getStudentsMappedBy(students, Student::getGroup);
    }

    public List<String> getFullNames(final List<Student> students) {
        return getStudentsMappedBy(students, this::getFullName);
    }


    // sortStudentsBy... implementation

    private static List<Student> sortStudentsBy(final Collection<Student> students,
                                                final Comparator<Student> comparator) {
        return students.stream().sorted(comparator).toList();
    }

    public List<Student> sortStudentsById(final Collection<Student> students) {
        return sortStudentsBy(students, Comparator.comparing(Student::getId));
    }

    public List<Student> sortStudentsByName(final Collection<Student> students) {
        return sortStudentsBy(students, nameComparator);
    }


    // findStudentsBy... implementation

    private <T> List<Student> findStudentsBy(final Collection<Student> students,
                                             final Function<Student, T> extractor,
                                             final T value) {
        return students.stream()
                .filter(o -> Objects.equals(extractor.apply(o), value))
                .sorted(nameComparator)
                .toList();
    }

    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String name) {
        return findStudentsBy(students, Student::getFirstName, name);
    }

    public List<Student> findStudentsByLastName(final Collection<Student> students, final String name) {
        return findStudentsBy(students, Student::getLastName, name);
    }

    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        return findStudentsBy(students, Student::getGroup, group);
    }

    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        return findStudentsByGroup(students, group).stream().collect(
                Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(Comparator.naturalOrder())
                ));
    }


    // Indices implementation

    private <E> List<E> getByIndices(final Collection<Student> students,
                                     final int[] indices,
                                     final Function<Student, E> mapper) {
        return getByIndicesImpl(students.stream().toList(), indices, mapper);
    }

    private <E> List<E> getByIndicesImpl(final List<Student> students,
                                         final int[] indices,
                                         final Function<Student, E> mapper) {

        return Arrays.stream(indices)
                .mapToObj(students::get)
                .map(mapper)
                .toList();
    }

    public List<String> getFirstNames(Collection<Student> students, int[] indices) {
        return getByIndices(students, indices, Student::getFirstName);
    }

    public List<String> getLastNames(Collection<Student> students, int[] indices) {
        return getByIndices(students, indices, Student::getLastName);
    }

    public List<GroupName> getGroups(Collection<Student> students, int[] indices) {
        return getByIndices(students, indices, Student::getGroup);
    }

    public List<String> getFullNames(Collection<Student> students, int[] indices) {
        return getByIndices(students, indices, this::getFullName);
    }
}
