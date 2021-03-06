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
package net.grinder.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common utility for network env.
 * 
 * @author JunHo Yoon
 * @since 3.0
 * 
 */
public abstract class NetworkUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtil.class);

	/**
	 * Get the local host address, try to get actual IP.
	 * 
	 * @return ip form of host address
	 */
	public static String getLocalHostAddress() {
		String addr = null;
		try {
			addr = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			LOGGER.error("Error while get localhost address", e);
		}
		if (addr != null && !"127.0.0.1".equals(addr)) {
			return addr;
		}
		return getLocalHostAddress("www.google.com", 80);
	}

	/**
	 * Get local address by connecting to a server.
	 * @param byConnecting
	 * 				the server address to conenct.
	 * @param port
	 * 				the port to connect
	 * @return IP address
	 * 				local IP address
	 */
	public static String getLocalHostAddress(String byConnecting, int port) {
		InetAddress addr = getLocalInetAddress(byConnecting, port);
		if (addr != null) {
			return addr.getHostAddress();
		} else {
			return "127.0.0.1";
		}
	}

	/**
	 * Get local host name by connecting to a server.
	 * @param byConnecting
	 * 				the server address to conenct.
	 * @param port
	 * 				the port to connect
	 * @return host name
	 * 				local host name
	 */
	public static String getLocalHostName(String byConnecting, int port) {
		InetAddress addr = getLocalInetAddress(byConnecting, port);
		if (addr != null) {
			return addr.getHostName();
		} else {
			return "localhost";
		}
	}

	private static InetAddress getLocalInetAddress(String byConnecting, int port) {

		InetAddress addr = getAddressWithSocket(byConnecting, port);
		if (addr == null) {
			addr = getAddressWithSocket("www.nhnopensource.org", 80);
		}
		if (addr == null) {
			try {
				addr = getFirstNonLoopbackAddress(true, false);
			} catch (SocketException e2) {
				addr = null;
			}
		}
		return addr;
	}

	private static InetAddress getAddressWithSocket(String byConnecting, int port) {
		Socket s = null;
		try {
			s = new Socket();
			SocketAddress addr = new InetSocketAddress(byConnecting, port);
			s.connect(addr, 1000); // 1 seconds timeout
			return s.getLocalAddress();
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(s);
		}
	}

	private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6)
					throws SocketException {
		Enumeration<?> en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			if (!i.isUp()) {
				continue;
			}
			for (Enumeration<?> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						if (preferIPv6) {
							continue;
						}
						return addr;
					}
					if (addr instanceof Inet6Address) {
						if (preferIpv4) {
							continue;
						}
						return addr;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get local host name.
	 * On some platform, InetAddress.getLocalHost().getHostName() will return "localhost". If the /etc/hosts file is not set properly, it will return
	 * "localhost" or throw exception. So, at this circumstance, we will get the address by connecting a network address.
	 * 
	 * @return local host name
	 */
	public static String getLocalHostName() {
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			LOGGER.error("Error while get localhost name", e);
		}
		if (hostName != null && !"localhost".equals(hostName)) {
			return hostName;
		}
		return getLocalHostName("www.google.com", 80);
		
	}

	/**
	 * Get the IP addresses from host name.
	 * 
	 * @param host
	 *            host
	 * @return {@link InetAddress} array
	 */
	public static InetAddress[] getIpsFromHost(String host) {
		try {
			return InetAddress.getAllByName(host);
		} catch (UnknownHostException e) {
			return new InetAddress[] {};
		}
	}

}
