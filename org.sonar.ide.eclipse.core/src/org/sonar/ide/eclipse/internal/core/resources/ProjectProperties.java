/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.eclipse.internal.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.sonar.ide.eclipse.core.ISonarProject;
import org.sonar.ide.eclipse.core.SonarCorePlugin;
import org.sonar.ide.eclipse.core.SonarKeyUtils;

/**
 * @author Evgeny Mandrikov
 */
public class ProjectProperties implements ISonarProject {

  private IProject project;
  private String url;
  private String groupId;
  private String artifactId;
  private String branch;
  private boolean analysedLocally;

  public ProjectProperties(IProject project) {
    this.project = project;
  }

  public static ProjectProperties getInstance(IResource resource) {
    if (resource == null) {
      return null;
    }
    IProject project = resource.getProject();
    if (project == null || !project.isAccessible()) {
      return null;
    }
    return SonarCorePlugin.getDefault().getProjectManager().readSonarConfiguration(project);
  }

  public void save() {
    SonarCorePlugin.getDefault().getProjectManager().saveSonarConfiguration(project, this);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getBranch() {
    return branch;
  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  public IProject getProject() {
    return project;
  }

  public IResource getResource() {
    return project;
  }

  public String getKey() {
    return SonarKeyUtils.projectKey(getGroupId(), getArtifactId(), getBranch());
  }

  public String getName() {
    return project.getName();
  }

  public boolean isAnalysedLocally() {
    return analysedLocally;
  }

  public void setAnalysedLocally(boolean value) {
    this.analysedLocally = value;
  }

}
