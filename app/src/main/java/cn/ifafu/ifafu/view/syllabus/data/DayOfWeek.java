package cn.ifafu.ifafu.view.syllabus.data;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

@IntDef(value = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY})
@Retention(RetentionPolicy.SOURCE)
public @interface DayOfWeek {
}
