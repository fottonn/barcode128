package ru.airiva;

import com.google.common.collect.ImmutableMap;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Ivan
 */
public class Ean128Generator {

    private static final String[] CODE128_TABLE = {
            "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213",
            "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132",
            "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211",
            "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313",
            "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331",
            "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111",
            "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214",
            "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111",
            "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141",
            "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141",
            "114131", "311141", "411131", "211412", "211214", "211232", "2331112"
    };

    private static final Map<Character, Integer> SYMBOLS = ImmutableMap.<Character, Integer>builder()
            .put('%', 5)
            .put('0', 16)
            .put('1', 17)
            .put('2', 18)
            .put('3', 19)
            .put('4', 20)
            .put('5', 21)
            .put('6', 22)
            .put('7', 23)
            .put('8', 24)
            .put('9', 25)
            .build();

    private static final int START_NUMBER = 103;
    private static final int STOP_NUMBER = 106;

    public static BufferedImage generateEan128Image(String ean128Code, int width, int height, int safeWidth) {

        boolean[] ean128BinaryCode = generateBinaryCode(ean128Code.toCharArray());
        int lineWidth = calcLineWidth(ean128BinaryCode.length, width, safeWidth);
        int indent = calcIndent(ean128BinaryCode.length, lineWidth, width);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D image = bufferedImage.createGraphics();

        image.setColor(Color.WHITE);
        image.fillRect(0, 0, width, height);


        int x = indent;
        for (boolean code : ean128BinaryCode) {
            image.setColor(code ? Color.BLACK : Color.WHITE);
            image.fillRect(x, 0, lineWidth, height);
            x += lineWidth;
        }
        try {
            ImageIO.write(bufferedImage, "png", new File("/home/ivan/img/img.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;

    }

    private static int calcLineWidth(int lineCount, int width, int safeWidth) {
        return (width - 2 * safeWidth) / lineCount;
    }

    private static int calcIndent(int lineCount, int lineWidth, int width) {
        return (width - lineCount * lineWidth) / 2;
    }

    private static boolean[] generateBinaryCode(char[] eanContent) {
        StringBuilder builder = new StringBuilder(CODE128_TABLE[START_NUMBER]);
        for (char symbol : eanContent) {
            builder.append(CODE128_TABLE[SYMBOLS.get(symbol)]);
        }
        builder.append(CODE128_TABLE[calculateCheckSymbol(eanContent)]);
        builder.append(CODE128_TABLE[STOP_NUMBER]);
        return toBinary(builder.toString());
    }

    private static int calculateCheckSymbol(char[] eanContent) {
        int result = 0;
        int i = 1;
        for (char symbol : eanContent) {
            result += i * SYMBOLS.get(symbol);
            i++;
        }
        return result % START_NUMBER;
    }

    private static boolean[] toBinary(String eanCode) {
        int[] eanCodeArray = new int[eanCode.length()];
        for (int i = 0; i < eanCode.length(); i++) {
            eanCodeArray[i] = Integer.parseInt(String.valueOf(eanCode.charAt(i)));
        }
        int sum = 0;
        for (int n : eanCodeArray) {
            sum += n;
        }
        boolean[] binary = new boolean[sum];
        int count = 0;
        for (int i = 0; i < eanCodeArray.length; i++) {
            boolean isLine = i % 2 == 0;
            for (int j = 0; j < eanCodeArray[i]; j++) {
                binary[count] = isLine;
                count++;
            }
        }
        return binary;
    }

    public static void main(String[] args) {
        generateEan128Image("%1234567890123", 200, 50, 0);
    }



}
