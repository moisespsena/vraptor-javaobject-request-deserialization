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
package com.moisespsena.vraptor.joreqdeserializer.interceptors;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.moisespsena.vraptor.joreqdeserializer.JORequest;
import com.moisespsena.vraptor.joreqdeserializer.JORequestDeserializer;

/**
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 01/06/2011
 * 
 */
@RequestScoped
@Intercepts(after = ParametersInstantiatorInterceptor.class)
public class DeserializeJavaObjectRequestBodyInterceptor implements Interceptor {
	private final MethodInfo methodInfo;
	private final HttpServletRequest request;

	public DeserializeJavaObjectRequestBodyInterceptor(
			final MethodInfo methodInfo, final HttpServletRequest request) {
		this.methodInfo = methodInfo;
		this.request = request;
	}

	@Override
	public boolean accepts(final ResourceMethod method) {
		if (JORequest.isValid(request)) {
			return true;
		}
		return false;
	}

	@Override
	public void intercept(final InterceptorStack stack,
			final ResourceMethod method, final Object resourceInstance) {
		final JORequestDeserializer requestDeserializer = new JORequestDeserializer(
				request);
		final Object[] parameters = requestDeserializer
				.getResourceMethodParameters();
		methodInfo.setParameters(parameters);

		stack.next(method, resourceInstance);
	}
}
