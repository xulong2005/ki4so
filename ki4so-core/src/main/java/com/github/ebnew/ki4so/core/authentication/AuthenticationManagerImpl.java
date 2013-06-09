package com.github.ebnew.ki4so.core.authentication;

import java.util.List;
import com.github.ebnew.ki4so.core.exception.EmptyCredentialException;
import com.github.ebnew.ki4so.core.exception.InvalidCredentialException;
import com.github.ebnew.ki4so.core.exception.UnsupportedCredentialsException;

/**
 * 认证管理器默认的实现类，
 * @author bidlink
 * @version 1.0
 * @updated 30-五月-2013 21:33:16
 */
public class AuthenticationManagerImpl implements AuthenticationManager {

	/**
	 * 认证处理器集合，使用多个认证处理器对凭证逐一校验，只要有一个通过即可。
	 */
	private List<AuthenticationHandler> authenticationHandlers;
	
	/**
	 * 用户凭据转为用户主体的解析器对象。
	 */
	private List<CredentialToPrincipalResolver> credentialToPrincipalResolvers;
	
	/**
	 * 认证成功后处理对象。
	 */
	private AuthenticationPostHandler authenticationPostHandler;
	
	public AuthenticationPostHandler getAuthenticationPostHandler() {
		return authenticationPostHandler;
	}

	public void setAuthenticationPostHandler(
			AuthenticationPostHandler authenticationPostHandler) {
		this.authenticationPostHandler = authenticationPostHandler;
	}

	public List<CredentialToPrincipalResolver> getCredentialToPrincipalResolvers() {
		return credentialToPrincipalResolvers;
	}

	public void setCredentialToPrincipalResolvers(
			List<CredentialToPrincipalResolver> credentialToPrincipalResolvers) {
		this.credentialToPrincipalResolvers = credentialToPrincipalResolvers;
	}

	public List<AuthenticationHandler> getAuthenticationHandlers() {
		return authenticationHandlers;
	}

	public void setAuthenticationHandlers(
			List<AuthenticationHandler> authenticationHandlers) {
		this.authenticationHandlers = authenticationHandlers;
	}
	
	public AuthenticationManagerImpl(){

	}


	/**
	 * 对用户凭据进行认证，返回认证结果。
	 * 
	 * @param credential
	 */
	public Authentication authenticate(Credential credential) throws InvalidCredentialException{
		//是否找到支持的凭据认证处理器。
		boolean foundSupported = false;
		//是否认证通过。
        boolean authenticated = false;
		//若凭据为空，则跑出异常。
		if(credential==null){
			throw new EmptyCredentialException();
		}
		//循环调用所有的认证处理器。
		if(authenticationHandlers!=null && authenticationHandlers.size()>0){
			for(AuthenticationHandler handler:authenticationHandlers){
				//认证处理器是否支持该凭据。
				if(handler.supports(credential)){
					foundSupported = true;
					authenticated = handler.authenticate(credential);
					//若认证成功，则跳出循环。
					if(authenticated){
						break;
					}
				}
			}
		}
		//未找到支持的认证处理器。
		if(!foundSupported){
			throw new UnsupportedCredentialsException();
		}
		
		Principal principal = null;
		//循环调用所有的用户凭据解析器。
		if(credentialToPrincipalResolvers!=null && credentialToPrincipalResolvers.size()>0){
			for(CredentialToPrincipalResolver resolver:credentialToPrincipalResolvers){
				//用户凭据解析器是否支持该凭据。
				if(resolver.supports(credential)){
					foundSupported = true;
					principal = resolver.resolvePrincipal(credential);
					//若解析成功，则跳出循环。
					if(principal!=null){
						break;
					}
				}
			}
		}
		//未找到支持的用户凭据解析器。
		if(!foundSupported){
			throw new UnsupportedCredentialsException();
		}
		return authenticationPostHandler.postAuthentication(credential, principal);
	}

}