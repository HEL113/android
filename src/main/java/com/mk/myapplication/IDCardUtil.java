package com.mk.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//身份证计算性别，年龄
public class IDCardUtil {

    // 获取性别
    public static String getGenderFromIDCard(String idCard) {
        String gender = "";
        if (idCard.length() == 18) {
            int genderValue = Integer.parseInt(idCard.substring(16, 17));
            gender = genderValue % 2 == 0 ? "女" : "男";

        }
        return gender;
    }

    // 获取出生日期
    public static Date getBirthDateFromIDCard(String idCard) {
        Date birthDate = null;
        if (idCard.length() == 18) {
            String year = idCard.substring(6, 10);
            String month = idCard.substring(10, 12);
            String day = idCard.substring(12, 14);
            String fullBirthDateStr = year + "-" + month + "-" + day;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                birthDate = sdf.parse(fullBirthDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return birthDate;
    }

    // 计算年龄
    public static int calculateAgeFromBirthDate(Date birthDate) {
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        Calendar currentCalendar = Calendar.getInstance();
        int age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}