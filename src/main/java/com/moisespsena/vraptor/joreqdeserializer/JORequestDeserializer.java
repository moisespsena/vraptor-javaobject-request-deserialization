/***
 * Copyright (c) 2011 Moises P. Sena - www.moisespsena.com
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.moisespsena.vraptor.joreqdeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 29/08/2011
 */
public class JORequestDeserializer {
	private static final String DESERIALIZED_OBJECT = JORequestDeserializer.class
			.getName() + "_DESERIALIZED_OBJECT";
	private final HttpServletRequest request;

	public JORequestDeserializer(final HttpServletRequest request) {
		this.request = request;
	}

	public Object deserialize() {
		final Object attr = request.getAttribute(DESERIALIZED_OBJECT);
		Object object = null;
		if (attr == null) {
			InputStream in;
			try {
				in = request.getInputStream();
				final ObjectInputStream oin = new ObjectInputStream(in);
				object = oin.readObject();
				request.setAttribute(DESERIALIZED_OBJECT, object);
				return object;
			} catch (final IOException e) {
				throw new JORequestDeserializerException(e);
			} catch (final ClassNotFoundException e) {
				throw new JORequestDeserializerException(e);
			}
		} else {
			return attr;
		}
	}

	public ResourceMethod deserializeResourceMethod() {
		final Object requestObject = deserialize();

		if ((requestObject == null)
				|| !(requestObject instanceof JOResourceMethodRequest)) {
			return null;
		}

		final JOResourceMethodRequest resourceMethodRequest = (JOResourceMethodRequest) requestObject;

		final Class<?> type = resourceMethodRequest.getType();
		final Class<?>[] paramsTypes = resourceMethodRequest
				.getParametersTypes();
		final String methodName = resourceMethodRequest.getMethodName();

		final Method method = new Mirror().on(type).reflect()
				.method(methodName).withArgs(paramsTypes);
		final ResourceClass resourceClass = new DefaultResourceClass(type);
		final ResourceMethod resourceMethod = new DefaultResourceMethod(
				resourceClass, method);

		return resourceMethod;
	}

	public Object[] getResourceMethodParameters() {
		final Object object = deserialize();

		if ((object == null) || !(object instanceof JOResourceMethodRequest)) {
			return null;
		}

		final JOResourceMethodRequest resourceMethodRequest = (JOResourceMethodRequest) object;

		final Object[] parameters = resourceMethodRequest.getParameters();
		return parameters;
	}
}
