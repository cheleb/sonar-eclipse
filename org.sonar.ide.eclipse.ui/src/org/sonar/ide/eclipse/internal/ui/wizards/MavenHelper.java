package org.sonar.ide.eclipse.internal.ui.wizards;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.ide.eclipse.internal.ui.wizards.ConfigureProjectsWizard.ConfigureProjectsPage.SonarProject;
import org.xml.sax.InputSource;

public final class MavenHelper {

	private static final class MavenNamespaceContext implements
			NamespaceContext {
		public String getNamespaceURI(String prefix) {
			return "pom".equals(prefix) ? "http://maven.apache.org/POM/4.0.0"
					: null;
		}

		public String getPrefix(String namespaceURI) {
			return null; // we are not using this.
		}

		public Iterator<String> getPrefixes(String namespaceURI) {
			return null; // we are not using this.
		}
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(MavenHelper.class);
	private XPath xpath;
	private IFile pom;

	public MavenHelper() {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		this.xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new MavenNamespaceContext());
	}

	public void setGAFromPomFileIfExist(IProject project,
			SonarProject sonarProject) {
		IFile pom = project.getFile("pom.xml");
		LOG.debug("pom file found for project: " + project.getName());
		try {
			if (pom.exists()) {
				this.pom = pom;
				String groupId = xpath("/pom:project/pom:groupId/text()");

				if (StringUtils.isBlank(groupId)) {

					groupId = xpath("/pom:project/pom:parent/pom:groupId/text()");
					if (StringUtils.isBlank(groupId)) {
						LOG.warn("Could not find groupId in pom file: "
								+ pom.getLocation());
						return;
					}
				}

				String artifactId = xpath("/pom:project/pom:artifactId/text()");
				if (StringUtils.isBlank(artifactId)) {
					LOG.warn("Could not fing artifactId in pom file: "
							+ pom.getLocation());
					return;
				}
				sonarProject.setGroupId(groupId);
				sonarProject.setArtifactId(artifactId);

			}
		} finally {
			this.pom = null;
		}
	}

	private String xpath(String query) {
		try {
			InputSource inputSource = new InputSource(pom.getContents());

			return xpath.evaluate(query, inputSource);
		} catch (XPathExpressionException e) {
			LOG.error("XPATH: " + query, e);
		} catch (CoreException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
