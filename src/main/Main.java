package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

	static int numObjects = 8;
	static int numAttr = 3;
	static double[][] prior = new double[numObjects][numObjects];
	static Random rand = new Random();

	static int[][] attr;

	static double eps = 0.000000000001;

	public static void main(String[] args) {
		prior = genPrior(numObjects, numObjects);
		System.out.printf("prior: \n%s \n", toString2DDoubleArray(prior));

		initializeAttributes();
		System.out.printf("attributes: \n %s \n\n", toString2DIntArray(attr));

		Map<Integer, Integer> aliceStrategyStageOne = getAliceStrategyStageOne();
		System.out.printf("Alice stage 1 strategy: %s", aliceStrategyStageOne.toString());
		System.out.println();

//		int aliceReportAttrIndex = 0;
//		int aliceReportAttrValue = 1;
		
		for (int aliceReportAttrIndex = 0; aliceReportAttrIndex < numAttr; aliceReportAttrIndex++) {
			for (int aliceReportAttrValue = 0; aliceReportAttrValue < 2; aliceReportAttrValue++) {
				Map<Integer, Integer> probDist = getDistOverObjs(aliceReportAttrIndex, aliceReportAttrValue, aliceStrategyStageOne);
			}
		}
		Map<Integer, Integer> boStrategyStageOne = getBoStrategyStageOne();
		System.out.printf("Bo stage 1 strategy: %s", boStrategyStageOne.toString());
		System.out.println();
	}

	private static Map<Integer, Integer> getDistOverObjs(int aliceReportAttrIndex,
			int aliceReportAttrValue, Map<Integer, Integer> aliceStrategyStageOne) {
		
		List<Integer> possibleObjects = new ArrayList<Integer>();
		for (int i = 0; i < numObjects; i++) {
			int attrIndex = aliceStrategyStageOne.get(i).intValue();
			int attrValue = attr[i][attrIndex];
			if (attrIndex == aliceReportAttrIndex && attrValue == aliceReportAttrValue) {
				possibleObjects.add(i);
			}
		}
		System.out.printf("attrIndex %d, attrValue %d, possibleObjs %s\n", 
				aliceReportAttrIndex, aliceReportAttrValue, possibleObjects.toString());
		return null;
	}

	private static void initializeAttributes() {
		attr = new int[numObjects][numAttr];
		
		int attrValue = 0;
		for (int objIndex = 0; objIndex < numObjects; objIndex++) {
			String binaryAttrValue = Integer.toBinaryString(attrValue);
			int length = binaryAttrValue.length();
			while (length < 3) {
				binaryAttrValue = "0" + binaryAttrValue;
				length = binaryAttrValue.length();
			}
			for (int j = 0; j < binaryAttrValue.length(); j++) {
				if (binaryAttrValue.charAt(j) == '1') {
					attr[objIndex][j] = 1;
				} else {
					attr[objIndex][j] = 0;
				}
			}
			
			attrValue++;
		}
	}

	private static Map<Integer, Integer> getAliceStrategyStageOne() {

		Map<Integer, Integer> aliceStrategyStageOne = 
				new HashMap<Integer, Integer>();
		
		for (int aliceObjIndex = 0; aliceObjIndex < numObjects; aliceObjIndex++) {

			
			double[] boProbCorrect = new double[numAttr];
			for (int i = 0; i < numAttr; i++) {
				boProbCorrect[i] = 0.0;
			}

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

//			System.out.printf("Alice gets obj %d, ", aliceObjIndex);
			if (boProbCorrect[0] > boProbCorrect[1]) {
//				System.out.printf("Alice should report attr 0\n");
				aliceStrategyStageOne.put(aliceObjIndex, 0);
			} else if (boProbCorrect[0] < boProbCorrect[1]) {
//				System.out.printf("Alice should report attr 1\n");
				aliceStrategyStageOne.put(aliceObjIndex, 1);
			} else {
//				System.out.printf("Alice should report attr 0 or 1\n");
				aliceStrategyStageOne.put(aliceObjIndex, 0);
			}
		}
		
		return aliceStrategyStageOne;
	}

	private static Map<Integer, Integer> getBoStrategyStageOne() {
		
		Map<Integer, Integer> boStrategyStageOne = new HashMap<Integer, Integer>();
		
		for (int boObjIndex = 0; boObjIndex < numObjects; boObjIndex++) {

			double[] aliceProbCorrect = new double[numAttr];
			for (int i = 0; i < numAttr; i++) {
				aliceProbCorrect[i] = 0.0;
			}
			
			for (int attrIndex = 0; attrIndex < numAttr; attrIndex++) {

				// System.out.printf("If Bo reports attr %d, ", attrIndex);

				for (int aliceObjIndex = 0; aliceObjIndex < numObjects; aliceObjIndex++) {

					double probAliceGivenBo = probOAGivenOB(aliceObjIndex,
							boObjIndex);
					int attrValue = attr[boObjIndex][attrIndex];
					String aliceDecision = getBoDecision(attrIndex, attrValue,
							aliceObjIndex);
					boolean aliceCorrect = boDecisionCorrect(boObjIndex,
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

//			System.out.printf("Bo gets obj %d, ", boObjIndex);
			if (aliceProbCorrect[0] > aliceProbCorrect[1]) {
//				System.out.printf("Bo should report attr 0\n");
				boStrategyStageOne.put(new Integer(boObjIndex), new Integer(0));
			} else if (aliceProbCorrect[0] < aliceProbCorrect[1]) {
//				System.out.printf("Bo should report attr 1\n");
				boStrategyStageOne.put(new Integer(boObjIndex), new Integer(1));
			} else {
//				System.out.printf("Bo should report attr 0 or 1\n");
				boStrategyStageOne.put(new Integer(boObjIndex), new Integer(0));
			}
		}
		return boStrategyStageOne;
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

	private static double[][] genPrior(int size, int innerSize) {
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
