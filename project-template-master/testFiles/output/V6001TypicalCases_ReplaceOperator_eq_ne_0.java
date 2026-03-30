package com.pvsstudio;

public class V6001TypicalCases {
    static void demoIdenticalSubexpressions() {
        int x = 10;
        int y = 20;
        // 1. Очевидное сравнение одной и той же переменной
        if (x < x) {
            java.lang.System.out.println("never");
        }
        // 2. Коммутативность без побочных эффектов → всегда true / false
        if ((x + y) != (y + x)) {
            // всегда true
        }
        if ((x * y) < (y * x)) {
            // всегда false
        }
        // 3. Дважды вызван один и тот же генератор случайных чисел
        double a = java.lang.Math.random() - java.lang.Math.random();// почти всегда ≈ 0

        // 4. Повтор условия (копипаст)
        java.lang.String s = "test";
        if (((s != null) && (s.length() > 0)) && (s != null)) {
        }
        // 5. Повтор instanceof
        java.lang.Exception ex = new java.lang.Exception();
        if ((ex instanceof java.lang.IllegalArgumentException) || (ex instanceof java.lang.IllegalArgumentException)) {
        }
        // 6. Вычитание одинаковых выражений
        double diff = ((x * 1.5) + (y / 2.0)) - ((x * 1.5) + (y / 2.0));// всегда 0

        // 8. Бессмысленное сравнение после присваивания (но без side-effect в сравнении)
        int aVal = 42;
        if (((aVal > 0) && ((aVal = y) > 0)) && (aVal > 0)) {
        }
    }
}