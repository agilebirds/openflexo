/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.ssl;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

 public final class DenaliSecurityProvider extends Provider {
	 
 	private final static String PROVIDER_ID = "DenaliSecurityProvider"; 

 	public DenaliSecurityProvider() { 
 		super(PROVIDER_ID, 1.0, "Denali security provider"); 

 		AccessController.doPrivileged(new SecurityPrivilegedAction()); 
 	} 

 	public static void insertSecurityProvider() throws Exception { 
 		if(Security.getProvider(PROVIDER_ID) == null) { 
 			Security.addProvider(new DenaliSecurityProvider()); 
 			Security.setProperty("ssl.TrustManagerFactory.algorithm", "DenaliX509"); 
 		} 
 	} 

 	protected final class SecurityPrivilegedAction implements PrivilegedAction<Object> {
		@Override
		public Object run() { 
		     put("TrustManagerFactory.DenaliX509", TrustManagerFactoryImpl.class.getName()); 
		     return null; 
		 }
	}

	public final static class TrustManagerFactoryImpl extends TrustManagerFactorySpi { 
 		protected final class DenaliX509TrustManager implements X509TrustManager {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
				
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
				//TODO: check in some way the certificates here.
			}
		}

		@Override
		protected void engineInit(KeyStore keystore) throws KeyStoreException {
			
		}

		@Override
		protected void engineInit(ManagerFactoryParameters mgrparams) throws InvalidAlgorithmParameterException {
			
		}

		@Override
		protected TrustManager[] engineGetTrustManagers() {
			return new TrustManager[] { new DenaliX509TrustManager() };
		} 
 	} 
 } 
