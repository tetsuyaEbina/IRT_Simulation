package jsise20190529;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/*
 * JSET全国大会に向けてのシミュレーションプログラム
 */

public class simulation {

	public static void main(String args[]) {

		int j; // 項目の添え字(番号)
		int i; // 受検者の添え字(番号)
		int n; // 真の能力値の添え字(番号)
		int res; // 反応データ
		double estimateTheta; // 受検者の能力値を格納する変数

		int id = 0;

		/*
		 * (θ= -2.0~2.0をそれぞれ)真の能力値として仮定する
		 */
		ArrayList<Double> trueThetaList = new ArrayList<Double>();
		trueThetaList.add(-2.0);
		trueThetaList.add(-1.0);
		trueThetaList.add(0.0);
		trueThetaList.add(1.0);
		trueThetaList.add(2.0);

		/*
		 * 回答データを保持する．添字1が真の能力値の添字，添字2がユーザ，添字3が課題番号
		 */
		int history[][][] = new int[5][101][16];

		/*
		 * 今回は1PLMで能力値推定を行うので、各カテゴリごとの困難度パラメータを設定する difficultyが困難度
		 */
		double difficulty[][] = {
				// 今回は決め打ちでやってます。本当はちゃんとしたルーブリックでやりたい
				{ -10, -3.18840359, -0.46700756, -0.35172005, 0.07128325, 0.09015796, 0.25497587, 0.31526141,
						0.45655353, 0.77400202, 1.28541061 },
				{ -10, -2.2073661, -1.5347504, -0.7521774, -0.6547943, -0.3875014, -0.1332358, 0.174615, 0.3723284,
						0.5462633, 1.8992623 },
				{ -10, -1.93787226, -1.19657849, -1.02348806, -0.71235776, -0.51943301, 0.09957525, 0.45551712,
						0.52445925, 0.85164371, 2.01207697 },
				{ -10, -1.84475451, -1.54788856, -1.08340809, -0.90728485, -0.42830414, -0.41043915, -0.08418848,
						0.19884044, 0.86473711, 1.25771032 },
				{ -10, -2.53185699, -2.2309966, -0.87015639, 0.05272088, 0.11338488, 0.14241448, 0.18019654, 0.59923875,
						0.67830729, 0.68176355 },
				{ -10, -1.271318357, -0.392208679, -0.203331575, -0.190119629, 0.006221373, 0.993973365, 1.007840067,
						1.010529478, 1.025318108, 1.760165642 },
				{ -10, -1.16790181, -0.82244144, -0.46590054, -0.18941339, 0.07024953, 0.36658897, 0.56553184,
						0.59872368, 0.6111494, 0.68737922 },
				{ -10, -1.68004701, -0.90265164, -0.09639192, 0.33525053, 0.3771146, 0.38944964, 0.74783277, 1.19085384,
						1.32798622, 1.81095739 },
				{ -10, -1.45288935, -0.90620164, -0.72986191, -0.03067936, 0.1722348, 0.6005291, 0.997383, 1.12405816,
						1.55927977, 2.15557381 },
				{ -10, -0.9234472, -0.4708751, -0.326891, -0.2762511, 0.1104019, 0.5020791, 0.5101884, 0.58991,
						1.127534, 1.2376989 },
				{ -10, -3.18840359, -0.46700756, -0.35172005, 0.07128325, 0.09015796, 0.25497587, 0.31526141,
						0.45655353, 0.77400202, 1.28541061 },
				{ -10, -0.9234472, -0.4708751, -0.326891, -0.2762511, 0.1104019, 0.5020791, 0.5101884, 0.58991,
						1.127534, 1.2376989 },
				{ -10, 0.1234472, 0.6708751, 0.726891, 0.8762511, 1.1104019, 1.5020791, 1.5101884, 1.58991, 2.127534,
						2.2376989 },
				{ -10, 0.68004701, 0.10265164, 0.19639192, 1.33525053, 1.3771146, 1.38944964, 1.74783277, 2.19085384,
						2.32798622, 2.81095739 },
				{ -10, -1.1102, 0.3141, 0.54231, 0.645213, 1.14, 2.35, 2.94, 3.32, 3.4, 3.74 },
				{ -10, 1.00, 1.10, 1.23, 2.21, 2.31, 2.43, 2.79, 3.01, 3.11, 3.47 }
		};

		// 項目の最高困難度レベル．添字は項目番号j
		int maxDifficultyLevel[] = { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };

		try {

			// ファイル書き出し用
			File csv = new File(
					"/Applications/Eclipse_4.8.0.app/Contents/workspace/IRT_Simulation/result/simulation_jsise_20190529.csv");
			// 追記モード
			// 文字コードの指定
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(csv), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			//bw.write("id,trueTheta,user,category,estimateTheta");
			bw.write("category");
			bw.newLine();

			/*
			 * 真の能力値(θ= -2.0~2.0をそれぞれ)それぞれ20人分繰り返す
			 */
			for (n = 0; n < trueThetaList.size(); n++) {
				for (i = 0; i < 20; i++) {
					estimateTheta = 0.0; // 能力値の初期値

					// resをエクセルで行に出力したいので、配列に格納する ここでは、宣言
					ArrayList<Integer> resList = new ArrayList<Integer>();
					double postTrueTheta = trueThetaList.get(n);

					for (j = 0; j < 16; j++) {

						if (j == 0) {
							res = res(j, trueThetaList.get(n), difficulty, maxDifficultyLevel);
						} else {
							res = res(j, postTrueTheta, difficulty, maxDifficultyLevel);
						}
						resList.add(res);
						history[n][i][j] = res; // 真の能力値の添字がn，ユーザの添字がi，課題番号がjのとき，カテゴリkで反応

						/*
						 * 能力値を求めるには、反応データ、項目の識別力、困難度、推定したθが必要
						 */
						estimateTheta = estimateTheta(id, n, j, difficulty, maxDifficultyLevel, history);
					}
					//bw.write(id + "," + trueThetaList.get(n) + "," + (n + 1) + "," + resList + "," + estimateTheta);
					bw.write(resList + ",");
					bw.newLine();
				}
				id++;
			}
			bw.close();
		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}

	/*
	 * 反応データ"res"を求めるプログラム
	 */
	public static int res(int j, double trueTheta, double difficulty[][], int maxDifficultyLevel[]) {

		// 0〜1未満のランダム値を生成
		double random = (double) (Math.random());
		int res = 0;

		double pj0 = 1;
		double pj1;
		double pj2;
		double pj3;
		double pj4;
		double pj5;
		double pj6;
		double pj7;
		double pj8;
		double pj9;
		double pj10;

		// 最新の能力パラメータを用いて，どの項目と反応するか求める
		// 今回は、1PLMで考えるので、Da == 1.7として考える
		switch (maxDifficultyLevel[j]) {

		case 10:// 最大困難度レベルが10のとき

			pj1 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][1]))));
			pj2 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][2]))));
			pj3 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][3]))));
			pj4 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][4]))));
			pj5 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][5]))));
			pj6 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][6]))));
			pj7 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][7]))));
			pj8 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][8]))));
			pj9 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][9]))));
			pj10 = 1 / (1 + (Math.exp(-1.7 * (trueTheta - difficulty[j][10]))));

			if (random < pj10) {
				res = 10;
			} else if (pj10 <= random && random < pj9) {
				res = 9;
			} else if (pj9 <= random && random < pj8) {
				res = 8;
			} else if (pj8 <= random && random < pj7) {
				res = 7;
			} else if (pj7 <= random && random < pj6) {
				res = 6;
			} else if (pj6 <= random && random <= pj5) {
				res = 5;
			} else if (pj5 <= random && random < pj4) {
				res = 4;
			} else if (pj4 <= random && random < pj3) {
				res = 3;
			} else if (pj3 <= random && random < pj2) {
				res = 2;
			} else if (pj2 <= random && random < pj1) {
				res = 1;
			} else if (pj1 <= random && random < pj0) {
				res = 0;
			}

			return res;
		}
		return res;
	}

	/*
	 * 現在の能力値を推定(ベイズのEAP)
	 */
	public static double estimateTheta(int i, int n, int j, double difficulty[][], int maxDifficultyLevel[],
			int history[][][]) {

		/*
		 * 求積点
		 */
		double[] Xm = { -4.15989, -3.92869, -3.69862, -3.46959, -3.24151, -3.01432, -2.78794, -2.5623, -2.33732,
				-2.11295, -1.88912, -1.66576, -1.44283, -1.22025, -0.997977, -0.775951, -0.554115, -0.332415, -0.110796,
				0.110796, 0.332415, 0.554115, 0.775951, 0.997977, 1.22025, 1.44283, 1.66576, 1.88912, 2.11295, 2.33732,
				2.5623, 2.78794, 3.01432, 3.24151, 3.46959, 3.69862, 3.92869, 4.15989 };

		/*
		 * 重み 総和が1になるように基準化した
		 */
		double[] Wm = { 7.08E-9, 4.57E-8, 2.63E-7, 1.35E-6, 6.22E-6, 2.57E-5, 9.52E-5, 3.17E-4, 9.53E-4, 0.00257927,
				0.006303, 0.0139157, 0.027779, 0.0501758, 0.082052, 0.121538, 0.16313, 0.19846, 0.21889, 0.21889,
				0.19846, 0.16313, 0.121538, 0.082052, 0.0501758, 0.027779, 0.0139157, 0.006303, 0.00257927, 9.53E-4,
				3.17E-4, 9.52E-5, 2.57E-5, 6.22E-6, 1.35E-6, 2.63E-7, 4.57E-8, 7.08E-9 };

		double result; // 能力値を格納する変数
		double ICC1;
		double ICC2;

		double Numerator[] = new double[Xm.length];// ベイズの分子
		double Denominator[] = new double[Xm.length]; // ベイズの分母

		double Nsum = 0.0;// 分子の和
		double Dsum = 0.0;// 分母の和

		for (int l = 0; l < Xm.length; l++) {
			result = 1;
			for (int m = 0; m < (j + 1); m++) {// 課題数分繰り返す

				switch (maxDifficultyLevel[m]) {

				case 10:// 最大困難度レベルが10のとき
					switch (history[n][i][m]) {
					case 0:
						ICC1 = 1.0;
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][1])));
						result *= ICC1 - ICC2;
						break;
					case 1:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][1])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][2])));
						result *= ICC1 - ICC2;
						break;
					case 2:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][2])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][3])));
						result *= ICC1 - ICC2;
						break;
					case 3:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][3])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][4])));
						result *= ICC1 - ICC2;
						break;
					case 4:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][4])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][5])));
						result *= ICC1 - ICC2;
						break;
					case 5:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][5])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][6])));
						result *= ICC1 - ICC2;
						break;
					case 6:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][6])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][7])));
						result *= ICC1 - ICC2;
						break;
					case 7:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][7])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][8])));
						result *= ICC1 - ICC2;
						break;
					case 8:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][8])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][9])));
						result *= ICC1 - ICC2;
						break;
					case 9:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][9])));
						ICC2 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][10])));
						result *= ICC1 - ICC2;
						break;
					case 10:
						ICC1 = 1.0 / (1.0 + Math.exp(-1.7 * (Xm[l] - difficulty[m][10])));
						result *= ICC1;
						ICC2 = 0.0;
						break;
					default:
						break;
					}
				}
			}
			Numerator[l] = result * Xm[l] * Wm[l];
			Denominator[l] = result * Wm[l];
		}

		for (int h = 0; h < Xm.length; h++) {

			Nsum += Numerator[h];
			Dsum += Denominator[h];
		}

		double estimateTheta = Nsum / Dsum;

		return estimateTheta;
	}

}