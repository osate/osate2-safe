package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.thoughtworks.xstream.XStream;

import edu.ksu.cis.projects.mdcf.aadltranslator.Translator;
import edu.ksu.cis.projects.mdcf.aadltranslator.error.TestParseErrorReporterFactory;

public class SampleTest {

	private HashMap<String, IFile> systemFiles = new HashMap<>();
	private ResourceSet resourceSet;
	private Translator stats;

	HashSet<String> supportingFiles = new HashSet<>();
	HashSet<String> propertyFiles = new HashSet<>();
	private final boolean GENERATE_EXPECTED = false;

	private final String BUNDLE_ID = "edu.ksu.cis.projects.mdcf.aadl-translator-test";
	private final String TEST_DIR = "src/test/resources/edu/ksu/cis/projects/mdcf/aadltranslator/test/";

	private IProject testProject = null;

	/*
	 * The test runner is designed to not recreate everything for every test,
	 * but rather create things as needed. So if a test makes destructive
	 * changes, just delete everything that gets changed, and they will be reset
	 * to defaults on the next run.
	 */
	@Before
	public void setUp() {
		testProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("TestProject");
		resourceSet = OsateResourceUtil.createResourceSet();
		try {

			IHandlerService handlerService = (IHandlerService) PlatformUI
					.getWorkbench().getService(IHandlerService.class);
			handlerService
					.executeCommand(
							"org.osate.xtext.aadl2.ui.resetpredeclaredproperties",
							null);

			String[] natureIDs = new String[] { "org.osate.core.aadlnature",
					"org.eclipse.xtext.ui.shared.xtextNature" };

			IProject pluginResources = ResourcesPlugin.getWorkspace().getRoot()
					.getProject("Plugin_Resources");

			IProject[] referencedProjects = new IProject[] { pluginResources };
			if (!testProject.isAccessible()) {
				testProject.create(null);
				testProject.open(null);
			}

			IProjectDescription prpd = pluginResources.getDescription();
			prpd.setNatureIds(natureIDs);
			pluginResources.setDescription(prpd, null);

			IProjectDescription testpd = testProject.getDescription();
			testpd.setNatureIds(natureIDs);
			testpd.setReferencedProjects(referencedProjects);
			testProject.setDescription(testpd, null);

			IFolder packagesFolder = testProject.getFolder("packages");
			if (!packagesFolder.isAccessible()) {
				packagesFolder.create(true, true, null);
			}

			IFolder propertySetsFolder = testProject.getFolder("propertysets");
			if (!propertySetsFolder.isAccessible()) {
				propertySetsFolder.create(true, true, null);
			}

			initFiles(packagesFolder, propertySetsFolder);

			testProject
					.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		} catch (CoreException | ExecutionException | NotDefinedException
				| NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
	}

	private void initFiles(IFolder packagesFolder, IFolder propertySetsFolder) {
		URL aadlDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/");
		URL aadlPropertysetsDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/propertyset/");
		URL aadlSystemDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/system/");
		File aadlDir = null;
		File aadlPropertysetsDir = null;
		File aadlSystemDir = null;
		try {
			aadlDir = new File(FileLocator.toFileURL(aadlDirUrl).getPath());
			aadlPropertysetsDir = new File(FileLocator.toFileURL(
					aadlPropertysetsDirUrl).getPath());
			aadlSystemDir = new File(FileLocator.toFileURL(aadlSystemDirUrl)
					.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		initFiles(packagesFolder, propertySetsFolder, aadlDir, systemFiles,
				supportingFiles);
		// We don't actually maintain a set of system definitions, since
		// they are each only used by a single test case, so we sort of
		// kludge this in with an unused hashset...
		initFiles(packagesFolder, propertySetsFolder, aadlSystemDir,
				systemFiles, new HashSet<String>());
		initFiles(packagesFolder, propertySetsFolder, aadlPropertysetsDir,
				systemFiles, propertyFiles);
	}

	private void initFiles(IFolder packagesFolder, IFolder propertySetsFolder,
			File dir, HashMap<String, IFile> fileMap,
			HashSet<String> fileNameMap) {
		String fileName = null;
		try {
			for (File f : dir.listFiles()) {
				if (f.isHidden() || f.isDirectory())
					continue;
				fileName = f.getName().substring(0, f.getName().length() - 5);
				fileNameMap.add(fileName);
				fileMap.put(fileName,
						packagesFolder.getFile(fileName + ".aadl"));
				if (!packagesFolder.getFile(fileName + ".aadl").exists()) {
					fileMap.get(fileName).create(new FileInputStream(f), true,
							null);
				}
				resourceSet.getResource(OsateResourceUtil
						.getResourceURI((IResource) fileMap.get(fileName)),
						true);
			}
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}

	private void runTest(final String testName, final String systemName,
			final HashSet<String> usedPropertyFiles,
			final HashSet<String> usedSupportingFiles) {
		StringBuilder errorSB = new StringBuilder();
		IFile inputFile = systemFiles.get(systemName);
		stats = new Translator(new NullProgressMonitor());

		ParseErrorReporterFactory parseErrorReporterFactory = TestParseErrorReporterFactory.INSTANCE;
		ParseErrorReporterManager parseErrManager = new ParseErrorReporterManager(
				parseErrorReporterFactory);

		stats.setErrorManager(parseErrManager);
		for (String propSetName : usedPropertyFiles) {
			stats.addPropertySetName(propSetName);
		}
		Resource res = resourceSet.getResource(
				OsateResourceUtil.getResourceURI((IResource) inputFile), true);
		Element target = (Element) res.getContents().get(0);
		stats.process(target);
		errorSB.append(parseErrManager.getReporter((IResource) inputFile)
				.toString());

		for (String supportingFileName : usedSupportingFiles) {
			IFile supportingFile = systemFiles.get(supportingFileName);
			res = resourceSet.getResource(OsateResourceUtil
					.getResourceURI((IResource) supportingFile), true);
			target = (Element) res.getContents().get(0);
			stats.process(target);
			errorSB.append(parseErrManager.getReporter(
					(IResource) supportingFile).toString());
		}
		XStream xs = new XStream();

		try {
			testExpectedResult(testName, systemName,
					xs.toXML(stats.getSystemModel()), errorSB.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testExpectedResult(final String testName,
			final String systemName, final String content, final String errors)
			throws URISyntaxException, IOException, Exception {

		URL testDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(TEST_DIR);
		File testDir = new File(FileLocator.toFileURL(testDirUrl).getPath());

		final File expected = new File(testDir, "expected/" + testName + ".xml");
		final File result = new File(testDir, "actual/" + testName + ".xml");
		if (GENERATE_EXPECTED) {
			expected.getParentFile().mkdirs();
			Files.write(errors + content, expected, StandardCharsets.US_ASCII);
		} else {
			result.getParentFile().mkdirs();
			Files.write(errors + content, result, StandardCharsets.US_ASCII);
			assertFilesEqual(expected, errors + content);
		}
	}

	private void assertFilesEqual(final File expectedFile,
			final String resultString) throws Exception {
		final String expectedFileAsString = Files.readLines(expectedFile,
				StandardCharsets.US_ASCII, new ReadFileIntoString());
		assertEquals(expectedFileAsString, resultString);
	}

	@Test
	public void testPulseOxSystem() {
		HashSet<String> usedProperties = new HashSet<>();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_SmartAlarm_Properties");
		runTest("PulseOx", "PulseOx_SmartAlarm_System", usedProperties,
				supportingFiles);
	}

	@Test
	public void testNoWCET() {
		HashSet<String> usedProperties = new HashSet<>();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_SmartAlarmNoWCET_Properties");
		runTest("PulseOxNoWCET", "PulseOx_SmartAlarm_System", usedProperties,
				supportingFiles);
	}

	private class ReadFileIntoString implements LineProcessor<String> {
		StringBuilder theFileAsAString = new StringBuilder();

		@Override
		public String getResult() {
			return theFileAsAString.deleteCharAt(theFileAsAString.length() - 1)
					.toString();
		}

		@Override
		public boolean processLine(String line) throws IOException {
			theFileAsAString.append(line);
			theFileAsAString.append("\n");
			return true;
		}
	}
}