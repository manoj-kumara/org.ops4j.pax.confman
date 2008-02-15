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
package org.ops4j.pax.cm.service.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.MetadataConstants;

/**
 * A Adapter wrapper that adds information about the type of adaptor.
 * ! Should be the last wrapper before adaptor as this to work.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
class AdaptorTypeInfoAdapterWrapper
    extends AdapterWrapper
{

    /**
     * Adaptor type.
     */
    private final String m_adaptorType;

    /**
     * Constructor.
     *
     * @param delegate wrapped Adapter
     */
    AdaptorTypeInfoAdapterWrapper( final Adapter delegate )
    {
        super( delegate );
        m_adaptorType = delegate.getClass().getName();
    }

    /**
     * Delegates to wrapped Adapter ands information entries.
     *
     * @see org.ops4j.pax.cm.api.Adapter#adapt(Object)
     */
    @SuppressWarnings( "unchecked" )
    public Object adapt( final Object object )
    {
        final Object adaptedObject = m_delegate.adapt( object );
        if( !( adaptedObject instanceof Dictionary ) )
        {
            // do not work on adapted objects that are not dictionaries
            return adaptedObject;
        }
        final Dictionary adapted = new Hashtable();
        // first we add the properties in order to allow adaptors to overide them
        adapted.put( MetadataConstants.INFO_ADAPTOR, m_adaptorType );
        // and do the adaptation
        DictionaryUtils.copy( (Dictionary) adaptedObject, adapted );
        return adapted;
    }
}