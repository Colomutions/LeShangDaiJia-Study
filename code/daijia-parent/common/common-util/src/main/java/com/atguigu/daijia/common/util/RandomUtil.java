package com.atguigu.daijia.common.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomUtil {
    public static String getOrderIdByTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;
    }
    public static String getRandomNumber(int num) {
        String result = "";
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            result += random.nextInt(10);
        }
        return result;
    }
//	public static void main(String[] args) {
//		RandomUtil RandomUtil = new RandomUtil();
//		System.out.println(RandomUtil.getOrderIdByTime());
//	}
}
