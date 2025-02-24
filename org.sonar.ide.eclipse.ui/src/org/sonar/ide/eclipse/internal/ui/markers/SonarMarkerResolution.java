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
package org.sonar.ide.eclipse.internal.ui.markers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.slf4j.LoggerFactory;
import org.sonar.ide.eclipse.internal.ui.SonarImages;
import org.sonar.ide.eclipse.ui.ISonarResolver;

/**
 * @author Jérémie Lagarde
 */
public class SonarMarkerResolution implements IMarkerResolution2 {

  ISonarResolver resolver;

  SonarMarkerResolution(final ISonarResolver sonarResolver) {
    resolver = sonarResolver;
  }

  public String getDescription() {
    return resolver.getDescription();
  }

  public Image getImage() {
    return SonarImages.SONAR16_IMG.createImage();
  }

  public String getLabel() {
    return resolver.getLabel();
  }

  public void run(final IMarker marker) {
    final IResource resource = marker.getResource();
    if (resource instanceof IFile && resource.isAccessible()) {
      if (resolver.resolve(marker, (IFile) resource)) {
        try {
          marker.delete();
        } catch (final CoreException e) {
          LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
      }
    }
  }
}
