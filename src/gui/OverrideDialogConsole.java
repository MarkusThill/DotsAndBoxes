package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class OverrideDialogConsole implements OverrideDialog {

	@Override
	public OverrideOption getUserResponse(File f, OverrideOption[] showOptions) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		boolean ok = false;
		OverrideOption selectedOption = null;
		do {
			System.out.println("The file/folder \"" + f
					+ "\" already exists. Override?");
			boolean yes = Arrays.asList(showOptions).contains(
					OverrideOption.YES);
			if (yes)
				System.out.print("YES (y)  ");
			boolean yes_to_all = Arrays.asList(showOptions).contains(
					OverrideOption.YES_TO_ALL);
			if (yes_to_all)
				System.out.print("YES to all (yy)  ");
			boolean no = Arrays.asList(showOptions).contains(OverrideOption.NO);
			if (no)
				System.out.print("No (n)  ");
			boolean no_to_all = Arrays.asList(showOptions).contains(
					OverrideOption.NO_TO_ALL);
			if (no_to_all)
				System.out.print("No to all (nn)");
			System.out.print(":\n");
			try {
				s = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			s = s.toLowerCase();
			if (s.equals("y") && yes) {
				ok = true;
				selectedOption = OverrideOption.YES;
			}
			if (s.equals("yy") && yes_to_all) {
				ok = true;
				selectedOption = OverrideOption.YES_TO_ALL;
			}
			if (s.equals("n") && no) {
				ok = true;
				selectedOption = OverrideOption.NO;
			}
			if (s.equals("nn") && no) {
				ok = true;
				selectedOption = OverrideOption.NO_TO_ALL;
			}

		} while (!ok);
		return selectedOption;
	}

}
