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
package org.sonar.ide.eclipse.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.osgi.framework.BundleContext;
import org.sonar.ide.eclipse.internal.core.*;
import org.sonar.ide.eclipse.internal.core.resources.SonarProjectManager;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Metric;

public class SonarCorePlugin extends AbstractPlugin {
  public static final String PLUGIN_ID = "org.sonar.ide.eclipse.core";

  public static final String NATURE_ID = PLUGIN_ID + ".sonarNature";

  /**
   * Godin: It would be better to use only one MARKER_ID at least at first time.
   */
  public static final String MARKER_ID = PLUGIN_ID + ".sonarProblem";

  public static final QualifiedName SESSION_PROPERY_MEASURES = new QualifiedName(PLUGIN_ID, "measures");

  private static SonarCorePlugin plugin;

  public SonarCorePlugin() {
    plugin = this; // NOSONAR
  }

  public static SonarCorePlugin getDefault() {
    return plugin;
  }

  private ServersManager serversManager;

  @Override
  public void start(BundleContext context) {
    super.start(context);

    serversManager = new ServersManager();
    serversManager.load();
  }

  @Override
  public void stop(BundleContext context) {
    serversManager.save();

    super.stop(context);
  }

  private static SonarProjectManager projectManager;

  public synchronized SonarProjectManager getProjectManager() {
    if (projectManager == null) {
      projectManager = new SonarProjectManager();
    }
    return projectManager;
  }

  public static ISonarServersManager getServersManager() {
    return getDefault().serversManager;
  }

  public static ISonarResource createSonarResource(IResource resource, String key, String name) {
    return new SonarResource(resource, key, name);
  }

  public static ISonarFile createSonarFile(IFile file, String key, String name) {
    return new SonarFile(file, key, name);
  }

  public static ISonarMeasure createSonarMeasure(ISonarResource sonarResource, Measure measure) {
    return new SonarMeasure(sonarResource, measure);
  }

  public static ISonarMeasure createSonarMeasure(ISonarResource sonarResource, Metric metric, Measure measure) {
    return new SonarMeasure(sonarResource, metric, measure);
  }

  public static ISonarMetric createSonarMetric(String metricKey) {
    return new SonarMetric(new Metric().setKey(metricKey).setName(metricKey));
  }

  public static ISonarMetric createSonarMetric(String metricKey, String metricName) {
    return new SonarMetric(new Metric().setKey(metricKey).setName(metricName));
  }

}
