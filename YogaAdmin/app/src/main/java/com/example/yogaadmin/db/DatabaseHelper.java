package com.example.yogaadmin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaAdmin.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_YOGA_COURSE = "yoga_course";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TABLE_CUSTOMER = "customer";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_LAST_MODIFIED = "last_modified";
    private static final String KEY_IS_SYNCED = "is_synced";
    
    // YogaCourse columns
    private static final String KEY_DAY_OF_WEEK = "day_of_week";
    private static final String KEY_TIME = "time";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CAPACITY = "capacity";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_EQUIPMENT = "equipment";

    // Schedule columns
    private static final String KEY_DATE = "date";
    private static final String KEY_TEACHER = "teacher";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_YOGA_COURSE_ID = "yoga_course_id";
    private static final String KEY_CURRENT_ENROLLMENT = "current_enrollment";
    private static final String KEY_IS_CANCELLED = "is_cancelled";

    // Customer columns
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_YOGA_COURSE_TABLE = "CREATE TABLE " + TABLE_YOGA_COURSE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DAY_OF_WEEK + " TEXT NOT NULL,"
                + KEY_TIME + " TEXT NOT NULL,"
                + KEY_PRICE + " REAL NOT NULL,"
                + KEY_CAPACITY + " INTEGER NOT NULL,"
                + KEY_DURATION + " INTEGER NOT NULL,"
                + KEY_TYPE + " TEXT NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IS_ACTIVE + " INTEGER DEFAULT 1,"
                + KEY_DIFFICULTY + " TEXT,"
                + KEY_EQUIPMENT + " TEXT,"
                + KEY_LAST_MODIFIED + " INTEGER,"
                + KEY_IS_SYNCED + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_YOGA_COURSE_TABLE);

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHEDULE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT NOT NULL,"
                + KEY_TEACHER + " TEXT NOT NULL,"
                + KEY_COMMENTS + " TEXT,"
                + KEY_YOGA_COURSE_ID + " INTEGER NOT NULL,"
                + KEY_CURRENT_ENROLLMENT + " INTEGER DEFAULT 0,"
                + KEY_IS_CANCELLED + " INTEGER DEFAULT 0,"
                + KEY_LAST_MODIFIED + " INTEGER,"
                + KEY_IS_SYNCED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + KEY_YOGA_COURSE_ID + ") REFERENCES " + TABLE_YOGA_COURSE + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_SCHEDULE_TABLE);

        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_EMAIL + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YOGA_COURSE);
        onCreate(db);
    }

    // YogaCourse CRUD operations
    public long addYogaCourse(YogaCourse course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(KEY_TIME, course.getTime());
        values.put(KEY_PRICE, course.getPrice());
        values.put(KEY_CAPACITY, course.getCapacity());
        values.put(KEY_DURATION, course.getDuration());
        values.put(KEY_TYPE, course.getType());
        values.put(KEY_DESCRIPTION, course.getDescription());
        values.put(KEY_IS_ACTIVE, course.isActive() ? 1 : 0);
        values.put(KEY_DIFFICULTY, course.getDifficulty());
        values.put(KEY_EQUIPMENT, course.getEquipment());
        values.put(KEY_LAST_MODIFIED, course.getLastModified());
        values.put(KEY_IS_SYNCED, course.isSynced() ? 1 : 0);

        long id = db.insert(TABLE_YOGA_COURSE, null, values);
        db.close();
        return id;
    }

    public List<YogaCourse> getAllYogaCourses() {
        List<YogaCourse> courseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_YOGA_COURSE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                YogaCourse course = new YogaCourse();
                course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                course.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK)));
                course.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
                course.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                course.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAPACITY)));
                course.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)));
                course.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
                course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                course.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ACTIVE)) == 1);
                course.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIFFICULTY)));
                course.setEquipment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EQUIPMENT)));
                course.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
                course.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);

                courseList.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return courseList;
    }

    public YogaCourse getYogaCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_YOGA_COURSE, null, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            YogaCourse course = new YogaCourse();
            course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            course.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK)));
            course.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
            course.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PRICE)));
            course.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAPACITY)));
            course.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)));
            course.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
            course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
            course.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ACTIVE)) == 1);
            course.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIFFICULTY)));
            course.setEquipment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EQUIPMENT)));
            course.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
            course.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);

            cursor.close();
            return course;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public int updateYogaCourse(YogaCourse course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(KEY_TIME, course.getTime());
        values.put(KEY_PRICE, course.getPrice());
        values.put(KEY_CAPACITY, course.getCapacity());
        values.put(KEY_DURATION, course.getDuration());
        values.put(KEY_TYPE, course.getType());
        values.put(KEY_DESCRIPTION, course.getDescription());
        values.put(KEY_IS_ACTIVE, course.isActive() ? 1 : 0);
        values.put(KEY_DIFFICULTY, course.getDifficulty());
        values.put(KEY_EQUIPMENT, course.getEquipment());
        values.put(KEY_LAST_MODIFIED, course.getLastModified());
        values.put(KEY_IS_SYNCED, course.isSynced() ? 1 : 0);

        return db.update(TABLE_YOGA_COURSE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
    }

    public void deleteYogaCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULE, KEY_YOGA_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)});
        db.delete(TABLE_YOGA_COURSE, KEY_ID + " = ?",
                new String[]{String.valueOf(courseId)});
        db.close();
    }

    // Schedule CRUD operations
    public long addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, schedule.getDate());
        values.put(KEY_TEACHER, schedule.getTeacher());
        values.put(KEY_COMMENTS, schedule.getComments());
        values.put(KEY_YOGA_COURSE_ID, schedule.getYogaCourseId());
        values.put(KEY_CURRENT_ENROLLMENT, schedule.getCurrentEnrollment());
        values.put(KEY_IS_CANCELLED, schedule.isCancelled() ? 1 : 0);
        values.put(KEY_LAST_MODIFIED, schedule.getLastModified());
        values.put(KEY_IS_SYNCED, schedule.isSynced() ? 1 : 0);

        long id = db.insert(TABLE_SCHEDULE, null, values);
        db.close();
        return id;
    }

    public List<Schedule> getSchedulesForCourse(int courseId) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_SCHEDULE, null,
                KEY_YOGA_COURSE_ID + "=?", new String[]{String.valueOf(courseId)},
                null, null, KEY_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                schedule.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)));
                schedule.setComments(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS)));
                schedule.setYogaCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YOGA_COURSE_ID)));
                schedule.setCurrentEnrollment(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CURRENT_ENROLLMENT)));
                schedule.setCancelled(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_CANCELLED)) == 1);
                schedule.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
                schedule.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);
                
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }

    public List<Schedule> searchSchedulesByTeacher(String teacherName) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_SCHEDULE + 
                      " WHERE " + KEY_TEACHER + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + teacherName + "%"});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                schedule.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)));
                schedule.setComments(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS)));
                schedule.setYogaCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YOGA_COURSE_ID)));
                schedule.setCurrentEnrollment(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CURRENT_ENROLLMENT)));
                schedule.setCancelled(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_CANCELLED)) == 1);
                schedule.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
                schedule.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);
                
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }

    public int updateSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, schedule.getDate());
        values.put(KEY_TEACHER, schedule.getTeacher());
        values.put(KEY_COMMENTS, schedule.getComments());
        values.put(KEY_CURRENT_ENROLLMENT, schedule.getCurrentEnrollment());
        values.put(KEY_IS_CANCELLED, schedule.isCancelled() ? 1 : 0);
        values.put(KEY_LAST_MODIFIED, schedule.getLastModified());
        values.put(KEY_IS_SYNCED, schedule.isSynced() ? 1 : 0);

        return db.update(TABLE_SCHEDULE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
    }

    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULE, KEY_ID + " = ?",
                new String[]{String.valueOf(scheduleId)});
        db.close();
    }

    // Sync operations
    public List<YogaCourse> getUnsyncedCourses() {
        List<YogaCourse> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_YOGA_COURSE, null,
                KEY_IS_SYNCED + "=?", new String[]{"0"},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                YogaCourse course = new YogaCourse();
                course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                course.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK)));
                course.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
                course.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                course.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAPACITY)));
                course.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)));
                course.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
                course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                course.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ACTIVE)) == 1);
                course.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIFFICULTY)));
                course.setEquipment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EQUIPMENT)));
                course.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
                course.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);
                courses.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courses;
    }

    public List<Schedule> getUnsyncedSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_SCHEDULE, null,
                KEY_IS_SYNCED + "=?", new String[]{"0"},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                schedule.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)));
                schedule.setComments(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS)));
                schedule.setYogaCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YOGA_COURSE_ID)));
                schedule.setCurrentEnrollment(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CURRENT_ENROLLMENT)));
                schedule.setCancelled(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_CANCELLED)) == 1);
                schedule.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_MODIFIED)));
                schedule.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }
}