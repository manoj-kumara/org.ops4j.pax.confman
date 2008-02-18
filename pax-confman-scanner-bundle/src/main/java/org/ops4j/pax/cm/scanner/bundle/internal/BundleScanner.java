/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cm.scanner.bundle.internal;

import java.net.URL;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.BundleURLScanner;
import org.ops4j.pax.swissbox.extender.BundleWatcher;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Acts as an extender for active bundle. If a bundle gets started and contains a directory as META-INF/config
 * each file contained in the directory will be considered a configuration where the name of the file wil be codered as
 * pid. If the directory constains subdirectories each subdirectory is consdered a factroy pid and contant of the
 * subdirectory a factory instance.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 18, 2008
 */
class BundleScanner
    extends AbstractLifecycle
    implements BundleObserver<URL>
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( BundleScanner.class );

    /**
     * Commands processor.
     */
    private final CommandProcessor<ConfigurationManager> m_processor;
    /**
     * Bundle watcher. Tracks bundle contents.
     */
    private final BundleWatcher<URL> m_bundleWatcher;

    /**
     * Constructor.
     *
     * @param bundleContext bundle context
     * @param processor     commands processor
     *
     * @throws NullArgumentException - If bundle context is null
     *                               - If commands processor is null
     */
    BundleScanner( final BundleContext bundleContext, final CommandProcessor<ConfigurationManager> processor
    )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( processor, "Command processor" );

        m_processor = processor;
        m_bundleWatcher = new BundleWatcher<URL>(
            bundleContext,
            new BundleURLScanner(
                "META-INF/",
                "config",
                false
            ),
            this
        );
    }

    /**
     * Add configurations contained by bundle configuration directory.
     *
     * @param bundle bundle containing the configuration directory
     * @param urls   url of configuration directory
     */
    public void addingEntries( final Bundle bundle, final List<URL> urls )
    {
        LOG.trace( "Scanning bundle " + bundle );
    }

    /**
     * Add configurations contained by bundle configuration directory.
     *
     * @param bundle bundle containing the configuration directory
     * @param urls   url of configuration directory
     */
    public void removingEntries( final Bundle bundle, final List<URL> urls )
    {
        LOG.trace( "Descanning bundle " + bundle );
    }

    /**
     * Starts directory scanning.
     */
    protected synchronized void onStart()
    {
        m_bundleWatcher.start();
    }

    /**
     * Stops directory scanning.
     */
    protected synchronized void onStop()
    {
        m_bundleWatcher.stop();
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "}" )
            .toString();
    }

}