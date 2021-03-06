package MySpringBoot.parentDemo.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 
 * 比较两个字符串的相似度
 * 
 */
public class StrCompareRateUtil {

	public static void main(String[] args) {

		String strA = "我--是中";

		String strB = "我是中国人";

		double result = SimilarDegree(strA, strB);

		if (result >= 0.4 && result < 0.6) {
			System.out.println("本次作业的成绩为：" + similarityResult(result) + result + "--及格");

		} else if (result >= 0.6 && result < 0.8) {

			System.out.println("本次作业的成绩为：" + similarityResult(result) + result + "--良好");

		} else if (result >= 0.8) {

			System.out.println("本次作业的成绩为：" + similarityResult(result) + result + "--优秀");
		} else {

			System.out.println("本次作业的成绩为：" + similarityResult(result) + result + "--不及格");

		}
		
		String  str= "论据容易理解,把它们分成小块,并以相似的格式和句子结构呈现每一个论据";
        String target = "根据领域关系可以合并领域相同或相似的句子，得到句群及其领域";
        System.out.println("similarityRatio=" + getSimilarityRatio(str, target));
		
	}

	/**
	 * 
	 * 相似度转百分比
	 * 
	 */

	public static String similarityResult(double resule) {

		return NumberFormat.getPercentInstance(new Locale("en ", "US ")).format(resule);

	}

	/**
	 * 
	 * 相似度比较
	 * 
	 * @param strA
	 * 
	 * @param strB
	 * 
	 * @return
	 * 
	 */

	public static double SimilarDegree(String strA, String strB) {

		String newStrA = removeSign(strA);
		// System.out.println(newStrA);
		String newStrB = removeSign(strB);
		// System.out.println(newStrB);

		int temp = Math.max(newStrA.length(), newStrB.length());
		int lenA = newStrA.length();
		int lenB = newStrB.length();
		System.out.println(temp);
		int temp2 = 0;
		// 在此判断newStrA和newStrB的长度
		if (lenA >= lenB ? true : false) {
			temp2 = longestCommonSubstring(newStrA, newStrB).length();
		} else {
			temp2 = longestCommonSubstring(newStrB, newStrA).length();
		}

		System.out.println(temp2);
		return temp2 * 1.0 / temp;

	}

	private static String removeSign(String str) {

		StringBuffer sb = new StringBuffer();

		for (char item : str.toCharArray())

			if (charReg(item)) {

				System.out.println("--" + item);

				sb.append(item);

			}
		System.out.println(sb.toString());

		return sb.toString();

	}

	// 利用正则表达式来解析字符串
	private static boolean charReg(char charValue) {

		return (charValue >= 0x4E00 && charValue <= 0X9FA5)

				|| (charValue >= 'a' && charValue <= 'z')

				|| (charValue >= 'A' && charValue <= 'Z')

				|| (charValue >= '0' && charValue <= '9');

	}

	// 解析两个字符串的相同部分的长度，返回公共部分，strA字符串长度 > strB字符串
	private static String longestCommonSubstring(String strA, String strB) {

		char[] chars_strA = strA.toCharArray();

		char[] chars_strB = strB.toCharArray();

		int m = chars_strA.length;

		int n = chars_strB.length;

		int[][] matrix = new int[m + 1][n + 1];

		for (int i = 1; i <= m; i++) {

			for (int j = 1; j <= n; j++) {

				if (chars_strA[i - 1] == chars_strB[j - 1])

					matrix[i][j] = matrix[i - 1][j - 1] + 1;

				else

					matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);

			}

		}

		char[] result = new char[matrix[m][n]];

		int currentIndex = result.length - 1;

		while (matrix[m][n] != 0) {

			if (matrix[n] == matrix[n - 1])

				n--;

			else if (matrix[m][n] == matrix[m - 1][n])

				m--;

			else {

				result[currentIndex] = chars_strA[m - 1];

				currentIndex--;

				n--;

				m--;

			}
		}

		return new String(result);

	}

	/**
	 * 比较两个字符串的相似度，核心算法是用一个2维数组记录每个字符串是否相同，如果相同记为0，不相同记为1，
	 * 每行，每列的相同个数累加，则数组最后一个数为不相同个数的总数。
	 * 从而判断这两个字符串的相似度，在判断大小写时，没有区分大小写，即大小写视为相同的字符。
	 * @param str
	 * @param target
	 * @return
	 */
	private static int compare(String str, String target) {
		int d[][]; // 矩阵
		int n = str.length();
		int m = target.length();
		int i; // 遍历str的
		int j; // 遍历target的
		char ch1; // str的
		char ch2; // target的
		int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { // 初始化第一列
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) { // 初始化第一行
			d[0][j] = j;
		}

		for (i = 1; i <= n; i++) { // 遍历str
			ch1 = str.charAt(i - 1);
			// 去匹配target
			for (j = 1; j <= m; j++) {
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
			}
		}
		return d[n][m];
	}

	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	/**
	 * 获取两字符串的相似度
	 */

	public static float getSimilarityRatio(String str, String target) {
		// 去除空白字符、换行、标点符号
		String regex = "[\\pP\\p{Punct}\\s]";
		return 1 - (float) compare(str.replaceAll(regex, ""), target.replaceAll(regex, ""))
				/ Math.max(str.length(), target.length());
	}

}
