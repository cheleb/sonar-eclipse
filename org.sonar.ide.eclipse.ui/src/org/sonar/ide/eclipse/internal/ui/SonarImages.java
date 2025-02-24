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
package org.sonar.ide.eclipse.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.sonar.ide.eclipse.core.ISonarMeasure;
import org.sonar.ide.eclipse.ui.SonarUiPlugin;

import java.net.MalformedURLException;
import java.net.URL;

public final class SonarImages {

  private static final URL BASE_URL = SonarUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

  public static final ImageDescriptor STAR = createImageDescriptor("star.png"); //$NON-NLS-1$
  public static final ImageDescriptor STAR_OFF = createImageDescriptor("star_off.png"); //$NON-NLS-1$

  public static final ImageDescriptor SONARWIZBAN_IMG = createImageDescriptor("sonar_wizban.png"); //$NON-NLS-1$
  public static final ImageDescriptor SONAR16_IMG = createImageDescriptor("sonar.png"); //$NON-NLS-1$
  public static final ImageDescriptor SONAR32_IMG = createImageDescriptor("sonar32.png"); //$NON-NLS-1$
  public static final ImageDescriptor SONARSYNCHRO_IMG = createImageDescriptor("synced.gif"); //$NON-NLS-1$
  public static final ImageDescriptor SONARREFRESH_IMG = createImageDescriptor("refresh.gif"); //$NON-NLS-1$
  public static final ImageDescriptor SONARCLOSE_IMG = createImageDescriptor("close.gif"); //$NON-NLS-1$

  public static final Image IMG_VIOLATION = createImage("violation.png"); //$NON-NLS-1$
  public static final Image IMG_SEVERITY_BLOCKER = createImage("severity/blocker.gif"); //$NON-NLS-1$
  public static final Image IMG_SEVERITY_CRITICAL = createImage("severity/critical.gif"); //$NON-NLS-1$
  public static final Image IMG_SEVERITY_MAJOR = createImage("severity/major.gif"); //$NON-NLS-1$
  public static final Image IMG_SEVERITY_MINOR = createImage("severity/minor.gif"); //$NON-NLS-1$
  public static final Image IMG_SEVERITY_INFO = createImage("severity/info.gif"); //$NON-NLS-1$

  public static final ImageDescriptor DEBUG = createImageDescriptor("debug.gif"); //$NON-NLS-1$
  public static final ImageDescriptor SHOW_CONSOLE = createImageDescriptor("showConsole.gif"); //$NON-NLS-1$

  private static final ImageDescriptor[][] TENDENCY = {
    { createTendency("-2-red"), createTendency("-1-red"), null, createTendency("1-red"), createTendency("2-red") },
    { createTendency("-2-black"), createTendency("-1-black"), null, createTendency("1-black"), createTendency("2-black") },
    { createTendency("-2-green"), createTendency("-1-green"), null, createTendency("1-green"), createTendency("2-green") } };

  public static ImageDescriptor forTendency(ISonarMeasure measure) {
    int trend = measure.getTrend(); // color: -1 = red; 0 = black, 1 = green
    int var = measure.getVar(); // value: from -2 to 2
    if ((trend < -1) || (trend > 1)) {
      return null; // should never happen
    }
    if ((var < -2) || (var > 2)) {
      return null; // should never happen
    }
    return TENDENCY[trend + 1][var + 2];
  }

  private static ImageDescriptor createTendency(String name) {
    return createImageDescriptor("tendency/" + name + "-small.png");
  }

  private static URL getUrl(String key) throws MalformedURLException {
    return new URL(BASE_URL, key);
  }

  private static Image createImage(String key) {
    createImageDescriptor(key);
    ImageRegistry imageRegistry = getImageRegistry();
    return imageRegistry != null ? imageRegistry.get(key) : null;
  }

  private static ImageDescriptor createImageDescriptor(String key) {
    ImageRegistry imageRegistry = getImageRegistry();
    if (imageRegistry != null) {
      ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(key);
      if (imageDescriptor == null) {
        try {
          imageDescriptor = ImageDescriptor.createFromURL(getUrl(key));
        } catch (MalformedURLException e) {
          imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
        }
        imageRegistry.put(key, imageDescriptor);
      }
      return imageDescriptor;
    }
    return null;
  }

  private static ImageRegistry getImageRegistry() {
    // "org.eclipse.swt.SWTError: Invalid thread access" might be thrown during unit tests
    if (PlatformUI.isWorkbenchRunning()) {
      return SonarUiPlugin.getDefault().getImageRegistry();
    }
    return null;
  }

  private SonarImages() {
  }
}
