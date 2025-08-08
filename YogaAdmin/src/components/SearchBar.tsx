import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Searchbar as PaperSearchbar, Chip } from 'react-native-paper';

interface SearchBarProps {
  value: string;
  onChangeText: (text: string) => void;
  onDayFilter?: (day: string) => void;
  selectedDay?: string;
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

export const SearchBar: React.FC<SearchBarProps> = ({
  value,
  onChangeText,
  onDayFilter,
  selectedDay,
}) => {
  return (
    <View style={styles.container}>
      <PaperSearchbar
        placeholder="Search classes..."
        onChangeText={onChangeText}
        value={value}
        style={styles.searchBar}
      />
      {onDayFilter && (
        <View style={styles.daysContainer}>
          <View style={styles.chipScroll}>
            {DAYS.map((day) => (
              <Chip
                key={day}
                selected={selectedDay === day}
                onPress={() => onDayFilter(day === selectedDay ? '' : day)}
                style={styles.chip}
              >
                {day}
              </Chip>
            ))}
          </View>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#fff',
  },
  searchBar: {
    elevation: 4,
  },
  daysContainer: {
    marginTop: 8,
  },
  chipScroll: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  chip: {
    marginRight: 8,
    marginBottom: 8,
  },
});