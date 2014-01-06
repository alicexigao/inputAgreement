package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

	static int numObjects = 4;
	static int numAttr = 2;
	static double[][] prior = new double[numObjects][numObjects];
	static Random rand = new Random();

	static int[][] attr;

	static double eps = 0.000000000001;

	public static void main(String[] args) {
		prior = genProbDist(numObjects, numObjects);
		System.out.printf("prior: \n%s \n", toString2DDoubleArray(prior));

		initAttr();
		System.out.printf("attributes: \n %s \n\n", toString2DIntArray(attr));

		Map<Integer, Integer> aliceStrategy = new HashMap<Integer, Integer>();
		getAliceStrategy();
		System.out.println();

		getBoStrategy();
		System.out.println();
	}

	private static void getAliceStrategy() {

		
		
		for (int aliceObjIndex = 0; aliceObjIndex < numObjects; aliceObjIndex++) {

			System.out.printf("Alice gets obj %d, ", aliceObjIndex);

			double[] boProbCorrect = new double[] { 0.0, 0.0 };

			for (int attrIndex = 0; attrIndex < numAttr; attrIndex++) {

				// System.out.printf("If Alice reports attr %d, ", attrIndex);

				for (int boObjIndex = 0; boObjIndex < numObjects; boObjIndex++) {
					double probBoGivenAlice = probOBGivenOA(boObjIndex,
							aliceObjIndex);
					int attrValue = attr[aliceObjIndex][attrIndex];
					String boDecision = getBoDecision(attrIndex, attrValue,
							boObjIndex);
					boolean boCorrect = boDecisionCorrect(aliceObjIndex,
							attrIndex, boObjIndex);
					// System.out.printf("With prob %.2f, Bob gets obj %d, says %s ",
					// probBoGivenAlice, boObjIndex, boDecision);
					// if (boCorrect)
					// System.out.printf("and is correct\n");
					// else
					// System.out.printf("and is wrong\n");

					if (boCorrect)
						boProbCorrect[attrIndex] += probBoGivenAlice;
				}

				// System.out.printf("Prob(Bo correct) = %.2f \n",
				// boProbCorrect[attrIndex]);
			}

			if (boProbCorrect[0] > boProbCorrect[1])
				System.out.printf("Alice should report attr 0\n");
			else if (boProbCorrect[0] < boProbCorrect[1])
				System.out.printf("Alice should report attr 1\n");
			else
				System.out.printf("Alice should report attr 0 or 1\n");
			// System.out.println();
			// System.out.println();

		}
	}

	private static void getBoStrategy() {
		for (int boboObjIndex = 0; boboObjIndex < numObjects; boboObjIndex++) {

			System.out.printf("Bo gets obj %d, ", boboObjIndex);

			double[] aliceProbCorrect = new double[] { 0.0, 0.0 };

			for (int attrIndex = 0; attrIndex < numAttr; attrIndex++) {

				// System.out.printf("If Bo reports attr %d, ", attrIndex);

				for (int aliceObjIndex = 0; aliceObjIndex < numObjects; aliceObjIndex++) {

					double probAliceGivenBo = probOAGivenOB(aliceObjIndex,
							boboObjIndex);
					int attrValue = attr[boboObjIndex][attrIndex];
					String aliceDecision = getBoDecision(attrIndex, attrValue,
							aliceObjIndex);
					boolean aliceCorrect = boDecisionCorrect(boboObjIndex,
							attrIndex, aliceObjIndex);
					// System.out.printf("With prob %.2f, Alice gets obj %d, says %s ",
					// probAliceGivenBo, aliceObjIndex, aliceDecision);
					// if (aliceCorrect)
					// System.out.printf("and is correct\n");
					// else
					// System.out.printf("and is wrong\n");

					if (aliceCorrect)
						aliceProbCorrect[attrIndex] += probAliceGivenBo;
				}

				// System.out.printf("Prob(Alice correct) = %.2f \n",
				// aliceProbCorrect[attrIndex]);
			}

			if (aliceProbCorrect[0] > aliceProbCorrect[1])
				System.out.printf("Bo should report attr 0\n");
			else if (aliceProbCorrect[0] < aliceProbCorrect[1])
				System.out.printf("Bo should report attr 1\n");
			else
				System.out.printf("Bo should report attr 0 or 1\n");
			// System.out.println();
			// System.out.println();

		}
	}

	private static boolean boDecisionCorrect(int oa, int attrIndex, int ob) {
		int attrValue = getAttrValue(oa, attrIndex);
		double probSameBo = getProbSameBo(attrIndex, attrValue, ob);
		if (probSameBo >= 0.5) {
			// Bo's decision is same
			return oa == ob;
		} else {
			// Bo's decision is diff
			return oa != ob;
		}
	}

	private static String getBoDecision(int attrIndex, int attrValue, int ob) {
		double probSameBo = getProbSameBo(attrIndex, attrValue, ob);
		if (probSameBo >= 0.5) {
			// Bo's decision is same
			return "same";
		} else {
			// Bo's decision is diff
			return "diff";
		}
	}

	private static double getProbSameBo(int oaAttrIndex, int oaAttrValue, int ob) {
		return getProbOaGivenAttr(ob, oaAttrIndex, oaAttrValue);
	}

	private static double getProbOaGivenAttr(int oa, int attrIndex,
			int attrValue) {
		List<Integer> objs = new ArrayList<Integer>();
		for (int i = 0; i < attr.length; i++) {
			if (attr[i][attrIndex] == attrValue)
				objs.add(i);
		}

		if (!objs.contains(new Integer(oa)))
			return 0;

		double total = 0;
		double probOa = 0;
		for (Integer obj : objs) {
			double temp = getProbOa(obj.intValue());
			total += temp;
			if (obj.intValue() == oa)
				probOa = temp;
		}
		return probOa / total;
	}

	private static double getProbOa(int oa) {
		double total = 0;
		for (int j = 0; j < prior[oa].length; j++) {
			total += prior[oa][j];
		}
		return total;
	}

	private static void initAttr() {
		attr = new int[numObjects][numAttr];
		attr[0][0] = 0;
		attr[0][1] = 0;

		attr[1][0] = 0;
		attr[1][1] = 1;

		attr[2][0] = 1;
		attr[2][1] = 0;

		attr[3][0] = 1;
		attr[3][1] = 1;
	}

	private static double[][] genProbDist(int size, int innerSize) {
		double[] temp = new double[size * innerSize];
		double total = 0;
		for (int i = 0; i < temp.length; i++) {
			temp[i] = rand.nextDouble();
			total += temp[i];
		}
		for (int i = 0; i < temp.length; i++) {
			temp[i] = temp[i] / total;
			temp[i] = Math.round(temp[i] * 1000) / 1000.0;
		}

		double totalMinusLast = 0;
		for (int i = 0; i < temp.length - 1; i++) {
			totalMinusLast += temp[i];
		}
		temp[temp.length - 1] = Math.round((1 - totalMinusLast) * 1000) / 1000.0;

		double[][] returnVal = new double[size][innerSize];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < innerSize; j++) {
				returnVal[i][j] = temp[i * size + j];
			}
		}
		return returnVal;
	}

	private static String toString2DIntArray(int[][] array) {
		String[] temp = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			temp[i] = Arrays.toString(array[i]);
		}
		return Arrays.toString(temp);
	}

	private static String toString2DDoubleArray(double[][] array) {
		String[] temp = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			temp[i] = Arrays.toString(array[i]);
		}
		StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			retVal.append(temp[i] + "\n");
		}

		return retVal.toString();
	}

	private static double probOAGivenOB(int oa, int ob) {
		double probOb = 0;
		double probOaOb = prior[oa][ob];
		for (int i = 0; i < prior.length; i++) {
			for (int j = 0; j < prior[i].length; j++) {
				if (j == probOb)
					probOb += prior[i][j];
			}
		}
		return probOaOb / probOb;
	}

	private static double probOBGivenOA(int ob, int oa) {
		double probOaOb = prior[oa][ob];
		double probOa = 0;
		for (int j = 0; j < prior[oa].length; j++) {
			probOa += prior[oa][j];
		}
		return probOaOb / probOa;
	}

	private static int getAttrValue(int obj, int attrIndex) {
		return attr[obj][attrIndex];
	}

}
