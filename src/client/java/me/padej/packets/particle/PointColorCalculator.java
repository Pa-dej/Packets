package me.padej.packets.particle;

public class PointColorCalculator {

    // Метод для интерполяции цвета
    public static int interpolateColor(double proportion) {
        // Цвета в виде int
        int colorClose = 0xc32454;
        int colorMid = 0xf9c22b;
        int colorFar = 0x30ce30;

        int resultColor;

        if (proportion < 0.5) {
            // Пропорция от 0 до 0.5 - интерполяция между ближним и средним цветом
            resultColor = blendColors(colorClose, colorMid, proportion * 2);
        } else {
            // Пропорция от 0.5 до 1.0 - интерполяция между средним и дальним цветом
            resultColor = blendColors(colorMid, colorFar, (proportion - 0.5) * 2);
        }

        return resultColor;
    }

    // Функция для смешивания двух цветов
    private static int blendColors(int color1, int color2, double ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }
}
