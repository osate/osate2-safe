package edu.ksu.cis.projects.mdcf.aadltranslator.preference;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import edu.ksu.cis.projects.mdcf.aadltranslator.AadlTranslatorPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class TranslatorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public TranslatorPreferencePage() {
		super(GRID);
		setPreferenceStore(AadlTranslatorPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the AADL to Java / MIDAS translator");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		DirectoryFieldEditor dfe = new DirectoryFieldEditor(PreferenceConstants.P_PATH, 
				"&AppDev Directory:", getFieldEditorParent());
		addField(dfe);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}