import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList, RefreshControl } from 'react-native';
import { ActivityIndicator, Text } from 'react-native-paper';
import { ClassCard, SearchBar } from '../../components';
import { getYogaCourses, searchCourses } from '../../services/classes';
import { YogaCourse } from '../../types';

interface ClassListScreenProps {
  navigation: any;
}

export const ClassListScreen: React.FC<ClassListScreenProps> = ({ navigation }) => {
  const [courses, setCourses] = useState<YogaCourse[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDay, setSelectedDay] = useState('');
  const [error, setError] = useState('');

  const loadCourses = async () => {
    try {
      const fetchedCourses = await getYogaCourses();
      setCourses(fetchedCourses);
      setError('');
    } catch (err) {
      setError('Failed to load courses. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      let filteredCourses: YogaCourse[];
      if (selectedDay || searchQuery) {
        filteredCourses = await searchCourses(selectedDay);
        if (searchQuery) {
          filteredCourses = filteredCourses.filter(course => 
            course.type.toLowerCase().includes(searchQuery.toLowerCase()) ||
            course.description?.toLowerCase().includes(searchQuery.toLowerCase())
          );
        }
      } else {
        filteredCourses = await getYogaCourses();
      }
      setCourses(filteredCourses);
      setError('');
    } catch (err) {
      setError('Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadCourses();
    setRefreshing(false);
  }, []);

  useEffect(() => {
    loadCourses();
  }, []);

  useEffect(() => {
    const debounceTimeout = setTimeout(() => {
      handleSearch();
    }, 500);

    return () => clearTimeout(debounceTimeout);
  }, [searchQuery, selectedDay]);

  const handleAddToCart = (course: YogaCourse) => {
    navigation.navigate('CourseDetails', { courseId: course.id });
  };

  const renderItem = ({ item }: { item: YogaCourse }) => (
    <ClassCard
      course={item}
      onAddToCart={() => handleAddToCart(item)}
      onViewDetails={() => navigation.navigate('CourseDetails', { courseId: item.id })}
    />
  );

  if (loading && !refreshing) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <SearchBar
        value={searchQuery}
        onChangeText={setSearchQuery}
        onDayFilter={setSelectedDay}
        selectedDay={selectedDay}
      />

      {error ? (
        <Text style={styles.errorText}>{error}</Text>
      ) : null}

      <FlatList
        data={courses}
        renderItem={renderItem}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.listContent}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
          />
        }
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text>No classes found</Text>
          </View>
        }
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listContent: {
    paddingVertical: 8,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  errorText: {
    color: '#B00020',
    padding: 16,
    textAlign: 'center',
  },
});