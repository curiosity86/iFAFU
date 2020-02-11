package cn.ifafu.ifafu.data.network.parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

import cn.ifafu.ifafu.util.BitmapUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VerifyParser implements ObservableTransformer<ResponseBody, String> {

    private BigDecimal[][] weight;

    private Context mContext;

    public VerifyParser(Context context) {
        mContext = context;
    }

    public void init() {
        try (InputStream inputStream = mContext.getAssets().open("theta.dat")) {
            weight = new BigDecimal[34][337];
            Scanner scanner = new Scanner(inputStream);
            for (int i = 0; i < 34; i++) {
                for (int j = 0; j < 337; j++) {
                    weight[i][j] = scanner.nextBigDecimal();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String todo(Bitmap bitmap) {
        if (weight == null) {
            init();
        }


        if (bitmap == null) {
            return "";
        }

        long longStart = System.currentTimeMillis();
        int[][] data = prepareData(bitmap);
        /* transpose data to x for linear classifier */
        BigDecimal[][] x = new BigDecimal[4][337];
        for (int i = 0; i < 4; i++) {
            x[i][0] = new BigDecimal(1);
            for (int j = 1; j < 337; j++) {
                x[i][j] = new BigDecimal(data[i][j - 1] / 255);
            }
        }

        /* predict */
        double[][] y = dot(weight, x);
        double[][] p = sigmoid(y);

        /* classify */
        char[] chr = new char[4];
        for (int i = 0; i < 4; i++) {
            double max = 0;
            int clas = 0;

            for (int j = 0; j < 34; j++) {
                if (p[i][j] > max) {
                    max = p[i][j];
                    clas = j;
                }
            }

            chr[i] = (char) (clas <= 9 ? clas + 48 : (clas <= 23 ? clas + 87 : clas + 88));
        }

        long time = System.currentTimeMillis() - longStart;
        Log.d("log", String.format(
                Locale.getDefault(),
                "Classify verify code use time: %ds%dms",
                time / 1000, time % 1000));
        return String.valueOf(chr);
    }

    private double[][] sigmoid(double[][] y) {
        double[][] answer = new double[4][34];

        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
                //  Sigmoid
                answer[i][j] = 1.0 / (1.0 + Math.exp(-1.0 * y[i][j]));
            }
        }

        return answer;
    }

    private double[][] dot(BigDecimal[][] weight, BigDecimal[][] x) {
        double[][] answer = new double[4][34];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 34; j++) {
                BigDecimal t = new BigDecimal(0);
                for (int k = 0; k < 337; k++) {
                    t = t.add(x[i][k].multiply(weight[j][k]));
                }
                answer[i][j] = t.doubleValue();
            }
        }

        return answer;
    }

    private int[][] prepareData(Bitmap bitmap) {
        int xSize = bitmap.getWidth();
        int ySize = bitmap.getHeight() - 5;
        int piece = (xSize - 22) / 8;

        int[] centers = {0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            centers[i] = 4 + piece * (2 * i + 1);
        }

        int[][] matrix = new int[4][(2 * piece + 4) * (ySize - 1)];
        for (int k = 0; k < 4; k++) {
            int center = centers[k];
            int ii = 0;
            for (int j = 1; j < ySize; j++) {
                for (int i = center - (piece + 2); i < center + (piece + 2); i++) {
                    matrix[k][ii++] = convertGreyDegree(bitmap.getPixel(i, j));
                }
            }
        }

        return matrix;
    }

    private int convertGreyDegree(int argb) {
        int red = (argb >> 16) & 0xff;
        int green = (argb >> 8) & 0xff;
        int blue = argb & 0xff;

        return (red * 30 + green * 59 + blue * 11 + 50) / 100;
    }

    @Override
    public ObservableSource<String> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> todo(BitmapUtil.bytesToBitmap(responseBody.bytes())));
    }

    public String parse(Response<ResponseBody> response) throws IOException {
        if (response.code() == 302) {
            throw new IOException();
        }
        return todo(BitmapUtil.bytesToBitmap(response.body().bytes()));
    }
}
