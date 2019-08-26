import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("unused")
public class Sudoku {

	static int size;
	static String[] rep;
	static String[] cand;
	static ArrayList<ArrayList<Integer>> bingoResults = new ArrayList<ArrayList<Integer>>();

	public static void main(String[] args) throws IOException {
		int boxesToDraw = 105;
		boolean bingoYieldsNothing = false;
		ArrayList<Integer> stab = new ArrayList<Integer>();
		boolean playingBingo = false;
		Scanner in = new Scanner(System.in);
		System.out.print("Enter a game: ");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(in.nextLine()));
		} catch (FileNotFoundException e) {
			System.out.println("This file does not exist!");
			System.exit(0);
		}
		String line = reader.readLine();
		size = Integer.parseInt(line);
		rep = new String[size];
		int i = 0;
		line = reader.readLine();
		while (line != null) {
			String nums[] = line.split(" ");
			for (int j = 0; j < nums.length; j++) {
				try {
					rep[i] = nums[j];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("The game that was entered was not properly formatted!");
					System.exit(0);
				}
				if (rep[i].equals("X")) {
					rep[i] = "";
				}
				i++;
			}
			line = reader.readLine();
		}
		for (int nullCheck = 0; nullCheck < rep.length; nullCheck++) {
			if (rep[nullCheck] == null) {
				System.out.println("The game that was entered was not properly formatted!");
				System.exit(0);
			}
		}
		printRep();
		for (int j = 0; j < boxesToDraw; j++) {
			System.out.print("\u2584");
		}
		System.out.println();
		cand = new String[size];
		for (int j = 0; j < cand.length; j++) {
			cand[j] = "--";
		}
		boolean going = true;
		String snapshot[] = new String[rep.length];
		while (!isCorrect() && going) {
			going = false;
			updateCand(false);
			System.out.print("");
			for (int j = 0; (j < cand.length) && !going; j++) {

				// NAKED SINGLE
				if (cand[j].length() == 1) {
					rep[j] = cand[j];
					going = true;
				}
			}
			// HIDDEN SINGLE
			if (!going) {
				going = hiddenSingle("r") || hiddenSingle("c") || hiddenSingle("b");
			}

			// VERY HACKY METHOD OF ONLY DOING TOUGH STRATS IF THE EASY STRATS FAIL
			if (!going) {
				updateCand(true);
				System.out.print("");
				for (int j = 0; j < cand.length && !going; j++) {
					if (cand[j].length() == 1) {
						rep[j] = cand[j];
						going = true;
					}
				}
				if (!going) {
					going = hiddenSingle("r") || hiddenSingle("c") || hiddenSingle("b");
				}

				// TODO - BOWMANS BINGO HAPPENS HERE (COMPLETELY BROKEN PLEASE HELP)
				if (!going && !playingBingo) {
					playingBingo = true;

					// THE SNAPSHOT IDEA HAS A LOT OF POTENTIAL... JUST NEED TO SIT ON IT
					// FOR A WHILE...
					for (int j = 0; j < rep.length; j++) {
						snapshot[j] = rep[j];
					}
					stab = playBingo();
				}

			}
			// THE HACK ENDS HERE

			if (!going) {
				if (!going && !playingBingo) {
					System.out.println("STUMPED!");
					for (int j = 0; j < boxesToDraw; j++) {
						System.out.print("\u2580");
					}
					System.out.println();
					printRep();
					System.exit(0);
				}
				if (contradictionPresent()) {
					// ADD stab TO bingoResults
					bingoResults.add(stab);
					rep = snapshot;

					// THIS NEVER HAPPENS BECAUSE IT NEVER GETS A CHANCE TO PLAY A LITTLE BIT
					System.out.println("CONTRA");
					playingBingo = false;
				} else if (!going && playingBingo) {
					rep = snapshot;
					System.out.println("STUCK?");
					playingBingo = false;
				}
			}
			going = true;

			// TODO - LOCKED CANDIDATE (TYPE 2)
		}
		System.out.println("SOLVED!");
		for (int j = 0; j < boxesToDraw; j++) {
			System.out.print("\u2580");
		}
		System.out.println();
		printRep();
		reader.close();
		in.close();
	}

	private static void rebuildCand(ArrayList<ArrayList<String>> candArr) {
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

	private static ArrayList<Integer> playBingo() {
		// ELEMENT ZERO IS THE CELL INDEX
		// ELEMENT ONE IS THE GUESS
		ArrayList<Integer> guess = new ArrayList<Integer>();

		// STANDARD STUFF
		int width = (int) Math.sqrt((double) size);
		ArrayList<ArrayList<String>> candArr = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cand.length; i++) {
			candArr.add(new ArrayList<String>(Arrays.asList(cand[i].split(" "))));
		}
		Random random = new Random();
		int randomGuess = random.nextInt(size);
		while (candArr.get(randomGuess).size() < 2) {
			randomGuess = random.nextInt(size);
		}

		// TAKING THE LONG SHOT
		guess.add(randomGuess);
		guess.add(Integer.parseInt(candArr.get(randomGuess).get(0)));
		rep[randomGuess] = candArr.get(randomGuess).get(0);

		// REBUILDING cand[]
		rebuildCand(candArr);
		return guess;
	}

	private static boolean contradictionPresent() {
		boolean contra = false;
		for (int i = 0; i < rep.length; i++) {
			if (cand[i].equals("--") && rep[i].equals("")) {
				contra = true;
			}
		}
		return contra;
	}

	private static void printRep() {
		int width = (int) Math.sqrt((double) size);
		int sqrtWidth = (int) Math.sqrt((double) width);
		int numberOfDigits = (int) Math.log10(width);
		System.out.print("\u2554");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < (numberOfDigits + 1); j++) {
				System.out.print("\u2550");
			}
			if (i + 1 != (width)) {
				if (i % sqrtWidth == (sqrtWidth - 1)) {
					System.out.print("\u2566");
				} else {
					System.out.print("\u2564");
				}
			}
		}
		System.out.println("\u2557");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				String toPrint = rep[width * i + j] + "";
				if (toPrint.length() == 0) {
					toPrint = " ";
				}
				while (toPrint.length() < numberOfDigits + 1) {
					toPrint += " ";
				}
				if (j % sqrtWidth == 0) {
					System.out.print("\u2551");
				} else {
					System.out.print("\u2502");
				}
				System.out.print(toPrint);
			}
			System.out.println("\u2551");
			if (i + 1 != width) {
				if (i % sqrtWidth == (sqrtWidth - 1)) {
					System.out.print("\u2560");
				} else {
					System.out.print("\u255F");
				}
				for (int j = 0; j < width; j++) {
					for (int k = 0; k < (numberOfDigits + 1); k++) {
						if (i % sqrtWidth == (sqrtWidth - 1)) {
							System.out.print("\u2550");
						} else {
							System.out.print("\u257C");
						}
					}
					if (j + 1 != width) {
						if (i % sqrtWidth == (sqrtWidth - 1) || (j % sqrtWidth == (sqrtWidth - 1))) {
							if (j % sqrtWidth == (sqrtWidth - 1) && (i % sqrtWidth == (sqrtWidth - 1))) {
								System.out.print("\u256C");
							} else if (i % sqrtWidth == (sqrtWidth - 1)) {
								System.out.print("\u256A");
							} else {
								System.out.print("\u256B");
							}
						} else {
							System.out.print("\u253C");
						}
					}
				}
				if (i % sqrtWidth == (sqrtWidth - 1)) {
					System.out.println("\u2563");
				} else {
					System.out.println("\u2562");
				}
			}
		}
		System.out.print("\u255A");
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < (numberOfDigits + 1); j++) {
				System.out.print("\u2550");
			}
			if (i + 1 != width) {
				if (i % sqrtWidth == (sqrtWidth - 1)) {
					System.out.print("\u2569");
				} else {
					System.out.print("\u2567");
				}
			}
		}
		System.out.println("\u255D");
	}

	private static boolean hiddenSingle(String option) {
		boolean playMade = false;
		int width = (int) Math.sqrt((double) size);
		ArrayList<ArrayList<Integer>> indices = null;
		ArrayList<Integer> index = new ArrayList<Integer>();
		if (option.equals("r")) {
			indices = indicesOfRows();
		} else if (option.equals("c")) {
			indices = indicesOfColumns();
		} else if (option.equals("b")) {
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
					for (int j = 0; j < width && !playMade; j++) {
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

	private static void updateCand(boolean applyToughStrats) {
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

		applyBingoResults();

		String settings = "rc";
		for (int i = 0; i < settings.length(); i++) {
			eliminateLockedCandidates(settings.charAt(i) + "");
		}
		if (applyToughStrats) {
			settings = "rcb";
			for (int i = 0; i < settings.length(); i++) {
				for (int j = 2; j < ((width / 2) + 1); j++) {
					eliminateSubsets(settings.charAt(i) + "", j);
				}
			}
			settings = "rc";
			for (int i = 0; i < settings.length(); i++) {
				for (int j = 2; j < ((width / 2) + 1); j++) {
					eliminateFish(settings.charAt(i) + "", j);
				}
			}
		}
	}

	private static void applyBingoResults() {
		int width = (int) Math.sqrt((double) size);
		ArrayList<ArrayList<String>> candArr = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cand.length; i++) {
			candArr.add(new ArrayList<String>(Arrays.asList(cand[i].split(" "))));
		}

		for (int i = 0; i < bingoResults.size(); i++) {
			ArrayList<Integer> set = bingoResults.get(i);
			candArr.get(set.get(0)).remove(set.get(1) + "");
			System.out.println("RESULTS APLPLIED");
		}

		rebuildCand(candArr);
	}

	private static void eliminateFish(String option, int fishSize) {
		int width = (int) Math.sqrt((double) size);
		ArrayList<ArrayList<String>> candArr = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cand.length; i++) {
			candArr.add(new ArrayList<String>(Arrays.asList(cand[i].split(" "))));
		}
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		if (option.equals("r")) {
			indices = indicesOfRows();
		} else if (option.equals("c")) {
			indices = indicesOfColumns();
		}

		// NEED TO ITERATE ON ALL THE NUMBERS IN THE RANGE OF WIDTH
		// fishDigit IS THE NUMBER THAT WE ARE CHECKING FOR FISH ON
		for (int fishDigit = 1; fishDigit <= width; fishDigit++) {

			// BUILD A LIST OF ALL THE HOUSES WHICH CONTAIN <= fishSize OCCURENCES
			// OF fishDigit. THIS LIST IS CALLED housesInQuestion
			ArrayList<Integer> housesInQuestion = new ArrayList<Integer>();
			for (int j = 0; j < indices.size(); j++) {
				int occurenceCount = 0;
				for (int k = 0; k < indices.get(j).size(); k++) {
					if (candArr.get(indices.get(j).get(k)).contains(fishDigit + "")) {
						occurenceCount++;
					}
				}
				if (occurenceCount <= fishSize && occurenceCount > 0) {
					housesInQuestion.add(j);
				}
			}

			// BUILDING THIS GOOFY LIST OF BINARY NUMBERS WHICH CONTAIN
			// fishSize ONES CALLED combos
			ArrayList<String> combos = new ArrayList<String>();
			for (int j = 0; j < Math.pow(2, housesInQuestion.size()); j++) {
				String binRep = Integer.toBinaryString(j);
				while (binRep.length() < housesInQuestion.size()) {
					binRep = "0" + binRep;
				}
				int onesCount = 0;
				for (int k = 0; k < binRep.length(); k++) {
					if (binRep.charAt(k) == '1') {
						onesCount++;
					}
				}
				if (onesCount == fishSize) {
					combos.add(binRep);
				}
			}
			for (int j = 0; j < combos.size(); j++) {

				// comboInQuestion IS A LIST OF THE HOUSE INDEX OF THE HOUSES NEEDED TO BE
				// CHECKED FOR A FISH
				ArrayList<Integer> comboInQuestion = new ArrayList<Integer>();
				for (int k = 0; k < combos.get(j).length(); k++) {
					if (combos.get(j).charAt(k) == '1') {
						comboInQuestion.add(housesInQuestion.get(k));
					}
				}

				ArrayList<Integer> uniqueIndicesFishDigitWasFoundIn = new ArrayList<Integer>();
				for (int k = 0; k < comboInQuestion.size(); k++) {
					int houseBeingChecked = comboInQuestion.get(k);
					for (int l = 0; l < indices.get(houseBeingChecked).size(); l++) {
						int cellBeingChecked = indices.get(houseBeingChecked).get(l);
						if (candArr.get(cellBeingChecked).contains(fishDigit + "")) {
							uniqueIndicesFishDigitWasFoundIn
									.add(option.equals("r") ? cellBeingChecked % width : cellBeingChecked / width);
						}
					}
				}
				uniqueIndicesFishDigitWasFoundIn = removeDuplicates(uniqueIndicesFishDigitWasFoundIn);

				// A FISH EXISTS
				if (uniqueIndicesFishDigitWasFoundIn.size() == fishSize) {
					ArrayList<Integer> housesOppositeTheOptionToBeCleared = new ArrayList<Integer>(
							uniqueIndicesFishDigitWasFoundIn);
					ArrayList<ArrayList<Integer>> oppositeIndices = new ArrayList<ArrayList<Integer>>();
					if (option.equals("c")) {
						oppositeIndices = indicesOfRows();
					} else if (option.equals("r")) {
						oppositeIndices = indicesOfColumns();
					}
					for (int k = 0; k < housesOppositeTheOptionToBeCleared.size(); k++) {
						int houseToBeCleared = housesOppositeTheOptionToBeCleared.get(k);
						for (int l = 0; l < oppositeIndices.get(houseToBeCleared).size(); l++) {
							int cellToBeCleared = oppositeIndices.get(houseToBeCleared).get(l);
							if (option.equals("r")) {
								if (!comboInQuestion.contains(cellToBeCleared / width)) {
									candArr.get(cellToBeCleared).remove(fishDigit + "");
								}
							} else if (option.equals("c")) {
								if (!comboInQuestion.contains(cellToBeCleared % width)) {
									candArr.get(cellToBeCleared).remove(fishDigit + "");
								}
							}
						}
					}
				}
			}
		}

		// REBUILDING cand[]
		rebuildCand(candArr);
	}

	private static void eliminateSubsets(String option, int tupleSize) {
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
		Map<String, ArrayList<Integer>> occurrences = new HashMap<String, ArrayList<Integer>>();

		// i REFERS TO THE HOUSE NUMBER
		for (int i = 0; i < indices.size(); i++) {
			ArrayList<Integer> houseInQuestion = indices.get(i);

			// INITIALIZING occurrences
			for (int i2 = 0; i2 < width; i2++) {
				occurrences.put(Integer.toString(i2 + 1), new ArrayList<Integer>());
			}
			// j REFERS TO THE J-TH ELEMENT OF THE HOUSE IN QUESTION
			// THIS FOR LOOP BUILDS A MAP OF [CANDIDATES]:[HOUSES IT APPEARS IN]
			for (int j = 0; j < houseInQuestion.size(); j++) {
				int index = houseInQuestion.get(j);
				ArrayList<String> candidates = candArr.get(index);
				for (int k = 0; k < candidates.size(); k++) {
					String candidate = candidates.get(k);
					if (!candidate.equals("--")) {
						occurrences.get(candidate).add(index);
					}
				}
			}

			// BUILDING A CONCENTRATED VERSION OF occurrences CALLED conOcc
			// THIS MAP CONTAINS ONLY ELEMENTS WITH VALUES OF SIZE (OR LESS THAN BUT NOT
			// ZERO) tupleSize
			ArrayList<Integer> numbersInQuestion = new ArrayList<Integer>();
			Map<String, ArrayList<Integer>> conOcc = new HashMap<String, ArrayList<Integer>>();
			for (Entry<String, ArrayList<Integer>> entry : occurrences.entrySet()) {
				// ONLY ONE OCCURENCE OF THIS ENTRY
				if ((entry.getValue().size() <= tupleSize) && (entry.getValue().size() != 0)) {
					conOcc.put(entry.getKey(), entry.getValue());
					numbersInQuestion.add(Integer.parseInt(entry.getKey()));
				}
			}

			// combos IS A LIST OF ALL THE BINARY REPRESENTATIONS <= TO
			// 2^numbersInQuestion.size() WHICH CONTAIN EXACTLY
			// tupleSize ONES IN IT. THIS WILL BE USED TO ITERATE THROUGH ALL THE
			// COMBINATIONS OF numbersInQuestion
			ArrayList<String> combos = new ArrayList<String>();
			for (int j = 0; j < Math.pow(2, numbersInQuestion.size()); j++) {
				String binRep = Integer.toBinaryString(j);
				while (binRep.length() < numbersInQuestion.size()) {
					binRep = "0" + binRep;
				}
				int onesCount = 0;
				for (int k = 0; k < binRep.length(); k++) {
					if (binRep.charAt(k) == '1') {
						onesCount++;
					}
				}
				if (onesCount == tupleSize) {
					combos.add(binRep);
				}
			}

			for (int j = 0; j < combos.size(); j++) {
				ArrayList<Integer> comboInQuestion = new ArrayList<Integer>();
				for (int k = 0; k < combos.get(j).length(); k++) {
					if (combos.get(j).charAt(k) == '1') {
						comboInQuestion.add(numbersInQuestion.get(k));
					}
				}

				// SOMETHING GOES HERE
				// THIS IS TALLYING UP THE CELLS IN WHICH THE COMBO IN QUESTION APPEARS IN
				ArrayList<Integer> cellsWhichContainElementsFromCombo = new ArrayList<Integer>();
				ArrayList<Integer> cells = new ArrayList<Integer>();
				for (int k = 0; k < comboInQuestion.size(); k++) {
					cells = conOcc.get(comboInQuestion.get(k) + "");
					for (int l = 0; l < cells.size(); l++) {
						cellsWhichContainElementsFromCombo.add(cells.get(l));
					}
				}
				cellsWhichContainElementsFromCombo = removeDuplicates(cellsWhichContainElementsFromCombo);

				// A TUPLE HAS OCCURED IF THIS CONDITION IS TRUE
				if (cellsWhichContainElementsFromCombo.size() == tupleSize) {
					for (int k = 0; k < cellsWhichContainElementsFromCombo.size(); k++) {
						int cellToElimFrom = cellsWhichContainElementsFromCombo.get(k);
						ArrayList<String> cands = candArr.get(cellToElimFrom);
						for (int l = 0; l < cands.size(); l++) {
							if (!comboInQuestion.contains(Integer.parseInt(cands.get(l)))) {
								candArr.get(cellToElimFrom).remove(cands.get(l));
								l--;
							}
						}
					}
				}
			}
		}

		// REBUILDING cand[]
		rebuildCand(candArr);
	}

	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
		ArrayList<T> newList = new ArrayList<T>();
		for (T element : list) {
			if (!newList.contains(element)) {
				newList.add(element);
			}
		}
		return newList;
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
		rebuildCand(candArr);
	}

	private static boolean isCorrect() {
		return check("b") && check("r") && check("c");
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
