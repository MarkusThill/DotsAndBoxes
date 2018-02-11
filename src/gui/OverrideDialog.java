package gui;

import java.io.File;

public interface OverrideDialog {
	public enum OverrideOption {
		YES, YES_TO_ALL, NO, NO_TO_ALL
	};
	public OverrideOption getUserResponse(File f, OverrideOption[] showOptions);
}
