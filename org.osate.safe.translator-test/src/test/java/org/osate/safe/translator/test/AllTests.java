package org.osate.safe.translator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
//import org.osate.core.test.Aadl2UiInjectorProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.xtext.junit4.InjectWith;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.safe.translator.IncludesCalculator;
import org.osate.safe.translator.Translator;
import org.osate.safe.translator.model.DevOrProcModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.arch.ControllerErrorTests;
import org.osate.safe.translator.test.arch.DeviceModelTests;
import org.osate.safe.translator.test.arch.PortModelTests;
import org.osate.safe.translator.test.arch.ProcessConnectionModelTests;
import org.osate.safe.translator.test.arch.ProcessModelTests;
import org.osate.safe.translator.test.arch.SystemConnectionModelTests;
import org.osate.safe.translator.test.arch.SystemModelTests;
import org.osate.safe.translator.test.arch.TaskModelTests;
import org.osate.safe.translator.test.error.TestParseErrorReporterFactory;
import org.osate.safe.translator.test.hazard.DetectionAndHandlingTests;
import org.osate.safe.translator.test.hazard.EliminatedFaultsTests;
import org.osate.safe.translator.test.hazard.ExternallyCausedDangerModelTests;
import org.osate.safe.translator.test.hazard.HazardPreliminariesTests;
import org.osate.safe.translator.test.hazard.InternallyCausedDangerModelTests;
import org.osate.safe.translator.test.hazard.NotDangerousDangerModelTests;
import org.osate.safe.translator.test.hazard.PropagatableErrorTests;
import org.osate.safe.translator.test.view.AppHAReportViewTests;
import org.osate.safe.translator.test.view.AppSpecViewTests;
import org.osate.safe.translator.test.view.AppSuperClassViewTests;
import org.osate.safe.translator.test.view.STRendererTests;
import org.osate.safe.translator.util.MarkdownLinkRenderer;
import org.osate.xtext.aadl2.errormodel.errorModel.impl.ErrorModelLibraryImpl;
import org.stringtemplate.v4.STGroup;

@RunWith(Suite.class)
//@InjectWith(typeof(Aadl2UiInjectorProvider)) Look into this, could remove 10s wait time
@Suite.SuiteClasses({
	// Architecture Model tests
	SystemModelTests.class, DeviceModelTests.class, ProcessModelTests.class, TaskModelTests.class,
	PortModelTests.class, SystemConnectionModelTests.class, ProcessConnectionModelTests.class,

	// Hazard Analysis Model tests
//		ConnectionModelHazardTests.class,
	HazardPreliminariesTests.class,
//		HazardBackgroundTests.class,
	PropagatableErrorTests.class, ExternallyCausedDangerModelTests.class, InternallyCausedDangerModelTests.class,
	NotDangerousDangerModelTests.class, DetectionAndHandlingTests.class, EliminatedFaultsTests.class,

	// Error-handling tests
	ControllerErrorTests.class,

	// View tests
	AppHAReportViewTests.class, AppSuperClassViewTests.class, AppSpecViewTests.class, STRendererTests.class,
//		AwasTests.class,
})
public class AllTests {
	private static final Logger log = Logger.getLogger(AllTests.class.getName());

	public static HashMap<String, IFile> targetableFiles = new HashMap<>();
	public static ResourceSet resourceSet = null;

	public final static String TEST_PLUGIN_BUNDLE_ID = "org.osate.safe.translator-test";
	public final static String MAIN_PLUGIN_BUNDLE_ID = "org.osate.safe.translator";
	public final static String TEST_DIR = "src/test/resources/org/osate/safe/translator/test/";
	public final static String TEMPLATE_DIR = "src/main/resources/templates/";

	public static IProject testProject = null;

	public static HashSet<String> usedDevices = new HashSet<>();
	public static HashSet<String> usedProperties = new HashSet<>();
	public static ParseErrorReporterFactory parseErrorReporterFactory = TestParseErrorReporterFactory.INSTANCE;
	public static ParseErrorReporterManager parseErrManager;

	public static boolean initComplete = false;
	public static StringBuilder errorSB = new StringBuilder();

	@BeforeClass
	public static void initialize() {
		testProject = ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
		try {

//			IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
//			handlerService.executeCommand("org.osate.xtext.aadl2.ui.resetpredeclaredproperties", null);

			String[] natureIDs = new String[] { "org.osate.core.aadlnature",
			"org.eclipse.xtext.ui.shared.xtextNature" };

///			IProject pluginResources = ResourcesPlugin.getWorkspace().getRoot().getProject("Plugin_Resources");
///
///			IProject[] referencedProjects = new IProject[] { pluginResources };
			if (!testProject.isAccessible()) {
				testProject.create(null);
				testProject.open(null);
			}

//			IProjectDescription prpd = pluginResources.getDescription();
//			prpd.setNatureIds(natureIDs);
//			pluginResources.setDescription(prpd, null);

			IProjectDescription testpd = testProject.getDescription();
			testpd.setNatureIds(natureIDs);
///			testpd.setReferencedProjects(referencedProjects);
			testProject.setDescription(testpd, null);

			IFolder packagesFolder = testProject.getFolder("packages");
			if (!packagesFolder.isAccessible()) {
				packagesFolder.create(true, true, null);
			}

			IFolder propertySetsFolder = testProject.getFolder("propertysets");
			if (!propertySetsFolder.isAccessible()) {
				propertySetsFolder.create(true, true, null);
			}

			resourceSet = OsateResourceUtil.createResourceSet();

			initFiles(packagesFolder, propertySetsFolder);

			testProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// This is a total hack because the guice injectors don't finish
		// running until some time after the build has completed. This 10s wait
		// allows them to finish, avoiding all sorts of nasty errors
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initComplete = true;
	}

	private static void initFiles(IFolder packagesFolder, IFolder propertySetsFolder) {
		URL aadlDirUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID).getEntry(TEST_DIR + "aadl/");
		URL aadlPropertysetsDirUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID).getEntry(TEST_DIR + "aadl/propertyset/");
		URL aadlSystemDirUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID).getEntry(TEST_DIR + "aadl/system/");
		URL aadlDeviceDirUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID).getEntry(TEST_DIR + "aadl/device/");
		File aadlDir = null;
		File aadlPropertysetsDir = null;
		File aadlSystemDir = null;
		File aadlDeviceDir = null;
		try {
			aadlDir = new File(FileLocator.toFileURL(aadlDirUrl).toURI());
			aadlPropertysetsDir = new File(FileLocator.toFileURL(aadlPropertysetsDirUrl).toURI());
			aadlSystemDir = new File(FileLocator.toFileURL(aadlSystemDirUrl).toURI());
			aadlDeviceDir = new File(FileLocator.toFileURL(aadlDeviceDirUrl).toURI());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		initFiles(packagesFolder, propertySetsFolder, aadlDir, targetableFiles);
		initFiles(packagesFolder, propertySetsFolder, aadlSystemDir, targetableFiles);
		initFiles(packagesFolder, propertySetsFolder, aadlDeviceDir, targetableFiles);
		initFiles(packagesFolder, propertySetsFolder, aadlPropertysetsDir, targetableFiles);
	}

	private static void initFiles(IFolder packagesFolder, IFolder propertySetsFolder, File dir,
			HashMap<String, IFile> fileMap) {
		String fileName = null;
		try {
			for (File f : dir.listFiles()) {
				if (f.isHidden() || f.isDirectory()) {
					continue;
				}
				fileName = f.getName().substring(0, f.getName().length() - 5);
				fileMap.put(fileName, packagesFolder.getFile(fileName + ".aadl"));
				if (!packagesFolder.getFile(fileName + ".aadl").exists()) {
					fileMap.get(fileName).create(new FileInputStream(f), true, null);
				}
				resourceSet.createResource(OsateResourceUtil.getResourceURI(fileMap.get(fileName)));
			}
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}

	public static SystemModel runArchTransTest(final String testName, final String systemName) {
		IFile inputFile = targetableFiles.get(systemName);
		HashSet<IFile> supportingFiles = getSupportingFiles(inputFile);
		Translator stats = new Translator(new NullProgressMonitor());

		configureTranslator(inputFile, stats);

		parseErrManager = new ParseErrorReporterManager(parseErrorReporterFactory);

		stats.setErrorManager(parseErrManager);
		for (String propSetName : usedProperties) {
			stats.addPropertySetName(propSetName);
		}
		Resource res = resourceSet.getResource(OsateResourceUtil.getResourceURI(inputFile), true);
		Element target = (Element) res.getContents().get(0);

		stats.setErrorInfo(getErrorTypes(resourceSet, supportingFiles));
		stats.process(target);
		String appName = inputFile.getProject().getName();
		stats.getSystemModel().setName(appName);
		for (DevOrProcModel dopm : stats.getSystemModel().getChildren().values()) {
			dopm.setParentName(appName);
		}
		errorSB.append(parseErrManager.getReporter(inputFile).toString());

		for (IFile supportingFile : supportingFiles) {
			if (supportingFile.getFullPath().segment(0).startsWith("org.osate.")) {
				continue;
			}
			res = resourceSet.getResource(OsateResourceUtil.getResourceURI(supportingFile), true);
			target = (Element) res.getContents().get(0);
			stats.process(target);
			errorSB.append(parseErrManager.getReporter(supportingFile).toString());
		}

		return stats.getSystemModel();
	}

	private static HashSet<IFile> getSupportingFiles(IFile inputFile) {
		HashSet<IFile> newSupportingFiles = new HashSet<>();
		IncludesCalculator ic = new IncludesCalculator(new NullProgressMonitor());
		Element targetElem = getTargetElement(inputFile);
		ic.process(targetElem);
		newSupportingFiles.addAll(ic.getUsedFiles());
		newSupportingFiles.remove(inputFile);
		return newSupportingFiles;
	}

	public static SystemModel runHazardTransTest(final String testName, final String systemName) {
		IFile inputFile = targetableFiles.get(systemName);
		Translator stats = new Translator(new NullProgressMonitor());

		configureTranslator(inputFile, stats);

		parseErrManager = new ParseErrorReporterManager(parseErrorReporterFactory);

		stats.setErrorManager(parseErrManager);
		for (String propSetName : usedProperties) {
			stats.addPropertySetName(propSetName);
		}
		Resource res = resourceSet.getResource(OsateResourceUtil.getResourceURI(inputFile), true);
		Element target = (Element) res.getContents().get(0);

		HashSet<IFile> supportingFiles = getSupportingFiles(inputFile);

		stats.setErrorInfo(getErrorTypes(resourceSet, supportingFiles));

		stats.process(target);
		String appName = inputFile.getProject().getName();
		stats.getSystemModel().setName(appName);
		for (DevOrProcModel dopm : stats.getSystemModel().getChildren().values()) {
			dopm.setParentName(appName);
		}
		errorSB.append(parseErrManager.getReporter(inputFile).toString());

		for (IFile supportingFile : supportingFiles) {
			if (supportingFile.getFullPath().segment(0).startsWith("org.osate.")) {
				continue;
			}
			res = resourceSet.getResource(OsateResourceUtil.getResourceURI(supportingFile), true);
			target = (Element) res.getContents().get(0);
			stats.process(target);
			errorSB.append(parseErrManager.getReporter(supportingFile).toString());
		}

		return stats.getSystemModel();
	}

	private static void configureTranslator(IFile inputFile, Translator stats) {
		AadlPackage pack = (AadlPackage) getTargetElement(inputFile);
		PublicPackageSection sect = pack.getPublicSection();
		Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
		if (ownedClassifier instanceof org.osate.aadl2.System) {
			stats.setTarget("System");
		} else if (ownedClassifier instanceof org.osate.aadl2.Device) {
			stats.setTarget("Device");
		} else if (ownedClassifier instanceof org.osate.aadl2.Process) {
			stats.setTarget("Process");
		}
	}

	public static void runWriterTest(String testName, Object var, String varName, STGroup stg,
			boolean GENERATE_EXPECTED, String expectedDir) {
		stg.registerRenderer(String.class, MarkdownLinkRenderer.getInstance());
		URL expectedOutputUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID)
				.getEntry(TEST_DIR + expectedDir + testName + ".txt");
		String actualStr = stg.getInstanceOf(testName).add(varName, var).render();
		String expectedStr = null;
		try {
			if (GENERATE_EXPECTED) {
				Files.write(Paths.get(FileLocator.toFileURL(expectedOutputUrl).toURI()), actualStr.getBytes());
				expectedStr = actualStr;
				fail("Test was run in generate expected mode!");
			} else {
				expectedStr = new String(
						Files.readAllBytes(Paths.get(FileLocator.toFileURL(expectedOutputUrl).toURI())));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		actualStr = actualStr.replace("\r\n", "\n"); // Fix line endings if we're on windows
		assertEquals(expectedStr, actualStr);
	}

	private static Element getTargetElement(IFile file) {
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		Resource res = rs.getResource(OsateResourceUtil.getResourceURI(file), true);
		Element target = (Element) res.getContents().get(0);
		return target;
	}

	private static HashSet<NamedElement> getErrorTypes(ResourceSet rs, HashSet<IFile> usedFiles) {
		HashSet<NamedElement> retSet = new HashSet<>();
		for (IFile f : usedFiles) {
			if (f.getFullPath().segment(0).startsWith("org.osate.")) {
				continue;
			}
			Resource res = rs.getResource(OsateResourceUtil.getResourceURI(f), true);
			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)) {
				continue;
			}
			AadlPackage pack = (AadlPackage) target;
			PublicPackageSection sect = pack.getPublicSection();
			if (sect.getOwnedAnnexLibraries().size() > 0
					&& sect.getOwnedAnnexLibraries().get(0).getName().equalsIgnoreCase("EMV2")) {
				AnnexLibrary annexLibrary = sect.getOwnedAnnexLibraries().get(0);
				DefaultAnnexLibrary defaultAnnexLibrary = (DefaultAnnexLibrary) annexLibrary;
				ErrorModelLibraryImpl emImpl = (ErrorModelLibraryImpl) defaultAnnexLibrary.getParsedAnnexLibrary();
				retSet.addAll(emImpl.getTypes());
				retSet.addAll(emImpl.getTypesets());
				retSet.addAll(emImpl.getBehaviors());
			}
		}
		return retSet;
	}

}
