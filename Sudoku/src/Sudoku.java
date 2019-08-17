import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

@SuppressWarnings("unused")
public class Sudoku {

	static int size;
	static String[] rep;
	static String[] cand;

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		BufferedReader reader = new BufferedReader(new FileReader(in.nextLine()));
		String line = reader.readLine();
		size = Integer.parseInt(line);
		rep = new String[size];
		int i = 0;
		line = reader.readLine();
		while (line != null) {
			String nums[] = line.split(" ");
			for (int j = 0; j < nums.length; j++) {
				rep[i] = nums[j];
				if (rep[i].equals("X")) {
					rep[i] = "";
				}
				i++;
			}
			line = reader.readLine();
		}
		printRep();
		System.out.println("--------------------------------------------------------------");
		cand = new String[size];
		for (int j = 0; j < cand.length; j++) {
			cand[j] = "--";
		}
		boolean going = true;
		while (!isCorrect() && going) {
			going = false;
			updateCand();
			System.out.print("");
			for (int j = 0; j < cand.length; j++) {

				// NAKED SINGLE
				if (cand[j].length() == 1) {
					rep[j] = cand[j];
					going = true;
				}
			}

			// HIDDEN SINGLE
			if (!going) {
				going = hiddenSingle("ROW") || hiddenSingle("COLUMN") || hiddenSingle("BOX");
			}
			if (!going) {
				System.out.println("STUMPED!");
				printRep();
				System.exit(0);
			}

			// TODO - LOCKED CANDIDATE (TYPE 1 HANDLED) (TYPE 2 AFTER TWINS)
			// TODO - NAKED/HIDDEN TUPLES
		}
		System.out.println("SOLVED!");
		printRep();
		reader.close();
		in.close();
	}

	private static void printRep() {
		int width = (int) Math.sqrt((double) size);
		int numberOfDigits = width / 10;
		System.out.print("╔");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < (numberOfDigits + 1); j++) {
				System.out.print("═");
			}
			if (i + 1 != (width)) {
				System.out.print("╦");
			}
		}
		System.out.println("╗");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				String toPrint = rep[width * i + j] + "";
				if (toPrint.length() == 0) {
					toPrint = " ";
				}
				while (toPrint.length() < numberOfDigits + 1) {
					toPrint += " ";
				}
				System.out.print("║");
				System.out.print(toPrint);
			}
			System.out.println("║");

			if (i + 1 != width) {
				System.out.print("╠");
				for (int j = 0; j < width; j++) {
					for (int k = 0; k < (numberOfDigits + 1); k++) {
						System.out.print("═");
					}
					if (j + 1 != width) {
						System.out.print("╬");
					}
				}
				System.out.println("╣");
			}
		}
		System.out.print("╚");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < (numberOfDigits + 1); j++) {
				System.out.print("═");
			}
			if (i + 1 != width) {
				System.out.print("╩");
			}
		}
		System.out.println("╝");
	}

	private static boolean hiddenSingle(String option) {
		boolean playMade = false;
		int width = (int) Math.sqrt((double) size);
		ArrayList<ArrayList<Integer>> indices = null;
		ArrayList<Integer> index = new ArrayList<Integer>();
		if (option.equals("ROW")) {
			indices = indicesOfRows();
		} else if (option.equals("COLUMN")) {
			indices = indicesOfColumns();
		} else if (option.equals("BOX")) {
			indices = indicesOfBoxes();
		}

		// LOOPING THROUGH EACH GROUP
		for (int i = 0; i < indices.size(); i++) {
			index = indices.get(i);
			Map<String, Integer> allCandsInGroup = new HashMap<String, Integer>();

			// LOOPING THROUGH EACH ELEMENT OF A GROUP
			for (int j = 0; j < width; j++) {
				String candsInQuestion[] = new String[100];
				candsInQuestion = cand[index.get(j)].split(" ");
				for (int k = 0; k < candsInQuestion.length; k++) {
					if (allCandsInGroup.containsKey(candsInQuestion[k]) && !candsInQuestion[k].equals("--")) {
						allCandsInGroup.replace(candsInQuestion[k], allCandsInGroup.get(candsInQuestion[k]) + 1);
					} else if (!candsInQuestion[k].equals("--")) {
						allCandsInGroup.put(candsInQuestion[k], 1);
					}
				}
			}
			for (Entry<String, Integer> entry : allCandsInGroup.entrySet()) {
				// ONLY ONE OCCURENCE OF THIS ENTRY
				if (entry.getValue() == 1) {
					String answerToBeFilled = " " + entry.getKey() + " ";
					for (int j = 0; j < width; j++) {
						if ((" " + (cand[index.get(j)]) + " ").contains(answerToBeFilled)) {
							rep[index.get(j)] = answerToBeFilled.substring(1, answerToBeFilled.length() - 1);
							playMade = true;
						}
					}
				}
			}
		}
		return playMade;
	}

	private static void updateCand() {
		cand = new String[size];
		int width = (int) Math.sqrt((double) size);
		for (int i = 0; i < size; i++) {
			String candidates = " ";
			for (int j = 0; j < width; j++) {
				candidates += (j + 1) + " ";
			}
			ArrayList<ArrayList<Integer>> rowIndices = indicesOfRows();
			ArrayList<ArrayList<Integer>> boxIndices = indicesOfBoxes();
			ArrayList<ArrayList<Integer>> columnIndices = indicesOfColumns();
			ArrayList<Integer> rowIndex = new ArrayList<Integer>();
			ArrayList<Integer> boxIndex = new ArrayList<Integer>();
			ArrayList<Integer> columnIndex = new ArrayList<Integer>();
			for (int j = 0; j < rowIndices.size(); j++) {
				if (rowIndices.get(j).contains(i)) {
					rowIndex = rowIndices.get(j);
				}
			}
			for (int j = 0; j < boxIndices.size(); j++) {
				if (boxIndices.get(j).contains(i)) {
					boxIndex = boxIndices.get(j);
				}
			}
			for (int j = 0; j < columnIndices.size(); j++) {
				if (columnIndices.get(j).contains(i)) {
					columnIndex = columnIndices.get(j);
				}
			}

			// IF rep[i] IS BLANK THEN UPDATE NEEDED
			if (rep[i].equals("")) {
				for (int j = 0; j < rowIndex.size(); j++) {
					if (!rep[rowIndex.get(j)].equals("")) {
						candidates = candidates.replace(" " + rep[rowIndex.get(j)] + " ", " ");
					}
				}
				for (int j = 0; j < boxIndex.size(); j++) {
					if (!rep[boxIndex.get(j)].equals("")) {
						candidates = candidates.replace(" " + rep[boxIndex.get(j)] + " ", " ");
					}
				}
				for (int j = 0; j < columnIndex.size(); j++) {
					if (!rep[columnIndex.get(j)].equals("")) {
						candidates = candidates.replace(" " + rep[columnIndex.get(j)] + " ", " ");
					}
				}
				try {
					candidates = candidates.substring(1, candidates.length() - 1);
				} catch (Exception e) {
				}
			} else {
				candidates = "--";
			}
			cand[i] = candidates;
		}

		// THIS IS WHERE THINGS GET TRICKY

		eliminateLockedCandidates("c");
		eliminateLockedCandidates("r");

		String settings = "rcb";
		for (int i = 0; i < settings.length(); i++) {
			for (int j = 2; j < ((width / 2) + 1); j++) {
				eliminateNTuplesKinda(settings.charAt(i) + "", j);
			}
		}
	}

	// WORKS ONLY IN THE PERFECT SCENARIO FOR TWO PLUS TUPLES (RARE APPARENTLY) (NOT
	// GOOD)
	private static void eliminateNTuplesKinda(String option, int tupleSize) {
		int width = (int) Math.sqrt((double) size);

		int widthOfBox = (int) Math.sqrt((double) width);
		ArrayList<ArrayList<String>> candArr = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cand.length; i++) {
			candArr.add(new ArrayList<String>(Arrays.asList(cand[i].split(" "))));
		}
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		if (option.equals("r")) {
			indices = indicesOfRows();
		} else if (option.equals("c")) {
			indices = indicesOfColumns();
		} else if (option.equals("b")) {
			indices = indicesOfBoxes();
		}
		Map<String, ArrayList<Integer>> occurences = new HashMap<String, ArrayList<Integer>>();

		// i REFERS TO THE HOUSE NUMBER
		for (int i = 0; i < indices.size(); i++) {
			ArrayList<Integer> houseInQuestion = indices.get(i);

			// INITIALIZING occurences
			for (int i2 = 0; i2 < width; i2++) {
				occurences.put(Integer.toString(i2 + 1), new ArrayList<Integer>());
			}
			// j REFERS TO THE J-TH ELEMENT OF THE HOUSE IN QUESTION
			// THIS FOR LOOP BUILDS A MAP OF [CANDIDATES]:[HOUSES IT APPEARS IN]
			for (int j = 0; j < houseInQuestion.size(); j++) {
				int index = houseInQuestion.get(j);
				ArrayList<String> candidates = candArr.get(index);
				for (int k = 0; k < candidates.size(); k++) {
					String candidate = candidates.get(k);
					if (!candidate.equals("--")) {
						occurences.get(candidate).add(index);
					}
				}
			}

			// BUILDING A CONCENTRATED VERSION OF occurrences CALLED conOcc
			// THIS MAP CONTAINS ONLY ELEMENTS WITH VALUES OF SIZE tupleSize
			ArrayList<Integer> numbersInQuestion = new ArrayList<Integer>();
			Map<String, ArrayList<Integer>> conOcc = new HashMap<String, ArrayList<Integer>>();
			for (Entry<String, ArrayList<Integer>> entry : occurences.entrySet()) {
				// ONLY ONE OCCURENCE OF THIS ENTRY
				if (entry.getValue().size() == tupleSize) {
					conOcc.put(entry.getKey(), entry.getValue());
					numbersInQuestion.add(Integer.parseInt(entry.getKey()));
				}
			}
			// TODO - conOcc MAY NOT BE HELPFUL...
			// ROLLING WITH IT ANYWAY

			for (int j = 0; j < numbersInQuestion.size(); j++) {
				int occurenceCount = 0;
				ArrayList<Integer> candidatesToSave = new ArrayList<Integer>();
				ArrayList<Integer> cellsToClear = new ArrayList<Integer>();
				for (int k = 0; k < numbersInQuestion.size(); k++) {
					if ((conOcc.get(numbersInQuestion.get(j) + "").equals(conOcc.get(numbersInQuestion.get(k) + "")))) {
						occurenceCount++;
						candidatesToSave.add(numbersInQuestion.get(k));
					}
				}
				ArrayList<String> candToSaveString = new ArrayList<String>();
				for (int k = 0; k < candidatesToSave.size(); k++) {
					candToSaveString.add(candidatesToSave.get(k) + "");
				}
				if (occurenceCount == tupleSize) {
					cellsToClear = conOcc.get(numbersInQuestion.get(j) + "");
					for (int k = 0; k < cellsToClear.size(); k++) {
						candArr.set(cellsToClear.get(k), candToSaveString);
					}
				}

			}
		}

		// REBUILDING cand[]
		for (int k = 0; k < candArr.size(); k++) {
			String candFill = "";
			for (int l = 0; l < candArr.get(k).size(); l++) {
				candFill += " " + candArr.get(k).get(l);
			}
			if (candFill.equals("")) {
				candFill = " --";
			}
			cand[k] = candFill.substring(1, candFill.length());
		}
	}

	private static void eliminateLockedCandidates(String option) {
		int width = (int) Math.sqrt((double) size);
		int widthOfBox = (int) Math.sqrt((double) width);
		ArrayList<ArrayList<String>> candArr = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cand.length; i++) {
			candArr.add(new ArrayList<String>(Arrays.asList(cand[i].split(" "))));
		}
		ArrayList<ArrayList<Integer>> boxIndices = indicesOfBoxes();
		for (int i = 0; i < boxIndices.size(); i++) {
			ArrayList<ArrayList<Integer>> rowsInBox = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> columnsInBox = new ArrayList<ArrayList<Integer>>();
			for (int j = 0; j < widthOfBox; j++) {
				rowsInBox.add(new ArrayList<Integer>());
				columnsInBox.add(new ArrayList<Integer>());
			}
			for (int j = 0; j < boxIndices.get(i).size(); j++) {
				columnsInBox.get(boxIndices.get(i).get(j) % widthOfBox).add(boxIndices.get(i).get(j));
				rowsInBox.get((boxIndices.get(i).get(j) / width) % widthOfBox).add(boxIndices.get(i).get(j));
			}

			// STEPPING STONE

			if (option.equals("c")) {

			} else {
				columnsInBox = rowsInBox;
			}

			// ITERATE THROUGH 1-WIDTH TO CHECK UNIQUENESS
			for (int j = 1; j != width + 1; j++) {

				// COLUMN WORK
				int columnUniqueCount = 0;
				int columnInWhichThisAppears = -1;
				for (int k = 0; k < columnsInBox.size(); k++) {
					boolean willAdd = false;
					boolean hasAdded = false;
					for (int l = 0; l < columnsInBox.size(); l++) {
						if (candArr.get(columnsInBox.get(k).get(l)).contains(Integer.toString(j))) {
							willAdd = true;
						}
						if (willAdd && !hasAdded) {
							columnUniqueCount++;
							columnInWhichThisAppears = k;
							hasAdded = true;
						}
						willAdd = false;
					}
				}
				// IF j IS UNIQUE IN A COLUMN IN BOX i
				if (columnUniqueCount == 1) {
					ArrayList<Integer> indicesOfSaidColumn = columnsInBox.get(columnInWhichThisAppears);
					int modColumn = 0;
					int comparison = 0;
					if (option.equals("c")) {
						modColumn = indicesOfSaidColumn.get(0) % width;
					} else if (option.equals("r")) {
						modColumn = indicesOfSaidColumn.get(0) / width;
					}
					for (int k = 0; k < candArr.size(); k++) {
						if (option.equals("c")) {
							comparison = k % width;
						} else if (option.equals("r")) {
							comparison = k / width;
						}
						if (!indicesOfSaidColumn.contains(k) && comparison == modColumn) {
							try {
								if (candArr.get(k).indexOf(j + "") != -1) {
									candArr.get(k).remove(candArr.get(k).indexOf(j + ""));
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
		for (int k = 0; k < candArr.size(); k++) {
			String candFill = "";
			for (int l = 0; l < candArr.get(k).size(); l++) {
				candFill += " " + candArr.get(k).get(l);
			}
			if (candFill.equals("")) {
				candFill = " --";
			}
			cand[k] = candFill.substring(1, candFill.length());
		}
	}

	private static boolean isCorrect() {
		boolean isCorrect = check("b") && check("r") && check("c");
		return isCorrect;
	}

	private static ArrayList<ArrayList<Integer>> indicesOfBoxes() {
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		int width = (int) Math.sqrt((double) size);
		int widthOfBox = (int) Math.sqrt((double) width);
		for (int i = 0; i < width; i++) {
			indices.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < size; i++) {
			int columnAlignment = ((i % width) / widthOfBox);
			int rowNum = i / width;
			int rowOfBoxes = rowNum / widthOfBox;
			int boxNum = rowOfBoxes * widthOfBox + columnAlignment;
			indices.get(boxNum).add(i);
		}
		return indices;
	}

	private static ArrayList<ArrayList<Integer>> indicesOfRows() {
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		int width = (int) Math.sqrt((double) size);
		for (int i = 0; i < width; i++) {
			indices.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < size; i++) {
			int rowNum = i / width;
			indices.get(rowNum).add(i);
		}
		return indices;
	}

	private static ArrayList<ArrayList<Integer>> indicesOfColumns() {
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		int width = (int) Math.sqrt((double) size);
		for (int i = 0; i < width; i++) {
			indices.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < size; i++) {
			int columnNum = i % width;
			indices.get(columnNum).add(i);
		}
		return indices;
	}

	private static boolean check(String option) {
		boolean correct = true;
		int sizeOfBox = (int) Math.sqrt((double) size);
		String correctComparison = "";
		for (int i = 0; i < sizeOfBox; i++) {
			correctComparison += Integer.toString(i + 1);
		}
		ArrayList<ArrayList<Integer>> indices = null;
		if (option.equals("b")) {
			indices = indicesOfBoxes();
		} else if (option.equals("r")) {
			indices = indicesOfRows();
		} else if (option.equals("c")) {
			indices = indicesOfColumns();
		}
		for (int i = 0; i < indices.size(); i++) {
			String box[] = new String[sizeOfBox];
			for (int j = 0; j < sizeOfBox; j++) {
				box[j] = rep[indices.get(i).get(j)];
			}
			StringSortByIntValue sorter = new StringSortByIntValue();
			Arrays.sort(box, sorter);
			String boxInOrder = "";
			for (int j = 0; j < box.length; j++) {
				boxInOrder += box[j];
			}

			if (!boxInOrder.equals(correctComparison)) {
				correct = false;
			}
		}
		return correct;
	}

	static class StringSortByIntValue implements Comparator<String> {
		@Override
		public int compare(String a, String b) {
			try {
				int aInt = Integer.parseInt(a);
				int bInt = Integer.parseInt(b);
				return aInt - bInt;
			} catch (Exception e) {
				return 0;
			}
		}
	}
}
