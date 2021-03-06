/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ngrinder.script.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.ngrinder.common.util.PathUtil;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;

/**
 * Customized Version of {@link HttpServletRequest} Tomcat's version has bug...
 * 
 * getPathInfo() translates encoded path wrongly..
 * 
 * @author JunHo Yoon
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
	/**
	 * Constructor.
	 * 
	 * @param request
	 *            request to be wrapped
	 */
	public MyHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getPathInfo() {
		// FIXME this function will be called many times just in one request.
		// it is better to handle the url outside.
		try {
			return SVNEncodingUtil.uriEncode(URLDecoder
							.decode(getRequestURI()
											.substring(PathUtil.removeDuplicatedPrependedSlash(
															getContextPath() + "/svn").length()), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
