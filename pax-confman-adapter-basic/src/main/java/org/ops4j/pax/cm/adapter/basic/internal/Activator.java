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
package org.ops4j.pax.cm.adapter.basic.internal;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.ops4j.pax.cm.adapter.basic.internal.spec.AndSpecification;
import org.ops4j.pax.cm.adapter.basic.internal.spec.FilterBasedSpecification;
import org.ops4j.pax.cm.adapter.basic.internal.spec.InstanceOfSpecification;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.ServiceConstants;

/**
 * Registers basic adapters services.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );

    /**
     * Register basic adapters:<br/>
     * - Dictionary -> Dictionary
     *
     * @see BundleActivator#start(BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        LOG.debug( "Starting OPS4J Pax ConfMan basic adapters" );
        registerDictionaryToDictionaryAdapter( bundleContext );
        registerPropertiesInputStreamToDictionaryAdapter( bundleContext );
        registerFileToInputStreamAdapter( bundleContext );
    }

    /**
     * Register properties input stream -> Properties (implements Dictionary) adapter.
     *
     * @param bundleContext bundle context
     */
    private void registerPropertiesInputStreamToDictionaryAdapter( BundleContext bundleContext )
    {
        try
        {
            bundleContext.registerService(
                Adapter.class.getName(),
                new PropertiesInputStreamToDictionaryAdapter(
                    new AndSpecification(
                        new InstanceOfSpecification( InputStream.class ),
                        new FilterBasedSpecification(
                            bundleContext.createFilter(
                                "(|"
                                + "(" + ServiceConstants.MIME_TYPE + "=extension/properties)"
                                + "(" + ServiceConstants.MIME_TYPE + "=extension/cfg)"
                                + ")"
                            )
                        )
                    )
                ),
                null // no properties
            );
        }
        catch( InvalidSyntaxException ignore )
        {
            // not expected
            LOG.trace( "Internal error: " + ignore.getMessage() );
        }
    }

    /**
     * Register Properties file -> Properties Input Stream adapter.
     *
     * @param bundleContext bundle context
     */
    private void registerFileToInputStreamAdapter( BundleContext bundleContext )
    {
        bundleContext.registerService(
            Adapter.class.getName(),
            new FileToInputStreamAdapter(
                new AndSpecification(
                    new InstanceOfSpecification( File.class )
                )
            ),
            null // no properties
        );
    }

    /**
     * Register Dictionary -> Dictionary adapter.
     *
     * @param bundleContext bundle context
     */
    private void registerDictionaryToDictionaryAdapter( final BundleContext bundleContext )
    {
        bundleContext.registerService(
            Adapter.class.getName(),
            new DictionaryToDictionaryAdapter(
                new InstanceOfSpecification( Dictionary.class )
            ),
            null // no properties
        );
    }

    /**
     * Does nothing.
     *
     * @see BundleActivator#stop(BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        LOG.debug( "Stopping OPS4J Pax ConfMan basic adapters" );
    }

    /**
     * Creates a filter based specification that targets objects of the specified class.
     *
     * @param bundleContext bundle context
     * @param objectClass   class of the object that can be adapted
     *
     * @return created OSGi filter
     */
    private static Filter createFilterSpecification( final BundleContext bundleContext,
                                                     final String objectClass )
    {
        try
        {
            final StringBuilder builder = new StringBuilder()
                .append( "(" )
                .append( ServiceConstants.OBJECTCLASS ).append( "=" ).append( objectClass )
                .append( ")" );

            return bundleContext.createFilter( builder.toString() );
        }
        catch( InvalidSyntaxException ignore )
        {
            // this should never happen
            LOG.trace( "Internal error: " + ignore.getMessage() );
            throw new IllegalStateException( "Internal error", ignore );
        }
    }

}
